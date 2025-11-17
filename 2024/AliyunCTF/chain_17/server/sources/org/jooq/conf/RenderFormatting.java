package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RenderFormatting", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/RenderFormatting.class */
public class RenderFormatting extends SettingsBase implements Serializable, Cloneable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlElement(defaultValue = "\n")
    protected String newline = "\n";

    @XmlElement(defaultValue = "  ")
    protected String indentation = "  ";

    @XmlElement(defaultValue = "80")
    protected Integer printMargin = 80;

    @Override // org.jooq.conf.SettingsBase
    public /* bridge */ /* synthetic */ Object clone() {
        return super.clone();
    }

    public String getNewline() {
        return this.newline;
    }

    public void setNewline(String value) {
        this.newline = value;
    }

    public String getIndentation() {
        return this.indentation;
    }

    public void setIndentation(String value) {
        this.indentation = value;
    }

    public Integer getPrintMargin() {
        return this.printMargin;
    }

    public void setPrintMargin(Integer value) {
        this.printMargin = value;
    }

    public RenderFormatting withNewline(String value) {
        setNewline(value);
        return this;
    }

    public RenderFormatting withIndentation(String value) {
        setIndentation(value);
        return this;
    }

    public RenderFormatting withPrintMargin(Integer value) {
        setPrintMargin(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("newline", this.newline);
        builder.append("indentation", this.indentation);
        builder.append("printMargin", this.printMargin);
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
        RenderFormatting other = (RenderFormatting) that;
        if (this.newline == null) {
            if (other.newline != null) {
                return false;
            }
        } else if (!this.newline.equals(other.newline)) {
            return false;
        }
        if (this.indentation == null) {
            if (other.indentation != null) {
                return false;
            }
        } else if (!this.indentation.equals(other.indentation)) {
            return false;
        }
        if (this.printMargin == null) {
            if (other.printMargin != null) {
                return false;
            }
            return true;
        }
        if (!this.printMargin.equals(other.printMargin)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.newline == null ? 0 : this.newline.hashCode());
        return (31 * ((31 * result) + (this.indentation == null ? 0 : this.indentation.hashCode()))) + (this.printMargin == null ? 0 : this.printMargin.hashCode());
    }
}
