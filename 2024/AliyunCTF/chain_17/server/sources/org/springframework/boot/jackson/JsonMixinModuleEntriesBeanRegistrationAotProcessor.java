package org.springframework.boot.jackson;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.lang.model.element.Modifier;
import org.springframework.aot.generate.AccessControl;
import org.springframework.aot.generate.GeneratedMethod;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.aot.BeanRegistrationCodeFragments;
import org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jackson/JsonMixinModuleEntriesBeanRegistrationAotProcessor.class */
class JsonMixinModuleEntriesBeanRegistrationAotProcessor implements BeanRegistrationAotProcessor {
    JsonMixinModuleEntriesBeanRegistrationAotProcessor() {
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationAotProcessor
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        if (registeredBean.getBeanClass().equals(JsonMixinModuleEntries.class)) {
            return BeanRegistrationAotContribution.withCustomCodeFragments(codeFragments -> {
                return new AotContribution(codeFragments, registeredBean);
            });
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jackson/JsonMixinModuleEntriesBeanRegistrationAotProcessor$AotContribution.class */
    public static class AotContribution extends BeanRegistrationCodeFragmentsDecorator {
        private static final Class<?> BEAN_TYPE = JsonMixinModuleEntries.class;
        private final RegisteredBean registeredBean;
        private final ClassLoader classLoader;

        AotContribution(BeanRegistrationCodeFragments delegate, RegisteredBean registeredBean) {
            super(delegate);
            this.registeredBean = registeredBean;
            this.classLoader = registeredBean.getBeanFactory().getBeanClassLoader();
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator, org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
        public ClassName getTarget(RegisteredBean registeredBean) {
            return ClassName.get(BEAN_TYPE);
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationCodeFragmentsDecorator, org.springframework.beans.factory.aot.BeanRegistrationCodeFragments
        public CodeBlock generateInstanceSupplierCode(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode, boolean allowDirectSupplierShortcut) {
            JsonMixinModuleEntries entries = (JsonMixinModuleEntries) this.registeredBean.getBeanFactory().getBean(this.registeredBean.getBeanName(), JsonMixinModuleEntries.class);
            contributeHints(generationContext.getRuntimeHints(), entries);
            GeneratedMethod generatedMethod = beanRegistrationCode.getMethods().add("getInstance", method -> {
                method.addJavadoc("Get the bean instance for '$L'.", this.registeredBean.getBeanName());
                method.addModifiers(Modifier.PRIVATE, Modifier.STATIC);
                method.returns(BEAN_TYPE);
                CodeBlock.Builder code = CodeBlock.builder();
                code.add("return $T.create(", JsonMixinModuleEntries.class).beginControlFlow("(mixins) ->", new Object[0]);
                entries.doWithEntry(this.classLoader, (type, mixin) -> {
                    addEntryCode(code, type, mixin);
                });
                code.endControlFlow(")", new Object[0]);
                method.addCode(code.build());
            });
            return generatedMethod.toMethodReference().toCodeBlock();
        }

        private void addEntryCode(CodeBlock.Builder code, Class<?> type, Class<?> mixin) {
            AccessControl accessForTypes = AccessControl.lowest(AccessControl.forClass(type), AccessControl.forClass(mixin));
            if (accessForTypes.isPublic()) {
                code.addStatement("$L.and($T.class, $T.class)", "mixins", type, mixin);
            } else {
                code.addStatement("$L.and($S, $S)", "mixins", type.getName(), mixin.getName());
            }
        }

        private void contributeHints(RuntimeHints runtimeHints, JsonMixinModuleEntries entries) {
            Set<Class<?>> mixins = new LinkedHashSet<>();
            entries.doWithEntry(this.classLoader, (type, mixin) -> {
                mixins.add(mixin);
            });
            new BindingReflectionHintsRegistrar().registerReflectionHints(runtimeHints.reflection(), (Type[]) mixins.toArray(x$0 -> {
                return new Class[x$0];
            }));
        }
    }
}
