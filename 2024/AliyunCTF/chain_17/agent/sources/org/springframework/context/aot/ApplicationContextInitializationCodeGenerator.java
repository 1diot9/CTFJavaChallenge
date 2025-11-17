package org.springframework.context.aot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.lang.model.element.Modifier;
import org.springframework.aot.generate.GeneratedClass;
import org.springframework.aot.generate.GeneratedMethods;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.generate.MethodReference;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.ParameterizedTypeName;
import org.springframework.javapoet.TypeName;
import org.springframework.javapoet.TypeSpec;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/ApplicationContextInitializationCodeGenerator.class */
public class ApplicationContextInitializationCodeGenerator implements BeanFactoryInitializationCode {
    private static final String INITIALIZE_METHOD = "initialize";
    private static final String APPLICATION_CONTEXT_VARIABLE = "applicationContext";
    private final GenericApplicationContext applicationContext;
    private final GeneratedClass generatedClass;
    private final List<MethodReference> initializers = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ApplicationContextInitializationCodeGenerator(GenericApplicationContext applicationContext, GenerationContext generationContext) {
        this.applicationContext = applicationContext;
        this.generatedClass = generationContext.getGeneratedClasses().addForFeature("ApplicationContextInitializer", this::generateType);
        this.generatedClass.reserveMethodNames(INITIALIZE_METHOD);
    }

    private void generateType(TypeSpec.Builder type) {
        type.addJavadoc("{@link $T} to restore an application context based on previous AOT processing.", ApplicationContextInitializer.class);
        type.addModifiers(Modifier.PUBLIC);
        type.addSuperinterface(ParameterizedTypeName.get((Class<?>) ApplicationContextInitializer.class, GenericApplicationContext.class));
        type.addMethod(generateInitializeMethod());
    }

    private MethodSpec generateInitializeMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder(INITIALIZE_METHOD);
        method.addAnnotation(Override.class);
        method.addModifiers(Modifier.PUBLIC);
        method.addParameter(GenericApplicationContext.class, APPLICATION_CONTEXT_VARIABLE, new Modifier[0]);
        method.addCode(generateInitializeCode());
        return method.build();
    }

    private CodeBlock generateInitializeCode() {
        CodeBlock.Builder code = CodeBlock.builder();
        code.addStatement("$T $L = $L.getDefaultListableBeanFactory()", DefaultListableBeanFactory.class, BeanFactoryInitializationCode.BEAN_FACTORY_VARIABLE, APPLICATION_CONTEXT_VARIABLE);
        code.addStatement("$L.setAutowireCandidateResolver(new $T())", BeanFactoryInitializationCode.BEAN_FACTORY_VARIABLE, ContextAnnotationAutowireCandidateResolver.class);
        code.addStatement("$L.setDependencyComparator($T.INSTANCE)", BeanFactoryInitializationCode.BEAN_FACTORY_VARIABLE, AnnotationAwareOrderComparator.class);
        code.add(generateActiveProfilesInitializeCode());
        MethodReference.ArgumentCodeGenerator argCodeGenerator = createInitializerMethodArgumentCodeGenerator();
        for (MethodReference initializer : this.initializers) {
            code.addStatement(initializer.toInvokeCodeBlock(argCodeGenerator, this.generatedClass.getName()));
        }
        return code.build();
    }

    private CodeBlock generateActiveProfilesInitializeCode() {
        CodeBlock.Builder code = CodeBlock.builder();
        ConfigurableEnvironment environment = this.applicationContext.getEnvironment();
        if (!Arrays.equals(environment.getActiveProfiles(), environment.getDefaultProfiles())) {
            for (String activeProfile : environment.getActiveProfiles()) {
                code.addStatement("$L.getEnvironment().addActiveProfile($S)", APPLICATION_CONTEXT_VARIABLE, activeProfile);
            }
        }
        return code.build();
    }

    static MethodReference.ArgumentCodeGenerator createInitializerMethodArgumentCodeGenerator() {
        return MethodReference.ArgumentCodeGenerator.from(new InitializerMethodArgumentCodeGenerator());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GeneratedClass getGeneratedClass() {
        return this.generatedClass;
    }

    @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationCode
    public GeneratedMethods getMethods() {
        return this.generatedClass.getMethods();
    }

    @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationCode
    public void addInitializer(MethodReference methodReference) {
        this.initializers.add(methodReference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/ApplicationContextInitializationCodeGenerator$InitializerMethodArgumentCodeGenerator.class */
    public static class InitializerMethodArgumentCodeGenerator implements Function<TypeName, CodeBlock> {
        private InitializerMethodArgumentCodeGenerator() {
        }

        @Override // java.util.function.Function
        @Nullable
        public CodeBlock apply(TypeName typeName) {
            if (!(typeName instanceof ClassName)) {
                return null;
            }
            ClassName className = (ClassName) typeName;
            return apply(className);
        }

        @Nullable
        private CodeBlock apply(ClassName className) {
            String name = className.canonicalName();
            if (name.equals(DefaultListableBeanFactory.class.getName()) || name.equals(ConfigurableListableBeanFactory.class.getName())) {
                return CodeBlock.of(BeanFactoryInitializationCode.BEAN_FACTORY_VARIABLE, new Object[0]);
            }
            if (name.equals(ConfigurableEnvironment.class.getName()) || name.equals(Environment.class.getName())) {
                return CodeBlock.of("$L.getEnvironment()", ApplicationContextInitializationCodeGenerator.APPLICATION_CONTEXT_VARIABLE);
            }
            if (name.equals(ResourceLoader.class.getName())) {
                return CodeBlock.of(ApplicationContextInitializationCodeGenerator.APPLICATION_CONTEXT_VARIABLE, new Object[0]);
            }
            return null;
        }
    }
}
