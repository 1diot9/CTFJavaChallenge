package jakarta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Resources.class)
/* loaded from: server.jar:BOOT-INF/lib/jakarta.annotation-api-2.1.1.jar:jakarta/annotation/Resource.class */
public @interface Resource {

    /* loaded from: server.jar:BOOT-INF/lib/jakarta.annotation-api-2.1.1.jar:jakarta/annotation/Resource$AuthenticationType.class */
    public enum AuthenticationType {
        CONTAINER,
        APPLICATION
    }

    String name() default "";

    String lookup() default "";

    Class<?> type() default Object.class;

    AuthenticationType authenticationType() default AuthenticationType.CONTAINER;

    boolean shareable() default true;

    String mappedName() default "";

    String description() default "";
}
