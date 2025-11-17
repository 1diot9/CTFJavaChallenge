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
@XmlType(name = "Table", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/Table.class */
public class Table implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "table_catalog")
    protected String tableCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "table_schema")
    protected String tableSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "table_name", required = true)
    protected String tableName;

    @XmlSchemaType(name = "string")
    @XmlElement(name = "table_type", defaultValue = "BASE TABLE")
    protected TableType tableType = TableType.BASE_TABLE;

    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String comment;

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

    public TableType getTableType() {
        return this.tableType;
    }

    public void setTableType(TableType value) {
        this.tableType = value;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public Table withTableCatalog(String value) {
        setTableCatalog(value);
        return this;
    }

    public Table withTableSchema(String value) {
        setTableSchema(value);
        return this;
    }

    public Table withTableName(String value) {
        setTableName(value);
        return this;
    }

    public Table withTableType(TableType value) {
        setTableType(value);
        return this;
    }

    public Table withComment(String value) {
        setComment(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("table_catalog", this.tableCatalog);
        builder.append("table_schema", this.tableSchema);
        builder.append("table_name", this.tableName);
        builder.append("table_type", this.tableType);
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
        Table other = (Table) that;
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
        if (this.tableType == null) {
            if (other.tableType != null) {
                return false;
            }
        } else if (!this.tableType.equals(other.tableType)) {
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
        int result = (31 * 1) + (this.tableCatalog == null ? 0 : this.tableCatalog.hashCode());
        return (31 * ((31 * ((31 * ((31 * result) + (this.tableSchema == null ? 0 : this.tableSchema.hashCode()))) + (this.tableName == null ? 0 : this.tableName.hashCode()))) + (this.tableType == null ? 0 : this.tableType.hashCode()))) + (this.comment == null ? 0 : this.comment.hashCode());
    }
}
