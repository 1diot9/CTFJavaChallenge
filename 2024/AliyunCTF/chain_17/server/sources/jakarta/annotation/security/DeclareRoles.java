package jakarta.annotation.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: server.jar:BOOT-INF/lib/jakarta.annotation-api-2.1.1.jar:jakarta/annotation/security/DeclareRoles.class */
public @interface DeclareRoles {
    String[] value();
}
