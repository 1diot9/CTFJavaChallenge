package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import org.jooq.util.jaxb.tools.StringAdapter;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Routine", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/Routine.class */
public class Routine implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "specific_catalog")
    protected String specificCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "specific_schema")
    protected String specificSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "specific_package")
    protected String specificPackage;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "specific_name")
    protected String specificName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "routine_catalog")
    protected String routineCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "routine_schema")
    protected String routineSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "routine_package")
    protected String routinePackage;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "routine_name", required = true)
    protected String routineName;

    @XmlSchemaType(name = "string")
    @XmlElement(name = "routine_type", required = true)
    protected RoutineType routineType;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "data_type")
    protected String dataType;

    @XmlElement(name = "character_maximum_length")
    protected Integer characterMaximumLength;

    @XmlElement(name = "numeric_precision")
    protected Integer numericPrecision;

    @XmlElement(name = "numeric_scale")
    protected Integer numericScale;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "udt_catalog")
    protected String udtCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "udt_schema")
    protected String udtSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "udt_name")
    protected String udtName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String comment;

    public String getSpecificCatalog() {
        return this.specificCatalog;
    }

    public void setSpecificCatalog(String value) {
        this.specificCatalog = value;
    }

    public String getSpecificSchema() {
        return this.specificSchema;
    }

    public void setSpecificSchema(String value) {
        this.specificSchema = value;
    }

    public String getSpecificPackage() {
        return this.specificPackage;
    }

    public void setSpecificPackage(String value) {
        this.specificPackage = value;
    }

    public String getSpecificName() {
        return this.specificName;
    }

    public void setSpecificName(String value) {
        this.specificName = value;
    }

    public String getRoutineCatalog() {
        return this.routineCatalog;
    }

    public void setRoutineCatalog(String value) {
        this.routineCatalog = value;
    }

    public String getRoutineSchema() {
        return this.routineSchema;
    }

    public void setRoutineSchema(String value) {
        this.routineSchema = value;
    }

    public String getRoutinePackage() {
        return this.routinePackage;
    }

    public void setRoutinePackage(String value) {
        this.routinePackage = value;
    }

    public String getRoutineName() {
        return this.routineName;
    }

    public void setRoutineName(String value) {
        this.routineName = value;
    }

    public RoutineType getRoutineType() {
        return this.routineType;
    }

    public void setRoutineType(RoutineType value) {
        this.routineType = value;
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

    public String getUdtCatalog() {
        return this.udtCatalog;
    }

    public void setUdtCatalog(String value) {
        this.udtCatalog = value;
    }

    public String getUdtSchema() {
        return this.udtSchema;
    }

    public void setUdtSchema(String value) {
        this.udtSchema = value;
    }

    public String getUdtName() {
        return this.udtName;
    }

    public void setUdtName(String value) {
        this.udtName = value;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public Routine withSpecificCatalog(String value) {
        setSpecificCatalog(value);
        return this;
    }

    public Routine withSpecificSchema(String value) {
        setSpecificSchema(value);
        return this;
    }

    public Routine withSpecificPackage(String value) {
        setSpecificPackage(value);
        return this;
    }

    public Routine withSpecificName(String value) {
        setSpecificName(value);
        return this;
    }

    public Routine withRoutineCatalog(String value) {
        setRoutineCatalog(value);
        return this;
    }

    public Routine withRoutineSchema(String value) {
        setRoutineSchema(value);
        return this;
    }

    public Routine withRoutinePackage(String value) {
        setRoutinePackage(value);
        return this;
    }

    public Routine withRoutineName(String value) {
        setRoutineName(value);
        return this;
    }

    public Routine withRoutineType(RoutineType value) {
        setRoutineType(value);
        return this;
    }

    public Routine withDataType(String value) {
        setDataType(value);
        return this;
    }

    public Routine withCharacterMaximumLength(Integer value) {
        setCharacterMaximumLength(value);
        return this;
    }

    public Routine withNumericPrecision(Integer value) {
        setNumericPrecision(value);
        return this;
    }

    public Routine withNumericScale(Integer value) {
        setNumericScale(value);
        return this;
    }

    public Routine withUdtCatalog(String value) {
        setUdtCatalog(value);
        return this;
    }

    public Routine withUdtSchema(String value) {
        setUdtSchema(value);
        return this;
    }

    public Routine withUdtName(String value) {
        setUdtName(value);
        return this;
    }

    public Routine withComment(String value) {
        setComment(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("specific_catalog", this.specificCatalog);
        builder.append("specific_schema", this.specificSchema);
        builder.append("specific_package", this.specificPackage);
        builder.append("specific_name", this.specificName);
        builder.append("routine_catalog", this.routineCatalog);
        builder.append("routine_schema", this.routineSchema);
        builder.append("routine_package", this.routinePackage);
        builder.append("routine_name", this.routineName);
        builder.append("routine_type", this.routineType);
        builder.append("data_type", this.dataType);
        builder.append("character_maximum_length", this.characterMaximumLength);
        builder.append("numeric_precision", this.numericPrecision);
        builder.append("numeric_scale", this.numericScale);
        builder.append("udt_catalog", this.udtCatalog);
        builder.append("udt_schema", this.udtSchema);
        builder.append("udt_name", this.udtName);
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
        Routine other = (Routine) that;
        if (this.specificCatalog == null) {
            if (other.specificCatalog != null) {
                return false;
            }
        } else if (!this.specificCatalog.equals(other.specificCatalog)) {
            return false;
        }
        if (this.specificSchema == null) {
            if (other.specificSchema != null) {
                return false;
            }
        } else if (!this.specificSchema.equals(other.specificSchema)) {
            return false;
        }
        if (this.specificPackage == null) {
            if (other.specificPackage != null) {
                return false;
            }
        } else if (!this.specificPackage.equals(other.specificPackage)) {
            return false;
        }
        if (this.specificName == null) {
            if (other.specificName != null) {
                return false;
            }
        } else if (!this.specificName.equals(other.specificName)) {
            return false;
        }
        if (this.routineCatalog == null) {
            if (other.routineCatalog != null) {
                return false;
            }
        } else if (!this.routineCatalog.equals(other.routineCatalog)) {
            return false;
        }
        if (this.routineSchema == null) {
            if (other.routineSchema != null) {
                return false;
            }
        } else if (!this.routineSchema.equals(other.routineSchema)) {
            return false;
        }
        if (this.routinePackage == null) {
            if (other.routinePackage != null) {
                return false;
            }
        } else if (!this.routinePackage.equals(other.routinePackage)) {
            return false;
        }
        if (this.routineName == null) {
            if (other.routineName != null) {
                return false;
            }
        } else if (!this.routineName.equals(other.routineName)) {
            return false;
        }
        if (this.routineType == null) {
            if (other.routineType != null) {
                return false;
            }
        } else if (!this.routineType.equals(other.routineType)) {
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
        if (this.udtCatalog == null) {
            if (other.udtCatalog != null) {
                return false;
            }
        } else if (!this.udtCatalog.equals(other.udtCatalog)) {
            return false;
        }
        if (this.udtSchema == null) {
            if (other.udtSchema != null) {
                return false;
            }
        } else if (!this.udtSchema.equals(other.udtSchema)) {
            return false;
        }
        if (this.udtName == null) {
            if (other.udtName != null) {
                return false;
            }
        } else if (!this.udtName.equals(other.udtName)) {
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
        int result = (31 * 1) + (this.specificCatalog == null ? 0 : this.specificCatalog.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.specificSchema == null ? 0 : this.specificSchema.hashCode()))) + (this.specificPackage == null ? 0 : this.specificPackage.hashCode()))) + (this.specificName == null ? 0 : this.specificName.hashCode()))) + (this.routineCatalog == null ? 0 : this.routineCatalog.hashCode()))) + (this.routineSchema == null ? 0 : this.routineSchema.hashCode()))) + (this.routinePackage == null ? 0 : this.routinePackage.hashCode()))) + (this.routineName == null ? 0 : this.routineName.hashCode()))) + (this.routineType == null ? 0 : this.routineType.hashCode()))) + (this.dataType == null ? 0 : this.dataType.hashCode()))) + (this.characterMaximumLength == null ? 0 : this.characterMaximumLength.hashCode()))) + (this.numericPrecision == null ? 0 : this.numericPrecision.hashCode()))) + (this.numericScale == null ? 0 : this.numericScale.hashCode()))) + (this.udtCatalog == null ? 0 : this.udtCatalog.hashCode()))) + (this.udtSchema == null ? 0 : this.udtSchema.hashCode()))) + (this.udtName == null ? 0 : this.udtName.hashCode()))) + (this.comment == null ? 0 : this.comment.hashCode());
    }
}
