package com.ibm.commerce.payment.samples.tokenization.utils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.ibm.commerce.payment.samples.tokenization.PaymentTokenSampleResource;
import com.ibm.commerce.payment.samples.tokenization.entities.TransactionInfo;

public class TokenManagement{
	private static final String CLASS_NAME = PaymentTokenSampleResource.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	/**
	 * The state for the payment or credit transaction when the transaction has not finished yet.
	 * It might mean that the plug-in implements an offline protocol that requires
	 * external intervention to move the payment or credit transaction into another state.
	 */
	public static final short TRANSACTION_STATE_PENDING = 1;

	/**
	 * The state for the payment or credit transaction on a successful execution.
	 */
	public static final short TRANSACTION_STATE_SUCCESS = 2;

	/**
	 * The state for the payment or credit transaction when the transaction has failed.
	 */
	public static final short TRANSACTION_STATE_FAILED = 3;

	/**
	 * The state for the payment or credit transaction when the transaction has been canceled.
	 */
	public static final short TRANSACTION_STATE_CANCELED = 4;	
	
	/**
	 * The state for the payment or credit transaction when the transaction has a invalid state.
	 */
	public static final short TRANSACTION_STATE_INVALID = 8; 
	
	private static TokenManagement tokenMgr = null;
	private static Map<String, Map<String, String>> paymentInfoHash;
	private static Map<String, TransactionInfo> transHash;
	
	private TokenManagement(){
		paymentInfoHash = new HashMap<String, Map<String, String>>();
		transHash = new HashMap<String, TransactionInfo>();
	}
	
	public static TokenManagement getInstance(){
		if(tokenMgr == null){
			tokenMgr = new TokenManagement();
		}
		return tokenMgr;
	}
	
	//create token
	public String createToken(Map<String, String> pi) throws Exception {
		
		LOGGER.info("createToken start");
		
		String cardNumber = pi.get(PaymentTokenSampleResource.PAYMENT_ACCOUNT);
		
		//Generate token using card number
		StringBuilder tokenBuilder = new StringBuilder();
		tokenBuilder.append("T").append(getRandom()).append(cardNumber.substring(12));
		String token = tokenBuilder.toString();
		
		//Get card number display value
		String diaplayValue = getDisplayValue(cardNumber);
		pi.put(PaymentTokenSampleResource.DISPLAY_VALUE, diaplayValue);
		
		//Store the payment info in cache
		paymentInfoHash.put(token, pi);
		
		//return the payment info
		TokenManagementHelper tokenHelper = new TokenManagementHelper();
		tokenHelper.saveToken(token, pi);
		
		LOGGER.info("createToken end");
		return token;	
	}
	
	//fetch payment data
	public Map<String, String> fetchPaymentData(String token) throws Exception{
		LOGGER.info("fetchToken start");
	
		Map<String, String> paymentInfoRes = paymentInfoHash.get(token);
		
		if (paymentInfoRes == null) {
			// fetch from helper
			TokenManagementHelper tokenHelper = new TokenManagementHelper();
			paymentInfoRes = tokenHelper.fetchToken(token);
		}
		
		// cache payment info
		paymentInfoHash.put(token, paymentInfoRes);
		
		LOGGER.info("fetchToken end");
		return paymentInfoRes;
	}
	
	//authorizes the payment and return transaction token
	public String authorize(String token,BigDecimal amount) throws Exception{
		LOGGER.info("authorize start");
		
		String  transactionToken = null;
		Map<String, String> paymenInfoRes = fetchPaymentData(token);
		
		if(paymenInfoRes != null){
			TransactionInfo transInfo = new TransactionInfo();
			transactionToken = String.valueOf(Calendar.getInstance().getTimeInMillis());
			
			transInfo.setTrans_token(transactionToken);
			transInfo.setToken(token);
			transInfo.setStatus(TRANSACTION_STATE_PENDING);
			transInfo.setAmount(amount);
			
			transHash.put(transactionToken, transInfo);
			
			// save transaction token
			TokenManagementHelper tokenHelper = new TokenManagementHelper();
			tokenHelper.saveTransToken(token, transactionToken, TRANSACTION_STATE_PENDING);
		}
		LOGGER.info("transactionToken = " + transactionToken);
		LOGGER.info("authorize end");
		
		return transactionToken;
	}
	
