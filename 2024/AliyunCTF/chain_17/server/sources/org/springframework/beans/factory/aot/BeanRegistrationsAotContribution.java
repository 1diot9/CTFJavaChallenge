package org.springframework.beans.factory.aot;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import javax.lang.model.element.Modifier;
import org.springframework.aot.generate.GeneratedClass;
import org.springframework.aot.generate.GeneratedMethod;
import org.springframework.aot.generate.GeneratedMethods;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.generate.MethodReference;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;
import org.springframework.javapoet.MethodSpec;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanRegistrationsAotContribution.class */
class BeanRegistrationsAotContribution implements BeanFactoryInitializationAotContribution {
    private static final String BEAN_FACTORY_PARAMETER_NAME = "beanFactory";
    private final Map<BeanRegistrationKey, Registration> registrations;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanRegistrationsAotContribution(Map<BeanRegistrationKey, Registration> registrations) {
        this.registrations = registrations;
    }

    @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
    public void applyTo(GenerationContext generationContext, BeanFactoryInitializationCode beanFactoryInitializationCode) {
        GeneratedClass generatedClass = generationContext.getGeneratedClasses().addForFeature("BeanFactoryRegistrations", type -> {
            type.addJavadoc("Register bean definitions for the bean factory.", new Object[0]);
            type.addModifiers(Modifier.PUBLIC);
        });
        BeanRegistrationsCodeGenerator codeGenerator = new BeanRegistrationsCodeGenerator(generatedClass);
        GeneratedMethod generatedBeanDefinitionsMethod = codeGenerator.getMethods().add("registerBeanDefinitions", method -> {
            generateRegisterBeanDefinitionsMethod(method, generationContext, codeGenerator);
        });
        beanFactoryInitializationCode.addInitializer(generatedBeanDefinitionsMethod.toMethodReference());
        GeneratedMethod generatedAliasesMethod = codeGenerator.getMethods().add("registerAliases", this::generateRegisterAliasesMethod);
        beanFactoryInitializationCode.addInitializer(generatedAliasesMethod.toMethodReference());
        generateRegisterHints(generationContext.getRuntimeHints(), this.registrations);
    }

    private void generateRegisterBeanDefinitionsMethod(MethodSpec.Builder method, GenerationContext generationContext, BeanRegistrationsCode beanRegistrationsCode) {
        method.addJavadoc("Register the bean definitions.", new Object[0]);
        method.addModifiers(Modifier.PUBLIC);
        method.addParameter(DefaultListableBeanFactory.class, "beanFactory", new Modifier[0]);
        CodeBlock.Builder code = CodeBlock.builder();
        this.registrations.forEach((registeredBean, registration) -> {
            MethodReference beanDefinitionMethod = registration.methodGenerator.generateBeanDefinitionMethod(generationContext, beanRegistrationsCode);
            CodeBlock methodInvocation = beanDefinitionMethod.toInvokeCodeBlock(MethodReference.ArgumentCodeGenerator.none(), beanRegistrationsCode.getClassName());
            code.addStatement("$L.registerBeanDefinition($S, $L)", "beanFactory", registeredBean.beanName(), methodInvocation);
        });
        method.addCode(code.build());
    }

    private void generateRegisterAliasesMethod(MethodSpec.Builder method) {
        method.addJavadoc("Register the aliases.", new Object[0]);
        method.addModifiers(Modifier.PUBLIC);
        method.addParameter(DefaultListableBeanFactory.class, "beanFactory", new Modifier[0]);
        CodeBlock.Builder code = CodeBlock.builder();
        this.registrations.forEach((registeredBean, registration) -> {
            for (String alias : registration.aliases) {
                code.addStatement("$L.registerAlias($S, $S)", "beanFactory", registeredBean.beanName(), alias);
            }
        });
        method.addCode(code.build());
    }

    private void generateRegisterHints(RuntimeHints runtimeHints, Map<BeanRegistrationKey, Registration> registrations) {
        registrations.keySet().forEach(beanRegistrationKey -> {
            ReflectionHints hints = runtimeHints.reflection();
            Class<?> beanClass = beanRegistrationKey.beanClass();
            hints.registerType(beanClass, MemberCategory.INTROSPECT_PUBLIC_METHODS, MemberCategory.INTROSPECT_DECLARED_METHODS);
            introspectPublicMethodsOnAllInterfaces(hints, beanClass);
        });
    }

    private void introspectPublicMethodsOnAllInterfaces(ReflectionHints hints, Class<?> type) {
        Class<?> cls = type;
        while (true) {
            Class<?> currentClass = cls;
            if (currentClass != null && currentClass != Object.class) {
                for (Class<?> interfaceType : currentClass.getInterfaces()) {
                    if (!ClassUtils.isJavaLanguageInterface(interfaceType)) {
                        hints.registerType(interfaceType, MemberCategory.INTROSPECT_PUBLIC_METHODS);
                        introspectPublicMethodsOnAllInterfaces(hints, interfaceType);
                    }
                }
                cls = currentClass.getSuperclass();
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanRegistrationsAotContribution$Registration.class */
    public static final class Registration extends Record {
        private final BeanDefinitionMethodGenerator methodGenerator;
        private final String[] aliases;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Registration(BeanDefinitionMethodGenerator methodGenerator, String[] aliases) {
            this.methodGenerator = methodGenerator;
            this.aliases = aliases;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Registration.class), Registration.class, "methodGenerator;aliases", "FIELD:Lorg/springframework/beans/factory/aot/BeanRegistrationsAotContribution$Registration;->methodGenerator:Lorg/springframework/beans/factory/aot/BeanDefinitionMethodGenerator;", "FIELD:Lorg/springframework/beans/factory/aot/BeanRegistrationsAotContribution$Registration;->aliases:[Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Registration.class), Registration.class, "methodGenerator;aliases", "FIELD:Lorg/springframework/beans/factory/aot/BeanRegistrationsAotContribution$Registration;->methodGenerator:Lorg/springframework/beans/factory/aot/BeanDefinitionMethodGenerator;", "FIELD:Lorg/springframework/beans/factory/aot/BeanRegistrationsAotContribution$Registration;->aliases:[Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Registration.class, Object.class), Registration.class, "methodGenerator;aliases", "FIELD:Lorg/springframework/beans/factory/aot/BeanRegistrationsAotContribution$Registration;->methodGenerator:Lorg/springframework/beans/factory/aot/BeanDefinitionMethodGenerator;", "FIELD:Lorg/springframework/beans/factory/aot/BeanRegistrationsAotContribution$Registration;->aliases:[Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public BeanDefinitionMethodGenerator methodGenerator() {
            return this.methodGenerator;
        }

        public String[] aliases() {
            return this.aliases;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanRegistrationsAotContribution$BeanRegistrationsCodeGenerator.class */
    static class BeanRegistrationsCodeGenerator implements BeanRegistrationsCode {
        private final GeneratedClass generatedClass;

        public BeanRegistrationsCodeGenerator(GeneratedClass generatedClass) {
            this.generatedClass = generatedClass;
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationsCode
        public ClassName getClassName() {
            return this.generatedClass.getName();
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationsCode
        public GeneratedMethods getMethods() {
            return this.generatedClass.getMethods();
        }
    }
}
