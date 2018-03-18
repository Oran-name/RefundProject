package com.ccservice.bus.returnInterface.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.bus.bean.BaseRequestDto;
import com.ccservice.bus.bean.BaseResponseDto;
import com.ccservice.bus.bean.TaoBaoConfigBean;
import com.ccservice.bus.bean.TaoBaoRequestDto;
import com.ccservice.bus.bean.TaoBaoResponseOrderDetailsDto;
import com.ccservice.bus.bean.TaoBaoResponseSetReturnFeeDto;
import com.ccservice.bus.exection.ReturnTicketExection;
import com.ccservice.bus.insertrc.BusOrderRcInsert;
import com.ccservice.bus.log.WriteLogThread;
import com.ccservice.bus.returnInterface.ReturnTicketInter;
import com.ccservice.bus.utils.ObjectUtils;
import com.taobao.api.ApiException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.TaobaoResponse;
import com.taobao.api.request.BusOrderGetRequest;
import com.taobao.api.request.BusOrderGetRequest.B2BOrderQueryRq;
import com.taobao.api.request.BusRefundSetRequest;
import com.taobao.api.request.BusRefundSetRequest.B2BRefundOrderRq;
import com.taobao.api.request.BusRefundfeeGetRequest;
import com.taobao.api.response.BusOrderGetResponse;
import com.taobao.api.response.BusRefundSetResponse;
import com.taobao.api.response.BusRefundfeeGetResponse;

public class TaoBaoReturnTicketIpml implements ReturnTicketInter {

    private static String first_request_taobao_log_name = "第一次调用top查询退票返回的信息";

    private static String request_taobaoset_log_name = "申请top退票接口返回的信息";

    private Log log = LogFactory.getLog(TaoBaoReturnTicketIpml.class);

    public BaseResponseDto getResponseResult(BaseRequestDto requestDto) throws ReturnTicketExection {
        // TODO Auto-generated method stub
        boolean empty = ObjectUtils.isEmpty(requestDto);
        if (empty) {
            log.error("请求的信息为空：不能被转换[" + requestDto + "]");
            throw new ReturnTicketExection(null);
        }
        // 请求信息
        TaoBaoRequestDto tRequestDto = (TaoBaoRequestDto) requestDto;
        BaseResponseDto returnTicket = null;
        try {
            // 去退票
            returnTicket = toReturnTicket(tRequestDto);
        }
        catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                throw new ReturnTicketExection("拉取淘宝退票接口异常信息：" + e.getErrMsg());
            }
            catch (ReturnTicketExection e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        // 可能为空
        return returnTicket;
    }

    /**
    * 查询退票费用明细 单张票查询
    *   
    * @Title: QueryRefundFee 
    * @Description: TODO(这里用一句话描述这个方法的作用)
    @param   tRequestDto 请求信息
    @param
     * client @param @throws ApiException 参数 @return void 返回类型 @throws
    */

