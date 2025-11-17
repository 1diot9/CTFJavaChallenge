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
@XmlType(name = "RenderMapping", propOrder = {})
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/RenderMapping.class */
public class RenderMapping extends SettingsBase implements Serializable, Cloneable, XMLAppendable {
    private static final long serialVersionUID = 31900;
    protected String defaultCatalog;
    protected String defaultSchema;

    @XmlElementWrapper(name = "catalogs")
    @XmlElement(name = "catalog")
    protected List<MappedCatalog> catalogs;

    @XmlElementWrapper(name = "schemata")
    @XmlElement(name = "schema")
    protected List<MappedSchema> schemata;

    @Override // org.jooq.conf.SettingsBase
    public /* bridge */ /* synthetic */ Object clone() {
        return super.clone();
    }

    public String getDefaultCatalog() {
        return this.defaultCatalog;
    }

    public void setDefaultCatalog(String value) {
        this.defaultCatalog = value;
    }

    public String getDefaultSchema() {
        return this.defaultSchema;
    }

    public void setDefaultSchema(String value) {
        this.defaultSchema = value;
    }

    public List<MappedCatalog> getCatalogs() {
        if (this.catalogs == null) {
            this.catalogs = new ArrayList();
        }
        return this.catalogs;
    }

    public void setCatalogs(List<MappedCatalog> catalogs) {
        this.catalogs = catalogs;
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

    public RenderMapping withDefaultCatalog(String value) {
        setDefaultCatalog(value);
        return this;
    }

    public RenderMapping withDefaultSchema(String value) {
        setDefaultSchema(value);
        return this;
    }

    public RenderMapping withCatalogs(MappedCatalog... values) {
        if (values != null) {
            for (MappedCatalog value : values) {
                getCatalogs().add(value);
            }
        }
        return this;
    }

    public RenderMapping withCatalogs(Collection<MappedCatalog> values) {
        if (values != null) {
            getCatalogs().addAll(values);
        }
        return this;
    }

    public RenderMapping withCatalogs(List<MappedCatalog> catalogs) {
        setCatalogs(catalogs);
        return this;
    }

    public RenderMapping withSchemata(MappedSchema... values) {
        if (values != null) {
            for (MappedSchema value : values) {
                getSchemata().add(value);
            }
        }
        return this;
    }

    public RenderMapping withSchemata(Collection<MappedSchema> values) {
        if (values != null) {
            getSchemata().addAll(values);
        }
        return this;
    }

    public RenderMapping withSchemata(List<MappedSchema> schemata) {
        setSchemata(schemata);
        return this;
    }

    @Override // org.jooq.util.jaxb.tools.XMLAppendable
    public final void appendTo(XMLBuilder builder) {
        builder.append("defaultCatalog", this.defaultCatalog);
        builder.append("defaultSchema", this.defaultSchema);
        builder.append("catalogs", "catalog", this.catalogs);
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
        RenderMapping other = (RenderMapping) that;
        if (this.defaultCatalog == null) {
            if (other.defaultCatalog != null) {
                return false;
            }
        } else if (!this.defaultCatalog.equals(other.defaultCatalog)) {
            return false;
        }
        if (this.defaultSchema == null) {
            if (other.defaultSchema != null) {
                return false;
            }
        } else if (!this.defaultSchema.equals(other.defaultSchema)) {
            return false;
        }
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
            return true;
        }
        if (!this.schemata.equals(other.schemata)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.defaultCatalog == null ? 0 : this.defaultCatalog.hashCode());
        return (31 * ((31 * ((31 * result) + (this.defaultSchema == null ? 0 : this.defaultSchema.hashCode()))) + (this.catalogs == null ? 0 : this.catalogs.hashCode()))) + (this.schemata == null ? 0 : this.schemata.hashCode());
    }
}
