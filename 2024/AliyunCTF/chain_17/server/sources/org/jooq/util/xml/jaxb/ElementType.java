package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import org.jooq.util.jaxb.tools.StringAdapter;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ElementType", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/ElementType.class */
public class ElementType implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "object_catalog")
    protected String objectCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "object_schema")
    protected String objectSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "object_name", required = true)
    protected String objectName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "object_type", required = true)
    protected String objectType;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "data_type", required = true)
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

    public String getObjectCatalog() {
        return this.objectCatalog;
    }

    public void setObjectCatalog(String value) {
        this.objectCatalog = value;
    }

    public String getObjectSchema() {
        return this.objectSchema;
    }

    public void setObjectSchema(String value) {
        this.objectSchema = value;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public void setObjectName(String value) {
        this.objectName = value;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String value) {
        this.objectType = value;
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

    public ElementType withObjectCatalog(String value) {
        setObjectCatalog(value);
        return this;
    }

    public ElementType withObjectSchema(String value) {
        setObjectSchema(value);
        return this;
    }

    public ElementType withObjectName(String value) {
        setObjectName(value);
        return this;
    }

    public ElementType withObjectType(String value) {
        setObjectType(value);
        return this;
    }

    public ElementType withDataType(String value) {
        setDataType(value);
        return this;
    }

    public ElementType withCharacterMaximumLength(Integer value) {
        setCharacterMaximumLength(value);
        return this;
    }

    public ElementType withNumericPrecision(Integer value) {
        setNumericPrecision(value);
        return this;
    }

    public ElementType withNumericScale(Integer value) {
        setNumericScale(value);
        return this;
    }

    public ElementType withUdtCatalog(String value) {
        setUdtCatalog(value);
        return this;
    }

    public ElementType withUdtSchema(String value) {
        setUdtSchema(value);
        return this;
    }

    public ElementType withUdtName(String value) {
        setUdtName(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("object_catalog", this.objectCatalog);
        builder.append("object_schema", this.objectSchema);
        builder.append("object_name", this.objectName);
        builder.append("object_type", this.objectType);
        builder.append("data_type", this.dataType);
        builder.append("character_maximum_length", this.characterMaximumLength);
        builder.append("numeric_precision", this.numericPrecision);
        builder.append("numeric_scale", this.numericScale);
        builder.append("udt_catalog", this.udtCatalog);
        builder.append("udt_schema", this.udtSchema);
        builder.append("udt_name", this.udtName);
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
        ElementType other = (ElementType) that;
        if (this.objectCatalog == null) {
            if (other.objectCatalog != null) {
                return false;
            }
        } else if (!this.objectCatalog.equals(other.objectCatalog)) {
            return false;
        }
        if (this.objectSchema == null) {
            if (other.objectSchema != null) {
                return false;
            }
        } else if (!this.objectSchema.equals(other.objectSchema)) {
            return false;
        }
        if (this.objectName == null) {
            if (other.objectName != null) {
                return false;
            }
        } else if (!this.objectName.equals(other.objectName)) {
            return false;
        }
        if (this.objectType == null) {
            if (other.objectType != null) {
                return false;
            }
        } else if (!this.objectType.equals(other.objectType)) {
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
            return true;
        }
        if (!this.udtName.equals(other.udtName)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.objectCatalog == null ? 0 : this.objectCatalog.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.objectSchema == null ? 0 : this.objectSchema.hashCode()))) + (this.objectName == null ? 0 : this.objectName.hashCode()))) + (this.objectType == null ? 0 : this.objectType.hashCode()))) + (this.dataType == null ? 0 : this.dataType.hashCode()))) + (this.characterMaximumLength == null ? 0 : this.characterMaximumLength.hashCode()))) + (this.numericPrecision == null ? 0 : this.numericPrecision.hashCode()))) + (this.numericScale == null ? 0 : this.numericScale.hashCode()))) + (this.udtCatalog == null ? 0 : this.udtCatalog.hashCode()))) + (this.udtSchema == null ? 0 : this.udtSchema.hashCode()))) + (this.udtName == null ? 0 : this.udtName.hashCode());
    }
}
