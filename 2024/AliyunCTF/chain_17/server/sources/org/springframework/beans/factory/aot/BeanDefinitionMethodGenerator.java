package org.springframework.beans.factory.aot;

import java.util.List;
import javax.lang.model.element.Modifier;
import org.springframework.aot.generate.GeneratedClass;
import org.springframework.aot.generate.GeneratedMethod;
import org.springframework.aot.generate.GeneratedMethods;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.generate.MethodReference;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.javapoet.ClassName;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanDefinitionMethodGenerator.class */
public class BeanDefinitionMethodGenerator {
    private final BeanDefinitionMethodGeneratorFactory methodGeneratorFactory;
    private final RegisteredBean registeredBean;

    @Nullable
    private final String currentPropertyName;
    private final List<BeanRegistrationAotContribution> aotContributions;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanDefinitionMethodGenerator(BeanDefinitionMethodGeneratorFactory methodGeneratorFactory, RegisteredBean registeredBean, @Nullable String currentPropertyName, List<BeanRegistrationAotContribution> aotContributions) {
        this.methodGeneratorFactory = methodGeneratorFactory;
        this.registeredBean = registeredBean;
        this.currentPropertyName = currentPropertyName;
        this.aotContributions = aotContributions;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MethodReference generateBeanDefinitionMethod(GenerationContext generationContext, BeanRegistrationsCode beanRegistrationsCode) {
        BeanRegistrationCodeFragments codeFragments = getCodeFragments(generationContext, beanRegistrationsCode);
        ClassName target = codeFragments.getTarget(this.registeredBean);
        if (isWritablePackageName(target)) {
            GeneratedClass generatedClass = lookupGeneratedClass(generationContext, target);
            GeneratedMethods generatedMethods = generatedClass.getMethods().withPrefix(getName());
            GeneratedMethod generatedMethod = generateBeanDefinitionMethod(generationContext, generatedClass.getName(), generatedMethods, codeFragments, Modifier.PUBLIC);
            return generatedMethod.toMethodReference();
        }
        GeneratedMethods generatedMethods2 = beanRegistrationsCode.getMethods().withPrefix(getName());
        GeneratedMethod generatedMethod2 = generateBeanDefinitionMethod(generationContext, beanRegistrationsCode.getClassName(), generatedMethods2, codeFragments, Modifier.PRIVATE);
        return generatedMethod2.toMethodReference();
    }

    private boolean isWritablePackageName(ClassName target) {
        String packageName = target.packageName();
        return (packageName.startsWith("java.") || packageName.startsWith("javax.")) ? false : true;
    }

    private static GeneratedClass lookupGeneratedClass(GenerationContext generationContext, ClassName target) {
        ClassName topLevelClassName = target.topLevelClassName();
        GeneratedClass generatedClass = generationContext.getGeneratedClasses().getOrAddForFeatureComponent("BeanDefinitions", topLevelClassName, type -> {
            type.addJavadoc("Bean definitions for {@link $T}.", topLevelClassName);
            type.addModifiers(Modifier.PUBLIC);
        });
        List<String> names = target.simpleNames();
        if (names.size() == 1) {
            return generatedClass;
        }
        List<String> namesToProcess = names.subList(1, names.size());
        ClassName currentTargetClassName = topLevelClassName;
        GeneratedClass tmp = generatedClass;
        for (String nameToProcess : namesToProcess) {
            currentTargetClassName = currentTargetClassName.nestedClass(nameToProcess);
            tmp = createInnerClass(tmp, nameToProcess, currentTargetClassName);
        }
        return tmp;
    }

    private static GeneratedClass createInnerClass(GeneratedClass generatedClass, String name, ClassName target) {
        return generatedClass.getOrAdd(name, type -> {
            type.addJavadoc("Bean definitions for {@link $T}.", target);
            type.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        });
    }

    private BeanRegistrationCodeFragments getCodeFragments(GenerationContext generationContext, BeanRegistrationsCode beanRegistrationsCode) {
        BeanRegistrationCodeFragments codeFragments = new DefaultBeanRegistrationCodeFragments(beanRegistrationsCode, this.registeredBean, this.methodGeneratorFactory);
        for (BeanRegistrationAotContribution aotContribution : this.aotContributions) {
            codeFragments = aotContribution.customizeBeanRegistrationCodeFragments(generationContext, codeFragments);
        }
        return codeFragments;
    }

    private GeneratedMethod generateBeanDefinitionMethod(GenerationContext generationContext, ClassName className, GeneratedMethods generatedMethods, BeanRegistrationCodeFragments codeFragments, Modifier modifier) {
        BeanRegistrationCodeGenerator codeGenerator = new BeanRegistrationCodeGenerator(className, generatedMethods, this.registeredBean, codeFragments);
        this.aotContributions.forEach(aotContribution -> {
            aotContribution.applyTo(generationContext, codeGenerator);
        });
        CodeWarnings codeWarnings = new CodeWarnings();
        codeWarnings.detectDeprecation(this.registeredBean.getBeanClass());
        return generatedMethods.add("getBeanDefinition", method -> {
            Object[] objArr = new Object[2];
            objArr[0] = this.registeredBean.isInnerBean() ? "inner-bean" : "bean";
            objArr[1] = getName();
            method.addJavadoc("Get the $L definition for '$L'.", objArr);
            method.addModifiers(modifier, Modifier.STATIC);
            codeWarnings.suppress(method);
            method.returns(BeanDefinition.class);
            method.addCode(codeGenerator.generateCode(generationContext));
        });
    }

    private String getName() {
        RegisteredBean nonGeneratedParent;
        if (this.currentPropertyName != null) {
            return this.currentPropertyName;
        }
        if (!this.registeredBean.isGeneratedBeanName()) {
            return getSimpleBeanName(this.registeredBean.getBeanName());
        }
        RegisteredBean registeredBean = this.registeredBean;
        while (true) {
            nonGeneratedParent = registeredBean;
            if (nonGeneratedParent == null || !nonGeneratedParent.isGeneratedBeanName()) {
                break;
            }
            registeredBean = nonGeneratedParent.getParent();
        }
        if (nonGeneratedParent != null) {
            return getSimpleBeanName(nonGeneratedParent.getBeanName()) + "InnerBean";
        }
        return "innerBean";
    }

    private String getSimpleBeanName(String beanName) {
        int lastDot = beanName.lastIndexOf(46);
        String beanName2 = lastDot != -1 ? beanName.substring(lastDot + 1) : beanName;
        int lastDollar = beanName2.lastIndexOf(36);
        return StringUtils.uncapitalize(lastDollar != -1 ? beanName2.substring(lastDollar + 1) : beanName2);
    }
}
