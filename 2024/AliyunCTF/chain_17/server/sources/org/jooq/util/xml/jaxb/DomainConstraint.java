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
@XmlType(name = "DomainConstraint", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/DomainConstraint.class */
public class DomainConstraint implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "constraint_catalog")
    protected String constraintCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "constraint_schema")
    protected String constraintSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "constraint_name", required = true)
    protected String constraintName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "domain_catalog")
    protected String domainCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "domain_schema")
    protected String domainSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "domain_name", required = true)
    protected String domainName;

    public String getConstraintCatalog() {
        return this.constraintCatalog;
    }

    public void setConstraintCatalog(String value) {
        this.constraintCatalog = value;
    }

    public String getConstraintSchema() {
        return this.constraintSchema;
    }

    public void setConstraintSchema(String value) {
        this.constraintSchema = value;
    }

    public String getConstraintName() {
        return this.constraintName;
    }

    public void setConstraintName(String value) {
        this.constraintName = value;
    }

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

    public DomainConstraint withConstraintCatalog(String value) {
        setConstraintCatalog(value);
        return this;
    }

    public DomainConstraint withConstraintSchema(String value) {
        setConstraintSchema(value);
        return this;
    }

    public DomainConstraint withConstraintName(String value) {
        setConstraintName(value);
        return this;
    }

    public DomainConstraint withDomainCatalog(String value) {
        setDomainCatalog(value);
        return this;
    }

    public DomainConstraint withDomainSchema(String value) {
        setDomainSchema(value);
        return this;
    }

    public DomainConstraint withDomainName(String value) {
        setDomainName(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("constraint_catalog", this.constraintCatalog);
        builder.append("constraint_schema", this.constraintSchema);
        builder.append("constraint_name", this.constraintName);
        builder.append("domain_catalog", this.domainCatalog);
        builder.append("domain_schema", this.domainSchema);
        builder.append("domain_name", this.domainName);
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
        DomainConstraint other = (DomainConstraint) that;
        if (this.constraintCatalog == null) {
            if (other.constraintCatalog != null) {
                return false;
            }
        } else if (!this.constraintCatalog.equals(other.constraintCatalog)) {
            return false;
        }
        if (this.constraintSchema == null) {
            if (other.constraintSchema != null) {
                return false;
            }
        } else if (!this.constraintSchema.equals(other.constraintSchema)) {
            return false;
        }
        if (this.constraintName == null) {
            if (other.constraintName != null) {
                return false;
            }
        } else if (!this.constraintName.equals(other.constraintName)) {
            return false;
        }
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
            return true;
        }
        if (!this.domainName.equals(other.domainName)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.constraintCatalog == null ? 0 : this.constraintCatalog.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * result) + (this.constraintSchema == null ? 0 : this.constraintSchema.hashCode()))) + (this.constraintName == null ? 0 : this.constraintName.hashCode()))) + (this.domainCatalog == null ? 0 : this.domainCatalog.hashCode()))) + (this.domainSchema == null ? 0 : this.domainSchema.hashCode()))) + (this.domainName == null ? 0 : this.domainName.hashCode());
    }
}
