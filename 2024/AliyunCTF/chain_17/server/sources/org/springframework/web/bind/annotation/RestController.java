package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

@Target({ElementType.TYPE})
@Controller
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ResponseBody
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/annotation/RestController.class */
public @interface RestController {
    @AliasFor(annotation = Controller.class)
    String value() default "";
}