    private BaseResponseDto toReturnTicket(TaoBaoRequestDto tRequestDto) throws ApiException {

        BaseResponseDto responseDto = new BaseResponseDto();
        // 淘宝子订单号
        List<Long> taoBaoSuOrderIds = tRequestDto.getTaoBaoSuOrderIds();
        //  巴士无忧的订单id

        long orderId = tRequestDto.getOrderId();
        insertLog("topReturnInfo申请退票", orderId + ":请求巴士无忧的退票功能参数：" + JSON.toJSONString(tRequestDto));
        BusOrderRcInsert.insertRcInfo(orderId, "操作了退票。。", tRequestDto.getCaoZuoName());
        // 淘宝必须的一些配置
        TaoBaoConfigBean baoConfigBean = tRequestDto.getBaoConfigBean();

        insertLog("topReturnInfo申请退票", "orderId:" + orderId + "淘宝分销配置:" + JSONObject.toJSON(baoConfigBean));
        //访问淘宝必须需要的参数
        TaobaoClient client = new DefaultTaobaoClient(baoConfigBean.getTbApiUrl(), baoConfigBean.getAppkey(),
                baoConfigBean.getAppSecret());
        boolean isAgree = tRequestDto.getIsAgree(); //客人同意的话    
        if (isAgree) {
            //申请退票 ，只能申请一次。
            TaoBaoResponseSetReturnFeeDto doOrderReturnTicketSet = doOrderReturnTicketSet(tRequestDto, client,
                    baoConfigBean);
            // 循环查询订单详情
            boolean isSucces = doOrderReturnTicketSet.getIsSuccess();
            // 申请退票成功！
            if (isSucces) {
                responseDto = auditReturnInfo(tRequestDto, client, taoBaoSuOrderIds, responseDto);
            }
            else {
                String error_msg = doOrderReturnTicketSet.getError_msg();
                BusOrderRcInsert.insertRcInfo(orderId, "淘宝退票返回的信息:" + error_msg, "系统"); // 
                if (error_msg.contains("退票")) {
                    responseDto = auditReturnInfo(tRequestDto, client, taoBaoSuOrderIds, responseDto);
                }
            }
        }
        else { //未核实过客人的时候执行de
                   // 查看退票服务费接口
            BusRefundfeeGetRequest req = new BusRefundfeeGetRequest();
            // 淘宝订单号
            req.setAliTripOrderId(tRequestDto.getTaoBaoOrderId());

            req.setSubOrderIds(String.valueOf(taoBaoSuOrderIds.get(0)));

            BusRefundfeeGetResponse response = client.execute(req, baoConfigBean.getSessionKey());
            String body = response.getBody();
            insertLog("topReturnInfo申请退票", "orderId:" + orderId + ":任意一个访问退票接口返回的内容:" + body);
            if (body.contains("can_return_single_ticket")) {
                log.info(first_request_taobao_log_name + body);
                JSONObject bodyJson = JSONObject.parseObject(body);
                JSONObject bus_refundfee_get_responseJson = bodyJson.getJSONObject("bus_refundfee_get_response");

                int code = 103;
                boolean isSuccess = false;
                String dec = "";
                JSONObject resultJson = bus_refundfee_get_responseJson.getJSONObject("result");
                // 能过退单张票
                Boolean can_return_single_ticket = resultJson.getBoolean("can_return_single_ticket");
                //  查询价格
                // 任意选一张票去测试是否能够退票
                if (can_return_single_ticket) {
                    dec = "该车次不支持线上退票，请找供应核实";
                    BusOrderRcInsert.insertRcInfo(orderId, dec, "系统");
                    responseDto.setDec(dec);
                    responseDto.setSuccess(isSuccess);
                    responseDto.setCode(code);
                }
                else { //能支持退票的话，需要信息给客人返回回去。退票规则，客人是否同意
                    isSuccess = true;
                    code = 100;
                    String refund_fees = resultJson.getString("refund_fees");
                    JSONObject refund_fees_josn = JSONObject.parseObject(refund_fees);
                    String json_str = refund_fees_josn.getString("json");
                    JSONArray json_array = JSONArray.parseArray(json_str);
                    String ticket_Str = json_array.getString(0);
                    JSONObject ticket_json = JSONObject.parseObject(ticket_Str);
                    double commissionFee = ticket_json.getDouble("commissionFee") / 100d;
                    dec = "尊敬的客户，您的退票除去服务费需要手续费为：" + commissionFee + "，请注意我们不会退服务费！";
                    BusOrderRcInsert.insertRcInfo(orderId, dec, "系统");
                    responseDto.setDec(dec);
                    responseDto.setSuccess(isSuccess);
                    responseDto.setCode(code);

                }
            }
            else {
                BusOrderRcInsert.insertRcInfo(orderId, "申请淘宝退票时出现异常，请重新提交退票", "系统");
                responseDto.setDec("申请淘宝退票时出现异常，请重新提交退票");
                responseDto.setSuccess(false);
                responseDto.setCode(104);
            }
        }
        // 申请退票接口
        return responseDto;
    }

    /**
    * @Title: auditReturnInfo
    * @Description: TODO(这里用一句话描述这个方法的作用)
    * @param @param tRequestDto
    * @param @param client
    * @param @param taoBaoSuOrderIds
    * @param @param responseDto
    * @param @throws ApiException    参数
    * @return void    返回类型
    * @throws
    */

