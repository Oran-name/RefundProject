package com.ccservice.bus.bean;

import com.ccservice.bus.annotation.FieldName;

public  class BaseResponseDto {
	@FieldName("response_code")
	private int code;
	@FieldName("response_isSuccess")
	private boolean isSuccess;
	@FieldName("response_info")
	private Object data;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public boolean getIsSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	@FieldName("response_dec")
	private String dec;
	public String getDec() {
		return dec;
	}
	public void setDec(String dec) {
		this.dec = dec;
	}
	
	
}
	