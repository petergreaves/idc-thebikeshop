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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.commerce.foundation.common.entities.CatalogEntryIdentifier;
import com.ibm.commerce.foundation.common.entities.MonetaryAmount;
import com.ibm.commerce.foundation.common.entities.OrderItemCharges;
import com.ibm.commerce.foundation.common.entities.PartNumberIdentifier;
import com.ibm.commerce.foundation.common.entities.Quantity;
import com.ibm.commerce.foundation.entities.ErrorUEResponse;
import com.ibm.commerce.foundation.entities.ExceptionData;
import com.ibm.commerce.foundation.entities.TaskCmdUEInput;
import com.ibm.commerce.foundation.ue.compensation.entities.CompensateForTxRollbackCmdUEInput;
import com.ibm.commerce.foundation.ue.compensation.entities.CompensateForTxRollbackCmdUEOutput;
import com.ibm.commerce.foundation.ue.compensation.entities.UEInvocation;
import com.ibm.commerce.foundation.ue.util.UserExitUtil;
import com.ibm.commerce.order.entities.Order;
import com.ibm.commerce.order.entities.OrderItem;
import com.ibm.commerce.order.ue.entities.ExtOrderProcessCmdUEInput;
import com.ibm.commerce.order.ue.entities.ExtOrderProcessCmdUEOutput;
import com.ibm.commerce.order.ue.entities.OrderMessagingCmdUEInput;
import com.ibm.commerce.order.ue.entities.OrderMessagingCmdUEOutput;
import com.ibm.commerce.order.ue.entities.OrderNotifyCmdUEInput;
import com.ibm.commerce.order.ue.entities.OrderNotifyCmdUEOutput;
import com.ibm.commerce.order.ue.entities.OrderPostUERequest;
import com.ibm.commerce.order.ue.entities.OrderPostUEResponse;
import com.ibm.commerce.order.ue.entities.OrderPreUERequest;
import com.ibm.commerce.order.ue.entities.OrderPreUEResponse;
import com.ibm.commerce.order.ue.entities.PreProcessOrderCmdUEInput;
import com.ibm.commerce.order.ue.entities.PreProcessOrderCmdUEOutput;
import com.ibm.commerce.order.ue.entities.ProcessOrderCmdUEInput;
import com.ibm.commerce.order.ue.entities.ProcessOrderCmdUEOutput;
import com.ibm.commerce.order.ue.entities.ProcessOrderSubmitEventCmdUEInput;
import com.ibm.commerce.order.ue.entities.ProcessOrderSubmitEventCmdUEOutput;
import com.ibm.commerce.order.ue.entities.TaxIntegrationCustomCmdUEInput;
import com.ibm.commerce.order.ue.entities.TaxIntegrationCustomCmdUEOutput;
import com.ibm.commerce.order.ue.entities.TaxIntegrationCustomOrderItem;
import com.ibm.commerce.order.ue.entities.UpdateShippingAddressCmdUEInput;
import com.ibm.commerce.order.ue.entities.UpdateShippingAddressCmdUEOutput;
import com.ibm.commerce.order.ue.entities.UpdateShippingChargesCmdUEInput;
import com.ibm.commerce.order.ue.entities.UpdateShippingChargesCmdUEOutput;
import com.ibm.commerce.ue.rest.BaseResource;
import com.ibm.commerce.ue.rest.MessageKey;

@Path("/order")
@SwaggerDefinition(tags = { @Tag(name = "order", description = "API Extensions for order customization.") })
@Api(value = "/order", tags = "order")
public class OrderResource extends BaseResource {
	
	@Context HttpServletRequest request;
	
	private static final String requestId = "X-Request-ID";
	private static final String responseId = "X-Response-ID";
	
	private static final MathContext DECIMAL2 = new MathContext(2);

	private static final Logger LOGGER = Logger.getLogger(OrderResource.class.getName());
	private static final String CLASS_NAME = OrderResource.class.getName();

	private static Locale locale = new Locale(MessageKey.getConfig("_CONFIG_LANGUAGE"),
			MessageKey.getConfig("_CONFIG_LOCALE"));

