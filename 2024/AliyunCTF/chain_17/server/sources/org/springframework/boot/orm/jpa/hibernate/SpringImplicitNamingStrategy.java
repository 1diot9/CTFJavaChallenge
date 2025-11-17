package org.springframework.boot.orm.jpa.hibernate;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitJoinTableNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/orm/jpa/hibernate/SpringImplicitNamingStrategy.class */
public class SpringImplicitNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl {
    public Identifier determineJoinTableName(ImplicitJoinTableNameSource source) {
        String name = source.getOwningPhysicalTableName() + "_" + source.getAssociationOwningAttributePath().getProperty();
        return toIdentifier(name, source.getBuildingContext());
    }
}
