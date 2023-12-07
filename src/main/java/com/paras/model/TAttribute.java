package com.paras.model;

import java.util.UUID;

public class TAttribute {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Object_ID
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private Integer object_ID;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Name
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Scope
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String scope;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Stereotype
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String stereotype;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Containment
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String containment;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.IsStatic
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private Integer isStatic;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.IsCollection
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private Integer isCollection;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.IsOrdered
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private Integer isOrdered;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.AllowDuplicates
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private Integer allowDuplicates;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.LowerBound
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String lowerBound;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.UpperBound
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String upperBound;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Container
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String container;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Derived
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String derived;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.ID
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private Integer ID;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Pos
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private Integer pos;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Length
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private Integer length;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Precision
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private Integer precision;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Scale
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private Integer scale;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Const
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private Integer constant;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Style
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String style;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Classifier
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String classifier;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Type
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String type;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.ea_guid
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    private String ea_guid;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Default
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    protected String defaultVal;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.GenOption
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    protected String genOption;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.Notes
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    protected String notes;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_attribute.StyleEx
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    protected String styleEx;
    
    public TAttribute() {
        this(false);
    }
    
    public TAttribute(boolean init) {
        if(init) {
            initGuid();
            setScope("Private");
        }
    }
    
    public void initGuid() {
        setEa_guid("{" + UUID.randomUUID() + "}");
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Object_ID
     *
     * @return the value of t_attribute.Object_ID
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public Integer getObject_ID() {
        return object_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Object_ID
     *
     * @param object_ID the value for t_attribute.Object_ID
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setObject_ID(Integer object_ID) {
        this.object_ID = object_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Name
     *
     * @return the value of t_attribute.Name
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Name
     *
     * @param name the value for t_attribute.Name
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Scope
     *
     * @return the value of t_attribute.Scope
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getScope() {
        return scope;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Scope
     *
     * @param scope the value for t_attribute.Scope
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setScope(String scope) {
        this.scope = scope == null ? null : scope.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Stereotype
     *
     * @return the value of t_attribute.Stereotype
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getStereotype() {
        return stereotype;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Stereotype
     *
     * @param stereotype the value for t_attribute.Stereotype
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setStereotype(String stereotype) {
        this.stereotype = stereotype == null ? null : stereotype.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Containment
     *
     * @return the value of t_attribute.Containment
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getContainment() {
        return containment;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Containment
     *
     * @param containment the value for t_attribute.Containment
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setContainment(String containment) {
        this.containment = containment == null ? null : containment.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.IsStatic
     *
     * @return the value of t_attribute.IsStatic
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public Integer getIsStatic() {
        return isStatic;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.IsStatic
     *
     * @param isStatic the value for t_attribute.IsStatic
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setIsStatic(Integer isStatic) {
        this.isStatic = isStatic;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.IsCollection
     *
     * @return the value of t_attribute.IsCollection
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public Integer getIsCollection() {
        return isCollection;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.IsCollection
     *
     * @param isCollection the value for t_attribute.IsCollection
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setIsCollection(Integer isCollection) {
        this.isCollection = isCollection;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.IsOrdered
     *
     * @return the value of t_attribute.IsOrdered
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public Integer getIsOrdered() {
        return isOrdered;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.IsOrdered
     *
     * @param isOrdered the value for t_attribute.IsOrdered
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setIsOrdered(Integer isOrdered) {
        this.isOrdered = isOrdered;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.AllowDuplicates
     *
     * @return the value of t_attribute.AllowDuplicates
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public Integer getAllowDuplicates() {
        return allowDuplicates;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.AllowDuplicates
     *
     * @param allowDuplicates the value for t_attribute.AllowDuplicates
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setAllowDuplicates(Integer allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.LowerBound
     *
     * @return the value of t_attribute.LowerBound
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getLowerBound() {
        return lowerBound;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.LowerBound
     *
     * @param lowerBound the value for t_attribute.LowerBound
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setLowerBound(String lowerBound) {
        this.lowerBound = lowerBound == null ? null : lowerBound.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.UpperBound
     *
     * @return the value of t_attribute.UpperBound
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getUpperBound() {
        return upperBound;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.UpperBound
     *
     * @param upperBound the value for t_attribute.UpperBound
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setUpperBound(String upperBound) {
        this.upperBound = upperBound == null ? null : upperBound.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Container
     *
     * @return the value of t_attribute.Container
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getContainer() {
        return container;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Container
     *
     * @param container the value for t_attribute.Container
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setContainer(String container) {
        this.container = container == null ? null : container.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Derived
     *
     * @return the value of t_attribute.Derived
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getDerived() {
        return derived;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Derived
     *
     * @param derived the value for t_attribute.Derived
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setDerived(String derived) {
        this.derived = derived == null ? null : derived.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.ID
     *
     * @return the value of t_attribute.ID
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public Integer getID() {
        return ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.ID
     *
     * @param ID the value for t_attribute.ID
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Pos
     *
     * @return the value of t_attribute.Pos
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public Integer getPos() {
        return pos;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Pos
     *
     * @param pos the value for t_attribute.Pos
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setPos(Integer pos) {
        this.pos = pos;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Length
     *
     * @return the value of t_attribute.Length
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public Integer getLength() {
        return length;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Length
     *
     * @param length the value for t_attribute.Length
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Precision
     *
     * @return the value of t_attribute.Precision
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public Integer getPrecision() {
        return precision == null ? 0 : precision;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Precision
     *
     * @param precision the value for t_attribute.Precision
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Scale
     *
     * @return the value of t_attribute.Scale
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public Integer getScale() {
        return scale == null ? 0 : scale;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Scale
     *
     * @param scale the value for t_attribute.Scale
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setScale(Integer scale) {
        this.scale = scale;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Const
     *
     * @return the value of t_attribute.Const
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public Integer getConstant() {
        return constant == null ? 0 : constant;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Const
     *
     * @param constant the value for t_attribute.Const
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setConstant(Integer constant) {
        this.constant = constant;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Style
     *
     * @return the value of t_attribute.Style
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getStyle() {
        return style;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Style
     *
     * @param style the value for t_attribute.Style
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setStyle(String style) {
        this.style = style == null ? null : style.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Classifier
     *
     * @return the value of t_attribute.Classifier
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getClassifier() {
        return classifier;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Classifier
     *
     * @param classifier the value for t_attribute.Classifier
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setClassifier(String classifier) {
        this.classifier = classifier == null ? null : classifier.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Type
     *
     * @return the value of t_attribute.Type
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Type
     *
     * @param type the value for t_attribute.Type
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.ea_guid
     *
     * @return the value of t_attribute.ea_guid
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getEa_guid() {
        return ea_guid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.ea_guid
     *
     * @param ea_guid the value for t_attribute.ea_guid
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setEa_guid(String ea_guid) {
        this.ea_guid = ea_guid == null ? null : ea_guid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Default
     *
     * @return the value of t_attribute.Default
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getDefaultVal() {
        return defaultVal;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.GenOption
     *
     * @return the value of t_attribute.GenOption
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getGenOption() {
        return genOption;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.Notes
     *
     * @return the value of t_attribute.Notes
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getNotes() {
        return notes;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_attribute.StyleEx
     *
     * @return the value of t_attribute.StyleEx
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public String getStyleEx() {
        return styleEx;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Default
     *
     * @param defaultVal the value for t_attribute.Default
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setDefaultVal(String defaultVal) {
        this.defaultVal = defaultVal == null ? null : defaultVal.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.GenOption
     *
     * @param genOption the value for t_attribute.GenOption
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setGenOption(String genOption) {
        this.genOption = genOption == null ? null : genOption.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.Notes
     *
     * @param notes the value for t_attribute.Notes
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setNotes(String notes) {
        this.notes = notes == null ? null : notes.trim();
        if(this.notes != null) {
            this.notes.replace("'", "\'"); // escape quote
            this.notes.replace("", "\""); // escape quote
        }
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_attribute.StyleEx
     *
     * @param styleEx the value for t_attribute.StyleEx
     *
     * @mbggenerated Thu Feb 16 09:50:26 NZDT 2012
     */
    public void setStyleEx(String styleEx) {
        this.styleEx = styleEx == null ? null : styleEx.trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TAttribute other = (TAttribute) obj;
        if ((this.ea_guid == null) ? (other.ea_guid != null) : !this.ea_guid.equals(other.ea_guid)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.ea_guid != null ? this.ea_guid.hashCode() : 0);
        return hash;
    }
    
    
}