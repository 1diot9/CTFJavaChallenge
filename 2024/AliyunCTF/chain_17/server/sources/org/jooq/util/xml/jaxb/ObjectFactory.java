package org.jooq.util.xml.jaxb;

import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/xml/jaxb/ObjectFactory.class */
public class ObjectFactory {
    public InformationSchema createInformationSchema() {
        return new InformationSchema();
    }

    public Catalog createCatalog() {
        return new Catalog();
    }

    public CheckConstraint createCheckConstraint() {
        return new CheckConstraint();
    }

    public Column createColumn() {
        return new Column();
    }

    public DomainConstraint createDomainConstraint() {
        return new DomainConstraint();
    }

    public Domain createDomain() {
        return new Domain();
    }

    public ElementType createElementType() {
        return new ElementType();
    }

    public IndexColumnUsage createIndexColumnUsage() {
        return new IndexColumnUsage();
    }

    public Index createIndex() {
        return new Index();
    }

    public KeyColumnUsage createKeyColumnUsage() {
        return new KeyColumnUsage();
    }

    public Parameter createParameter() {
        return new Parameter();
    }

    public ReferentialConstraint createReferentialConstraint() {
        return new ReferentialConstraint();
    }

    public Routine createRoutine() {
        return new Routine();
    }

    public Schema createSchema() {
        return new Schema();
    }

    public Sequence createSequence() {
        return new Sequence();
    }

    public TableConstraint createTableConstraint() {
        return new TableConstraint();
    }

    public Table createTable() {
        return new Table();
    }

    public View createView() {
        return new View();
    }

    public Trigger createTrigger() {
        return new Trigger();
    }
}
