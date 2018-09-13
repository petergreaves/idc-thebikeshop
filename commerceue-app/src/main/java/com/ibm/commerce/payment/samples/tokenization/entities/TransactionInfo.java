package com.ibm.commerce.payment.samples.tokenization.entities;

import java.math.BigDecimal;

public class TransactionInfo {
	private String trans_token;
	private String token;
	private short status;
	private BigDecimal amount;
	
	public String getTrans_token() {
		return trans_token;
	}
	public void setTrans_token(String trans_token) {
		this.trans_token = trans_token;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public short getStatus() {
		return status;
	}
	public void setStatus(short status) {
		this.status = status;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
}
