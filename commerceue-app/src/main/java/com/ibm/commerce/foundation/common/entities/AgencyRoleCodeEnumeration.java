//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.16 at 11:14:39 AM CST 
//


package com.ibm.commerce.foundation.common.entities;

import com.ibm.commerce.copyright.IBMCopyright;



/**
 * <p>Java class for AgencyRoleCodeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AgencyRoleCodeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="Customer"/>
 *     &lt;enumeration value="Supplier"/>
 *     &lt;enumeration value="Manufacturer"/>
 *     &lt;enumeration value="Broker"/>
 *     &lt;enumeration value="Carrier"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum AgencyRoleCodeEnumeration {

    CUSTOMER("Customer"),
    SUPPLIER("Supplier"),
    MANUFACTURER("Manufacturer"),
    BROKER("Broker"),
    CARRIER("Carrier");
    private final String value;

    AgencyRoleCodeEnumeration(String v) {
        value = v;
    }

    /**
	 * IBM copyright notice field.
	 */
	@SuppressWarnings("unused")
	private static final String COPYRIGHT = IBMCopyright.SHORT_COPYRIGHT;
	
    public String value() {
        return value;
    }

    public static AgencyRoleCodeEnumeration fromValue(String v) {
        for (AgencyRoleCodeEnumeration c: AgencyRoleCodeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}