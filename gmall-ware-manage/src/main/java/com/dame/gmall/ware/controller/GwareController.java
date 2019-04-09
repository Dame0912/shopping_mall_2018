package com.dame.gmall.ware.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.dame.gmall.bean.WareInfo;
import com.dame.gmall.bean.WareOrderTask;
import com.dame.gmall.bean.WareSku;
import com.dame.gmall.bean.enums.TaskStatus;
import com.dame.gmall.ware.service.GwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @return
 */

@Controller
public class GwareController {

    @Autowired
    private GwareService gwareService;

    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping("wareSkuListPage")
    public String wareSkuListPage() {
        return "wareSkuListPage";
    }

    /**
     * 判断是否有库存，订单系统在提交支付时调用.
     * ResponseEntity<String> 和 返回 String 是一样的
     *
     * @param reqMap
     * @return
     */
    @RequestMapping("hasStock")
    @ResponseBody
    public ResponseEntity<String> hasStock(@RequestParam Map<String, String> reqMap) {
        String numstr = reqMap.get("num");
        Integer num = Integer.parseInt(numstr);
        String skuid = reqMap.get("skuId");
        boolean hasStock = gwareService.hasStockBySkuId(skuid, num);
        if (hasStock) {
            return ResponseEntity.ok("1");
        }
        return ResponseEntity.ok("0");
    }


    /**
     * 根据skuid 返回 仓库信息
     *
     * @param skuid
     * @return
     */
    @RequestMapping(value = "skuWareInfo")
    @ResponseBody
    public ResponseEntity<String> getWareInfoBySkuid(String skuid) {
        if (skuid == null) {
            return ResponseEntity.noContent().build();
        }
        List<WareInfo> wareInfos = gwareService.getWareInfoBySkuid(skuid);
        String jsonString = JSON.toJSONString(wareInfos);
        return ResponseEntity.ok(jsonString);
    }

    /**
     * 保存sku的库存明细
     *
     * @param wareSku
     * @return
     */
    @RequestMapping(value = "saveWareSku", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> addWareSku(WareSku wareSku) {
        gwareService.addWareSku(wareSku);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取所有sku的库存信息
     *
     * @param response
     * @return
     */
    @RequestMapping(value = "wareSkuList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<WareSku> getWareSkuList() {
        return gwareService.getWareSkuList();
    }

    /**
     * 获取所有的仓库信息
     *
     * @return
     */
    @RequestMapping(value = "wareInfoList", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<WareInfo> getWareInfoList() {
        return gwareService.getWareInfoList();
    }


    /***
     * 订单出库
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "delivery", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> deliveryStock(HttpServletRequest httpServletRequest) {
        String id = httpServletRequest.getParameter("id");
        String trackingNo = httpServletRequest.getParameter("trackingNo");//物流单号
        WareOrderTask wareOrderTask = new WareOrderTask();
        wareOrderTask.setId(id);
        wareOrderTask.setTrackingNo(trackingNo);
        gwareService.deliveryStock(wareOrderTask);
        return ResponseEntity.ok().build();
    }

    /**
     * 查询所有的 WareOrderTask
     *
     * @return
     */
    @RequestMapping(value = "taskList", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getWareOrderTaskList() {
        List<WareOrderTask> wareOrderTaskList = gwareService.getWareOrderTaskList(null);
        SerializeConfig config = new SerializeConfig();
        config.configEnumAsJavaBean(TaskStatus.class);
        String jsonString = JSON.toJSONString(wareOrderTaskList);
        return ResponseEntity.ok().body(jsonString);
    }

}
