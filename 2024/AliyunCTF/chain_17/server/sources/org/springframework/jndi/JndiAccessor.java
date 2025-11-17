package org.springframework.jndi;

import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jndi/JndiAccessor.class */
public class JndiAccessor {
    protected final Log logger = LogFactory.getLog(getClass());
    private JndiTemplate jndiTemplate = new JndiTemplate();

    public void setJndiTemplate(@Nullable JndiTemplate jndiTemplate) {
        this.jndiTemplate = jndiTemplate != null ? jndiTemplate : new JndiTemplate();
    }

    public JndiTemplate getJndiTemplate() {
        return this.jndiTemplate;
    }

    public void setJndiEnvironment(@Nullable Properties jndiEnvironment) {
        this.jndiTemplate = new JndiTemplate(jndiEnvironment);
    }

    @Nullable
    public Properties getJndiEnvironment() {
        return this.jndiTemplate.getEnvironment();
    }
}
