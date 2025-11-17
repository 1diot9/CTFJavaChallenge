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
@XmlType(name = "IndexColumnUsage", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/IndexColumnUsage.class */
public class IndexColumnUsage implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "index_catalog")
    protected String indexCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "index_schema")
    protected String indexSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "index_name", required = true)
    protected String indexName;

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
    @XmlElement(name = "column_name", required = true)
    protected String columnName;

    @XmlElement(name = "ordinal_position")
    protected int ordinalPosition;

    @XmlElement(name = "is_descending")
    protected Boolean isDescending;

    public String getIndexCatalog() {
        return this.indexCatalog;
    }

    public void setIndexCatalog(String value) {
        this.indexCatalog = value;
    }

    public String getIndexSchema() {
        return this.indexSchema;
    }

    public void setIndexSchema(String value) {
        this.indexSchema = value;
    }

    public String getIndexName() {
        return this.indexName;
    }

    public void setIndexName(String value) {
        this.indexName = value;
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

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String value) {
        this.columnName = value;
    }

    public int getOrdinalPosition() {
        return this.ordinalPosition;
    }

    public void setOrdinalPosition(int value) {
        this.ordinalPosition = value;
    }

    public Boolean isIsDescending() {
        return this.isDescending;
    }

    public void setIsDescending(Boolean value) {
        this.isDescending = value;
    }

    public IndexColumnUsage withIndexCatalog(String value) {
        setIndexCatalog(value);
        return this;
    }

    public IndexColumnUsage withIndexSchema(String value) {
        setIndexSchema(value);
        return this;
    }

    public IndexColumnUsage withIndexName(String value) {
        setIndexName(value);
        return this;
    }

    public IndexColumnUsage withTableCatalog(String value) {
        setTableCatalog(value);
        return this;
    }

    public IndexColumnUsage withTableSchema(String value) {
        setTableSchema(value);
        return this;
    }

    public IndexColumnUsage withTableName(String value) {
        setTableName(value);
        return this;
    }

    public IndexColumnUsage withColumnName(String value) {
        setColumnName(value);
        return this;
    }

    public IndexColumnUsage withOrdinalPosition(int value) {
        setOrdinalPosition(value);
        return this;
    }

    public IndexColumnUsage withIsDescending(Boolean value) {
        setIsDescending(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("index_catalog", this.indexCatalog);
        builder.append("index_schema", this.indexSchema);
        builder.append("index_name", this.indexName);
        builder.append("table_catalog", this.tableCatalog);
        builder.append("table_schema", this.tableSchema);
        builder.append("table_name", this.tableName);
        builder.append("column_name", this.columnName);
        builder.append("ordinal_position", this.ordinalPosition);
        builder.append("is_descending", this.isDescending);
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
        IndexColumnUsage other = (IndexColumnUsage) that;
        if (this.indexCatalog == null) {
            if (other.indexCatalog != null) {
                return false;
            }
        } else if (!this.indexCatalog.equals(other.indexCatalog)) {
            return false;
        }
        if (this.indexSchema == null) {
            if (other.indexSchema != null) {
                return false;
            }
        } else if (!this.indexSchema.equals(other.indexSchema)) {
            return false;
        }
        if (this.indexName == null) {
            if (other.indexName != null) {
                return false;
            }
        } else if (!this.indexName.equals(other.indexName)) {
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
        if (this.columnName == null) {
            if (other.columnName != null) {
                return false;
            }
        } else if (!this.columnName.equals(other.columnName)) {
            return false;
        }
        if (this.ordinalPosition != other.ordinalPosition) {
            return false;
        }
        if (this.isDescending == null) {
            if (other.isDescending != null) {
                return false;
            }
            return true;
        }
        if (!this.isDescending.equals(other.isDescending)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.indexCatalog == null ? 0 : this.indexCatalog.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.indexSchema == null ? 0 : this.indexSchema.hashCode()))) + (this.indexName == null ? 0 : this.indexName.hashCode()))) + (this.tableCatalog == null ? 0 : this.tableCatalog.hashCode()))) + (this.tableSchema == null ? 0 : this.tableSchema.hashCode()))) + (this.tableName == null ? 0 : this.tableName.hashCode()))) + (this.columnName == null ? 0 : this.columnName.hashCode()))) + this.ordinalPosition)) + (this.isDescending == null ? 0 : this.isDescending.hashCode());
    }
}
