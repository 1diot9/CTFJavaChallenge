package org.springframework.scheduling.concurrent;

import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import javax.naming.NamingException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.jndi.JndiTemplate;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/DefaultManagedTaskScheduler.class */
public class DefaultManagedTaskScheduler extends ConcurrentTaskScheduler implements InitializingBean {
    private final JndiLocatorDelegate jndiLocator;
    private String jndiName;

    public DefaultManagedTaskScheduler() {
        super(null);
        this.jndiLocator = new JndiLocatorDelegate();
        this.jndiName = "java:comp/DefaultManagedScheduledExecutorService";
    }

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
        ScheduledExecutorService executor = (ScheduledExecutorService) this.jndiLocator.lookup(this.jndiName, ScheduledExecutorService.class);
        setConcurrentExecutor(executor);
        setScheduledExecutor(executor);
    }
}
