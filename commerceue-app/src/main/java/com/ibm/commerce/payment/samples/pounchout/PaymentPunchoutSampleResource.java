package com.ibm.commerce.payment.samples.pounchout;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.commerce.foundation.ue.compensation.entities.CompensateForTxRollbackCmdUEInput;
import com.ibm.commerce.foundation.ue.compensation.entities.CompensateForTxRollbackCmdUEOutput;
import com.ibm.commerce.foundation.ue.compensation.entities.UEInvocation;
import com.ibm.commerce.foundation.ue.util.UserExitUtil;
import com.ibm.commerce.payment.entities.FinancialTransaction;
import com.ibm.commerce.payment.entities.PaymentInstruction;
import com.ibm.commerce.payment.ue.entities.GetPunchoutURLCmdUEInput;
import com.ibm.commerce.payment.ue.entities.GetPunchoutURLCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.PaymentApproveAndDepositCmdUEInput;
import com.ibm.commerce.payment.ue.entities.PaymentApproveAndDepositCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.PaymentApproveCmdUEInput;
import com.ibm.commerce.payment.ue.entities.PaymentApproveCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.PaymentCreditCmdUEInput;
import com.ibm.commerce.payment.ue.entities.PaymentCreditCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.PaymentDepositCmdUEInput;
import com.ibm.commerce.payment.ue.entities.PaymentDepositCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.PaymentReverseApprovalCmdUEInput;
import com.ibm.commerce.payment.ue.entities.PaymentReverseApprovalCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.PaymentReverseDepositCmdUEInput;
import com.ibm.commerce.payment.ue.entities.PaymentReverseDepositCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.ProcessPunchoutResponseCmdUEInput;
import com.ibm.commerce.payment.ue.entities.ProcessPunchoutResponseCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.SessionCleanCmdUEInput;
import com.ibm.commerce.payment.ue.entities.SessionCleanCmdUEOutput;
import com.ibm.commerce.ue.rest.BaseResource;
import com.ibm.commerce.ue.rest.MessageKey;

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

@Path("/payment/punchout/sample")
@SwaggerDefinition(
        tags = {
                @Tag(name = "payment punchout", description = "API Extensions for payment customization.")
        }
)
@Api(value = "/payment/punchout/sample", tags = "payment punchout")
public class PaymentPunchoutSampleResource extends BaseResource {
	
	private static final Logger LOGGER = Logger.getLogger(PaymentPunchoutSampleResource.class.getName());
	private static final String CLASS_NAME = PaymentPunchoutSampleResource.class.getName();
	
	@Context HttpServletRequest request;
	private static final String requestId = "X-Request-ID";
	private static final String responseId = "X-Response-ID";
	
	private static Locale locale = new Locale(
			MessageKey.getConfig("_CONFIG_LANGUAGE"),
			MessageKey.getConfig("_CONFIG_LOCALE"));
	
	private static final String RESULT_DONE = MessageKey.getConfig("_PSP_STRING_DONE");
	private static final String CONFIRMATION_SUCCESS = MessageKey.getConfig("_PSP_STRING_YES");
	
	// final confirmation request parameters to SimplePunchout
	private static final String FINISH_SUCCESS = MessageKey.getConfig("_PSP_STRING_DONE");
	
	// callback parameters from SimplePunchout
	private static final String RESULT = MessageKey.getConfig("_PSP_STRING_RESULT");
	
	/** The value standing for payment is done successfully. */
	public static final String PAYMENT_SUCCESSFUL = MessageKey.getConfig("_PSP_STRING_SUCCESSFUL");

	/** The value standing for payment is failed. */
	public static final String PAYMENT_FAILED = MessageKey.getConfig("_PSP_STRING_FAILED");

	/** The value standing for payment is invalid. */
	public static final String PAYMENT_INVALID = MessageKey.getConfig("_PSP_STRING_INVALID");
	
	/** The name of transaction result of punch-out payment. It was used as key. */
	public static final String PUNCHOUT_TRAN_RESULT = MessageKey.getConfig("_PSP_STRING_PUNCHOUTTRANRESULT");

