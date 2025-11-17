package org.springframework.expression.spel.ast;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.cglib.core.Constants;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.ConstructorExecutor;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypedValue;
import org.springframework.expression.common.ExpressionUtils;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.ReflectiveConstructorExecutor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/ConstructorReference.class */
public class ConstructorReference extends SpelNodeImpl {
    private static final int MAX_ARRAY_ELEMENTS = 262144;
    private final boolean isArrayConstructor;

    @Nullable
    private final SpelNodeImpl[] dimensions;

    @Nullable
    private volatile ConstructorExecutor cachedExecutor;

    public ConstructorReference(int startPos, int endPos, SpelNodeImpl... arguments) {
        super(startPos, endPos, arguments);
        this.isArrayConstructor = false;
        this.dimensions = null;
    }

    public ConstructorReference(int startPos, int endPos, SpelNodeImpl[] dimensions, SpelNodeImpl... arguments) {
        super(startPos, endPos, arguments);
        this.isArrayConstructor = true;
        this.dimensions = dimensions;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        if (this.isArrayConstructor) {
            return createArray(state);
        }
        return createNewInstance(state);
    }

    private TypedValue createNewInstance(ExpressionState state) throws EvaluationException {
        Object[] arguments = new Object[getChildCount() - 1];
        List<TypeDescriptor> argumentTypes = new ArrayList<>(getChildCount() - 1);
        for (int i = 0; i < arguments.length; i++) {
            TypedValue childValue = this.children[i + 1].getValueInternal(state);
            Object value = childValue.getValue();
            arguments[i] = value;
            argumentTypes.add(TypeDescriptor.forObject(value));
        }
        ConstructorExecutor executorToUse = this.cachedExecutor;
        if (executorToUse != null) {
            try {
                return executorToUse.execute(state.getEvaluationContext(), arguments);
            } catch (AccessException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof InvocationTargetException) {
                    InvocationTargetException cause2 = (InvocationTargetException) cause;
                    Throwable rootCause = cause2.getCause();
                    if (rootCause instanceof RuntimeException) {
                        RuntimeException runtimeException = (RuntimeException) rootCause;
                        throw runtimeException;
                    }
                    throw new SpelEvaluationException(getStartPosition(), rootCause, SpelMessage.CONSTRUCTOR_INVOCATION_PROBLEM, (String) this.children[0].getValueInternal(state).getValue(), FormatHelper.formatMethodForMessage("", argumentTypes));
                }
                this.cachedExecutor = null;
            }
        }
        String typeName = (String) this.children[0].getValueInternal(state).getValue();
        Assert.state(typeName != null, "No type name");
        ConstructorExecutor executorToUse2 = findExecutorForConstructor(typeName, argumentTypes, state);
        try {
            this.cachedExecutor = executorToUse2;
            if (executorToUse2 instanceof ReflectiveConstructorExecutor) {
                ReflectiveConstructorExecutor reflectiveConstructorExecutor = (ReflectiveConstructorExecutor) executorToUse2;
                this.exitTypeDescriptor = CodeFlow.toDescriptor(reflectiveConstructorExecutor.getConstructor().getDeclaringClass());
            }
            return executorToUse2.execute(state.getEvaluationContext(), arguments);
        } catch (AccessException ex2) {
            throw new SpelEvaluationException(getStartPosition(), ex2, SpelMessage.CONSTRUCTOR_INVOCATION_PROBLEM, typeName, FormatHelper.formatMethodForMessage("", argumentTypes));
        }
    }

    private ConstructorExecutor findExecutorForConstructor(String typeName, List<TypeDescriptor> argumentTypes, ExpressionState state) throws SpelEvaluationException {
        EvaluationContext evalContext = state.getEvaluationContext();
        List<ConstructorResolver> ctorResolvers = evalContext.getConstructorResolvers();
        for (ConstructorResolver ctorResolver : ctorResolvers) {
            try {
                ConstructorExecutor ce = ctorResolver.resolve(state.getEvaluationContext(), typeName, argumentTypes);
                if (ce != null) {
                    return ce;
                }
            } catch (AccessException ex) {
                throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.CONSTRUCTOR_INVOCATION_PROBLEM, typeName, FormatHelper.formatMethodForMessage("", argumentTypes));
            }
        }
        throw new SpelEvaluationException(getStartPosition(), SpelMessage.CONSTRUCTOR_NOT_FOUND, typeName, FormatHelper.formatMethodForMessage("", argumentTypes));
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        StringBuilder sb = new StringBuilder("new ");
        sb.append(getChild(0).toStringAST());
        if (this.isArrayConstructor) {
            if (hasInitializer()) {
                InlineList initializer = (InlineList) getChild(1);
                sb.append("[] ").append(initializer.toStringAST());
            } else {
                for (SpelNodeImpl dimension : this.dimensions) {
                    sb.append('[').append(dimension.toStringAST()).append(']');
                }
            }
        } else {
            StringJoiner sj = new StringJoiner(",", "(", ")");
            int count = getChildCount();
            for (int i = 1; i < count; i++) {
                sj.add(getChild(i).toStringAST());
            }
            sb.append(sj.toString());
        }
        return sb.toString();
    }

    private TypedValue createArray(ExpressionState state) throws EvaluationException {
        Class<?> componentType;
        Object createDoubleArray;
        Object intendedArrayType = getChild(0).getValue(state);
        if (!(intendedArrayType instanceof String)) {
            int startPosition = getChild(0).getStartPosition();
            SpelMessage spelMessage = SpelMessage.TYPE_NAME_EXPECTED_FOR_ARRAY_CONSTRUCTION;
            Object[] objArr = new Object[1];
            objArr[0] = FormatHelper.formatClassNameForMessage(intendedArrayType != null ? intendedArrayType.getClass() : null);
            throw new SpelEvaluationException(startPosition, spelMessage, objArr);
        }
        String type = (String) intendedArrayType;
        if (state.getEvaluationContext().getConstructorResolvers().isEmpty()) {
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.CONSTRUCTOR_NOT_FOUND, type + "[]", ClassUtils.ARRAY_SUFFIX);
        }
        TypeCode arrayTypeCode = TypeCode.forName(type);
        if (arrayTypeCode == TypeCode.OBJECT) {
            componentType = state.findType(type);
        } else {
            componentType = arrayTypeCode.getType();
        }
        Object newArray = null;
        if (!hasInitializer()) {
            if (this.dimensions != null) {
                for (SpelNodeImpl dimension : this.dimensions) {
                    if (dimension == null) {
                        throw new SpelEvaluationException(getStartPosition(), SpelMessage.MISSING_ARRAY_DIMENSION, new Object[0]);
                    }
                }
                TypeConverter typeConverter = state.getEvaluationContext().getTypeConverter();
                if (this.dimensions.length == 1) {
                    TypedValue o = this.dimensions[0].getTypedValue(state);
                    int arraySize = ExpressionUtils.toInt(typeConverter, o);
                    checkNumElements(arraySize);
                    newArray = Array.newInstance(componentType, arraySize);
                } else {
                    int[] dims = new int[this.dimensions.length];
                    long numElements = 1;
                    for (int d = 0; d < this.dimensions.length; d++) {
                        TypedValue o2 = this.dimensions[d].getTypedValue(state);
                        int arraySize2 = ExpressionUtils.toInt(typeConverter, o2);
                        dims[d] = arraySize2;
                        numElements *= arraySize2;
                        checkNumElements(numElements);
                    }
                    newArray = Array.newInstance(componentType, dims);
                }
            }
        } else {
            if (this.dimensions == null || this.dimensions.length > 1) {
                throw new SpelEvaluationException(getStartPosition(), SpelMessage.MULTIDIM_ARRAY_INITIALIZER_NOT_SUPPORTED, new Object[0]);
            }
            TypeConverter typeConverter2 = state.getEvaluationContext().getTypeConverter();
            InlineList initializer = (InlineList) getChild(1);
            if (this.dimensions[0] != null) {
                TypedValue dValue = this.dimensions[0].getTypedValue(state);
                int i = ExpressionUtils.toInt(typeConverter2, dValue);
                if (i != initializer.getChildCount()) {
                    throw new SpelEvaluationException(getStartPosition(), SpelMessage.INITIALIZER_LENGTH_INCORRECT, new Object[0]);
                }
            }
            switch (arrayTypeCode) {
                case OBJECT:
                    createDoubleArray = createReferenceTypeArray(state, typeConverter2, initializer.children, componentType);
                    break;
                case BOOLEAN:
                    createDoubleArray = createBooleanArray(state, typeConverter2, initializer.children);
                    break;
                case CHAR:
                    createDoubleArray = createCharArray(state, typeConverter2, initializer.children);
                    break;
                case BYTE:
                    createDoubleArray = createByteArray(state, typeConverter2, initializer.children);
                    break;
                case SHORT:
                    createDoubleArray = createShortArray(state, typeConverter2, initializer.children);
                    break;
                case INT:
                    createDoubleArray = createIntArray(state, typeConverter2, initializer.children);
                    break;
                case LONG:
                    createDoubleArray = createLongArray(state, typeConverter2, initializer.children);
                    break;
                case FLOAT:
                    createDoubleArray = createFloatArray(state, typeConverter2, initializer.children);
                    break;
                case DOUBLE:
                    createDoubleArray = createDoubleArray(state, typeConverter2, initializer.children);
                    break;
                default:
                    throw new IllegalStateException("Unsupported TypeCode: " + arrayTypeCode);
            }
            newArray = createDoubleArray;
        }
        return new TypedValue(newArray);
    }

    private void checkNumElements(long numElements) {
        if (numElements >= 262144) {
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.MAX_ARRAY_ELEMENTS_THRESHOLD_EXCEEDED, 262144);
        }
    }

    private Object createReferenceTypeArray(ExpressionState state, TypeConverter typeConverter, SpelNodeImpl[] children, Class<?> componentType) {
        Object[] array = (Object[]) Array.newInstance(componentType, children.length);
        TypeDescriptor targetType = TypeDescriptor.valueOf(componentType);
        for (int i = 0; i < array.length; i++) {
            Object value = children[i].getValue(state);
            array[i] = typeConverter.convertValue(value, TypeDescriptor.forObject(value), targetType);
        }
        return array;
    }

    private boolean[] createBooleanArray(ExpressionState state, TypeConverter typeConverter, SpelNodeImpl[] children) {
        boolean[] array = new boolean[children.length];
        for (int i = 0; i < array.length; i++) {
            TypedValue typedValue = children[i].getTypedValue(state);
            array[i] = ExpressionUtils.toBoolean(typeConverter, typedValue);
        }
        return array;
    }

    private char[] createCharArray(ExpressionState state, TypeConverter typeConverter, SpelNodeImpl[] children) {
        char[] array = new char[children.length];
        for (int i = 0; i < array.length; i++) {
            TypedValue typedValue = children[i].getTypedValue(state);
            array[i] = ExpressionUtils.toChar(typeConverter, typedValue);
        }
        return array;
    }

    private byte[] createByteArray(ExpressionState state, TypeConverter converter, SpelNodeImpl[] children) {
        byte[] array = new byte[children.length];
        for (int i = 0; i < array.length; i++) {
            TypedValue typedValue = children[i].getTypedValue(state);
            array[i] = ExpressionUtils.toByte(converter, typedValue);
        }
        return array;
    }

    private short[] createShortArray(ExpressionState state, TypeConverter typeConverter, SpelNodeImpl[] children) {
        short[] array = new short[children.length];
        for (int i = 0; i < array.length; i++) {
            TypedValue typedValue = children[i].getTypedValue(state);
            array[i] = ExpressionUtils.toShort(typeConverter, typedValue);
        }
        return array;
    }

    private int[] createIntArray(ExpressionState state, TypeConverter typeConverter, SpelNodeImpl[] children) {
        int[] array = new int[children.length];
        for (int i = 0; i < array.length; i++) {
            TypedValue typedValue = children[i].getTypedValue(state);
            array[i] = ExpressionUtils.toInt(typeConverter, typedValue);
        }
        return array;
    }

    private long[] createLongArray(ExpressionState state, TypeConverter converter, SpelNodeImpl[] children) {
        long[] array = new long[children.length];
        for (int i = 0; i < array.length; i++) {
            TypedValue typedValue = children[i].getTypedValue(state);
            array[i] = ExpressionUtils.toLong(converter, typedValue);
        }
        return array;
    }

    private float[] createFloatArray(ExpressionState state, TypeConverter typeConverter, SpelNodeImpl[] children) {
        float[] array = new float[children.length];
        for (int i = 0; i < array.length; i++) {
            TypedValue typedValue = children[i].getTypedValue(state);
            array[i] = ExpressionUtils.toFloat(typeConverter, typedValue);
        }
        return array;
    }

    private double[] createDoubleArray(ExpressionState state, TypeConverter typeConverter, SpelNodeImpl[] children) {
        double[] array = new double[children.length];
        for (int i = 0; i < array.length; i++) {
            TypedValue typedValue = children[i].getTypedValue(state);
            array[i] = ExpressionUtils.toDouble(typeConverter, typedValue);
        }
        return array;
    }

    private boolean hasInitializer() {
        return getChildCount() > 1;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        ConstructorExecutor constructorExecutor = this.cachedExecutor;
        if (!(constructorExecutor instanceof ReflectiveConstructorExecutor)) {
            return false;
        }
        ReflectiveConstructorExecutor executor = (ReflectiveConstructorExecutor) constructorExecutor;
        if (this.exitTypeDescriptor == null) {
            return false;
        }
        if (getChildCount() > 1) {
            int max = getChildCount();
            for (int c = 1; c < max; c++) {
                if (!this.children[c].isCompilable()) {
                    return false;
                }
            }
        }
        Constructor<?> constructor = executor.getConstructor();
        return Modifier.isPublic(constructor.getModifiers()) && Modifier.isPublic(constructor.getDeclaringClass().getModifiers());
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        ReflectiveConstructorExecutor executor = (ReflectiveConstructorExecutor) this.cachedExecutor;
        Assert.state(executor != null, "No cached executor");
        Constructor<?> constructor = executor.getConstructor();
        String classDesc = constructor.getDeclaringClass().getName().replace('.', '/');
        mv.visitTypeInsn(Opcodes.NEW, classDesc);
        mv.visitInsn(89);
        SpelNodeImpl[] arguments = new SpelNodeImpl[this.children.length - 1];
        System.arraycopy(this.children, 1, arguments, 0, this.children.length - 1);
        generateCodeForArguments(mv, cf, constructor, arguments);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classDesc, Constants.CONSTRUCTOR_NAME, CodeFlow.createSignatureDescriptor(constructor), false);
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}
