package com.ibm.commerce.order.ue.rest;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.commerce.foundation.entities.ErrorUEResponse;
import com.ibm.commerce.foundation.entities.ExceptionData;
import com.ibm.commerce.foundation.ue.util.UserExitUtil;
import com.ibm.commerce.order.entities.Order;
import com.ibm.commerce.order.entities.OrderItem;
import com.ibm.commerce.order.ue.entities.OrderPostUERequest;
import com.ibm.commerce.order.ue.entities.OrderPostUEResponse;
import com.ibm.commerce.order.ue.entities.OrderPreUERequest;
import com.ibm.commerce.order.ue.entities.OrderPreUEResponse;
import com.ibm.commerce.ue.rest.BaseResource;
import com.ibm.commerce.ue.rest.MessageKey;

@Path("/promotion")
@SwaggerDefinition(tags = { @Tag(name = "promotion", description = "API Extensions for promotion customization.") })
@Api(value = "/promotion", tags = "promotion")
public class PromotionResource extends BaseResource {
	
	@Context HttpServletRequest request;
	
	private static final String requestId = "X-Request-ID";
	private static final String responseId = "X-Response-ID";

	private static final Logger LOGGER = Logger.getLogger(PromotionResource.class.getName());
	private static final String CLASS_NAME = PromotionResource.class.getName();

	private static Locale locale = new Locale(MessageKey.getConfig("_CONFIG_LANGUAGE"),
			MessageKey.getConfig("_CONFIG_LOCALE"));
	
