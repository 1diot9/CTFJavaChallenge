package org.springframework.jmx.export.assembler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import javax.management.Descriptor;
import javax.management.MBeanParameterInfo;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.metadata.InvalidMetadataException;
import org.springframework.jmx.export.metadata.JmxAttributeSource;
import org.springframework.jmx.export.metadata.JmxMetadataUtils;
import org.springframework.jmx.export.metadata.ManagedAttribute;
import org.springframework.jmx.export.metadata.ManagedMetric;
import org.springframework.jmx.export.metadata.ManagedNotification;
import org.springframework.jmx.export.metadata.ManagedOperation;
import org.springframework.jmx.export.metadata.ManagedOperationParameter;
import org.springframework.jmx.export.metadata.ManagedResource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/assembler/MetadataMBeanInfoAssembler.class */
public class MetadataMBeanInfoAssembler extends AbstractReflectiveMBeanInfoAssembler implements AutodetectCapableMBeanInfoAssembler, InitializingBean {

    @Nullable
    private JmxAttributeSource attributeSource;

    public MetadataMBeanInfoAssembler() {
    }

    public MetadataMBeanInfoAssembler(JmxAttributeSource attributeSource) {
        Assert.notNull(attributeSource, "JmxAttributeSource must not be null");
        this.attributeSource = attributeSource;
    }

