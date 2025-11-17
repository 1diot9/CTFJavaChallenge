package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotation.class */
public interface MergedAnnotation<A extends Annotation> {
    public static final String VALUE = "value";

    Class<A> getType();

    boolean isPresent();

    boolean isDirectlyPresent();

    boolean isMetaPresent();

    int getDistance();

    int getAggregateIndex();

    @Nullable
    Object getSource();

    @Nullable
    MergedAnnotation<?> getMetaSource();

    MergedAnnotation<?> getRoot();

    List<Class<? extends Annotation>> getMetaTypes();

    boolean hasNonDefaultValue(String attributeName);

    boolean hasDefaultValue(String attributeName) throws NoSuchElementException;

    byte getByte(String attributeName) throws NoSuchElementException;

    byte[] getByteArray(String attributeName) throws NoSuchElementException;

    boolean getBoolean(String attributeName) throws NoSuchElementException;

    boolean[] getBooleanArray(String attributeName) throws NoSuchElementException;

    char getChar(String attributeName) throws NoSuchElementException;

    char[] getCharArray(String attributeName) throws NoSuchElementException;

    short getShort(String attributeName) throws NoSuchElementException;

    short[] getShortArray(String attributeName) throws NoSuchElementException;

    int getInt(String attributeName) throws NoSuchElementException;

    int[] getIntArray(String attributeName) throws NoSuchElementException;

    long getLong(String attributeName) throws NoSuchElementException;

    long[] getLongArray(String attributeName) throws NoSuchElementException;

    double getDouble(String attributeName) throws NoSuchElementException;

    double[] getDoubleArray(String attributeName) throws NoSuchElementException;

    float getFloat(String attributeName) throws NoSuchElementException;

    float[] getFloatArray(String attributeName) throws NoSuchElementException;

    String getString(String attributeName) throws NoSuchElementException;

    String[] getStringArray(String attributeName) throws NoSuchElementException;

    Class<?> getClass(String attributeName) throws NoSuchElementException;

    Class<?>[] getClassArray(String attributeName) throws NoSuchElementException;

    <E extends Enum<E>> E getEnum(String attributeName, Class<E> type) throws NoSuchElementException;

    <E extends Enum<E>> E[] getEnumArray(String attributeName, Class<E> type) throws NoSuchElementException;

    <T extends Annotation> MergedAnnotation<T> getAnnotation(String attributeName, Class<T> type) throws NoSuchElementException;

    <T extends Annotation> MergedAnnotation<T>[] getAnnotationArray(String attributeName, Class<T> type) throws NoSuchElementException;

    Optional<Object> getValue(String attributeName);

    <T> Optional<T> getValue(String attributeName, Class<T> type);

    Optional<Object> getDefaultValue(String attributeName);

    <T> Optional<T> getDefaultValue(String attributeName, Class<T> type);

    MergedAnnotation<A> filterDefaultValues();

    MergedAnnotation<A> filterAttributes(Predicate<String> predicate);

    MergedAnnotation<A> withNonMergedAttributes();

    AnnotationAttributes asAnnotationAttributes(Adapt... adaptations);

    Map<String, Object> asMap(Adapt... adaptations);

    <T extends Map<String, Object>> T asMap(Function<MergedAnnotation<?>, T> factory, Adapt... adaptations);

    A synthesize() throws NoSuchElementException;

    Optional<A> synthesize(Predicate<? super MergedAnnotation<A>> condition) throws NoSuchElementException;

    static <A extends Annotation> MergedAnnotation<A> missing() {
        return MissingMergedAnnotation.getInstance();
    }

    static <A extends Annotation> MergedAnnotation<A> from(A annotation) {
        return from(null, annotation);
    }

    static <A extends Annotation> MergedAnnotation<A> from(@Nullable Object source, A annotation) {
        return TypeMappedAnnotation.from(source, annotation);
    }

    static <A extends Annotation> MergedAnnotation<A> of(Class<A> annotationType) {
        return of(null, annotationType, null);
    }

    static <A extends Annotation> MergedAnnotation<A> of(Class<A> annotationType, @Nullable Map<String, ?> attributes) {
        return of(null, annotationType, attributes);
    }

    static <A extends Annotation> MergedAnnotation<A> of(@Nullable AnnotatedElement source, Class<A> annotationType, @Nullable Map<String, ?> attributes) {
        return of(null, source, annotationType, attributes);
    }

    static <A extends Annotation> MergedAnnotation<A> of(@Nullable ClassLoader classLoader, @Nullable Object source, Class<A> annotationType, @Nullable Map<String, ?> attributes) {
        return TypeMappedAnnotation.of(classLoader, source, annotationType, attributes);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotation$Adapt.class */
    public enum Adapt {
        CLASS_TO_STRING,
        ANNOTATION_TO_MAP;

        /* JADX INFO: Access modifiers changed from: protected */
        public final boolean isIn(Adapt... adaptations) {
            for (Adapt candidate : adaptations) {
                if (candidate == this) {
                    return true;
                }
            }
            return false;
        }

        public static Adapt[] values(boolean classToString, boolean annotationsToMap) {
            EnumSet<Adapt> result = EnumSet.noneOf(Adapt.class);
            addIfTrue(result, CLASS_TO_STRING, classToString);
            addIfTrue(result, ANNOTATION_TO_MAP, annotationsToMap);
            return (Adapt[]) result.toArray(new Adapt[0]);
        }

        private static <T> void addIfTrue(Set<T> result, T value, boolean test) {
            if (test) {
                result.add(value);
            }
        }
    }
}
