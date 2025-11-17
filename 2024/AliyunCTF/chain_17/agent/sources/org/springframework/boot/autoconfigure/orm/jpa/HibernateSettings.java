package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernateSettings.class */
public class HibernateSettings {
    private Supplier<String> ddlAuto;
    private Collection<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers;

    public HibernateSettings ddlAuto(Supplier<String> ddlAuto) {
        this.ddlAuto = ddlAuto;
        return this;
    }

    public String getDdlAuto() {
        if (this.ddlAuto != null) {
            return this.ddlAuto.get();
        }
        return null;
    }

    public HibernateSettings hibernatePropertiesCustomizers(Collection<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers) {
        this.hibernatePropertiesCustomizers = new ArrayList(hibernatePropertiesCustomizers);
        return this;
    }

    public Collection<HibernatePropertiesCustomizer> getHibernatePropertiesCustomizers() {
        return this.hibernatePropertiesCustomizers;
    }
}
