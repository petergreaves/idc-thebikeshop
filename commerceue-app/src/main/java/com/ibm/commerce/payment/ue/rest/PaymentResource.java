package com.ibm.commerce.payment.ue.rest;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.commerce.payment.entities.PaymentInstruction;
import com.ibm.commerce.payment.samples.tokenization.utils.TokenManagement;
import com.ibm.commerce.payment.ue.entities.CreatePaymentTokenCmdUEInput;
import com.ibm.commerce.payment.ue.entities.CreatePaymentTokenCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.GetPunchoutURLCmdUEInput;
import com.ibm.commerce.payment.ue.entities.GetPunchoutURLCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.PaymentApproveCmdUEInput;
import com.ibm.commerce.payment.ue.entities.PaymentApproveCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.ProcessPunchoutResponseCmdUEInput;
import com.ibm.commerce.payment.ue.entities.ProcessPunchoutResponseCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.RemovePaymentTokenCmdUEInput;
import com.ibm.commerce.payment.ue.entities.SessionCleanCmdUEInput;
import com.ibm.commerce.payment.ue.entities.SessionCleanCmdUEOutput;
import com.ibm.commerce.payment.ue.entities.UpdatePaymentTokenCmdUEInput;
import com.ibm.commerce.payment.ue.entities.VerifyPaymentAccountCmdUEInput;
import com.ibm.commerce.payment.ue.entities.VerifyPaymentAccountCmdUEOutput;
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
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

@Path("/payment")
@SwaggerDefinition(
        tags = {
                @Tag(name = "payment", description = "API Extensions for payment customization.")
        }
)
@Api(value = "/payment", tags = "payment")
public class PaymentResource extends BaseResource {
	
	private static final Logger LOGGER = Logger.getLogger(PaymentResource.class.getName());
	private static final String CLASS_NAME = PaymentResource.class.getName();
	
	private static Locale locale = new Locale(
			MessageKey.getConfig("_CONFIG_LANGUAGE"),
			MessageKey.getConfig("_CONFIG_LOCALE"));
	
	public static final String EXPIRE_YEAR = "expire_year";
	public static final String EXPIRE_MONTH = "expire_month";
	public static final String CVC = "cc_cvc";
	public static final String CITY = "billto_city";
	
	private static final String RESULT_DONE = MessageKey.getConfig("_PSP_STRING_DONE");
	private static final String CONFIRMATION_SUCCESS = MessageKey.getConfig("_PSP_STRING_YES");
	private static final String CONFIRMATION_FAIL = MessageKey.getConfig("_PSP_STRING_NO");
	
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

	
	/** The name of popup URL of punch-out payment. It was used as key. */
	public static final String PUNCHOUT_POPUP_URL = "punchoutPopupURL";

	/** The name of payment method of punch-out payment. It was used as key. */
	public static final String PUNCHOUT_PAYMENT_METHOD = "punchoutPaymentMethod";
	
	/** The name of payment token. */
	public static final String PAYMENT_TOKEN = "payment_token";
	
	/** The account number. */
	public static final String PAYMENT_ACCOUNT = "account";
	
	/**
	 * The payment account verification result
	 */
	public static final String ACCOUNT_VERIFY_RESULT = "verifyResult";
	
	/**
	 * The payment account verification result of success
	 */
	public static final String ACCOUNT_VERIFY_RESULT_SUCCESS = "success";
	/**
	 * The payment account verification result of success
	 */
	public static final String ACCOUNT_VERIFY_RESULT_FAILED = "failed";
	private static final String ERROR_MESSAGE = "errorMessage";
	
	public static final String DISPLAY_VALUE = "display_value";
	public static final String TOKEN_FLAG = "allowRemoveToken";
	
	private TokenManagement tokenMgr;
	
	/**
	 * Default constructor.
	 */
	public PaymentResource() {
		tokenMgr=TokenManagement.getInstance();
	}
	