	/**
	 * The pre API extension for orderItemAddCmd controller command. This
	 * command is used to add items to one or a list of orders.
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/order_item_add_pre")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to perform before adding item to cart.", notes = "| Command: com.ibm.commerce.orderitems.commands.OrderItemAddCmd |\n|----------|\n| OrderItemAddCmd is a controller command which is used to add items to one or a list of orders.|", response = OrderPreUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.orderitems.commands.OrderItemAddCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response orderItemAddPre(
			@ApiParam(name = "orderItemAddPre UE input", value = "The UE Request String", required = true) OrderPreUERequest ueRequest) {

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
	 * The post API extension for orderItemAddCmd controller command. This
	 * command is used to add items to one or a list of orders.
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/order_item_add_post")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to perform after adding item to cart.", notes = "| Command: com.ibm.commerce.orderitems.commands.OrderItemAddCmd |\n|----------|\n| OrderItemAddCmd is a controller command which is used to add items to one or a list of orders.|", response = OrderPostUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.orderitems.commands.OrderItemAddCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response orderItemAddPost(
			@ApiParam(name = "orderItemAddPost UE input", value = "The UE Request String", required = true) OrderPostUERequest ueRequest) {

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
	 * The pre API extension for orderItemUpdateCmd controller command. This
	 * command is used to Updates one or more order items in the shopping cart.
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/order_item_update_pre")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to perform before updating items that are in the cart.", notes = "| Command: com.ibm.commerce.orderitems.commands.OrderItemUpdateCmd |\n|----------|\n| orderItemUpdateCmd is a controller command which is used to Updates one or more order items in the shopping cart.|", response = OrderPreUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.orderitems.commands.OrderItemUpdateCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response orderItemUpdatePre(
			@ApiParam(name = "orderItemUpdateCmdPre UE input", value = "The UE Request String", required = true) OrderPreUERequest ueRequest) {

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
	 * The post API extension for orderItemUpdateCmd controller command. This
	 * command is used to Updates one or more order items in the shopping cart.
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/order_item_update_post")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to perform after updating items that are in the cart.", notes = "| Command: com.ibm.commerce.orderitems.commands.OrderItemUpdateCmd |\n|----------|\n| orderItemUpdateCmd is a controller command which is used to Updates one or more order items in the shopping cart.|", response = OrderPostUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.orderitems.commands.OrderItemUpdateCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response orderItemUpdatePost(
			@ApiParam(name = "orderItemUpdateCmdPost UE input", value = "The UE Request String", required = true) OrderPostUERequest ueRequest) {

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
	 * The pre API extension for OrderProcessCmd controller command.
	 * OrderProcessCmd is a controller command which is used to submit an order.
	 * The order must have been locked by OrderPrepare. Once the OrderProcess
	 * command begins running, the order cannot be cancelled with OrderCancel.
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/order_process_pre")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to perform before processing the order.", notes = "| Command: com.ibm.commerce.order.commands.OrderProcessCmd |\n|----------|\n| OrderProcessCmd is a controller command which is used to submit an order. The order must have been locked by OrderPrepare. Once the  OrderProcess command begins running, the order cannot be cancelled with OrderCancel.|", response = OrderPreUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.OrderProcessCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response OrderProcessPre(
			@ApiParam(name = "OrderProcessCmdPre UE input", value = "The UE Request String", required = true) OrderPreUERequest ueRequest) {

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
	 * The post API extension for OrderProcessCmd controller command. This
	 * command is used to Updates one or more order items in the shopping cart.
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/order_process_post")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to perform after processing the order.", notes = "| Command: com.ibm.commerce.order.commands.OrderProcessCmd |\n|----------|\n| OrderProcessCmd is a controller command which is used to submit an order. The order must have been locked by OrderPrepare. Once the  OrderProcess command begins running, the order cannot be cancelled with OrderCancel.|", response = OrderPostUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.OrderProcessCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response OrderProcessPost(
			@ApiParam(name = "OrderProcessCmdPost UE input", value = "The UE Request String", required = true) OrderPostUERequest ueRequest) {

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
	 * The pre API extension for OrderPrepareCmd controller command.
	 * OrderPrepareCmd is a controller command which is used to prepare an order
	 * by determining prices, discounts, shipping charges, shipping adjustment,
	 * and taxes for an order. If an order reference number is not specified,
	 * all current pending orders will be prepared for the current customer at
	 * the given store.
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/order_prepare_pre")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to perform before preparing the cart for checkout.", notes = "| Command: com.ibm.commerce.order.commands.OrderPrepareCmd |\n|----------|\n| OrderPrepareCmd is a controller command which is used to prepare an order by determining prices, discounts, shipping charges, shipping adjustment,and taxes for an order. If an order reference number is not specified,all current pending orders will be prepared for the current customer at the given store.|", response = OrderPreUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.OrderPrepareCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response OrderPreparePre(
			@ApiParam(name = "OrderPrepareCmdPre UE input", value = "The UE Request String", required = true) OrderPreUERequest ueRequest) {

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
	 * The post API extension for OrderPrepareCmd controller command.
	 * OrderPrepareCmd is a controller command which is used to prepare an order
	 * by determining prices, discounts, shipping charges, shipping adjustment,
	 * and taxes for an order. If an order reference number is not specified,
	 * all current pending orders will be prepared for the current customer at
	 * the given store.
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/order_prepare_post")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to perform after preparing the cart for checkout.", notes = "| Command: com.ibm.commerce.order.commands.OrderPrepareCmd |\n|----------|\n| OrderPrepareCmd is a controller command which is used to prepare an order by determining prices, discounts, shipping charges, shipping adjustment,and taxes for an order. If an order reference number is not specified,all current pending orders will be prepared for the current customer at the given store.|", response = OrderPostUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.OrderPrepareCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response OrderPreparePost(
			@ApiParam(name = "OrderPrepareCmdPre UE input", value = "The UE Request String", required = true) OrderPostUERequest ueRequest) {

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
	 * The pre API extension for OrderCalculateCmd controller command.
	 * OrderCalculateCmd is a controller command which is used to re-calculate
	 * specified calculation usages. <br>
	 * This command is called by
	 * {@link com.ibm.commerce.orderitems.commands.OrderItemBaseCmdImpl} and
	 * {@link com.ibm.commerce.orderitems.commands.OrderItemDeleteCmdImpl} to
	 * refresh the order price, charges and freebie items after Add, Update and
	 * Delete order item.<br>
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/order_calculate_pre")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to perform before calculating cart total.", notes = "| Command: com.ibm.commerce.order.commands.OrderCalculateCmd |\n|----------|\n| OrderCalculateCmd is a controller command which is used to re-calculate specified calculation usages.<br> This command is called by OrderItemBaseCmdImpl and OrderItemDeleteCmdImpl to refresh the order price, charges and freebie items after Add, Update and Delete order item.<br>|", response = OrderPreUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.OrderCalculateCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response OrderCalculatePre(
			@ApiParam(name = "OrderCalculateCmdPre UE input", value = "The UE Request String", required = true) OrderPreUERequest ueRequest) {

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
	 * The post API extension for OrderCalculateCmd controller command.
	 * OrderCalculateCmd is a controller command which is used to re-calculate
	 * specified calculation usages. <br>
	 * This command is called by
	 * {@link com.ibm.commerce.orderitems.commands.OrderItemBaseCmdImpl} and
	 * {@link com.ibm.commerce.orderitems.commands.OrderItemDeleteCmdImpl} to
	 * refresh the order price, charges and freebie items after Add, Update and
	 * Delete order item.<br>
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/order_calculate_post")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to perform after calculating cart total.", notes = "| Command: com.ibm.commerce.order.commands.OrderCalculateCmd |\n|----------|\n| OrderCalculateCmd is a controller command which is used to re-calculate specified calculation usages.<br> This command is called by OrderItemBaseCmdImpl and OrderItemDeleteCmdImpl to refresh the order price, charges and freebie items after Add, Update and Delete order item.<br>|", response = OrderPostUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.OrderCalculateCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response OrderCalculatePost(
			@ApiParam(name = "OrderCalculateCmdPost UE input", value = "The UE Request String", required = true) OrderPostUERequest ueRequest) {

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
	 * The pre API extension for OrderItemDeleteCmd controller command.
	 * OrderItemDeleteCmd is a controller command which is used to delete some
	 * order items from one or more pending orders for some
	 * <samp>orderitemIds</samp> or product.
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/order_item_delete_pre")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to perform before deleting items from the cart.", notes = "| Command: com.ibm.commerce.orderitems.commands.OrderItemDeleteCmd |\n|----------|\n| OrderItemDeleteCmd is a controller command which is used to delete some order items from one or more pending orders for some orderitemIds or product.|", response = OrderPreUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.orderitems.commands.OrderItemDeleteCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response OrderItemDeletePre(
			@ApiParam(name = "OrderDeleteCmdPre UE input", value = "The UE Request String", required = true) OrderPreUERequest ueRequest) {

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
	 * The post API extension for OrderItemDeleteCmd controller command.
	 * OrderItemDeleteCmd is a controller command which is used to delete some
	 * order items from one or more pending orders for some
	 * <samp>orderitemIds</samp> or product.
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/order_item_delete_post")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Customize actions to perform after deleting items from the cart.", notes = "| Command: com.ibm.commerce.orderitems.commands.OrderItemDeleteCmd |\n|----------|\n| OrderItemDeleteCmd is a controller command which is used to delete some order items from one or more pending orders for some orderitemIds or product.|", response = OrderPostUEResponse.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.orderitems.commands.OrderItemDeleteCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response OrderItemDeletePost(
			@ApiParam(name = "OrderDeleteCmdPoste UE input", value = "The UE Request String", required = true) OrderPostUERequest ueRequest) {

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
	 * Validate quantity limit against the order.
	 * 
	 * @param ueRequest
	 *            The UERequest passed.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/validate_quantity")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "The validate_quantity command validates the quantity limitation against the order.", notes="Command: com.ibm.commerce.orderitems.commands.OrderItemAddCmd", response = OrderPreUEResponse.class,
			extensions = { 
		       @Extension( name = "data-command", properties = {
		           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.orderitems.commands.OrderItemAddCmd")
		           })
		    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response validateQuantity(
			@ApiParam(name = "validateQuantity UE input", value = "The UE Request String", required = true) OrderPreUERequest ueRequest) {

		final String METHOD_NAME = "validateQuantity()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		
		String id = null;
		if(request != null){
			id = request.getHeader(requestId);
		}
		

		Order orderPOJO = ueRequest.getOrder();

		Map<String, Double> catentryIdToTotalQuantity = new HashMap<String, Double>();
		if (orderPOJO != null) {
			List<OrderItem> orderitemPOJOs = orderPOJO.getOrderItem();
			if (orderitemPOJOs != null && !orderitemPOJOs.isEmpty()) {
				for (OrderItem orderitemPOJO : orderitemPOJOs) {
					Quantity quantity = orderitemPOJO.getQuantity();
					if (quantity != null) {
						double currentQuantity = quantity.getValue();
						String catentryId = orderitemPOJO.getCatalogEntryIdentifier().getUniqueID();
						Double totalQuantityInOrder = catentryIdToTotalQuantity.get(catentryId);
						if (totalQuantityInOrder == null) {
							totalQuantityInOrder = 0.0;
						}
						totalQuantityInOrder = totalQuantityInOrder + currentQuantity;
						catentryIdToTotalQuantity.put(catentryId, totalQuantityInOrder);
					}
				}
			}
			LOGGER.info("all total quantities by catentryId = " + catentryIdToTotalQuantity);
		} else {
			LOGGER.info("order is null. all total quantities by catentryId = " + catentryIdToTotalQuantity);
		}

		Map<String, Object> requestPropertyPOJOs = ueRequest.getCommandInputs();
		Map<String, CQ> indexToCQMap = new HashMap<String, CQ>();
		if (requestPropertyPOJOs != null && !requestPropertyPOJOs.isEmpty()) {
			for (Entry<String, Object> entry : requestPropertyPOJOs.entrySet()) {
				String name = entry.getKey();
				if (name != null && (name.startsWith("quantity_") || name.startsWith("catEntryId_")
						|| name.startsWith("orderItemId_"))) {
					int lastUnderscoreIndex = name.indexOf('_');
					String index = name.substring(lastUnderscoreIndex + 1);
					CQ cq = indexToCQMap.get(index);
					if (cq == null) {
						cq = new CQ();
						indexToCQMap.put(index, cq);
					}

					String value = (String) requestPropertyPOJOs.get(name);
					if (name.startsWith("quantity_")) {
						cq.quantity = Double.valueOf(value);
					} else if (name.startsWith("catEntryId_")) {
						// no catentryId for OrderItemUpdateCmd,
						// use orderItemId_ to get catentryID
						cq.catentryId = value;
					} else {
						if (orderPOJO != null) {
							List<OrderItem> orderitemPOJOs = orderPOJO.getOrderItem();
							if (orderitemPOJOs != null && !orderitemPOJOs.isEmpty()) {
								for (OrderItem orderitemPOJO : orderitemPOJOs) {
									String catentryId = orderitemPOJO.getOrderItemIdentifier().getUniqueID();
									if (catentryId.equals(value)) {
										cq.catentryId = catentryId;
									}
								}
							}
						}
					}
				}

			}
		}
		LOGGER.info("indexToCQMap = " + indexToCQMap);

		for (CQ cq : indexToCQMap.values()) {
			String catentryId = cq.catentryId;
			Double requestQuantity = cq.quantity;
			if (catentryId != null && requestQuantity != null) {
				// addToTotal(catentryIdToTotalQuantity, catentryId,
				// requestQuantity);
				// For OrderItemUpdateCmd, it should replace qty here.
				catentryIdToTotalQuantity.put(catentryId, requestQuantity);
			}
		}

		LOGGER.info(
				"all total quantities after adding request properties by catentryId = " + catentryIdToTotalQuantity);

		for (Entry<String, Double> entry : catentryIdToTotalQuantity.entrySet()) {
			String catentryId = entry.getKey();
			Double totalQuantity = entry.getValue();
			// hard coded quantity limit
			Double maxQuantityForCatentry = 1000.0;
			// Double maxQuantityForCatentry = getMaxQuantity(catentryId);

			if (maxQuantityForCatentry != null && totalQuantity != null && totalQuantity > maxQuantityForCatentry) {
				LOGGER.info("Quantity validation fail for catentry " + catentryId + " with quantity " + totalQuantity
						+ ", maximum is " + maxQuantityForCatentry + ", will throw exception.");

				ErrorUEResponse ueResponse = new ErrorUEResponse();
				List<ExceptionData> errors = new ArrayList<ExceptionData>();
				ExceptionData error = new ExceptionData();
				error.setCode("400");
				error.setMessageKey("_ERR_INVALID_PARAMETER_VALUE");
				error.setMessage(MessageKey.getMessage(locale, "_ERR_INVALID_PARAMETER_VALUE",
						new Object[] { "quantity", totalQuantity }));
				errors.add(error);
				ueResponse.setErrors(errors);
				Response response = Response.status(400).type(MediaType.APPLICATION_JSON).entity(ueResponse).build();

				LOGGER.exiting(CLASS_NAME, METHOD_NAME, response);
				return response;
			}
		}

		LOGGER.info("Quantity validation success, will return with OK!");

		Map<String, Object> inputParameters = ueRequest.getCommandInputs();

		OrderPreUEResponse ueResponse = UserExitUtil.createPreUEResponse(orderPOJO, inputParameters);

		Response response = Response.ok(ueResponse).build();
		
		ResponseBuilder responsebuilder = Response.fromResponse(response);
		if(id != null){
			responsebuilder.header(requestId, id);
			responsebuilder.header(responseId, id + "_001");
			response = responsebuilder.build();
		}
		
		
		LOGGER.exiting(CLASS_NAME, METHOD_NAME, response);
		return response;

	}
	
	@POST
	@Path("/compensate_transaction")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON }) 
	@ApiOperation(value = "The compensation for transaction rollback", notes="Command: com.ibm.commerce.foundation.ue.compensation.CompensateForTxRollbackCmd",
			extensions = { 
		       @Extension( name = "data-command", properties = {
		           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.foundation.ue.compensation.CompensateForTxRollbackCmd")
		           })
		    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Application error"),
			@ApiResponse(code = 500, message = "System error") })
	public Response compensateForTxTransanction(@ApiParam(name = "compensateForTxTransanction UE input", value = "The compensateForTxTransanction UE input parameters", required = true) CompensateForTxRollbackCmdUEInput ueRequest) {

		final String METHOD_NAME = "compensateForTxTransanction()";
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.entering(CLASS_NAME, METHOD_NAME, new Object[] {});
		}
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.log(Level.FINER, "This is a UE implementation for compensateForTxTransanction");
		}
		
		CompensateForTxRollbackCmdUEOutput ueResponse = new CompensateForTxRollbackCmdUEOutput();
		List<UEInvocation> list = ueRequest.getUEInvocations();
		list.get(0).setCompensationResult("succeed");
		ueResponse.setUEInvocations(list);
		Response response = Response.ok(ueResponse).build();
		
		return response;

	}

	private class CQ {
		@Override
		public String toString() {
			return "CQ [catentryId=" + catentryId + ", quantity=" + quantity + "]";
		}

		private String catentryId;
		private Double quantity;
	}

	/**
	 * The API extension for PreProcessOrderCmd task command. This task command
	 * is used to pre process the order, it's called before the calling of
	 * {@link ProcessOrderCmd}.
	 * 
	 * @param PreProcessOrderUEInput
	 *            The PreProcessOrder UE Input Parameter.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/tax_integration_custom")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "The API extension for TaxIntegrationCustom task command.", notes = "| Command: com.ibm.commerce.isv.kit.tax.TaxIntegrationCustomCmd |\n|----------|\n| TaxIntegrationCustomCmd is a task command which is used to custom the tax integration by CoC Customization framework.|", response = TaxIntegrationCustomCmdUEOutput.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.isv.kit.tax.TaxIntegrationCustomCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response taxIntegrationOrderCus(
			@ApiParam(name = "TaxIntegrationCustomCmdUEInput", value = "TaxIntegrationCustomCmd UE Input Parameter", required = true) TaxIntegrationCustomCmdUEInput taxIntegrationCustomCmdUEInput) {
		List<TaxIntegrationCustomOrderItem> taxOrderItems = taxIntegrationCustomCmdUEInput.getTaxOrderItems();
		for(TaxIntegrationCustomOrderItem taxOrderItem:taxOrderItems){
			taxOrderItem.setTotalTax(BigDecimal.valueOf(1.0));
			taxOrderItem.setTotalTaxRate(BigDecimal.valueOf(1.1));
			taxOrderItem.setRecyclingFee(BigDecimal.valueOf(1.2));
			taxOrderItem.setTaxRate(new BigDecimal[]{BigDecimal.valueOf(1.0),BigDecimal.valueOf(1.0),BigDecimal.valueOf(1.0),BigDecimal.valueOf(1.0),BigDecimal.valueOf(1.0),BigDecimal.valueOf(1.0)});
			taxOrderItem.setSecondaryTaxRate(new BigDecimal[]{BigDecimal.valueOf(1.0),BigDecimal.valueOf(1.0),BigDecimal.valueOf(1.0)});
			taxOrderItem.setTaxAmounts(new BigDecimal[]{BigDecimal.valueOf(1.1),BigDecimal.valueOf(1.1),BigDecimal.valueOf(1.1),BigDecimal.valueOf(1.1),BigDecimal.valueOf(1.1),BigDecimal.valueOf(1.1)});
			taxOrderItem.setSecondaryTaxAmounts(new BigDecimal[]{BigDecimal.valueOf(1.1),BigDecimal.valueOf(1.1),BigDecimal.valueOf(1.1)});
		}
		TaxIntegrationCustomCmdUEOutput taxIntegrationCustomCmdUEOutput = new TaxIntegrationCustomCmdUEOutput();
		taxIntegrationCustomCmdUEOutput.setTaxOrderItems(taxOrderItems);
		taxIntegrationCustomCmdUEOutput.setTotalTax(BigDecimal.valueOf(1.0));
		taxIntegrationCustomCmdUEOutput.setTotalRecyclingFee(BigDecimal.valueOf(1.0));
		Response response = Response.ok(taxIntegrationCustomCmdUEOutput).build();
		return response;
	}
	
	/**
	 * The API extension for taxIntegrationOrderCusVAT task command. This task command
	 * is used to do the VAT Calculations for the checkout pages, it's called twice for each page. First, to calculate the VAT tax for the item. Second, to calculate the shipping tax.
	 * 
	 * @param TaxIntegrationCustomCmdUEInput
	 *            The PreProcessOrder UE Input Parameter.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/tax_integration_custom_vat")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "The API extension for TaxIntegrationCustom task command.", notes = "| Command: com.ibm.commerce.isv.kit.tax.TaxIntegrationCustomCmd |\n|----------|\n| TaxIntegrationCustomCmd is a task command which is used to custom the tax integration by CoC Customization framework.|", response = TaxIntegrationCustomCmdUEOutput.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.isv.kit.tax.TaxIntegrationCustomCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response taxIntegrationOrderCusVAT(
			@ApiParam(name = "TaxIntegrationCustomCmdUEInput", value = "TaxIntegrationCustomCmd UE Input Parameter", required = true) TaxIntegrationCustomCmdUEInput taxIntegrationCustomCmdUEInput) {
		
		Boolean calculateShipping = false;
		double rateVAT = 0.2;
		double totalCostInit = 0.0;
		double totalDiscount = 0.0;
		double totalVATTax = 0.0;
		double totalShippingCost = 0.0;
		double totalShippingTax = 0.0;

		List<TaxIntegrationCustomOrderItem> taxOrderItems = taxIntegrationCustomCmdUEInput.getTaxOrderItems();
		
		//Check if the method was called to calculating Shipping or Item cost by checking the values sent in.
		if (taxOrderItems.get(0).getCost() == null) {
			calculateShipping = true;
		}
		for(TaxIntegrationCustomOrderItem taxOrderItem:taxOrderItems){
		
			double itemCost = 0;
			double itemDiscount = 0;
			double itemVATTax = 0;
			double itemShippingCost = 0;
			double itemShippingTax = 0;
			double itemShippingTaxRate = 0;


			//If only calculating Item Cost and VAT Tax
			if (!calculateShipping) {
				//Extracting and Calculating Values
				itemCost = taxOrderItem.getCost().doubleValue();
				itemDiscount = taxOrderItem.getDiscount().doubleValue();
				itemVATTax = (((itemCost - itemDiscount) / (1 + rateVAT)) * rateVAT);
				totalCostInit += itemCost;
				totalDiscount += itemDiscount;
				totalVATTax += itemVATTax;
				taxOrderItem.setTotalTax(BigDecimal.valueOf(itemVATTax));			
			}
			//If Calculating Shipping Cost and Shipping Tax
			else {
				//Extracting and Calculating Values
				itemShippingCost = taxOrderItem.getFreight().doubleValue();
				itemShippingTaxRate = rateVAT;
				itemShippingTax = (itemShippingCost/ (1 + rateVAT))*itemShippingTaxRate;
				totalShippingCost += itemShippingCost;
				totalShippingTax += itemShippingTax;
				taxOrderItem.setTotalTax(BigDecimal.valueOf(itemShippingTax));
			}			
			taxOrderItem.setTotalTaxRate(BigDecimal.valueOf(rateVAT));
	}
		//Inputting Data for the the Output Response
		TaxIntegrationCustomCmdUEOutput taxIntegrationCustomCmdUEOutput = new TaxIntegrationCustomCmdUEOutput();
		taxIntegrationCustomCmdUEOutput.setTaxOrderItems(taxOrderItems);
		taxIntegrationCustomCmdUEOutput.setTotalTax(BigDecimal.valueOf(totalVATTax));
		Response response = Response.ok(taxIntegrationCustomCmdUEOutput).build();
		return response;
	}
	

	
	/**
	 * The API extension for PreProcessOrderCmd task command. This task command
	 * is used to pre process the order, it's called before the calling of
	 * {@link ProcessOrderCmd}.
	 * 
	 * @param PreProcessOrderUEInput
	 *            The PreProcessOrder UE Input Parameter.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/pre_process_order")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "The API extension for PreProcessOrder task command.", notes = "| Command: com.ibm.commerce.order.commands.PreProcessOrderCmd |\n|----------|\n| PreProcessOrderCmd is a task command which is used to pre process the order, it's called before the calling of ProcessOrderCmd.|", response = PreProcessOrderCmdUEOutput.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.PreProcessOrderCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response preProcessOrderCus(
			@ApiParam(name = "PreProcessOrderUEInput", value = "PreProcessOrder UE Input Parameter", required = true) PreProcessOrderCmdUEInput preProcessOrderUEInput) {
		List<Field> properties = new ArrayList<Field>();
		Field[] fields = PreProcessOrderCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		fields = TaskCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		PreProcessOrderCmdUEOutput preProcessOrderUEOutput = new PreProcessOrderCmdUEOutput();
		UserExitUtil.setPropertiesFromInputToOutput(preProcessOrderUEInput, PreProcessOrderCmdUEInput.class,
				preProcessOrderUEOutput, PreProcessOrderCmdUEOutput.class, properties);

		// testing properties preservation.
		preProcessOrderUEOutput.setPoNumber("666666666");
		Response response = Response.ok(preProcessOrderUEOutput).build();
		return response;
	}

	/**
	 * The API extension for ProcessOrderSubmitEvent task command. This task
	 * command is used to process the event captured by the listener when the
	 * event name is order submit.
	 * 
	 * @param processOrderSubmitEventCmdUEInputInput
	 *            The ProcessOrderSubmitEventCmd UE Input Parameter.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/process_order_submit_event")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "The API extension for ProcessOrderSubmitEventCmd task command.", notes = "| Command: com.ibm.commerce.order.commands.ProcessOrderSubmitEventCmd |\n|----------|\n| ProcessOrderSubmitEventCmd is a task command which is used to process the event captured by the listener when the event name is order submit.|", response = ProcessOrderSubmitEventCmdUEOutput.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.ProcessOrderSubmitEventCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response processOrderSubmitEvent(
			@ApiParam(name = "ProcessOrderSubmitEventCmdUEInput", value = "ProcessOrderSubmitEventCmd UE Input Parameter", required = true) ProcessOrderSubmitEventCmdUEInput processOrderSubmitEventCmdUEInputInput) {

		List<Field> properties = new ArrayList<Field>();
		Field[] fields = ProcessOrderSubmitEventCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		fields = TaskCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		ProcessOrderSubmitEventCmdUEOutput ueOutput = new ProcessOrderSubmitEventCmdUEOutput();
		UserExitUtil.setPropertiesFromInputToOutput(processOrderSubmitEventCmdUEInputInput,
				ProcessOrderSubmitEventCmdUEInput.class, ueOutput, ProcessOrderSubmitEventCmdUEOutput.class,
				properties);

		// testing order modification
		Order orderPOJO = ueOutput.getOrder();
		if (orderPOJO != null) {
			orderPOJO.setOrderDescription("Testing process_order_submit_event");
		}

		Response response = Response.ok(ueOutput).build();
		return response;
	}

	/**
	 * The API extension for ProcessOrderCmd task command. This Order task
	 * command is used to submit an order.
	 * 
	 * @param processOrderUEInput
	 *            ProcessOrderCmd UE Input Parameter.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/process_order")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "The API extension for ProcessOrderCmd task command.", notes = "| Command: com.ibm.commerce.order.commands.ProcessOrderCmd |\n|----------|\n| ProcessOrderCmd is a task command which is used to submit an order.|", response = ProcessOrderCmdUEOutput.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.ProcessOrderCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response processOrderCus(
			@ApiParam(name = "ProcessOrderUEInput", value = "ProcessOrderCmd UE Input Parameter", required = true) ProcessOrderCmdUEInput processOrderUEInput) {
		List<Field> properties = new ArrayList<Field>();
		Field[] fields = ProcessOrderCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		fields = TaskCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		ProcessOrderCmdUEOutput processOrderCmdUEOutput = new ProcessOrderCmdUEOutput();
		UserExitUtil.setPropertiesFromInputToOutput(processOrderUEInput, ProcessOrderCmdUEInput.class,
				processOrderCmdUEOutput, ProcessOrderCmdUEOutput.class, properties);
		processOrderCmdUEOutput.setPoNumber("666666666");
		Response response = Response.ok(processOrderCmdUEOutput).build();
		return response;
	}

	/**
	 * The API extension for ExtOrderProcessCmd task command. This command is
	 * used to perform custom processing just prior to the completion of the
	 * {@link OrderProcessCmd} controller command
	 * 
	 * @param extOrderProcessCmdUEInput
	 *            The ExtOrderProcessCmd UE Input Parameter.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/ext_order_process")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "The API extension for ExtOrderProcessCmd task command.", notes = "| Command: com.ibm.commerce.order.commands.ExtOrderProcessCmd |\n|----------|\n| ExtOrderProcessCmd is a task command which is used to perform custom processing just prior to the completion of the OrderProcessCmd controller command.|", response = ExtOrderProcessCmdUEOutput.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.ExtOrderProcessCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response extOrderProcessCus(
			@ApiParam(name = "ExtOrderProcessCmdUEInput", value = "ExtOrderProcessCmd UE Input Parameter", required = true) ExtOrderProcessCmdUEInput extOrderProcessCmdUEInput) {
		List<Field> properties = new ArrayList<Field>();
		Field[] fields = ExtOrderProcessCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		fields = TaskCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		ExtOrderProcessCmdUEOutput extOrderProcessCmdUEOutput = new ExtOrderProcessCmdUEOutput();
		UserExitUtil.setPropertiesFromInputToOutput(extOrderProcessCmdUEInput, ExtOrderProcessCmdUEInput.class,
				extOrderProcessCmdUEOutput, ExtOrderProcessCmdUEOutput.class, properties);
		extOrderProcessCmdUEOutput.setOrderRn(10001L);
		Response response = Response.ok(extOrderProcessCmdUEOutput).build();
		return response;
	}

	/**
	 * The API extension for UpdateShippingAddressCmd task command. This Order
	 * task command is used to update the shipping address for the passed order
	 * items.
	 * 
	 * @param updateShippingAddressCmdUEInput
	 *            The UpdateShippingAddress UE Input Parameter.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/update_shipping_address")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "The API extension for UpdateShippingAddressCmd task command.", notes = "| Command: com.ibm.commerce.order.commands.UpdateShippingAddressCmd |\n|----------|\n| UpdateShippingAddressCmd is a task command which is used to update the shipping address for the passed order items.|", response = UpdateShippingAddressCmdUEOutput.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.UpdateShippingAddressCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response updateShippingAddressCus(
			@ApiParam(name = "UpdateShippingAddressUEInput", value = "UpdateShippingAddress UE Input Parameter", required = true) UpdateShippingAddressCmdUEInput updateShippingAddressUEInput) {
		List<Field> properties = new ArrayList<Field>();
		Field[] fields = UpdateShippingAddressCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		fields = TaskCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		UpdateShippingAddressCmdUEOutput updateShippingAddressUEOutput = new UpdateShippingAddressCmdUEOutput();
		UserExitUtil.setPropertiesFromInputToOutput(updateShippingAddressUEInput, UpdateShippingAddressCmdUEInput.class,
				updateShippingAddressUEOutput, UpdateShippingAddressCmdUEOutput.class, properties);

		updateShippingAddressUEOutput.setAddressIds(new Long[] { 1L });
		Response response = Response.ok(updateShippingAddressUEOutput).build();
		return response;
	}

	/**
	 * The API extension for OrderNotifyCmd task command. This Order task
	 * command is used to Sends order notification message.
	 * 
	 * @param OrderNotifyCmdUEInput
	 *            The OrderNotifyCmd UE Input Parameter.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/order_notify")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "The API extension for OrderNotifyCmd task command.", notes = "| Command: com.ibm.commerce.order.commands.OrderNotifyCmd |\n|----------|\n| OrderNotifyCmd is task command is used to Sends order notification message.|", response = OrderNotifyCmdUEOutput.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.OrderNotifyCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response orderNotifyCus(
			@ApiParam(name = "OrderNotifyUEInput", value = "OrderNotify UE Input Parameter", required = true) OrderNotifyCmdUEInput orderNotifyUEInput) {
		List<Field> properties = new ArrayList<Field>();
		Field[] fields = OrderNotifyCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		fields = TaskCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		OrderNotifyCmdUEOutput orderNotifyUEOutput = new OrderNotifyCmdUEOutput();
		UserExitUtil.setPropertiesFromInputToOutput(orderNotifyUEInput, OrderNotifyCmdUEInput.class,
				orderNotifyUEOutput, OrderNotifyCmdUEOutput.class, properties);

		orderNotifyUEOutput.setNotificationEnabled(true);
		Response response = Response.ok(orderNotifyUEOutput).build();
		return response;
	}

	/**
	 * The API extension for OrderMessagingCmd task command. The interface of
	 * the OrderMessagingCmd task command that generates the outbound Order
	 * Create Message <samp>"Report_NC_PurchaseOrder"</samp>.
	 * 
	 * @param orderMessagingUEInput
	 *            The orderMessaging UE Input Parameter.
	 * 
	 * @return The response.
	 */

