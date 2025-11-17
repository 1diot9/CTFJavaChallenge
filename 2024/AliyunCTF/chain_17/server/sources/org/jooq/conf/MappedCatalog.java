package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MappedCatalog", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/MappedCatalog.class */
public class MappedCatalog extends SettingsBase implements Serializable, Cloneable, XMLAppendable {
    private static final long serialVersionUID = 31900;
    protected String input;

    @XmlJavaTypeAdapter(RegexAdapter.class)
    @XmlElement(type = String.class)
    protected Pattern inputExpression;
    protected String output;

    @XmlElementWrapper(name = "schemata")
    @XmlElement(name = "schema")
    protected List<MappedSchema> schemata;

    @Override // org.jooq.conf.SettingsBase
    public /* bridge */ /* synthetic */ Object clone() {
        return super.clone();
    }

    public String getInput() {
        return this.input;
    }

    public void setInput(String value) {
        this.input = value;
    }

    public Pattern getInputExpression() {
        return this.inputExpression;
    }

    public void setInputExpression(Pattern value) {
        this.inputExpression = value;
    }

    public String getOutput() {
        return this.output;
    }

    public void setOutput(String value) {
        this.output = value;
    }

    public List<MappedSchema> getSchemata() {
        if (this.schemata == null) {
            this.schemata = new ArrayList();
        }
        return this.schemata;
    }

    public void setSchemata(List<MappedSchema> schemata) {
        this.schemata = schemata;
    }

    public MappedCatalog withInput(String value) {
        setInput(value);
        return this;
    }

    public MappedCatalog withInputExpression(Pattern value) {
        setInputExpression(value);
        return this;
    }

    public MappedCatalog withOutput(String value) {
        setOutput(value);
        return this;
    }

    public MappedCatalog withSchemata(MappedSchema... values) {
        if (values != null) {
            for (MappedSchema value : values) {
                getSchemata().add(value);
            }
        }
        return this;
    }

    public MappedCatalog withSchemata(Collection<MappedSchema> values) {
        if (values != null) {
            getSchemata().addAll(values);
        }
        return this;
    }

    public MappedCatalog withSchemata(List<MappedSchema> schemata) {
        setSchemata(schemata);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("input", this.input);
        builder.append("inputExpression", this.inputExpression);
        builder.append("output", this.output);
        builder.append("schemata", "schema", this.schemata);
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
        MappedCatalog other = (MappedCatalog) that;
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
        } else if (!this.output.equals(other.output)) {
            return false;
        }
        if (this.schemata == null) {
            if (other.schemata != null) {
                return false;
            }
            return true;
        }
        if (!this.schemata.equals(other.schemata)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.input == null ? 0 : this.input.hashCode());
        return (31 * ((31 * ((31 * result) + (this.inputExpression == null ? 0 : this.inputExpression.pattern().hashCode()))) + (this.output == null ? 0 : this.output.hashCode()))) + (this.schemata == null ? 0 : this.schemata.hashCode());
    }
}