    private BaseResponseDto auditReturnInfo(TaoBaoRequestDto tRequestDto, TaobaoClient client,
            List<Long> taoBaoSuOrderIds, BaseResponseDto responseDto) throws ApiException {
        List<TaoBaoResponseOrderDetailsDto> queryOrder;
        //
        boolean isLoop = true;
        int countNull = 10;
        while (isLoop) {
            queryOrder = queryOrder(tRequestDto, client);
            if (queryOrder.isEmpty()) { //申请失败 
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                countNull--;
                if (countNull == 0) {
                    isLoop = false;
                }
            }
            else {
                for (TaoBaoResponseOrderDetailsDto taoBaoResponseOrderDetailsDto : queryOrder) {
                    //退票状态
                    int refund_status = taoBaoResponseOrderDetailsDto.getRefund_status();
                    switch (refund_status) {
                    case 1:
                        log.info("淘宝已建立退票。。。");
                        break;
                    case 2:
                        log.info("淘宝退票处理中。。。");
                        break;
                    case 3:
                        log.info("淘宝同意退款。。。");
                        responseDto.setCode(100);
                        responseDto.setDec("淘宝已同意退款");
                        responseDto.setSuccess(true);
                        responseDto.setData(queryOrder);

                        break;
                    case 4:
                        log.info("淘宝拒绝退款。。。");
                        if (Long.valueOf(taoBaoResponseOrderDetailsDto.getSub_order_id()) == taoBaoSuOrderIds
                                .get(taoBaoSuOrderIds.size() - 1)) {
                            isLoop = false;
                        }
                        responseDto.setCode(101);
                        responseDto.setDec("淘宝拒绝退款");
                        responseDto.setSuccess(false);
                        responseDto.setData(queryOrder);
                        break;
                    case 5:
                        log.info("淘宝已退过款。。。");

                        System.out.println("********************");
                        System.out.println(Long.valueOf(taoBaoResponseOrderDetailsDto.getSub_order_id()));
                        System.out.println(
                                Long.valueOf(taoBaoResponseOrderDetailsDto.getSub_order_id()) == taoBaoSuOrderIds
                                        .get(taoBaoSuOrderIds.size() - 1));

                        System.out.println(Long.valueOf(taoBaoResponseOrderDetailsDto.getSub_order_id())
                                .equals(taoBaoSuOrderIds.get(taoBaoSuOrderIds.size() - 1)));

                        System.out.println(taoBaoSuOrderIds.get(taoBaoSuOrderIds.size() - 1));

                        if (Long.valueOf(taoBaoResponseOrderDetailsDto.getSub_order_id())
                                .equals(taoBaoSuOrderIds.get(taoBaoSuOrderIds.size() - 1))) {
                            isLoop = false;
                        }
                        responseDto.setCode(102);
                        responseDto.setDec("淘宝已退过款");
                        responseDto.setSuccess(false);
                        responseDto.setData(queryOrder);
                        break;
                    default:
                        log.info("淘宝未知状态。。。" + refund_status);
                        if (Long.valueOf(taoBaoResponseOrderDetailsDto.getSub_order_id()) == taoBaoSuOrderIds
                                .get(taoBaoSuOrderIds.size() - 1)) {
                            isLoop = false;
                        }
                        break;
                    }
                }

            }

        }
        return responseDto;
    }

    /**
    * @Title: insertLog
    * @Description: TODO(这里用一句话描述这个方法的作用)
    * @param @param tRequestDto
    * @param @param orderId    参数
    * @return void    返回类型
    * @throws
    */

    private void insertLog(String logName, String logContent) {

        WriteLogThread writeLogThread = new WriteLogThread(logName, logContent);
        // 记录日志到本地
        writeLogThread.start();
    }

    /**
     * 申请退票 ，类
     * 
     * @Title: orderReturnTicketSet @Description:
     *         TODO(这里用一句话描述这个方法的作用) @param @param client @param @throws
     *         ApiException 参数 @return void 返回类型 @throws
     */

