package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import org.jooq.util.jaxb.tools.StringAdapter;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Parameter", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/Parameter.class */
public class Parameter implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "specific_catalog")
    protected String specificCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "specific_schema")
    protected String specificSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "specific_package")
    protected String specificPackage;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "specific_name", required = true)
    protected String specificName;

    @XmlElement(name = "ordinal_position")
    protected int ordinalPosition;

    @XmlSchemaType(name = "string")
    @XmlElement(name = "parameter_mode", required = true)
    protected ParameterMode parameterMode;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "parameter_name")
    protected String parameterName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "data_type")
    protected String dataType;

    @XmlElement(name = "character_maximum_length")
    protected Integer characterMaximumLength;

    @XmlElement(name = "numeric_precision")
    protected Integer numericPrecision;

    @XmlElement(name = "numeric_scale")
    protected Integer numericScale;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "udt_catalog")
    protected String udtCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "udt_schema")
    protected String udtSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "udt_name")
    protected String udtName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "parameter_default")
    protected String parameterDefault;

    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String comment;

    public String getSpecificCatalog() {
        return this.specificCatalog;
    }

    public void setSpecificCatalog(String value) {
        this.specificCatalog = value;
    }

    public String getSpecificSchema() {
        return this.specificSchema;
    }

    public void setSpecificSchema(String value) {
        this.specificSchema = value;
    }

    public String getSpecificPackage() {
        return this.specificPackage;
    }

    public void setSpecificPackage(String value) {
        this.specificPackage = value;
    }

    public String getSpecificName() {
        return this.specificName;
    }

    public void setSpecificName(String value) {
        this.specificName = value;
    }

    public int getOrdinalPosition() {
        return this.ordinalPosition;
    }

    public void setOrdinalPosition(int value) {
        this.ordinalPosition = value;
    }

    public ParameterMode getParameterMode() {
        return this.parameterMode;
    }

    public void setParameterMode(ParameterMode value) {
        this.parameterMode = value;
    }

    public String getParameterName() {
        return this.parameterName;
    }

    public void setParameterName(String value) {
        this.parameterName = value;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String value) {
        this.dataType = value;
    }

    public Integer getCharacterMaximumLength() {
        return this.characterMaximumLength;
    }

    public void setCharacterMaximumLength(Integer value) {
        this.characterMaximumLength = value;
    }

    public Integer getNumericPrecision() {
        return this.numericPrecision;
    }

    public void setNumericPrecision(Integer value) {
        this.numericPrecision = value;
    }

    public Integer getNumericScale() {
        return this.numericScale;
    }

    public void setNumericScale(Integer value) {
        this.numericScale = value;
    }

    public String getUdtCatalog() {
        return this.udtCatalog;
    }

    public void setUdtCatalog(String value) {
        this.udtCatalog = value;
    }

    public String getUdtSchema() {
        return this.udtSchema;
    }

    public void setUdtSchema(String value) {
        this.udtSchema = value;
    }

    public String getUdtName() {
        return this.udtName;
    }

    public void setUdtName(String value) {
        this.udtName = value;
    }

    public String getParameterDefault() {
        return this.parameterDefault;
    }

    public void setParameterDefault(String value) {
        this.parameterDefault = value;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public Parameter withSpecificCatalog(String value) {
        setSpecificCatalog(value);
        return this;
    }

    public Parameter withSpecificSchema(String value) {
        setSpecificSchema(value);
        return this;
    }

    public Parameter withSpecificPackage(String value) {
        setSpecificPackage(value);
        return this;
    }

    public Parameter withSpecificName(String value) {
        setSpecificName(value);
        return this;
    }

    public Parameter withOrdinalPosition(int value) {
        setOrdinalPosition(value);
        return this;
    }

    public Parameter withParameterMode(ParameterMode value) {
        setParameterMode(value);
        return this;
    }

    public Parameter withParameterName(String value) {
        setParameterName(value);
        return this;
    }

    public Parameter withDataType(String value) {
        setDataType(value);
        return this;
    }

    public Parameter withCharacterMaximumLength(Integer value) {
        setCharacterMaximumLength(value);
        return this;
    }

    public Parameter withNumericPrecision(Integer value) {
        setNumericPrecision(value);
        return this;
    }

    public Parameter withNumericScale(Integer value) {
        setNumericScale(value);
        return this;
    }

    public Parameter withUdtCatalog(String value) {
        setUdtCatalog(value);
        return this;
    }

    public Parameter withUdtSchema(String value) {
        setUdtSchema(value);
        return this;
    }

    public Parameter withUdtName(String value) {
        setUdtName(value);
        return this;
    }

    public Parameter withParameterDefault(String value) {
        setParameterDefault(value);
        return this;
    }

    public Parameter withComment(String value) {
        setComment(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("specific_catalog", this.specificCatalog);
        builder.append("specific_schema", this.specificSchema);
        builder.append("specific_package", this.specificPackage);
        builder.append("specific_name", this.specificName);
        builder.append("ordinal_position", this.ordinalPosition);
        builder.append("parameter_mode", this.parameterMode);
        builder.append("parameter_name", this.parameterName);
        builder.append("data_type", this.dataType);
        builder.append("character_maximum_length", this.characterMaximumLength);
        builder.append("numeric_precision", this.numericPrecision);
        builder.append("numeric_scale", this.numericScale);
        builder.append("udt_catalog", this.udtCatalog);
        builder.append("udt_schema", this.udtSchema);
        builder.append("udt_name", this.udtName);
        builder.append("parameter_default", this.parameterDefault);
        builder.append("comment", this.comment);
    }

    public String toString() {
        XMLBuilder builder = XMLBuilder.nonFormatting();
        appendTo(builder);
        return builder.toString();
    }

    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }
        Parameter other = (Parameter) that;
        if (this.specificCatalog == null) {
            if (other.specificCatalog != null) {
                return false;
            }
        } else if (!this.specificCatalog.equals(other.specificCatalog)) {
            return false;
        }
        if (this.specificSchema == null) {
            if (other.specificSchema != null) {
                return false;
            }
        } else if (!this.specificSchema.equals(other.specificSchema)) {
            return false;
        }
        if (this.specificPackage == null) {
            if (other.specificPackage != null) {
                return false;
            }
        } else if (!this.specificPackage.equals(other.specificPackage)) {
            return false;
        }
        if (this.specificName == null) {
            if (other.specificName != null) {
                return false;
            }
        } else if (!this.specificName.equals(other.specificName)) {
            return false;
        }
        if (this.ordinalPosition != other.ordinalPosition) {
            return false;
        }
        if (this.parameterMode == null) {
            if (other.parameterMode != null) {
                return false;
            }
        } else if (!this.parameterMode.equals(other.parameterMode)) {
            return false;
        }
        if (this.parameterName == null) {
            if (other.parameterName != null) {
                return false;
            }
        } else if (!this.parameterName.equals(other.parameterName)) {
            return false;
        }
        if (this.dataType == null) {
            if (other.dataType != null) {
                return false;
            }
        } else if (!this.dataType.equals(other.dataType)) {
            return false;
        }
        if (this.characterMaximumLength == null) {
            if (other.characterMaximumLength != null) {
                return false;
            }
        } else if (!this.characterMaximumLength.equals(other.characterMaximumLength)) {
            return false;
        }
        if (this.numericPrecision == null) {
            if (other.numericPrecision != null) {
                return false;
            }
        } else if (!this.numericPrecision.equals(other.numericPrecision)) {
            return false;
        }
        if (this.numericScale == null) {
            if (other.numericScale != null) {
                return false;
            }
        } else if (!this.numericScale.equals(other.numericScale)) {
            return false;
        }
        if (this.udtCatalog == null) {
            if (other.udtCatalog != null) {
                return false;
            }
        } else if (!this.udtCatalog.equals(other.udtCatalog)) {
            return false;
        }
        if (this.udtSchema == null) {
            if (other.udtSchema != null) {
                return false;
            }
        } else if (!this.udtSchema.equals(other.udtSchema)) {
            return false;
        }
        if (this.udtName == null) {
            if (other.udtName != null) {
                return false;
            }
        } else if (!this.udtName.equals(other.udtName)) {
            return false;
        }
        if (this.parameterDefault == null) {
            if (other.parameterDefault != null) {
                return false;
            }
        } else if (!this.parameterDefault.equals(other.parameterDefault)) {
            return false;
        }
        if (this.comment == null) {
            if (other.comment != null) {
                return false;
            }
            return true;
        }
        if (!this.comment.equals(other.comment)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.specificCatalog == null ? 0 : this.specificCatalog.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.specificSchema == null ? 0 : this.specificSchema.hashCode()))) + (this.specificPackage == null ? 0 : this.specificPackage.hashCode()))) + (this.specificName == null ? 0 : this.specificName.hashCode()))) + this.ordinalPosition)) + (this.parameterMode == null ? 0 : this.parameterMode.hashCode()))) + (this.parameterName == null ? 0 : this.parameterName.hashCode()))) + (this.dataType == null ? 0 : this.dataType.hashCode()))) + (this.characterMaximumLength == null ? 0 : this.characterMaximumLength.hashCode()))) + (this.numericPrecision == null ? 0 : this.numericPrecision.hashCode()))) + (this.numericScale == null ? 0 : this.numericScale.hashCode()))) + (this.udtCatalog == null ? 0 : this.udtCatalog.hashCode()))) + (this.udtSchema == null ? 0 : this.udtSchema.hashCode()))) + (this.udtName == null ? 0 : this.udtName.hashCode()))) + (this.parameterDefault == null ? 0 : this.parameterDefault.hashCode()))) + (this.comment == null ? 0 : this.comment.hashCode());
    }
}
