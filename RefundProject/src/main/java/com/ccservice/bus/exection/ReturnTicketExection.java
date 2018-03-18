package com.ccservice.bus.exection;

import com.ccservice.Util.file.WriteLog;

public class ReturnTicketExection extends Exception {
    private String exectionFileName = "bus退票异常";

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ReturnTicketExection(Long orderId, String message) {
        super(message);
        WriteLog.write(exectionFileName, "orderId:" + orderId + "[" + message + "]");
    }

    public ReturnTicketExection(String message) {
        super(message);
        WriteLog.write(exectionFileName, "请求的信息为空：不能被转换[" + message + "]");
    }

}
