package org.jooq.migrations.xml.jaxb;

import ch.qos.logback.classic.encoder.JsonEncoder;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TagType", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/migrations/xml/jaxb/TagType.class */
public class TagType implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlElement(required = true)
    protected String id;
    protected String message;

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public TagType withId(String value) {
        setId(value);
        return this;
    }

    public TagType withMessage(String value) {
        setMessage(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("id", this.id);
        builder.append(JsonEncoder.MESSAGE_ATTR_NAME, this.message);
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
        TagType other = (TagType) that;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        if (this.message == null) {
            if (other.message != null) {
                return false;
            }
            return true;
        }
        if (!this.message.equals(other.message)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.id == null ? 0 : this.id.hashCode());
        return (31 * result) + (this.message == null ? 0 : this.message.hashCode());
    }
}
