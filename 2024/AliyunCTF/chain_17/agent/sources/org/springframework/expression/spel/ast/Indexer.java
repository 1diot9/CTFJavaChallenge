package org.springframework.expression.spel.ast;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Supplier;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/Indexer.class */
public class Indexer extends SpelNodeImpl {

    @Nullable
    private String cachedReadName;

    @Nullable
    private Class<?> cachedReadTargetType;

    @Nullable
    private PropertyAccessor cachedReadAccessor;

    @Nullable
    private String cachedWriteName;

    @Nullable
    private Class<?> cachedWriteTargetType;

    @Nullable
    private PropertyAccessor cachedWriteAccessor;

    @Nullable
    private IndexedType indexedType;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/Indexer$IndexedType.class */
    public enum IndexedType {
        ARRAY,
        LIST,
        MAP,
        STRING,
        OBJECT
    }

    public Indexer(int startPos, int endPos, SpelNodeImpl expr) {
        super(startPos, endPos, expr);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        return getValueRef(state).getValue();
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue setValueInternal(ExpressionState state, Supplier<TypedValue> valueSupplier) throws EvaluationException {
        TypedValue typedValue = valueSupplier.get();
        getValueRef(state).setValue(typedValue.getValue());
        return typedValue;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl, org.springframework.expression.spel.SpelNode
    public boolean isWritable(ExpressionState expressionState) throws SpelEvaluationException {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:11:0x0094  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0081  */
    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.springframework.expression.spel.ast.ValueRef getValueRef(org.springframework.expression.spel.ExpressionState r11) throws org.springframework.expression.EvaluationException {
        /*
            Method dump skipped, instructions count: 463
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.expression.spel.ast.Indexer.getValueRef(org.springframework.expression.spel.ExpressionState):org.springframework.expression.spel.ast.ValueRef");
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        if (this.indexedType == IndexedType.ARRAY) {
            return this.exitTypeDescriptor != null;
        }
        if (this.indexedType == IndexedType.LIST) {
            return this.children[0].isCompilable();
        }
        return this.indexedType == IndexedType.MAP ? (this.children[0] instanceof PropertyOrFieldReference) || this.children[0].isCompilable() : this.indexedType == IndexedType.OBJECT && this.cachedReadAccessor != null && (this.cachedReadAccessor instanceof ReflectivePropertyAccessor.OptimalPropertyAccessor) && (getChild(0) instanceof StringLiteral);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        int insn;
        String descriptor = cf.lastDescriptor();
        if (descriptor == null) {
            cf.loadTarget(mv);
        }
        if (this.indexedType == IndexedType.ARRAY) {
            if ("D".equals(this.exitTypeDescriptor)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[D");
                insn = 49;
            } else if ("F".equals(this.exitTypeDescriptor)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[F");
                insn = 48;
            } else if ("J".equals(this.exitTypeDescriptor)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[J");
                insn = 47;
            } else if ("I".equals(this.exitTypeDescriptor)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[I");
                insn = 46;
            } else if ("S".equals(this.exitTypeDescriptor)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[S");
                insn = 53;
            } else if ("B".equals(this.exitTypeDescriptor)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[B");
                insn = 51;
            } else if ("C".equals(this.exitTypeDescriptor)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[C");
                insn = 52;
            } else {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[" + this.exitTypeDescriptor + (CodeFlow.isPrimitiveArray(this.exitTypeDescriptor) ? "" : ";"));
                insn = 50;
            }
            SpelNodeImpl index = this.children[0];
            cf.enterCompilationScope();
            index.generateCode(mv, cf);
            cf.exitCompilationScope();
            mv.visitInsn(insn);
        } else if (this.indexedType == IndexedType.LIST) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/util/List");
            cf.enterCompilationScope();
            this.children[0].generateCode(mv, cf);
            cf.exitCompilationScope();
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", BeanUtil.PREFIX_GETTER_GET, "(I)Ljava/lang/Object;", true);
        } else if (this.indexedType == IndexedType.MAP) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/util/Map");
            SpelNodeImpl spelNodeImpl = this.children[0];
            if (spelNodeImpl instanceof PropertyOrFieldReference) {
                PropertyOrFieldReference reference = (PropertyOrFieldReference) spelNodeImpl;
                String mapKeyName = reference.getName();
                mv.visitLdcInsn(mapKeyName);
            } else {
                cf.enterCompilationScope();
                this.children[0].generateCode(mv, cf);
                cf.exitCompilationScope();
            }
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map", BeanUtil.PREFIX_GETTER_GET, "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        } else if (this.indexedType == IndexedType.OBJECT) {
            ReflectivePropertyAccessor.OptimalPropertyAccessor accessor = (ReflectivePropertyAccessor.OptimalPropertyAccessor) this.cachedReadAccessor;
            Assert.state(accessor != null, "No cached read accessor");
            Member member = accessor.member;
            boolean isStatic = Modifier.isStatic(member.getModifiers());
            String classDesc = member.getDeclaringClass().getName().replace('.', '/');
            if (!isStatic) {
                if (descriptor == null) {
                    cf.loadTarget(mv);
                }
                if (descriptor == null || !classDesc.equals(descriptor.substring(1))) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, classDesc);
                }
            }
            if (member instanceof Method) {
                Method method = (Method) member;
                mv.visitMethodInsn(isStatic ? 184 : Opcodes.INVOKEVIRTUAL, classDesc, member.getName(), CodeFlow.createSignatureDescriptor(method), false);
            } else {
                mv.visitFieldInsn(isStatic ? Opcodes.GETSTATIC : Opcodes.GETFIELD, classDesc, member.getName(), CodeFlow.toJvmDescriptor(((Field) member).getType()));
            }
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        StringJoiner sj = new StringJoiner(",", "[", "]");
        for (int i = 0; i < getChildCount(); i++) {
            sj.add(getChild(i).toStringAST());
        }
        return sj.toString();
    }

    private void setArrayElement(TypeConverter converter, Object ctx, int idx, @Nullable Object newValue, Class<?> arrayComponentType) throws EvaluationException {
        if (arrayComponentType == Boolean.TYPE) {
            boolean[] array = (boolean[]) ctx;
            checkAccess(array.length, idx);
            array[idx] = ((Boolean) convertValue(converter, newValue, Boolean.class)).booleanValue();
            return;
        }
        if (arrayComponentType == Byte.TYPE) {
            byte[] array2 = (byte[]) ctx;
            checkAccess(array2.length, idx);
            array2[idx] = ((Byte) convertValue(converter, newValue, Byte.class)).byteValue();
            return;
        }
        if (arrayComponentType == Character.TYPE) {
            char[] array3 = (char[]) ctx;
            checkAccess(array3.length, idx);
            array3[idx] = ((Character) convertValue(converter, newValue, Character.class)).charValue();
            return;
        }
        if (arrayComponentType == Double.TYPE) {
            double[] array4 = (double[]) ctx;
            checkAccess(array4.length, idx);
            array4[idx] = ((Double) convertValue(converter, newValue, Double.class)).doubleValue();
            return;
        }
        if (arrayComponentType == Float.TYPE) {
            float[] array5 = (float[]) ctx;
            checkAccess(array5.length, idx);
            array5[idx] = ((Float) convertValue(converter, newValue, Float.class)).floatValue();
            return;
        }
        if (arrayComponentType == Integer.TYPE) {
            int[] array6 = (int[]) ctx;
            checkAccess(array6.length, idx);
            array6[idx] = ((Integer) convertValue(converter, newValue, Integer.class)).intValue();
        } else if (arrayComponentType == Long.TYPE) {
            long[] array7 = (long[]) ctx;
            checkAccess(array7.length, idx);
            array7[idx] = ((Long) convertValue(converter, newValue, Long.class)).longValue();
        } else if (arrayComponentType == Short.TYPE) {
            short[] array8 = (short[]) ctx;
            checkAccess(array8.length, idx);
            array8[idx] = ((Short) convertValue(converter, newValue, Short.class)).shortValue();
        } else {
            Object[] array9 = (Object[]) ctx;
            checkAccess(array9.length, idx);
            array9[idx] = convertValue(converter, newValue, arrayComponentType);
        }
    }

    private Object accessArrayElement(Object ctx, int idx) throws SpelEvaluationException {
        Class<?> arrayComponentType = ctx.getClass().componentType();
        if (arrayComponentType == Boolean.TYPE) {
            boolean[] array = (boolean[]) ctx;
            checkAccess(array.length, idx);
            this.exitTypeDescriptor = "Z";
            return Boolean.valueOf(array[idx]);
        }
        if (arrayComponentType == Byte.TYPE) {
            byte[] array2 = (byte[]) ctx;
            checkAccess(array2.length, idx);
            this.exitTypeDescriptor = "B";
            return Byte.valueOf(array2[idx]);
        }
        if (arrayComponentType == Character.TYPE) {
            char[] array3 = (char[]) ctx;
            checkAccess(array3.length, idx);
            this.exitTypeDescriptor = "C";
            return Character.valueOf(array3[idx]);
        }
        if (arrayComponentType == Double.TYPE) {
            double[] array4 = (double[]) ctx;
            checkAccess(array4.length, idx);
            this.exitTypeDescriptor = "D";
            return Double.valueOf(array4[idx]);
        }
        if (arrayComponentType == Float.TYPE) {
            float[] array5 = (float[]) ctx;
            checkAccess(array5.length, idx);
            this.exitTypeDescriptor = "F";
            return Float.valueOf(array5[idx]);
        }
        if (arrayComponentType == Integer.TYPE) {
            int[] array6 = (int[]) ctx;
            checkAccess(array6.length, idx);
            this.exitTypeDescriptor = "I";
            return Integer.valueOf(array6[idx]);
        }
        if (arrayComponentType == Long.TYPE) {
            long[] array7 = (long[]) ctx;
            checkAccess(array7.length, idx);
            this.exitTypeDescriptor = "J";
            return Long.valueOf(array7[idx]);
        }
        if (arrayComponentType == Short.TYPE) {
            short[] array8 = (short[]) ctx;
            checkAccess(array8.length, idx);
            this.exitTypeDescriptor = "S";
            return Short.valueOf(array8[idx]);
        }
        Object[] array9 = (Object[]) ctx;
        checkAccess(array9.length, idx);
        Object retValue = array9[idx];
        this.exitTypeDescriptor = CodeFlow.toDescriptor(arrayComponentType);
        return retValue;
    }

    private void checkAccess(int arrayLength, int index) throws SpelEvaluationException {
        if (index >= arrayLength) {
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.ARRAY_INDEX_OUT_OF_BOUNDS, Integer.valueOf(arrayLength), Integer.valueOf(index));
        }
    }

    private <T> T convertValue(TypeConverter typeConverter, @Nullable Object obj, Class<T> cls) {
        T t = (T) typeConverter.convertValue(obj, TypeDescriptor.forObject(obj), TypeDescriptor.valueOf(cls));
        if (t == null) {
            throw new IllegalStateException("Null conversion result for index [" + obj + "]");
        }
        return t;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/Indexer$ArrayIndexingValueRef.class */
    public class ArrayIndexingValueRef implements ValueRef {
        private final TypeConverter typeConverter;
        private final Object array;
        private final int index;
        private final TypeDescriptor typeDescriptor;

        ArrayIndexingValueRef(TypeConverter typeConverter, Object array, int index, TypeDescriptor typeDescriptor) {
            this.typeConverter = typeConverter;
            this.array = array;
            this.index = index;
            this.typeDescriptor = typeDescriptor;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            Object arrayElement = Indexer.this.accessArrayElement(this.array, this.index);
            return new TypedValue(arrayElement, this.typeDescriptor.elementTypeDescriptor(arrayElement));
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            TypeDescriptor elementType = this.typeDescriptor.getElementTypeDescriptor();
            Assert.state(elementType != null, "No element type");
            Indexer.this.setArrayElement(this.typeConverter, this.array, this.index, newValue, elementType.getType());
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/Indexer$MapIndexingValueRef.class */
    public class MapIndexingValueRef implements ValueRef {
        private final TypeConverter typeConverter;
        private final Map map;

        @Nullable
        private final Object key;
        private final TypeDescriptor mapEntryDescriptor;

        public MapIndexingValueRef(TypeConverter typeConverter, Map map, @Nullable Object key, TypeDescriptor mapEntryDescriptor) {
            this.typeConverter = typeConverter;
            this.map = map;
            this.key = key;
            this.mapEntryDescriptor = mapEntryDescriptor;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            Object value = this.map.get(this.key);
            Indexer.this.exitTypeDescriptor = CodeFlow.toDescriptor(Object.class);
            return new TypedValue(value, this.mapEntryDescriptor.getMapValueTypeDescriptor(value));
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            if (this.mapEntryDescriptor.getMapValueTypeDescriptor() != null) {
                newValue = this.typeConverter.convertValue(newValue, TypeDescriptor.forObject(newValue), this.mapEntryDescriptor.getMapValueTypeDescriptor());
            }
            this.map.put(this.key, newValue);
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/Indexer$PropertyIndexingValueRef.class */
    public class PropertyIndexingValueRef implements ValueRef {
        private final Object targetObject;
        private final String name;
        private final EvaluationContext evaluationContext;
        private final TypeDescriptor targetObjectTypeDescriptor;

        public PropertyIndexingValueRef(Object targetObject, String value, EvaluationContext evaluationContext, TypeDescriptor targetObjectTypeDescriptor) {
            this.targetObject = targetObject;
            this.name = value;
            this.evaluationContext = evaluationContext;
            this.targetObjectTypeDescriptor = targetObjectTypeDescriptor;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            Class<?> type;
            Class<?> targetObjectRuntimeClass = Indexer.this.getObjectClass(this.targetObject);
            try {
                if (Indexer.this.cachedReadName != null && Indexer.this.cachedReadName.equals(this.name) && Indexer.this.cachedReadTargetType != null && Indexer.this.cachedReadTargetType.equals(targetObjectRuntimeClass)) {
                    PropertyAccessor accessor = Indexer.this.cachedReadAccessor;
                    Assert.state(accessor != null, "No cached read accessor");
                    return accessor.read(this.evaluationContext, this.targetObject, this.name);
                }
                List<PropertyAccessor> accessorsToTry = AstUtils.getPropertyAccessorsToTry(targetObjectRuntimeClass, this.evaluationContext.getPropertyAccessors());
                for (PropertyAccessor accessor2 : accessorsToTry) {
                    if (accessor2.canRead(this.evaluationContext, this.targetObject, this.name)) {
                        if (accessor2 instanceof ReflectivePropertyAccessor) {
                            ReflectivePropertyAccessor reflectivePropertyAccessor = (ReflectivePropertyAccessor) accessor2;
                            accessor2 = reflectivePropertyAccessor.createOptimalAccessor(this.evaluationContext, this.targetObject, this.name);
                        }
                        Indexer.this.cachedReadAccessor = accessor2;
                        Indexer.this.cachedReadName = this.name;
                        Indexer.this.cachedReadTargetType = targetObjectRuntimeClass;
                        if (accessor2 instanceof ReflectivePropertyAccessor.OptimalPropertyAccessor) {
                            ReflectivePropertyAccessor.OptimalPropertyAccessor optimalAccessor = (ReflectivePropertyAccessor.OptimalPropertyAccessor) accessor2;
                            Member member = optimalAccessor.member;
                            Indexer indexer = Indexer.this;
                            if (member instanceof Method) {
                                Method method = (Method) member;
                                type = method.getReturnType();
                            } else {
                                type = ((Field) member).getType();
                            }
                            indexer.exitTypeDescriptor = CodeFlow.toDescriptor(type);
                        }
                        return accessor2.read(this.evaluationContext, this.targetObject, this.name);
                    }
                }
                throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, this.targetObjectTypeDescriptor.toString());
            } catch (AccessException ex) {
                throw new SpelEvaluationException(Indexer.this.getStartPosition(), ex, SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, this.targetObjectTypeDescriptor.toString());
            }
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            Class<?> contextObjectClass = Indexer.this.getObjectClass(this.targetObject);
            try {
                if (Indexer.this.cachedWriteName != null && Indexer.this.cachedWriteName.equals(this.name) && Indexer.this.cachedWriteTargetType != null && Indexer.this.cachedWriteTargetType.equals(contextObjectClass)) {
                    PropertyAccessor accessor = Indexer.this.cachedWriteAccessor;
                    Assert.state(accessor != null, "No cached write accessor");
                    accessor.write(this.evaluationContext, this.targetObject, this.name, newValue);
                    return;
                }
                List<PropertyAccessor> accessorsToTry = AstUtils.getPropertyAccessorsToTry(contextObjectClass, this.evaluationContext.getPropertyAccessors());
                for (PropertyAccessor accessor2 : accessorsToTry) {
                    if (accessor2.canWrite(this.evaluationContext, this.targetObject, this.name)) {
                        Indexer.this.cachedWriteName = this.name;
                        Indexer.this.cachedWriteTargetType = contextObjectClass;
                        Indexer.this.cachedWriteAccessor = accessor2;
                        accessor2.write(this.evaluationContext, this.targetObject, this.name, newValue);
                        return;
                    }
                }
            } catch (AccessException ex) {
                throw new SpelEvaluationException(Indexer.this.getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_PROPERTY_WRITE, this.name, ex.getMessage());
            }
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/Indexer$CollectionIndexingValueRef.class */
    public class CollectionIndexingValueRef implements ValueRef {
        private final Collection collection;
        private final int index;
        private final TypeDescriptor collectionEntryDescriptor;
        private final TypeConverter typeConverter;
        private final boolean growCollection;
        private final int maximumSize;

        public CollectionIndexingValueRef(Collection collection, int index, TypeDescriptor collectionEntryDescriptor, TypeConverter typeConverter, boolean growCollection, int maximumSize) {
            this.collection = collection;
            this.index = index;
            this.collectionEntryDescriptor = collectionEntryDescriptor;
            this.typeConverter = typeConverter;
            this.growCollection = growCollection;
            this.maximumSize = maximumSize;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            growCollectionIfNecessary();
            Collection collection = this.collection;
            if (collection instanceof List) {
                List list = (List) collection;
                Object o = list.get(this.index);
                Indexer.this.exitTypeDescriptor = CodeFlow.toDescriptor(Object.class);
                return new TypedValue(o, this.collectionEntryDescriptor.elementTypeDescriptor(o));
            }
            int pos = 0;
            for (Object o2 : this.collection) {
                if (pos == this.index) {
                    return new TypedValue(o2, this.collectionEntryDescriptor.elementTypeDescriptor(o2));
                }
                pos++;
            }
            throw new IllegalStateException("Failed to find indexed element " + this.index + ": " + this.collection);
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            growCollectionIfNecessary();
            Collection collection = this.collection;
            if (collection instanceof List) {
                List list = (List) collection;
                if (this.collectionEntryDescriptor.getElementTypeDescriptor() != null) {
                    newValue = this.typeConverter.convertValue(newValue, TypeDescriptor.forObject(newValue), this.collectionEntryDescriptor.getElementTypeDescriptor());
                }
                list.set(this.index, newValue);
                return;
            }
            throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, this.collectionEntryDescriptor.toString());
        }

        private void growCollectionIfNecessary() {
            if (this.index >= this.collection.size()) {
                if (!this.growCollection) {
                    throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.COLLECTION_INDEX_OUT_OF_BOUNDS, Integer.valueOf(this.collection.size()), Integer.valueOf(this.index));
                }
                if (this.index >= this.maximumSize) {
                    throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.UNABLE_TO_GROW_COLLECTION, new Object[0]);
                }
                if (this.collectionEntryDescriptor.getElementTypeDescriptor() == null) {
                    throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.UNABLE_TO_GROW_COLLECTION_UNKNOWN_ELEMENT_TYPE, new Object[0]);
                }
                TypeDescriptor elementType = this.collectionEntryDescriptor.getElementTypeDescriptor();
                try {
                    Constructor<?> ctor = getDefaultConstructor(elementType.getType());
                    for (int newElements = this.index - this.collection.size(); newElements >= 0; newElements--) {
                        this.collection.add(ctor != null ? ctor.newInstance(new Object[0]) : null);
                    }
                } catch (Throwable ex) {
                    throw new SpelEvaluationException(Indexer.this.getStartPosition(), ex, SpelMessage.UNABLE_TO_GROW_COLLECTION, new Object[0]);
                }
            }
        }

        @Nullable
        private Constructor<?> getDefaultConstructor(Class<?> type) {
            try {
                return ReflectionUtils.accessibleConstructor(type, new Class[0]);
            } catch (Throwable th) {
                return null;
            }
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/Indexer$StringIndexingLValue.class */
    public class StringIndexingLValue implements ValueRef {
        private final String target;
        private final int index;
        private final TypeDescriptor typeDescriptor;

        public StringIndexingLValue(String target, int index, TypeDescriptor typeDescriptor) {
            this.target = target;
            this.index = index;
            this.typeDescriptor = typeDescriptor;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            if (this.index >= this.target.length()) {
                throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.STRING_INDEX_OUT_OF_BOUNDS, Integer.valueOf(this.target.length()), Integer.valueOf(this.index));
            }
            return new TypedValue(String.valueOf(this.target.charAt(this.index)));
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, this.typeDescriptor.toString());
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return true;
        }
    }
}
