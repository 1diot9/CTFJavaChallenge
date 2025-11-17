package org.springframework.boot.context.properties.bind.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginProvider;
import org.springframework.util.Assert;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/validation/ValidationErrors.class */
public class ValidationErrors implements Iterable<ObjectError> {
    private final ConfigurationPropertyName name;
    private final Set<ConfigurationProperty> boundProperties;
    private final List<ObjectError> errors;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValidationErrors(ConfigurationPropertyName name, Set<ConfigurationProperty> boundProperties, List<ObjectError> errors) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(boundProperties, "BoundProperties must not be null");
        Assert.notNull(errors, "Errors must not be null");
        this.name = name;
        this.boundProperties = Collections.unmodifiableSet(boundProperties);
        this.errors = convertErrors(name, boundProperties, errors);
    }

    private List<ObjectError> convertErrors(ConfigurationPropertyName name, Set<ConfigurationProperty> boundProperties, List<ObjectError> errors) {
        List<ObjectError> converted = new ArrayList<>(errors.size());
        for (ObjectError error : errors) {
            converted.add(convertError(name, boundProperties, error));
        }
        return Collections.unmodifiableList(converted);
    }

    private ObjectError convertError(ConfigurationPropertyName name, Set<ConfigurationProperty> boundProperties, ObjectError error) {
        if (error instanceof FieldError) {
            FieldError fieldError = (FieldError) error;
            return convertFieldError(name, boundProperties, fieldError);
        }
        return error;
    }

    private FieldError convertFieldError(ConfigurationPropertyName name, Set<ConfigurationProperty> boundProperties, FieldError error) {
        if (error instanceof OriginProvider) {
            return error;
        }
        return OriginTrackedFieldError.of(error, findFieldErrorOrigin(name, boundProperties, error));
    }

    private Origin findFieldErrorOrigin(ConfigurationPropertyName name, Set<ConfigurationProperty> boundProperties, FieldError error) {
        for (ConfigurationProperty boundProperty : boundProperties) {
            if (isForError(name, boundProperty.getName(), error)) {
                return Origin.from(boundProperty);
            }
        }
        return null;
    }

    private boolean isForError(ConfigurationPropertyName name, ConfigurationPropertyName boundPropertyName, FieldError error) {
        return name.isParentOf(boundPropertyName) && boundPropertyName.getLastElement(ConfigurationPropertyName.Form.UNIFORM).equalsIgnoreCase(error.getField());
    }

    public ConfigurationPropertyName getName() {
        return this.name;
    }

    public Set<ConfigurationProperty> getBoundProperties() {
        return this.boundProperties;
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public List<ObjectError> getAllErrors() {
        return this.errors;
    }

    @Override // java.lang.Iterable
    public Iterator<ObjectError> iterator() {
        return this.errors.iterator();
    }
}
