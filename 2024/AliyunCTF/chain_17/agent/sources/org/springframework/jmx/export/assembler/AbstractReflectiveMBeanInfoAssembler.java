package org.springframework.jmx.export.assembler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.management.Descriptor;
import javax.management.JMException;
import javax.management.MBeanParameterInfo;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/assembler/AbstractReflectiveMBeanInfoAssembler.class */
public abstract class AbstractReflectiveMBeanInfoAssembler extends AbstractMBeanInfoAssembler {
    protected static final String FIELD_GET_METHOD = "getMethod";
    protected static final String FIELD_SET_METHOD = "setMethod";
    protected static final String FIELD_ROLE = "role";
    protected static final String ROLE_GETTER = "getter";
    protected static final String ROLE_SETTER = "setter";
    protected static final String ROLE_OPERATION = "operation";
    protected static final String FIELD_VISIBILITY = "visibility";
    protected static final int ATTRIBUTE_OPERATION_VISIBILITY = 4;
    protected static final String FIELD_CLASS = "class";
    protected static final String FIELD_LOG = "log";
    protected static final String FIELD_LOG_FILE = "logFile";
    protected static final String FIELD_CURRENCY_TIME_LIMIT = "currencyTimeLimit";
    protected static final String FIELD_DEFAULT = "default";
    protected static final String FIELD_PERSIST_POLICY = "persistPolicy";
    protected static final String FIELD_PERSIST_PERIOD = "persistPeriod";
    protected static final String FIELD_PERSIST_LOCATION = "persistLocation";
    protected static final String FIELD_PERSIST_NAME = "persistName";
    protected static final String FIELD_DISPLAY_NAME = "displayName";
    protected static final String FIELD_UNITS = "units";
    protected static final String FIELD_METRIC_TYPE = "metricType";
    protected static final String FIELD_METRIC_CATEGORY = "metricCategory";

    @Nullable
    private Integer defaultCurrencyTimeLimit;
    private boolean useStrictCasing = true;
    private boolean exposeClassDescriptor = false;

    @Nullable
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    protected abstract boolean includeReadAttribute(Method method, String beanKey);

    protected abstract boolean includeWriteAttribute(Method method, String beanKey);

    protected abstract boolean includeOperation(Method method, String beanKey);

    public void setDefaultCurrencyTimeLimit(@Nullable Integer defaultCurrencyTimeLimit) {
        this.defaultCurrencyTimeLimit = defaultCurrencyTimeLimit;
    }

    @Nullable
    protected Integer getDefaultCurrencyTimeLimit() {
        return this.defaultCurrencyTimeLimit;
    }

    public void setUseStrictCasing(boolean useStrictCasing) {
        this.useStrictCasing = useStrictCasing;
    }

    protected boolean isUseStrictCasing() {
        return this.useStrictCasing;
    }

    public void setExposeClassDescriptor(boolean exposeClassDescriptor) {
        this.exposeClassDescriptor = exposeClassDescriptor;
    }

    protected boolean isExposeClassDescriptor() {
        return this.exposeClassDescriptor;
    }

    public void setParameterNameDiscoverer(@Nullable ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Nullable
    protected ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }

    @Override // org.springframework.jmx.export.assembler.AbstractMBeanInfoAssembler
    protected ModelMBeanAttributeInfo[] getAttributeInfo(Object managedBean, String beanKey) throws JMException {
        PropertyDescriptor[] props = BeanUtils.getPropertyDescriptors(getClassToExpose(managedBean));
        List<ModelMBeanAttributeInfo> infos = new ArrayList<>();
        for (PropertyDescriptor prop : props) {
            Method getter = prop.getReadMethod();
            if (getter == null || getter.getDeclaringClass() != Object.class) {
                if (getter != null && !includeReadAttribute(getter, beanKey)) {
                    getter = null;
                }
                Method setter = prop.getWriteMethod();
                if (setter != null && !includeWriteAttribute(setter, beanKey)) {
                    setter = null;
                }
                if (getter != null || setter != null) {
                    String attrName = JmxUtils.getAttributeName(prop, isUseStrictCasing());
                    String description = getAttributeDescription(prop, beanKey);
                    ModelMBeanAttributeInfo info = new ModelMBeanAttributeInfo(attrName, description, getter, setter);
                    Descriptor desc = info.getDescriptor();
                    if (getter != null) {
                        desc.setField(FIELD_GET_METHOD, getter.getName());
                    }
                    if (setter != null) {
                        desc.setField(FIELD_SET_METHOD, setter.getName());
                    }
                    populateAttributeDescriptor(desc, getter, setter, beanKey);
                    info.setDescriptor(desc);
                    infos.add(info);
                }
            }
        }
        return (ModelMBeanAttributeInfo[]) infos.toArray(new ModelMBeanAttributeInfo[0]);
    }

