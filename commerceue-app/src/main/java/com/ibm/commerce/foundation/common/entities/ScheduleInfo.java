//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.16 at 11:14:39 AM CST 
//


package com.ibm.commerce.foundation.common.entities;

import com.ibm.commerce.copyright.IBMCopyright;



/**
 * The type encapsulates the scheduling information for the subscription.
 * 
 * <p>Java class for ScheduleInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ScheduleInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StartInfo" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}ScheduleStartInfo" minOccurs="0"/>
 *         &lt;element name="FrequencyInfo" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}ScheduleFrequencyInfo" minOccurs="0"/>
 *         &lt;element name="EndInfo" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}ScheduleEndInfo" minOccurs="0"/>
 *         &lt;element name="UserData" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}UserData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
public class ScheduleInfo {

	/**
	 * IBM copyright notice field.
	 */
	@SuppressWarnings("unused")
	private static final String COPYRIGHT = IBMCopyright.SHORT_COPYRIGHT;
	
    protected ScheduleStartInfo startInfo;
    protected ScheduleFrequencyInfo frequencyInfo;
    protected ScheduleEndInfo endInfo;
    protected UserData userData;

    /**
     * Gets the value of the startInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ScheduleStartInfo }
     *     
     */
    public ScheduleStartInfo getStartInfo() {
        return startInfo;
    }

    /**
     * Sets the value of the startInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScheduleStartInfo }
     *     
     */
    public void setStartInfo(ScheduleStartInfo value) {
        this.startInfo = value;
    }

    /**
     * Gets the value of the frequencyInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ScheduleFrequencyInfo }
     *     
     */
    public ScheduleFrequencyInfo getFrequencyInfo() {
        return frequencyInfo;
    }

    /**
     * Sets the value of the frequencyInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScheduleFrequencyInfo }
     *     
     */
    public void setFrequencyInfo(ScheduleFrequencyInfo value) {
        this.frequencyInfo = value;
    }

    /**
     * Gets the value of the endInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ScheduleEndInfo }
     *     
     */
    public ScheduleEndInfo getEndInfo() {
        return endInfo;
    }

    /**
     * Sets the value of the endInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScheduleEndInfo }
     *     
     */
    public void setEndInfo(ScheduleEndInfo value) {
        this.endInfo = value;
    }

    /**
     * Gets the value of the userData property.
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
