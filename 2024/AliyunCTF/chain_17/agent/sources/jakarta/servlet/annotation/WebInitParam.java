package jakarta.servlet.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/annotation/WebInitParam.class */
public @interface WebInitParam {
    String name();

    String value();

    String description() default "";
}