    @Override // org.springframework.jmx.export.assembler.AbstractMBeanInfoAssembler
    protected ModelMBeanOperationInfo[] getOperationInfo(Object managedBean, String beanKey) {
        Method[] methods = getClassToExpose(managedBean).getMethods();
        List<ModelMBeanOperationInfo> infos = new ArrayList<>();
        for (Method method : methods) {
            if (!method.isSynthetic() && Object.class != method.getDeclaringClass()) {
                ModelMBeanOperationInfo info = null;
                PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
                if (pd != null && ((method.equals(pd.getReadMethod()) && includeReadAttribute(method, beanKey)) || (method.equals(pd.getWriteMethod()) && includeWriteAttribute(method, beanKey)))) {
                    info = createModelMBeanOperationInfo(method, pd.getName(), beanKey);
                    Descriptor desc = info.getDescriptor();
                    if (method.equals(pd.getReadMethod())) {
                        desc.setField(FIELD_ROLE, ROLE_GETTER);
                    } else {
                        desc.setField(FIELD_ROLE, ROLE_SETTER);
                    }
                    desc.setField(FIELD_VISIBILITY, 4);
                    if (isExposeClassDescriptor()) {
                        desc.setField("class", getClassForDescriptor(managedBean).getName());
                    }
                    info.setDescriptor(desc);
                }
                if (info == null && includeOperation(method, beanKey)) {
                    info = createModelMBeanOperationInfo(method, method.getName(), beanKey);
                    Descriptor desc2 = info.getDescriptor();
                    desc2.setField(FIELD_ROLE, ROLE_OPERATION);
                    if (isExposeClassDescriptor()) {
                        desc2.setField("class", getClassForDescriptor(managedBean).getName());
                    }
                    populateOperationDescriptor(desc2, method, beanKey);
                    info.setDescriptor(desc2);
                }
                if (info != null) {
                    infos.add(info);
                }
            }
        }
        return (ModelMBeanOperationInfo[]) infos.toArray(new ModelMBeanOperationInfo[0]);
    }

    protected ModelMBeanOperationInfo createModelMBeanOperationInfo(Method method, String name, String beanKey) {
        MBeanParameterInfo[] params = getOperationParameters(method, beanKey);
        if (params.length == 0) {
            return new ModelMBeanOperationInfo(getOperationDescription(method, beanKey), method);
        }
        return new ModelMBeanOperationInfo(method.getName(), getOperationDescription(method, beanKey), getOperationParameters(method, beanKey), method.getReturnType().getName(), 3);
    }

    protected Class<?> getClassForDescriptor(Object managedBean) {
        if (AopUtils.isJdkDynamicProxy(managedBean)) {
            return AopProxyUtils.proxiedUserInterfaces(managedBean)[0];
        }
        return getClassToExpose(managedBean);
    }

    protected String getAttributeDescription(PropertyDescriptor propertyDescriptor, String beanKey) {
        return propertyDescriptor.getDisplayName();
    }

    protected String getOperationDescription(Method method, String beanKey) {
        return method.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MBeanParameterInfo[] getOperationParameters(Method method, String beanKey) {
        ParameterNameDiscoverer paramNameDiscoverer = getParameterNameDiscoverer();
        String[] paramNames = paramNameDiscoverer != null ? paramNameDiscoverer.getParameterNames(method) : null;
        if (paramNames == null) {
            return new MBeanParameterInfo[0];
        }
        MBeanParameterInfo[] info = new MBeanParameterInfo[paramNames.length];
        Class<?>[] typeParameters = method.getParameterTypes();
        for (int i = 0; i < info.length; i++) {
            info[i] = new MBeanParameterInfo(paramNames[i], typeParameters[i].getName(), paramNames[i]);
        }
        return info;
    }

    @Override // org.springframework.jmx.export.assembler.AbstractMBeanInfoAssembler
    protected void populateMBeanDescriptor(Descriptor descriptor, Object managedBean, String beanKey) {
        applyDefaultCurrencyTimeLimit(descriptor);
    }

    protected void populateAttributeDescriptor(Descriptor desc, @Nullable Method getter, @Nullable Method setter, String beanKey) {
        applyDefaultCurrencyTimeLimit(desc);
    }

    protected void populateOperationDescriptor(Descriptor desc, Method method, String beanKey) {
        applyDefaultCurrencyTimeLimit(desc);
    }

    protected final void applyDefaultCurrencyTimeLimit(Descriptor desc) {
        if (getDefaultCurrencyTimeLimit() != null) {
            desc.setField(FIELD_CURRENCY_TIME_LIMIT, getDefaultCurrencyTimeLimit().toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void applyCurrencyTimeLimit(Descriptor desc, int currencyTimeLimit) {
        if (currencyTimeLimit > 0) {
            desc.setField(FIELD_CURRENCY_TIME_LIMIT, Integer.toString(currencyTimeLimit));
        } else if (currencyTimeLimit == 0) {
            desc.setField(FIELD_CURRENCY_TIME_LIMIT, Integer.toString(Integer.MAX_VALUE));
        } else {
            applyDefaultCurrencyTimeLimit(desc);
        }
    }
}
