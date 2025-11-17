package org.springframework.boot.context.properties.bind.validation;

import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginProvider;
import org.springframework.validation.FieldError;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/validation/OriginTrackedFieldError.class */
final class OriginTrackedFieldError extends FieldError implements OriginProvider {
    private final Origin origin;

    private OriginTrackedFieldError(FieldError fieldError, Origin origin) {
        super(fieldError.getObjectName(), fieldError.getField(), fieldError.getRejectedValue(), fieldError.isBindingFailure(), fieldError.getCodes(), fieldError.getArguments(), fieldError.getDefaultMessage());
        this.origin = origin;
    }

    @Override // org.springframework.boot.origin.OriginProvider
    public Origin getOrigin() {
        return this.origin;
    }

    @Override // org.springframework.validation.FieldError, org.springframework.validation.ObjectError, org.springframework.context.support.DefaultMessageSourceResolvable
    public String toString() {
        if (this.origin == null) {
            return super.toString();
        }
        return super.toString() + "; origin " + this.origin;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static FieldError of(FieldError fieldError, Origin origin) {
        if (fieldError == null || origin == null) {
            return fieldError;
        }
        return new OriginTrackedFieldError(fieldError, origin);
    }
}
