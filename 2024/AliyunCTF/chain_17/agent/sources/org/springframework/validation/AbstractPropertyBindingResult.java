package org.springframework.validation;

import java.beans.PropertyEditor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.ConvertingPropertyEditorAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/AbstractPropertyBindingResult.class */
public abstract class AbstractPropertyBindingResult extends AbstractBindingResult {

    @Nullable
    private transient ConversionService conversionService;

    public abstract ConfigurablePropertyAccessor getPropertyAccessor();

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractPropertyBindingResult(String objectName) {
        super(objectName);
    }

    public void initConversion(ConversionService conversionService) {
        Assert.notNull(conversionService, "ConversionService must not be null");
        this.conversionService = conversionService;
        if (getTarget() != null) {
            getPropertyAccessor().setConversionService(conversionService);
        }
    }

    @Override // org.springframework.validation.AbstractBindingResult, org.springframework.validation.BindingResult
    public PropertyEditorRegistry getPropertyEditorRegistry() {
        if (getTarget() != null) {
            return getPropertyAccessor();
        }
        return null;
    }

    @Override // org.springframework.validation.AbstractErrors
    protected String canonicalFieldName(String field) {
        return PropertyAccessorUtils.canonicalPropertyName(field);
    }

    @Override // org.springframework.validation.AbstractBindingResult, org.springframework.validation.Errors
    @Nullable
    public Class<?> getFieldType(@Nullable String field) {
        return getTarget() != null ? getPropertyAccessor().getPropertyType(fixedField(field)) : super.getFieldType(field);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.validation.AbstractBindingResult
    @Nullable
    public Object getActualFieldValue(String field) {
        return getPropertyAccessor().getPropertyValue(field);
    }

    @Override // org.springframework.validation.AbstractBindingResult
    protected Object formatFieldValue(String field, @Nullable Object value) {
        String fixedField = fixedField(field);
        PropertyEditor customEditor = getCustomEditor(fixedField);
        if (customEditor != null) {
            customEditor.setValue(value);
            String textValue = customEditor.getAsText();
            if (textValue != null) {
                return textValue;
            }
        }
        if (this.conversionService != null) {
            TypeDescriptor fieldDesc = getPropertyAccessor().getPropertyTypeDescriptor(fixedField);
            TypeDescriptor strDesc = TypeDescriptor.valueOf(String.class);
            if (fieldDesc != null && this.conversionService.canConvert(fieldDesc, strDesc)) {
                return this.conversionService.convert(value, fieldDesc, strDesc);
            }
        }
        return value;
    }

    @Nullable
    protected PropertyEditor getCustomEditor(String fixedField) {
        Class<?> targetType = getPropertyAccessor().getPropertyType(fixedField);
        PropertyEditor editor = getPropertyAccessor().findCustomEditor(targetType, fixedField);
        if (editor == null) {
            editor = BeanUtils.findEditorByConvention(targetType);
        }
        return editor;
    }

    @Override // org.springframework.validation.AbstractBindingResult, org.springframework.validation.BindingResult
    @Nullable
    public PropertyEditor findEditor(@Nullable String field, @Nullable Class<?> valueType) {
        TypeDescriptor ptd;
        Class<?> valueTypeForLookup = valueType;
        if (valueTypeForLookup == null) {
            valueTypeForLookup = getFieldType(field);
        }
        ConvertingPropertyEditorAdapter findEditor = super.findEditor(field, valueTypeForLookup);
        if (findEditor == null && this.conversionService != null) {
            TypeDescriptor td = null;
            if (field != null && getTarget() != null && (ptd = getPropertyAccessor().getPropertyTypeDescriptor(fixedField(field))) != null && (valueType == null || valueType.isAssignableFrom(ptd.getType()))) {
                td = ptd;
            }
            if (td == null) {
                td = TypeDescriptor.valueOf(valueTypeForLookup);
            }
            if (this.conversionService.canConvert(TypeDescriptor.valueOf(String.class), td)) {
                findEditor = new ConvertingPropertyEditorAdapter(this.conversionService, td);
            }
        }
        return findEditor;
    }
}
