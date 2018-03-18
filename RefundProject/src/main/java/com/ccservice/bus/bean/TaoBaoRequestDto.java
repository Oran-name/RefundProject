package com.ccservice.bus.bean;

import java.util.List;

import com.ccservice.bus.annotation.FieldName;

/***
 * 淘宝退票需要请求的参数
    * @ClassName: TaoBaoRequestDto
    * @Description: TODO
    * @author 朱元元
    *
    *
 */
public class TaoBaoRequestDto extends BaseRequestDto {
    private static final long serialVersionUID = 1L;

    @Deprecated
    @FieldName("request_ticketId") // 
    private int ticketId;

    /**
     * 淘宝的子订单号（淘宝分销的退票支持退单张票，这个其实就是淘宝分销自己标识的自己的票号）。 退票就使用这个订单号，使用long类型可以支持退多张票
     */
    @FieldName("request_taoBaoSuOrderId")
    private List<Long> taoBaoSuOrderIds;

    /**
     * 淘宝分销的订单号
     */
    @FieldName("request_taoBaoOrderId")
    private String taoBaoOrderId;

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getTaoBaoOrderId() {
        return taoBaoOrderId;
    }

    public void setTaoBaoOrderId(String taoBaoOrderId) {
        this.taoBaoOrderId = taoBaoOrderId;
    }

    /**
    * @return taoBaoSuOrderIds
    */

    public List<Long> getTaoBaoSuOrderIds() {
        return taoBaoSuOrderIds;
    }

    /**
     * @param taoBaoSuOrderIds the taoBaoSuOrderIds to set
     */

    public void setTaoBaoSuOrderIds(List<Long> taoBaoSuOrderIds) {
        this.taoBaoSuOrderIds = taoBaoSuOrderIds;
    }

    /**
     *   淘宝分销需要的前面等等一些配置信息
     */
    @FieldName("request_baoConfigBean")
    private TaoBaoConfigBean baoConfigBean;

    public TaoBaoConfigBean getBaoConfigBean() {
        return baoConfigBean;
    }

    public void setBaoConfigBean(TaoBaoConfigBean baoConfigBean) {
        this.baoConfigBean = baoConfigBean;
    }

    /**
    * @return isAgree
    */

    public boolean getIsAgree() {
        return isAgree;
    }

    /**
     * @param isAgree the isAgree to set
     */

    public void setIsAgree(boolean isAgree) {
        this.isAgree = isAgree;
    }

    @FieldName("request_isAgree")
    private boolean isAgree;

}
