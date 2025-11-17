package org.springframework.beans.factory.aot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.springframework.aot.generate.AccessControl;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.generate.MethodReference;
import org.springframework.aot.generate.ValueCodeGenerator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.aot.AotServices;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;
import org.springframework.javapoet.ParameterizedTypeName;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.function.SingletonSupplier;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/DefaultBeanRegistrationCodeFragments.class */
class DefaultBeanRegistrationCodeFragments implements BeanRegistrationCodeFragments {
    private static final ValueCodeGenerator valueCodeGenerator = ValueCodeGenerator.withDefaults();
    private final BeanRegistrationsCode beanRegistrationsCode;
    private final RegisteredBean registeredBean;
    private final BeanDefinitionMethodGeneratorFactory beanDefinitionMethodGeneratorFactory;
    private final Supplier<Executable> constructorOrFactoryMethod;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultBeanRegistrationCodeFragments(BeanRegistrationsCode beanRegistrationsCode, RegisteredBean registeredBean, BeanDefinitionMethodGeneratorFactory beanDefinitionMethodGeneratorFactory) {
        this.beanRegistrationsCode = beanRegistrationsCode;
        this.registeredBean = registeredBean;
        this.beanDefinitionMethodGeneratorFactory = beanDefinitionMethodGeneratorFactory;
        Objects.requireNonNull(registeredBean);
        this.constructorOrFactoryMethod = SingletonSupplier.of(registeredBean::resolveConstructorOrFactoryMethod);
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
    public ClassName getTarget(RegisteredBean registeredBean) {
        Class<?> target;
        if (hasInstanceSupplier()) {
            throw new IllegalStateException("Default code generation is not supported for bean definitions declaring an instance supplier callback: " + registeredBean.getMergedBeanDefinition());
        }
        Class<?> extractDeclaringClass = extractDeclaringClass(registeredBean.getBeanType(), this.constructorOrFactoryMethod.get());
        while (true) {
            target = extractDeclaringClass;
            if (!target.getName().startsWith("java.") || !registeredBean.isInnerBean()) {
                break;
            }
            RegisteredBean parent = registeredBean.getParent();
            Assert.state(parent != null, "No parent available for inner bean");
            extractDeclaringClass = parent.getBeanClass();
        }
        return target.isArray() ? ClassName.get(target.getComponentType()) : ClassName.get(target);
    }

    private Class<?> extractDeclaringClass(ResolvableType beanType, Executable executable) {
        Class<?> declaringClass = ClassUtils.getUserClass(executable.getDeclaringClass());
        if ((executable instanceof Constructor) && AccessControl.forMember(executable).isPublic() && FactoryBean.class.isAssignableFrom(declaringClass)) {
            return extractTargetClassFromFactoryBean(declaringClass, beanType);
        }
        return executable.getDeclaringClass();
    }

    private Class<?> extractTargetClassFromFactoryBean(Class<?> factoryBeanType, ResolvableType beanType) {
        ResolvableType target = ResolvableType.forType(factoryBeanType).as(FactoryBean.class).getGeneric(0);
        if (target.getType().equals(Class.class)) {
            return target.toClass();
        }
        return factoryBeanType.isAssignableFrom(beanType.toClass()) ? beanType.as(FactoryBean.class).getGeneric(0).toClass() : beanType.toClass();
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
    public CodeBlock generateNewBeanDefinitionCode(GenerationContext generationContext, ResolvableType beanType, BeanRegistrationCode beanRegistrationCode) {
        CodeBlock.Builder code = CodeBlock.builder();
        RootBeanDefinition mergedBeanDefinition = this.registeredBean.getMergedBeanDefinition();
        Class<?> beanClass = mergedBeanDefinition.hasBeanClass() ? ClassUtils.getUserClass(mergedBeanDefinition.getBeanClass()) : null;
        CodeBlock beanClassCode = generateBeanClassCode(beanRegistrationCode.getClassName().packageName(), beanClass != null ? beanClass : beanType.toClass());
        code.addStatement("$T $L = new $T($L)", RootBeanDefinition.class, BeanRegistrationCodeFragments.BEAN_DEFINITION_VARIABLE, RootBeanDefinition.class, beanClassCode);
        if (targetTypeNecessary(beanType, beanClass)) {
            code.addStatement("$L.setTargetType($L)", BeanRegistrationCodeFragments.BEAN_DEFINITION_VARIABLE, generateBeanTypeCode(beanType));
        }
        return code.build();
    }

    private CodeBlock generateBeanClassCode(String targetPackage, Class<?> beanClass) {
        if (Modifier.isPublic(beanClass.getModifiers()) || targetPackage.equals(beanClass.getPackageName())) {
            return CodeBlock.of("$T.class", beanClass);
        }
        return CodeBlock.of("$S", beanClass.getName());
    }

    private CodeBlock generateBeanTypeCode(ResolvableType beanType) {
        if (!beanType.hasGenerics()) {
            return valueCodeGenerator.generateCode(ClassUtils.getUserClass(beanType.toClass()));
        }
        return valueCodeGenerator.generateCode(beanType);
    }

    private boolean targetTypeNecessary(ResolvableType beanType, @Nullable Class<?> beanClass) {
        if (beanType.hasGenerics()) {
            return true;
        }
        if (beanClass == null || this.registeredBean.getMergedBeanDefinition().getFactoryMethodName() == null) {
            return (beanClass == null || beanType.toClass().equals(beanClass)) ? false : true;
        }
        return true;
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
    public CodeBlock generateSetBeanDefinitionPropertiesCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, RootBeanDefinition beanDefinition, Predicate<String> attributeFilter) {
        AotServices.Loader loader = AotServices.factories(this.registeredBean.getBeanFactory().getBeanClassLoader());
        List<ValueCodeGenerator.Delegate> additionalDelegates = loader.load(ValueCodeGenerator.Delegate.class).asList();
        return new BeanDefinitionPropertiesCodeGenerator(generationContext.getRuntimeHints(), attributeFilter, beanRegistrationCode.getMethods(), additionalDelegates, (name, value) -> {
            return generateValueCode(generationContext, name, value);
        }).generateCode(beanDefinition);
    }

    @Nullable
    protected CodeBlock generateValueCode(GenerationContext generationContext, String name, Object value) {
        RegisteredBean innerRegisteredBean = getInnerRegisteredBean(value);
        if (innerRegisteredBean != null) {
            BeanDefinitionMethodGenerator methodGenerator = this.beanDefinitionMethodGeneratorFactory.getBeanDefinitionMethodGenerator(innerRegisteredBean, name);
            Assert.state(methodGenerator != null, "Unexpected filtering of inner-bean");
            MethodReference generatedMethod = methodGenerator.generateBeanDefinitionMethod(generationContext, this.beanRegistrationsCode);
            return generatedMethod.toInvokeCodeBlock(MethodReference.ArgumentCodeGenerator.none());
        }
        return null;
    }

    @Nullable
    private RegisteredBean getInnerRegisteredBean(Object value) {
        if (value instanceof BeanDefinitionHolder) {
            BeanDefinitionHolder beanDefinitionHolder = (BeanDefinitionHolder) value;
            return RegisteredBean.ofInnerBean(this.registeredBean, beanDefinitionHolder);
        }
        if (value instanceof BeanDefinition) {
            BeanDefinition beanDefinition = (BeanDefinition) value;
            return RegisteredBean.ofInnerBean(this.registeredBean, beanDefinition);
        }
        return null;
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
    public CodeBlock generateSetBeanInstanceSupplierCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, CodeBlock instanceSupplierCode, List<MethodReference> postProcessors) {
        CodeBlock.Builder code = CodeBlock.builder();
        if (postProcessors.isEmpty()) {
            code.addStatement("$L.setInstanceSupplier($L)", BeanRegistrationCodeFragments.BEAN_DEFINITION_VARIABLE, instanceSupplierCode);
            return code.build();
        }
        code.addStatement("$T $L = $L", ParameterizedTypeName.get((Class<?>) InstanceSupplier.class, this.registeredBean.getBeanClass()), BeanRegistrationCodeFragments.INSTANCE_SUPPLIER_VARIABLE, instanceSupplierCode);
        for (MethodReference postProcessor : postProcessors) {
            code.addStatement("$L = $L.andThen($L)", BeanRegistrationCodeFragments.INSTANCE_SUPPLIER_VARIABLE, BeanRegistrationCodeFragments.INSTANCE_SUPPLIER_VARIABLE, postProcessor.toCodeBlock());
        }
        code.addStatement("$L.setInstanceSupplier($L)", BeanRegistrationCodeFragments.BEAN_DEFINITION_VARIABLE, BeanRegistrationCodeFragments.INSTANCE_SUPPLIER_VARIABLE);
        return code.build();
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
    public CodeBlock generateInstanceSupplierCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, boolean allowDirectSupplierShortcut) {
        if (hasInstanceSupplier()) {
            throw new IllegalStateException("Default code generation is not supported for bean definitions declaring an instance supplier callback: " + this.registeredBean.getMergedBeanDefinition());
        }
        return new InstanceSupplierCodeGenerator(generationContext, beanRegistrationCode.getClassName(), beanRegistrationCode.getMethods(), allowDirectSupplierShortcut).generateCode(this.registeredBean, this.constructorOrFactoryMethod.get());
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
    public CodeBlock generateReturnCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode) {
        CodeBlock.Builder code = CodeBlock.builder();
        code.addStatement("return $L", BeanRegistrationCodeFragments.BEAN_DEFINITION_VARIABLE);
        return code.build();
    }

    private boolean hasInstanceSupplier() {
        return this.registeredBean.getMergedBeanDefinition().getInstanceSupplier() != null;
    }
}
