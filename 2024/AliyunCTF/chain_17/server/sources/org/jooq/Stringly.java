package org.jooq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Stringly.class */
public final class Stringly {

    @Target({ElementType.PARAMETER})
    @Inherited
    @Retention(RetentionPolicy.SOURCE)
    @Documented
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Stringly$Comment.class */
    public @interface Comment {
    }

    @Target({ElementType.PARAMETER})
    @Inherited
    @Retention(RetentionPolicy.SOURCE)
    @Documented
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Stringly$Keyword.class */
    public @interface Keyword {
    }

    @Target({ElementType.PARAMETER})
    @Inherited
    @Retention(RetentionPolicy.SOURCE)
    @Documented
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Stringly$Name.class */
    public @interface Name {
    }

    @Target({ElementType.PARAMETER})
    @Inherited
    @Retention(RetentionPolicy.SOURCE)
    @Documented
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Stringly$Param.class */
    public @interface Param {
    }

    @Target({ElementType.PARAMETER})
    @Inherited
    @Retention(RetentionPolicy.SOURCE)
    @Documented
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Stringly$SQL.class */
    public @interface SQL {
    }
}
