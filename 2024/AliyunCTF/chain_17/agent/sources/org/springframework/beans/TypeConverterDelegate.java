package org.springframework.beans;

import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/TypeConverterDelegate.class */
public class TypeConverterDelegate {
    private static final Log logger = LogFactory.getLog((Class<?>) TypeConverterDelegate.class);
    private final PropertyEditorRegistrySupport propertyEditorRegistry;

    @Nullable
    private final Object targetObject;

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry) {
        this(propertyEditorRegistry, null);
    }

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry, @Nullable Object targetObject) {
        this.propertyEditorRegistry = propertyEditorRegistry;
        this.targetObject = targetObject;
    }

    @Nullable
    public <T> T convertIfNecessary(@Nullable String str, @Nullable Object obj, Object obj2, @Nullable Class<T> cls) throws IllegalArgumentException {
        return (T) convertIfNecessary(str, obj, obj2, cls, TypeDescriptor.valueOf(cls));
    }

    @Nullable
    public <T> T convertIfNecessary(@Nullable String str, @Nullable Object obj, @Nullable Object obj2, @Nullable Class<T> cls, @Nullable TypeDescriptor typeDescriptor) throws IllegalArgumentException {
        TypeDescriptor elementTypeDescriptor;
        PropertyEditor findCustomEditor = this.propertyEditorRegistry.findCustomEditor(cls, str);
        ConversionFailedException conversionFailedException = null;
        ConversionService conversionService = this.propertyEditorRegistry.getConversionService();
        if (findCustomEditor == null && conversionService != null && obj2 != null && typeDescriptor != null) {
            TypeDescriptor forObject = TypeDescriptor.forObject(obj2);
            if (conversionService.canConvert(forObject, typeDescriptor)) {
                try {
                    return (T) conversionService.convert(obj2, forObject, typeDescriptor);
                } catch (ConversionFailedException e) {
                    conversionFailedException = e;
                }
            }
        }
        Object obj3 = obj2;
        if (findCustomEditor != null || (cls != null && !ClassUtils.isAssignableValue(cls, obj3))) {
            if (typeDescriptor != null && cls != null && Collection.class.isAssignableFrom(cls) && (elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor()) != null) {
                Class<?> type = elementTypeDescriptor.getType();
                if (obj3 instanceof String) {
                    String str2 = (String) obj3;
                    if (Class.class == type || Enum.class.isAssignableFrom(type)) {
                        obj3 = StringUtils.commaDelimitedListToStringArray(str2);
                    }
                    if (findCustomEditor == null && String.class != type) {
                        findCustomEditor = findDefaultEditor(type.arrayType());
                    }
                }
            }
            if (findCustomEditor == null) {
                findCustomEditor = findDefaultEditor(cls);
            }
            obj3 = doConvertValue(obj, obj3, cls, findCustomEditor);
        }
        boolean z = false;
        if (cls != null) {
            if (obj3 != null) {
                if (Object.class == cls) {
                    return (T) obj3;
                }
                if (cls.isArray()) {
                    if (obj3 instanceof String) {
                        String str3 = (String) obj3;
                        if (Enum.class.isAssignableFrom(cls.componentType())) {
                            obj3 = StringUtils.commaDelimitedListToStringArray(str3);
                        }
                    }
                    return (T) convertToTypedArray(obj3, str, cls.componentType());
                }
                if (obj3.getClass().isArray()) {
                    if (Collection.class.isAssignableFrom(cls)) {
                        obj3 = convertToTypedCollection(CollectionUtils.arrayToList(obj3), str, cls, typeDescriptor);
                        z = true;
                    } else if (Array.getLength(obj3) == 1) {
                        obj3 = Array.get(obj3, 0);
                        z = true;
                    }
                } else if (obj3 instanceof Collection) {
                    obj3 = convertToTypedCollection((Collection) obj3, str, cls, typeDescriptor);
                    z = true;
                } else if (obj3 instanceof Map) {
                    obj3 = convertToTypedMap((Map) obj3, str, cls, typeDescriptor);
                    z = true;
                }
                if (String.class == cls && ClassUtils.isPrimitiveOrWrapper(obj3.getClass())) {
                    return (T) obj3.toString();
                }
                if (obj3 instanceof String) {
                    String str4 = (String) obj3;
                    if (!cls.isInstance(obj3)) {
                        if (conversionFailedException == null && !cls.isInterface() && !cls.isEnum()) {
                            try {
                                return (T) BeanUtils.instantiateClass(cls.getConstructor(String.class), obj3);
                            } catch (NoSuchMethodException e2) {
                                if (logger.isTraceEnabled()) {
                                    logger.trace("No String constructor found on type [" + cls.getName() + "]", e2);
                                }
                            } catch (Exception e3) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Construction via String failed for type [" + cls.getName() + "]", e3);
                                }
                            }
                        }
                        String trim = str4.trim();
                        if (cls.isEnum() && trim.isEmpty()) {
                            return null;
                        }
                        obj3 = attemptToConvertStringToEnum(cls, trim, obj3);
                        z = true;
                    }
                }
                if (obj3 instanceof Number) {
                    Number number = (Number) obj3;
                    if (Number.class.isAssignableFrom(cls)) {
                        obj3 = NumberUtils.convertNumberToTargetClass(number, cls);
                        z = true;
                    }
                }
            } else if (cls == Optional.class) {
                obj3 = Optional.empty();
            }
            if (!ClassUtils.isAssignableValue(cls, obj3)) {
                if (conversionFailedException != null) {
                    throw conversionFailedException;
                }
                if (conversionService != null && typeDescriptor != null) {
                    TypeDescriptor forObject2 = TypeDescriptor.forObject(obj2);
                    if (conversionService.canConvert(forObject2, typeDescriptor)) {
                        return (T) conversionService.convert(obj2, forObject2, typeDescriptor);
                    }
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Cannot convert value of type '").append(ClassUtils.getDescriptiveType(obj2));
                sb.append("' to required type '").append(ClassUtils.getQualifiedName(cls)).append('\'');
                if (str != null) {
                    sb.append(" for property '").append(str).append('\'');
                }
                if (findCustomEditor != null) {
                    sb.append(": PropertyEditor [").append(findCustomEditor.getClass().getName()).append("] returned inappropriate value of type '").append(ClassUtils.getDescriptiveType(obj3)).append('\'');
                    throw new IllegalArgumentException(sb.toString());
                }
                sb.append(": no matching editors or conversion strategy found");
                throw new IllegalStateException(sb.toString());
            }
        }
        if (conversionFailedException != null) {
            if (findCustomEditor == null && !z && cls != null && Object.class != cls) {
                throw conversionFailedException;
            }
            logger.debug("Original ConversionService attempt failed - ignored since PropertyEditor based conversion eventually succeeded", conversionFailedException);
        }
        return (T) obj3;
    }

    private Object attemptToConvertStringToEnum(Class<?> requiredType, String trimmedValue, Object currentConvertedValue) {
        int index;
        Object convertedValue = currentConvertedValue;
        if (Enum.class == requiredType && this.targetObject != null && (index = trimmedValue.lastIndexOf(46)) > -1) {
            String enumType = trimmedValue.substring(0, index);
            String fieldName = trimmedValue.substring(index + 1);
            ClassLoader cl = this.targetObject.getClass().getClassLoader();
            try {
                Class<?> enumValueType = ClassUtils.forName(enumType, cl);
                convertedValue = enumValueType.getField(fieldName).get(null);
            } catch (ClassNotFoundException ex) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Enum class [" + enumType + "] cannot be loaded", ex);
                }
            } catch (Throwable ex2) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Field [" + fieldName + "] isn't an enum value for type [" + enumType + "]", ex2);
                }
            }
        }
        if (convertedValue == currentConvertedValue) {
            try {
                Field enumField = requiredType.getField(trimmedValue);
                ReflectionUtils.makeAccessible(enumField);
                convertedValue = enumField.get(null);
            } catch (Throwable ex3) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Field [" + convertedValue + "] isn't an enum value", ex3);
                }
            }
        }
        return convertedValue;
    }

    @Nullable
    private PropertyEditor findDefaultEditor(@Nullable Class<?> requiredType) {
        PropertyEditor editor = null;
        if (requiredType != null) {
            editor = this.propertyEditorRegistry.getDefaultEditor(requiredType);
            if (editor == null && String.class != requiredType) {
                editor = BeanUtils.findEditorByConvention(requiredType);
            }
        }
        return editor;
    }

    @Nullable
    private Object doConvertValue(@Nullable Object oldValue, @Nullable Object newValue, @Nullable Class<?> requiredType, @Nullable PropertyEditor editor) {
        Object convertedValue = newValue;
        if (editor != null && !(convertedValue instanceof String)) {
            try {
                editor.setValue(convertedValue);
                Object newConvertedValue = editor.getValue();
                if (newConvertedValue != convertedValue) {
                    convertedValue = newConvertedValue;
                    editor = null;
                }
            } catch (Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", ex);
                }
            }
        }
        Object returnValue = convertedValue;
        if (requiredType != null && !requiredType.isArray() && (convertedValue instanceof String[])) {
            String[] array = (String[]) convertedValue;
            if (logger.isTraceEnabled()) {
                logger.trace("Converting String array to comma-delimited String [" + convertedValue + "]");
            }
            convertedValue = StringUtils.arrayToCommaDelimitedString(array);
        }
        if (convertedValue instanceof String) {
            String newTextValue = (String) convertedValue;
            if (editor != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Converting String to [" + requiredType + "] using property editor [" + editor + "]");
                }
                return doConvertTextValue(oldValue, newTextValue, editor);
            }
            if (String.class == requiredType) {
                returnValue = convertedValue;
            }
        }
        return returnValue;
    }

    private Object doConvertTextValue(@Nullable Object oldValue, String newTextValue, PropertyEditor editor) {
        try {
            editor.setValue(oldValue);
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", ex);
            }
        }
        editor.setAsText(newTextValue);
        return editor.getValue();
    }

    private Object convertToTypedArray(Object input, @Nullable String propertyName, Class<?> componentType) {
        if (input instanceof Collection) {
            Collection<?> coll = (Collection) input;
            Object result = Array.newInstance(componentType, coll.size());
            int i = 0;
            Iterator<?> it = coll.iterator();
            while (it.hasNext()) {
                Object value = convertIfNecessary(buildIndexedPropertyName(propertyName, i), null, it.next(), componentType);
                Array.set(result, i, value);
                i++;
            }
            return result;
        }
        if (input.getClass().isArray()) {
            if (componentType.equals(input.getClass().componentType()) && !this.propertyEditorRegistry.hasCustomEditorForElement(componentType, propertyName)) {
                return input;
            }
            int arrayLength = Array.getLength(input);
            Object result2 = Array.newInstance(componentType, arrayLength);
            for (int i2 = 0; i2 < arrayLength; i2++) {
                Object value2 = convertIfNecessary(buildIndexedPropertyName(propertyName, i2), null, Array.get(input, i2), componentType);
                Array.set(result2, i2, value2);
            }
            return result2;
        }
        Object result3 = Array.newInstance(componentType, 1);
        Object value3 = convertIfNecessary(buildIndexedPropertyName(propertyName, 0), null, input, componentType);
        Array.set(result3, 0, value3);
        return result3;
    }

    /* JADX WARN: Removed duplicated region for block: B:35:0x00fb  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0178  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x017c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.util.Collection<?> convertToTypedCollection(java.util.Collection<?> r8, @org.springframework.lang.Nullable java.lang.String r9, java.lang.Class<?> r10, @org.springframework.lang.Nullable org.springframework.core.convert.TypeDescriptor r11) {
        /*
            Method dump skipped, instructions count: 383
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.TypeConverterDelegate.convertToTypedCollection(java.util.Collection, java.lang.String, java.lang.Class, org.springframework.core.convert.TypeDescriptor):java.util.Collection");
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x0112  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x01c5  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x01c9  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.util.Map<?, ?> convertToTypedMap(java.util.Map<?, ?> r8, @org.springframework.lang.Nullable java.lang.String r9, java.lang.Class<?> r10, @org.springframework.lang.Nullable org.springframework.core.convert.TypeDescriptor r11) {
        /*
            Method dump skipped, instructions count: 460
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.TypeConverterDelegate.convertToTypedMap(java.util.Map, java.lang.String, java.lang.Class, org.springframework.core.convert.TypeDescriptor):java.util.Map");
    }

    @Nullable
    private String buildIndexedPropertyName(@Nullable String propertyName, int index) {
        if (propertyName != null) {
            return propertyName + "[" + index + "]";
        }
        return null;
    }

    @Nullable
    private String buildKeyedPropertyName(@Nullable String propertyName, Object key) {
        if (propertyName != null) {
            return propertyName + "[" + key + "]";
        }
        return null;
    }

    private boolean canCreateCopy(Class<?> requiredType) {
        return !requiredType.isInterface() && !Modifier.isAbstract(requiredType.getModifiers()) && Modifier.isPublic(requiredType.getModifiers()) && ClassUtils.hasConstructor(requiredType, new Class[0]);
    }
}
