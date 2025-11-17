package org.springframework.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

@Target({ElementType.PACKAGE})
@TypeQualifierDefault({ElementType.METHOD, ElementType.PARAMETER})
@Nonnull
@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/lang/NonNullApi.class */
public @interface NonNullApi {
}
