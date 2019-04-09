package com.dame.gmall.ware.service.impl;

import com.alibaba.fastjson.JSON;
import com.dame.gmall.bean.WareInfo;
import com.dame.gmall.bean.WareOrderTask;
import com.dame.gmall.bean.WareOrderTaskDetail;
import com.dame.gmall.bean.WareSku;
import com.dame.gmall.bean.enums.TaskStatus;
import com.dame.gmall.util.HttpClientUtil;
import com.dame.gmall.ware.config.ActiveMQUtil;
import com.dame.gmall.ware.mapper.WareInfoMapper;
import com.dame.gmall.ware.mapper.WareOrderTaskDetailMapper;
import com.dame.gmall.ware.mapper.WareOrderTaskMapper;
import com.dame.gmall.ware.mapper.WareSkuMapper;
import com.dame.gmall.ware.service.GwareService;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.*;

@Service
public class GwareServiceImpl implements GwareService {

    @Autowired
    private WareSkuMapper wareSkuMapper;

    @Autowired
    private WareInfoMapper wareInfoMapper;

    @Autowired
    private WareOrderTaskMapper wareOrderTaskMapper;

    @Autowired
    private WareOrderTaskDetailMapper wareOrderTaskDetailMapper;

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Value("${order.split.url}")
    private String ORDER_URL;


    /**
     * 查询库存数量，库存数量 — 锁定的库存数量
     *
     * @param skuid
     * @return
     */
    public Integer getStockBySkuId(String skuid) {
        Integer stock = wareSkuMapper.selectStockBySkuid(skuid);
        return stock;
    }

    /**
     * 判断是否有指定数量的库存
     *
     * @param skuid
     * @param num
     * @return
     */
    public boolean hasStockBySkuId(String skuid, Integer num) {
        Integer stock = getStockBySkuId(skuid);

        if (stock == null || stock < num) {
            return false;
        }
        return true;
    }

    /**
     * 根据skuid查询仓库的信息
     *
     * @param skuid
     * @return
     */
    public List<WareInfo> getWareInfoBySkuid(String skuid) {
        List<WareInfo> wareInfos = wareInfoMapper.selectWareInfoBySkuid(skuid);
        return wareInfos;
    }

    /**
     * 获取所有的仓库信息
     *
     * @return
     */
    public List<WareInfo> getWareInfoList() {
        List<WareInfo> wareInfos = wareInfoMapper.selectAll();
        return wareInfos;
    }

    /**
     * 将相同库存地址的 skuId 存放一起
     *
     * @param skuIdlist
     * @return
     */
    private Map<String, List<String>> getWareSkuMap(List<String> skuIdlist) {
        Example example = new Example(WareSku.class);
        example.createCriteria().andIn("skuId", skuIdlist);
        List<WareSku> wareSkuList = wareSkuMapper.selectByExample(example);

        Map<String, List<String>> wareSkuMap = new HashMap<>();

        for (WareSku wareSku : wareSkuList) {
            List<String> skulistOfWare = wareSkuMap.get(wareSku.getWarehouseId());
            if (skulistOfWare == null) {
                skulistOfWare = new ArrayList<>();
            }
            skulistOfWare.add(wareSku.getSkuId());
            wareSkuMap.put(wareSku.getWarehouseId(), skulistOfWare);
        }
        return wareSkuMap;
    }


