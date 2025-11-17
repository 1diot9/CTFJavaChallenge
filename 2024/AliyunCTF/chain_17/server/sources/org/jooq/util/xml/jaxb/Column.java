package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import org.jooq.util.jaxb.tools.StringAdapter;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Column", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/Column.class */
public class Column implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "table_catalog")
    protected String tableCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "table_schema")
    protected String tableSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "table_name", required = true)
    protected String tableName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "column_name", required = true)
    protected String columnName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "data_type", required = true)
    protected String dataType;

    @XmlElement(name = "character_maximum_length")
    protected Integer characterMaximumLength;

    @XmlElement(name = "numeric_precision")
    protected Integer numericPrecision;

    @XmlElement(name = "numeric_scale")
    protected Integer numericScale;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "domain_catalog")
    protected String domainCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "domain_schema")
    protected String domainSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "domain_name")
    protected String domainName;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "udt_catalog")
    protected String udtCatalog;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "udt_schema")
    protected String udtSchema;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "udt_name")
    protected String udtName;

    @XmlElement(name = "ordinal_position")
    protected Integer ordinalPosition;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "identity_generation")
    protected String identityGeneration;

    @XmlElement(name = "is_nullable")
    protected Boolean isNullable;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "column_default")
    protected String columnDefault;

    @XmlJavaTypeAdapter(StringAdapter.class)
    protected String comment;
    protected Boolean readonly;

    @XmlElement(name = "is_generated")
    protected Boolean isGenerated;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "generation_expression")
    protected String generationExpression;

    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(name = "generation_option")
    protected String generationOption;

    public String getTableCatalog() {
        return this.tableCatalog;
    }

    public void setTableCatalog(String value) {
        this.tableCatalog = value;
    }

    public String getTableSchema() {
        return this.tableSchema;
    }

    public void setTableSchema(String value) {
        this.tableSchema = value;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String value) {
        this.tableName = value;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String value) {
        this.columnName = value;
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

    public String getDomainCatalog() {
        return this.domainCatalog;
    }

    public void setDomainCatalog(String value) {
        this.domainCatalog = value;
    }

    public String getDomainSchema() {
        return this.domainSchema;
    }

    public void setDomainSchema(String value) {
        this.domainSchema = value;
    }

    public String getDomainName() {
        return this.domainName;
    }

    public void setDomainName(String value) {
        this.domainName = value;
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

    public Integer getOrdinalPosition() {
        return this.ordinalPosition;
    }

    public void setOrdinalPosition(Integer value) {
        this.ordinalPosition = value;
    }

    public String getIdentityGeneration() {
        return this.identityGeneration;
    }

    public void setIdentityGeneration(String value) {
        this.identityGeneration = value;
    }

    public Boolean isIsNullable() {
        return this.isNullable;
    }

    public void setIsNullable(Boolean value) {
        this.isNullable = value;
    }

    public String getColumnDefault() {
        return this.columnDefault;
    }

    public void setColumnDefault(String value) {
        this.columnDefault = value;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public Boolean isReadonly() {
        return this.readonly;
    }

    public void setReadonly(Boolean value) {
        this.readonly = value;
    }

    public Boolean isIsGenerated() {
        return this.isGenerated;
    }

    public void setIsGenerated(Boolean value) {
        this.isGenerated = value;
    }

    public String getGenerationExpression() {
        return this.generationExpression;
    }

    public void setGenerationExpression(String value) {
        this.generationExpression = value;
    }

    public String getGenerationOption() {
        return this.generationOption;
    }

    public void setGenerationOption(String value) {
        this.generationOption = value;
    }

    public Column withTableCatalog(String value) {
        setTableCatalog(value);
        return this;
    }

    public Column withTableSchema(String value) {
        setTableSchema(value);
        return this;
    }

    public Column withTableName(String value) {
        setTableName(value);
        return this;
    }

    public Column withColumnName(String value) {
        setColumnName(value);
        return this;
    }

    public Column withDataType(String value) {
        setDataType(value);
        return this;
    }

    public Column withCharacterMaximumLength(Integer value) {
        setCharacterMaximumLength(value);
        return this;
    }

    public Column withNumericPrecision(Integer value) {
        setNumericPrecision(value);
        return this;
    }

    public Column withNumericScale(Integer value) {
        setNumericScale(value);
        return this;
    }

    public Column withDomainCatalog(String value) {
        setDomainCatalog(value);
        return this;
    }

    public Column withDomainSchema(String value) {
        setDomainSchema(value);
        return this;
    }

    public Column withDomainName(String value) {
        setDomainName(value);
        return this;
    }

    public Column withUdtCatalog(String value) {
        setUdtCatalog(value);
        return this;
    }

    public Column withUdtSchema(String value) {
        setUdtSchema(value);
        return this;
    }

    public Column withUdtName(String value) {
        setUdtName(value);
        return this;
    }

    public Column withOrdinalPosition(Integer value) {
        setOrdinalPosition(value);
        return this;
    }

    public Column withIdentityGeneration(String value) {
        setIdentityGeneration(value);
        return this;
    }

    public Column withIsNullable(Boolean value) {
        setIsNullable(value);
        return this;
    }

    public Column withColumnDefault(String value) {
        setColumnDefault(value);
        return this;
    }

    public Column withComment(String value) {
        setComment(value);
        return this;
    }

    public Column withReadonly(Boolean value) {
        setReadonly(value);
        return this;
    }

    public Column withIsGenerated(Boolean value) {
        setIsGenerated(value);
        return this;
    }

    public Column withGenerationExpression(String value) {
        setGenerationExpression(value);
        return this;
    }

    public Column withGenerationOption(String value) {
        setGenerationOption(value);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("table_catalog", this.tableCatalog);
        builder.append("table_schema", this.tableSchema);
        builder.append("table_name", this.tableName);
        builder.append("column_name", this.columnName);
        builder.append("data_type", this.dataType);
        builder.append("character_maximum_length", this.characterMaximumLength);
        builder.append("numeric_precision", this.numericPrecision);
        builder.append("numeric_scale", this.numericScale);
        builder.append("domain_catalog", this.domainCatalog);
        builder.append("domain_schema", this.domainSchema);
        builder.append("domain_name", this.domainName);
        builder.append("udt_catalog", this.udtCatalog);
        builder.append("udt_schema", this.udtSchema);
        builder.append("udt_name", this.udtName);
        builder.append("ordinal_position", this.ordinalPosition);
        builder.append("identity_generation", this.identityGeneration);
        builder.append("is_nullable", this.isNullable);
        builder.append("column_default", this.columnDefault);
        builder.append("comment", this.comment);
        builder.append(AbstractHtmlInputElementTag.READONLY_ATTRIBUTE, this.readonly);
        builder.append("is_generated", this.isGenerated);
        builder.append("generation_expression", this.generationExpression);
        builder.append("generation_option", this.generationOption);
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
        Column other = (Column) that;
        if (this.tableCatalog == null) {
            if (other.tableCatalog != null) {
                return false;
            }
        } else if (!this.tableCatalog.equals(other.tableCatalog)) {
            return false;
        }
        if (this.tableSchema == null) {
            if (other.tableSchema != null) {
                return false;
            }
        } else if (!this.tableSchema.equals(other.tableSchema)) {
            return false;
        }
        if (this.tableName == null) {
            if (other.tableName != null) {
                return false;
            }
        } else if (!this.tableName.equals(other.tableName)) {
            return false;
        }
        if (this.columnName == null) {
            if (other.columnName != null) {
                return false;
            }
        } else if (!this.columnName.equals(other.columnName)) {
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
        if (this.domainCatalog == null) {
            if (other.domainCatalog != null) {
                return false;
            }
        } else if (!this.domainCatalog.equals(other.domainCatalog)) {
            return false;
        }
        if (this.domainSchema == null) {
            if (other.domainSchema != null) {
                return false;
            }
        } else if (!this.domainSchema.equals(other.domainSchema)) {
            return false;
        }
        if (this.domainName == null) {
            if (other.domainName != null) {
                return false;
            }
        } else if (!this.domainName.equals(other.domainName)) {
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
        if (this.ordinalPosition == null) {
            if (other.ordinalPosition != null) {
                return false;
            }
        } else if (!this.ordinalPosition.equals(other.ordinalPosition)) {
            return false;
        }
        if (this.identityGeneration == null) {
            if (other.identityGeneration != null) {
                return false;
            }
        } else if (!this.identityGeneration.equals(other.identityGeneration)) {
            return false;
        }
        if (this.isNullable == null) {
            if (other.isNullable != null) {
                return false;
            }
        } else if (!this.isNullable.equals(other.isNullable)) {
            return false;
        }
        if (this.columnDefault == null) {
            if (other.columnDefault != null) {
                return false;
            }
        } else if (!this.columnDefault.equals(other.columnDefault)) {
            return false;
        }
        if (this.comment == null) {
            if (other.comment != null) {
                return false;
            }
        } else if (!this.comment.equals(other.comment)) {
            return false;
        }
        if (this.readonly == null) {
            if (other.readonly != null) {
                return false;
            }
        } else if (!this.readonly.equals(other.readonly)) {
            return false;
        }
        if (this.isGenerated == null) {
            if (other.isGenerated != null) {
                return false;
            }
        } else if (!this.isGenerated.equals(other.isGenerated)) {
            return false;
        }
        if (this.generationExpression == null) {
            if (other.generationExpression != null) {
                return false;
            }
        } else if (!this.generationExpression.equals(other.generationExpression)) {
            return false;
        }
        if (this.generationOption == null) {
            if (other.generationOption != null) {
                return false;
            }
            return true;
        }
        if (!this.generationOption.equals(other.generationOption)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.tableCatalog == null ? 0 : this.tableCatalog.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.tableSchema == null ? 0 : this.tableSchema.hashCode()))) + (this.tableName == null ? 0 : this.tableName.hashCode()))) + (this.columnName == null ? 0 : this.columnName.hashCode()))) + (this.dataType == null ? 0 : this.dataType.hashCode()))) + (this.characterMaximumLength == null ? 0 : this.characterMaximumLength.hashCode()))) + (this.numericPrecision == null ? 0 : this.numericPrecision.hashCode()))) + (this.numericScale == null ? 0 : this.numericScale.hashCode()))) + (this.domainCatalog == null ? 0 : this.domainCatalog.hashCode()))) + (this.domainSchema == null ? 0 : this.domainSchema.hashCode()))) + (this.domainName == null ? 0 : this.domainName.hashCode()))) + (this.udtCatalog == null ? 0 : this.udtCatalog.hashCode()))) + (this.udtSchema == null ? 0 : this.udtSchema.hashCode()))) + (this.udtName == null ? 0 : this.udtName.hashCode()))) + (this.ordinalPosition == null ? 0 : this.ordinalPosition.hashCode()))) + (this.identityGeneration == null ? 0 : this.identityGeneration.hashCode()))) + (this.isNullable == null ? 0 : this.isNullable.hashCode()))) + (this.columnDefault == null ? 0 : this.columnDefault.hashCode()))) + (this.comment == null ? 0 : this.comment.hashCode()))) + (this.readonly == null ? 0 : this.readonly.hashCode()))) + (this.isGenerated == null ? 0 : this.isGenerated.hashCode()))) + (this.generationExpression == null ? 0 : this.generationExpression.hashCode()))) + (this.generationOption == null ? 0 : this.generationOption.hashCode());
    }
}
