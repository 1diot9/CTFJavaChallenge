package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.regex.Pattern;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MappedUDT", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/MappedUDT.class */
public class MappedUDT extends SettingsBase implements Serializable, Cloneable, MappedSchemaObject, XMLAppendable {
    private static final long serialVersionUID = 31900;
    protected String input;

    @XmlJavaTypeAdapter(RegexAdapter.class)
    @XmlElement(type = String.class)
    protected Pattern inputExpression;

    @XmlElement(required = true)
    protected String output;

    @Override // org.jooq.conf.SettingsBase
    public /* bridge */ /* synthetic */ Object clone() {
        return super.clone();
    }

    @Override // org.jooq.conf.MappedSchemaObject
    public String getInput() {
        return this.input;
    }

    public void setInput(String value) {
        this.input = value;
    }

    @Override // org.jooq.conf.MappedSchemaObject
    public Pattern getInputExpression() {
        return this.inputExpression;
    }

    public void setInputExpression(Pattern value) {
        this.inputExpression = value;
    }

    @Override // org.jooq.conf.MappedSchemaObject
    public String getOutput() {
        return this.output;
    }

    public void setOutput(String value) {
        this.output = value;
    }

    public MappedUDT withInput(String value) {
        setInput(value);
        return this;
    }

    public MappedUDT withInputExpression(Pattern value) {
        setInputExpression(value);
        return this;
    }

    public MappedUDT withOutput(String value) {
        setOutput(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("input", this.input);
        builder.append("inputExpression", this.inputExpression);
        builder.append("output", this.output);
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
        MappedUDT other = (MappedUDT) that;
        if (this.input == null) {
            if (other.input != null) {
                return false;
            }
        } else if (!this.input.equals(other.input)) {
            return false;
        }
        if (this.inputExpression == null) {
            if (other.inputExpression != null) {
                return false;
            }
        } else if (!this.inputExpression.pattern().equals(other.inputExpression.pattern())) {
            return false;
        }
        if (this.output == null) {
            if (other.output != null) {
                return false;
            }
            return true;
        }
        if (!this.output.equals(other.output)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.input == null ? 0 : this.input.hashCode());
        return (31 * ((31 * result) + (this.inputExpression == null ? 0 : this.inputExpression.pattern().hashCode()))) + (this.output == null ? 0 : this.output.hashCode());
    }
}
