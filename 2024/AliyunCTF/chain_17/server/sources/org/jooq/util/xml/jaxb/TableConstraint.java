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
@XmlType(name = "TableConstraint", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/TableConstraint.class */
public class TableConstraint implements Serializable, XMLAppendable {
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

    @XmlSchemaType(name = "string")
    @XmlElement(name = "constraint_type", required = true)
    protected TableConstraintType constraintType;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "table_catalog")
    protected String tableCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "table_schema")
    protected String tableSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "table_name", required = true)
    protected String tableName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String comment;
    protected Boolean enforced;

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

    public TableConstraintType getConstraintType() {
        return this.constraintType;
    }

    public void setConstraintType(TableConstraintType value) {
        this.constraintType = value;
    }

    public String getTableCatalog() {
        return this.tableCatalog;
    }

    public void setTableCatalog(String value) {
        this.tableCatalog = value;
    }

    public String getTableSchema() {
        return this.tableSchema;
    }

    public void setTableSchema(String value) {
        this.tableSchema = value;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String value) {
        this.tableName = value;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public Boolean isEnforced() {
        return this.enforced;
    }

    public void setEnforced(Boolean value) {
        this.enforced = value;
    }

    public TableConstraint withConstraintCatalog(String value) {
        setConstraintCatalog(value);
        return this;
    }

    public TableConstraint withConstraintSchema(String value) {
        setConstraintSchema(value);
        return this;
    }

    public TableConstraint withConstraintName(String value) {
        setConstraintName(value);
        return this;
    }

    public TableConstraint withConstraintType(TableConstraintType value) {
        setConstraintType(value);
        return this;
    }

    public TableConstraint withTableCatalog(String value) {
        setTableCatalog(value);
        return this;
    }

    public TableConstraint withTableSchema(String value) {
        setTableSchema(value);
        return this;
    }

    public TableConstraint withTableName(String value) {
        setTableName(value);
        return this;
    }

    public TableConstraint withComment(String value) {
        setComment(value);
        return this;
    }

    public TableConstraint withEnforced(Boolean value) {
        setEnforced(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("constraint_catalog", this.constraintCatalog);
        builder.append("constraint_schema", this.constraintSchema);
        builder.append("constraint_name", this.constraintName);
        builder.append("constraint_type", this.constraintType);
        builder.append("table_catalog", this.tableCatalog);
        builder.append("table_schema", this.tableSchema);
        builder.append("table_name", this.tableName);
        builder.append("comment", this.comment);
        builder.append("enforced", this.enforced);
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
        TableConstraint other = (TableConstraint) that;
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
        if (this.constraintType == null) {
            if (other.constraintType != null) {
                return false;
            }
        } else if (!this.constraintType.equals(other.constraintType)) {
            return false;
        }
        if (this.tableCatalog == null) {
            if (other.tableCatalog != null) {
                return false;
            }
        } else if (!this.tableCatalog.equals(other.tableCatalog)) {
            return false;
        }
        if (this.tableSchema == null) {
            if (other.tableSchema != null) {
                return false;
            }
        } else if (!this.tableSchema.equals(other.tableSchema)) {
            return false;
        }
        if (this.tableName == null) {
            if (other.tableName != null) {
                return false;
            }
        } else if (!this.tableName.equals(other.tableName)) {
            return false;
        }
        if (this.comment == null) {
            if (other.comment != null) {
                return false;
            }
        } else if (!this.comment.equals(other.comment)) {
            return false;
        }
        if (this.enforced == null) {
            if (other.enforced != null) {
                return false;
            }
            return true;
        }
        if (!this.enforced.equals(other.enforced)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.constraintCatalog == null ? 0 : this.constraintCatalog.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.constraintSchema == null ? 0 : this.constraintSchema.hashCode()))) + (this.constraintName == null ? 0 : this.constraintName.hashCode()))) + (this.constraintType == null ? 0 : this.constraintType.hashCode()))) + (this.tableCatalog == null ? 0 : this.tableCatalog.hashCode()))) + (this.tableSchema == null ? 0 : this.tableSchema.hashCode()))) + (this.tableName == null ? 0 : this.tableName.hashCode()))) + (this.comment == null ? 0 : this.comment.hashCode()))) + (this.enforced == null ? 0 : this.enforced.hashCode());
    }
}
