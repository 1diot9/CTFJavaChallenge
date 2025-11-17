package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AbstractMergedAnnotation.class */
public abstract class AbstractMergedAnnotation<A extends Annotation> implements MergedAnnotation<A> {

    @Nullable
    private volatile A synthesizedAnnotation;

    @Nullable
    protected abstract <T> T getAttributeValue(String attributeName, Class<T> type);

    protected abstract A createSynthesizedAnnotation();

    @Override // org.springframework.core.annotation.MergedAnnotation
    public boolean isDirectlyPresent() {
        return isPresent() && getDistance() == 0;
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public boolean isMetaPresent() {
        return isPresent() && getDistance() > 0;
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public boolean hasNonDefaultValue(String attributeName) {
        return !hasDefaultValue(attributeName);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public byte getByte(String attributeName) {
        return ((Byte) getRequiredAttributeValue(attributeName, Byte.class)).byteValue();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public byte[] getByteArray(String attributeName) {
        return (byte[]) getRequiredAttributeValue(attributeName, byte[].class);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public boolean getBoolean(String attributeName) {
        return ((Boolean) getRequiredAttributeValue(attributeName, Boolean.class)).booleanValue();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public boolean[] getBooleanArray(String attributeName) {
        return (boolean[]) getRequiredAttributeValue(attributeName, boolean[].class);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public char getChar(String attributeName) {
        return ((Character) getRequiredAttributeValue(attributeName, Character.class)).charValue();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public char[] getCharArray(String attributeName) {
        return (char[]) getRequiredAttributeValue(attributeName, char[].class);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public short getShort(String attributeName) {
        return ((Short) getRequiredAttributeValue(attributeName, Short.class)).shortValue();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public short[] getShortArray(String attributeName) {
        return (short[]) getRequiredAttributeValue(attributeName, short[].class);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public int getInt(String attributeName) {
        return ((Integer) getRequiredAttributeValue(attributeName, Integer.class)).intValue();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public int[] getIntArray(String attributeName) {
        return (int[]) getRequiredAttributeValue(attributeName, int[].class);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public long getLong(String attributeName) {
        return ((Long) getRequiredAttributeValue(attributeName, Long.class)).longValue();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public long[] getLongArray(String attributeName) {
        return (long[]) getRequiredAttributeValue(attributeName, long[].class);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public double getDouble(String attributeName) {
        return ((Double) getRequiredAttributeValue(attributeName, Double.class)).doubleValue();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public double[] getDoubleArray(String attributeName) {
        return (double[]) getRequiredAttributeValue(attributeName, double[].class);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public float getFloat(String attributeName) {
        return ((Float) getRequiredAttributeValue(attributeName, Float.class)).floatValue();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public float[] getFloatArray(String attributeName) {
        return (float[]) getRequiredAttributeValue(attributeName, float[].class);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public String getString(String attributeName) {
        return (String) getRequiredAttributeValue(attributeName, String.class);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public String[] getStringArray(String attributeName) {
        return (String[]) getRequiredAttributeValue(attributeName, String[].class);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public Class<?> getClass(String attributeName) {
        return (Class) getRequiredAttributeValue(attributeName, Class.class);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public Class<?>[] getClassArray(String attributeName) {
        return (Class[]) getRequiredAttributeValue(attributeName, Class[].class);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.core.annotation.MergedAnnotation
    public <E extends Enum<E>> E getEnum(String attributeName, Class<E> type) {
        Assert.notNull(type, "Type must not be null");
        return (E) getRequiredAttributeValue(attributeName, type);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public <E extends Enum<E>> E[] getEnumArray(String str, Class<E> cls) {
        Assert.notNull(cls, "Type must not be null");
        return (E[]) ((Enum[]) getRequiredAttributeValue(str, cls.arrayType()));
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public Optional<Object> getValue(String attributeName) {
        return getValue(attributeName, Object.class);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public <T> Optional<T> getValue(String attributeName, Class<T> type) {
        return Optional.ofNullable(getAttributeValue(attributeName, type));
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public Optional<Object> getDefaultValue(String attributeName) {
        return getDefaultValue(attributeName, Object.class);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public MergedAnnotation<A> filterDefaultValues() {
        return filterAttributes(this::hasNonDefaultValue);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public AnnotationAttributes asAnnotationAttributes(MergedAnnotation.Adapt... adaptations) {
        return (AnnotationAttributes) asMap(mergedAnnotation -> {
            return new AnnotationAttributes((Class<? extends Annotation>) mergedAnnotation.getType());
        }, adaptations);
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public Optional<A> synthesize(Predicate<? super MergedAnnotation<A>> condition) throws NoSuchElementException {
        return condition.test(this) ? Optional.of(synthesize()) : Optional.empty();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public A synthesize() {
        if (!isPresent()) {
            throw new NoSuchElementException("Unable to synthesize missing annotation");
        }
        A synthesized = this.synthesizedAnnotation;
        if (synthesized == null) {
            synthesized = createSynthesizedAnnotation();
            this.synthesizedAnnotation = synthesized;
        }
        return synthesized;
    }

    private <T> T getRequiredAttributeValue(String str, Class<T> cls) {
        T t = (T) getAttributeValue(str, cls);
        if (t == null) {
            throw new NoSuchElementException("No attribute named '" + str + "' present in merged annotation " + getType().getName());
        }
        return t;
    }
}
