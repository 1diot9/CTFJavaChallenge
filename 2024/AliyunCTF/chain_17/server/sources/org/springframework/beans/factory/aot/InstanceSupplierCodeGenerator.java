package org.springframework.beans.factory.aot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.lang.model.element.Modifier;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import org.springframework.aot.generate.AccessControl;
import org.springframework.aot.generate.GeneratedMethod;
import org.springframework.aot.generate.GeneratedMethods;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.generate.MethodReference;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.ParameterizedTypeName;
import org.springframework.util.ClassUtils;
import org.springframework.util.function.ThrowingSupplier;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/InstanceSupplierCodeGenerator.class */
public class InstanceSupplierCodeGenerator {
    private static final String REGISTERED_BEAN_PARAMETER_NAME = "registeredBean";
    private static final String ARGS_PARAMETER_NAME = "args";
    private static final Modifier[] PRIVATE_STATIC = {Modifier.PRIVATE, Modifier.STATIC};
    private static final CodeBlock NO_ARGS = CodeBlock.of("", new Object[0]);
    private final GenerationContext generationContext;
    private final ClassName className;
    private final GeneratedMethods generatedMethods;
    private final boolean allowDirectSupplierShortcut;

    public InstanceSupplierCodeGenerator(GenerationContext generationContext, ClassName className, GeneratedMethods generatedMethods, boolean allowDirectSupplierShortcut) {
        this.generationContext = generationContext;
        this.className = className;
        this.generatedMethods = generatedMethods;
        this.allowDirectSupplierShortcut = allowDirectSupplierShortcut;
    }

    public CodeBlock generateCode(RegisteredBean registeredBean, Executable constructorOrFactoryMethod) {
        registerRuntimeHintsIfNecessary(registeredBean, constructorOrFactoryMethod);
        if (constructorOrFactoryMethod instanceof Constructor) {
            Constructor<?> constructor = (Constructor) constructorOrFactoryMethod;
            return generateCodeForConstructor(registeredBean, constructor);
        }
        if (constructorOrFactoryMethod instanceof Method) {
            Method method = (Method) constructorOrFactoryMethod;
            return generateCodeForFactoryMethod(registeredBean, method);
        }
        throw new IllegalStateException("No suitable executor found for " + registeredBean.getBeanName());
    }

    private void registerRuntimeHintsIfNecessary(RegisteredBean registeredBean, Executable constructorOrFactoryMethod) {
        ConfigurableListableBeanFactory beanFactory = registeredBean.getBeanFactory();
        if (beanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
            RuntimeHints runtimeHints = this.generationContext.getRuntimeHints();
            ProxyRuntimeHintsRegistrar registrar = new ProxyRuntimeHintsRegistrar(dlbf.getAutowireCandidateResolver());
            if (constructorOrFactoryMethod instanceof Method) {
                Method method = (Method) constructorOrFactoryMethod;
                registrar.registerRuntimeHints(runtimeHints, method);
            } else if (constructorOrFactoryMethod instanceof Constructor) {
                Constructor<?> constructor = (Constructor) constructorOrFactoryMethod;
                registrar.registerRuntimeHints(runtimeHints, constructor);
            }
        }
    }

