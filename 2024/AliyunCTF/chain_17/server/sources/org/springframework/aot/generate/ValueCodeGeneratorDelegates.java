package org.springframework.aot.generate;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;
import org.springframework.aot.generate.ValueCodeGenerator;
import org.springframework.core.ResolvableType;
import org.springframework.javapoet.CodeBlock;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGeneratorDelegates.class */
public abstract class ValueCodeGeneratorDelegates {
    public static final List<ValueCodeGenerator.Delegate> INSTANCES = List.of(new PrimitiveDelegate(), new StringDelegate(), new CharsetDelegate(), new EnumDelegate(), new ClassDelegate(), new ResolvableTypeDelegate(), new ArrayDelegate(), new ListDelegate(), new SetDelegate(), new MapDelegate());

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGeneratorDelegates$CollectionDelegate.class */
    public static abstract class CollectionDelegate<T extends Collection<?>> implements ValueCodeGenerator.Delegate {
        private final Class<?> collectionType;
        private final CodeBlock emptyResult;

        /* JADX INFO: Access modifiers changed from: protected */
        public CollectionDelegate(Class<?> collectionType, CodeBlock emptyResult) {
            this.collectionType = collectionType;
            this.emptyResult = emptyResult;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.springframework.aot.generate.ValueCodeGenerator.Delegate
        public CodeBlock generateCode(ValueCodeGenerator valueCodeGenerator, Object value) {
            if (this.collectionType.isInstance(value)) {
                Collection collection = (Collection) value;
                if (collection.isEmpty()) {
                    return this.emptyResult;
                }
                return generateCollectionCode(valueCodeGenerator, collection);
            }
            return null;
        }

        protected CodeBlock generateCollectionCode(ValueCodeGenerator valueCodeGenerator, T collection) {
            return generateCollectionOf(valueCodeGenerator, collection, this.collectionType);
        }

        protected final CodeBlock generateCollectionOf(ValueCodeGenerator valueCodeGenerator, Collection<?> collection, Class<?> collectionType) {
            CodeBlock.Builder code = CodeBlock.builder();
            code.add("$T.of(", collectionType);
            Iterator<?> iterator = collection.iterator();
            while (iterator.hasNext()) {
                Object element = iterator.next();
                code.add("$L", valueCodeGenerator.generateCode(element));
                if (iterator.hasNext()) {
                    code.add(", ", new Object[0]);
                }
            }
            code.add(")", new Object[0]);
            return code.build();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGeneratorDelegates$MapDelegate.class */
    public static class MapDelegate implements ValueCodeGenerator.Delegate {
        private static final CodeBlock EMPTY_RESULT = CodeBlock.of("$T.emptyMap()", Collections.class);

        @Override // org.springframework.aot.generate.ValueCodeGenerator.Delegate
        public CodeBlock generateCode(ValueCodeGenerator valueCodeGenerator, Object value) {
            if (value instanceof Map) {
                Map<?, ?> map = (Map) value;
                if (map.isEmpty()) {
                    return EMPTY_RESULT;
                }
                return generateMapCode(valueCodeGenerator, map);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Nullable
        public CodeBlock generateMapCode(ValueCodeGenerator valueCodeGenerator, Map<?, ?> map) {
            Map<?, ?> map2 = orderForCodeConsistency(map);
            boolean useOfEntries = map2.size() > 10;
            CodeBlock.Builder code = CodeBlock.builder();
            code.add("$T" + (!useOfEntries ? ".of(" : ".ofEntries("), Map.class);
            Iterator<? extends Map.Entry<?, ?>> iterator = map2.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<?, ?> entry = iterator.next();
                CodeBlock keyCode = valueCodeGenerator.generateCode(entry.getKey());
                CodeBlock valueCode = valueCodeGenerator.generateCode(entry.getValue());
                if (!useOfEntries) {
                    code.add("$L, $L", keyCode, valueCode);
                } else {
                    code.add("$T.entry($L,$L)", Map.class, keyCode, valueCode);
                }
                if (iterator.hasNext()) {
                    code.add(", ", new Object[0]);
                }
            }
            code.add(")", new Object[0]);
            return code.build();
        }

        private <K, V> Map<K, V> orderForCodeConsistency(Map<K, V> map) {
            try {
                return new TreeMap(map);
            } catch (ClassCastException e) {
                return map;
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGeneratorDelegates$PrimitiveDelegate.class */
    private static class PrimitiveDelegate implements ValueCodeGenerator.Delegate {
        private static final Map<Character, String> CHAR_ESCAPES = Map.of('\b', "\\b", '\t', "\\t", '\n', "\\n", '\f', "\\f", '\r', "\\r", '\"', "\"", '\'', "\\'", '\\', "\\\\");

        private PrimitiveDelegate() {
        }

        @Override // org.springframework.aot.generate.ValueCodeGenerator.Delegate
        @Nullable
        public CodeBlock generateCode(ValueCodeGenerator codeGenerator, Object value) {
            if ((value instanceof Boolean) || (value instanceof Integer)) {
                return CodeBlock.of("$L", value);
            }
            if (value instanceof Byte) {
                return CodeBlock.of("(byte) $L", value);
            }
            if (value instanceof Short) {
                return CodeBlock.of("(short) $L", value);
            }
            if (value instanceof Long) {
                return CodeBlock.of("$LL", value);
            }
            if (value instanceof Float) {
                return CodeBlock.of("$LF", value);
            }
            if (value instanceof Double) {
                return CodeBlock.of("(double) $L", value);
            }
            if (value instanceof Character) {
                Character character = (Character) value;
                return CodeBlock.of("'$L'", escape(character.charValue()));
            }
            return null;
        }

        private String escape(char ch2) {
            String escaped = CHAR_ESCAPES.get(Character.valueOf(ch2));
            if (escaped != null) {
                return escaped;
            }
            return !Character.isISOControl(ch2) ? Character.toString(ch2) : String.format("\\u%04x", Integer.valueOf(ch2));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGeneratorDelegates$StringDelegate.class */
    private static class StringDelegate implements ValueCodeGenerator.Delegate {
        private StringDelegate() {
        }

        @Override // org.springframework.aot.generate.ValueCodeGenerator.Delegate
        @Nullable
        public CodeBlock generateCode(ValueCodeGenerator codeGenerator, Object value) {
            if (value instanceof String) {
                return CodeBlock.of("$S", value);
            }
            return null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGeneratorDelegates$CharsetDelegate.class */
    private static class CharsetDelegate implements ValueCodeGenerator.Delegate {
        private CharsetDelegate() {
        }

        @Override // org.springframework.aot.generate.ValueCodeGenerator.Delegate
        @Nullable
        public CodeBlock generateCode(ValueCodeGenerator codeGenerator, Object value) {
            if (value instanceof Charset) {
                Charset charset = (Charset) value;
                return CodeBlock.of("$T.forName($S)", Charset.class, charset.name());
            }
            return null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGeneratorDelegates$EnumDelegate.class */
    private static class EnumDelegate implements ValueCodeGenerator.Delegate {
        private EnumDelegate() {
        }

        @Override // org.springframework.aot.generate.ValueCodeGenerator.Delegate
        @Nullable
        public CodeBlock generateCode(ValueCodeGenerator codeGenerator, Object value) {
            if (value instanceof Enum) {
                Enum<?> enumValue = (Enum) value;
                return CodeBlock.of("$T.$L", enumValue.getDeclaringClass(), enumValue.name());
            }
            return null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGeneratorDelegates$ClassDelegate.class */
    private static class ClassDelegate implements ValueCodeGenerator.Delegate {
        private ClassDelegate() {
        }

        @Override // org.springframework.aot.generate.ValueCodeGenerator.Delegate
        @Nullable
        public CodeBlock generateCode(ValueCodeGenerator codeGenerator, Object value) {
            if (value instanceof Class) {
                Class<?> clazz = (Class) value;
                return CodeBlock.of("$T.class", ClassUtils.getUserClass(clazz));
            }
            return null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGeneratorDelegates$ResolvableTypeDelegate.class */
    private static class ResolvableTypeDelegate implements ValueCodeGenerator.Delegate {
        private ResolvableTypeDelegate() {
        }

        @Override // org.springframework.aot.generate.ValueCodeGenerator.Delegate
        @Nullable
        public CodeBlock generateCode(ValueCodeGenerator codeGenerator, Object value) {
            if (value instanceof ResolvableType) {
                ResolvableType resolvableType = (ResolvableType) value;
                return generateCode(resolvableType, false);
            }
            return null;
        }

        private static CodeBlock generateCode(ResolvableType resolvableType, boolean allowClassResult) {
            if (ResolvableType.NONE.equals(resolvableType)) {
                return CodeBlock.of("$T.NONE", ResolvableType.class);
            }
            Class<?> type = ClassUtils.getUserClass(resolvableType.toClass());
            if (resolvableType.hasGenerics() && !resolvableType.hasUnresolvableGenerics()) {
                return generateCodeWithGenerics(resolvableType, type);
            }
            if (allowClassResult) {
                return CodeBlock.of("$T.class", type);
            }
            return CodeBlock.of("$T.forClass($T.class)", ResolvableType.class, type);
        }

        private static CodeBlock generateCodeWithGenerics(ResolvableType target, Class<?> type) {
            ResolvableType[] generics = target.getGenerics();
            boolean hasNoNestedGenerics = Arrays.stream(generics).noneMatch((v0) -> {
                return v0.hasGenerics();
            });
            CodeBlock.Builder code = CodeBlock.builder();
            code.add("$T.forClassWithGenerics($T.class", ResolvableType.class, type);
            for (ResolvableType generic : generics) {
                code.add(", $L", generateCode(generic, hasNoNestedGenerics));
            }
            code.add(")", new Object[0]);
            return code.build();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGeneratorDelegates$ArrayDelegate.class */
    private static class ArrayDelegate implements ValueCodeGenerator.Delegate {
        private ArrayDelegate() {
        }

        @Override // org.springframework.aot.generate.ValueCodeGenerator.Delegate
        @Nullable
        public CodeBlock generateCode(ValueCodeGenerator codeGenerator, Object value) {
            if (value.getClass().isArray()) {
                Stream stream = Arrays.stream(ObjectUtils.toObjectArray(value));
                Objects.requireNonNull(codeGenerator);
                Stream<CodeBlock> elements = stream.map(codeGenerator::generateCode);
                CodeBlock.Builder code = CodeBlock.builder();
                code.add("new $T {", value.getClass());
                code.add((CodeBlock) elements.collect(CodeBlock.joining(", ")));
                code.add("}", new Object[0]);
                return code.build();
            }
            return null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGeneratorDelegates$ListDelegate.class */
    private static class ListDelegate extends CollectionDelegate<List<?>> {
        ListDelegate() {
            super(List.class, CodeBlock.of("$T.emptyList()", Collections.class));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/ValueCodeGeneratorDelegates$SetDelegate.class */
    private static class SetDelegate extends CollectionDelegate<Set<?>> {
        SetDelegate() {
            super(Set.class, CodeBlock.of("$T.emptySet()", Collections.class));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.aot.generate.ValueCodeGeneratorDelegates.CollectionDelegate
        public CodeBlock generateCollectionCode(ValueCodeGenerator valueCodeGenerator, Set<?> collection) {
            if (collection instanceof LinkedHashSet) {
                return CodeBlock.of("new $T($L)", LinkedHashSet.class, generateCollectionOf(valueCodeGenerator, collection, List.class));
            }
            return super.generateCollectionCode(valueCodeGenerator, (ValueCodeGenerator) orderForCodeConsistency(collection));
        }

        private Set<?> orderForCodeConsistency(Set<?> set) {
            try {
                return new TreeSet(set);
            } catch (ClassCastException e) {
                return set;
            }
        }
    }
}
