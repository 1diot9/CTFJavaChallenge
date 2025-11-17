package org.springframework.objenesis.instantiator.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/annotations/Instantiator.class */
public @interface Instantiator {
    Typology value();
}
