package com.ibm.commerce.order.ue.entities;

import com.ibm.commerce.foundation.entities.TaskCmdUEInput;
import com.ibm.commerce.order.entities.Order;

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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "This is the UpdateShippingChargesCmd UE input pojo")
public class UpdateShippingChargesCmdUEInput extends TaskCmdUEInput {
	/**
	 * The order pojo object containing order items whose shipping charges will be updated
	 */
	protected Order order;
	
	@ApiModelProperty(value = "The order pojo object containing order items whose shipping charges will be updated")
	public Order getOrder(){
		return this.order;
	}
	
	public void setOrder(Order order){
		this.order = order;
	}
}
