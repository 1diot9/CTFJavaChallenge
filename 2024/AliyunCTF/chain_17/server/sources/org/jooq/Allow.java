package org.jooq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.PACKAGE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Allow.class */
public @interface Allow {

    @Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.PACKAGE})
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Allow$PlainSQL.class */
    public @interface PlainSQL {
    }

    SQLDialect[] value() default {SQLDialect.CUBRID, SQLDialect.DEFAULT, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB};
}
