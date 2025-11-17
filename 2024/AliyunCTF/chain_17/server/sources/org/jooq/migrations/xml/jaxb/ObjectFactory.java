package org.jooq.migrations.xml.jaxb;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/migrations/xml/jaxb/ObjectFactory.class */
public class ObjectFactory {
    private static final QName _Migrations_QNAME = new QName("http://www.jooq.org/xsd/jooq-migrations-3.19.0.xsd", "migrations");

    public MigrationsType createMigrationsType() {
        return new MigrationsType();
    }

    public CommitType createCommitType() {
        return new CommitType();
    }

    public ParentType createParentType() {
        return new ParentType();
    }

    public TagType createTagType() {
        return new TagType();
    }

    public FileType createFileType() {
        return new FileType();
    }

    @XmlElementDecl(namespace = "http://www.jooq.org/xsd/jooq-migrations-3.19.0.xsd", name = "migrations")
    public JAXBElement<MigrationsType> createMigrations(MigrationsType value) {
        return new JAXBElement<>(_Migrations_QNAME, MigrationsType.class, (Class) null, value);
    }
}
