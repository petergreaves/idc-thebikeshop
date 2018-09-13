package com.ibm.commerce.payment.samples.tokenization;

/*
 *-----------------------------------------------------------------
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WebSphere Commerce
 *
 * (C) Copyright IBM Corp. 2015, 2016
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 *-----------------------------------------------------------------
 */

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.commerce.foundation.entities.ErrorUEResponse;
import com.ibm.commerce.foundation.entities.ExceptionData;
import com.ibm.commerce.payment.entities.FinancialTransaction;
import com.ibm.commerce.payment.entities.PaymentInstruction;
import com.ibm.commerce.payment.samples.tokenization.utils.TokenManagement;
import com.ibm.commerce.payment.ue.entities.CreatePaymentTokenCmdUEInput;
import com.ibm.commerce.payment.ue.entities.CreatePaymentTokenCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.FetchPaymentTokenCmdUEInput;
import com.ibm.commerce.payment.ue.entities.FetchPaymentTokenCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.PaymentApproveCmdUEInput;
import com.ibm.commerce.payment.ue.entities.PaymentApproveCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.RemovePaymentTokenCmdUEInput;
import com.ibm.commerce.payment.ue.entities.RemovePaymentTokenCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.SessionCleanCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.UpdatePaymentTokenCmdUEInput;
import com.ibm.commerce.payment.ue.entities.UpdatePaymentTokenCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.VerifyPaymentAccountCmdUEInput;
import com.ibm.commerce.payment.ue.entities.VerifyPaymentAccountCmdUEOutput;
import com.ibm.commerce.ue.rest.BaseResource;
import com.ibm.commerce.ue.rest.MessageKey;

@Path("/payment/payment_token/sample")
@SwaggerDefinition(
        tags = {
                @Tag(name = "payment - tokenization", description = "API Extensions for payment customization.")
        }
)
@Api(value = "/payment/payment_token/sample", tags = "payment - tokenization")
public class PaymentTokenSampleResource extends BaseResource {
	
	private static final String CLASS_NAME = PaymentTokenSampleResource.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	private static Locale locale = new Locale(
			MessageKey.getConfig("_CONFIG_LANGUAGE"),
			MessageKey.getConfig("_CONFIG_LOCALE"));
	
	public static final String PAYMENT_TOKEN = "payment_token";
	
	public static final String PAYMENT_ACCOUNT = "account";
	public static final String TRANSCTIONID = "transction_id";
	public static final String DISPLAY_VALUE = "display_value";
	public static final String EXPIRE_YEAR = "expire_year";
	public static final String EXPIRE_MONTH = "expire_month";
	public static final String CVC = "cc_cvc";
	public static final String CITY = "billto_city";
	public static final String TOKEN_FLAG = "allowRemoveToken";
	
	public static final String ACCOUNT_VERIFY_RESULT = "verifyResult";
	public static final String ACCOUNT_VERIFY_RESULT_SUCCESS = "success";
	public static final String ACCOUNT_VERIFY_RESULT_FAILED = "failed";
	private static final String ERROR_MESSAGE = "errorMessage"; 
	
	/** The response code when the operation is successful. */
	public static final String RESPONSECODE_SUCCESS = "0";

	/** The reason code when the operation is successful. */
	public static final String REASONCODE_NONE = "0";

	/**
	 * The state for the payment or credit transaction when the transaction has
	 * not finished yet. It might mean that the plug-in implements an offline
	 * protocol that requires external intervention to move the payment or
	 * credit transaction into another state.
	 */
	public static final short TRANSACTION_STATE_PENDING = 1;

	private TokenManagement tokenMgr;
	
	/**
	 * Default constructor.
	 */
	public PaymentTokenSampleResource() {		
		tokenMgr=TokenManagement.getInstance();
	}
	
