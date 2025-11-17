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
@XmlType(name = "ReferentialConstraint", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/ReferentialConstraint.class */
public class ReferentialConstraint implements Serializable, XMLAppendable {
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
    @XmlElement(name = "unique_constraint_catalog")
    protected String uniqueConstraintCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "unique_constraint_schema")
    protected String uniqueConstraintSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "unique_constraint_name", required = true)
    protected String uniqueConstraintName;

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

    public String getUniqueConstraintCatalog() {
        return this.uniqueConstraintCatalog;
    }

    public void setUniqueConstraintCatalog(String value) {
        this.uniqueConstraintCatalog = value;
    }

    public String getUniqueConstraintSchema() {
        return this.uniqueConstraintSchema;
    }

    public void setUniqueConstraintSchema(String value) {
        this.uniqueConstraintSchema = value;
    }

    public String getUniqueConstraintName() {
        return this.uniqueConstraintName;
    }

    public void setUniqueConstraintName(String value) {
        this.uniqueConstraintName = value;
    }

    public ReferentialConstraint withConstraintCatalog(String value) {
        setConstraintCatalog(value);
        return this;
    }

    public ReferentialConstraint withConstraintSchema(String value) {
        setConstraintSchema(value);
        return this;
    }

    public ReferentialConstraint withConstraintName(String value) {
        setConstraintName(value);
        return this;
    }

    public ReferentialConstraint withUniqueConstraintCatalog(String value) {
        setUniqueConstraintCatalog(value);
        return this;
    }

    public ReferentialConstraint withUniqueConstraintSchema(String value) {
        setUniqueConstraintSchema(value);
        return this;
    }

    public ReferentialConstraint withUniqueConstraintName(String value) {
        setUniqueConstraintName(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("constraint_catalog", this.constraintCatalog);
        builder.append("constraint_schema", this.constraintSchema);
        builder.append("constraint_name", this.constraintName);
        builder.append("unique_constraint_catalog", this.uniqueConstraintCatalog);
        builder.append("unique_constraint_schema", this.uniqueConstraintSchema);
        builder.append("unique_constraint_name", this.uniqueConstraintName);
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
        ReferentialConstraint other = (ReferentialConstraint) that;
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
        if (this.uniqueConstraintCatalog == null) {
            if (other.uniqueConstraintCatalog != null) {
                return false;
            }
        } else if (!this.uniqueConstraintCatalog.equals(other.uniqueConstraintCatalog)) {
            return false;
        }
        if (this.uniqueConstraintSchema == null) {
            if (other.uniqueConstraintSchema != null) {
                return false;
            }
        } else if (!this.uniqueConstraintSchema.equals(other.uniqueConstraintSchema)) {
            return false;
        }
        if (this.uniqueConstraintName == null) {
            if (other.uniqueConstraintName != null) {
                return false;
            }
            return true;
        }
        if (!this.uniqueConstraintName.equals(other.uniqueConstraintName)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.constraintCatalog == null ? 0 : this.constraintCatalog.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * result) + (this.constraintSchema == null ? 0 : this.constraintSchema.hashCode()))) + (this.constraintName == null ? 0 : this.constraintName.hashCode()))) + (this.uniqueConstraintCatalog == null ? 0 : this.uniqueConstraintCatalog.hashCode()))) + (this.uniqueConstraintSchema == null ? 0 : this.uniqueConstraintSchema.hashCode()))) + (this.uniqueConstraintName == null ? 0 : this.uniqueConstraintName.hashCode());
    }
}
