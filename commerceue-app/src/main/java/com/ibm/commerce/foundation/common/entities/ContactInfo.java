//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.16 at 11:14:39 AM CST 
//


package com.ibm.commerce.foundation.common.entities;

import java.util.Map;

import com.ibm.commerce.copyright.IBMCopyright;


/**
 * The type definition of a contact information entry.
 * 
 * <p>Java class for ContactInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContactInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ContactInfoIdentifier" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}ContactInfoIdentifier" minOccurs="0"/>
 *         &lt;element ref="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}ContactName" minOccurs="0"/>
 *         &lt;element ref="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}Address" minOccurs="0"/>
 *         &lt;element name="Telephone1" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}Telephone" minOccurs="0"/>
 *         &lt;element name="Telephone2" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}Telephone" minOccurs="0"/>
 *         &lt;element ref="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}BestCallingTime" minOccurs="0"/>
 *         &lt;element name="EmailAddress1" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}EmailAddress" minOccurs="0"/>
 *         &lt;element name="EmailAddress2" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}EmailAddress" minOccurs="0"/>
 *         &lt;element name="Fax1" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}Fax" minOccurs="0"/>
 *         &lt;element name="Fax2" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}Fax" minOccurs="0"/>
 *         &lt;element name="MobilePhone1" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}MobilePhone" minOccurs="0"/>
 *         &lt;element name="OrganizationName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OrganizationUnitName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GeographicalShippingCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GeographicalTaxCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Attributes" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}Map"/>
 *         &lt;element ref="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}UserData" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="language" type="{http://www.ibm.com/xmlns/prod/commerce/9/foundation}LanguageType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
public class ContactInfo {
	
	/**
	 * IBM copyright notice field.
	 */
	@SuppressWarnings("unused")
	private static final String COPYRIGHT = IBMCopyright.SHORT_COPYRIGHT;
	
    protected ContactInfoIdentifier contactInfoIdentifier;
    protected PersonName contactName;
    protected Address address;
    protected Telephone telephone1;
    protected Telephone telephone2;
    protected java.lang.String bestCallingTime;
    protected EmailAddress emailAddress1;
    protected EmailAddress emailAddress2;
    protected Fax fax1;
    protected Fax fax2;
    protected MobilePhone mobilePhone1;
    protected java.lang.String organizationName;
    protected java.lang.String organizationUnitName;
    protected java.lang.String geographicalShippingCode;
    protected java.lang.String geographicalTaxCode;
    protected Map<String, String> attributes;
    protected UserData userData;
    protected java.lang.String language;

    /**
     * Gets the value of the contactInfoIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link ContactInfoIdentifier }
     *     
     */
    public ContactInfoIdentifier getContactInfoIdentifier() {
        return contactInfoIdentifier;
    }

    /**
     * Sets the value of the contactInfoIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactInfoIdentifier }
     *     
     */
    public void setContactInfoIdentifier(ContactInfoIdentifier value) {
        this.contactInfoIdentifier = value;
    }

    /**
     * The name of the contact.
     * 
     * @return
     *     possible object is
     *     {@link PersonName }
     *     
     */
    public PersonName getContactName() {
        return contactName;
    }

    /**
     * Sets the value of the contactName property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonName }
     *     
     */
    public void setContactName(PersonName value) {
        this.contactName = value;
    }

    /**
     * The address of the contact.
     * 
     * @return
     *     possible object is
     *     {@link Address }
     *     
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address }
     *     
     */
    public void setAddress(Address value) {
        this.address = value;
    }

    /**
     * Gets the value of the telephone1 property.
     * 
     * @return
     *     possible object is
     *     {@link Telephone }
     *     
     */
    public Telephone getTelephone1() {
        return telephone1;
    }

    /**
     * Sets the value of the telephone1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Telephone }
     *     
     */
    public void setTelephone1(Telephone value) {
        this.telephone1 = value;
    }

    /**
     * Gets the value of the telephone2 property.
     * 
     * @return
     *     possible object is
     *     {@link Telephone }
     *     
     */
    public Telephone getTelephone2() {
        return telephone2;
    }

    /**
     * Sets the value of the telephone2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Telephone }
     *     
     */
    public void setTelephone2(Telephone value) {
        this.telephone2 = value;
    }

    /**
     * The best time of the day to call.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getBestCallingTime() {
        return bestCallingTime;
    }

    /**
     * Sets the value of the bestCallingTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setBestCallingTime(java.lang.String value) {
        this.bestCallingTime = value;
    }

    /**
     * Gets the value of the emailAddress1 property.
     * 
     * @return
     *     possible object is
     *     {@link EmailAddress }
     *     
     */
    public EmailAddress getEmailAddress1() {
        return emailAddress1;
    }

    /**
     * Sets the value of the emailAddress1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmailAddress }
     *     
     */
    public void setEmailAddress1(EmailAddress value) {
        this.emailAddress1 = value;
    }

    /**
     * Gets the value of the emailAddress2 property.
     * 
     * @return
     *     possible object is
     *     {@link EmailAddress }
     *     
     */
    public EmailAddress getEmailAddress2() {
        return emailAddress2;
    }

    /**
     * Sets the value of the emailAddress2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmailAddress }
     *     
     */
    public void setEmailAddress2(EmailAddress value) {
        this.emailAddress2 = value;
    }

    /**
     * Gets the value of the fax1 property.
     * 
     * @return
     *     possible object is
     *     {@link Fax }
     *     
     */
    public Fax getFax1() {
        return fax1;
    }

    /**
     * Sets the value of the fax1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Fax }
     *     
     */
    public void setFax1(Fax value) {
        this.fax1 = value;
    }

    /**
     * Gets the value of the fax2 property.
     * 
     * @return
     *     possible object is
     *     {@link Fax }
     *     
     */
    public Fax getFax2() {
        return fax2;
    }

    /**
     * Sets the value of the fax2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Fax }
     *     
     */
    public void setFax2(Fax value) {
        this.fax2 = value;
    }

    /**
     * Gets the value of the mobilePhone1 property.
     * 
     * @return
     *     possible object is
     *     {@link MobilePhone }
     *     
     */
    public MobilePhone getMobilePhone1() {
        return mobilePhone1;
    }

    /**
     * Sets the value of the mobilePhone1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link MobilePhone }
     *     
     */
    public void setMobilePhone1(MobilePhone value) {
        this.mobilePhone1 = value;
    }

    /**
     * Gets the value of the organizationName property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrganizationName() {
        return organizationName;
    }

    /**
     * Sets the value of the organizationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrganizationName(java.lang.String value) {
        this.organizationName = value;
    }

    /**
     * Gets the value of the organizationUnitName property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrganizationUnitName() {
        return organizationUnitName;
    }

    /**
     * Sets the value of the organizationUnitName property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrganizationUnitName(java.lang.String value) {
        this.organizationUnitName = value;
    }

    /**
     * Gets the value of the geographicalShippingCode property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getGeographicalShippingCode() {
        return geographicalShippingCode;
    }

    /**
     * Sets the value of the geographicalShippingCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setGeographicalShippingCode(java.lang.String value) {
        this.geographicalShippingCode = value;
    }

    /**
     * Gets the value of the geographicalTaxCode property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getGeographicalTaxCode() {
        return geographicalTaxCode;
    }

    /**
     * Sets the value of the geographicalTaxCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setGeographicalTaxCode(java.lang.String value) {
        this.geographicalTaxCode = value;
    }

    /**
     * Gets the value of the attributes property.
     * 
     * @return
     *     possible object is
     *     {@link Map }
     *     
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Sets the value of the attributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Map }
     *     
     */
    public void setAttributes(Map<String, String> value) {
        this.attributes = value;
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

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setLanguage(java.lang.String value) {
        this.language = value;
    }

}