    private TaoBaoResponseSetReturnFeeDto doOrderReturnTicketSet(TaoBaoRequestDto tBaoRequestDto, TaobaoClient client,
            TaoBaoConfigBean baoConfigBean) throws ApiException {
        // 退票
        BaseTaobaoRequest<BusRefundSetResponse> req = new BusRefundSetRequest();
        B2BRefundOrderRq rq = new B2BRefundOrderRq();
        rq.setAliTripOrderId(tBaoRequestDto.getTaoBaoOrderId()); // 阿里订单号
        rq.setRefundReason(tBaoRequestDto.getDescription());
        rq.setSubOrderIds(tBaoRequestDto.getTaoBaoSuOrderIds()); //可以退多张
        rq.setSellerAgentId(Long.valueOf(baoConfigBean.getAppkey())); // 	
        ((BusRefundSetRequest) req).setParam0(rq);
        /*   insertLog("topReturnInfo申请退票",
                "orderId:" + tBaoRequestDto.getOrderId() + ":申请退票请求参数:" + JSONObject.toJSON(req));
        */ BusRefundSetResponse response = (BusRefundSetResponse) postTaoBaoApi(client, baoConfigBean, req);
        // 调用退票接口返回的信息
        String setBody = response.getBody();
        log.info(request_taobaoset_log_name + "[" + setBody + "]");

        insertLog("topReturnInfo申请退票", tBaoRequestDto.getOrderId() + ":调用退票申请的接口返回的信息：" + setBody);
        BusOrderRcInsert.insertRcInfo(tBaoRequestDto.getOrderId(), "调用退票接口成功", "系统");
        JSONObject setJson = JSON.parseObject(setBody);
        JSONObject bus_refund_set_responseJson = setJson.getJSONObject("bus_refund_set_response");
        JSONObject resultJson = bus_refund_set_responseJson.getJSONObject("result");
        TaoBaoResponseSetReturnFeeDto taoBaoResponseSetReturnFeeDto = JSON.toJavaObject(resultJson,
                TaoBaoResponseSetReturnFeeDto.class);
        return taoBaoResponseSetReturnFeeDto;
    }

    /**
     *   查询订单详情
     * @param client
     * @throws ApiException
     */
    private List<TaoBaoResponseOrderDetailsDto> queryOrder(TaoBaoRequestDto tBaoRequestDto, TaobaoClient client)
            throws ApiException {
        List<TaoBaoResponseOrderDetailsDto> taoBaoResponseOrderDetailsDtos = new LinkedList<TaoBaoResponseOrderDetailsDto>();
        TaoBaoConfigBean baoConfigBean = tBaoRequestDto.getBaoConfigBean();
        BaseTaobaoRequest<BusOrderGetResponse> req = new BusOrderGetRequest();
        B2BOrderQueryRq obj1 = new B2BOrderQueryRq();
        String aliTripOrderId = tBaoRequestDto.getTaoBaoOrderId();
        obj1.setAliTripOrderId(aliTripOrderId);
        ((BusOrderGetRequest) req).setParamB2BOrderQueryRQ(obj1);
        //  WriteLog.write("topReturnTicket订单详情", "请求参数" + tBaoRequestDto.getOrderId() + ":" + JSON.toJSON(req));
        BusOrderGetResponse rsp = (BusOrderGetResponse) postTaoBaoApi(client, baoConfigBean, req);
        String bodyStr = rsp.getBody();
        insertLog("top退票查询订单详情", tBaoRequestDto.getOrderId() + ":响应参数:" + bodyStr);
        if (bodyStr.contains("bus_b2b_ticket_info_list")) { //只有包含这个的时候才会
            JSONObject bodyObj = JSON.parseObject(bodyStr);

            JSONObject lists = bodyObj.getJSONObject("bus_order_get_response").getJSONObject("result")
                    .getJSONObject("b2b_bus_order_info").getJSONObject("bus_b2b_ticket_info_list");
            JSONArray b2_b_ticket_infoList = lists.getJSONArray("b2_b_ticket_info");

            List<Long> taoBaoSuOrderIds = tBaoRequestDto.getTaoBaoSuOrderIds();
            for (Object item : b2_b_ticket_infoList) {
                JSONObject itemJson = (JSONObject) item;
                for (int i = 0; i < taoBaoSuOrderIds.size(); i++) {
                    System.out.println(itemJson.getLong("sub_order_id"));
                    System.out.println(taoBaoSuOrderIds.get(i));
                    System.out.println(itemJson.getLong("sub_order_id").compareTo(taoBaoSuOrderIds.get(i)) == 0);
                    if (itemJson.getLong("sub_order_id").compareTo(taoBaoSuOrderIds.get(i)) == 0) {
                        taoBaoResponseOrderDetailsDtos
                                .add(JSON.toJavaObject(itemJson, TaoBaoResponseOrderDetailsDto.class));
                    }
                }
            }
        }

        return taoBaoResponseOrderDetailsDtos;

    }

