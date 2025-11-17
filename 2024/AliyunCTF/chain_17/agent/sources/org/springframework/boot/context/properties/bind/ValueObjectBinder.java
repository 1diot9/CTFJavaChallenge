package org.springframework.boot.context.properties.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.core.CollectionFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/ValueObjectBinder.class */
public class ValueObjectBinder implements DataObjectBinder {
    private static final Log logger = LogFactory.getLog((Class<?>) ValueObjectBinder.class);
    private final BindConstructorProvider constructorProvider;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueObjectBinder(BindConstructorProvider constructorProvider) {
        this.constructorProvider = constructorProvider;
    }

    @Override // org.springframework.boot.context.properties.bind.DataObjectBinder
    public <T> T bind(ConfigurationPropertyName name, Bindable<T> target, Binder.Context context, DataObjectPropertyBinder propertyBinder) {
        ValueObject<T> valueObject = ValueObject.get(target, this.constructorProvider, context, Discoverer.LENIENT);
        if (valueObject == null) {
            return null;
        }
        context.pushConstructorBoundTypes(target.getType().resolve());
        List<ConstructorParameter> parameters = valueObject.getConstructorParameters();
        List<Object> args = new ArrayList<>(parameters.size());
        boolean bound = false;
        for (ConstructorParameter parameter : parameters) {
            Object arg = parameter.bind(propertyBinder);
            bound = bound || arg != null;
            args.add(arg != null ? arg : getDefaultValue(context, parameter));
        }
        context.clearConfigurationProperty();
        context.popConstructorBoundTypes();
        if (bound) {
            return valueObject.instantiate(args);
        }
        return null;
    }

    @Override // org.springframework.boot.context.properties.bind.DataObjectBinder
    public <T> T create(Bindable<T> target, Binder.Context context) {
        ValueObject<T> valueObject = ValueObject.get(target, this.constructorProvider, context, Discoverer.LENIENT);
        if (valueObject == null) {
            return null;
        }
        List<ConstructorParameter> parameters = valueObject.getConstructorParameters();
        List<Object> args = new ArrayList<>(parameters.size());
        for (ConstructorParameter parameter : parameters) {
            args.add(getDefaultValue(context, parameter));
        }
        return valueObject.instantiate(args);
    }

    @Override // org.springframework.boot.context.properties.bind.DataObjectBinder
    public <T> void onUnableToCreateInstance(Bindable<T> target, Binder.Context context, RuntimeException exception) {
        try {
            ValueObject.get(target, this.constructorProvider, context, Discoverer.STRICT);
        } catch (Exception ex) {
            exception.addSuppressed(ex);
        }
    }

