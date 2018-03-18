package com.ccservice.bus.bean;

import java.io.Serializable;

import com.ccservice.bus.annotation.FieldName;

public class TaoBaoResponseSetReturnFeeDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@FieldName("taobao_resp_code" /*可能为空*/)
	private  String error_code;
	@FieldName("taobao_resp_msg"/*可能为空*/)
	private  String error_msg;
	@FieldName("taobao_resp_success"/*只有true和false*/)
	private  boolean success;
	@FieldName("taobao_resp_success1"/*只有true和false*/)
	private  boolean success1;
	public String getError_code() {
		return error_code;
	}
	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
	public String getError_msg() {
		return error_msg;
	}
	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}
	public boolean getIsSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public boolean isSuccess1() {
		return success1;
	}
	public void setSuccess1(boolean success1) {
		this.success1 = success1;
	}
	
	
	
	
	
}
