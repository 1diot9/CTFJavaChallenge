package ch.qos.logback.core.joran.util;

import ch.qos.logback.core.joran.spi.DefaultClass;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.util.beans.BeanDescription;
import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
import ch.qos.logback.core.joran.util.beans.BeanUtil;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.AggregationType;
import ch.qos.logback.core.util.PropertySetterException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/util/PropertySetter.class */
public class PropertySetter extends ContextAwareBase {
    protected final Object obj;
    protected final Class<?> objClass;
    protected final BeanDescription beanDescription;

    public PropertySetter(BeanDescriptionCache beanDescriptionCache, Object obj) {
        this.obj = obj;
        this.objClass = obj.getClass();
        this.beanDescription = beanDescriptionCache.getBeanDescription(this.objClass);
    }

    public void setProperty(String name, String value) {
        if (value == null) {
            return;
        }
        Method setter = findSetterMethod(name);
        if (setter == null) {
            addWarn("No setter for property [" + name + "] in " + this.objClass.getName() + ".");
            return;
        }
        try {
            setProperty(setter, name, value);
        } catch (PropertySetterException ex) {
            addWarn("Failed to set property [" + name + "] to value \"" + value + "\". ", ex);
        }
    }

    private void setProperty(Method setter, String name, String value) throws PropertySetterException {
        Class<?>[] paramTypes = setter.getParameterTypes();
        try {
            Object arg = StringToObjectConverter.convertArg(this, value, paramTypes[0]);
            if (arg == null) {
                throw new PropertySetterException("Conversion to type [" + String.valueOf(paramTypes[0]) + "] failed.");
            }
            try {
                setter.invoke(this.obj, arg);
            } catch (Exception ex) {
                throw new PropertySetterException(ex);
            }
        } catch (Throwable t) {
            throw new PropertySetterException("Conversion to type [" + String.valueOf(paramTypes[0]) + "] failed. ", t);
        }
    }

    public AggregationType computeAggregationType(String name) {
        String cName = capitalizeFirstLetter(name);
        Method addMethod = findAdderMethod(cName);
        if (addMethod != null) {
            AggregationType type = computeRawAggregationType(addMethod);
            switch (type) {
                case NOT_FOUND:
                    return AggregationType.NOT_FOUND;
                case AS_BASIC_PROPERTY:
                    return AggregationType.AS_BASIC_PROPERTY_COLLECTION;
                case AS_COMPLEX_PROPERTY:
                    return AggregationType.AS_COMPLEX_PROPERTY_COLLECTION;
                case AS_BASIC_PROPERTY_COLLECTION:
                case AS_COMPLEX_PROPERTY_COLLECTION:
                    addError("Unexpected AggregationType " + String.valueOf(type));
                    break;
            }
        }
        Method setter = findSetterMethod(name);
        if (setter != null) {
            return computeRawAggregationType(setter);
        }
        return AggregationType.NOT_FOUND;
    }

    private Method findAdderMethod(String name) {
        String propertyName = BeanUtil.toLowerCamelCase(name);
        return this.beanDescription.getAdder(propertyName);
    }

    private Method findSetterMethod(String name) {
        String propertyName = BeanUtil.toLowerCamelCase(name);
        return this.beanDescription.getSetter(propertyName);
    }

    private Class<?> getParameterClassForMethod(Method method) {
        if (method == null) {
            return null;
        }
        Class<?>[] classArray = method.getParameterTypes();
        if (classArray.length != 1) {
            return null;
        }
        return classArray[0];
    }

    private AggregationType computeRawAggregationType(Method method) {
        Class<?> parameterClass = getParameterClassForMethod(method);
        if (parameterClass == null) {
            return AggregationType.NOT_FOUND;
        }
        if (StringToObjectConverter.canBeBuiltFromSimpleString(parameterClass)) {
            return AggregationType.AS_BASIC_PROPERTY;
        }
        return AggregationType.AS_COMPLEX_PROPERTY;
    }

    private boolean isUnequivocallyInstantiable(Class<?> clazz) {
        if (clazz.isInterface()) {
            return false;
        }
        try {
            Object o = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            if (o != null) {
                return true;
            }
            return false;
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            return false;
        }
    }

    public Class<?> getObjClass() {
        return this.objClass;
    }

    public void addComplexProperty(String name, Object complexProperty) {
        Method adderMethod = findAdderMethod(name);
        if (adderMethod != null) {
            Class<?>[] paramTypes = adderMethod.getParameterTypes();
            if (!isSanityCheckSuccessful(name, adderMethod, paramTypes, complexProperty)) {
                return;
            }
            invokeMethodWithSingleParameterOnThisObject(adderMethod, complexProperty);
            return;
        }
        addError("Could not find method [add" + name + "] in class [" + this.objClass.getName() + "].");
    }

