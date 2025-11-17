package org.springframework.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/ValidationUtils.class */
public abstract class ValidationUtils {
    private static final Log logger = LogFactory.getLog((Class<?>) ValidationUtils.class);

    public static void invokeValidator(Validator validator, Object target, Errors errors) {
        invokeValidator(validator, target, errors, (Object[]) null);
    }

    public static void invokeValidator(Validator validator, Object target, Errors errors, @Nullable Object... validationHints) {
        Assert.notNull(validator, "Validator must not be null");
        Assert.notNull(target, "Target object must not be null");
        Assert.notNull(errors, "Errors object must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Invoking validator [" + validator + "]");
        }
        if (!validator.supports(target.getClass())) {
            throw new IllegalArgumentException("Validator [" + validator.getClass() + "] does not support [" + target.getClass() + "]");
        }
        if (!ObjectUtils.isEmpty(validationHints) && (validator instanceof SmartValidator)) {
            SmartValidator smartValidator = (SmartValidator) validator;
            smartValidator.validate(target, errors, validationHints);
        } else {
            validator.validate(target, errors);
        }
        if (logger.isDebugEnabled()) {
            if (errors.hasErrors()) {
                logger.debug("Validator found " + errors.getErrorCount() + " errors");
            } else {
                logger.debug("Validator found no errors");
            }
        }
    }

    public static void rejectIfEmpty(Errors errors, String field, String errorCode) {
        rejectIfEmpty(errors, field, errorCode, null, null);
    }

    public static void rejectIfEmpty(Errors errors, String field, String errorCode, String defaultMessage) {
        rejectIfEmpty(errors, field, errorCode, null, defaultMessage);
    }

    public static void rejectIfEmpty(Errors errors, String field, String errorCode, Object[] errorArgs) {
        rejectIfEmpty(errors, field, errorCode, errorArgs, null);
    }

    public static void rejectIfEmpty(Errors errors, String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        Assert.notNull(errors, "Errors object must not be null");
        Object value = errors.getFieldValue(field);
        if (value == null || !StringUtils.hasLength(value.toString())) {
            errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
        }
    }

    public static void rejectIfEmptyOrWhitespace(Errors errors, String field, String errorCode) {
        rejectIfEmptyOrWhitespace(errors, field, errorCode, null, null);
    }

    public static void rejectIfEmptyOrWhitespace(Errors errors, String field, String errorCode, String defaultMessage) {
        rejectIfEmptyOrWhitespace(errors, field, errorCode, null, defaultMessage);
    }

    public static void rejectIfEmptyOrWhitespace(Errors errors, String field, String errorCode, @Nullable Object[] errorArgs) {
        rejectIfEmptyOrWhitespace(errors, field, errorCode, errorArgs, null);
    }

    public static void rejectIfEmptyOrWhitespace(Errors errors, String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        Assert.notNull(errors, "Errors object must not be null");
        Object value = errors.getFieldValue(field);
        if (value == null || !StringUtils.hasText(value.toString())) {
            errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
        }
    }
}
