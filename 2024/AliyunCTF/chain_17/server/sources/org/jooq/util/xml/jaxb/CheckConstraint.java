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
@XmlType(name = "CheckConstraint", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/CheckConstraint.class */
public class CheckConstraint implements Serializable, XMLAppendable {
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
    @XmlElement(name = "check_clause", required = true)
    protected String checkClause;

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

    public String getCheckClause() {
        return this.checkClause;
    }

    public void setCheckClause(String value) {
        this.checkClause = value;
    }

    public CheckConstraint withConstraintCatalog(String value) {
        setConstraintCatalog(value);
        return this;
    }

    public CheckConstraint withConstraintSchema(String value) {
        setConstraintSchema(value);
        return this;
    }

    public CheckConstraint withConstraintName(String value) {
        setConstraintName(value);
        return this;
    }

    public CheckConstraint withCheckClause(String value) {
        setCheckClause(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("constraint_catalog", this.constraintCatalog);
        builder.append("constraint_schema", this.constraintSchema);
        builder.append("constraint_name", this.constraintName);
        builder.append("check_clause", this.checkClause);
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
        CheckConstraint other = (CheckConstraint) that;
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
        if (this.checkClause == null) {
            if (other.checkClause != null) {
                return false;
            }
            return true;
        }
        if (!this.checkClause.equals(other.checkClause)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.constraintCatalog == null ? 0 : this.constraintCatalog.hashCode());
        return (31 * ((31 * ((31 * result) + (this.constraintSchema == null ? 0 : this.constraintSchema.hashCode()))) + (this.constraintName == null ? 0 : this.constraintName.hashCode()))) + (this.checkClause == null ? 0 : this.checkClause.hashCode());
    }
}
