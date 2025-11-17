package org.springframework.validation;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/Errors.class */
public interface Errors {
    public static final String NESTED_PATH_SEPARATOR = ".";

    String getObjectName();

    void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);

    void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);

    List<ObjectError> getGlobalErrors();

    List<FieldError> getFieldErrors();

    @Nullable
    Object getFieldValue(String field);

    String toString();

    default void setNestedPath(String nestedPath) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support nested paths");
    }

    default String getNestedPath() {
        return "";
    }

    default void pushNestedPath(String subPath) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support nested paths");
    }

    default void popNestedPath() throws IllegalStateException {
        throw new IllegalStateException("Cannot pop nested path: no nested path on stack");
    }

    default void reject(String errorCode) {
        reject(errorCode, null, null);
    }

    default void reject(String errorCode, String defaultMessage) {
        reject(errorCode, null, defaultMessage);
    }

    default void rejectValue(@Nullable String field, String errorCode) {
        rejectValue(field, errorCode, null, null);
    }

    default void rejectValue(@Nullable String field, String errorCode, String defaultMessage) {
        rejectValue(field, errorCode, null, defaultMessage);
    }

    default void addAllErrors(Errors errors) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support addAllErrors");
    }

    default <T extends Throwable> void failOnError(Function<String, T> messageToException) throws Throwable {
        if (hasErrors()) {
            throw messageToException.apply(toString());
        }
    }

    default boolean hasErrors() {
        return (getGlobalErrors().isEmpty() && getFieldErrors().isEmpty()) ? false : true;
    }

    default int getErrorCount() {
        return getGlobalErrors().size() + getFieldErrors().size();
    }

    default List<ObjectError> getAllErrors() {
        return Stream.concat(getGlobalErrors().stream(), getFieldErrors().stream()).toList();
    }

    default boolean hasGlobalErrors() {
        return !getGlobalErrors().isEmpty();
    }

    default int getGlobalErrorCount() {
        return getGlobalErrors().size();
    }

    @Nullable
    default ObjectError getGlobalError() {
        return getGlobalErrors().stream().findFirst().orElse(null);
    }

    default boolean hasFieldErrors() {
        return !getFieldErrors().isEmpty();
    }

    default int getFieldErrorCount() {
        return getFieldErrors().size();
    }

    @Nullable
    default FieldError getFieldError() {
        return getFieldErrors().stream().findFirst().orElse(null);
    }

    default boolean hasFieldErrors(String field) {
        return getFieldError(field) != null;
    }

    default int getFieldErrorCount(String field) {
        return getFieldErrors(field).size();
    }

    default List<FieldError> getFieldErrors(String field) {
        return getFieldErrors().stream().filter(error -> {
            return field.equals(error.getField());
        }).toList();
    }

    @Nullable
    default FieldError getFieldError(String field) {
        return getFieldErrors().stream().filter(error -> {
            return field.equals(error.getField());
        }).findFirst().orElse(null);
    }

    @Nullable
    default Class<?> getFieldType(String field) {
        return (Class) Optional.ofNullable(getFieldValue(field)).map((v0) -> {
            return v0.getClass();
        }).orElse(null);
    }
}