    void invokeMethodWithSingleParameterOnThisObject(Method method, Object parameter) {
        Class<?> ccc = parameter.getClass();
        try {
            method.invoke(this.obj, parameter);
        } catch (Exception e) {
            addError("Could not invoke method " + method.getName() + " in class " + this.obj.getClass().getName() + " with parameter of type " + ccc.getName(), e);
        }
    }

    public void addBasicProperty(String name, String strValue) {
        if (strValue == null) {
            return;
        }
        String name2 = capitalizeFirstLetter(name);
        Method adderMethod = findAdderMethod(name2);
        if (adderMethod == null) {
            addError("No adder for property [" + name2 + "].");
            return;
        }
        Class<?>[] paramTypes = adderMethod.getParameterTypes();
        isSanityCheckSuccessful(name2, adderMethod, paramTypes, strValue);
        try {
            Object arg = StringToObjectConverter.convertArg(this, strValue, paramTypes[0]);
            if (arg != null) {
                invokeMethodWithSingleParameterOnThisObject(adderMethod, strValue);
            }
        } catch (Throwable t) {
            addError("Conversion to type [" + String.valueOf(paramTypes[0]) + "] failed. ", t);
        }
    }

    public void setComplexProperty(String name, Object complexProperty) {
        Method setter = findSetterMethod(name);
        if (setter == null) {
            addWarn("Not setter method for property [" + name + "] in " + this.obj.getClass().getName());
            return;
        }
        Class<?>[] paramTypes = setter.getParameterTypes();
        if (!isSanityCheckSuccessful(name, setter, paramTypes, complexProperty)) {
            return;
        }
        try {
            invokeMethodWithSingleParameterOnThisObject(setter, complexProperty);
        } catch (Exception e) {
            addError("Could not set component " + String.valueOf(this.obj) + " for parent component " + String.valueOf(this.obj), e);
        }
    }

    private boolean isSanityCheckSuccessful(String name, Method method, Class<?>[] params, Object complexProperty) {
        Class<?> ccc = complexProperty.getClass();
        if (params.length != 1) {
            addError("Wrong number of parameters in setter method for property [" + name + "] in " + this.obj.getClass().getName());
            return false;
        }
        if (!params[0].isAssignableFrom(complexProperty.getClass())) {
            addError("A \"" + ccc.getName() + "\" object is not assignable to a \"" + params[0].getName() + "\" variable.");
            addError("The class \"" + params[0].getName() + "\" was loaded by ");
            addError("[" + String.valueOf(params[0].getClassLoader()) + "] whereas object of type ");
            addError("\"" + ccc.getName() + "\" was loaded by [" + String.valueOf(ccc.getClassLoader()) + "].");
            return false;
        }
        return true;
    }

    private String capitalizeFirstLetter(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public Object getObj() {
        return this.obj;
    }

    Method getRelevantMethod(String name, AggregationType aggregationType) {
        Method relevantMethod;
        if (aggregationType == AggregationType.AS_COMPLEX_PROPERTY_COLLECTION) {
            relevantMethod = findAdderMethod(name);
        } else if (aggregationType == AggregationType.AS_COMPLEX_PROPERTY) {
            relevantMethod = findSetterMethod(name);
        } else {
            throw new IllegalStateException(String.valueOf(aggregationType) + " not allowed here");
        }
        return relevantMethod;
    }

    <T extends Annotation> T getAnnotation(String str, Class<T> cls, Method method) {
        if (method != null) {
            return (T) method.getAnnotation(cls);
        }
        return null;
    }

    Class<?> getDefaultClassNameByAnnonation(String name, Method relevantMethod) {
        DefaultClass defaultClassAnnon = (DefaultClass) getAnnotation(name, DefaultClass.class, relevantMethod);
        if (defaultClassAnnon != null) {
            return defaultClassAnnon.value();
        }
        return null;
    }

    Class<?> getByConcreteType(String name, Method relevantMethod) {
        Class<?> paramType = getParameterClassForMethod(relevantMethod);
        if (paramType == null) {
            return null;
        }
        boolean isUnequivocallyInstantiable = isUnequivocallyInstantiable(paramType);
        if (isUnequivocallyInstantiable) {
            return paramType;
        }
        return null;
    }

    public Class<?> getClassNameViaImplicitRules(String name, AggregationType aggregationType, DefaultNestedComponentRegistry registry) {
        Class<?> registryResult = registry.findDefaultComponentType(this.obj.getClass(), name);
        if (registryResult != null) {
            return registryResult;
        }
        Method relevantMethod = getRelevantMethod(name, aggregationType);
        if (relevantMethod == null) {
            return null;
        }
        Class<?> byAnnotation = getDefaultClassNameByAnnonation(name, relevantMethod);
        if (byAnnotation != null) {
            return byAnnotation;
        }
        return getByConcreteType(name, relevantMethod);
    }
}
