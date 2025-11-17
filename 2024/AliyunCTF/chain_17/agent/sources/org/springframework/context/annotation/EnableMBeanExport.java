package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.jmx.support.RegistrationPolicy;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MBeanExportConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/EnableMBeanExport.class */
public @interface EnableMBeanExport {
    String defaultDomain() default "";

    String server() default "";

    RegistrationPolicy registration() default RegistrationPolicy.FAIL_ON_EXISTING;
}