	@POST
	@Path("/create_payment_token")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for CreatePaymentTokenCmd task command.",
			notes = "CreatePaymentTokenCmd is a task command which is used to create payment token.",
			response = CreatePaymentTokenCmdUEOutput.class)
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response createPaymentToken(
			@ApiParam(name="CreatePaymentTokenCmdUEInput", value = "CreatePaymentTokenCmd UE Input Parameter", required = true)
			CreatePaymentTokenCmdUEInput ueRequest) throws SQLException, Exception {

		final String METHOD_NAME = "createPaymentToken()";
		LOGGER.info("Entering " + CLASS_NAME + " " + METHOD_NAME);
		
		Map<String, String> tokenData = new HashMap<String, String>();
		Response response;
		
		@SuppressWarnings("unchecked")
		Map<String, String> pi = setInputPara(ueRequest.getProtocolData());

		if(pi.get(PAYMENT_ACCOUNT) != null) {
			String token = tokenMgr.createToken(pi);
			
		    tokenData.put(PAYMENT_TOKEN, token);
		    tokenData.put(DISPLAY_VALUE, pi.get(DISPLAY_VALUE));
		    tokenData.put(CVC, pi.get(CVC));
		    
		    CreatePaymentTokenCmdUEOutput ueResponse = new CreatePaymentTokenCmdUEOutput();
			ueResponse.setPaymentToken(tokenData);
		    response = Response.ok(ueResponse).build();
		} 
		else{
			//throw exception
			ErrorUEResponse ueResponse = new ErrorUEResponse();
			List<ExceptionData> errors = new ArrayList<ExceptionData>();
			ExceptionData error = new ExceptionData();
			error.setCode("400");
			error.setMessageKey("_ERR_INVALID_PARAMETER_VALUE");
			error.setMessage(MessageKey.getMessage(locale, "_ERR_INVALID_PARAMETER_VALUE",
					new Object[] { "account", "" }));
			errors.add(error);
			ueResponse.setErrors(errors);
			response = Response.status(400).type(MediaType.APPLICATION_JSON).entity(ueResponse).build();
		}

		LOGGER.info("Exiting " + CLASS_NAME + " " + METHOD_NAME);
		return response;
	}	
	
	@POST
	@Path("/fetch_payment_token")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for FetchPaymentTokenCmd task command.",
			notes = "FetchPaymentTokenCmd is a task command which is used to fetch the payment token data by token number.", 
	response = SessionCleanCmdUEOutput.class)
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response fetchPaymentToken(@ApiParam(name="FetchPaymentTokenCmdUEInput",value = "FetchPaymentTokenCmd UE Input Parameter",
			required = true)FetchPaymentTokenCmdUEInput ueRequest) throws Exception {

		final String METHOD_NAME = "fetchPaymentToken()";
		LOGGER.info("Entering " + CLASS_NAME + " " + METHOD_NAME);
		
		Response response;
		
		@SuppressWarnings("unchecked")
		Map<String, String> protocolData = ueRequest.getProtocolData();
		String tokenId = protocolData.get(PAYMENT_TOKEN);
		
		if(tokenId != null){
			Map<String, String> res = tokenMgr.fetchPaymentData(tokenId);
			
			if(res != null){
				FetchPaymentTokenCmdUEOutput ueResponse = new FetchPaymentTokenCmdUEOutput();
		    
				Map<String,String> tokenData = new HashMap<String,String>();
				tokenData.put(PAYMENT_TOKEN, tokenId);
				tokenData.put(DISPLAY_VALUE, res.get(DISPLAY_VALUE));
				tokenData.put(CVC, res.get(CVC));
			
				ueResponse.setPaymentToken(tokenData);
				response = Response.ok(ueResponse).build();
				
				LOGGER.info("Exiting " + CLASS_NAME + " " + METHOD_NAME);
				return response;
			}
		}

		//throw exception
		ErrorUEResponse ueResponse = new ErrorUEResponse();
		List<ExceptionData> errors = new ArrayList<ExceptionData>();
		ExceptionData error = new ExceptionData();
		error.setCode("400");
		error.setMessageKey("_ERR_INVALID_PARAMETER_VALUE");
		error.setMessage(MessageKey.getMessage(locale, "_ERR_INVALID_PARAMETER_VALUE",
				new Object[] { PAYMENT_TOKEN, tokenId }));
		errors.add(error);
		ueResponse.setErrors(errors);
		response = Response.status(400).type(MediaType.APPLICATION_JSON).entity(ueResponse).build();
		
		LOGGER.info("Exiting " + CLASS_NAME + " " + METHOD_NAME);
		return response;
	}	
	
	@POST
	@Path("/remove_payment_token")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for RemovePaymentTokenCmd task command.",
			notes = "RemovePaymentTokenCmd is a task command which is used to remove the payment token data by token number.", 
	response = SessionCleanCmdUEOutput.class)
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response removePaymentToken(@ApiParam(name="RemovePaymentTokenCmdUEInput",value = "RemovePaymentTokenCmd UE Input Parameter",
			required = true)RemovePaymentTokenCmdUEInput ueRequest) throws Exception {

		final String METHOD_NAME = "removePaymentToken()";
		LOGGER.info("Entering " + CLASS_NAME + " " + METHOD_NAME);
		
		Response response;
		
		@SuppressWarnings("unchecked")
		Map<String,Object> protocolData = ueRequest.getProtocolData();
		Object tokenId = protocolData.get(PAYMENT_TOKEN);
		if(tokenId != null){
			tokenMgr.removeToken(tokenId.toString());		
		    RemovePaymentTokenCmdUEOutput ueResponse = new RemovePaymentTokenCmdUEOutput();
			Map<String,String> tokenData = new HashMap<String,String>();
			tokenData.put(DISPLAY_VALUE, null);
			ueResponse.setPaymentToken(tokenData);			
			response = Response.ok(ueResponse).build();
		}
		else{
			//throw exception
			ErrorUEResponse ueResponse = new ErrorUEResponse();
			List<ExceptionData> errors = new ArrayList<ExceptionData>();
			ExceptionData error = new ExceptionData();
			error.setCode("400");
			error.setMessageKey("_ERR_INVALID_PARAMETER_VALUE");
			error.setMessage(MessageKey.getMessage(locale, "_ERR_INVALID_PARAMETER_VALUE",
					new Object[] { PAYMENT_TOKEN, tokenId }));
			errors.add(error);
			ueResponse.setErrors(errors);
			response = Response.status(400).type(MediaType.APPLICATION_JSON).entity(ueResponse).build();
		}
		
		LOGGER.info("Exiting " + CLASS_NAME + " " + METHOD_NAME);
		return response;
	}	
	
	@POST
	@Path("/update_payment_token")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for UpdatePaymentTokenCmd task command.",
			notes = "UpdatePaymentTokenCmd is a task command which is used to update the payment token data with the token number and payment data.", 
	response = SessionCleanCmdUEOutput.class)
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response updatePaymentToken(@ApiParam(name="UpdatePaymentTokenCmdUEInput",value = "UpdatePaymentTokenCmd UE Input Parameter",
			required = true)UpdatePaymentTokenCmdUEInput ueRequest) throws SQLException, Exception {

		final String METHOD_NAME = "updatePaymentToken()";
		LOGGER.info("Entering " + CLASS_NAME + " " + METHOD_NAME);
		
		Response response;

		@SuppressWarnings("unchecked")
		Map<String, Object> protocolData = ueRequest.getProtocolData();		
		Map<String, String> newPI = setInputPara(protocolData);
		
		String currentToken = (String) protocolData.get(PAYMENT_TOKEN);
		if(currentToken != null){
			Map<String, String> currentPI = tokenMgr.fetchPaymentData(currentToken);			
						
			// judge if it's a quick checkout profile token
			Boolean flag = (Boolean) protocolData.get(TOKEN_FLAG);
			LOGGER.info("Flag - allowRemoveToken: " + flag);
				
			if(flag != null && !flag){
					// it's a quick checkout profile token, can't be deleted.
			}
			else {
				tokenMgr.removeToken(currentToken);
			}
				
			// save new token
			// save the unchanged properties
			if(currentPI != null){
				Set<String> keys = currentPI.keySet();
				for(String key : keys){
					if(newPI.get(key) == null){
						newPI.put(key, currentPI.get(key));
					}
				}
			}
			currentToken = tokenMgr.createToken(newPI);
			currentPI = newPI;				
			
		    UpdatePaymentTokenCmdUEOutput ueResponse = new UpdatePaymentTokenCmdUEOutput();
		    
		    Map<String,String> tokenData = new HashMap<String,String>();
		    tokenData.put(PAYMENT_TOKEN, currentToken);
			tokenData.put(DISPLAY_VALUE, currentPI.get(DISPLAY_VALUE));
			
			ueResponse.setPaymentToken(tokenData);			
			response = Response.ok(ueResponse).build();
		}
		else{
			//throw exception
			ErrorUEResponse ueResponse = new ErrorUEResponse();
			List<ExceptionData> errors = new ArrayList<ExceptionData>();
			ExceptionData error = new ExceptionData();
			error.setCode("400");
			error.setMessageKey("_ERR_INVALID_PARAMETER_VALUE");
			error.setMessage(MessageKey.getMessage(locale, "_ERR_INVALID_PARAMETER_VALUE",
					new Object[] { PAYMENT_TOKEN, currentToken }));
			errors.add(error);
			ueResponse.setErrors(errors);
			response = Response.status(400).type(MediaType.APPLICATION_JSON).entity(ueResponse).build();
		}
		
		LOGGER.info("Exiting " + CLASS_NAME + " " + METHOD_NAME);
		return response;
	}
	
	@POST
	@Path("/verify_payment_account")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "The API extension for VerifyPaymentAccountCmd task command.",
	notes = "VerifyPaymentAccountCmd is a task command which is used to verify the payment account.", 
    response = VerifyPaymentAccountCmdUEOutput.class)
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response verifyPaymentAccount(@ApiParam(name="VerifyPaymentAccountCmdUEInput",value = "VerifyPaymentAccountCmdUEInput UE Input Parameter",
			required = true)VerifyPaymentAccountCmdUEInput ueRequest) {

		final String METHOD_NAME = "verifyPaymentAccount()";
		LOGGER.info("Entering " + CLASS_NAME + " " + METHOD_NAME);
		
		Response response;
		
		@SuppressWarnings("unchecked")
		Map<String,String> pi = setInputPara(ueRequest.getProtocolData());
		if(pi.get(PAYMENT_ACCOUNT) != null) {
			VerifyPaymentAccountCmdUEOutput ueResponse = new VerifyPaymentAccountCmdUEOutput();
			Map<String,String> results = new HashMap<String,String>();
			if(pi.get(PAYMENT_ACCOUNT).length()!=16){
				results.put(ACCOUNT_VERIFY_RESULT, ACCOUNT_VERIFY_RESULT_FAILED);
				results.put(ERROR_MESSAGE, "Your account number is not supported.");
			}
			else{
				results.put(ACCOUNT_VERIFY_RESULT, ACCOUNT_VERIFY_RESULT_SUCCESS);
			}
			
			ueResponse.setVerificationResult(results);

			System.out.println("verify result is " + results.get(ACCOUNT_VERIFY_RESULT));
			response = Response.ok(ueResponse).build();
		} 
		else{
			//throw exception
			ErrorUEResponse ueResponse = new ErrorUEResponse();
			List<ExceptionData> errors = new ArrayList<ExceptionData>();
			ExceptionData error = new ExceptionData();
			error.setCode("400");
			error.setMessageKey("_ERR_INVALID_PARAMETER_VALUE");
			error.setMessage(MessageKey.getMessage(locale, "_ERR_INVALID_PARAMETER_VALUE",
					new Object[] { "account", "" }));
			errors.add(error);
			ueResponse.setErrors(errors);
			response = Response.status(400).type(MediaType.APPLICATION_JSON).entity(ueResponse).build();
		}	
		
		LOGGER.info("Exiting " + CLASS_NAME + " " + METHOD_NAME);
		return response;

	}	
	
	@SuppressWarnings("unchecked")
	@POST
	@Path("/approve_payment")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for PaymentApproveCmd task command.", notes = "PaymentApproveCmd is a task command which is used to authorize the payment.", response = PaymentApproveCmdUEOutput.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Application error"),
			@ApiResponse(code = 500, message = "System error") })
	public Response approvePayment(
			@ApiParam(name = "PaymentApproveCmdUEInput", value = "PaymentApproveCmd UE Input Parameter", required = true) 
			PaymentApproveCmdUEInput ueRequest) throws Exception {

		final String METHOD_NAME = "approvePayment()";
		LOGGER.info("Entering " + CLASS_NAME + " " + METHOD_NAME);

		FinancialTransaction financialTransaction = ueRequest.getFinancialTransaction();
		PaymentInstruction paymentInstruction = financialTransaction.getPayment().getPaymentInstruction();

		Map<String, Object> extData = paymentInstruction.getExtendedData();
		String token = (String) extData.get(PAYMENT_TOKEN);
		
		if(token != null){
			String transctionId = tokenMgr.createTransToken(token, null);
			extData.put(TRANSCTIONID, transctionId);

			// test update transaction property
			financialTransaction.setResponseCode(RESPONSECODE_SUCCESS);
			financialTransaction.setReasonCode(REASONCODE_NONE);
			financialTransaction.setState(TRANSACTION_STATE_PENDING);
			financialTransaction.setTrackingId("1");

			PaymentApproveCmdUEOutput ueResponse = new PaymentApproveCmdUEOutput();
			ueResponse.setFinancialTransaction(financialTransaction);
			Response response = Response.ok(ueResponse).build();

			LOGGER.info("Exiting " + CLASS_NAME + " " + METHOD_NAME);
			return response;
		}
		else{
			Response response = generateErrorResponse(PAYMENT_TOKEN, token);
					
			LOGGER.info("Exiting " + CLASS_NAME + " " + METHOD_NAME);
			return response;
		}
	}
	private Map<String, String> setInputPara(Map<String, Object> protocolData){
		Map<String, String> pi = new HashMap<String, String>();
		
		String accountNumber = (String) protocolData.get(PAYMENT_ACCOUNT);
		String year = (String) protocolData.get(EXPIRE_YEAR);
		String month = (String) protocolData.get(EXPIRE_MONTH);
		String cvc = (String) protocolData.get(CVC);
		String city = (String) protocolData.get(CITY);
		
		if(accountNumber != null){
			pi.put(PAYMENT_ACCOUNT, accountNumber);
		}
		if(year != null){
			pi.put(EXPIRE_YEAR, year);
		}
		if(month != null){
			pi.put(EXPIRE_MONTH, month);
		}
		if(cvc != null){
			pi.put(CVC, cvc);
		}
		if(city != null){
			pi.put(CITY, city);
		}
		
		return pi;
	}
	
	private Response generateErrorResponse(String propName, String propValue){
		ErrorUEResponse ueResponse = new ErrorUEResponse();
		List<ExceptionData> errors = new ArrayList<ExceptionData>();
		ExceptionData error = new ExceptionData();
		error.setCode("400");
		error.setMessageKey("_ERR_INVALID_PARAMETER_VALUE");
		error.setMessage(MessageKey.getMessage(locale, "_ERR_INVALID_PARAMETER_VALUE",
				new Object[] {propName, propValue}));
		errors.add(error);
		ueResponse.setErrors(errors);
		Response response = Response.status(400).type(MediaType.APPLICATION_JSON).entity(ueResponse).build();
		
		return response;
	}
}

