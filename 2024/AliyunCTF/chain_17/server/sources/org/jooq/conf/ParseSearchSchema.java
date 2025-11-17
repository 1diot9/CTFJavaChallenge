package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParseSearchSchema", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/ParseSearchSchema.class */
public class ParseSearchSchema extends SettingsBase implements Serializable, Cloneable, XMLAppendable {
    private static final long serialVersionUID = 31900;
    protected String catalog;

    @XmlElement(required = true)
    protected String schema;

    @Override // org.jooq.conf.SettingsBase
    public /* bridge */ /* synthetic */ Object clone() {
        return super.clone();
    }

    public String getCatalog() {
        return this.catalog;
    }

    public void setCatalog(String value) {
        this.catalog = value;
    }

    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String value) {
        this.schema = value;
    }

    public ParseSearchSchema withCatalog(String value) {
        setCatalog(value);
        return this;
    }

    public ParseSearchSchema withSchema(String value) {
        setSchema(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("catalog", this.catalog);
        builder.append("schema", this.schema);
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
        ParseSearchSchema other = (ParseSearchSchema) that;
        if (this.catalog == null) {
            if (other.catalog != null) {
                return false;
            }
        } else if (!this.catalog.equals(other.catalog)) {
            return false;
        }
        if (this.schema == null) {
            if (other.schema != null) {
                return false;
            }
            return true;
        }
        if (!this.schema.equals(other.schema)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.catalog == null ? 0 : this.catalog.hashCode());
        return (31 * result) + (this.schema == null ? 0 : this.schema.hashCode());
    }
}