    private CodeBlock generateCodeForConstructor(RegisteredBean registeredBean, Constructor<?> constructor) {
        String beanName = registeredBean.getBeanName();
        Class<?> beanClass = registeredBean.getBeanClass();
        Class<?> declaringClass = constructor.getDeclaringClass();
        boolean dependsOnBean = ClassUtils.isInnerClass(declaringClass);
        AccessControl.Visibility accessVisibility = getAccessVisibility(registeredBean, constructor);
        if (KotlinDetector.isKotlinReflectPresent() && KotlinDelegate.hasConstructorWithOptionalParameter(beanClass)) {
            return generateCodeForInaccessibleConstructor(beanName, beanClass, constructor, dependsOnBean, hints -> {
                hints.registerType((Class<?>) beanClass, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
            });
        }
        if (accessVisibility != AccessControl.Visibility.PRIVATE) {
            return generateCodeForAccessibleConstructor(beanName, beanClass, constructor, dependsOnBean, declaringClass);
        }
        return generateCodeForInaccessibleConstructor(beanName, beanClass, constructor, dependsOnBean, hints2 -> {
            hints2.registerConstructor(constructor, ExecutableMode.INVOKE);
        });
    }

    private CodeBlock generateCodeForAccessibleConstructor(String beanName, Class<?> beanClass, Constructor<?> constructor, boolean dependsOnBean, Class<?> declaringClass) {
        this.generationContext.getRuntimeHints().reflection().registerConstructor(constructor, ExecutableMode.INTROSPECT);
        if (!dependsOnBean && constructor.getParameterCount() == 0) {
            if (!this.allowDirectSupplierShortcut) {
                return CodeBlock.of("$T.using($T::new)", InstanceSupplier.class, declaringClass);
            }
            if (!isThrowingCheckedException(constructor)) {
                return CodeBlock.of("$T::new", declaringClass);
            }
            return CodeBlock.of("$T.of($T::new)", ThrowingSupplier.class, declaringClass);
        }
        GeneratedMethod generatedMethod = generateGetInstanceSupplierMethod(method -> {
            buildGetInstanceMethodForConstructor(method, beanName, beanClass, constructor, declaringClass, dependsOnBean, PRIVATE_STATIC);
        });
        return generateReturnStatement(generatedMethod);
    }

    private CodeBlock generateCodeForInaccessibleConstructor(String beanName, Class<?> beanClass, Constructor<?> constructor, boolean dependsOnBean, Consumer<ReflectionHints> hints) {
        CodeWarnings codeWarnings = new CodeWarnings();
        codeWarnings.detectDeprecation(beanClass, constructor).detectDeprecation(Arrays.stream(constructor.getParameters()).map((v0) -> {
            return v0.getType();
        }));
        hints.accept(this.generationContext.getRuntimeHints().reflection());
        GeneratedMethod generatedMethod = generateGetInstanceSupplierMethod(method -> {
            method.addJavadoc("Get the bean instance supplier for '$L'.", beanName);
            method.addModifiers(PRIVATE_STATIC);
            codeWarnings.suppress(method);
            method.returns(ParameterizedTypeName.get((Class<?>) BeanInstanceSupplier.class, beanClass));
            int parameterOffset = !dependsOnBean ? 0 : 1;
            method.addStatement(generateResolverForConstructor(beanClass, constructor, parameterOffset));
        });
        return generateReturnStatement(generatedMethod);
    }

    private void buildGetInstanceMethodForConstructor(MethodSpec.Builder method, String beanName, Class<?> beanClass, Constructor<?> constructor, Class<?> declaringClass, boolean dependsOnBean, Modifier... modifiers) {
        CodeBlock codeBlock;
        CodeWarnings codeWarnings = new CodeWarnings();
        codeWarnings.detectDeprecation(beanClass, constructor, declaringClass).detectDeprecation(Arrays.stream(constructor.getParameters()).map((v0) -> {
            return v0.getType();
        }));
        method.addJavadoc("Get the bean instance supplier for '$L'.", beanName);
        method.addModifiers(modifiers);
        codeWarnings.suppress(method);
        method.returns(ParameterizedTypeName.get((Class<?>) BeanInstanceSupplier.class, beanClass));
        int parameterOffset = !dependsOnBean ? 0 : 1;
        CodeBlock.Builder code = CodeBlock.builder();
        code.add(generateResolverForConstructor(beanClass, constructor, parameterOffset));
        boolean hasArguments = constructor.getParameterCount() > 0;
        if (hasArguments) {
            codeBlock = new AutowiredArgumentsCodeGenerator(declaringClass, constructor).generateCode(constructor.getParameterTypes(), parameterOffset);
        } else {
            codeBlock = NO_ARGS;
        }
        CodeBlock arguments = codeBlock;
        CodeBlock newInstance = generateNewInstanceCodeForConstructor(dependsOnBean, declaringClass, arguments);
        code.add(generateWithGeneratorCode(hasArguments, newInstance));
        method.addStatement(code.build());
    }

    private CodeBlock generateResolverForConstructor(Class<?> beanClass, Constructor<?> constructor, int parameterOffset) {
        CodeBlock parameterTypes = generateParameterTypesCode(constructor.getParameterTypes(), parameterOffset);
        return CodeBlock.of("return $T.<$T>forConstructor($L)", BeanInstanceSupplier.class, beanClass, parameterTypes);
    }

    private CodeBlock generateNewInstanceCodeForConstructor(boolean dependsOnBean, Class<?> declaringClass, CodeBlock args) {
        if (!dependsOnBean) {
            return CodeBlock.of("new $T($L)", declaringClass, args);
        }
        return CodeBlock.of("$L.getBeanFactory().getBean($T.class).new $L($L)", REGISTERED_BEAN_PARAMETER_NAME, declaringClass.getEnclosingClass(), declaringClass.getSimpleName(), args);
    }

    private CodeBlock generateCodeForFactoryMethod(RegisteredBean registeredBean, Method factoryMethod) {
        String beanName = registeredBean.getBeanName();
        Class<?> declaringClass = ClassUtils.getUserClass(factoryMethod.getDeclaringClass());
        boolean dependsOnBean = !java.lang.reflect.Modifier.isStatic(factoryMethod.getModifiers());
        AccessControl.Visibility accessVisibility = getAccessVisibility(registeredBean, factoryMethod);
        if (accessVisibility != AccessControl.Visibility.PRIVATE) {
            return generateCodeForAccessibleFactoryMethod(beanName, factoryMethod, declaringClass, dependsOnBean);
        }
        return generateCodeForInaccessibleFactoryMethod(beanName, factoryMethod, declaringClass);
    }

    private CodeBlock generateCodeForAccessibleFactoryMethod(String beanName, Method factoryMethod, Class<?> declaringClass, boolean dependsOnBean) {
        this.generationContext.getRuntimeHints().reflection().registerMethod(factoryMethod, ExecutableMode.INTROSPECT);
        if (!dependsOnBean && factoryMethod.getParameterCount() == 0) {
            Class<?> suppliedType = ClassUtils.resolvePrimitiveIfNecessary(factoryMethod.getReturnType());
            CodeBlock.Builder code = CodeBlock.builder();
            code.add("$T.<$T>forFactoryMethod($T.class, $S)", BeanInstanceSupplier.class, suppliedType, declaringClass, factoryMethod.getName());
            code.add(".withGenerator(($L) -> $T.$L())", REGISTERED_BEAN_PARAMETER_NAME, declaringClass, factoryMethod.getName());
            return code.build();
        }
        GeneratedMethod getInstanceMethod = generateGetInstanceSupplierMethod(method -> {
            buildGetInstanceMethodForFactoryMethod(method, beanName, factoryMethod, declaringClass, dependsOnBean, PRIVATE_STATIC);
        });
        return generateReturnStatement(getInstanceMethod);
    }

    private CodeBlock generateCodeForInaccessibleFactoryMethod(String beanName, Method factoryMethod, Class<?> declaringClass) {
        this.generationContext.getRuntimeHints().reflection().registerMethod(factoryMethod, ExecutableMode.INVOKE);
        GeneratedMethod getInstanceMethod = generateGetInstanceSupplierMethod(method -> {
            Class<?> suppliedType = ClassUtils.resolvePrimitiveIfNecessary(factoryMethod.getReturnType());
            method.addJavadoc("Get the bean instance supplier for '$L'.", beanName);
            method.addModifiers(PRIVATE_STATIC);
            method.returns(ParameterizedTypeName.get((Class<?>) BeanInstanceSupplier.class, suppliedType));
            method.addStatement(generateInstanceSupplierForFactoryMethod(factoryMethod, suppliedType, declaringClass, factoryMethod.getName()));
        });
        return generateReturnStatement(getInstanceMethod);
    }

    private void buildGetInstanceMethodForFactoryMethod(MethodSpec.Builder method, String beanName, Method factoryMethod, Class<?> declaringClass, boolean dependsOnBean, Modifier... modifiers) {
        CodeBlock codeBlock;
        String factoryMethodName = factoryMethod.getName();
        Class<?> suppliedType = ClassUtils.resolvePrimitiveIfNecessary(factoryMethod.getReturnType());
        CodeWarnings codeWarnings = new CodeWarnings();
        codeWarnings.detectDeprecation(declaringClass, factoryMethod, suppliedType).detectDeprecation(Arrays.stream(factoryMethod.getParameters()).map((v0) -> {
            return v0.getType();
        }));
        method.addJavadoc("Get the bean instance supplier for '$L'.", beanName);
        method.addModifiers(modifiers);
        codeWarnings.suppress(method);
        method.returns(ParameterizedTypeName.get((Class<?>) BeanInstanceSupplier.class, suppliedType));
        CodeBlock.Builder code = CodeBlock.builder();
        code.add(generateInstanceSupplierForFactoryMethod(factoryMethod, suppliedType, declaringClass, factoryMethodName));
        boolean hasArguments = factoryMethod.getParameterCount() > 0;
        if (hasArguments) {
            codeBlock = new AutowiredArgumentsCodeGenerator(declaringClass, factoryMethod).generateCode(factoryMethod.getParameterTypes());
        } else {
            codeBlock = NO_ARGS;
        }
        CodeBlock arguments = codeBlock;
        CodeBlock newInstance = generateNewInstanceCodeForMethod(dependsOnBean, declaringClass, factoryMethodName, arguments);
        code.add(generateWithGeneratorCode(hasArguments, newInstance));
        method.addStatement(code.build());
    }

    private CodeBlock generateInstanceSupplierForFactoryMethod(Method factoryMethod, Class<?> suppliedType, Class<?> declaringClass, String factoryMethodName) {
        if (factoryMethod.getParameterCount() == 0) {
            return CodeBlock.of("return $T.<$T>forFactoryMethod($T.class, $S)", BeanInstanceSupplier.class, suppliedType, declaringClass, factoryMethodName);
        }
        CodeBlock parameterTypes = generateParameterTypesCode(factoryMethod.getParameterTypes(), 0);
        return CodeBlock.of("return $T.<$T>forFactoryMethod($T.class, $S, $L)", BeanInstanceSupplier.class, suppliedType, declaringClass, factoryMethodName, parameterTypes);
    }

    private CodeBlock generateNewInstanceCodeForMethod(boolean dependsOnBean, Class<?> declaringClass, String factoryMethodName, CodeBlock args) {
        if (!dependsOnBean) {
            return CodeBlock.of("$T.$L($L)", declaringClass, factoryMethodName, args);
        }
        return CodeBlock.of("$L.getBeanFactory().getBean($T.class).$L($L)", REGISTERED_BEAN_PARAMETER_NAME, declaringClass, factoryMethodName, args);
    }

    private CodeBlock generateReturnStatement(GeneratedMethod generatedMethod) {
        return generatedMethod.toMethodReference().toInvokeCodeBlock(MethodReference.ArgumentCodeGenerator.none(), this.className);
    }

    private CodeBlock generateWithGeneratorCode(boolean hasArguments, CodeBlock newInstance) {
        CodeBlock of;
        if (hasArguments) {
            of = CodeBlock.of("($L, $L)", REGISTERED_BEAN_PARAMETER_NAME, ARGS_PARAMETER_NAME);
        } else {
            of = CodeBlock.of("($L)", REGISTERED_BEAN_PARAMETER_NAME);
        }
        CodeBlock lambdaArguments = of;
        CodeBlock.Builder code = CodeBlock.builder();
        code.add("\n", new Object[0]);
        code.indent().indent();
        code.add(".withGenerator($L -> $L)", lambdaArguments, newInstance);
        code.unindent().unindent();
        return code.build();
    }

    private AccessControl.Visibility getAccessVisibility(RegisteredBean registeredBean, Member member) {
        AccessControl beanTypeAccessControl = AccessControl.forResolvableType(registeredBean.getBeanType());
        AccessControl memberAccessControl = AccessControl.forMember(member);
        return AccessControl.lowest(beanTypeAccessControl, memberAccessControl).getVisibility();
    }

    private CodeBlock generateParameterTypesCode(Class<?>[] parameterTypes, int offset) {
        CodeBlock.Builder code = CodeBlock.builder();
        int i = offset;
        while (i < parameterTypes.length) {
            code.add(i != offset ? ", " : "", new Object[0]);
            code.add("$T.class", parameterTypes[i]);
            i++;
        }
        return code.build();
    }

    private GeneratedMethod generateGetInstanceSupplierMethod(Consumer<MethodSpec.Builder> method) {
        return this.generatedMethods.add("getInstanceSupplier", method);
    }

    private boolean isThrowingCheckedException(Executable executable) {
        Stream map = Arrays.stream(executable.getGenericExceptionTypes()).map(ResolvableType::forType).map((v0) -> {
            return v0.toClass();
        });
        Class<Exception> cls = Exception.class;
        Objects.requireNonNull(Exception.class);
        return map.anyMatch(cls::isAssignableFrom);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/InstanceSupplierCodeGenerator$KotlinDelegate.class */
    public static class KotlinDelegate {
        private KotlinDelegate() {
        }

        public static boolean hasConstructorWithOptionalParameter(Class<?> beanClass) {
            if (KotlinDetector.isKotlinType(beanClass)) {
                KClass<?> kClass = JvmClassMappingKt.getKotlinClass(beanClass);
                for (KFunction<?> constructor : kClass.getConstructors()) {
                    for (KParameter parameter : constructor.getParameters()) {
                        if (parameter.isOptional()) {
                            return true;
                        }
                    }
                }
                return false;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/InstanceSupplierCodeGenerator$ProxyRuntimeHintsRegistrar.class */
    public static class ProxyRuntimeHintsRegistrar {
        private final AutowireCandidateResolver candidateResolver;

        public ProxyRuntimeHintsRegistrar(AutowireCandidateResolver candidateResolver) {
            this.candidateResolver = candidateResolver;
        }

        public void registerRuntimeHints(RuntimeHints runtimeHints, Method method) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                MethodParameter methodParam = new MethodParameter(method, i);
                DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(methodParam, true);
                registerProxyIfNecessary(runtimeHints, dependencyDescriptor);
            }
        }

        public void registerRuntimeHints(RuntimeHints runtimeHints, Constructor<?> constructor) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                MethodParameter methodParam = new MethodParameter(constructor, i);
                DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(methodParam, true);
                registerProxyIfNecessary(runtimeHints, dependencyDescriptor);
            }
        }

        private void registerProxyIfNecessary(RuntimeHints runtimeHints, DependencyDescriptor dependencyDescriptor) {
            Class<?> proxyType = this.candidateResolver.getLazyResolutionProxyClass(dependencyDescriptor, null);
            if (proxyType != null && Proxy.isProxyClass(proxyType)) {
                runtimeHints.proxies().registerJdkProxy(proxyType.getInterfaces());
            }
        }
    }
}
