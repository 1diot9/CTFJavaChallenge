package org.springframework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/AbstractBeanDefinition.class */
public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor implements BeanDefinition, Cloneable {
    public static final String SCOPE_DEFAULT = "";
    public static final int AUTOWIRE_NO = 0;
    public static final int AUTOWIRE_BY_NAME = 1;
    public static final int AUTOWIRE_BY_TYPE = 2;
    public static final int AUTOWIRE_CONSTRUCTOR = 3;

    @Deprecated
    public static final int AUTOWIRE_AUTODETECT = 4;
    public static final int DEPENDENCY_CHECK_NONE = 0;
    public static final int DEPENDENCY_CHECK_OBJECTS = 1;
    public static final int DEPENDENCY_CHECK_SIMPLE = 2;
    public static final int DEPENDENCY_CHECK_ALL = 3;
    public static final String PREFERRED_CONSTRUCTORS_ATTRIBUTE = "preferredConstructors";
    public static final String ORDER_ATTRIBUTE = "order";
    public static final String INFER_METHOD = "(inferred)";

    @Nullable
    private volatile Object beanClass;

    @Nullable
    private String scope;
    private boolean abstractFlag;

    @Nullable
    private Boolean lazyInit;
    private int autowireMode;
    private int dependencyCheck;

    @Nullable
    private String[] dependsOn;
    private boolean autowireCandidate;
    private boolean primary;
    private final Map<String, AutowireCandidateQualifier> qualifiers;

    @Nullable
    private Supplier<?> instanceSupplier;
    private boolean nonPublicAccessAllowed;
    private boolean lenientConstructorResolution;

    @Nullable
    private String factoryBeanName;

    @Nullable
    private String factoryMethodName;

    @Nullable
    private ConstructorArgumentValues constructorArgumentValues;

    @Nullable
    private MutablePropertyValues propertyValues;
    private MethodOverrides methodOverrides;

    @Nullable
    private String[] initMethodNames;

    @Nullable
    private String[] destroyMethodNames;
    private boolean enforceInitMethod;
    private boolean enforceDestroyMethod;
    private boolean synthetic;
    private int role;

    @Nullable
    private String description;

    @Nullable
    private Resource resource;

    public abstract AbstractBeanDefinition cloneBeanDefinition();

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractBeanDefinition() {
        this(null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractBeanDefinition(@Nullable ConstructorArgumentValues cargs, @Nullable MutablePropertyValues pvs) {
        this.scope = "";
        this.abstractFlag = false;
        this.autowireMode = 0;
        this.dependencyCheck = 0;
        this.autowireCandidate = true;
        this.primary = false;
        this.qualifiers = new LinkedHashMap();
        this.nonPublicAccessAllowed = true;
        this.lenientConstructorResolution = true;
        this.methodOverrides = new MethodOverrides();
        this.enforceInitMethod = true;
        this.enforceDestroyMethod = true;
        this.synthetic = false;
        this.role = 0;
        this.constructorArgumentValues = cargs;
        this.propertyValues = pvs;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractBeanDefinition(BeanDefinition original) {
        this.scope = "";
        this.abstractFlag = false;
        this.autowireMode = 0;
        this.dependencyCheck = 0;
        this.autowireCandidate = true;
        this.primary = false;
        this.qualifiers = new LinkedHashMap();
        this.nonPublicAccessAllowed = true;
        this.lenientConstructorResolution = true;
        this.methodOverrides = new MethodOverrides();
        this.enforceInitMethod = true;
        this.enforceDestroyMethod = true;
        this.synthetic = false;
        this.role = 0;
        setParentName(original.getParentName());
        setBeanClassName(original.getBeanClassName());
        setScope(original.getScope());
        setAbstract(original.isAbstract());
        setFactoryBeanName(original.getFactoryBeanName());
        setFactoryMethodName(original.getFactoryMethodName());
        setRole(original.getRole());
        setSource(original.getSource());
        copyAttributesFrom(original);
        if (original instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition originalAbd = (AbstractBeanDefinition) original;
            if (originalAbd.hasBeanClass()) {
                setBeanClass(originalAbd.getBeanClass());
            }
            if (originalAbd.hasConstructorArgumentValues()) {
                setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
            }
            if (originalAbd.hasPropertyValues()) {
                setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
            }
            if (originalAbd.hasMethodOverrides()) {
                setMethodOverrides(new MethodOverrides(originalAbd.getMethodOverrides()));
            }
            Boolean lazyInit = originalAbd.getLazyInit();
            if (lazyInit != null) {
                setLazyInit(lazyInit.booleanValue());
            }
            setAutowireMode(originalAbd.getAutowireMode());
            setDependencyCheck(originalAbd.getDependencyCheck());
            setDependsOn(originalAbd.getDependsOn());
            setAutowireCandidate(originalAbd.isAutowireCandidate());
            setPrimary(originalAbd.isPrimary());
            copyQualifiersFrom(originalAbd);
            setInstanceSupplier(originalAbd.getInstanceSupplier());
            setNonPublicAccessAllowed(originalAbd.isNonPublicAccessAllowed());
            setLenientConstructorResolution(originalAbd.isLenientConstructorResolution());
            setInitMethodNames(originalAbd.getInitMethodNames());
            setEnforceInitMethod(originalAbd.isEnforceInitMethod());
            setDestroyMethodNames(originalAbd.getDestroyMethodNames());
            setEnforceDestroyMethod(originalAbd.isEnforceDestroyMethod());
            setSynthetic(originalAbd.isSynthetic());
            setResource(originalAbd.getResource());
            return;
        }
        setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
        setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
        setLazyInit(original.isLazyInit());
        setResourceDescription(original.getResourceDescription());
    }

    public void overrideFrom(BeanDefinition other) {
        if (StringUtils.hasLength(other.getBeanClassName())) {
            setBeanClassName(other.getBeanClassName());
        }
        if (StringUtils.hasLength(other.getScope())) {
            setScope(other.getScope());
        }
        setAbstract(other.isAbstract());
        if (StringUtils.hasLength(other.getFactoryBeanName())) {
            setFactoryBeanName(other.getFactoryBeanName());
        }
        if (StringUtils.hasLength(other.getFactoryMethodName())) {
            setFactoryMethodName(other.getFactoryMethodName());
        }
        setRole(other.getRole());
        setSource(other.getSource());
        copyAttributesFrom(other);
        if (other instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition otherAbd = (AbstractBeanDefinition) other;
            if (otherAbd.hasBeanClass()) {
                setBeanClass(otherAbd.getBeanClass());
            }
            if (otherAbd.hasConstructorArgumentValues()) {
                getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
            }
            if (otherAbd.hasPropertyValues()) {
                getPropertyValues().addPropertyValues(other.getPropertyValues());
            }
            if (otherAbd.hasMethodOverrides()) {
                getMethodOverrides().addOverrides(otherAbd.getMethodOverrides());
            }
            Boolean lazyInit = otherAbd.getLazyInit();
            if (lazyInit != null) {
                setLazyInit(lazyInit.booleanValue());
            }
            setAutowireMode(otherAbd.getAutowireMode());
            setDependencyCheck(otherAbd.getDependencyCheck());
            setDependsOn(otherAbd.getDependsOn());
            setAutowireCandidate(otherAbd.isAutowireCandidate());
            setPrimary(otherAbd.isPrimary());
            copyQualifiersFrom(otherAbd);
            setInstanceSupplier(otherAbd.getInstanceSupplier());
            setNonPublicAccessAllowed(otherAbd.isNonPublicAccessAllowed());
            setLenientConstructorResolution(otherAbd.isLenientConstructorResolution());
            if (otherAbd.getInitMethodNames() != null) {
                setInitMethodNames(otherAbd.getInitMethodNames());
                setEnforceInitMethod(otherAbd.isEnforceInitMethod());
            }
            if (otherAbd.getDestroyMethodNames() != null) {
                setDestroyMethodNames(otherAbd.getDestroyMethodNames());
                setEnforceDestroyMethod(otherAbd.isEnforceDestroyMethod());
            }
            setSynthetic(otherAbd.isSynthetic());
            setResource(otherAbd.getResource());
            return;
        }
        getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
        getPropertyValues().addPropertyValues(other.getPropertyValues());
        setLazyInit(other.isLazyInit());
        setResourceDescription(other.getResourceDescription());
    }

    public void applyDefaults(BeanDefinitionDefaults defaults) {
        Boolean lazyInit = defaults.getLazyInit();
        if (lazyInit != null) {
            setLazyInit(lazyInit.booleanValue());
        }
        setAutowireMode(defaults.getAutowireMode());
        setDependencyCheck(defaults.getDependencyCheck());
        setInitMethodName(defaults.getInitMethodName());
        setEnforceInitMethod(false);
        setDestroyMethodName(defaults.getDestroyMethodName());
        setEnforceDestroyMethod(false);
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setBeanClassName(@Nullable String beanClassName) {
        this.beanClass = beanClassName;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    @Nullable
    public String getBeanClassName() {
        Object beanClassObject = this.beanClass;
        if (!(beanClassObject instanceof Class)) {
            return (String) beanClassObject;
        }
        Class<?> clazz = (Class) beanClassObject;
        return clazz.getName();
    }

    public void setBeanClass(@Nullable Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() throws IllegalStateException {
        Object beanClassObject = this.beanClass;
        if (beanClassObject == null) {
            throw new IllegalStateException("No bean class specified on bean definition");
        }
        if (!(beanClassObject instanceof Class)) {
            throw new IllegalStateException("Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
        }
        Class<?> clazz = (Class) beanClassObject;
        return clazz;
    }

    public boolean hasBeanClass() {
        return this.beanClass instanceof Class;
    }

    @Nullable
    public Class<?> resolveBeanClass(@Nullable ClassLoader classLoader) throws ClassNotFoundException {
        String className = getBeanClassName();
        if (className == null) {
            return null;
        }
        Class<?> resolvedClass = ClassUtils.forName(className, classLoader);
        this.beanClass = resolvedClass;
        return resolvedClass;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public ResolvableType getResolvableType() {
        return hasBeanClass() ? ResolvableType.forClass(getBeanClass()) : ResolvableType.NONE;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setScope(@Nullable String scope) {
        this.scope = scope;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    @Nullable
    public String getScope() {
        return this.scope;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public boolean isSingleton() {
        return "singleton".equals(this.scope) || "".equals(this.scope);
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public boolean isPrototype() {
        return "prototype".equals(this.scope);
    }

    public void setAbstract(boolean abstractFlag) {
        this.abstractFlag = abstractFlag;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public boolean isAbstract() {
        return this.abstractFlag;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = Boolean.valueOf(lazyInit);
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public boolean isLazyInit() {
        return this.lazyInit != null && this.lazyInit.booleanValue();
    }

    @Nullable
    public Boolean getLazyInit() {
        return this.lazyInit;
    }

    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    public int getAutowireMode() {
        return this.autowireMode;
    }

    public int getResolvedAutowireMode() {
        if (this.autowireMode == 4) {
            Constructor<?>[] constructors = getBeanClass().getConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == 0) {
                    return 2;
                }
            }
            return 3;
        }
        return this.autowireMode;
    }

    public void setDependencyCheck(int dependencyCheck) {
        this.dependencyCheck = dependencyCheck;
    }

    public int getDependencyCheck() {
        return this.dependencyCheck;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setDependsOn(@Nullable String... dependsOn) {
        this.dependsOn = dependsOn;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    @Nullable
    public String[] getDependsOn() {
        return this.dependsOn;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setAutowireCandidate(boolean autowireCandidate) {
        this.autowireCandidate = autowireCandidate;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public boolean isAutowireCandidate() {
        return this.autowireCandidate;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public boolean isPrimary() {
        return this.primary;
    }

    public void addQualifier(AutowireCandidateQualifier qualifier) {
        this.qualifiers.put(qualifier.getTypeName(), qualifier);
    }

    public boolean hasQualifier(String typeName) {
        return this.qualifiers.containsKey(typeName);
    }

    @Nullable
    public AutowireCandidateQualifier getQualifier(String typeName) {
        return this.qualifiers.get(typeName);
    }

    public Set<AutowireCandidateQualifier> getQualifiers() {
        return new LinkedHashSet(this.qualifiers.values());
    }

    public void copyQualifiersFrom(AbstractBeanDefinition source) {
        Assert.notNull(source, "Source must not be null");
        this.qualifiers.putAll(source.qualifiers);
    }

    public void setInstanceSupplier(@Nullable Supplier<?> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
    }

    @Nullable
    public Supplier<?> getInstanceSupplier() {
        return this.instanceSupplier;
    }

    public void setNonPublicAccessAllowed(boolean nonPublicAccessAllowed) {
        this.nonPublicAccessAllowed = nonPublicAccessAllowed;
    }

    public boolean isNonPublicAccessAllowed() {
        return this.nonPublicAccessAllowed;
    }

    public void setLenientConstructorResolution(boolean lenientConstructorResolution) {
        this.lenientConstructorResolution = lenientConstructorResolution;
    }

    public boolean isLenientConstructorResolution() {
        return this.lenientConstructorResolution;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setFactoryBeanName(@Nullable String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    @Nullable
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setFactoryMethodName(@Nullable String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    @Nullable
    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }

    public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues = constructorArgumentValues;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public ConstructorArgumentValues getConstructorArgumentValues() {
        ConstructorArgumentValues cav = this.constructorArgumentValues;
        if (cav == null) {
            cav = new ConstructorArgumentValues();
            this.constructorArgumentValues = cav;
        }
        return cav;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public boolean hasConstructorArgumentValues() {
        return (this.constructorArgumentValues == null || this.constructorArgumentValues.isEmpty()) ? false : true;
    }

    public void setPropertyValues(MutablePropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public MutablePropertyValues getPropertyValues() {
        MutablePropertyValues pvs = this.propertyValues;
        if (pvs == null) {
            pvs = new MutablePropertyValues();
            this.propertyValues = pvs;
        }
        return pvs;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public boolean hasPropertyValues() {
        return (this.propertyValues == null || this.propertyValues.isEmpty()) ? false : true;
    }

    public void setMethodOverrides(MethodOverrides methodOverrides) {
        this.methodOverrides = methodOverrides;
    }

    public MethodOverrides getMethodOverrides() {
        return this.methodOverrides;
    }

    public boolean hasMethodOverrides() {
        return !this.methodOverrides.isEmpty();
    }

    public void setInitMethodNames(@Nullable String... initMethodNames) {
        this.initMethodNames = initMethodNames;
    }

    @Nullable
    public String[] getInitMethodNames() {
        return this.initMethodNames;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setInitMethodName(@Nullable String initMethodName) {
        this.initMethodNames = initMethodName != null ? new String[]{initMethodName} : null;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    @Nullable
    public String getInitMethodName() {
        if (ObjectUtils.isEmpty((Object[]) this.initMethodNames)) {
            return null;
        }
        return this.initMethodNames[0];
    }

    public void setEnforceInitMethod(boolean enforceInitMethod) {
        this.enforceInitMethod = enforceInitMethod;
    }

    public boolean isEnforceInitMethod() {
        return this.enforceInitMethod;
    }

    public void setDestroyMethodNames(@Nullable String... destroyMethodNames) {
        this.destroyMethodNames = destroyMethodNames;
    }

    @Nullable
    public String[] getDestroyMethodNames() {
        return this.destroyMethodNames;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setDestroyMethodName(@Nullable String destroyMethodName) {
        this.destroyMethodNames = destroyMethodName != null ? new String[]{destroyMethodName} : null;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    @Nullable
    public String getDestroyMethodName() {
        if (ObjectUtils.isEmpty((Object[]) this.destroyMethodNames)) {
            return null;
        }
        return this.destroyMethodNames[0];
    }

    public void setEnforceDestroyMethod(boolean enforceDestroyMethod) {
        this.enforceDestroyMethod = enforceDestroyMethod;
    }

    public boolean isEnforceDestroyMethod() {
        return this.enforceDestroyMethod;
    }

    public void setSynthetic(boolean synthetic) {
        this.synthetic = synthetic;
    }

    public boolean isSynthetic() {
        return this.synthetic;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setRole(int role) {
        this.role = role;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public int getRole() {
        return this.role;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    @Nullable
    public String getDescription() {
        return this.description;
    }

    public void setResource(@Nullable Resource resource) {
        this.resource = resource;
    }

    @Nullable
    public Resource getResource() {
        return this.resource;
    }

    public void setResourceDescription(@Nullable String resourceDescription) {
        this.resource = resourceDescription != null ? new DescriptiveResource(resourceDescription) : null;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    @Nullable
    public String getResourceDescription() {
        if (this.resource != null) {
            return this.resource.getDescription();
        }
        return null;
    }

    public void setOriginatingBeanDefinition(BeanDefinition originatingBd) {
        this.resource = new BeanDefinitionResource(originatingBd);
    }

    @Override // org.springframework.beans.factory.config.BeanDefinition
    @Nullable
    public BeanDefinition getOriginatingBeanDefinition() {
        Resource resource = this.resource;
        if (!(resource instanceof BeanDefinitionResource)) {
            return null;
        }
        BeanDefinitionResource bdr = (BeanDefinitionResource) resource;
        return bdr.getBeanDefinition();
    }

    public void validate() throws BeanDefinitionValidationException {
        if (hasMethodOverrides() && getFactoryMethodName() != null) {
            throw new BeanDefinitionValidationException("Cannot combine factory method with container-generated method overrides: the factory method must create the concrete bean instance.");
        }
        if (hasBeanClass()) {
            prepareMethodOverrides();
        }
    }

    public void prepareMethodOverrides() throws BeanDefinitionValidationException {
        if (hasMethodOverrides()) {
            getMethodOverrides().getOverrides().forEach(this::prepareMethodOverride);
        }
    }

    protected void prepareMethodOverride(MethodOverride mo) throws BeanDefinitionValidationException {
        int count = ClassUtils.getMethodCountForName(getBeanClass(), mo.getMethodName());
        if (count == 0) {
            throw new BeanDefinitionValidationException("Invalid method override: no method with name '" + mo.getMethodName() + "' on class [" + getBeanClassName() + "]");
        }
        if (count == 1) {
            mo.setOverloaded(false);
        }
    }

    public Object clone() {
        return cloneBeanDefinition();
    }

    @Override // org.springframework.core.AttributeAccessorSupport
    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof AbstractBeanDefinition) {
                AbstractBeanDefinition that = (AbstractBeanDefinition) other;
                if (!ObjectUtils.nullSafeEquals(getBeanClassName(), that.getBeanClassName()) || !ObjectUtils.nullSafeEquals(this.scope, that.scope) || this.abstractFlag != that.abstractFlag || this.lazyInit != that.lazyInit || this.autowireMode != that.autowireMode || this.dependencyCheck != that.dependencyCheck || !Arrays.equals(this.dependsOn, that.dependsOn) || this.autowireCandidate != that.autowireCandidate || !ObjectUtils.nullSafeEquals(this.qualifiers, that.qualifiers) || this.primary != that.primary || this.nonPublicAccessAllowed != that.nonPublicAccessAllowed || this.lenientConstructorResolution != that.lenientConstructorResolution || !equalsConstructorArgumentValues(that) || !equalsPropertyValues(that) || !ObjectUtils.nullSafeEquals(this.methodOverrides, that.methodOverrides) || !ObjectUtils.nullSafeEquals(this.factoryBeanName, that.factoryBeanName) || !ObjectUtils.nullSafeEquals(this.factoryMethodName, that.factoryMethodName) || !ObjectUtils.nullSafeEquals(this.initMethodNames, that.initMethodNames) || this.enforceInitMethod != that.enforceInitMethod || !ObjectUtils.nullSafeEquals(this.destroyMethodNames, that.destroyMethodNames) || this.enforceDestroyMethod != that.enforceDestroyMethod || this.synthetic != that.synthetic || this.role != that.role || !super.equals(other)) {
                }
            }
            return false;
        }
        return true;
    }

    private boolean equalsConstructorArgumentValues(AbstractBeanDefinition other) {
        if (hasConstructorArgumentValues()) {
            return ObjectUtils.nullSafeEquals(this.constructorArgumentValues, other.constructorArgumentValues);
        }
        return !other.hasConstructorArgumentValues();
    }

    private boolean equalsPropertyValues(AbstractBeanDefinition other) {
        if (hasPropertyValues()) {
            return ObjectUtils.nullSafeEquals(this.propertyValues, other.propertyValues);
        }
        return !other.hasPropertyValues();
    }

    @Override // org.springframework.core.AttributeAccessorSupport
    public int hashCode() {
        int hashCode = (29 * ObjectUtils.nullSafeHashCode(getBeanClassName())) + ObjectUtils.nullSafeHashCode(this.scope);
        if (hasConstructorArgumentValues()) {
            hashCode = (29 * hashCode) + ObjectUtils.nullSafeHashCode(this.constructorArgumentValues);
        }
        if (hasPropertyValues()) {
            hashCode = (29 * hashCode) + ObjectUtils.nullSafeHashCode(this.propertyValues);
        }
        return (29 * ((29 * ((29 * hashCode) + ObjectUtils.nullSafeHashCode(this.factoryBeanName))) + ObjectUtils.nullSafeHashCode(this.factoryMethodName))) + super.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("class [");
        sb.append(getBeanClassName()).append(']');
        sb.append("; scope=").append(this.scope);
        sb.append("; abstract=").append(this.abstractFlag);
        sb.append("; lazyInit=").append(this.lazyInit);
        sb.append("; autowireMode=").append(this.autowireMode);
        sb.append("; dependencyCheck=").append(this.dependencyCheck);
        sb.append("; autowireCandidate=").append(this.autowireCandidate);
        sb.append("; primary=").append(this.primary);
        sb.append("; factoryBeanName=").append(this.factoryBeanName);
        sb.append("; factoryMethodName=").append(this.factoryMethodName);
        sb.append("; initMethodNames=").append(Arrays.toString(this.initMethodNames));
        sb.append("; destroyMethodNames=").append(Arrays.toString(this.destroyMethodNames));
        if (this.resource != null) {
            sb.append("; defined in ").append(this.resource.getDescription());
        }
        return sb.toString();
    }
}
