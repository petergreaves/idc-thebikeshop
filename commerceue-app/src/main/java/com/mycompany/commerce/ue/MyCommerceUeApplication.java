/**
* =================================================================
* Licensed Materials - Property of IBM
*
* IBM Digital Commerce
*
* © Copyright IBM Corp. 2017
*
* US Government Users Restricted Rights - Use, duplication or
* disclosure restricted by GSA ADP Schedule Contract with
* IBM Corp.
* =================================================================
*/

package com.mycompany.commerce.ue;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.mycompany.commerce.giftwrap.GiftWrapperResource;

@ApplicationPath("app")
public class MyCommerceUeApplication extends Application {
	public Set<Class<?>> getClasses() {
		Set<Class<?>> s = new HashSet<Class<?>>();
		s.add(GiftWrapperResource.class);

		return s;
	}
}