	/**
	 * The pre API extension for PromotionCodeAddRemoveControllerCmd controller
	 * command. This command is used to add or remove a promotion code from an
	 * order.
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/promotion_code_add_remove_pre")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to perform before adding/removing promotion codes to/from the cart.", notes = "| Command: com.ibm.commerce.marketing.commands.PromotionCodeAddRemoveControllerCmd |\n|----------|\n| PromotionCodeAddRemoveControllerCmd is a controller command which is used to add or remove a promotion code from an order.|", response = OrderPreUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.marketing.commands.PromotionCodeAddRemoveControllerCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response promotionCodeAddRemovePre(
			@ApiParam(name = "promotionCodeAddRemovePre UE input", value = "The UE Request String", required = true) OrderPreUERequest ueRequest) {

		Order orderPOJO = ueRequest.getOrder();
		if (orderPOJO != null) {
			List<OrderItem> orderitemPOJOs = orderPOJO.getOrderItem();
			if (orderitemPOJOs != null && !orderitemPOJOs.isEmpty()) {
				for (OrderItem orderitemPOJO : orderitemPOJOs) {
					LOGGER.info("orderItemId:"
							+ orderitemPOJO.getOrderItemIdentifier()
							+ " quantity:" + orderitemPOJO.getQuantity());
				}
			}
		}
		
		Map<String, Object> inputParameters = ueRequest.getCommandInputs();

		OrderPreUEResponse ueResponse = UserExitUtil.createPreUEResponse(orderPOJO, inputParameters);

		Response response = Response.ok(ueResponse).build();
		return response;

	}

	/**
	 * The post API extension for PromotionCodeAddRemoveControllerCmd controller
	 * command. This command is used to add or remove a promotion code from an
	 * order.
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/promotion_code_add_remove_post")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to perform after adding/removing promotion codes to/from the cart.", notes = "| Command: com.ibm.commerce.marketing.commands.PromotionCodeAddRemoveControllerCmd |\n|----------|\n| PromotionCodeAddRemoveControllerCmd is a controller command which is used to add or remove a promotion code from an order.|", response = OrderPreUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.marketing.commands.PromotionCodeAddRemoveControllerCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response promotionCodeAddRemovePost(
			@ApiParam(name = "promotionCodeAddRemovePost UE input", value = "The UE Request String", required = true) OrderPostUERequest ueRequest) {

		Order orderPOJO = ueRequest.getOrder();
		if (orderPOJO != null) {
			List<OrderItem> orderitemPOJOs = orderPOJO.getOrderItem();
			if (orderitemPOJOs != null && !orderitemPOJOs.isEmpty()) {
				for (OrderItem orderitemPOJO : orderitemPOJOs) {
					LOGGER.info("orderItemId:"
							+ orderitemPOJO.getOrderItemIdentifier()
							+ " quantity:" + orderitemPOJO.getQuantity());
				}
			}
		}
		
		Map<String, Object> inputParameters = ueRequest.getCommandInputs();

		OrderPostUEResponse ueResponse = UserExitUtil.createPostUEResponse(orderPOJO, inputParameters);

		Response response = Response.ok(ueResponse).build();
		return response;

	}

	/**
	 * Process the promotion code. pass the real promotion code as input to
	 * PromotionCodeAddRemoveControllerCmdImpl when validation success,
	 * otherwise, throw exception.
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/process_promotion_code")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "The process_promotion_code command processes the promotion code - return the real promotion code, or throw exception.", notes = "| Command: com.ibm.commerce.marketing.commands.PromotionCodeAddRemoveControllerCmd |\n|----------|\n| PromotionCodeAddRemoveControllerCmd is a controller command which is used to add or remove a promotion code from an order.|", response = OrderPreUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.marketing.commands.PromotionCodeAddRemoveControllerCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response processPromotionCode(
			@ApiParam(name = "processPromotionCode UE input", value = "The UE Request String", required = true) OrderPreUERequest ueRequest) {

		final String METHOD_NAME = "processPromotionCode()";
		Order orderPOJO = ueRequest.getOrder();
		Map<String, Object> inputParameters = ueRequest.getCommandInputs();

		// It's ApplyPromotionCode when taskType is A, RemovePromotionCode for R
		String taskType = (String) inputParameters.get("taskType");
		// Get the associate/employee ID.
		String inputPromotionCode = (String) inputParameters.get("promotionCode");
		String storeId = (String) inputParameters.get("storeId");

		if (inputPromotionCode != null && !inputPromotionCode.isEmpty() && storeId != null
				&& !storeId.isEmpty() && "A".equals(taskType)) {

			if (isValidCode(storeId, inputPromotionCode)) {
				LOGGER.info("Code validation success, will return the real promotion code.");

				// Hard code the real promotion code here, 20PCTOFF is a public
				// promotion code created via promotion tool.
				String newPromotionCode = "20PCTOFF";
				inputParameters.put("promotionCode", newPromotionCode);
			} else {
				LOGGER.info("Code validation fail for the entered promotion code "
						+ inputPromotionCode + " under store " + storeId
						+ ", will throw exception.");

				ErrorUEResponse ueResponse = new ErrorUEResponse();
				List<ExceptionData> errors = new ArrayList<ExceptionData>();
				ExceptionData error = new ExceptionData();
				error.setCode("400");
				error.setMessageKey("_ERR_INVALID_PARAMETER_VALUE");
				error.setMessage(MessageKey.getMessage(locale,
						"_ERR_INVALID_PARAMETER_VALUE", new Object[] {
								"promotionCode", inputPromotionCode }));
				errors.add(error);
				ueResponse.setErrors(errors);
				Response response = Response.status(400)
						.type(MediaType.APPLICATION_JSON).entity(ueResponse)
						.build();

				LOGGER.exiting(CLASS_NAME, METHOD_NAME, response);
				return response;
			}

		}

		OrderPreUEResponse ueResponse = UserExitUtil.createPreUEResponse(orderPOJO, inputParameters);

		Response response = Response.ok(ueResponse).build();
		return response;

	}
	
	private boolean isValidCode(String storeId, String inputPromotionCode) {
		// Hard code Store Associate information here
		Map<String, Set> storeIdToEmployeeIdMap = new HashMap<String, Set>();
		String AuroraESiteId = "10201";
		Set<String> employeeIdSet = new HashSet<String>();
		employeeIdSet.add("-1000");
		storeIdToEmployeeIdMap.put(AuroraESiteId, employeeIdSet);

		// Validation
		if (storeIdToEmployeeIdMap.get(storeId) != null
				&& storeIdToEmployeeIdMap.get(storeId).contains(inputPromotionCode)) {
			return true;
		} else {
			return false;
		}
	}
}
