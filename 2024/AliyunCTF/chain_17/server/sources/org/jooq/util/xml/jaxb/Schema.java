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
@XmlType(name = "Schema", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/Schema.class */
public class Schema implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "catalog_name")
    protected String catalogName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "schema_name", required = true)
    protected String schemaName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String comment;

    public String getCatalogName() {
        return this.catalogName;
    }

    public void setCatalogName(String value) {
        this.catalogName = value;
    }

    public String getSchemaName() {
        return this.schemaName;
    }

    public void setSchemaName(String value) {
        this.schemaName = value;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public Schema withCatalogName(String value) {
        setCatalogName(value);
        return this;
    }

    public Schema withSchemaName(String value) {
        setSchemaName(value);
        return this;
    }

    public Schema withComment(String value) {
        setComment(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("catalog_name", this.catalogName);
        builder.append("schema_name", this.schemaName);
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
        Schema other = (Schema) that;
        if (this.catalogName == null) {
            if (other.catalogName != null) {
                return false;
            }
        } else if (!this.catalogName.equals(other.catalogName)) {
            return false;
        }
        if (this.schemaName == null) {
            if (other.schemaName != null) {
                return false;
            }
        } else if (!this.schemaName.equals(other.schemaName)) {
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
        int result = (31 * 1) + (this.catalogName == null ? 0 : this.catalogName.hashCode());
        return (31 * ((31 * result) + (this.schemaName == null ? 0 : this.schemaName.hashCode()))) + (this.comment == null ? 0 : this.comment.hashCode());
    }
}
