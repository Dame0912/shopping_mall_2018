package com.dame.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 用户地址
 */
public class UserAddress implements Serializable {

    @Column
    @Id
    private String id;

    /**
     * 用户地址
     */
    @Column
    private String userAddress;

    /**
     * 用户id
     */
    @Column
    private String userId;

    /**
     * 收件人
     */
    @Column
    private String consignee;

    /**
     * 联系方式
     */
    @Column
    private String phoneNum;

    /**
     * 是否默认
     */
    @Column
    private String isDefault;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }
}
