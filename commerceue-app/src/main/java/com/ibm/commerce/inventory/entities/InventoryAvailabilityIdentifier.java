package com.ibm.commerce.inventory.entities;

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


/**
 * The type definition of an InventoryAvailability identifier.
 */
public class InventoryAvailabilityIdentifier {

    protected String uniqueID;
    protected InventoryAvailabilityExternalIdentifier externalIdentifier;

    /**
     * Gets the value of the uniqueID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUniqueID() {
        return uniqueID;
    }

    /**
     * Sets the value of the uniqueID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUniqueID(String value) {
        this.uniqueID = value;
    }

    /**
     * Gets the value of the externalIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link InventoryAvailabilityExternalIdentifier }
     *     
     */
    public InventoryAvailabilityExternalIdentifier getExternalIdentifier() {
        return externalIdentifier;
    }

    /**
     * Sets the value of the externalIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link InventoryAvailabilityExternalIdentifier }
     *     
     */
    public void setExternalIdentifier(InventoryAvailabilityExternalIdentifier value) {
        this.externalIdentifier = value;
    }

}