	@POST
	@Path("/order_messaging")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "The API extension for OrderMessagingCmd task command.", notes = "| Command: com.ibm.commerce.order.commands.OrderMessagingCmd |\n|----------|\n| OrderMessagingCmd is a task command that generates the outbound Order Create Message \"Report_NC_PurchaseOrder\".|", response = OrderMessagingCmdUEOutput.class,
		extensions = { 
	       @Extension( name = "data-command", properties = {
	           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.OrderMessagingCmd")
	           })
	    })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response orderMessagingCus(
			@ApiParam(name = "OrderMessagingUEInput", value = "OrderMessaging UE Input Parameter", required = true) OrderMessagingCmdUEInput orderMessagingUEInput) {

		List<Field> properties = new ArrayList<Field>();
		Field[] fields = OrderMessagingCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		fields = TaskCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		} 
		OrderMessagingCmdUEOutput orderMessagingUEOutput = new OrderMessagingCmdUEOutput();
		UserExitUtil.setPropertiesFromInputToOutput(orderMessagingUEInput, OrderMessagingCmdUEInput.class,
				orderMessagingUEOutput, OrderMessagingCmdUEOutput.class, properties);

		Order order = orderMessagingUEOutput.getOrder();
		Map<String, String> userData = new HashMap<String, String>();
		userData.put("PGreetings", "Hello, This is a UE test");
		order.getUserData().setUserDataField(userData);

		Response response = Response.ok(orderMessagingUEOutput).build();
		return response;
	}

	@POST
	@Path("/update_shipping_charges")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "The API extension for UpdateShippingChargesCmd task command.", notes = "| Command: com.ibm.commerce.order.commands.UpdateShippingChargesCmd |\n|----------|\n| UpdateShippingChargesCmd is a task command which is used to update the shipping charges for the passed order items.|", response = UpdateShippingChargesCmdUEOutput.class, extensions = {
			@Extension(name = "data-command", properties = {
					@ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.UpdateShippingChargesCmd") }) })
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid Input Parameter"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response updateShippingChargesCus(
			@ApiParam(name = "UpdateShippingChargesUEInput", value = "UpdateShippingCharges UE Input Parameter", required = true) UpdateShippingChargesCmdUEInput updateShippingChargesUEInput) {

		List<Field> properties = new ArrayList<Field>();
		Field[] fields = UpdateShippingChargesCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		fields = TaskCmdUEInput.class.getDeclaredFields();
		for (Field field : fields) {
			properties.add(field);
		}
		UpdateShippingChargesCmdUEOutput updateShippingChargesUEOutput = new UpdateShippingChargesCmdUEOutput();
		UserExitUtil.setPropertiesFromInputToOutput(updateShippingChargesUEInput, UpdateShippingChargesCmdUEInput.class,
				updateShippingChargesUEOutput, UpdateShippingChargesCmdUEOutput.class, properties);

		List<OrderItem> orderItemList = updateShippingChargesUEOutput.getOrder().getOrderItem();
		Double newShippingCharge = 79.99;
		for (OrderItem orderItem : orderItemList) {
			long quantity = Math.round(orderItem.getQuantity().getValue());
			BigDecimal shippingCharge = new BigDecimal(newShippingCharge * quantity, DECIMAL2);
			orderItem.getOrderItemAmount().getShippingCharge().setValue(shippingCharge);
			orderItem.getOrderItemAmount().getShippingTax().setValue(BigDecimal.ZERO);
		}

		Response response = Response.ok(updateShippingChargesUEOutput).build();
		return response;
	}

}