    private <T> T getDefaultValue(Binder.Context context, ConstructorParameter constructorParameter) {
        ResolvableType type = constructorParameter.getType();
        Annotation[] annotations = constructorParameter.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof DefaultValue) {
                String[] value = ((DefaultValue) annotation).value();
                if (value.length == 0) {
                    return (T) getNewDefaultValueInstanceIfPossible(context, type);
                }
                return (T) convertDefaultValue(context.getConverter(), value, type, annotations);
            }
        }
        return null;
    }

    private <T> T convertDefaultValue(BindConverter bindConverter, String[] strArr, ResolvableType resolvableType, Annotation[] annotationArr) {
        try {
            return (T) bindConverter.convert(strArr, resolvableType, annotationArr);
        } catch (ConversionException e) {
            if (strArr.length == 1) {
                return (T) bindConverter.convert(strArr[0], resolvableType, annotationArr);
            }
            throw e;
        }
    }

    private <T> T getNewDefaultValueInstanceIfPossible(Binder.Context context, ResolvableType resolvableType) {
        Class<?> resolve = resolvableType.resolve();
        Assert.state(resolve == null || isEmptyDefaultValueAllowed(resolve), (Supplier<String>) () -> {
            return "Parameter of type " + resolvableType + " must have a non-empty default value.";
        });
        if (resolve != null) {
            if (Optional.class == resolve) {
                return (T) Optional.empty();
            }
            if (Collection.class.isAssignableFrom(resolve)) {
                return (T) CollectionFactory.createCollection(resolve, 0);
            }
            if (Map.class.isAssignableFrom(resolve)) {
                return (T) CollectionFactory.createMap(resolve, 0);
            }
            if (resolve.isArray()) {
                return (T) Array.newInstance(resolve.getComponentType(), 0);
            }
        }
        T t = (T) create(Bindable.of(resolvableType), context);
        if (t != null) {
            return t;
        }
        if (resolve != null) {
            return (T) BeanUtils.instantiateClass(resolve);
        }
        return null;
    }

    private boolean isEmptyDefaultValueAllowed(Class<?> type) {
        return Optional.class == type || isAggregate(type) || !(type.isPrimitive() || type.isEnum() || type.getName().startsWith("java.lang"));
    }

    private boolean isAggregate(Class<?> type) {
        return type.isArray() || Map.class.isAssignableFrom(type) || Collection.class.isAssignableFrom(type);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/ValueObjectBinder$ValueObject.class */
    public static abstract class ValueObject<T> {
        private final Constructor<T> constructor;

        abstract List<ConstructorParameter> getConstructorParameters();

        protected ValueObject(Constructor<T> constructor) {
            this.constructor = constructor;
        }

        T instantiate(List<Object> list) {
            return (T) BeanUtils.instantiateClass(this.constructor, list.toArray());
        }

        static <T> ValueObject<T> get(Bindable<T> bindable, BindConstructorProvider constructorProvider, Binder.Context context, ParameterNameDiscoverer parameterNameDiscoverer) {
            Constructor<?> bindConstructor;
            Class<?> resolve = bindable.getType().resolve();
            if (resolve == null || resolve.isEnum() || Modifier.isAbstract(resolve.getModifiers()) || (bindConstructor = constructorProvider.getBindConstructor((Bindable<?>) bindable, context.isNestedConstructorBinding())) == null) {
                return null;
            }
            if (KotlinDetector.isKotlinType(resolve)) {
                return KotlinValueObject.get(bindConstructor, bindable.getType(), parameterNameDiscoverer);
            }
            return DefaultValueObject.get(bindConstructor, bindable.getType(), parameterNameDiscoverer);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/ValueObjectBinder$KotlinValueObject.class */
    public static final class KotlinValueObject<T> extends ValueObject<T> {
        private static final Annotation[] ANNOTATION_ARRAY = new Annotation[0];
        private final List<ConstructorParameter> constructorParameters;

        private KotlinValueObject(Constructor<T> primaryConstructor, KFunction<T> kotlinConstructor, ResolvableType type) {
            super(primaryConstructor);
            this.constructorParameters = parseConstructorParameters(kotlinConstructor, type);
        }

        private List<ConstructorParameter> parseConstructorParameters(KFunction<T> kotlinConstructor, ResolvableType type) {
            List<KParameter> parameters = kotlinConstructor.getParameters();
            List<ConstructorParameter> result = new ArrayList<>(parameters.size());
            for (KParameter parameter : parameters) {
                String name = getParameterName(parameter);
                ResolvableType parameterType = ResolvableType.forType(ReflectJvmMapping.getJavaType(parameter.getType()), type);
                Annotation[] annotations = (Annotation[]) parameter.getAnnotations().toArray(ANNOTATION_ARRAY);
                result.add(new ConstructorParameter(name, parameterType, annotations));
            }
            return Collections.unmodifiableList(result);
        }

        private String getParameterName(KParameter kParameter) {
            Optional<T> value = MergedAnnotations.from(kParameter, (Annotation[]) kParameter.getAnnotations().toArray(ANNOTATION_ARRAY)).get(Name.class).getValue("value", String.class);
            Objects.requireNonNull(kParameter);
            return (String) value.orElseGet(kParameter::getName);
        }

        @Override // org.springframework.boot.context.properties.bind.ValueObjectBinder.ValueObject
        List<ConstructorParameter> getConstructorParameters() {
            return this.constructorParameters;
        }

        static <T> ValueObject<T> get(Constructor<T> bindConstructor, ResolvableType type, ParameterNameDiscoverer parameterNameDiscoverer) {
            KFunction<T> kotlinConstructor = ReflectJvmMapping.getKotlinFunction(bindConstructor);
            if (kotlinConstructor != null) {
                return new KotlinValueObject(bindConstructor, kotlinConstructor, type);
            }
            return DefaultValueObject.get(bindConstructor, type, parameterNameDiscoverer);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/ValueObjectBinder$DefaultValueObject.class */
    public static final class DefaultValueObject<T> extends ValueObject<T> {
        private final List<ConstructorParameter> constructorParameters;

        private DefaultValueObject(Constructor<T> constructor, List<ConstructorParameter> constructorParameters) {
            super(constructor);
            this.constructorParameters = constructorParameters;
        }

        @Override // org.springframework.boot.context.properties.bind.ValueObjectBinder.ValueObject
        List<ConstructorParameter> getConstructorParameters() {
            return this.constructorParameters;
        }

        static <T> ValueObject<T> get(Constructor<?> bindConstructor, ResolvableType type, ParameterNameDiscoverer parameterNameDiscoverer) {
            String[] names = parameterNameDiscoverer.getParameterNames(bindConstructor);
            if (names == null) {
                return null;
            }
            List<ConstructorParameter> constructorParameters = parseConstructorParameters(bindConstructor, type, names);
            return new DefaultValueObject(bindConstructor, constructorParameters);
        }

        private static List<ConstructorParameter> parseConstructorParameters(Constructor<?> constructor, ResolvableType resolvableType, String[] strArr) {
            Parameter[] parameters = constructor.getParameters();
            ArrayList arrayList = new ArrayList(parameters.length);
            for (int i = 0; i < parameters.length; i++) {
                arrayList.add(new ConstructorParameter((String) MergedAnnotations.from(parameters[i]).get(Name.class).getValue("value", String.class).orElse(strArr[i]), ResolvableType.forMethodParameter(new MethodParameter(constructor, i), resolvableType), parameters[i].getDeclaredAnnotations()));
            }
            return Collections.unmodifiableList(arrayList);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/ValueObjectBinder$ConstructorParameter.class */
    public static class ConstructorParameter {
        private final String name;
        private final ResolvableType type;
        private final Annotation[] annotations;

        ConstructorParameter(String name, ResolvableType type, Annotation[] annotations) {
            this.name = DataObjectPropertyName.toDashedForm(name);
            this.type = type;
            this.annotations = annotations;
        }

        Object bind(DataObjectPropertyBinder propertyBinder) {
            return propertyBinder.bindProperty(this.name, Bindable.of(this.type).withAnnotations(this.annotations));
        }

        Annotation[] getAnnotations() {
            return this.annotations;
        }

        ResolvableType getType() {
            return this.type;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/ValueObjectBinder$Discoverer.class */
    public static final class Discoverer implements ParameterNameDiscoverer {
        private static final ParameterNameDiscoverer DEFAULT_DELEGATE = new DefaultParameterNameDiscoverer();
        private static final ParameterNameDiscoverer LENIENT = new Discoverer(DEFAULT_DELEGATE, message -> {
        });
        private static final ParameterNameDiscoverer STRICT = new Discoverer(DEFAULT_DELEGATE, message -> {
            throw new IllegalStateException(message.toString());
        });
        private final ParameterNameDiscoverer delegate;
        private final Consumer<LogMessage> noParameterNamesHandler;

        private Discoverer(ParameterNameDiscoverer delegate, Consumer<LogMessage> noParameterNamesHandler) {
            this.delegate = delegate;
            this.noParameterNamesHandler = noParameterNamesHandler;
        }

        @Override // org.springframework.core.ParameterNameDiscoverer
        public String[] getParameterNames(Method method) {
            throw new UnsupportedOperationException();
        }

        @Override // org.springframework.core.ParameterNameDiscoverer
        public String[] getParameterNames(Constructor<?> constructor) {
            String[] names = this.delegate.getParameterNames(constructor);
            if (names != null) {
                return names;
            }
            LogMessage message = LogMessage.format("Unable to use value object binding with constructor [%s] as parameter names cannot be discovered. Ensure that the compiler uses the '-parameters' flag", constructor);
            this.noParameterNamesHandler.accept(message);
            ValueObjectBinder.logger.debug(message);
            return null;
        }
    }
}
