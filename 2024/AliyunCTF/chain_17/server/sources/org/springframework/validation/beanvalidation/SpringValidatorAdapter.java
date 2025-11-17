package org.springframework.validation.beanvalidation;

import ch.qos.logback.classic.encoder.JsonEncoder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.BeanDescriptor;
import jakarta.validation.metadata.ConstraintDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.SmartValidator;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/SpringValidatorAdapter.class */
public class SpringValidatorAdapter implements SmartValidator, Validator {
    private static final Set<String> internalAnnotationAttributes = Set.of(JsonEncoder.MESSAGE_ATTR_NAME, "groups", "payload");

    @Nullable
    private Validator targetValidator;

    public SpringValidatorAdapter(Validator targetValidator) {
        Assert.notNull(targetValidator, "Target Validator must not be null");
        this.targetValidator = targetValidator;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringValidatorAdapter() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTargetValidator(Validator targetValidator) {
        this.targetValidator = targetValidator;
    }

    @Override // org.springframework.validation.Validator
    public boolean supports(Class<?> clazz) {
        return this.targetValidator != null;
    }

    @Override // org.springframework.validation.Validator
    public void validate(Object target, Errors errors) {
        if (this.targetValidator != null) {
            processConstraintViolations(this.targetValidator.validate(target, new Class[0]), errors);
        }
    }

    @Override // org.springframework.validation.SmartValidator
    public void validate(Object target, Errors errors, Object... validationHints) {
        if (this.targetValidator != null) {
            processConstraintViolations(this.targetValidator.validate(target, asValidationGroups(validationHints)), errors);
        }
    }

    @Override // org.springframework.validation.SmartValidator
    public void validateValue(Class<?> targetType, String fieldName, @Nullable Object value, Errors errors, Object... validationHints) {
        if (this.targetValidator != null) {
            processConstraintViolations(this.targetValidator.validateValue(targetType, fieldName, value, asValidationGroups(validationHints)), errors);
        }
    }

    private Class<?>[] asValidationGroups(Object... validationHints) {
        Set<Class<?>> groups = new LinkedHashSet<>(4);
        for (Object hint : validationHints) {
            if (hint instanceof Class) {
                Class<?> clazz = (Class) hint;
                groups.add(clazz);
            }
        }
        return ClassUtils.toClassArray(groups);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void processConstraintViolations(Set<ConstraintViolation<Object>> violations, Errors errors) {
        for (ConstraintViolation<Object> violation : violations) {
            String field = determineField(violation);
            FieldError fieldError = errors.getFieldError(field);
            if (fieldError == null || !fieldError.isBindingFailure()) {
                try {
                    ConstraintDescriptor<?> cd = violation.getConstraintDescriptor();
                    String errorCode = determineErrorCode(cd);
                    Object[] errorArgs = getArgumentsForConstraint(errors.getObjectName(), field, cd);
                    if (errors instanceof BindingResult) {
                        BindingResult bindingResult = (BindingResult) errors;
                        String nestedField = bindingResult.getNestedPath() + field;
                        if (nestedField.isEmpty()) {
                            String[] errorCodes = bindingResult.resolveMessageCodes(errorCode);
                            ObjectError error = new ViolationObjectError(errors.getObjectName(), errorCodes, errorArgs, violation, this);
                            bindingResult.addError(error);
                        } else {
                            Object rejectedValue = getRejectedValue(field, violation, bindingResult);
                            String[] errorCodes2 = bindingResult.resolveMessageCodes(errorCode, field);
                            FieldError error2 = new ViolationFieldError(errors.getObjectName(), nestedField, rejectedValue, errorCodes2, errorArgs, violation, this);
                            bindingResult.addError(error2);
                        }
                    } else {
                        errors.rejectValue(field, errorCode, errorArgs, violation.getMessage());
                    }
                } catch (NotReadablePropertyException ex) {
                    throw new IllegalStateException("JSR-303 validated property '" + field + "' does not have a corresponding accessor for Spring data binding - check your DataBinder's configuration (bean property versus direct field access)", ex);
                }
            }
        }
    }

    protected String determineField(ConstraintViolation<Object> violation) {
        Path<Path.Node> path = violation.getPropertyPath();
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Path.Node node : path) {
            if (node.isInIterable() && !first) {
                sb.append('[');
                Object index = node.getIndex();
                if (index == null) {
                    index = node.getKey();
                }
                if (index != null) {
                    sb.append(index);
                }
                sb.append(']');
            }
            String name = node.getName();
            if (name != null && node.getKind() == ElementKind.PROPERTY && !name.startsWith("<")) {
                if (!first) {
                    sb.append('.');
                }
                first = false;
                sb.append(name);
            }
        }
        return sb.toString();
    }

    protected String determineErrorCode(ConstraintDescriptor<?> descriptor) {
        return descriptor.getAnnotation().annotationType().getSimpleName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object[] getArgumentsForConstraint(String objectName, String field, ConstraintDescriptor<?> descriptor) {
        List<Object> arguments = new ArrayList<>();
        arguments.add(getResolvableField(objectName, field));
        Map<String, Object> attributesToExpose = new TreeMap<>();
        descriptor.getAttributes().forEach((attributeName, attributeValue) -> {
            if (!internalAnnotationAttributes.contains(attributeName)) {
                if (attributeValue instanceof String) {
                    String str = (String) attributeValue;
                    attributeValue = new ResolvableAttribute(str);
                }
                attributesToExpose.put(attributeName, attributeValue);
            }
        });
        arguments.addAll(attributesToExpose.values());
        return arguments.toArray();
    }

    protected MessageSourceResolvable getResolvableField(String objectName, String field) {
        String[] strArr;
        if (StringUtils.hasText(field)) {
            strArr = new String[]{objectName + "." + field, field};
        } else {
            strArr = new String[]{objectName};
        }
        String[] codes = strArr;
        return new DefaultMessageSourceResolvable(codes, field);
    }

    @Nullable
    protected Object getRejectedValue(String field, ConstraintViolation<Object> violation, BindingResult bindingResult) {
        Object invalidValue = violation.getInvalidValue();
        if (!field.isEmpty() && !field.contains(ClassUtils.ARRAY_SUFFIX) && (invalidValue == violation.getLeafBean() || field.contains(PropertyAccessor.PROPERTY_KEY_PREFIX) || field.contains("."))) {
            try {
                invalidValue = bindingResult.getRawFieldValue(field);
            } catch (InvalidPropertyException e) {
            }
        }
        return invalidValue;
    }

    protected boolean requiresMessageFormat(ConstraintViolation<?> violation) {
        return containsSpringStylePlaceholder(violation.getMessage());
    }

    private static boolean containsSpringStylePlaceholder(@Nullable String message) {
        return message != null && message.contains("{0}");
    }

    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        Assert.state(this.targetValidator != null, "No target Validator set");
        return this.targetValidator.validate(object, groups);
    }

    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        Assert.state(this.targetValidator != null, "No target Validator set");
        return this.targetValidator.validateProperty(object, propertyName, groups);
    }

    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        Assert.state(this.targetValidator != null, "No target Validator set");
        return this.targetValidator.validateValue(beanType, propertyName, value, groups);
    }

    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        Assert.state(this.targetValidator != null, "No target Validator set");
        return this.targetValidator.getConstraintsForClass(clazz);
    }

    @Override // org.springframework.validation.SmartValidator
    public <T> T unwrap(@Nullable Class<T> cls) {
        Assert.state(this.targetValidator != null, "No target Validator set");
        try {
            return cls != null ? (T) this.targetValidator.unwrap(cls) : (T) this.targetValidator;
        } catch (ValidationException e) {
            if (Validator.class == cls) {
                return (T) this.targetValidator;
            }
            throw e;
        }
    }

    public ExecutableValidator forExecutables() {
        Assert.state(this.targetValidator != null, "No target Validator set");
        return this.targetValidator.forExecutables();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/SpringValidatorAdapter$ResolvableAttribute.class */
    public static class ResolvableAttribute implements MessageSourceResolvable, Serializable {
        private final String resolvableString;

        public ResolvableAttribute(String resolvableString) {
            this.resolvableString = resolvableString;
        }

        @Override // org.springframework.context.MessageSourceResolvable
        public String[] getCodes() {
            return new String[]{this.resolvableString};
        }

        @Override // org.springframework.context.MessageSourceResolvable
        @Nullable
        public Object[] getArguments() {
            return null;
        }

        @Override // org.springframework.context.MessageSourceResolvable
        public String getDefaultMessage() {
            return this.resolvableString;
        }

        public String toString() {
            return this.resolvableString;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/SpringValidatorAdapter$ViolationObjectError.class */
    public static class ViolationObjectError extends ObjectError implements Serializable {

        @Nullable
        private transient SpringValidatorAdapter adapter;

        @Nullable
        private transient ConstraintViolation<?> violation;

        public ViolationObjectError(String objectName, String[] codes, Object[] arguments, ConstraintViolation<?> violation, SpringValidatorAdapter adapter) {
            super(objectName, codes, arguments, violation.getMessage());
            this.adapter = adapter;
            this.violation = violation;
            wrap(violation);
        }

        @Override // org.springframework.context.support.DefaultMessageSourceResolvable
        public boolean shouldRenderDefaultMessage() {
            if (this.adapter != null && this.violation != null) {
                return this.adapter.requiresMessageFormat(this.violation);
            }
            return SpringValidatorAdapter.containsSpringStylePlaceholder(getDefaultMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/SpringValidatorAdapter$ViolationFieldError.class */
    public static class ViolationFieldError extends FieldError implements Serializable {

        @Nullable
        private transient SpringValidatorAdapter adapter;

        @Nullable
        private transient ConstraintViolation<?> violation;

        public ViolationFieldError(String objectName, String field, @Nullable Object rejectedValue, String[] codes, Object[] arguments, ConstraintViolation<?> violation, SpringValidatorAdapter adapter) {
            super(objectName, field, rejectedValue, false, codes, arguments, violation.getMessage());
            this.adapter = adapter;
            this.violation = violation;
            wrap(violation);
        }

        @Override // org.springframework.context.support.DefaultMessageSourceResolvable
        public boolean shouldRenderDefaultMessage() {
            if (this.adapter != null && this.violation != null) {
                return this.adapter.requiresMessageFormat(this.violation);
            }
            return SpringValidatorAdapter.containsSpringStylePlaceholder(getDefaultMessage());
        }
    }
}
