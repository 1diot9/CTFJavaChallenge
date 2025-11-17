package jakarta.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: server.jar:BOOT-INF/lib/jakarta.annotation-api-2.1.1.jar:jakarta/annotation/Priority.class */
public @interface Priority {
    int value();
}
