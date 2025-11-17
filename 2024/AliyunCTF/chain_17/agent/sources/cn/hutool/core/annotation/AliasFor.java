package cn.hutool.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Link(type = RelationType.ALIAS_FOR)
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/AliasFor.class */
public @interface AliasFor {
    @Link(annotation = Link.class, attribute = "annotation", type = RelationType.FORCE_ALIAS_FOR)
    Class<? extends Annotation> annotation() default Annotation.class;

    @Link(annotation = Link.class, attribute = BeanDefinitionParserDelegate.QUALIFIER_ATTRIBUTE_ELEMENT, type = RelationType.FORCE_ALIAS_FOR)
    String attribute() default "";
}
