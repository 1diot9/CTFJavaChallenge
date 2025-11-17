package org.springframework.expression.spel.ast;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.CompilablePropertyAccessor;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/PropertyOrFieldReference.class */
public class PropertyOrFieldReference extends SpelNodeImpl {
    private final boolean nullSafe;
    private final String name;

    @Nullable
    private String originalPrimitiveExitTypeDescriptor;

    @Nullable
    private volatile PropertyAccessor cachedReadAccessor;

    @Nullable
    private volatile PropertyAccessor cachedWriteAccessor;

    public PropertyOrFieldReference(boolean nullSafe, String propertyOrFieldName, int startPos, int endPos) {
        super(startPos, endPos, new SpelNodeImpl[0]);
        this.nullSafe = nullSafe;
        this.name = propertyOrFieldName;
    }

    public boolean isNullSafe() {
        return this.nullSafe;
    }

    public String getName() {
        return this.name;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public ValueRef getValueRef(ExpressionState state) throws EvaluationException {
        return new AccessorLValue(this, state.getActiveContextObject(), state.getEvaluationContext(), state.getConfiguration().isAutoGrowNullReferences());
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        TypedValue tv = getValueInternal(state.getActiveContextObject(), state.getEvaluationContext(), state.getConfiguration().isAutoGrowNullReferences());
        PropertyAccessor accessorToUse = this.cachedReadAccessor;
        if (accessorToUse instanceof CompilablePropertyAccessor) {
            CompilablePropertyAccessor compilablePropertyAccessor = (CompilablePropertyAccessor) accessorToUse;
            setExitTypeDescriptor(CodeFlow.toDescriptor(compilablePropertyAccessor.getPropertyType()));
        }
        return tv;
    }

    private TypedValue getValueInternal(TypedValue contextObject, EvaluationContext evalContext, boolean isAutoGrowNullReferences) throws EvaluationException {
        TypedValue result = readProperty(contextObject, evalContext, this.name);
        if (result.getValue() == null && isAutoGrowNullReferences && nextChildIs(Indexer.class, PropertyOrFieldReference.class)) {
            TypeDescriptor resultDescriptor = result.getTypeDescriptor();
            Assert.state(resultDescriptor != null, "No result type");
            if (List.class == resultDescriptor.getType()) {
                if (isWritableProperty(this.name, contextObject, evalContext)) {
                    List<?> newList = new ArrayList<>();
                    writeProperty(contextObject, evalContext, this.name, newList);
                    result = readProperty(contextObject, evalContext, this.name);
                }
            } else if (Map.class == resultDescriptor.getType()) {
                if (isWritableProperty(this.name, contextObject, evalContext)) {
                    Map<?, ?> newMap = new HashMap<>();
                    writeProperty(contextObject, evalContext, this.name, newMap);
                    result = readProperty(contextObject, evalContext, this.name);
                }
            } else {
                try {
                    if (isWritableProperty(this.name, contextObject, evalContext)) {
                        Class<?> clazz = result.getTypeDescriptor().getType();
                        Object newObject = ReflectionUtils.accessibleConstructor(clazz, new Class[0]).newInstance(new Object[0]);
                        writeProperty(contextObject, evalContext, this.name, newObject);
                        result = readProperty(contextObject, evalContext, this.name);
                    }
                } catch (InvocationTargetException ex) {
                    throw new SpelEvaluationException(getStartPosition(), ex.getTargetException(), SpelMessage.UNABLE_TO_DYNAMICALLY_CREATE_OBJECT, result.getTypeDescriptor().getType());
                } catch (Throwable ex2) {
                    throw new SpelEvaluationException(getStartPosition(), ex2, SpelMessage.UNABLE_TO_DYNAMICALLY_CREATE_OBJECT, result.getTypeDescriptor().getType());
                }
            }
        }
        return result;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue setValueInternal(ExpressionState state, Supplier<TypedValue> valueSupplier) throws EvaluationException {
        TypedValue typedValue = valueSupplier.get();
        writeProperty(state.getActiveContextObject(), state.getEvaluationContext(), this.name, typedValue.getValue());
        return typedValue;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl, org.springframework.expression.spel.SpelNode
    public boolean isWritable(ExpressionState state) throws EvaluationException {
        return isWritableProperty(this.name, state.getActiveContextObject(), state.getEvaluationContext());
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        return this.name;
    }

    private TypedValue readProperty(TypedValue contextObject, EvaluationContext evalContext, String name) throws EvaluationException {
        Object targetObject = contextObject.getValue();
        if (targetObject == null && this.nullSafe) {
            return TypedValue.NULL;
        }
        PropertyAccessor accessorToUse = this.cachedReadAccessor;
        if (accessorToUse != null) {
            if (evalContext.getPropertyAccessors().contains(accessorToUse)) {
                try {
                    return accessorToUse.read(evalContext, contextObject.getValue(), name);
                } catch (Exception e) {
                }
            }
            this.cachedReadAccessor = null;
        }
        List<PropertyAccessor> accessorsToTry = getPropertyAccessorsToTry(contextObject.getValue(), evalContext.getPropertyAccessors());
        try {
            for (PropertyAccessor accessor : accessorsToTry) {
                if (accessor.canRead(evalContext, contextObject.getValue(), name)) {
                    if (accessor instanceof ReflectivePropertyAccessor) {
                        ReflectivePropertyAccessor reflectivePropertyAccessor = (ReflectivePropertyAccessor) accessor;
                        accessor = reflectivePropertyAccessor.createOptimalAccessor(evalContext, contextObject.getValue(), name);
                    }
                    this.cachedReadAccessor = accessor;
                    return accessor.read(evalContext, contextObject.getValue(), name);
                }
            }
            if (contextObject.getValue() == null) {
                throw new SpelEvaluationException(SpelMessage.PROPERTY_OR_FIELD_NOT_READABLE_ON_NULL, name);
            }
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.PROPERTY_OR_FIELD_NOT_READABLE, name, FormatHelper.formatClassNameForMessage(getObjectClass(contextObject.getValue())));
        } catch (Exception ex) {
            throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_DURING_PROPERTY_READ, name, ex.getMessage());
        }
    }

    private void writeProperty(TypedValue contextObject, EvaluationContext evalContext, String name, @Nullable Object newValue) throws EvaluationException {
        if (contextObject.getValue() == null && this.nullSafe) {
            return;
        }
        if (contextObject.getValue() == null) {
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.PROPERTY_OR_FIELD_NOT_WRITABLE_ON_NULL, name);
        }
        PropertyAccessor accessorToUse = this.cachedWriteAccessor;
        if (accessorToUse != null) {
            if (evalContext.getPropertyAccessors().contains(accessorToUse)) {
                try {
                    accessorToUse.write(evalContext, contextObject.getValue(), name, newValue);
                    return;
                } catch (Exception e) {
                }
            }
            this.cachedWriteAccessor = null;
        }
        List<PropertyAccessor> accessorsToTry = getPropertyAccessorsToTry(contextObject.getValue(), evalContext.getPropertyAccessors());
        try {
            for (PropertyAccessor accessor : accessorsToTry) {
                if (accessor.canWrite(evalContext, contextObject.getValue(), name)) {
                    this.cachedWriteAccessor = accessor;
                    accessor.write(evalContext, contextObject.getValue(), name, newValue);
                    return;
                }
            }
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.PROPERTY_OR_FIELD_NOT_WRITABLE, name, FormatHelper.formatClassNameForMessage(getObjectClass(contextObject.getValue())));
        } catch (AccessException ex) {
            throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_PROPERTY_WRITE, name, ex.getMessage());
        }
    }

    public boolean isWritableProperty(String name, TypedValue contextObject, EvaluationContext evalContext) throws EvaluationException {
        Object value = contextObject.getValue();
        if (value != null) {
            List<PropertyAccessor> accessorsToTry = getPropertyAccessorsToTry(contextObject.getValue(), evalContext.getPropertyAccessors());
            for (PropertyAccessor accessor : accessorsToTry) {
                if (accessor.canWrite(evalContext, value, name)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private List<PropertyAccessor> getPropertyAccessorsToTry(@Nullable Object contextObject, List<PropertyAccessor> propertyAccessors) {
        Class<?> targetType = contextObject != null ? contextObject.getClass() : null;
        List<PropertyAccessor> specificAccessors = new ArrayList<>();
        ArrayList arrayList = new ArrayList();
        for (PropertyAccessor resolver : propertyAccessors) {
            Class<?>[] targets = resolver.getSpecificTargetClasses();
            if (targets == null) {
                arrayList.add(resolver);
            } else if (targetType != null) {
                int length = targets.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    Class<?> clazz = targets[i];
                    if (clazz == targetType) {
                        specificAccessors.add(resolver);
                        break;
                    }
                    if (clazz.isAssignableFrom(targetType)) {
                        arrayList.add(resolver);
                    }
                    i++;
                }
            }
        }
        List<PropertyAccessor> resolvers = new ArrayList<>(specificAccessors);
        arrayList.removeAll(specificAccessors);
        resolvers.addAll(arrayList);
        return resolvers;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        PropertyAccessor propertyAccessor = this.cachedReadAccessor;
        if (propertyAccessor instanceof CompilablePropertyAccessor) {
            CompilablePropertyAccessor compilablePropertyAccessor = (CompilablePropertyAccessor) propertyAccessor;
            if (compilablePropertyAccessor.isCompilable()) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        PropertyAccessor accessorToUse = this.cachedReadAccessor;
        if (!(accessorToUse instanceof CompilablePropertyAccessor)) {
            throw new IllegalStateException("Property accessor is not compilable: " + accessorToUse);
        }
        CompilablePropertyAccessor compilablePropertyAccessor = (CompilablePropertyAccessor) accessorToUse;
        Label skipIfNull = null;
        if (this.nullSafe) {
            mv.visitInsn(89);
            skipIfNull = new Label();
            Label continueLabel = new Label();
            mv.visitJumpInsn(Opcodes.IFNONNULL, continueLabel);
            CodeFlow.insertCheckCast(mv, this.exitTypeDescriptor);
            mv.visitJumpInsn(Opcodes.GOTO, skipIfNull);
            mv.visitLabel(continueLabel);
        }
        compilablePropertyAccessor.generateCode(this.name, mv, cf);
        cf.pushDescriptor(this.exitTypeDescriptor);
        if (this.originalPrimitiveExitTypeDescriptor != null) {
            CodeFlow.insertBoxIfNecessary(mv, this.originalPrimitiveExitTypeDescriptor);
        }
        if (skipIfNull != null) {
            mv.visitLabel(skipIfNull);
        }
    }

    void setExitTypeDescriptor(String descriptor) {
        if (this.nullSafe && CodeFlow.isPrimitive(descriptor)) {
            this.originalPrimitiveExitTypeDescriptor = descriptor;
            this.exitTypeDescriptor = CodeFlow.toBoxedDescriptor(descriptor);
        } else {
            this.exitTypeDescriptor = descriptor;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/PropertyOrFieldReference$AccessorLValue.class */
    private static class AccessorLValue implements ValueRef {
        private final PropertyOrFieldReference ref;
        private final TypedValue contextObject;
        private final EvaluationContext evalContext;
        private final boolean autoGrowNullReferences;

        public AccessorLValue(PropertyOrFieldReference propertyOrFieldReference, TypedValue activeContextObject, EvaluationContext evalContext, boolean autoGrowNullReferences) {
            this.ref = propertyOrFieldReference;
            this.contextObject = activeContextObject;
            this.evalContext = evalContext;
            this.autoGrowNullReferences = autoGrowNullReferences;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            TypedValue value = this.ref.getValueInternal(this.contextObject, this.evalContext, this.autoGrowNullReferences);
            PropertyAccessor propertyAccessor = this.ref.cachedReadAccessor;
            if (propertyAccessor instanceof CompilablePropertyAccessor) {
                CompilablePropertyAccessor compilablePropertyAccessor = (CompilablePropertyAccessor) propertyAccessor;
                this.ref.setExitTypeDescriptor(CodeFlow.toDescriptor(compilablePropertyAccessor.getPropertyType()));
            }
            return value;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            this.ref.writeProperty(this.contextObject, this.evalContext, this.ref.name, newValue);
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return this.ref.isWritableProperty(this.ref.name, this.contextObject, this.evalContext);
        }
    }
}
