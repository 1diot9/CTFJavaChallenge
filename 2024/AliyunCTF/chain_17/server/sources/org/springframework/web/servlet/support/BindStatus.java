package org.springframework.web.servlet.support;

import java.beans.PropertyEditor;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.util.HtmlUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/support/BindStatus.class */
public class BindStatus {
    private final RequestContext requestContext;
    private final String path;
    private final boolean htmlEscape;

    @Nullable
    private final String expression;

    @Nullable
    private final Errors errors;
    private final String[] errorCodes;

    @Nullable
    private String[] errorMessages;

    @Nullable
    private List<? extends ObjectError> objectErrors;

    @Nullable
    private Object value;

    @Nullable
    private Class<?> valueType;

    @Nullable
    private Object actualValue;

    @Nullable
    private PropertyEditor editor;

    @Nullable
    private BindingResult bindingResult;

    public BindStatus(RequestContext requestContext, String path, boolean htmlEscape) throws IllegalStateException {
        String beanName;
        this.requestContext = requestContext;
        this.path = path;
        this.htmlEscape = htmlEscape;
        int dotPos = path.indexOf(46);
        if (dotPos == -1) {
            beanName = path;
            this.expression = null;
        } else {
            beanName = path.substring(0, dotPos);
            this.expression = path.substring(dotPos + 1);
        }
        this.errors = requestContext.getErrors(beanName, false);
        if (this.errors != null) {
            if (this.expression != null) {
                if ("*".equals(this.expression)) {
                    this.objectErrors = this.errors.getAllErrors();
                } else if (this.expression.endsWith("*")) {
                    this.objectErrors = this.errors.getFieldErrors(this.expression);
                } else {
                    this.objectErrors = this.errors.getFieldErrors(this.expression);
                    this.value = this.errors.getFieldValue(this.expression);
                    this.valueType = this.errors.getFieldType(this.expression);
                    Errors errors = this.errors;
                    if (errors instanceof BindingResult) {
                        BindingResult br = (BindingResult) errors;
                        this.bindingResult = br;
                        this.actualValue = this.bindingResult.getRawFieldValue(this.expression);
                        this.editor = this.bindingResult.findEditor(this.expression, null);
                    } else {
                        this.actualValue = this.value;
                    }
                }
            } else {
                this.objectErrors = this.errors.getGlobalErrors();
            }
            this.errorCodes = initErrorCodes(this.objectErrors);
        } else {
            Object target = requestContext.getModelObject(beanName);
            if (target == null) {
                throw new IllegalStateException("Neither BindingResult nor plain target object for bean name '" + beanName + "' available as request attribute");
            }
            if (this.expression != null && !"*".equals(this.expression) && !this.expression.endsWith("*")) {
                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(target);
                this.value = bw.getPropertyValue(this.expression);
                this.valueType = bw.getPropertyType(this.expression);
                this.actualValue = this.value;
            }
            this.errorCodes = new String[0];
            this.errorMessages = new String[0];
        }
        if (htmlEscape) {
            Object obj = this.value;
            if (obj instanceof String) {
                String text = (String) obj;
                this.value = HtmlUtils.htmlEscape(text);
            }
        }
    }

    private static String[] initErrorCodes(List<? extends ObjectError> objectErrors) {
        String[] errorCodes = new String[objectErrors.size()];
        for (int i = 0; i < objectErrors.size(); i++) {
            ObjectError error = objectErrors.get(i);
            errorCodes[i] = error.getCode();
        }
        return errorCodes;
    }

    public String getPath() {
        return this.path;
    }

    @Nullable
    public String getExpression() {
        return this.expression;
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }

    @Nullable
    public Class<?> getValueType() {
        return this.valueType;
    }

    @Nullable
    public Object getActualValue() {
        return this.actualValue;
    }

    public String getDisplayValue() {
        Object obj = this.value;
        if (obj instanceof String) {
            String displayValue = (String) obj;
            return displayValue;
        }
        if (this.value != null) {
            return this.htmlEscape ? HtmlUtils.htmlEscape(this.value.toString()) : this.value.toString();
        }
        return "";
    }

    public boolean isError() {
        return this.errorCodes.length > 0;
    }

    public String[] getErrorCodes() {
        return this.errorCodes;
    }

    public String getErrorCode() {
        return this.errorCodes.length > 0 ? this.errorCodes[0] : "";
    }

    public String[] getErrorMessages() {
        return initErrorMessages();
    }

    public String getErrorMessage() {
        String[] errorMessages = initErrorMessages();
        return errorMessages.length > 0 ? errorMessages[0] : "";
    }

    public String getErrorMessagesAsString(String delimiter) {
        return StringUtils.arrayToDelimitedString(initErrorMessages(), delimiter);
    }

    private String[] initErrorMessages() throws NoSuchMessageException {
        if (this.errorMessages == null) {
            if (this.objectErrors != null) {
                this.errorMessages = new String[this.objectErrors.size()];
                for (int i = 0; i < this.objectErrors.size(); i++) {
                    ObjectError error = this.objectErrors.get(i);
                    this.errorMessages[i] = this.requestContext.getMessage(error, this.htmlEscape);
                }
            } else {
                this.errorMessages = new String[0];
            }
        }
        return this.errorMessages;
    }

    @Nullable
    public Errors getErrors() {
        return this.errors;
    }

    @Nullable
    public PropertyEditor getEditor() {
        return this.editor;
    }

    @Nullable
    public PropertyEditor findEditor(Class<?> valueClass) {
        if (this.bindingResult != null) {
            return this.bindingResult.findEditor(this.expression, valueClass);
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("BindStatus: ");
        sb.append("expression=[").append(this.expression).append("]; ");
        sb.append("value=[").append(this.value).append(']');
        if (!ObjectUtils.isEmpty((Object[]) this.errorCodes)) {
            sb.append("; errorCodes=").append(Arrays.asList(this.errorCodes));
        }
        return sb.toString();
    }
}
