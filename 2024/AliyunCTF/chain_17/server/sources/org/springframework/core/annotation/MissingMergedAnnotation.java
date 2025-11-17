package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MissingMergedAnnotation.class */
final class MissingMergedAnnotation<A extends Annotation> extends AbstractMergedAnnotation<A> {
    private static final MissingMergedAnnotation<?> INSTANCE = new MissingMergedAnnotation<>();

    private MissingMergedAnnotation() {
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public Class<A> getType() {
        throw new NoSuchElementException("Unable to get type for missing annotation");
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public boolean isPresent() {
        return false;
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    @Nullable
    public Object getSource() {
        return null;
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    @Nullable
    public MergedAnnotation<?> getMetaSource() {
        return null;
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public MergedAnnotation<?> getRoot() {
        return this;
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public List<Class<? extends Annotation>> getMetaTypes() {
        return Collections.emptyList();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public int getDistance() {
        return -1;
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public int getAggregateIndex() {
        return -1;
    }

    @Override // org.springframework.core.annotation.AbstractMergedAnnotation, org.springframework.core.annotation.MergedAnnotation
    public boolean hasNonDefaultValue(String attributeName) {
        throw new NoSuchElementException("Unable to check non-default value for missing annotation");
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public boolean hasDefaultValue(String attributeName) {
        throw new NoSuchElementException("Unable to check default value for missing annotation");
    }

    @Override // org.springframework.core.annotation.AbstractMergedAnnotation, org.springframework.core.annotation.MergedAnnotation
    public <T> Optional<T> getValue(String attributeName, Class<T> type) {
        return Optional.empty();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public <T> Optional<T> getDefaultValue(@Nullable String attributeName, Class<T> type) {
        return Optional.empty();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public MergedAnnotation<A> filterAttributes(Predicate<String> predicate) {
        return this;
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public MergedAnnotation<A> withNonMergedAttributes() {
        return this;
    }

    @Override // org.springframework.core.annotation.AbstractMergedAnnotation, org.springframework.core.annotation.MergedAnnotation
    public AnnotationAttributes asAnnotationAttributes(MergedAnnotation.Adapt... adaptations) {
        return new AnnotationAttributes();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public Map<String, Object> asMap(MergedAnnotation.Adapt... adaptations) {
        return Collections.emptyMap();
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public <T extends Map<String, Object>> T asMap(Function<MergedAnnotation<?>, T> factory, MergedAnnotation.Adapt... adaptations) {
        return factory.apply(this);
    }

    public String toString() {
        return "(missing)";
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public <T extends Annotation> MergedAnnotation<T> getAnnotation(String attributeName, Class<T> type) throws NoSuchElementException {
        throw new NoSuchElementException("Unable to get attribute value for missing annotation");
    }

    @Override // org.springframework.core.annotation.MergedAnnotation
    public <T extends Annotation> MergedAnnotation<T>[] getAnnotationArray(String attributeName, Class<T> type) throws NoSuchElementException {
        throw new NoSuchElementException("Unable to get attribute value for missing annotation");
    }

    @Override // org.springframework.core.annotation.AbstractMergedAnnotation
    protected <T> T getAttributeValue(String attributeName, Class<T> type) {
        throw new NoSuchElementException("Unable to get attribute value for missing annotation");
    }

    @Override // org.springframework.core.annotation.AbstractMergedAnnotation
    protected A createSynthesizedAnnotation() {
        throw new NoSuchElementException("Unable to synthesize missing annotation");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <A extends Annotation> MergedAnnotation<A> getInstance() {
        return INSTANCE;
    }
}
