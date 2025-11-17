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
@XmlType(name = "Catalog", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/Catalog.class */
public class Catalog implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "catalog_name")
    protected String catalogName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String comment;

    public String getCatalogName() {
        return this.catalogName;
    }

    public void setCatalogName(String value) {
        this.catalogName = value;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public Catalog withCatalogName(String value) {
        setCatalogName(value);
        return this;
    }

    public Catalog withComment(String value) {
        setComment(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("catalog_name", this.catalogName);
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
        Catalog other = (Catalog) that;
        if (this.catalogName == null) {
            if (other.catalogName != null) {
                return false;
            }
        } else if (!this.catalogName.equals(other.catalogName)) {
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
        return (31 * result) + (this.comment == null ? 0 : this.comment.hashCode());
    }
}