    public List<Map<String, Object>> convertWareSkuMapList(Map<String, List<String>> wareSkuMap) {
        List<Map<String, Object>> wareSkuMapList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : wareSkuMap.entrySet()) {
            Map<String, Object> skuWareMap = new HashMap<>();
            String wareid = entry.getKey();
            skuWareMap.put("wareId", wareid);
            List<String> skuids = entry.getValue();
            skuWareMap.put("skuIds", skuids);
            wareSkuMapList.add(skuWareMap);
        }
        return wareSkuMapList;
    }

    /**
     * 保存sku的库存信息
     *
     * @param wareSku
     */
    public void addWareSku(WareSku wareSku) {
        wareSkuMapper.insert(wareSku);
    }

    /**
     * 获取所有sku的库存信息
     *
     * @return
     */
    public List<WareSku> getWareSkuList() {
        List<WareSku> wareSkuList = wareSkuMapper.selectWareSkuAll();
        return wareSkuList;
    }

    public WareOrderTask getWareOrderTask(String taskId) {

        WareOrderTask wareOrderTask = wareOrderTaskMapper.selectByPrimaryKey(taskId);

        WareOrderTaskDetail wareOrderTaskDetail = new WareOrderTaskDetail();
        wareOrderTaskDetail.setTaskId(taskId);
        List<WareOrderTaskDetail> details = wareOrderTaskDetailMapper.select(wareOrderTaskDetail);
        wareOrderTask.setDetails(details);
        return wareOrderTask;
    }


    /***
     * 出库操作，减库存和减锁定库存，通知订单系统订单出库
     * @param wareOrderTask
     */
    @Transactional
    public void deliveryStock(WareOrderTask wareOrderTask) {
        String trackingNo = wareOrderTask.getTrackingNo();
        WareOrderTask wareOrderTaskQuery = getWareOrderTask(wareOrderTask.getId());
        wareOrderTaskQuery.setTaskStatus(TaskStatus.DELEVERED);
        List<WareOrderTaskDetail> details = wareOrderTaskQuery.getDetails();
        for (WareOrderTaskDetail detail : details) {
            WareSku wareSku = new WareSku();
            wareSku.setWarehouseId(wareOrderTaskQuery.getWareId());
            wareSku.setSkuId(detail.getSkuId());
            wareSku.setStock(detail.getSkuNum());
            wareSkuMapper.deliveryStock(wareSku);
        }

        wareOrderTaskQuery.setTaskStatus(TaskStatus.DELEVERED);
        wareOrderTaskQuery.setTrackingNo(trackingNo);
        wareOrderTaskMapper.updateByPrimaryKeySelective(wareOrderTaskQuery);
        try {
            sendToOrder(wareOrderTaskQuery);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    public void sendToOrder(WareOrderTask wareOrderTask) throws JMSException {
        Connection conn = activeMQUtil.getConn();

        Session session = conn.createSession(true, Session.SESSION_TRANSACTED);
        Destination destination = session.createQueue("SKU_DELIVER_QUEUE");
        MessageProducer producer = session.createProducer(destination);
        MapMessage mapMessage = new ActiveMQMapMessage();
        mapMessage.setString("orderId", wareOrderTask.getOrderId());
        mapMessage.setString("status", wareOrderTask.getTaskStatus().toString()); //小细节 枚举
        mapMessage.setString("trackingNo", wareOrderTask.getTrackingNo());

        producer.send(mapMessage);
        session.commit();
    }

    /**
     * 拆单
     *
     * @param wareOrderTask
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<WareOrderTask> checkOrderSplit(WareOrderTask wareOrderTask) {
        List<WareOrderTaskDetail> details = wareOrderTask.getDetails();
        List<String> skulist = new ArrayList<>();
        for (WareOrderTaskDetail detail : details) {
            skulist.add(detail.getSkuId());
        }
        // 将相同库存地址的 skuId 存放一起
        Map<String, List<String>> wareSkuMap = getWareSkuMap(skulist);
        if (wareSkuMap.size() == 1) {
            Map.Entry<String, List<String>> entry = wareSkuMap.entrySet().iterator().next();
            String wareid = entry.getKey();
            wareOrderTask.setWareId(wareid);

        } else {//需要拆单
            // 拼参，请求订单系统拆单
            List<Map<String, Object>> wareSkuMapList = convertWareSkuMapList(wareSkuMap);
            String jsonString = JSON.toJSONString(wareSkuMapList);
            Map<String, String> map = new HashMap<>();
            map.put("orderId", wareOrderTask.getOrderId());
            map.put("wareSkuMap", jsonString);
            String resultJson = HttpClientUtil.doPost(ORDER_URL, map);// 请求订单系统
            List<WareOrderTask> wareOrderTaskList = JSON.parseArray(resultJson, WareOrderTask.class);

            if (wareOrderTaskList.size() >= 2) {
                for (WareOrderTask subOrderTask : wareOrderTaskList) {
                    // 保存拆单后的订单和订单明细
                    subOrderTask.setTaskStatus(TaskStatus.DEDUCTED);
                    saveWareOrderTask(subOrderTask);
                }
                // 将原订单状态修改为：已拆分
                updateStatusWareOrderTaskByOrderId(wareOrderTask.getOrderId(), TaskStatus.SPLIT);
                return wareOrderTaskList;
            } else {
                throw new RuntimeException("拆单异常!!");
            }
        }
        return null;
    }

    /**
     * 保存 wareOrderTask 和 wareOrderTaskDetail
     *
     * @param wareOrderTask
     * @return
     */
    public WareOrderTask saveWareOrderTask(WareOrderTask wareOrderTask) {
        wareOrderTask.setCreateTime(new Date());
        WareOrderTask wareOrderTaskQuery = new WareOrderTask();
        wareOrderTaskQuery.setOrderId(wareOrderTask.getOrderId());
        WareOrderTask wareOrderTaskOrigin = wareOrderTaskMapper.selectOne(wareOrderTaskQuery);
        if (wareOrderTaskOrigin != null) {
            return wareOrderTaskOrigin;
        }

        wareOrderTaskMapper.insert(wareOrderTask);

        List<WareOrderTaskDetail> wareOrderTaskDetails = wareOrderTask.getDetails();
        for (WareOrderTaskDetail wareOrderTaskDetail : wareOrderTaskDetails) {
            wareOrderTaskDetail.setTaskId(wareOrderTask.getId());
            wareOrderTaskDetailMapper.insert(wareOrderTaskDetail);
        }
        return wareOrderTask;
    }


    public void updateStatusWareOrderTaskByOrderId(String orderId, TaskStatus taskStatus) {
        Example example = new Example(WareOrderTask.class);
        example.createCriteria().andEqualTo("orderId", orderId);
        WareOrderTask wareOrderTask = new WareOrderTask();
        wareOrderTask.setTaskStatus(taskStatus);
        wareOrderTaskMapper.updateByExampleSelective(wareOrderTask, example);
    }

    /**
     * 利用mq，通知订单系统减库存状态，即是否成功
     *
     * @param wareOrderTask
     * @throws JMSException
     */
    public void sendSkuDeductMQ(WareOrderTask wareOrderTask) throws JMSException {
        Connection conn = activeMQUtil.getConn();
        Session session = conn.createSession(true, Session.SESSION_TRANSACTED);
        Destination destination = session.createQueue("SKU_DEDUCT_QUEUE");
        MessageProducer producer = session.createProducer(destination);
        MapMessage mapMessage = new ActiveMQMapMessage();
        mapMessage.setString("orderId", wareOrderTask.getOrderId());
        mapMessage.setString("status", wareOrderTask.getTaskStatus().toString());
        producer.send(mapMessage);
        session.commit();
    }

    /**
     * 锁库存。
     * 此处使用的是排他锁(select ... for update)，所以在该事务没有提交之前，其他操作都会等待。比如另一个线程查询该订单的库存量。
     *
     * @param wareOrderTask
     */
    @Transactional
    public void lockStock(WareOrderTask wareOrderTask) {
        List<WareOrderTaskDetail> wareOrderTaskDetails = wareOrderTask.getDetails();
        String comment = "";
        // 查询每个sku的可用数量
        for (WareOrderTaskDetail wareOrderTaskDetail : wareOrderTaskDetails) {
            WareSku wareSku = new WareSku();
            wareSku.setWarehouseId(wareOrderTask.getWareId());
            wareSku.setStockLocked(wareOrderTaskDetail.getSkuNum());
            wareSku.setSkuId(wareOrderTaskDetail.getSkuId());
            // 查询可用库存，加行级写锁，注意索引避免表锁
            // 行锁变表锁：索引失效导致，比如：where i = '1',但是i是数字类型的，此时用的string，导致索引失效，行锁变表锁
            int availableStock = wareSkuMapper.selectStockBySkuidForUpdate(wareSku);
            if (availableStock - wareOrderTaskDetail.getSkuNum() < 0) {
                comment += "减库存异常：名称：" + wareOrderTaskDetail.getSkuName() + "，实际可用库存数" + availableStock + ",要求库存" + wareOrderTaskDetail.getSkuNum();
            }
        }

        // 库存超卖 记录日志，返回错误状态
        if (comment.length() > 0) {
            wareOrderTask.setTaskComment(comment);
            wareOrderTask.setTaskStatus(TaskStatus.OUT_OF_STOCK);
            Example example = new Example(WareOrderTask.class);
            example.createCriteria().andEqualTo("orderId", wareOrderTask.getOrderId());
            wareOrderTaskMapper.updateByExampleSelective(wareOrderTask, example);
            // updateStatusWareOrderTaskByOrderId(wareOrderTask.getOrderId(), TaskStatus.OUT_OF_STOCK);
        } else {   // 库存正常  进行锁库存
            for (WareOrderTaskDetail wareOrderTaskDetail : wareOrderTaskDetails) {
                WareSku wareSku = new WareSku();
                wareSku.setWarehouseId(wareOrderTask.getWareId());
                wareSku.setStockLocked(wareOrderTaskDetail.getSkuNum());
                wareSku.setSkuId(wareOrderTaskDetail.getSkuId());
                // 加行级写锁 注意索引避免表锁
                wareSkuMapper.incrStockLocked(wareSku);
            }
            wareOrderTask.setTaskStatus(TaskStatus.DEDUCTED);
            updateStatusWareOrderTaskByOrderId(wareOrderTask.getOrderId(), TaskStatus.DEDUCTED);
        }

        // 利用mq，通知订单系统锁库存是否成功
        try {
            sendSkuDeductMQ(wareOrderTask);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return;
    }


    public List<WareOrderTask> getWareOrderTaskList(WareOrderTask wareOrderTask) {
        List<WareOrderTask> wareOrderTasks = null;
        if (wareOrderTask == null) {
            wareOrderTasks = wareOrderTaskMapper.selectAll();
        } else {
            wareOrderTasks = wareOrderTaskMapper.select(wareOrderTask);
        }
        return wareOrderTasks;
    }

}
