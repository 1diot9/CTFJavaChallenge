package org.springframework.jmx.export.assembler;

import javax.management.Descriptor;
import javax.management.JMException;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import org.springframework.aop.support.AopUtils;
import org.springframework.jmx.support.JmxUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/assembler/AbstractMBeanInfoAssembler.class */
public abstract class AbstractMBeanInfoAssembler implements MBeanInfoAssembler {
    protected abstract ModelMBeanAttributeInfo[] getAttributeInfo(Object managedBean, String beanKey) throws JMException;

    protected abstract ModelMBeanOperationInfo[] getOperationInfo(Object managedBean, String beanKey) throws JMException;

    @Override // org.springframework.jmx.export.assembler.MBeanInfoAssembler
    public ModelMBeanInfo getMBeanInfo(Object managedBean, String beanKey) throws JMException {
        checkManagedBean(managedBean);
        ModelMBeanInfoSupport modelMBeanInfoSupport = new ModelMBeanInfoSupport(getClassName(managedBean, beanKey), getDescription(managedBean, beanKey), getAttributeInfo(managedBean, beanKey), getConstructorInfo(managedBean, beanKey), getOperationInfo(managedBean, beanKey), getNotificationInfo(managedBean, beanKey));
        Descriptor desc = modelMBeanInfoSupport.getMBeanDescriptor();
        populateMBeanDescriptor(desc, managedBean, beanKey);
        modelMBeanInfoSupport.setMBeanDescriptor(desc);
        return modelMBeanInfoSupport;
    }

    protected void checkManagedBean(Object managedBean) throws IllegalArgumentException {
    }

    protected Class<?> getTargetClass(Object managedBean) {
        return AopUtils.getTargetClass(managedBean);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Class<?> getClassToExpose(Object managedBean) {
        return JmxUtils.getClassToExpose(managedBean);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Class<?> getClassToExpose(Class<?> beanClass) {
        return JmxUtils.getClassToExpose(beanClass);
    }

    protected String getClassName(Object managedBean, String beanKey) throws JMException {
        return getTargetClass(managedBean).getName();
    }

    protected String getDescription(Object managedBean, String beanKey) throws JMException {
        String targetClassName = getTargetClass(managedBean).getName();
        if (AopUtils.isAopProxy(managedBean)) {
            return "Proxy for " + targetClassName;
        }
        return targetClassName;
    }

    protected void populateMBeanDescriptor(Descriptor descriptor, Object managedBean, String beanKey) throws JMException {
    }

    protected ModelMBeanConstructorInfo[] getConstructorInfo(Object managedBean, String beanKey) throws JMException {
        return new ModelMBeanConstructorInfo[0];
    }

    protected ModelMBeanNotificationInfo[] getNotificationInfo(Object managedBean, String beanKey) throws JMException {
        return new ModelMBeanNotificationInfo[0];
    }
}
