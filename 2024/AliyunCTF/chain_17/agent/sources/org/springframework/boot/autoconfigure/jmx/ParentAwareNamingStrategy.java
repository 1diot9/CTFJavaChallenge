package org.springframework.boot.autoconfigure.jmx;

import java.util.Hashtable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jmx.export.metadata.JmxAttributeSource;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jmx/ParentAwareNamingStrategy.class */
public class ParentAwareNamingStrategy extends MetadataNamingStrategy implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private boolean ensureUniqueRuntimeObjectNames;

    public ParentAwareNamingStrategy(JmxAttributeSource attributeSource) {
        super(attributeSource);
    }

    public void setEnsureUniqueRuntimeObjectNames(boolean ensureUniqueRuntimeObjectNames) {
        this.ensureUniqueRuntimeObjectNames = ensureUniqueRuntimeObjectNames;
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.jmx.export.naming.MetadataNamingStrategy, org.springframework.jmx.export.naming.ObjectNamingStrategy
    public ObjectName getObjectName(Object managedBean, String beanKey) throws MalformedObjectNameException {
        ObjectName name = super.getObjectName(managedBean, beanKey);
        if (this.ensureUniqueRuntimeObjectNames) {
            return JmxUtils.appendIdentityToObjectName(name, managedBean);
        }
        if (parentContextContainsSameBean(this.applicationContext, beanKey)) {
            return appendToObjectName(name, "context", ObjectUtils.getIdentityHexString(this.applicationContext));
        }
        return name;
    }

    private boolean parentContextContainsSameBean(ApplicationContext context, String beanKey) {
        if (context.getParent() == null) {
            return false;
        }
        try {
            this.applicationContext.getParent().getBean(beanKey);
            return true;
        } catch (BeansException e) {
            return parentContextContainsSameBean(context.getParent(), beanKey);
        }
    }

    private ObjectName appendToObjectName(ObjectName name, String key, String value) throws MalformedObjectNameException {
        Hashtable<String, String> keyProperties = name.getKeyPropertyList();
        keyProperties.put(key, value);
        return ObjectNameManager.getInstance(name.getDomain(), keyProperties);
    }
}
