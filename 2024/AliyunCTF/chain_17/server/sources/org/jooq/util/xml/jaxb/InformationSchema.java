package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

@XmlRootElement(name = "information_schema")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/InformationSchema.class */
public class InformationSchema implements Serializable, XMLAppendable {
    private static final long serialVersionUID = 31900;

    @XmlElementWrapper(name = "catalogs")
    @XmlElement(name = "catalog")
    protected List<Catalog> catalogs;

    @XmlElementWrapper(name = "schemata")
    @XmlElement(name = "schema")
    protected List<Schema> schemata;

    @XmlElementWrapper(name = "sequences")
    @XmlElement(name = "sequence")
    protected List<Sequence> sequences;

    @XmlElementWrapper(name = "tables")
    @XmlElement(name = "table")
    protected List<Table> tables;

    @XmlElementWrapper(name = ResourceBundleViewResolver.DEFAULT_BASENAME)
    @XmlElement(name = "view")
    protected List<View> views;

    @XmlElementWrapper(name = "columns")
    @XmlElement(name = "column")
    protected List<Column> columns;

    @XmlElementWrapper(name = "table_constraints")
    @XmlElement(name = "table_constraint")
    protected List<TableConstraint> tableConstraints;

    @XmlElementWrapper(name = "key_column_usages")
    @XmlElement(name = "key_column_usage")
    protected List<KeyColumnUsage> keyColumnUsages;

    @XmlElementWrapper(name = "referential_constraints")
    @XmlElement(name = "referential_constraint")
    protected List<ReferentialConstraint> referentialConstraints;

    @XmlElementWrapper(name = "check_constraints")
    @XmlElement(name = "check_constraint")
    protected List<CheckConstraint> checkConstraints;

    @XmlElementWrapper(name = "domains")
    @XmlElement(name = "domain")
    protected List<Domain> domains;

    @XmlElementWrapper(name = "domain_constraints")
    @XmlElement(name = "domainConstraint")
    protected List<DomainConstraint> domainConstraints;

    @XmlElementWrapper(name = "indexes")
    @XmlElement(name = BeanDefinitionParserDelegate.INDEX_ATTRIBUTE)
    protected List<Index> indexes;

    @XmlElementWrapper(name = "index_column_usages")
    @XmlElement(name = "index_column_usage")
    protected List<IndexColumnUsage> indexColumnUsages;

    @XmlElementWrapper(name = "routines")
    @XmlElement(name = "routine")
    protected List<Routine> routines;

    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    protected List<Parameter> parameters;

    @XmlElementWrapper(name = "element_types")
    @XmlElement(name = "element_type")
    protected List<ElementType> elementTypes;

    @XmlElementWrapper(name = "triggers")
    @XmlElement(name = "trigger")
    protected List<Trigger> triggers;

    public List<Catalog> getCatalogs() {
        if (this.catalogs == null) {
            this.catalogs = new ArrayList();
        }
        return this.catalogs;
    }

    public void setCatalogs(List<Catalog> catalogs) {
        this.catalogs = catalogs;
    }

    public List<Schema> getSchemata() {
        if (this.schemata == null) {
            this.schemata = new ArrayList();
        }
        return this.schemata;
    }

    public void setSchemata(List<Schema> schemata) {
        this.schemata = schemata;
    }

    public List<Sequence> getSequences() {
        if (this.sequences == null) {
            this.sequences = new ArrayList();
        }
        return this.sequences;
    }

    public void setSequences(List<Sequence> sequences) {
        this.sequences = sequences;
    }

