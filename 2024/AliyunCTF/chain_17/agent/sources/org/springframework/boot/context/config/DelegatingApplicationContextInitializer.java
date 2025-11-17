package org.springframework.boot.context.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@Deprecated(since = "3.2.0", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/DelegatingApplicationContextInitializer.class */
public class DelegatingApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    private static final String PROPERTY_NAME = "context.initializer.classes";
    private int order = 0;

    @Override // org.springframework.context.ApplicationContextInitializer
    public void initialize(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        List<Class<?>> initializerClasses = getInitializerClasses(environment);
        if (!initializerClasses.isEmpty()) {
            applyInitializerClasses(context, initializerClasses);
        }
    }

    private List<Class<?>> getInitializerClasses(ConfigurableEnvironment env) {
        String classNames = env.getProperty(PROPERTY_NAME);
        List<Class<?>> classes = new ArrayList<>();
        if (StringUtils.hasLength(classNames)) {
            for (String className : StringUtils.tokenizeToStringArray(classNames, ",")) {
                classes.add(getInitializerClass(className));
            }
        }
        return classes;
    }

    private Class<?> getInitializerClass(String className) throws LinkageError {
        try {
            Class<?> initializerClass = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
            Assert.isAssignable(ApplicationContextInitializer.class, initializerClass);
            return initializerClass;
        } catch (ClassNotFoundException ex) {
            throw new ApplicationContextException("Failed to load context initializer class [" + className + "]", ex);
        }
    }

    private void applyInitializerClasses(ConfigurableApplicationContext context, List<Class<?>> initializerClasses) {
        Class<?> contextClass = context.getClass();
        List<ApplicationContextInitializer<?>> initializers = new ArrayList<>();
        for (Class<?> initializerClass : initializerClasses) {
            initializers.add(instantiateInitializer(contextClass, initializerClass));
        }
        applyInitializers(context, initializers);
    }

    private ApplicationContextInitializer<?> instantiateInitializer(Class<?> contextClass, Class<?> initializerClass) {
        Class<?> requireContextClass = GenericTypeResolver.resolveTypeArgument(initializerClass, ApplicationContextInitializer.class);
        Assert.isAssignable(requireContextClass, contextClass, (Supplier<String>) () -> {
            return String.format("Could not add context initializer [%s] as its generic parameter [%s] is not assignable from the type of application context used by this context loader [%s]: ", initializerClass.getName(), requireContextClass.getName(), contextClass.getName());
        });
        return (ApplicationContextInitializer) BeanUtils.instantiateClass(initializerClass);
    }

    private void applyInitializers(ConfigurableApplicationContext context, List<ApplicationContextInitializer<?>> initializers) {
        initializers.sort(new AnnotationAwareOrderComparator());
        for (ApplicationContextInitializer initializer : initializers) {
            initializer.initialize(context);
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }
}
