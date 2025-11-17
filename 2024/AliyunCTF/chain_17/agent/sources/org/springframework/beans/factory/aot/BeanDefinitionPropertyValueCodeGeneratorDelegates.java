package org.springframework.beans.factory.aot;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.aot.generate.GeneratedMethod;
import org.springframework.aot.generate.GeneratedMethods;
import org.springframework.aot.generate.ValueCodeGenerator;
import org.springframework.aot.generate.ValueCodeGeneratorDelegates;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.javapoet.AnnotationSpec;
import org.springframework.javapoet.CodeBlock;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanDefinitionPropertyValueCodeGeneratorDelegates.class */
abstract class BeanDefinitionPropertyValueCodeGeneratorDelegates {
    public static final List<ValueCodeGenerator.Delegate> INSTANCES = List.of(new ManagedListDelegate(), new ManagedSetDelegate(), new ManagedMapDelegate(), new LinkedHashMapDelegate(), new BeanReferenceDelegate(), new TypedStringValueDelegate());

    BeanDefinitionPropertyValueCodeGeneratorDelegates() {
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanDefinitionPropertyValueCodeGeneratorDelegates$ManagedListDelegate.class */
    private static class ManagedListDelegate extends ValueCodeGeneratorDelegates.CollectionDelegate<ManagedList<?>> {
        public ManagedListDelegate() {
            super(ManagedList.class, CodeBlock.of("new $T()", ManagedList.class));
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanDefinitionPropertyValueCodeGeneratorDelegates$ManagedSetDelegate.class */
    private static class ManagedSetDelegate extends ValueCodeGeneratorDelegates.CollectionDelegate<ManagedSet<?>> {
        public ManagedSetDelegate() {
            super(ManagedSet.class, CodeBlock.of("new $T()", ManagedSet.class));
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanDefinitionPropertyValueCodeGeneratorDelegates$ManagedMapDelegate.class */
    private static class ManagedMapDelegate implements ValueCodeGenerator.Delegate {
        private static final CodeBlock EMPTY_RESULT = CodeBlock.of("$T.ofEntries()", ManagedMap.class);

        private ManagedMapDelegate() {
        }

        @Override // org.springframework.aot.generate.ValueCodeGenerator.Delegate
        public CodeBlock generateCode(ValueCodeGenerator valueCodeGenerator, Object value) {
            if (value instanceof ManagedMap) {
                ManagedMap<?, ?> managedMap = (ManagedMap) value;
                return generateManagedMapCode(valueCodeGenerator, managedMap);
            }
            return null;
        }

        private <K, V> CodeBlock generateManagedMapCode(ValueCodeGenerator valueCodeGenerator, ManagedMap<K, V> managedMap) {
            if (managedMap.isEmpty()) {
                return EMPTY_RESULT;
            }
            CodeBlock.Builder code = CodeBlock.builder();
            code.add("$T.ofEntries(", ManagedMap.class);
            Iterator<Map.Entry<K, V>> iterator = managedMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<K, V> next = iterator.next();
                code.add("$T.entry($L,$L)", Map.class, valueCodeGenerator.generateCode(next.getKey()), valueCodeGenerator.generateCode(next.getValue()));
                if (iterator.hasNext()) {
                    code.add(", ", new Object[0]);
                }
            }
            code.add(")", new Object[0]);
            return code.build();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanDefinitionPropertyValueCodeGeneratorDelegates$LinkedHashMapDelegate.class */
    private static class LinkedHashMapDelegate extends ValueCodeGeneratorDelegates.MapDelegate {
        private LinkedHashMapDelegate() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.aot.generate.ValueCodeGeneratorDelegates.MapDelegate
        public CodeBlock generateMapCode(ValueCodeGenerator valueCodeGenerator, Map<?, ?> map) {
            GeneratedMethods generatedMethods = valueCodeGenerator.getGeneratedMethods();
            if ((map instanceof LinkedHashMap) && generatedMethods != null) {
                return generateLinkedHashMapCode(valueCodeGenerator, generatedMethods, map);
            }
            return super.generateMapCode(valueCodeGenerator, map);
        }

        private CodeBlock generateLinkedHashMapCode(ValueCodeGenerator valueCodeGenerator, GeneratedMethods generatedMethods, Map<?, ?> map) {
            GeneratedMethod generatedMethod = generatedMethods.add("getMap", method -> {
                method.addAnnotation(AnnotationSpec.builder((Class<?>) SuppressWarnings.class).addMember("value", "{\"rawtypes\", \"unchecked\"}", new Object[0]).build());
                method.returns(Map.class);
                method.addStatement("$T map = new $T($L)", Map.class, LinkedHashMap.class, Integer.valueOf(map.size()));
                map.forEach((key, value) -> {
                    method.addStatement("map.put($L, $L)", valueCodeGenerator.generateCode(key), valueCodeGenerator.generateCode(value));
                });
                method.addStatement("return map", new Object[0]);
            });
            return CodeBlock.of("$L()", generatedMethod.getName());
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanDefinitionPropertyValueCodeGeneratorDelegates$BeanReferenceDelegate.class */
    private static class BeanReferenceDelegate implements ValueCodeGenerator.Delegate {
        private BeanReferenceDelegate() {
        }

        @Override // org.springframework.aot.generate.ValueCodeGenerator.Delegate
        public CodeBlock generateCode(ValueCodeGenerator valueCodeGenerator, Object value) {
            if (value instanceof RuntimeBeanReference) {
                RuntimeBeanReference runtimeBeanReference = (RuntimeBeanReference) value;
                if (runtimeBeanReference.getBeanType() != null) {
                    return CodeBlock.of("new $T($T.class)", RuntimeBeanReference.class, runtimeBeanReference.getBeanType());
                }
            }
            if (value instanceof BeanReference) {
                BeanReference beanReference = (BeanReference) value;
                return CodeBlock.of("new $T($S)", RuntimeBeanReference.class, beanReference.getBeanName());
            }
            return null;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanDefinitionPropertyValueCodeGeneratorDelegates$TypedStringValueDelegate.class */
    private static class TypedStringValueDelegate implements ValueCodeGenerator.Delegate {
        private TypedStringValueDelegate() {
        }

        @Override // org.springframework.aot.generate.ValueCodeGenerator.Delegate
        public CodeBlock generateCode(ValueCodeGenerator valueCodeGenerator, Object value) {
            if (value instanceof TypedStringValue) {
                TypedStringValue typedStringValue = (TypedStringValue) value;
                return generateTypeStringValueCode(valueCodeGenerator, typedStringValue);
            }
            return null;
        }

        private CodeBlock generateTypeStringValueCode(ValueCodeGenerator valueCodeGenerator, TypedStringValue typedStringValue) {
            String value = typedStringValue.getValue();
            if (typedStringValue.hasTargetType()) {
                return CodeBlock.of("new $T($S, $L)", TypedStringValue.class, value, valueCodeGenerator.generateCode(typedStringValue.getTargetType()));
            }
            return valueCodeGenerator.generateCode(value);
        }
    }
}
