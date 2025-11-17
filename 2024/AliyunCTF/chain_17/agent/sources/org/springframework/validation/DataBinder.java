package org.springframework.validation;

import java.beans.PropertyEditor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyBatchUpdateException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.format.Formatter;
import org.springframework.format.support.FormatterPropertyEditorAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.ValidationAnnotationUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/DataBinder.class */
public class DataBinder implements PropertyEditorRegistry, TypeConverter {
    public static final String DEFAULT_OBJECT_NAME = "target";
    public static final int DEFAULT_AUTO_GROW_COLLECTION_LIMIT = 256;
    protected static final Log logger = LogFactory.getLog((Class<?>) DataBinder.class);

    @Nullable
    private Object target;

    @Nullable
    ResolvableType targetType;
    private final String objectName;

    @Nullable
    private AbstractPropertyBindingResult bindingResult;
    private boolean directFieldAccess;

    @Nullable
    private ExtendedTypeConverter typeConverter;
    private boolean declarativeBinding;
    private boolean ignoreUnknownFields;
    private boolean ignoreInvalidFields;
    private boolean autoGrowNestedPaths;
    private int autoGrowCollectionLimit;

    @Nullable
    private String[] allowedFields;

    @Nullable
    private String[] disallowedFields;

    @Nullable
    private String[] requiredFields;

    @Nullable
    private NameResolver nameResolver;

    @Nullable
    private ConversionService conversionService;

    @Nullable
    private MessageCodesResolver messageCodesResolver;
    private BindingErrorProcessor bindingErrorProcessor;
    private final List<Validator> validators;

    @Nullable
    private Predicate<Validator> excludedValidators;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/DataBinder$NameResolver.class */
    public interface NameResolver {
        @Nullable
        String resolveName(MethodParameter parameter);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/DataBinder$ValueResolver.class */
    public interface ValueResolver {
        @Nullable
        Object resolveValue(String name, Class<?> type);

        Set<String> getNames();
    }

    public DataBinder(@Nullable Object target) {
        this(target, DEFAULT_OBJECT_NAME);
    }

    public DataBinder(@Nullable Object target, String objectName) {
        this.directFieldAccess = false;
        this.declarativeBinding = false;
        this.ignoreUnknownFields = true;
        this.ignoreInvalidFields = false;
        this.autoGrowNestedPaths = true;
        this.autoGrowCollectionLimit = 256;
        this.bindingErrorProcessor = new DefaultBindingErrorProcessor();
        this.validators = new ArrayList();
        this.target = ObjectUtils.unwrapOptional(target);
        this.objectName = objectName;
    }

    @Nullable
    public Object getTarget() {
        return this.target;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public void setTargetType(ResolvableType targetType) {
        Assert.state(this.target == null, "targetType is used to for target creation but target is already set");
        this.targetType = targetType;
    }

    @Nullable
    public ResolvableType getTargetType() {
        return this.targetType;
    }

    public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
        Assert.state(this.bindingResult == null, "DataBinder is already initialized - call setAutoGrowNestedPaths before other configuration methods");
        this.autoGrowNestedPaths = autoGrowNestedPaths;
    }

    public boolean isAutoGrowNestedPaths() {
        return this.autoGrowNestedPaths;
    }

    public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
        Assert.state(this.bindingResult == null, "DataBinder is already initialized - call setAutoGrowCollectionLimit before other configuration methods");
        this.autoGrowCollectionLimit = autoGrowCollectionLimit;
    }

    public int getAutoGrowCollectionLimit() {
        return this.autoGrowCollectionLimit;
    }

    public void initBeanPropertyAccess() {
        Assert.state(this.bindingResult == null, "DataBinder is already initialized - call initBeanPropertyAccess before other configuration methods");
        this.directFieldAccess = false;
    }

    protected AbstractPropertyBindingResult createBeanPropertyBindingResult() {
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(getTarget(), getObjectName(), isAutoGrowNestedPaths(), getAutoGrowCollectionLimit());
        if (this.conversionService != null) {
            result.initConversion(this.conversionService);
        }
        if (this.messageCodesResolver != null) {
            result.setMessageCodesResolver(this.messageCodesResolver);
        }
        return result;
    }