	//update token
	public void updateToken(String token, Map<String, String> newPI) throws Exception{
		LOGGER.info("updateToken start");
		LOGGER.info("token=" + token);
		
		// update to cache
		Map<String, String> currentPI = fetchPaymentData(token);
		newPI.put(PaymentTokenSampleResource.DISPLAY_VALUE, currentPI.get(PaymentTokenSampleResource.DISPLAY_VALUE));
		paymentInfoHash.put(token, newPI);
		
		// update
		TokenManagementHelper tokenHelper = new TokenManagementHelper();
		tokenHelper.updateToken(token, newPI);
		
		LOGGER.info("updateToken end");
	}
	
	//remove token
	public void removeToken(String token) throws Exception {
		LOGGER.info("removeToken start");
		LOGGER.info("token=" + token);
		
		paymentInfoHash.remove(token);
		
		// remove
		TokenManagementHelper tokenHelper = new TokenManagementHelper();
		tokenHelper.removeToken(token);
		
		LOGGER.info("removeToken end");
	}

	public Boolean validateTrans(String token, String transactionToken) throws Exception {
		LOGGER.info("validateTrans start");
		LOGGER.info("token = " + token);
		LOGGER.info("transactionToken = " + transactionToken);
		boolean result = false;

		if(token != null && transactionToken != null){				
			String status = null;		
			if(transHash.get(transactionToken) != null){
				status = String.valueOf(transHash.get(transactionToken).getStatus());			
			}
			else{
				TokenManagementHelper tokenHelper = new TokenManagementHelper();	
				status = tokenHelper.getTransTokenStatus(token, transactionToken);
			}
		
			if(status != null 
					&& String.valueOf(PaymentTokenSampleResource.TRANSACTION_STATE_PENDING).equals(status)){
				result = true;
			}
		}
		
		LOGGER.info("validateTrans end");
		return result;
	}

	public String createTransToken(String token, String transToken) throws Exception {
		LOGGER.info("createTransToken start");
		LOGGER.info("token = " + token);

		TokenManagementHelper tokenHelper = new TokenManagementHelper();
		if(transToken != null){			
			TransactionInfo transInfo = transHash.get(transToken);
			if(transInfo != null){
				transInfo.setStatus(TRANSACTION_STATE_SUCCESS);
			}
			
			tokenHelper.saveTransToken(token, transToken, TRANSACTION_STATE_SUCCESS);
		}
		
		TransactionInfo transInfo = new TransactionInfo();
		String transactionToken = String.valueOf(Calendar.getInstance().getTimeInMillis());
			
		transInfo.setTrans_token(transactionToken);
		transInfo.setToken(token);
		transInfo.setStatus(TRANSACTION_STATE_PENDING);
			
		transHash.put(transactionToken, transInfo);
			
		// save transaction token
		tokenHelper.saveTransToken(token, transactionToken, TRANSACTION_STATE_PENDING);
		
		LOGGER.info("transactionToken = " + transactionToken);
		LOGGER.info("createTransToken end");
		return transactionToken;
	}
	
	//create a Random between upper and lower
	private static int getRandom(){
		int lower = 1000;
		int upper = 9999;
		int a = upper-lower;
		int random = (int)(lower+Math.random()*a);
		return random;
		
	}
	
	private String getDisplayValue(String value){
		StringBuilder valueBuilder = new StringBuilder();
		valueBuilder.append("************").append(value.substring(12));
		String displayValue = valueBuilder.toString();
		return displayValue;
	}
}
