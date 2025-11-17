package org.jooq.migrations.xml.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParentType", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/migrations/xml/jaxb/ParentType.class */
public class ParentType implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlElement(required = true)
    protected String id;

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public ParentType withId(String value) {
        setId(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("id", this.id);
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
        ParentType other = (ParentType) that;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
            return true;
        }
        if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.id == null ? 0 : this.id.hashCode());
        return result;
    }
}
