package org.springframework.beans.support;

import java.beans.PropertyEditor;
import java.lang.reflect.Method;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/support/ArgumentConvertingMethodInvoker.class */
public class ArgumentConvertingMethodInvoker extends MethodInvoker {

    @Nullable
    private TypeConverter typeConverter;
    private boolean useDefaultConverter = true;

    public void setTypeConverter(@Nullable TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
        this.useDefaultConverter = typeConverter == null;
    }

    @Nullable
    public TypeConverter getTypeConverter() {
        if (this.typeConverter == null && this.useDefaultConverter) {
            this.typeConverter = getDefaultTypeConverter();
        }
        return this.typeConverter;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public TypeConverter getDefaultTypeConverter() {
        return new SimpleTypeConverter();
    }

    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        TypeConverter converter = getTypeConverter();
        if (!(converter instanceof PropertyEditorRegistry)) {
            throw new IllegalStateException("TypeConverter does not implement PropertyEditorRegistry interface: " + converter);
        }
        PropertyEditorRegistry registry = (PropertyEditorRegistry) converter;
        registry.registerCustomEditor(requiredType, propertyEditor);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.util.MethodInvoker
    @Nullable
    public Method findMatchingMethod() {
        Method matchingMethod = super.findMatchingMethod();
        if (matchingMethod == null) {
            matchingMethod = doFindMatchingMethod(getArguments());
        }
        if (matchingMethod == null) {
            matchingMethod = doFindMatchingMethod(new Object[]{getArguments()});
        }
        return matchingMethod;
    }

    @Nullable
    protected Method doFindMatchingMethod(Object[] arguments) {
        int typeDiffWeight;
        TypeConverter converter = getTypeConverter();
        if (converter != null) {
            String targetMethod = getTargetMethod();
            Method matchingMethod = null;
            int argCount = arguments.length;
            Class<?> targetClass = getTargetClass();
            Assert.state(targetClass != null, "No target class set");
            Method[] candidates = ReflectionUtils.getAllDeclaredMethods(targetClass);
            int minTypeDiffWeight = Integer.MAX_VALUE;
            Object[] argumentsToUse = null;
            for (Method candidate : candidates) {
                if (candidate.getName().equals(targetMethod)) {
                    int parameterCount = candidate.getParameterCount();
                    if (parameterCount == argCount) {
                        Class<?>[] paramTypes = candidate.getParameterTypes();
                        Object[] convertedArguments = new Object[argCount];
                        boolean match = true;
                        for (int j = 0; j < argCount && match; j++) {
                            try {
                                convertedArguments[j] = converter.convertIfNecessary(arguments[j], paramTypes[j]);
                            } catch (TypeMismatchException e) {
                                match = false;
                            }
                        }
                        if (match && (typeDiffWeight = getTypeDifferenceWeight(paramTypes, convertedArguments)) < minTypeDiffWeight) {
                            minTypeDiffWeight = typeDiffWeight;
                            matchingMethod = candidate;
                            argumentsToUse = convertedArguments;
                        }
                    }
                }
            }
            if (matchingMethod != null) {
                setArguments(argumentsToUse);
                return matchingMethod;
            }
            return null;
        }
        return null;
    }
}
