package org.jooq.migrations.xml.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import org.jooq.ContentType;
import org.jooq.migrations.xml.ContentTypeAdapter;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FileType", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/migrations/xml/jaxb/FileType.class */
public class FileType implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlElement(required = true)
    protected String path;
    protected String content;

    @XmlJavaTypeAdapter(ContentTypeAdapter.class)
    @XmlElement(type = String.class)
    protected ContentType contentType;

    @XmlSchemaType(name = "string")
    @XmlElement(required = true)
    protected ChangeType change;

    public String getPath() {
        return this.path;
    }

    public void setPath(String value) {
        this.path = value;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String value) {
        this.content = value;
    }

    public ContentType getContentType() {
        return this.contentType;
    }

    public void setContentType(ContentType value) {
        this.contentType = value;
    }

    public ChangeType getChange() {
        return this.change;
    }

    public void setChange(ChangeType value) {
        this.change = value;
    }

    public FileType withPath(String value) {
        setPath(value);
        return this;
    }

    public FileType withContent(String value) {
        setContent(value);
        return this;
    }

    public FileType withContentType(ContentType value) {
        setContentType(value);
        return this;
    }

    public FileType withChange(ChangeType value) {
        setChange(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("path", this.path);
        builder.append("content", this.content);
        builder.append("contentType", this.contentType);
        builder.append("change", this.change);
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
        FileType other = (FileType) that;
        if (this.path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!this.path.equals(other.path)) {
            return false;
        }
        if (this.content == null) {
            if (other.content != null) {
                return false;
            }
        } else if (!this.content.equals(other.content)) {
            return false;
        }
        if (this.contentType == null) {
            if (other.contentType != null) {
                return false;
            }
        } else if (!this.contentType.equals(other.contentType)) {
            return false;
        }
        if (this.change == null) {
            if (other.change != null) {
                return false;
            }
            return true;
        }
        if (!this.change.equals(other.change)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.path == null ? 0 : this.path.hashCode());
        return (31 * ((31 * ((31 * result) + (this.content == null ? 0 : this.content.hashCode()))) + (this.contentType == null ? 0 : this.contentType.hashCode()))) + (this.change == null ? 0 : this.change.hashCode());
    }
}