    private static TaobaoResponse postTaoBaoApi(TaobaoClient client, TaoBaoConfigBean baoConfigBean,
            BaseTaobaoRequest<? extends TaobaoResponse> req) throws ApiException {
        return client.execute(req, baoConfigBean.getSessionKey());
    }

    public static void main(String[] args) {
        TaoBaoReturnTicketIpml ipml = new TaoBaoReturnTicketIpml();
        TaoBaoRequestDto dRequestDto = new TaoBaoRequestDto();
        TaoBaoConfigBean baoConfigBeanOld = new TaoBaoConfigBean();
        baoConfigBeanOld.setAppkey("23572324");
        baoConfigBeanOld.setAppSecret("9c02f22d56826404f7d402eb41fbd402");
        baoConfigBeanOld.setSessionKey("61002289d44699e3f588a2f23d78b8ccd84012716aaddec3073415605");
        baoConfigBeanOld.setTbApiUrl("http://gw.api.taobao.com/router/rest");
        dRequestDto.setBaoConfigBean(baoConfigBeanOld);
        //  dRequestDto.setOrderId(3186285L);
        dRequestDto.setOrderId(3554157l);
        dRequestDto.setDescription("客人退票");
        dRequestDto.setTaoBaoOrderId("911069168605");
        List<Long> taoBaoSuOrderIds = new ArrayList<Long>();
        taoBaoSuOrderIds.add(1199279352L);
        dRequestDto.setTaoBaoSuOrderIds(taoBaoSuOrderIds);
        dRequestDto.setTicketId(5);
        dRequestDto.setIsAgree(true);
        try {
            BaseResponseDto responseResult = ipml.getResponseResult(dRequestDto);
            System.out.println(JSON.toJSON(responseResult));
        }
        catch (ReturnTicketExection e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void mainNoRun(String[] args) {
        TaoBaoReturnTicketIpml ipml = new TaoBaoReturnTicketIpml();
        TaoBaoRequestDto dRequestDto = new TaoBaoRequestDto();
        TaoBaoConfigBean baoConfigBeanOld = new TaoBaoConfigBean();
        baoConfigBeanOld.setAppkey("23572324");
        baoConfigBeanOld.setAppSecret("9c02f22d56826404f7d402eb41fbd402");
        baoConfigBeanOld.setSessionKey("61002289d44699e3f588a2f23d78b8ccd84012716aaddec3073415605");
        baoConfigBeanOld.setTbApiUrl("http://gw.api.taobao.com/router/rest");
        dRequestDto.setBaoConfigBean(baoConfigBeanOld);
        dRequestDto.setOrderId(3469526L);
        dRequestDto.setDescription("客人退票");
        dRequestDto.setTaoBaoOrderId("704287244605");
        List<Long> taoBaoSuOrderIds = new ArrayList<Long>();
        taoBaoSuOrderIds.add(825095500L);
        dRequestDto.setTaoBaoSuOrderIds(taoBaoSuOrderIds);
        dRequestDto.setTicketId(5);
        try {
            BaseResponseDto responseResult = ipml.getResponseResult(dRequestDto);
            System.out.println(JSON.toJSON(responseResult));
        }
        catch (ReturnTicketExection e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
