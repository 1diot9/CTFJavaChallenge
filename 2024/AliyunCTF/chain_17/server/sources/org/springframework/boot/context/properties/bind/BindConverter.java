package org.springframework.boot.context.properties.bind;

import java.beans.PropertyEditor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/BindConverter.class */
public final class BindConverter {
    private static BindConverter sharedInstance;
    private final List<ConversionService> delegates;

    private BindConverter(List<ConversionService> conversionServices, Consumer<PropertyEditorRegistry> propertyEditorInitializer) {
        List<ConversionService> delegates = new ArrayList<>();
        delegates.add(new TypeConverterConversionService(propertyEditorInitializer));
        boolean hasApplication = false;
        if (!CollectionUtils.isEmpty(conversionServices)) {
            for (ConversionService conversionService : conversionServices) {
                delegates.add(conversionService);
                hasApplication = hasApplication || (conversionService instanceof ApplicationConversionService);
            }
        }
        if (!hasApplication) {
            delegates.add(ApplicationConversionService.getSharedInstance());
        }
        this.delegates = Collections.unmodifiableList(delegates);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canConvert(Object source, ResolvableType targetType, Annotation... targetAnnotations) {
        return canConvert(TypeDescriptor.forObject(source), new ResolvableTypeDescriptor(targetType, targetAnnotations));
    }

    private boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
        for (ConversionService service : this.delegates) {
            if (service.canConvert(sourceType, targetType)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T> T convert(Object obj, Bindable<T> bindable) {
        return (T) convert(obj, bindable.getType(), bindable.getAnnotations());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T> T convert(Object obj, ResolvableType resolvableType, Annotation... annotationArr) {
        if (obj == null) {
            return null;
        }
        return (T) convert(obj, TypeDescriptor.forObject(obj), new ResolvableTypeDescriptor(resolvableType, annotationArr));
    }

    private Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        ConversionException failure = null;
        for (ConversionService delegate : this.delegates) {
            try {
            } catch (ConversionException ex) {
                if (failure == null && (ex instanceof ConversionFailedException)) {
                    failure = ex;
                }
            }
            if (delegate.canConvert(sourceType, targetType)) {
                return delegate.convert(source, sourceType, targetType);
            }
            continue;
        }
        if (failure != null) {
            throw failure;
        }
        throw new ConverterNotFoundException(sourceType, targetType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BindConverter get(List<ConversionService> conversionServices, Consumer<PropertyEditorRegistry> propertyEditorInitializer) {
        boolean sharedApplicationConversionService = conversionServices == null || (conversionServices.size() == 1 && conversionServices.get(0) == ApplicationConversionService.getSharedInstance());
        if (propertyEditorInitializer == null && sharedApplicationConversionService) {
            return getSharedInstance();
        }
        return new BindConverter(conversionServices, propertyEditorInitializer);
    }

    private static BindConverter getSharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new BindConverter(null, null);
        }
        return sharedInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/BindConverter$ResolvableTypeDescriptor.class */
    public static class ResolvableTypeDescriptor extends TypeDescriptor {
        ResolvableTypeDescriptor(ResolvableType resolvableType, Annotation[] annotations) {
            super(resolvableType, null, annotations);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/BindConverter$TypeConverterConversionService.class */
    private static class TypeConverterConversionService extends GenericConversionService {
        TypeConverterConversionService(Consumer<PropertyEditorRegistry> initializer) {
            ApplicationConversionService.addDelimitedStringConverters(this);
            addConverter(new TypeConverterConverter(initializer));
        }

        @Override // org.springframework.core.convert.support.GenericConversionService, org.springframework.core.convert.ConversionService
        public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (targetType.isArray() && targetType.getElementTypeDescriptor().isPrimitive()) {
                return false;
            }
            return super.canConvert(sourceType, targetType);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/BindConverter$TypeConverterConverter.class */
    private static class TypeConverterConverter implements ConditionalGenericConverter {
        private static final Set<Class<?>> EXCLUDED_EDITORS;
        private final Consumer<PropertyEditorRegistry> initializer;
        private final SimpleTypeConverter matchesOnlyTypeConverter = createTypeConverter();

        static {
            Set<Class<?>> excluded = new HashSet<>();
            excluded.add(CustomNumberEditor.class);
            excluded.add(CustomBooleanEditor.class);
            excluded.add(FileEditor.class);
            EXCLUDED_EDITORS = Collections.unmodifiableSet(excluded);
        }

        TypeConverterConverter(Consumer<PropertyEditorRegistry> initializer) {
            this.initializer = initializer;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Set.of(new GenericConverter.ConvertiblePair(String.class, Object.class), new GenericConverter.ConvertiblePair(String.class, Resource[].class), new GenericConverter.ConvertiblePair(String.class, Collection.class));
        }

        @Override // org.springframework.core.convert.converter.ConditionalConverter
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            TypeDescriptor elementType;
            Class<?> type = targetType.getType();
            if (type == null || type == Object.class || Map.class.isAssignableFrom(type)) {
                return false;
            }
            if (Collection.class.isAssignableFrom(type) && ((elementType = targetType.getElementTypeDescriptor()) == null || !Resource.class.isAssignableFrom(elementType.getType()))) {
                return false;
            }
            PropertyEditor editor = this.matchesOnlyTypeConverter.getDefaultEditor(type);
            if (editor == null) {
                editor = this.matchesOnlyTypeConverter.findCustomEditor(type, null);
            }
            if (editor == null && String.class != type) {
                editor = BeanUtils.findEditorByConvention(type);
            }
            return (editor == null || EXCLUDED_EDITORS.contains(editor.getClass())) ? false : true;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            return createTypeConverter().convertIfNecessary(source, targetType.getType(), targetType);
        }

        private SimpleTypeConverter createTypeConverter() {
            SimpleTypeConverter typeConverter = new SimpleTypeConverter();
            if (this.initializer != null) {
                this.initializer.accept(typeConverter);
            }
            return typeConverter;
        }
    }
}
