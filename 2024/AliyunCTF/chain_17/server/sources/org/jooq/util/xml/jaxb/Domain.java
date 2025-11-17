package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import org.apache.tomcat.util.descriptor.web.Constants;
import org.jooq.util.jaxb.tools.StringAdapter;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = Constants.COOKIE_DOMAIN_ATTR, propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/Domain.class */
public class Domain implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "domain_catalog")
    protected String domainCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "domain_schema")
    protected String domainSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "domain_name", required = true)
    protected String domainName;

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
    @XmlElement(name = "domain_default")
    protected String domainDefault;

    public String getDomainCatalog() {
        return this.domainCatalog;
    }

    public void setDomainCatalog(String value) {
        this.domainCatalog = value;
    }

    public String getDomainSchema() {
        return this.domainSchema;
    }

    public void setDomainSchema(String value) {
        this.domainSchema = value;
    }

    public String getDomainName() {
        return this.domainName;
    }

    public void setDomainName(String value) {
        this.domainName = value;
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

    public String getDomainDefault() {
        return this.domainDefault;
    }

    public void setDomainDefault(String value) {
        this.domainDefault = value;
    }

    public Domain withDomainCatalog(String value) {
        setDomainCatalog(value);
        return this;
    }

    public Domain withDomainSchema(String value) {
        setDomainSchema(value);
        return this;
    }

    public Domain withDomainName(String value) {
        setDomainName(value);
        return this;
    }

    public Domain withDataType(String value) {
        setDataType(value);
        return this;
    }

    public Domain withCharacterMaximumLength(Integer value) {
        setCharacterMaximumLength(value);
        return this;
    }

    public Domain withNumericPrecision(Integer value) {
        setNumericPrecision(value);
        return this;
    }

    public Domain withNumericScale(Integer value) {
        setNumericScale(value);
        return this;
    }

    public Domain withDomainDefault(String value) {
        setDomainDefault(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("domain_catalog", this.domainCatalog);
        builder.append("domain_schema", this.domainSchema);
        builder.append("domain_name", this.domainName);
        builder.append("data_type", this.dataType);
        builder.append("character_maximum_length", this.characterMaximumLength);
        builder.append("numeric_precision", this.numericPrecision);
        builder.append("numeric_scale", this.numericScale);
        builder.append("domain_default", this.domainDefault);
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
        Domain other = (Domain) that;
        if (this.domainCatalog == null) {
            if (other.domainCatalog != null) {
                return false;
            }
        } else if (!this.domainCatalog.equals(other.domainCatalog)) {
            return false;
        }
        if (this.domainSchema == null) {
            if (other.domainSchema != null) {
                return false;
            }
        } else if (!this.domainSchema.equals(other.domainSchema)) {
            return false;
        }
        if (this.domainName == null) {
            if (other.domainName != null) {
                return false;
            }
        } else if (!this.domainName.equals(other.domainName)) {
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
        if (this.domainDefault == null) {
            if (other.domainDefault != null) {
                return false;
            }
            return true;
        }
        if (!this.domainDefault.equals(other.domainDefault)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.domainCatalog == null ? 0 : this.domainCatalog.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.domainSchema == null ? 0 : this.domainSchema.hashCode()))) + (this.domainName == null ? 0 : this.domainName.hashCode()))) + (this.dataType == null ? 0 : this.dataType.hashCode()))) + (this.characterMaximumLength == null ? 0 : this.characterMaximumLength.hashCode()))) + (this.numericPrecision == null ? 0 : this.numericPrecision.hashCode()))) + (this.numericScale == null ? 0 : this.numericScale.hashCode()))) + (this.domainDefault == null ? 0 : this.domainDefault.hashCode());
    }
}
