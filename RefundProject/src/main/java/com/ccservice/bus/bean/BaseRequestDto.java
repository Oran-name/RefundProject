package com.ccservice.bus.bean;

import java.io.Serializable;

import com.ccservice.bus.annotation.FieldName;

/**
 *  请求基类信息封装
    * @ClassName: BaseRequestDto
    * @Description: TODO
    * @author 朱元元
    *
    *
 */
public abstract class BaseRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *  退票原因描述
     */
    @FieldName("request_description")
    private String description;

    /**
     * 巴士无忧订单id
     */

    @FieldName("request_orderId")
    private Long orderId;

    /**
     *  操作人的姓名，用于写操作记录使用,因为要做成mq的形式，就无法传递后台登陆者的名字，此可以选写。
     */
    @FieldName("request_caoZuoName")
    private String caoZuoName;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCaoZuoName() {
        return caoZuoName;
    }

    public void setCaoZuoName(String caoZuoName) {
        this.caoZuoName = caoZuoName;
    }

}
