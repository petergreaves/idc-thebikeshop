package com.ibm.commerce.webhook.rest;

/*
 *-----------------------------------------------------------------
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WebSphere Commerce
 *
 * (C) Copyright IBM Corp. 2018
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 *-----------------------------------------------------------------
 */
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.commerce.webhook.entities.WebhookRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

import com.ibm.commerce.member.ue.entities.PersonPostUERequest;
import com.ibm.commerce.order.ue.entities.OrderPostUERequest;
import com.ibm.commerce.ue.rest.BaseResource;

@Path("/webhook/sample")
@SwaggerDefinition(tags = { @Tag(name = "Webhook Samples", description = "Examples for webhook consumption.") })
@Api(value = "/webhook/sample", tags = "webhook")
public class WebhookResource extends BaseResource {

   private static final String CLASSNAME = WebhookResource.class.getName();
   private static final Logger LOGGER = Logger.getLogger(CLASSNAME);

   @POST
   @Path("/person_registered")
   @Consumes({ MediaType.APPLICATION_JSON })
   @Produces({ MediaType.APPLICATION_JSON })
   @ApiOperation(value = "Sample webhook actions to perform for user registration event.", notes = "| Command: com.ibm.commerce.usermanagement.commands.UserRegistrationAddCmd |\n|----------|\n| UserRegistrationAddCmd is a controller command which is used to register a new user|", response = WebhookRequest.class,
           extensions = {
                   @Extension( name = "data-command", properties = {
                           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.usermanagement.commands.UserRegistrationUpdateCmd")
                   })
           })
   @ApiResponses(value = { @ApiResponse(code = 400, message = "Application error"),
           @ApiResponse(code = 500, message = "System error") })
   public Response personRegistered(@ApiParam(name="WebhookRequest<PersonPostUERequest>", value = "The webhook payload", required = true) WebhookRequest<PersonPostUERequest> personResource) {
      final String METHODNAME = "personRegistered(WebhookRequest<PersonPostUERequest>)";
      if (LOGGER.isLoggable(Level.FINER)) {
         LOGGER.entering(CLASSNAME, METHODNAME);
      }
      LOGGER.info("Webhook Request: " + personResource);

      Response response = Response.ok(personResource).build();

      LOGGER.exiting(CLASSNAME, METHODNAME);
      return response;
   }

   @POST
   @Path("/person_reset_password")
   @Consumes({ MediaType.APPLICATION_JSON })
   @Produces({ MediaType.APPLICATION_JSON })
   @ApiOperation(value = "Sample webhook actions to perform for user password reset event.", notes = "| Command: com.ibm.commerce.security.commands.ResetPasswordCmd |\n|----------|\n| ResetPasswordCmd is a controller command which is used to reset a users password|", response = WebhookRequest.class,
           extensions = {
                   @Extension( name = "data-command", properties = {
                           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.security.commands.ResetPasswordCmd")
                   })
           })
   @ApiResponses(value = { @ApiResponse(code = 400, message = "Application error"),
           @ApiResponse(code = 500, message = "System error") })
   public Response personResetPassword(@ApiParam(name="WebhookRequest<PersonPostUERequest>", value = "The webhook payload", required = true) WebhookRequest<PersonPostUERequest> personResource) {
      final String METHODNAME = "personResetPassword(WebhookRequest<PersonPostUERequest>";
      if (LOGGER.isLoggable(Level.FINER)) {
         LOGGER.entering(CLASSNAME, METHODNAME);
      }
      LOGGER.info("Webhook Request: " + personResource);

      Response response = Response.ok(personResource).build();

      LOGGER.exiting(CLASSNAME, METHODNAME);
      return response;
   }

   @POST
   @Path("/order_submitted")
   @Consumes({ MediaType.APPLICATION_JSON })
   @Produces({ MediaType.APPLICATION_JSON })
   @ApiOperation(value = "Sample webhook actions to perform for order submitted event.", notes = "| Command: com.ibm.commerce.order.commands.OrderProcessCmd |\n|----------|\n| OrderProcessCmd is a controller command which is used to process orders|", response = WebhookRequest.class,
           extensions = {
                   @Extension( name = "data-command", properties = {
                           @ExtensionProperty(name = "Command", value = "com.ibm.commerce.order.commands.OrderProcessCmd")
                   })
           })
   @ApiResponses(value = { @ApiResponse(code = 400, message = "Application error"),
           @ApiResponse(code = 500, message = "System error") })
   public Response orderSubmitted(@ApiParam(name="WebhookRequest<OrderPostUERequest>", value = "The webhook payload", required = true) WebhookRequest<OrderPostUERequest> orderResource) {
      final String METHODNAME = "orderSubmitted(WebhookRequest<OrderPostUERequest>";
      if (LOGGER.isLoggable(Level.FINER)) {
         LOGGER.entering(CLASSNAME, METHODNAME);
      }
      LOGGER.info("Webhook Request: " + orderResource);

      Response response = Response.ok(orderResource).build();

      LOGGER.exiting(CLASSNAME, METHODNAME);
      return response;
   }
}
