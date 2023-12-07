package com.paras.model;

import java.util.UUID;

public class TConnectortag {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_connectortag.PropertyID
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private Integer propertyID;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_connectortag.ElementID
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private Integer elementID;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_connectortag.Property
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String property;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_connectortag.VALUE
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String VALUE;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_connectortag.ea_guid
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String ea_guid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_connectortag.NOTES
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String NOTES;
    
    public TConnectortag() {
        this(false);
    }
    
    public TConnectortag(boolean init) {
        if(init) {
            initGuid();
        }
    }
    
    public void initGuid() {
        setEa_guid("{" + UUID.randomUUID() + "}");
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_connectortag.PropertyID
     *
     * @return the value of t_connectortag.PropertyID
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public Integer getPropertyID() {
        return propertyID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_connectortag.PropertyID
     *
     * @param propertyID the value for t_connectortag.PropertyID
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setPropertyID(Integer propertyID) {
        this.propertyID = propertyID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_connectortag.ElementID
     *
     * @return the value of t_connectortag.ElementID
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public Integer getElementID() {
        return elementID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_connectortag.ElementID
     *
     * @param elementID the value for t_connectortag.ElementID
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setElementID(Integer elementID) {
        this.elementID = elementID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_connectortag.Property
     *
     * @return the value of t_connectortag.Property
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getProperty() {
        return property;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_connectortag.Property
     *
     * @param property the value for t_connectortag.Property
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setProperty(String property) {
        this.property = property == null ? null : property.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_connectortag.VALUE
     *
     * @return the value of t_connectortag.VALUE
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getVALUE() {
        return VALUE;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_connectortag.VALUE
     *
     * @param VALUE the value for t_connectortag.VALUE
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setVALUE(String VALUE) {
        this.VALUE = VALUE == null ? null : VALUE.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_connectortag.ea_guid
     *
     * @return the value of t_connectortag.ea_guid
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getEa_guid() {
        return ea_guid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_connectortag.ea_guid
     *
     * @param ea_guid the value for t_connectortag.ea_guid
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setEa_guid(String ea_guid) {
        this.ea_guid = ea_guid == null ? null : ea_guid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_connectortag.NOTES
     *
     * @return the value of t_connectortag.NOTES
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getNOTES() {
        return NOTES;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_connectortag.NOTES
     *
     * @param NOTES the value for t_connectortag.NOTES
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setNOTES(String NOTES) {
        this.NOTES = NOTES == null ? null : NOTES.trim();
        if(this.NOTES != null) {
            this.NOTES.replace("'", "\'"); // escape quote
            this.NOTES.replace("", "\""); // escape quote
        }
    }
}