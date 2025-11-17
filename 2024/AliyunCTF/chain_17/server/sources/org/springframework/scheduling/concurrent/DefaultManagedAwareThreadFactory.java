package org.springframework.scheduling.concurrent;

import java.util.Properties;
import java.util.concurrent.ThreadFactory;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.jndi.JndiTemplate;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/DefaultManagedAwareThreadFactory.class */
public class DefaultManagedAwareThreadFactory extends CustomizableThreadFactory implements InitializingBean {
    protected final Log logger = LogFactory.getLog(getClass());
    private final JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate();

    @Nullable
    private String jndiName = "java:comp/DefaultManagedThreadFactory";

    @Nullable
    private ThreadFactory threadFactory;

    public void setJndiTemplate(JndiTemplate jndiTemplate) {
        this.jndiLocator.setJndiTemplate(jndiTemplate);
    }

    public void setJndiEnvironment(Properties jndiEnvironment) {
        this.jndiLocator.setJndiEnvironment(jndiEnvironment);
    }

    public void setResourceRef(boolean resourceRef) {
        this.jndiLocator.setResourceRef(resourceRef);
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws NamingException {
        if (this.jndiName != null) {
            try {
                this.threadFactory = (ThreadFactory) this.jndiLocator.lookup(this.jndiName, ThreadFactory.class);
            } catch (NamingException e) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Failed to retrieve [" + this.jndiName + "] from JNDI", e);
                }
                this.logger.info("Could not find default managed thread factory in JNDI - proceeding with default local thread factory");
            }
        }
    }

    @Override // org.springframework.scheduling.concurrent.CustomizableThreadFactory, java.util.concurrent.ThreadFactory
    public Thread newThread(Runnable runnable) {
        if (this.threadFactory != null) {
            return this.threadFactory.newThread(runnable);
        }
        return super.newThread(runnable);
    }
}