    public void initDirectFieldAccess() {
        Assert.state(this.bindingResult == null, "DataBinder is already initialized - call initDirectFieldAccess before other configuration methods");
        this.directFieldAccess = true;
    }

    protected AbstractPropertyBindingResult createDirectFieldBindingResult() {
        DirectFieldBindingResult result = new DirectFieldBindingResult(getTarget(), getObjectName(), isAutoGrowNestedPaths());
        if (this.conversionService != null) {
            result.initConversion(this.conversionService);
        }
        if (this.messageCodesResolver != null) {
            result.setMessageCodesResolver(this.messageCodesResolver);
        }
        return result;
    }

    protected AbstractPropertyBindingResult getInternalBindingResult() {
        if (this.bindingResult == null) {
            this.bindingResult = this.directFieldAccess ? createDirectFieldBindingResult() : createBeanPropertyBindingResult();
        }
        return this.bindingResult;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ConfigurablePropertyAccessor getPropertyAccessor() {
        return getInternalBindingResult().getPropertyAccessor();
    }

    protected SimpleTypeConverter getSimpleTypeConverter() {
        if (this.typeConverter == null) {
            this.typeConverter = new ExtendedTypeConverter();
            if (this.conversionService != null) {
                this.typeConverter.setConversionService(this.conversionService);
            }
        }
        return this.typeConverter;
    }

    protected PropertyEditorRegistry getPropertyEditorRegistry() {
        if (getTarget() != null) {
            return getInternalBindingResult().getPropertyAccessor();
        }
        return getSimpleTypeConverter();
    }

    protected TypeConverter getTypeConverter() {
        if (getTarget() != null) {
            return getInternalBindingResult().getPropertyAccessor();
        }
        return getSimpleTypeConverter();
    }

    public BindingResult getBindingResult() {
        return getInternalBindingResult();
    }

    public void setDeclarativeBinding(boolean declarativeBinding) {
        this.declarativeBinding = declarativeBinding;
    }

    public boolean isDeclarativeBinding() {
        return this.declarativeBinding;
    }

    public void setIgnoreUnknownFields(boolean ignoreUnknownFields) {
        this.ignoreUnknownFields = ignoreUnknownFields;
    }

    public boolean isIgnoreUnknownFields() {
        return this.ignoreUnknownFields;
    }

    public void setIgnoreInvalidFields(boolean ignoreInvalidFields) {
        this.ignoreInvalidFields = ignoreInvalidFields;
    }

    public boolean isIgnoreInvalidFields() {
        return this.ignoreInvalidFields;
    }

    public void setAllowedFields(@Nullable String... allowedFields) {
        this.allowedFields = PropertyAccessorUtils.canonicalPropertyNames(allowedFields);
    }

    @Nullable
    public String[] getAllowedFields() {
        return this.allowedFields;
    }

    public void setDisallowedFields(@Nullable String... disallowedFields) {
        if (disallowedFields == null) {
            this.disallowedFields = null;
            return;
        }
        String[] fieldPatterns = new String[disallowedFields.length];
        for (int i = 0; i < fieldPatterns.length; i++) {
            fieldPatterns[i] = PropertyAccessorUtils.canonicalPropertyName(disallowedFields[i]).toLowerCase();
        }
        this.disallowedFields = fieldPatterns;
    }

    @Nullable
    public String[] getDisallowedFields() {
        return this.disallowedFields;
    }

    public void setRequiredFields(@Nullable String... requiredFields) {
        this.requiredFields = PropertyAccessorUtils.canonicalPropertyNames(requiredFields);
        if (logger.isDebugEnabled()) {
            logger.debug("DataBinder requires binding of required fields [" + StringUtils.arrayToCommaDelimitedString(requiredFields) + "]");
        }
    }

    @Nullable
    public String[] getRequiredFields() {
        return this.requiredFields;
    }

    public void setNameResolver(NameResolver nameResolver) {
        this.nameResolver = nameResolver;
    }

    @Nullable
    public NameResolver getNameResolver() {
        return this.nameResolver;
    }

    public void setMessageCodesResolver(@Nullable MessageCodesResolver messageCodesResolver) {
        Assert.state(this.messageCodesResolver == null, "DataBinder is already initialized with MessageCodesResolver");
        this.messageCodesResolver = messageCodesResolver;
        if (this.bindingResult != null && messageCodesResolver != null) {
            this.bindingResult.setMessageCodesResolver(messageCodesResolver);
        }
    }

    public void setBindingErrorProcessor(BindingErrorProcessor bindingErrorProcessor) {
        Assert.notNull(bindingErrorProcessor, "BindingErrorProcessor must not be null");
        this.bindingErrorProcessor = bindingErrorProcessor;
    }

    public BindingErrorProcessor getBindingErrorProcessor() {
        return this.bindingErrorProcessor;
    }

    public void setValidator(@Nullable Validator validator) {
        assertValidators(validator);
        this.validators.clear();
        if (validator != null) {
            this.validators.add(validator);
        }
    }

    private void assertValidators(Validator... validators) {
        Object target = getTarget();
        for (Validator validator : validators) {
            if (validator != null && target != null && !validator.supports(target.getClass())) {
                throw new IllegalStateException("Invalid target for Validator [" + validator + "]: " + target);
            }
        }
    }

    public void setExcludedValidators(Predicate<Validator> predicate) {
        this.excludedValidators = predicate;
    }

    public void addValidators(Validator... validators) {
        assertValidators(validators);
        this.validators.addAll(Arrays.asList(validators));
    }

    public void replaceValidators(Validator... validators) {
        assertValidators(validators);
        this.validators.clear();
        this.validators.addAll(Arrays.asList(validators));
    }

    @Nullable
    public Validator getValidator() {
        if (this.validators.isEmpty()) {
            return null;
        }
        return this.validators.get(0);
    }

    public List<Validator> getValidators() {
        return Collections.unmodifiableList(this.validators);
    }

    public List<Validator> getValidatorsToApply() {
        if (this.excludedValidators != null) {
            return this.validators.stream().filter(validator -> {
                return !this.excludedValidators.test(validator);
            }).toList();
        }
        return Collections.unmodifiableList(this.validators);
    }

    public void setConversionService(@Nullable ConversionService conversionService) {
        Assert.state(this.conversionService == null, "DataBinder is already initialized with ConversionService");
        this.conversionService = conversionService;
        if (this.bindingResult != null && conversionService != null) {
            this.bindingResult.initConversion(conversionService);
        }
    }

    @Nullable
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    public void addCustomFormatter(Formatter<?> formatter) {
        FormatterPropertyEditorAdapter adapter = new FormatterPropertyEditorAdapter(formatter);
        getPropertyEditorRegistry().registerCustomEditor(adapter.getFieldType(), adapter);
    }

    public void addCustomFormatter(Formatter<?> formatter, String... fields) {
        FormatterPropertyEditorAdapter adapter = new FormatterPropertyEditorAdapter(formatter);
        Class<?> fieldType = adapter.getFieldType();
        if (ObjectUtils.isEmpty((Object[]) fields)) {
            getPropertyEditorRegistry().registerCustomEditor(fieldType, adapter);
            return;
        }
        for (String field : fields) {
            getPropertyEditorRegistry().registerCustomEditor(fieldType, field, adapter);
        }
    }

    public void addCustomFormatter(Formatter<?> formatter, Class<?>... fieldTypes) {
        FormatterPropertyEditorAdapter adapter = new FormatterPropertyEditorAdapter(formatter);
        if (ObjectUtils.isEmpty((Object[]) fieldTypes)) {
            getPropertyEditorRegistry().registerCustomEditor(adapter.getFieldType(), adapter);
            return;
        }
        for (Class<?> fieldType : fieldTypes) {
            getPropertyEditorRegistry().registerCustomEditor(fieldType, adapter);
        }
    }

    @Override // org.springframework.beans.PropertyEditorRegistry
    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        getPropertyEditorRegistry().registerCustomEditor(requiredType, propertyEditor);
    }