    public List<Table> getTables() {
        if (this.tables == null) {
            this.tables = new ArrayList();
        }
        return this.tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public List<View> getViews() {
        if (this.views == null) {
            this.views = new ArrayList();
        }
        return this.views;
    }

    public void setViews(List<View> views) {
        this.views = views;
    }

    public List<Column> getColumns() {
        if (this.columns == null) {
            this.columns = new ArrayList();
        }
        return this.columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<TableConstraint> getTableConstraints() {
        if (this.tableConstraints == null) {
            this.tableConstraints = new ArrayList();
        }
        return this.tableConstraints;
    }

    public void setTableConstraints(List<TableConstraint> tableConstraints) {
        this.tableConstraints = tableConstraints;
    }

    public List<KeyColumnUsage> getKeyColumnUsages() {
        if (this.keyColumnUsages == null) {
            this.keyColumnUsages = new ArrayList();
        }
        return this.keyColumnUsages;
    }

    public void setKeyColumnUsages(List<KeyColumnUsage> keyColumnUsages) {
        this.keyColumnUsages = keyColumnUsages;
    }

    public List<ReferentialConstraint> getReferentialConstraints() {
        if (this.referentialConstraints == null) {
            this.referentialConstraints = new ArrayList();
        }
        return this.referentialConstraints;
    }

    public void setReferentialConstraints(List<ReferentialConstraint> referentialConstraints) {
        this.referentialConstraints = referentialConstraints;
    }

    public List<CheckConstraint> getCheckConstraints() {
        if (this.checkConstraints == null) {
            this.checkConstraints = new ArrayList();
        }
        return this.checkConstraints;
    }

    public void setCheckConstraints(List<CheckConstraint> checkConstraints) {
        this.checkConstraints = checkConstraints;
    }

    public List<Domain> getDomains() {
        if (this.domains == null) {
            this.domains = new ArrayList();
        }
        return this.domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }

    public List<DomainConstraint> getDomainConstraints() {
        if (this.domainConstraints == null) {
            this.domainConstraints = new ArrayList();
        }
        return this.domainConstraints;
    }

    public void setDomainConstraints(List<DomainConstraint> domainConstraints) {
        this.domainConstraints = domainConstraints;
    }

    public List<Index> getIndexes() {
        if (this.indexes == null) {
            this.indexes = new ArrayList();
        }
        return this.indexes;
    }

    public void setIndexes(List<Index> indexes) {
        this.indexes = indexes;
    }

    public List<IndexColumnUsage> getIndexColumnUsages() {
        if (this.indexColumnUsages == null) {
            this.indexColumnUsages = new ArrayList();
        }
        return this.indexColumnUsages;
    }

    public void setIndexColumnUsages(List<IndexColumnUsage> indexColumnUsages) {
        this.indexColumnUsages = indexColumnUsages;
    }

    public List<Routine> getRoutines() {
        if (this.routines == null) {
            this.routines = new ArrayList();
        }
        return this.routines;
    }

    public void setRoutines(List<Routine> routines) {
        this.routines = routines;
    }

    public List<Parameter> getParameters() {
        if (this.parameters == null) {
            this.parameters = new ArrayList();
        }
        return this.parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public List<ElementType> getElementTypes() {
        if (this.elementTypes == null) {
            this.elementTypes = new ArrayList();
        }
        return this.elementTypes;
    }

    public void setElementTypes(List<ElementType> elementTypes) {
        this.elementTypes = elementTypes;
    }

    public List<Trigger> getTriggers() {
        if (this.triggers == null) {
            this.triggers = new ArrayList();
        }
        return this.triggers;
    }

    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
    }

    public InformationSchema withCatalogs(Catalog... values) {
        if (values != null) {
            for (Catalog value : values) {
                getCatalogs().add(value);
            }
        }
        return this;
    }

    public InformationSchema withCatalogs(Collection<Catalog> values) {
        if (values != null) {
            getCatalogs().addAll(values);
        }
        return this;
    }

    public InformationSchema withCatalogs(List<Catalog> catalogs) {
        setCatalogs(catalogs);
        return this;
    }

    public InformationSchema withSchemata(Schema... values) {
        if (values != null) {
            for (Schema value : values) {
                getSchemata().add(value);
            }
        }
        return this;
    }

    public InformationSchema withSchemata(Collection<Schema> values) {
        if (values != null) {
            getSchemata().addAll(values);
        }
        return this;
    }

    public InformationSchema withSchemata(List<Schema> schemata) {
        setSchemata(schemata);
        return this;
    }

    public InformationSchema withSequences(Sequence... values) {
        if (values != null) {
            for (Sequence value : values) {
                getSequences().add(value);
            }
        }
        return this;
    }

    public InformationSchema withSequences(Collection<Sequence> values) {
        if (values != null) {
            getSequences().addAll(values);
        }
        return this;
    }

    public InformationSchema withSequences(List<Sequence> sequences) {
        setSequences(sequences);
        return this;
    }

    public InformationSchema withTables(Table... values) {
        if (values != null) {
            for (Table value : values) {
                getTables().add(value);
            }
        }
        return this;
    }

    public InformationSchema withTables(Collection<Table> values) {
        if (values != null) {
            getTables().addAll(values);
        }
        return this;
    }

    public InformationSchema withTables(List<Table> tables) {
        setTables(tables);
        return this;
    }

    public InformationSchema withViews(View... values) {
        if (values != null) {
            for (View value : values) {
                getViews().add(value);
            }
        }
        return this;
    }

    public InformationSchema withViews(Collection<View> values) {
        if (values != null) {
            getViews().addAll(values);
        }
        return this;
    }

    public InformationSchema withViews(List<View> views) {
        setViews(views);
        return this;
    }

    public InformationSchema withColumns(Column... values) {
        if (values != null) {
            for (Column value : values) {
                getColumns().add(value);
            }
        }
        return this;
    }

    public InformationSchema withColumns(Collection<Column> values) {
        if (values != null) {
            getColumns().addAll(values);
        }
        return this;
    }

    public InformationSchema withColumns(List<Column> columns) {
        setColumns(columns);
        return this;
    }

    public InformationSchema withTableConstraints(TableConstraint... values) {
        if (values != null) {
            for (TableConstraint value : values) {
                getTableConstraints().add(value);
            }
        }
        return this;
    }

    public InformationSchema withTableConstraints(Collection<TableConstraint> values) {
        if (values != null) {
            getTableConstraints().addAll(values);
        }
        return this;
    }

    public InformationSchema withTableConstraints(List<TableConstraint> tableConstraints) {
        setTableConstraints(tableConstraints);
        return this;
    }

    public InformationSchema withKeyColumnUsages(KeyColumnUsage... values) {
        if (values != null) {
            for (KeyColumnUsage value : values) {
                getKeyColumnUsages().add(value);
            }
        }
        return this;
    }

    public InformationSchema withKeyColumnUsages(Collection<KeyColumnUsage> values) {
        if (values != null) {
            getKeyColumnUsages().addAll(values);
        }
        return this;
    }

    public InformationSchema withKeyColumnUsages(List<KeyColumnUsage> keyColumnUsages) {
        setKeyColumnUsages(keyColumnUsages);
        return this;
    }

    public InformationSchema withReferentialConstraints(ReferentialConstraint... values) {
        if (values != null) {
            for (ReferentialConstraint value : values) {
                getReferentialConstraints().add(value);
            }
        }
        return this;
    }

    public InformationSchema withReferentialConstraints(Collection<ReferentialConstraint> values) {
        if (values != null) {
            getReferentialConstraints().addAll(values);
        }
        return this;
    }

    public InformationSchema withReferentialConstraints(List<ReferentialConstraint> referentialConstraints) {
        setReferentialConstraints(referentialConstraints);
        return this;
    }

    public InformationSchema withCheckConstraints(CheckConstraint... values) {
        if (values != null) {
            for (CheckConstraint value : values) {
                getCheckConstraints().add(value);
            }
        }
        return this;
    }

    public InformationSchema withCheckConstraints(Collection<CheckConstraint> values) {
        if (values != null) {
            getCheckConstraints().addAll(values);
        }
        return this;
    }

    public InformationSchema withCheckConstraints(List<CheckConstraint> checkConstraints) {
        setCheckConstraints(checkConstraints);
        return this;
    }

    public InformationSchema withDomains(Domain... values) {
        if (values != null) {
            for (Domain value : values) {
                getDomains().add(value);
            }
        }
        return this;
    }

    public InformationSchema withDomains(Collection<Domain> values) {
        if (values != null) {
            getDomains().addAll(values);
        }
        return this;
    }

    public InformationSchema withDomains(List<Domain> domains) {
        setDomains(domains);
        return this;
    }

    public InformationSchema withDomainConstraints(DomainConstraint... values) {
        if (values != null) {
            for (DomainConstraint value : values) {
                getDomainConstraints().add(value);
            }
        }
        return this;
    }

    public InformationSchema withDomainConstraints(Collection<DomainConstraint> values) {
        if (values != null) {
            getDomainConstraints().addAll(values);
        }
        return this;
    }

    public InformationSchema withDomainConstraints(List<DomainConstraint> domainConstraints) {
        setDomainConstraints(domainConstraints);
        return this;
    }

    public InformationSchema withIndexes(Index... values) {
        if (values != null) {
            for (Index value : values) {
                getIndexes().add(value);
            }
        }
        return this;
    }

    public InformationSchema withIndexes(Collection<Index> values) {
        if (values != null) {
            getIndexes().addAll(values);
        }
        return this;
    }

    public InformationSchema withIndexes(List<Index> indexes) {
        setIndexes(indexes);
        return this;
    }

    public InformationSchema withIndexColumnUsages(IndexColumnUsage... values) {
        if (values != null) {
            for (IndexColumnUsage value : values) {
                getIndexColumnUsages().add(value);
            }
        }
        return this;
    }

    public InformationSchema withIndexColumnUsages(Collection<IndexColumnUsage> values) {
        if (values != null) {
            getIndexColumnUsages().addAll(values);
        }
        return this;
    }

    public InformationSchema withIndexColumnUsages(List<IndexColumnUsage> indexColumnUsages) {
        setIndexColumnUsages(indexColumnUsages);
        return this;
    }

    public InformationSchema withRoutines(Routine... values) {
        if (values != null) {
            for (Routine value : values) {
                getRoutines().add(value);
            }
        }
        return this;
    }

    public InformationSchema withRoutines(Collection<Routine> values) {
        if (values != null) {
            getRoutines().addAll(values);
        }
        return this;
    }

    public InformationSchema withRoutines(List<Routine> routines) {
        setRoutines(routines);
        return this;
    }

    public InformationSchema withParameters(Parameter... values) {
        if (values != null) {
            for (Parameter value : values) {
                getParameters().add(value);
            }
        }
        return this;
    }

    public InformationSchema withParameters(Collection<Parameter> values) {
        if (values != null) {
            getParameters().addAll(values);
        }
        return this;
    }

    public InformationSchema withParameters(List<Parameter> parameters) {
        setParameters(parameters);
        return this;
    }

    public InformationSchema withElementTypes(ElementType... values) {
        if (values != null) {
            for (ElementType value : values) {
                getElementTypes().add(value);
            }
        }
        return this;
    }

    public InformationSchema withElementTypes(Collection<ElementType> values) {
        if (values != null) {
            getElementTypes().addAll(values);
        }
        return this;
    }

    public InformationSchema withElementTypes(List<ElementType> elementTypes) {
        setElementTypes(elementTypes);
        return this;
    }

    public InformationSchema withTriggers(Trigger... values) {
        if (values != null) {
            for (Trigger value : values) {
                getTriggers().add(value);
            }
        }
        return this;
    }

    public InformationSchema withTriggers(Collection<Trigger> values) {
        if (values != null) {
            getTriggers().addAll(values);
        }
        return this;
    }

    public InformationSchema withTriggers(List<Trigger> triggers) {
        setTriggers(triggers);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("catalogs", "catalog", this.catalogs);
        builder.append("schemata", "schema", this.schemata);
        builder.append("sequences", "sequence", this.sequences);
        builder.append("tables", "table", this.tables);
        builder.append(ResourceBundleViewResolver.DEFAULT_BASENAME, "view", this.views);
        builder.append("columns", "column", this.columns);
        builder.append("table_constraints", "table_constraint", this.tableConstraints);
        builder.append("key_column_usages", "key_column_usage", this.keyColumnUsages);
        builder.append("referential_constraints", "referential_constraint", this.referentialConstraints);
        builder.append("check_constraints", "check_constraint", this.checkConstraints);
        builder.append("domains", "domain", this.domains);
        builder.append("domain_constraints", "domainConstraint", this.domainConstraints);
        builder.append("indexes", BeanDefinitionParserDelegate.INDEX_ATTRIBUTE, this.indexes);
        builder.append("index_column_usages", "index_column_usage", this.indexColumnUsages);
        builder.append("routines", "routine", this.routines);
        builder.append("parameters", "parameter", this.parameters);
        builder.append("element_types", "element_type", this.elementTypes);
        builder.append("triggers", "trigger", this.triggers);
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
        InformationSchema other = (InformationSchema) that;
        if (this.catalogs == null) {
            if (other.catalogs != null) {
                return false;
            }
        } else if (!this.catalogs.equals(other.catalogs)) {
            return false;
        }
        if (this.schemata == null) {
            if (other.schemata != null) {
                return false;
            }
        } else if (!this.schemata.equals(other.schemata)) {
            return false;
        }
        if (this.sequences == null) {
            if (other.sequences != null) {
                return false;
            }
        } else if (!this.sequences.equals(other.sequences)) {
            return false;
        }
        if (this.tables == null) {
            if (other.tables != null) {
                return false;
            }
        } else if (!this.tables.equals(other.tables)) {
            return false;
        }
        if (this.views == null) {
            if (other.views != null) {
                return false;
            }
        } else if (!this.views.equals(other.views)) {
            return false;
        }
        if (this.columns == null) {
            if (other.columns != null) {
                return false;
            }
        } else if (!this.columns.equals(other.columns)) {
            return false;
        }
        if (this.tableConstraints == null) {
            if (other.tableConstraints != null) {
                return false;
            }
        } else if (!this.tableConstraints.equals(other.tableConstraints)) {
            return false;
        }
        if (this.keyColumnUsages == null) {
            if (other.keyColumnUsages != null) {
                return false;
            }
        } else if (!this.keyColumnUsages.equals(other.keyColumnUsages)) {
            return false;
        }
        if (this.referentialConstraints == null) {
            if (other.referentialConstraints != null) {
                return false;
            }
        } else if (!this.referentialConstraints.equals(other.referentialConstraints)) {
            return false;
        }
        if (this.checkConstraints == null) {
            if (other.checkConstraints != null) {
                return false;
            }
        } else if (!this.checkConstraints.equals(other.checkConstraints)) {
            return false;
        }
        if (this.domains == null) {
            if (other.domains != null) {
                return false;
            }
        } else if (!this.domains.equals(other.domains)) {
            return false;
        }
        if (this.domainConstraints == null) {
            if (other.domainConstraints != null) {
                return false;
            }
        } else if (!this.domainConstraints.equals(other.domainConstraints)) {
            return false;
        }
        if (this.indexes == null) {
            if (other.indexes != null) {
                return false;
            }
        } else if (!this.indexes.equals(other.indexes)) {
            return false;
        }
        if (this.indexColumnUsages == null) {
            if (other.indexColumnUsages != null) {
                return false;
            }
        } else if (!this.indexColumnUsages.equals(other.indexColumnUsages)) {
            return false;
        }
        if (this.routines == null) {
            if (other.routines != null) {
                return false;
            }
        } else if (!this.routines.equals(other.routines)) {
            return false;
        }
        if (this.parameters == null) {
            if (other.parameters != null) {
                return false;
            }
        } else if (!this.parameters.equals(other.parameters)) {
            return false;
        }
        if (this.elementTypes == null) {
            if (other.elementTypes != null) {
                return false;
            }
        } else if (!this.elementTypes.equals(other.elementTypes)) {
            return false;
        }
        if (this.triggers == null) {
            if (other.triggers != null) {
                return false;
            }
            return true;
        }
        if (!this.triggers.equals(other.triggers)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.catalogs == null ? 0 : this.catalogs.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.schemata == null ? 0 : this.schemata.hashCode()))) + (this.sequences == null ? 0 : this.sequences.hashCode()))) + (this.tables == null ? 0 : this.tables.hashCode()))) + (this.views == null ? 0 : this.views.hashCode()))) + (this.columns == null ? 0 : this.columns.hashCode()))) + (this.tableConstraints == null ? 0 : this.tableConstraints.hashCode()))) + (this.keyColumnUsages == null ? 0 : this.keyColumnUsages.hashCode()))) + (this.referentialConstraints == null ? 0 : this.referentialConstraints.hashCode()))) + (this.checkConstraints == null ? 0 : this.checkConstraints.hashCode()))) + (this.domains == null ? 0 : this.domains.hashCode()))) + (this.domainConstraints == null ? 0 : this.domainConstraints.hashCode()))) + (this.indexes == null ? 0 : this.indexes.hashCode()))) + (this.indexColumnUsages == null ? 0 : this.indexColumnUsages.hashCode()))) + (this.routines == null ? 0 : this.routines.hashCode()))) + (this.parameters == null ? 0 : this.parameters.hashCode()))) + (this.elementTypes == null ? 0 : this.elementTypes.hashCode()))) + (this.triggers == null ? 0 : this.triggers.hashCode());
    }
}