	/****************************************
	 * Payment Punchout Extension points
	 ****************************************/
	@POST
	@Path("/get_punchout_url")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize action to get payment form URL for a punch-out payment scenario.",
			notes = "| Command: com.ibm.commerce.payment.task.commands.GetPunchoutURLCmd |\n|----------|\n| GetPunchoutURLCmd is a task command which is used to compose the payment punchout url.|", 
	response = GetPunchoutURLCmdUEOutput.class,
	extensions = { 
       @Extension( name = "data-command", properties = {
           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.payment.task.commands.GetPunchoutURLCmd")
           })
    })
    @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Application error"),
      @ApiResponse(code = 500, message = "System error") })
	public Response getPunchoutURL(@ApiParam(name="GetPunchoutURLCmdUEInput", value = "GetPunchoutURLCmd UE Input Parameter", required = true)GetPunchoutURLCmdUEInput ueRequest) {
	
		final String METHOD_NAME = "getPunchoutURL()";

		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		
		//Enter your implementation below
		
		Response response = Response.ok(null).build();
		
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		
		return response;
	}
	
	
	@POST
	@Path("/process_punchout_response")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to process callback parameters from the payment service provider for a punch-out payment scenario.", 
			notes = "| Command: com.ibm.commerce.payment.task.commands.ProcessPunchoutResponseCmd |\n|----------|\n| ProcessPunchoutResponseCmd is a task command which is used to process the provider response data.|", 
	response = ProcessPunchoutResponseCmdUEOutput.class,
	extensions = { 
       @Extension( name = "data-command", properties = {
           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.payment.task.commands.ProcessPunchoutResponseCmd")
           })
    })
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
		
		//Enter your implementation below
				
		Response response = Response.ok(null).build();
		
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		return response;

	}
	
	@POST
	@Path("/approve_payment")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to authorize payment.",
			notes = "| Command: com.ibm.commerce.payment.task.commands.PaymentApproveCmd |\n|----------|\n| PaymentApproveCmd is a task command which is used to authorize the payment.|", 
	response = PaymentApproveCmdUEOutput.class,
	extensions = { 
       @Extension( name = "data-command", properties = {
           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.payment.task.commands.ProcessPunchoutResponseCmd")
           })
    })
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response approvePayment(@ApiParam(name="PaymentApproveCmdUEInput",value = "PaymentApproveCmd UE Input Parameter",
			required = true)PaymentApproveCmdUEInput ueRequest) throws Exception {

		final String METHOD_NAME = "approvePayment()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}

		//Enter your implementation below
		
		Response response = null;
		
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		return response;

	}
	
	@POST
	@Path("/clean_session")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "The API extension for SessionCleanCmd task command.",
			notes = "| Command: com.ibm.commerce.payment.task.commands.SessionCleanCmd |\n|----------|\n| SessionCleanCmd is a task command which is used to clean the pending and expired payments.|", 
	response = SessionCleanCmdUEOutput.class,
	extensions = { 
       @Extension( name = "data-command", properties = {
           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.payment.task.commands.SessionCleanCmd")
           })
    })
	@ApiResponses(value = {
    @ApiResponse(code = 400, message = "Application error"),
    @ApiResponse(code = 500, message = "System error") })

	public Response cleanSession(@ApiParam(name="SessionCleanCmdUEInput",value = "SessionCleanCmd UE Input Parameter",
			required = true)SessionCleanCmdUEInput ueRequest) {

		final String METHOD_NAME = "cleanSession()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		
		
		List<PaymentInstruction> paymentInstructions = ueRequest.getPaymentInstructions();		

		LOGGER.info("The amount to be clean is " + paymentInstructions.get(0).getAmount());
		SessionCleanCmdUEOutput ueResponse = new SessionCleanCmdUEOutput();
		for(int i=0; i<paymentInstructions.size(); i++)
		{
			Map extData = paymentInstructions.get(i).getExtendedData();
			extData.put("testdata", "datatotestincleanSession");
			extData.put("action", "reverse");
			
		}
		
		ueResponse.setPaymentInstructions(paymentInstructions);
		
		Response response = Response.ok(ueResponse).build();
		
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.exiting(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		return response;

	}	
	
	
	/****************************************
	 * Payment Tokenization Extension points
	 ****************************************/
	
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
		
		//Enter your implementation below
		
		Response response = null;
		
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
		
		//Enter your implementation below
		
		Response response = null;
		
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
		
		//Enter your implementation below
		
		Response response = null;

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
		
		//Enter your implementation below
		
		Response response = null;
		
		LOGGER.info("Exiting " + CLASS_NAME + " " + METHOD_NAME);
		return response;

	}	
	
}

