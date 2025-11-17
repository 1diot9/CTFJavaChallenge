package org.jooq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.ApiStatus;

@Target({ElementType.METHOD})
@ApiStatus.Internal
@Documented
@Retention(RetentionPolicy.CLASS)
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CheckReturnValue.class */
public @interface CheckReturnValue {
}
