package cn.hutool.extra.validation;

import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/validation/BeanValidationResult.class */
public class BeanValidationResult {
    private boolean success;
    private List<ErrorMessage> errorMessages = new ArrayList();

    public BeanValidationResult(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public BeanValidationResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public List<ErrorMessage> getErrorMessages() {
        return this.errorMessages;
    }

    public BeanValidationResult setErrorMessages(List<ErrorMessage> errorMessages) {
        this.errorMessages = errorMessages;
        return this;
    }

    public BeanValidationResult addErrorMessage(ErrorMessage errorMessage) {
        this.errorMessages.add(errorMessage);
        return this;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/validation/BeanValidationResult$ErrorMessage.class */
    public static class ErrorMessage {
        private String propertyName;
        private String message;
        private Object value;

        public String getPropertyName() {
            return this.propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String toString() {
            return "ErrorMessage{propertyName='" + this.propertyName + "', message='" + this.message + "', value=" + this.value + '}';
        }
    }
}
