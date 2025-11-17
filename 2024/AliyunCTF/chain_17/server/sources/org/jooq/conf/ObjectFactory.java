package org.jooq.conf;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.jooq.Constants;

@XmlRegistry
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/ObjectFactory.class */
public class ObjectFactory {
    private static final QName _Settings_QNAME = new QName(Constants.NS_RUNTIME, "settings");

    public Settings createSettings() {
        return new Settings();
    }

    public ParseSearchSchema createParseSearchSchema() {
        return new ParseSearchSchema();
    }

    public InterpreterSearchSchema createInterpreterSearchSchema() {
        return new InterpreterSearchSchema();
    }

    public MigrationSchema createMigrationSchema() {
        return new MigrationSchema();
    }

    public RenderMapping createRenderMapping() {
        return new RenderMapping();
    }

    public MappedCatalog createMappedCatalog() {
        return new MappedCatalog();
    }

    public MappedSchema createMappedSchema() {
        return new MappedSchema();
    }

    public MappedTable createMappedTable() {
        return new MappedTable();
    }

    public MappedUDT createMappedUDT() {
        return new MappedUDT();
    }

    public RenderFormatting createRenderFormatting() {
        return new RenderFormatting();
    }

    @XmlElementDecl(namespace = Constants.NS_RUNTIME, name = "settings")
    public JAXBElement<Settings> createSettings(Settings value) {
        return new JAXBElement<>(_Settings_QNAME, Settings.class, (Class) null, value);
    }
}
