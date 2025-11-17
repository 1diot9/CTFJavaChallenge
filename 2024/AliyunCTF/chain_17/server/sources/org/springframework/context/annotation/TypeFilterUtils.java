package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/TypeFilterUtils.class */
public abstract class TypeFilterUtils {
    public static List<TypeFilter> createTypeFiltersFor(AnnotationAttributes filterAttributes, Environment environment, ResourceLoader resourceLoader, BeanDefinitionRegistry registry) {
        List<TypeFilter> typeFilters = new ArrayList<>();
        FilterType filterType = (FilterType) filterAttributes.getEnum("type");
        for (Class<?> filterClass : filterAttributes.getClassArray("classes")) {
            switch (filterType) {
                case ANNOTATION:
                    Assert.isAssignable((Class<?>) Annotation.class, filterClass, "@ComponentScan ANNOTATION type filter requires an annotation type");
                    typeFilters.add(new AnnotationTypeFilter(filterClass));
                    break;
                case ASSIGNABLE_TYPE:
                    typeFilters.add(new AssignableTypeFilter(filterClass));
                    break;
                case CUSTOM:
                    Assert.isAssignable((Class<?>) TypeFilter.class, filterClass, "@ComponentScan CUSTOM type filter requires a TypeFilter implementation");
                    TypeFilter filter = (TypeFilter) ParserStrategyUtils.instantiateClass(filterClass, TypeFilter.class, environment, resourceLoader, registry);
                    typeFilters.add(filter);
                    break;
                default:
                    throw new IllegalArgumentException("Filter type not supported with Class value: " + filterType);
            }
        }
        for (String expression : filterAttributes.getStringArray("pattern")) {
            switch (filterType) {
                case ASPECTJ:
                    typeFilters.add(new AspectJTypeFilter(expression, resourceLoader.getClassLoader()));
                    break;
                case REGEX:
                    typeFilters.add(new RegexPatternTypeFilter(Pattern.compile(expression)));
                    break;
                default:
                    throw new IllegalArgumentException("Filter type not supported with String pattern: " + filterType);
            }
        }
        return typeFilters;
    }
}
