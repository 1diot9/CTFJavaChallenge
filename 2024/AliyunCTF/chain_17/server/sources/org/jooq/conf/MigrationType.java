package org.jooq.conf;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jooq.util.jaxb.tools.XMLAppendable;
import org.jooq.util.jaxb.tools.XMLBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MigrationType", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/MigrationType.class */
public class MigrationType extends SettingsBase implements Serializable, Cloneable, XMLAppendable {
    private static final long serialVersionUID = 31500;

    @XmlElementWrapper(name = "schemata")
    @XmlElement(name = "schema")
    protected List<MigrationSchema> schemata;

    @Override // org.jooq.conf.SettingsBase
    public /* bridge */ /* synthetic */ Object clone() {
        return super.clone();
    }

    public List<MigrationSchema> getSchemata() {
        if (this.schemata == null) {
            this.schemata = new ArrayList();
        }
        return this.schemata;
    }

    public void setSchemata(List<MigrationSchema> schemata) {
        this.schemata = schemata;
    }

    public MigrationType withSchemata(MigrationSchema... values) {
        if (values != null) {
            for (MigrationSchema value : values) {
                getSchemata().add(value);
            }
        }
        return this;
    }

    public MigrationType withSchemata(Collection<MigrationSchema> values) {
        if (values != null) {
            getSchemata().addAll(values);
        }
        return this;
    }

    public MigrationType withSchemata(List<MigrationSchema> schemata) {
        setSchemata(schemata);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
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
        MigrationType other = (MigrationType) that;
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
        int result = (31 * 1) + (this.schemata == null ? 0 : this.schemata.hashCode());
        return result;
    }
}
