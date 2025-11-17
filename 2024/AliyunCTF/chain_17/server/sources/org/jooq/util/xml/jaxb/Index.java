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
@XmlType(name = "Index", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/Index.class */
public class Index implements Serializable, XMLAppendable {
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

    @XmlElement(name = "is_unique")
    protected Boolean isUnique;

    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String comment;

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

    public Boolean isIsUnique() {
        return this.isUnique;
    }

    public void setIsUnique(Boolean value) {
        this.isUnique = value;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public Index withIndexCatalog(String value) {
        setIndexCatalog(value);
        return this;
    }

    public Index withIndexSchema(String value) {
        setIndexSchema(value);
        return this;
    }

    public Index withIndexName(String value) {
        setIndexName(value);
        return this;
    }

    public Index withTableCatalog(String value) {
        setTableCatalog(value);
        return this;
    }

    public Index withTableSchema(String value) {
        setTableSchema(value);
        return this;
    }

    public Index withTableName(String value) {
        setTableName(value);
        return this;
    }

    public Index withIsUnique(Boolean value) {
        setIsUnique(value);
        return this;
    }

    public Index withComment(String value) {
        setComment(value);
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
        builder.append("is_unique", this.isUnique);
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
        Index other = (Index) that;
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
        if (this.isUnique == null) {
            if (other.isUnique != null) {
                return false;
            }
        } else if (!this.isUnique.equals(other.isUnique)) {
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
        int result = (31 * 1) + (this.indexCatalog == null ? 0 : this.indexCatalog.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.indexSchema == null ? 0 : this.indexSchema.hashCode()))) + (this.indexName == null ? 0 : this.indexName.hashCode()))) + (this.tableCatalog == null ? 0 : this.tableCatalog.hashCode()))) + (this.tableSchema == null ? 0 : this.tableSchema.hashCode()))) + (this.tableName == null ? 0 : this.tableName.hashCode()))) + (this.isUnique == null ? 0 : this.isUnique.hashCode()))) + (this.comment == null ? 0 : this.comment.hashCode());
    }
}
