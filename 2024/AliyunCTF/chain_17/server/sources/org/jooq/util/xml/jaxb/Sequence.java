package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.math.BigInteger;
import org.jooq.util.jaxb.tools.StringAdapter;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Sequence", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/Sequence.class */
public class Sequence implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "sequence_catalog")
    protected String sequenceCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "sequence_schema")
    protected String sequenceSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "sequence_name", required = true)
    protected String sequenceName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "data_type", required = true)
    protected String dataType;

    @XmlElement(name = "character_maximum_length")
    protected Integer characterMaximumLength;

    @XmlElement(name = "numeric_precision")
    protected Integer numericPrecision;

    @XmlElement(name = "numeric_scale")
    protected Integer numericScale;

    @XmlElement(name = "start_value")
    protected BigInteger startValue;
    protected BigInteger increment;

    @XmlElement(name = "minimum_value")
    protected BigInteger minimumValue;

    @XmlElement(name = "maximum_value")
    protected BigInteger maximumValue;

    @XmlElement(name = "cycle_option")
    protected Boolean cycleOption;
    protected BigInteger cache;

    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String comment;

    public String getSequenceCatalog() {
        return this.sequenceCatalog;
    }

    public void setSequenceCatalog(String value) {
        this.sequenceCatalog = value;
    }

    public String getSequenceSchema() {
        return this.sequenceSchema;
    }

    public void setSequenceSchema(String value) {
        this.sequenceSchema = value;
    }

    public String getSequenceName() {
        return this.sequenceName;
    }

    public void setSequenceName(String value) {
        this.sequenceName = value;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String value) {
        this.dataType = value;
    }

    public Integer getCharacterMaximumLength() {
        return this.characterMaximumLength;
    }

    public void setCharacterMaximumLength(Integer value) {
        this.characterMaximumLength = value;
    }

    public Integer getNumericPrecision() {
        return this.numericPrecision;
    }

    public void setNumericPrecision(Integer value) {
        this.numericPrecision = value;
    }

    public Integer getNumericScale() {
        return this.numericScale;
    }

    public void setNumericScale(Integer value) {
        this.numericScale = value;
    }

    public BigInteger getStartValue() {
        return this.startValue;
    }

    public void setStartValue(BigInteger value) {
        this.startValue = value;
    }

    public BigInteger getIncrement() {
        return this.increment;
    }

    public void setIncrement(BigInteger value) {
        this.increment = value;
    }

    public BigInteger getMinimumValue() {
        return this.minimumValue;
    }

    public void setMinimumValue(BigInteger value) {
        this.minimumValue = value;
    }

    public BigInteger getMaximumValue() {
        return this.maximumValue;
    }

    public void setMaximumValue(BigInteger value) {
        this.maximumValue = value;
    }

    public Boolean isCycleOption() {
        return this.cycleOption;
    }

    public void setCycleOption(Boolean value) {
        this.cycleOption = value;
    }

    public BigInteger getCache() {
        return this.cache;
    }

    public void setCache(BigInteger value) {
        this.cache = value;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public Sequence withSequenceCatalog(String value) {
        setSequenceCatalog(value);
        return this;
    }

    public Sequence withSequenceSchema(String value) {
        setSequenceSchema(value);
        return this;
    }

    public Sequence withSequenceName(String value) {
        setSequenceName(value);
        return this;
    }

    public Sequence withDataType(String value) {
        setDataType(value);
        return this;
    }

    public Sequence withCharacterMaximumLength(Integer value) {
        setCharacterMaximumLength(value);
        return this;
    }

    public Sequence withNumericPrecision(Integer value) {
        setNumericPrecision(value);
        return this;
    }

    public Sequence withNumericScale(Integer value) {
        setNumericScale(value);
        return this;
    }

    public Sequence withStartValue(BigInteger value) {
        setStartValue(value);
        return this;
    }

    public Sequence withIncrement(BigInteger value) {
        setIncrement(value);
        return this;
    }

    public Sequence withMinimumValue(BigInteger value) {
        setMinimumValue(value);
        return this;
    }

    public Sequence withMaximumValue(BigInteger value) {
        setMaximumValue(value);
        return this;
    }

    public Sequence withCycleOption(Boolean value) {
        setCycleOption(value);
        return this;
    }

    public Sequence withCache(BigInteger value) {
        setCache(value);
        return this;
    }

    public Sequence withComment(String value) {
        setComment(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("sequence_catalog", this.sequenceCatalog);
        builder.append("sequence_schema", this.sequenceSchema);
        builder.append("sequence_name", this.sequenceName);
        builder.append("data_type", this.dataType);
        builder.append("character_maximum_length", this.characterMaximumLength);
        builder.append("numeric_precision", this.numericPrecision);
        builder.append("numeric_scale", this.numericScale);
        builder.append("start_value", this.startValue);
        builder.append("increment", this.increment);
        builder.append("minimum_value", this.minimumValue);
        builder.append("maximum_value", this.maximumValue);
        builder.append("cycle_option", this.cycleOption);
        builder.append("cache", this.cache);
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
        Sequence other = (Sequence) that;
        if (this.sequenceCatalog == null) {
            if (other.sequenceCatalog != null) {
                return false;
            }
        } else if (!this.sequenceCatalog.equals(other.sequenceCatalog)) {
            return false;
        }
        if (this.sequenceSchema == null) {
            if (other.sequenceSchema != null) {
                return false;
            }
        } else if (!this.sequenceSchema.equals(other.sequenceSchema)) {
            return false;
        }
        if (this.sequenceName == null) {
            if (other.sequenceName != null) {
                return false;
            }
        } else if (!this.sequenceName.equals(other.sequenceName)) {
            return false;
        }
        if (this.dataType == null) {
            if (other.dataType != null) {
                return false;
            }
        } else if (!this.dataType.equals(other.dataType)) {
            return false;
        }
        if (this.characterMaximumLength == null) {
            if (other.characterMaximumLength != null) {
                return false;
            }
        } else if (!this.characterMaximumLength.equals(other.characterMaximumLength)) {
            return false;
        }
        if (this.numericPrecision == null) {
            if (other.numericPrecision != null) {
                return false;
            }
        } else if (!this.numericPrecision.equals(other.numericPrecision)) {
            return false;
        }
        if (this.numericScale == null) {
            if (other.numericScale != null) {
                return false;
            }
        } else if (!this.numericScale.equals(other.numericScale)) {
            return false;
        }
        if (this.startValue == null) {
            if (other.startValue != null) {
                return false;
            }
        } else if (!this.startValue.equals(other.startValue)) {
            return false;
        }
        if (this.increment == null) {
            if (other.increment != null) {
                return false;
            }
        } else if (!this.increment.equals(other.increment)) {
            return false;
        }
        if (this.minimumValue == null) {
            if (other.minimumValue != null) {
                return false;
            }
        } else if (!this.minimumValue.equals(other.minimumValue)) {
            return false;
        }
        if (this.maximumValue == null) {
            if (other.maximumValue != null) {
                return false;
            }
        } else if (!this.maximumValue.equals(other.maximumValue)) {
            return false;
        }
        if (this.cycleOption == null) {
            if (other.cycleOption != null) {
                return false;
            }
        } else if (!this.cycleOption.equals(other.cycleOption)) {
            return false;
        }
        if (this.cache == null) {
            if (other.cache != null) {
                return false;
            }
        } else if (!this.cache.equals(other.cache)) {
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
        int result = (31 * 1) + (this.sequenceCatalog == null ? 0 : this.sequenceCatalog.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.sequenceSchema == null ? 0 : this.sequenceSchema.hashCode()))) + (this.sequenceName == null ? 0 : this.sequenceName.hashCode()))) + (this.dataType == null ? 0 : this.dataType.hashCode()))) + (this.characterMaximumLength == null ? 0 : this.characterMaximumLength.hashCode()))) + (this.numericPrecision == null ? 0 : this.numericPrecision.hashCode()))) + (this.numericScale == null ? 0 : this.numericScale.hashCode()))) + (this.startValue == null ? 0 : this.startValue.hashCode()))) + (this.increment == null ? 0 : this.increment.hashCode()))) + (this.minimumValue == null ? 0 : this.minimumValue.hashCode()))) + (this.maximumValue == null ? 0 : this.maximumValue.hashCode()))) + (this.cycleOption == null ? 0 : this.cycleOption.hashCode()))) + (this.cache == null ? 0 : this.cache.hashCode()))) + (this.comment == null ? 0 : this.comment.hashCode());
    }
}