	/** The name of callback response of punch-out payment. It was used as key. */
	public static final String PUNCHOUT_CALLBACK_RESPONSE = MessageKey.getConfig("_PSP_STRING_PUNCHOUTCALLBACKRESPONSE");

	/** The response code when the operation is successful. */
	public static final String RESPONSECODE_SUCCESS = "0";

	/** The reason code when the operation is successful. */
	public static final String REASONCODE_NONE = "0";
	
	/** The response code when the opreation failed. */
	public static final String RESPONSECODE_FAIL = "1";
	
	/**
	 * The state of the financial transaction to allow the plugin has chance to control the state transition. 
	 * Just before Plugin Controller passes the current financial transaction to plugins, it set 
	 * the transaction state to TRANSACTION_STATE_NEW. If the plugin wants to control the state transition 
	 * of the transaction, the plugin can set any other valid state: TRANSACTION_STATE_PENDING, TRANSACTION_STATE_SUCCESS, TRANSACTION_STATE_FAILED
	 * or TRANSACTION_STATE_CANCELED. 
	 */
	public static final short TRANSACTION_STATE_NEW = 0;

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

	
	/** The name of popup URL of punch-out payment. It was used as key. */
	public static final String PUNCHOUT_POPUP_URL = "punchoutPopupURL";

	/** The name of payment method of punch-out payment. It was used as key. */
	public static final String PUNCHOUT_PAYMENT_METHOD = "punchoutPaymentMethod";
	
	/**
	 * Default constructor.
	 */
	public PaymentPunchoutSampleResource() {
	}
	

