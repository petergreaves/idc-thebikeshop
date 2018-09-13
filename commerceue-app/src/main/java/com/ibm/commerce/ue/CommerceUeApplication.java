package com.ibm.commerce.ue;

/*
 *-----------------------------------------------------------------
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WebSphere Commerce
 *
 * (C) Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 *-----------------------------------------------------------------
 */

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.ibm.commerce.inventory.ue.rest.InventoryResource;
import com.ibm.commerce.member.ue.rest.PersonResource;
import com.ibm.commerce.order.ue.rest.OrderResource;
import com.ibm.commerce.order.ue.rest.PromotionResource;
import com.ibm.commerce.payment.samples.pounchout.PaymentPunchoutSampleResource;
import com.ibm.commerce.payment.samples.tokenization.PaymentTokenSampleResource;
import com.ibm.commerce.payment.ue.rest.PaymentResource;
import com.ibm.commerce.search.ue.rest.CategoryViewResource;
import com.ibm.commerce.search.ue.rest.ProductViewResource;
import com.ibm.commerce.search.ue.rest.SiteContentResource;
import com.ibm.commerce.ue.rest.JacksonCustomProvider;
import com.ibm.commerce.ue.rest.SwaggerDefinitions;
import com.ibm.commerce.webhook.rest.WebhookResource;

@ApplicationPath("app")
public class CommerceUeApplication extends Application {
	public Set<Class<?>> getClasses() {
		Set<Class<?>> s = new HashSet<Class<?>>();

		// Add Swagger API resources to Application class set
		s.add(io.swagger.jaxrs.listing.ApiListingResource.class);
		s.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);

		// Add default and custom providers to Application class set
		s.add(JacksonJsonProvider.class);
		s.add(JacksonCustomProvider.class);
				
		// Add custom API Extension resources to Application class set
		s.add(SwaggerDefinitions.class);
		s.add(OrderResource.class);
		s.add(InventoryResource.class);
		s.add(PersonResource.class);
		s.add(PromotionResource.class);
		
		// Add Search custom API Extension resources
		s.add(SiteContentResource.class);
		s.add(CategoryViewResource.class);
		s.add(ProductViewResource.class);

		//s.add(PaymentResource.class);

		// Payment Punchout
		s.add(PaymentPunchoutSampleResource.class);

		// Payment tokenization
		s.add(PaymentTokenSampleResource.class);

		// webhook
		s.add(WebhookResource.class);

		return s;
	}
}