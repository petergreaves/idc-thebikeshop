//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.16 at 11:14:39 AM CST 
//


package com.ibm.commerce.order.entities;

import com.ibm.commerce.copyright.IBMCopyright;
import com.ibm.commerce.foundation.common.entities.Description;
import com.ibm.commerce.foundation.common.entities.FulfillmentCenterIdentifier;
import com.ibm.commerce.foundation.common.entities.UserData;



/**
 * Type definition of the fulfillment center.
 * 
 * <p>Java class for FulfillmentCenter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FulfillmentCenter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FulfillmentCenterIdentifier" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}FulfillmentCenterIdentifier" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}Description" minOccurs="0"/>
 *         &lt;element ref="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}UserData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
public class FulfillmentCenter {

	/**
	 * IBM copyright notice field.
	 */
	@SuppressWarnings("unused")
	private static final String COPYRIGHT = IBMCopyright.SHORT_COPYRIGHT;
	
    protected FulfillmentCenterIdentifier fulfillmentCenterIdentifier;
    protected Description description;
    protected UserData userData;

    /**
     * Gets the value of the fulfillmentCenterIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link FulfillmentCenterIdentifier }
     *     
     */
    public FulfillmentCenterIdentifier getFulfillmentCenterIdentifier() {
        return fulfillmentCenterIdentifier;
    }

    /**
     * Sets the value of the fulfillmentCenterIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link FulfillmentCenterIdentifier }
     *     
     */
    public void setFulfillmentCenterIdentifier(FulfillmentCenterIdentifier value) {
        this.fulfillmentCenterIdentifier = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link Description }
     *     
     */
    public Description getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link Description }
     *     
     */
    public void setDescription(Description value) {
        this.description = value;
    }

    /**
     * The user data area.
     * 
     * @return
     *     possible object is
     *     {@link UserData }
     *     
     */
    public UserData getUserData() {
        return userData;
    }

    /**
     * Sets the value of the userData property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserData }
     *     
     */
    public void setUserData(UserData value) {
        this.userData = value;
    }

}