    public void setAttributeSource(JmxAttributeSource attributeSource) {
        Assert.notNull(attributeSource, "JmxAttributeSource must not be null");
        this.attributeSource = attributeSource;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.attributeSource == null) {
            throw new IllegalArgumentException("Property 'attributeSource' is required");
        }
    }

    private JmxAttributeSource obtainAttributeSource() {
        Assert.state(this.attributeSource != null, "No JmxAttributeSource set");
        return this.attributeSource;
    }

    @Override // org.springframework.jmx.export.assembler.AbstractMBeanInfoAssembler
    protected void checkManagedBean(Object managedBean) throws IllegalArgumentException {
        if (AopUtils.isJdkDynamicProxy(managedBean)) {
            throw new IllegalArgumentException("MetadataMBeanInfoAssembler does not support JDK dynamic proxies - export the target beans directly or use CGLIB proxies instead");
        }
    }

    @Override // org.springframework.jmx.export.assembler.AutodetectCapableMBeanInfoAssembler
    public boolean includeBean(Class<?> beanClass, String beanName) {
        return obtainAttributeSource().getManagedResource(getClassToExpose(beanClass)) != null;
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected boolean includeReadAttribute(Method method, String beanKey) {
        return hasManagedAttribute(method) || hasManagedMetric(method);
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected boolean includeWriteAttribute(Method method, String beanKey) {
        return hasManagedAttribute(method);
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected boolean includeOperation(Method method, String beanKey) {
        PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
        return (pd != null && hasManagedAttribute(method)) || hasManagedOperation(method);
    }

    private boolean hasManagedAttribute(Method method) {
        return obtainAttributeSource().getManagedAttribute(method) != null;
    }

    private boolean hasManagedMetric(Method method) {
        return obtainAttributeSource().getManagedMetric(method) != null;
    }

    private boolean hasManagedOperation(Method method) {
        return obtainAttributeSource().getManagedOperation(method) != null;
    }

    @Override // org.springframework.jmx.export.assembler.AbstractMBeanInfoAssembler
    protected String getDescription(Object managedBean, String beanKey) {
        ManagedResource mr = obtainAttributeSource().getManagedResource(getClassToExpose(managedBean));
        return mr != null ? mr.getDescription() : "";
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected String getAttributeDescription(PropertyDescriptor propertyDescriptor, String beanKey) {
        Method readMethod = propertyDescriptor.getReadMethod();
        Method writeMethod = propertyDescriptor.getWriteMethod();
        ManagedAttribute getter = readMethod != null ? obtainAttributeSource().getManagedAttribute(readMethod) : null;
        ManagedAttribute setter = writeMethod != null ? obtainAttributeSource().getManagedAttribute(writeMethod) : null;
        if (getter != null && StringUtils.hasText(getter.getDescription())) {
            return getter.getDescription();
        }
        if (setter != null && StringUtils.hasText(setter.getDescription())) {
            return setter.getDescription();
        }
        ManagedMetric metric = readMethod != null ? obtainAttributeSource().getManagedMetric(readMethod) : null;
        if (metric != null && StringUtils.hasText(metric.getDescription())) {
            return metric.getDescription();
        }
        return propertyDescriptor.getDisplayName();
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected String getOperationDescription(Method method, String beanKey) {
        PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
        if (pd != null) {
            ManagedAttribute ma = obtainAttributeSource().getManagedAttribute(method);
            if (ma != null && StringUtils.hasText(ma.getDescription())) {
                return ma.getDescription();
            }
            ManagedMetric metric = obtainAttributeSource().getManagedMetric(method);
            if (metric != null && StringUtils.hasText(metric.getDescription())) {
                return metric.getDescription();
            }
            return method.getName();
        }
        ManagedOperation mo = obtainAttributeSource().getManagedOperation(method);
        if (mo != null && StringUtils.hasText(mo.getDescription())) {
            return mo.getDescription();
        }
        return method.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    public MBeanParameterInfo[] getOperationParameters(Method method, String beanKey) {
        ManagedOperationParameter[] params = obtainAttributeSource().getManagedOperationParameters(method);
        if (ObjectUtils.isEmpty((Object[]) params)) {
            return super.getOperationParameters(method, beanKey);
        }
        MBeanParameterInfo[] parameterInfo = new MBeanParameterInfo[params.length];
        Class<?>[] methodParameters = method.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            ManagedOperationParameter param = params[i];
            parameterInfo[i] = new MBeanParameterInfo(param.getName(), methodParameters[i].getName(), param.getDescription());
        }
        return parameterInfo;
    }

    @Override // org.springframework.jmx.export.assembler.AbstractMBeanInfoAssembler
    protected ModelMBeanNotificationInfo[] getNotificationInfo(Object managedBean, String beanKey) {
        ManagedNotification[] notificationAttributes = obtainAttributeSource().getManagedNotifications(getClassToExpose(managedBean));
        ModelMBeanNotificationInfo[] notificationInfos = new ModelMBeanNotificationInfo[notificationAttributes.length];
        for (int i = 0; i < notificationAttributes.length; i++) {
            ManagedNotification attribute = notificationAttributes[i];
            notificationInfos[i] = JmxMetadataUtils.convertToModelMBeanNotificationInfo(attribute);
        }
        return notificationInfos;
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler, org.springframework.jmx.export.assembler.AbstractMBeanInfoAssembler
    protected void populateMBeanDescriptor(Descriptor desc, Object managedBean, String beanKey) {
        ManagedResource mr = obtainAttributeSource().getManagedResource(getClassToExpose(managedBean));
        if (mr == null) {
            throw new InvalidMetadataException("No ManagedResource attribute found for class: " + getClassToExpose(managedBean));
        }
        applyCurrencyTimeLimit(desc, mr.getCurrencyTimeLimit());
        if (mr.isLog()) {
            desc.setField("log", "true");
        }
        if (StringUtils.hasLength(mr.getLogFile())) {
            desc.setField("logFile", mr.getLogFile());
        }
        if (StringUtils.hasLength(mr.getPersistPolicy())) {
            desc.setField("persistPolicy", mr.getPersistPolicy());
        }
        if (mr.getPersistPeriod() >= 0) {
            desc.setField("persistPeriod", Integer.toString(mr.getPersistPeriod()));
        }
        if (StringUtils.hasLength(mr.getPersistName())) {
            desc.setField("persistName", mr.getPersistName());
        }
        if (StringUtils.hasLength(mr.getPersistLocation())) {
            desc.setField("persistLocation", mr.getPersistLocation());
        }
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected void populateAttributeDescriptor(Descriptor desc, @Nullable Method getter, @Nullable Method setter, String beanKey) {
        ManagedMetric metric;
        if (getter != null && (metric = obtainAttributeSource().getManagedMetric(getter)) != null) {
            populateMetricDescriptor(desc, metric);
            return;
        }
        ManagedAttribute gma = getter != null ? obtainAttributeSource().getManagedAttribute(getter) : null;
        ManagedAttribute sma = setter != null ? obtainAttributeSource().getManagedAttribute(setter) : null;
        populateAttributeDescriptor(desc, gma != null ? gma : ManagedAttribute.EMPTY, sma != null ? sma : ManagedAttribute.EMPTY);
    }

    private void populateAttributeDescriptor(Descriptor desc, ManagedAttribute gma, ManagedAttribute sma) {
        applyCurrencyTimeLimit(desc, resolveIntDescriptor(gma.getCurrencyTimeLimit(), sma.getCurrencyTimeLimit()));
        Object defaultValue = resolveObjectDescriptor(gma.getDefaultValue(), sma.getDefaultValue());
        desc.setField("default", defaultValue);
        String persistPolicy = resolveStringDescriptor(gma.getPersistPolicy(), sma.getPersistPolicy());
        if (StringUtils.hasLength(persistPolicy)) {
            desc.setField("persistPolicy", persistPolicy);
        }
        int persistPeriod = resolveIntDescriptor(gma.getPersistPeriod(), sma.getPersistPeriod());
        if (persistPeriod >= 0) {
            desc.setField("persistPeriod", Integer.toString(persistPeriod));
        }
    }

    private void populateMetricDescriptor(Descriptor desc, ManagedMetric metric) {
        applyCurrencyTimeLimit(desc, metric.getCurrencyTimeLimit());
        if (StringUtils.hasLength(metric.getPersistPolicy())) {
            desc.setField("persistPolicy", metric.getPersistPolicy());
        }
        if (metric.getPersistPeriod() >= 0) {
            desc.setField("persistPeriod", Integer.toString(metric.getPersistPeriod()));
        }
        if (StringUtils.hasLength(metric.getDisplayName())) {
            desc.setField("displayName", metric.getDisplayName());
        }
        if (StringUtils.hasLength(metric.getUnit())) {
            desc.setField("units", metric.getUnit());
        }
        if (StringUtils.hasLength(metric.getCategory())) {
            desc.setField("metricCategory", metric.getCategory());
        }
        desc.setField("metricType", metric.getMetricType().toString());
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected void populateOperationDescriptor(Descriptor desc, Method method, String beanKey) {
        ManagedOperation mo = obtainAttributeSource().getManagedOperation(method);
        if (mo != null) {
            applyCurrencyTimeLimit(desc, mo.getCurrencyTimeLimit());
        }
    }

    private int resolveIntDescriptor(int getter, int setter) {
        return Math.max(getter, setter);
    }

    @Nullable
    private Object resolveObjectDescriptor(@Nullable Object getter, @Nullable Object setter) {
        return getter != null ? getter : setter;
    }

    @Nullable
    private String resolveStringDescriptor(@Nullable String getter, @Nullable String setter) {
        return StringUtils.hasLength(getter) ? getter : setter;
    }
}
