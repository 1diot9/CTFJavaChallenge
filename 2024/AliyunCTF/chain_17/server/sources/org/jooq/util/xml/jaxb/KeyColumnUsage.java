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
@XmlType(name = "KeyColumnUsage", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/KeyColumnUsage.class */
public class KeyColumnUsage implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "column_name", required = true)
    protected String columnName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "constraint_catalog")
    protected String constraintCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "constraint_schema")
    protected String constraintSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "constraint_name", required = true)
    protected String constraintName;

    @XmlElement(name = "ordinal_position")
    protected int ordinalPosition;

    @XmlElement(name = "position_in_unique_constraint")
    protected Integer positionInUniqueConstraint;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "table_catalog")
    protected String tableCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "table_schema")
    protected String tableSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "table_name", required = true)
    protected String tableName;

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String value) {
        this.columnName = value;
    }

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

    public int getOrdinalPosition() {
        return this.ordinalPosition;
    }

    public void setOrdinalPosition(int value) {
        this.ordinalPosition = value;
    }

    public Integer getPositionInUniqueConstraint() {
        return this.positionInUniqueConstraint;
    }

    public void setPositionInUniqueConstraint(Integer value) {
        this.positionInUniqueConstraint = value;
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

    public KeyColumnUsage withColumnName(String value) {
        setColumnName(value);
        return this;
    }

    public KeyColumnUsage withConstraintCatalog(String value) {
        setConstraintCatalog(value);
        return this;
    }

    public KeyColumnUsage withConstraintSchema(String value) {
        setConstraintSchema(value);
        return this;
    }

    public KeyColumnUsage withConstraintName(String value) {
        setConstraintName(value);
        return this;
    }

    public KeyColumnUsage withOrdinalPosition(int value) {
        setOrdinalPosition(value);
        return this;
    }

    public KeyColumnUsage withPositionInUniqueConstraint(Integer value) {
        setPositionInUniqueConstraint(value);
        return this;
    }

    public KeyColumnUsage withTableCatalog(String value) {
        setTableCatalog(value);
        return this;
    }

    public KeyColumnUsage withTableSchema(String value) {
        setTableSchema(value);
        return this;
    }

    public KeyColumnUsage withTableName(String value) {
        setTableName(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("column_name", this.columnName);
        builder.append("constraint_catalog", this.constraintCatalog);
        builder.append("constraint_schema", this.constraintSchema);
        builder.append("constraint_name", this.constraintName);
        builder.append("ordinal_position", this.ordinalPosition);
        builder.append("position_in_unique_constraint", this.positionInUniqueConstraint);
        builder.append("table_catalog", this.tableCatalog);
        builder.append("table_schema", this.tableSchema);
        builder.append("table_name", this.tableName);
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
        KeyColumnUsage other = (KeyColumnUsage) that;
        if (this.columnName == null) {
            if (other.columnName != null) {
                return false;
            }
        } else if (!this.columnName.equals(other.columnName)) {
            return false;
        }
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
        if (this.ordinalPosition != other.ordinalPosition) {
            return false;
        }
        if (this.positionInUniqueConstraint == null) {
            if (other.positionInUniqueConstraint != null) {
                return false;
            }
        } else if (!this.positionInUniqueConstraint.equals(other.positionInUniqueConstraint)) {
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
            return true;
        }
        if (!this.tableName.equals(other.tableName)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.columnName == null ? 0 : this.columnName.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.constraintCatalog == null ? 0 : this.constraintCatalog.hashCode()))) + (this.constraintSchema == null ? 0 : this.constraintSchema.hashCode()))) + (this.constraintName == null ? 0 : this.constraintName.hashCode()))) + this.ordinalPosition)) + (this.positionInUniqueConstraint == null ? 0 : this.positionInUniqueConstraint.hashCode()))) + (this.tableCatalog == null ? 0 : this.tableCatalog.hashCode()))) + (this.tableSchema == null ? 0 : this.tableSchema.hashCode()))) + (this.tableName == null ? 0 : this.tableName.hashCode());
    }
}