	@POST
	@Path("/get_punchout_url")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for GetPunchoutURLCmd task command.",
			notes = "GetPunchoutURLCmd is a task command which is used to compose the payment punchout url.", 
	response = GetPunchoutURLCmdUEOutput.class)
    @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Application error"),
      @ApiResponse(code = 500, message = "System error") })
	public Response getPunchoutURL(@ApiParam(name="GetPunchoutURLCmdUEInput", value = "GetPunchoutURLCmd UE Input Parameter", required = true)GetPunchoutURLCmdUEInput ueRequest) {
	
		final String METHOD_NAME = "getPunchoutURL()";

		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}

		LOGGER.info("Hi, this is from get_punchout_url remote User Exit implementation!");
		
		PaymentInstruction paymentInstruction = ueRequest.getPaymentInstruction();
		
		GetPunchoutURLCmdUEOutput ueResponse = new GetPunchoutURLCmdUEOutput();
		String punchURL = MessageKey.getConfig("_PSP_PUNCHOUT_URL");
		int start = punchURL.indexOf("price=");
		int end = punchURL.indexOf("&callback_url");
		String substring1 = punchURL.substring(start, end);
				
		start = punchURL.indexOf("tran_id=");
		String substring2 = punchURL.substring(start);
		
		punchURL = punchURL.replace(substring1, "price=" + paymentInstruction.getAmount().toString());
		punchURL = punchURL.replace(substring2, "tran_id=" + paymentInstruction.getId());

		ueResponse.setPunchoutURL(punchURL);
		
		Map extData = paymentInstruction.getExtendedData();
		extData.put("testdata", "punchoutURL_testing");
		ueResponse.setPaymentInstruction(paymentInstruction);
		LOGGER.info("punchoutURL is " + punchURL);

		Response response = Response.ok(ueResponse).build();
		
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		return response;

	}
 
	@POST
	@Path("/process_punchout_response")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for ProcessPunchoutResponseCmd task command.", 
			notes = "ProcessPunchoutResponseCmd is a task command which is used to process the provider response data.", 
	response = ProcessPunchoutResponseCmdUEOutput.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Application error"),
			@ApiResponse(code = 500, message = "System error")
	})
	
	public Response processPunchoutResponse(@ApiParam(name="ProcessPunchoutResponseCmdUEInput",value = "ProcessPunchoutResponseCmd UE Input Parameter",
			required = true)ProcessPunchoutResponseCmdUEInput ueRequest) {

		final String METHOD_NAME = "processPunchoutResponse()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		
		PaymentInstruction paymentInstruction = ueRequest.getPaymentInstruction();

		LOGGER.info("Hi, this is from process_punchout_response remote User Exit implementation!");
	
		ProcessPunchoutResponseCmdUEOutput ueResponse = new ProcessPunchoutResponseCmdUEOutput();
		Map responseParams = new HashMap();
		String externalResult = (String) ueRequest.getCallBackParams().get(RESULT);
		String result = null;
		if (!RESULT_DONE.equalsIgnoreCase(externalResult)) {
			result = PAYMENT_INVALID;
		} else {
			String confirmResult = "yes";
			if (CONFIRMATION_SUCCESS.equalsIgnoreCase(confirmResult)) {
				result =PAYMENT_SUCCESSFUL;
			} else {
				result = PAYMENT_INVALID;
			}
		}
		responseParams.put(PUNCHOUT_TRAN_RESULT, result);
		responseParams.put(PUNCHOUT_CALLBACK_RESPONSE, FINISH_SUCCESS);
		ueResponse.setResponseParams(responseParams);
		
		Map extData = paymentInstruction.getExtendedData();
		extData.put(MessageKey.getConfig("_PSP_EXTENDED_DATA_TRANSACTION_ID"), paymentInstruction.getId());
		LOGGER.info("transactionId is " + extData.get("transactionId"));
		ueResponse.setPaymentInstruction(paymentInstruction);
		
		Response response = Response.ok(ueResponse).build();
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		return response;

	}
	
	@POST
	@Path("/approve_and_deposit_payment")
	@Consumes({ MediaType.TEXT_PLAIN })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for PaymentApproveAndDepositCmd task command.",
			notes = "PaymentApproveAndDepositCmd is a task command which is used to authorize the payment.", 
	response = PaymentApproveAndDepositCmdUEOutput.class)
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response approveAndDepositPayment(String ueRequestString) {

		final String METHOD_NAME = "approveAndDepositPayment()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}

		ObjectMapper mapper = UserExitUtil.getMapper();
		PaymentApproveAndDepositCmdUEInput ueRequest;
		try {
			ueRequest = mapper.readValue(ueRequestString, PaymentApproveAndDepositCmdUEInput.class);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e
					+ " "
					+ Status.INTERNAL_SERVER_ERROR.getStatusCode()
					+ " "
					+ MessageKey.getMessage(locale,
							"_ERR_INVALID_PARAMETER_VALUE"));
		}
		
		FinancialTransaction financialTransaction = ueRequest.getFinancialTransaction();
		PaymentInstruction paymentInstruction = ueRequest.getFinancialTransaction().getPayment().getPaymentInstruction();
		
		LOGGER.info("This is approveAndDepositPayment remote User Exit implementation.");
		LOGGER.info("The amount to be authorize is " + paymentInstruction.getAmount());
		PaymentApproveAndDepositCmdUEOutput ueResponse = new PaymentApproveAndDepositCmdUEOutput();
		Map extData = paymentInstruction.getExtendedData();
		extData.put("payment_rule", "ApproveAndDepositPayment");
		financialTransaction.setState(TRANSACTION_STATE_SUCCESS);
		ueResponse.setFinancialTransaction(financialTransaction);
		
		Response response = Response.ok(ueResponse).build();
		
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		return response;

	}
	
    @POST
	@Path("/approve_payment")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for PaymentApproveCmd task command.",
			notes = "PaymentApproveCmd is a task command which is used to authorize the payment.", 
	response = PaymentApproveCmdUEOutput.class)
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response approvePayment(@ApiParam(name="PaymentApproveCmdUEInput",value = "PaymentApproveCmd UE Input Parameter",
			required = true)PaymentApproveCmdUEInput ueRequest) {

		final String METHOD_NAME = "approvePayment()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}

		FinancialTransaction financialTransaction = ueRequest.getFinancialTransaction();
		PaymentInstruction paymentInstruction = ueRequest.getFinancialTransaction().getPayment().getPaymentInstruction();
		
		LOGGER.info("This is approvePayment remote User Exit implementation.");
		LOGGER.info("The amount to be authorize is " + paymentInstruction.getAmount());
		
		PaymentApproveCmdUEOutput ueResponse = new PaymentApproveCmdUEOutput();
		Map extData = paymentInstruction.getExtendedData();
		Map tranExtData = financialTransaction.getExtendedData();
		
		extData.put("testdata1", "datatotestinApprove-PI");
		tranExtData.put("testdata2", "datatotestinApprove-Transaction");
		
		financialTransaction.setResponseCode(RESPONSECODE_SUCCESS);
		financialTransaction.setState(TRANSACTION_STATE_SUCCESS);
		financialTransaction.setTrackingId("1");
		
		ueResponse.setFinancialTransaction(financialTransaction);
		Response response = Response.ok(ueResponse).build();
		
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		return response;

	}

    /**
     * this function to test PaymentApproveCmd UE set pending state of financialTransaction back to WC
     * , then WC will update payment to pending state, it's required for getPunchOutURL UE	 
     * @param ueRequest
     * @return
     */    
    
    @POST
	@Path("/approve_payment_pending")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for PaymentApproveCmd task command with pending state.",
			notes = "PaymentApproveCmd is a task command which is used to authorize the payment.", 
	response = PaymentApproveCmdUEOutput.class)
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response approvePaymentPending(@ApiParam(name="PaymentApproveCmdUEInput",value = "PaymentApproveCmd UE Input Parameter",
			required = true)PaymentApproveCmdUEInput ueRequest) {

		final String METHOD_NAME = "approvePayment()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}

		FinancialTransaction financialTransaction = ueRequest.getFinancialTransaction();
		PaymentInstruction paymentInstruction = ueRequest.getFinancialTransaction().getPayment().getPaymentInstruction();
		
		LOGGER.info("This is approvePaymentPending remote User Exit implementation.");
		LOGGER.info("The amount to be authorize is " + paymentInstruction.getAmount());
		
		PaymentApproveCmdUEOutput ueResponse = new PaymentApproveCmdUEOutput();
		Map extData = paymentInstruction.getExtendedData();
		Map tranExtData = financialTransaction.getExtendedData();
		
		extData.put("testdata1", "datatotestinApprove-PI");
		tranExtData.put("testdata2", "datatotestinApprove-Transaction");
		
		financialTransaction.setResponseCode(RESPONSECODE_SUCCESS);
		financialTransaction.setReasonCode(REASONCODE_NONE);
		financialTransaction.setState(TRANSACTION_STATE_PENDING);
		financialTransaction.setTrackingId("1");
		
		ueResponse.setFinancialTransaction(financialTransaction);
		Response response = Response.ok(ueResponse).build();
		
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		return response;

	}
	
	@POST
	@Path("/deposit_payment")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for PaymentDepositCmd task command.",
			notes = "PaymentDepositCmd is a task command which is used to authorize the payment.", 
	response = PaymentDepositCmdUEOutput.class)
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response depositPayment(@ApiParam(name="PaymentDepositCmdUEInput",value = "PaymentDepositCmd UE Input Parameter",
			required = true)PaymentDepositCmdUEInput ueRequest) {

		final String METHOD_NAME = "depositPayment()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}		
	
		FinancialTransaction financialTransaction = ueRequest.getFinancialTransaction();
		PaymentInstruction paymentInstruction = ueRequest.getFinancialTransaction().getPayment().getPaymentInstruction();		

		LOGGER.info("This is depositPayment remote User Exit implementation.");
		LOGGER.info("The amount to be authorize is " + paymentInstruction.getAmount());
		PaymentDepositCmdUEOutput ueResponse = new PaymentDepositCmdUEOutput();
		Map extData = paymentInstruction.getExtendedData();
		extData.put("deposit_testdata", "datatotestindepositPayment");
		ueResponse.setFinancialTransaction(financialTransaction);
		Response response = Response.ok(ueResponse).build();
		
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		return response;

	}
	
		
	@POST
	@Path("/reverse_approval")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for PaymentReverseApprovalCmd task command.",
			notes = "PaymentReverseApprovalCmd is a task command which is used to reverse authorize the payment.", 
	response = PaymentReverseApprovalCmdUEOutput.class)
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response reverseApproval(@ApiParam(name="PaymentReverseApprovalCmdUEInput",value = "PaymentReverseApprovalCmd UE Input Parameter",
			required = true)PaymentReverseApprovalCmdUEInput ueRequest) {

		final String METHOD_NAME = "reverseApproval()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		
		FinancialTransaction financialTransaction = ueRequest.getFinancialTransaction();
		PaymentInstruction paymentInstruction = ueRequest.getFinancialTransaction().getPayment().getPaymentInstruction();		

		LOGGER.info("This is reverseApproval remote User Exit implementation.");
		LOGGER.info("The amount to be authorize is " + paymentInstruction.getAmount());
		PaymentReverseApprovalCmdUEOutput ueResponse = new PaymentReverseApprovalCmdUEOutput();
		Map extData = paymentInstruction.getExtendedData();
		extData.put("reverse_approval", "datatotestinreverseApproval");
		ueResponse.setFinancialTransaction(financialTransaction);
		Response response = Response.ok(ueResponse).build();
		
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		return response;

	}
	
	@POST
	@Path("/reverse_deposit")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for PaymentReverseDepositCmd task command.",
			notes = "PaymentReverseDepositCmd is a task command which is used to reverse authorize the payment.", 
	response = PaymentReverseDepositCmdUEOutput.class)
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response reverseDeposit(@ApiParam(name="PaymentReverseDepositCmdUEInput",value = "PaymentReverseDepositCmd UE Input Parameter",
			required = true)PaymentReverseDepositCmdUEInput ueRequest) {

		final String METHOD_NAME = "reverseDeposit()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		
		FinancialTransaction financialTransaction = ueRequest.getFinancialTransaction();
		PaymentInstruction paymentInstruction = ueRequest.getFinancialTransaction().getPayment().getPaymentInstruction();		

		LOGGER.severe("This is reverseDeposit remote User Exit implementation.");
		LOGGER.info("The amount to be authorize is " + paymentInstruction.getAmount());
		PaymentReverseDepositCmdUEOutput ueResponse = new PaymentReverseDepositCmdUEOutput();
		Map extData = paymentInstruction.getExtendedData();
		extData.put("reverse_deposit", "datatotestinreverseDeposit");
		ueResponse.setFinancialTransaction(financialTransaction);
		Response response = Response.ok(ueResponse).build();
		
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		return response;

	}
		
	@POST
	@Path("/credit_payment")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for PaymentCreditCmd task command.",
			notes = "PaymentCreditCmd is a task command which is used to credit the payment.", 
	response = PaymentCreditCmdUEOutput.class)
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response creditPayment(@ApiParam(name="PaymentCreditCmdUEInput",value = "PaymentCreditCmd UE Input Parameter",
			required = true)PaymentCreditCmdUEInput ueRequest) {

		final String METHOD_NAME = "creditPayment()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		
		FinancialTransaction financialTransaction = ueRequest.getFinancialTransaction();
		PaymentInstruction paymentInstruction = ueRequest.getFinancialTransaction().getCredit().getPaymentInstruction();
		

		LOGGER.info("This is creditPayment remote User Exit implementation.");
		LOGGER.info("The amount to be authorize is " + paymentInstruction.getAmount());
		PaymentCreditCmdUEOutput ueResponse = new PaymentCreditCmdUEOutput();
		Map extData = paymentInstruction.getExtendedData();
		extData.put("credit_test", "datatotestincreditPayment");
		ueResponse.setFinancialTransaction(financialTransaction);
		Response response = Response.ok(ueResponse).build();
		
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		return response;

	}

	@POST
	@Path("/clean_session")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for SessionCleanCmd task command.",
			notes = "SessionCleanCmd is a task command which is used to clean the pending and expired payments.", 
	response = SessionCleanCmdUEOutput.class)
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response cleanSession(@ApiParam(name="SessionCleanCmdUEInput",value = "SessionCleanCmd UE Input Parameter",
			required = true)SessionCleanCmdUEInput ueRequest) {

		final String METHOD_NAME = "cleanSession()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		
		LOGGER.info("this is from cleanSession remote User Exit implementation!");
		
		List<PaymentInstruction> paymentInstructions = ueRequest.getPaymentInstructions();		

		LOGGER.info("The amount to be clean is " + paymentInstructions.get(0).getAmount());
		SessionCleanCmdUEOutput ueResponse = new SessionCleanCmdUEOutput();
		for(int i=0; i<paymentInstructions.size(); i++)
		{
			LOGGER.severe("The amount to be clean is " + paymentInstructions.get(i).getAmount());
			Map extData = paymentInstructions.get(i).getExtendedData();
			extData.put("property1", "datatotestincleanSession");
			extData.put("action", "reverse");			
		}
		
		ueResponse.setPaymentInstructions(paymentInstructions);			
		Response response = Response.ok(ueResponse).build();
		
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		return response;

	}		
	
	/**
     * this function to test SessionCleanCmd UE notify WC to do nothing for expired payment
     * , then WC will not take any action for expired payment	 
     * @param ueRequest
     * @return
     */ 
	
	@POST
	@Path("/clean_session_donothing")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "This is the UE api for SessionCleanCmd task command with do nothing action.",
			notes = "SessionCleanCmd is a task command which is used to clean the pending and expired payments.", 
	response = SessionCleanCmdUEOutput.class)
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response cleanSessionDoNothing(@ApiParam(name="SessionCleanCmdUEInput",value = "SessionCleanCmd UE Input Parameter",
			required = true)SessionCleanCmdUEInput ueRequest) {

		final String METHOD_NAME = "cleanSession()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		
		LOGGER.info("this is from cleanSession remote User Exit implementation!");
		
		List<PaymentInstruction> paymentInstructions = ueRequest.getPaymentInstructions();		

		LOGGER.info("The amount to be clean is " + paymentInstructions.get(0).getAmount());
		SessionCleanCmdUEOutput ueResponse = new SessionCleanCmdUEOutput();
		for(int i=0; i<paymentInstructions.size(); i++)
		{
			LOGGER.severe("The amount to be clean is " + paymentInstructions.get(i).getAmount());
			Map extData = paymentInstructions.get(i).getExtendedData();
			extData.put("property1", "datatotestincleanSession");
			extData.put("action", "donothing");			
			
		}
		
		ueResponse.setPaymentInstructions(paymentInstructions);
		
		
		
		Response response = Response.ok(ueResponse).build();
		
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		return response;

	}	
	
	@POST
	@Path("/payment_compensate_transaction")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON }) 
	public Response compensateForPaymentTransanction(CompensateForTxRollbackCmdUEInput ueRequest) {

		final String METHOD_NAME = "compensateForPaymentAppoveTransanction()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		System.out.println("This is a UE implementation for compensateForPaymentAppoveTransanction");
		CompensateForTxRollbackCmdUEOutput ueResponse = new CompensateForTxRollbackCmdUEOutput();
		List<UEInvocation> list = ueRequest.getUEInvocations();
		if (list != null && !list.isEmpty()){
		
			for(UEInvocation uEInvocation:list){
				uEInvocation.setCompensationResult("succeed");	
				System.out.print("WC call UE request Id: " + uEInvocation.getRequestId());
				System.out.print("WC call UE response Id: " + uEInvocation.getResponseId());
				System.out.print("WC call UE Name : " + uEInvocation.getUEName());
			}
		}
		
		ueResponse.setUEInvocations(list);
		Response response = Response.ok(ueResponse).build();
		
		return response;

	}
	
	
}