package org.springframework.context.aot;

import java.util.Objects;
import java.util.function.Supplier;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.javapoet.ClassName;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/ApplicationContextAotGenerator.class */
public class ApplicationContextAotGenerator {
    public ClassName processAheadOfTime(GenericApplicationContext applicationContext, GenerationContext generationContext) {
        return (ClassName) withCglibClassHandler(new CglibClassHandler(generationContext), () -> {
            applicationContext.refreshForAotProcessing(generationContext.getRuntimeHints());
            ApplicationContextInitializationCodeGenerator codeGenerator = new ApplicationContextInitializationCodeGenerator(applicationContext, generationContext);
            DefaultListableBeanFactory beanFactory = applicationContext.getDefaultListableBeanFactory();
            new BeanFactoryInitializationAotContributions(beanFactory).applyTo(generationContext, codeGenerator);
            return codeGenerator.getGeneratedClass().getName();
        });
    }

    private <T> T withCglibClassHandler(CglibClassHandler cglibClassHandler, Supplier<T> task) {
        try {
            Objects.requireNonNull(cglibClassHandler);
            ReflectUtils.setLoadedClassHandler(cglibClassHandler::handleLoadedClass);
            Objects.requireNonNull(cglibClassHandler);
            ReflectUtils.setGeneratedClassHandler(cglibClassHandler::handleGeneratedClass);
            T t = task.get();
            ReflectUtils.setLoadedClassHandler(null);
            ReflectUtils.setGeneratedClassHandler(null);
            return t;
        } catch (Throwable th) {
            ReflectUtils.setLoadedClassHandler(null);
            ReflectUtils.setGeneratedClassHandler(null);
            throw th;
        }
    }
}
