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
@XmlType(name = "MappedSchema", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/MappedSchema.class */
public class MappedSchema extends SettingsBase implements Serializable, Cloneable, XMLAppendable {
    private static final long serialVersionUID = 31900;
    protected String input;

    @XmlJavaTypeAdapter(RegexAdapter.class)
    @XmlElement(type = String.class)
    protected Pattern inputExpression;
    protected String output;

    @XmlElementWrapper(name = "tables")
    @XmlElement(name = "table")
    protected List<MappedTable> tables;

    @XmlElementWrapper(name = "udts")
    @XmlElement(name = "udt")
    protected List<MappedUDT> udts;

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

    public List<MappedTable> getTables() {
        if (this.tables == null) {
            this.tables = new ArrayList();
        }
        return this.tables;
    }

    public void setTables(List<MappedTable> tables) {
        this.tables = tables;
    }

    public List<MappedUDT> getUdts() {
        if (this.udts == null) {
            this.udts = new ArrayList();
        }
        return this.udts;
    }

    public void setUdts(List<MappedUDT> udts) {
        this.udts = udts;
    }

    public MappedSchema withInput(String value) {
        setInput(value);
        return this;
    }

    public MappedSchema withInputExpression(Pattern value) {
        setInputExpression(value);
        return this;
    }

    public MappedSchema withOutput(String value) {
        setOutput(value);
        return this;
    }

    public MappedSchema withTables(MappedTable... values) {
        if (values != null) {
            for (MappedTable value : values) {
                getTables().add(value);
            }
        }
        return this;
    }

    public MappedSchema withTables(Collection<MappedTable> values) {
        if (values != null) {
            getTables().addAll(values);
        }
        return this;
    }

    public MappedSchema withTables(List<MappedTable> tables) {
        setTables(tables);
        return this;
    }

    public MappedSchema withUdts(MappedUDT... values) {
        if (values != null) {
            for (MappedUDT value : values) {
                getUdts().add(value);
            }
        }
        return this;
    }

    public MappedSchema withUdts(Collection<MappedUDT> values) {
        if (values != null) {
            getUdts().addAll(values);
        }
        return this;
    }

    public MappedSchema withUdts(List<MappedUDT> udts) {
        setUdts(udts);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("input", this.input);
        builder.append("inputExpression", this.inputExpression);
        builder.append("output", this.output);
        builder.append("tables", "table", this.tables);
        builder.append("udts", "udt", this.udts);
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
        MappedSchema other = (MappedSchema) that;
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
        if (this.tables == null) {
            if (other.tables != null) {
                return false;
            }
        } else if (!this.tables.equals(other.tables)) {
            return false;
        }
        if (this.udts == null) {
            if (other.udts != null) {
                return false;
            }
            return true;
        }
        if (!this.udts.equals(other.udts)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.input == null ? 0 : this.input.hashCode());
        return (31 * ((31 * ((31 * ((31 * result) + (this.inputExpression == null ? 0 : this.inputExpression.pattern().hashCode()))) + (this.output == null ? 0 : this.output.hashCode()))) + (this.tables == null ? 0 : this.tables.hashCode()))) + (this.udts == null ? 0 : this.udts.hashCode());
    }
}
