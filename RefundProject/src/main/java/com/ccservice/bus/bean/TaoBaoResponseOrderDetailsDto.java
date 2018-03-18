package com.ccservice.bus.bean;

public class TaoBaoResponseOrderDetailsDto {
	private String rider_cert_type,
					rider_name,
					rider_seat_number,
					service_charge,
					sub_order_id,
					ticket_id,
					ticket_price;
	private String commission_fee;
	private String refund_fee;
	private int refund_status;
	
	public String getCommission_fee() {
		return commission_fee;
	}

	public void setCommission_fee(String commission_fee) {
		this.commission_fee = commission_fee;
	}

	public String getRefund_fee() {
		return refund_fee;
	}

	public void setRefund_fee(String refund_fee) {
		this.refund_fee = refund_fee;
	}

	public int getRefund_status() {
		return refund_status;
	}

	public void setRefund_status(int refund_status) {
		this.refund_status = refund_status;
	}

	public String getRider_cert_type() {
		return rider_cert_type;
	}

	public void setRider_cert_type(String rider_cert_type) {
		this.rider_cert_type = rider_cert_type;
	}

	public String getRider_name() {
		return rider_name;
	}

	public void setRider_name(String rider_name) {
		this.rider_name = rider_name;
	}

	public String getRider_seat_number() {
		return rider_seat_number;
	}

	public void setRider_seat_number(String rider_seat_number) {
		this.rider_seat_number = rider_seat_number;
	}

	public String getService_charge() {
		return service_charge;
	}

	public void setService_charge(String service_charge) {
		this.service_charge = service_charge;
	}

	public String getSub_order_id() {
		return sub_order_id;
	}

	public void setSub_order_id(String sub_order_id) {
		this.sub_order_id = sub_order_id;
	}

	public String getTicket_id() {
		return ticket_id;
	}

	public void setTicket_id(String ticket_id) {
		this.ticket_id = ticket_id;
	}

	public String getTicket_price() {
		return ticket_price;
	}

	public void setTicket_price(String ticket_price) {
		this.ticket_price = ticket_price;
	}
	
	
}	