    @Override // org.springframework.beans.PropertyEditorRegistry
    public void registerCustomEditor(@Nullable Class<?> requiredType, @Nullable String field, PropertyEditor propertyEditor) {
        getPropertyEditorRegistry().registerCustomEditor(requiredType, field, propertyEditor);
    }

    @Override // org.springframework.beans.PropertyEditorRegistry
    @Nullable
    public PropertyEditor findCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath) {
        return getPropertyEditorRegistry().findCustomEditor(requiredType, propertyPath);
    }

    @Override // org.springframework.beans.TypeConverter
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object obj, @Nullable Class<T> cls) throws TypeMismatchException {
        return (T) getTypeConverter().convertIfNecessary(obj, cls);
    }

    @Override // org.springframework.beans.TypeConverter
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object obj, @Nullable Class<T> cls, @Nullable MethodParameter methodParameter) throws TypeMismatchException {
        return (T) getTypeConverter().convertIfNecessary(obj, cls, methodParameter);
    }

    @Override // org.springframework.beans.TypeConverter
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object obj, @Nullable Class<T> cls, @Nullable Field field) throws TypeMismatchException {
        return (T) getTypeConverter().convertIfNecessary(obj, cls, field);
    }

    @Override // org.springframework.beans.TypeConverter
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object obj, @Nullable Class<T> cls, @Nullable TypeDescriptor typeDescriptor) throws TypeMismatchException {
        return (T) getTypeConverter().convertIfNecessary(obj, cls, typeDescriptor);
    }

    public void construct(ValueResolver valueResolver) {
        Assert.state(this.target == null, "Target instance already available");
        Assert.state(this.targetType != null, "Target type not set");
        this.target = createObject(this.targetType, "", valueResolver);
        if (!getBindingResult().hasErrors()) {
            this.bindingResult = null;
            if (this.typeConverter != null) {
                this.typeConverter.registerCustomEditors(getPropertyAccessor());
            }
        }
    }

    @Nullable
    private Object createObject(ResolvableType objectType, String nestedPath, ValueResolver valueResolver) {
        Class<?> clazz = objectType.resolve();
        boolean isOptional = clazz == Optional.class;
        Class<?> clazz2 = isOptional ? objectType.resolveGeneric(0) : clazz;
        if (clazz2 == null) {
            throw new IllegalStateException("Insufficient type information to create instance of " + objectType);
        }
        Object result = null;
        Constructor<?> ctor = BeanUtils.getResolvableConstructor(clazz2);
        if (ctor.getParameterCount() == 0) {
            result = BeanUtils.instantiateClass(ctor, new Object[0]);
        } else {
            String[] paramNames = BeanUtils.getParameterNames(ctor);
            Class<?>[] paramTypes = ctor.getParameterTypes();
            Object[] args = new Object[paramTypes.length];
            Set<String> failedParamNames = new HashSet<>(4);
            for (int i = 0; i < paramNames.length; i++) {
                MethodParameter param = MethodParameter.forFieldAwareConstructor(ctor, i, paramNames[i]);
                String lookupName = null;
                if (this.nameResolver != null) {
                    lookupName = this.nameResolver.resolveName(param);
                }
                if (lookupName == null) {
                    lookupName = paramNames[i];
                }
                String paramPath = nestedPath + lookupName;
                Class<?> paramType = paramTypes[i];
                Object value = valueResolver.resolveValue(paramPath, paramType);
                if (value == null && shouldConstructArgument(param) && hasValuesFor(paramPath, valueResolver)) {
                    ResolvableType type = ResolvableType.forMethodParameter(param);
                    args[i] = createObject(type, paramPath + ".", valueResolver);
                } else {
                    if (value == null) {
                        try {
                            if (param.isOptional() || getBindingResult().hasErrors()) {
                                args[i] = param.getParameterType() == Optional.class ? Optional.empty() : null;
                            }
                        } catch (TypeMismatchException ex) {
                            ex.initPropertyName(paramPath);
                            args[i] = null;
                            failedParamNames.add(paramPath);
                            getBindingResult().recordFieldValue(paramPath, paramType, value);
                            getBindingErrorProcessor().processPropertyAccessException(ex, getBindingResult());
                        }
                    }
                    args[i] = convertIfNecessary(value, paramType, param);
                }
            }
            if (getBindingResult().hasErrors()) {
                for (int i2 = 0; i2 < paramNames.length; i2++) {
                    String paramPath2 = nestedPath + paramNames[i2];
                    if (!failedParamNames.contains(paramPath2)) {
                        Object value2 = args[i2];
                        getBindingResult().recordFieldValue(paramPath2, paramTypes[i2], value2);
                        validateConstructorArgument(ctor.getDeclaringClass(), nestedPath, paramNames[i2], value2);
                    }
                }
                Object source = objectType.getSource();
                if (!(source instanceof MethodParameter) || !((MethodParameter) source).isOptional()) {
                    try {
                        result = BeanUtils.instantiateClass(ctor, args);
                    } catch (BeanInstantiationException e) {
                    }
                }
            } else {
                try {
                    result = BeanUtils.instantiateClass(ctor, args);
                } catch (BeanInstantiationException ex2) {
                    if (KotlinDetector.isKotlinType(clazz2)) {
                        Throwable cause = ex2.getCause();
                        if (cause instanceof NullPointerException) {
                            NullPointerException cause2 = (NullPointerException) cause;
                            ObjectError error = new ObjectError(ctor.getName(), cause2.getMessage());
                            getBindingResult().addError(error);
                        }
                    }
                    throw ex2;
                }
            }
        }
        return (!isOptional || nestedPath.isEmpty()) ? result : Optional.ofNullable(result);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldConstructArgument(MethodParameter param) {
        Class<?> type = param.nestedIfOptional().getNestedParameterType();
        return (BeanUtils.isSimpleValueType(type) || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type) || type.isArray() || type.getPackageName().startsWith("java.")) ? false : true;
    }

    private boolean hasValuesFor(String paramPath, ValueResolver resolver) {
        for (String name : resolver.getNames()) {
            if (name.startsWith(paramPath + ".")) {
                return true;
            }
        }
        return false;
    }

    private void validateConstructorArgument(Class<?> constructorClass, String nestedPath, String name, @Nullable Object value) {
        Object[] hints = null;
        Object source = this.targetType.getSource();
        if (source instanceof MethodParameter) {
            MethodParameter parameter = (MethodParameter) source;
            for (Annotation ann : parameter.getParameterAnnotations()) {
                hints = ValidationAnnotationUtils.determineValidationHints(ann);
                if (hints != null) {
                    break;
                }
            }
        }
        if (hints == null) {
            return;
        }
        for (Validator validator : getValidatorsToApply()) {
            if (validator instanceof SmartValidator) {
                SmartValidator smartValidator = (SmartValidator) validator;
                boolean isNested = !nestedPath.isEmpty();
                if (isNested) {
                    getBindingResult().pushNestedPath(nestedPath.substring(0, nestedPath.length() - 1));
                }
                try {
                    smartValidator.validateValue(constructorClass, name, value, getBindingResult(), hints);
                } catch (IllegalArgumentException e) {
                }
                if (isNested) {
                    getBindingResult().popNestedPath();
                }
            }
        }
    }

    public void bind(PropertyValues pvs) {
        MutablePropertyValues mutablePropertyValues;
        if (shouldNotBindPropertyValues()) {
            return;
        }
        if (pvs instanceof MutablePropertyValues) {
            MutablePropertyValues mutablePropertyValues2 = (MutablePropertyValues) pvs;
            mutablePropertyValues = mutablePropertyValues2;
        } else {
            mutablePropertyValues = new MutablePropertyValues(pvs);
        }
        MutablePropertyValues mpvs = mutablePropertyValues;
        doBind(mpvs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldNotBindPropertyValues() {
        return isDeclarativeBinding() && ObjectUtils.isEmpty((Object[]) this.allowedFields);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doBind(MutablePropertyValues mpvs) {
        checkAllowedFields(mpvs);
        checkRequiredFields(mpvs);
        applyPropertyValues(mpvs);
    }

    protected void checkAllowedFields(MutablePropertyValues mpvs) {
        PropertyValue[] pvs = mpvs.getPropertyValues();
        for (PropertyValue pv : pvs) {
            String field = PropertyAccessorUtils.canonicalPropertyName(pv.getName());
            if (!isAllowed(field)) {
                mpvs.removePropertyValue(pv);
                getBindingResult().recordSuppressedField(field);
                if (logger.isDebugEnabled()) {
                    logger.debug("Field [" + field + "] has been removed from PropertyValues and will not be bound, because it has not been found in the list of allowed fields");
                }
            }
        }
    }

    protected boolean isAllowed(String field) {
        String[] allowed = getAllowedFields();
        String[] disallowed = getDisallowedFields();
        return (ObjectUtils.isEmpty((Object[]) allowed) || PatternMatchUtils.simpleMatch(allowed, field)) && (ObjectUtils.isEmpty((Object[]) disallowed) || !PatternMatchUtils.simpleMatch(disallowed, field.toLowerCase()));
    }

    protected void checkRequiredFields(MutablePropertyValues mpvs) {
        String[] requiredFields = getRequiredFields();
        if (!ObjectUtils.isEmpty((Object[]) requiredFields)) {
            Map<String, PropertyValue> propertyValues = new HashMap<>();
            PropertyValue[] pvs = mpvs.getPropertyValues();
            for (PropertyValue pv : pvs) {
                String canonicalName = PropertyAccessorUtils.canonicalPropertyName(pv.getName());
                propertyValues.put(canonicalName, pv);
            }
            for (String field : requiredFields) {
                PropertyValue pv2 = propertyValues.get(field);
                boolean empty = pv2 == null || pv2.getValue() == null;
                if (!empty) {
                    Object value = pv2.getValue();
                    if (value instanceof String) {
                        String text = (String) value;
                        empty = !StringUtils.hasText(text);
                    } else {
                        Object value2 = pv2.getValue();
                        if (value2 instanceof String[]) {
                            String[] values = (String[]) value2;
                            empty = values.length == 0 || !StringUtils.hasText(values[0]);
                        }
                    }
                }
                if (empty) {
                    getBindingErrorProcessor().processMissingFieldError(field, getInternalBindingResult());
                    if (pv2 != null) {
                        mpvs.removePropertyValue(pv2);
                        propertyValues.remove(field);
                    }
                }
            }
        }
    }

    protected void applyPropertyValues(MutablePropertyValues mpvs) {
        try {
            getPropertyAccessor().setPropertyValues(mpvs, isIgnoreUnknownFields(), isIgnoreInvalidFields());
        } catch (PropertyBatchUpdateException ex) {
            for (PropertyAccessException pae : ex.getPropertyAccessExceptions()) {
                getBindingErrorProcessor().processPropertyAccessException(pae, getInternalBindingResult());
            }
        }
    }

    public void validate() {
        Object target = getTarget();
        Assert.state(target != null, "No target to validate");
        BindingResult bindingResult = getBindingResult();
        for (Validator validator : getValidatorsToApply()) {
            validator.validate(target, bindingResult);
        }
    }

    public void validate(Object... validationHints) {
        Object target = getTarget();
        Assert.state(target != null, "No target to validate");
        BindingResult bindingResult = getBindingResult();
        for (Validator validator : getValidatorsToApply()) {
            if (!ObjectUtils.isEmpty(validationHints) && (validator instanceof SmartValidator)) {
                SmartValidator smartValidator = (SmartValidator) validator;
                smartValidator.validate(target, bindingResult, validationHints);
            } else if (validator != null) {
                validator.validate(target, bindingResult);
            }
        }
    }

    public Map<?, ?> close() throws BindException {
        if (getBindingResult().hasErrors()) {
            throw new BindException(getBindingResult());
        }
        return getBindingResult().getModel();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/DataBinder$ExtendedTypeConverter.class */
    public static class ExtendedTypeConverter extends SimpleTypeConverter implements PropertyEditorRegistrar {
        private ExtendedTypeConverter() {
        }

        @Override // org.springframework.beans.PropertyEditorRegistrar
        public void registerCustomEditors(PropertyEditorRegistry registry) {
            copyCustomEditorsTo(registry, null);
        }
    }
}
