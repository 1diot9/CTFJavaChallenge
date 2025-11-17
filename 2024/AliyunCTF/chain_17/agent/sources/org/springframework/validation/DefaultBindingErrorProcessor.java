package org.springframework.validation;

import org.springframework.beans.PropertyAccessException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/DefaultBindingErrorProcessor.class */
public class DefaultBindingErrorProcessor implements BindingErrorProcessor {
    public static final String MISSING_FIELD_ERROR_CODE = "required";

    @Override // org.springframework.validation.BindingErrorProcessor
    public void processMissingFieldError(String missingField, BindingResult bindingResult) {
        String fixedField = bindingResult.getNestedPath() + missingField;
        String[] codes = bindingResult.resolveMessageCodes(MISSING_FIELD_ERROR_CODE, missingField);
        Object[] arguments = getArgumentsForBindError(bindingResult.getObjectName(), fixedField);
        FieldError error = new FieldError(bindingResult.getObjectName(), fixedField, "", true, codes, arguments, "Field '" + fixedField + "' is required");
        bindingResult.addError(error);
    }

    @Override // org.springframework.validation.BindingErrorProcessor
    public void processPropertyAccessException(PropertyAccessException ex, BindingResult bindingResult) {
        String field = ex.getPropertyName();
        Assert.state(field != null, "No field in exception");
        String[] codes = bindingResult.resolveMessageCodes(ex.getErrorCode(), field);
        Object[] arguments = getArgumentsForBindError(bindingResult.getObjectName(), field);
        Object rejectedValue = ex.getValue();
        if (ObjectUtils.isArray(rejectedValue)) {
            rejectedValue = StringUtils.arrayToCommaDelimitedString(ObjectUtils.toObjectArray(rejectedValue));
        }
        FieldError error = new FieldError(bindingResult.getObjectName(), field, rejectedValue, true, codes, arguments, ex.getLocalizedMessage());
        error.wrap(ex);
        bindingResult.addError(error);
    }

    protected Object[] getArgumentsForBindError(String objectName, String field) {
        String[] codes = {objectName + "." + field, field};
        return new Object[]{new DefaultMessageSourceResolvable(codes, field)};
    }
}
