package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jndi/JndiLocatorSupport.class */
public abstract class JndiLocatorSupport extends JndiAccessor {
    public static final String CONTAINER_PREFIX = "java:comp/env/";
    private boolean resourceRef = false;

    public void setResourceRef(boolean resourceRef) {
        this.resourceRef = resourceRef;
    }

    public boolean isResourceRef() {
        return this.resourceRef;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object lookup(String jndiName) throws NamingException {
        return lookup(jndiName, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <T> T lookup(String str, @Nullable Class<T> cls) throws NamingException {
        Object lookup;
        Assert.notNull(str, "'jndiName' must not be null");
        String convertJndiName = convertJndiName(str);
        try {
            lookup = getJndiTemplate().lookup(convertJndiName, cls);
        } catch (NamingException e) {
            if (!convertJndiName.equals(str)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Converted JNDI name [" + convertJndiName + "] not found - trying original name [" + str + "]. " + e);
                }
                lookup = getJndiTemplate().lookup(str, cls);
            } else {
                throw e;
            }
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Located object with JNDI name [" + convertJndiName + "]");
        }
        return (T) lookup;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String convertJndiName(String jndiName) {
        if (isResourceRef() && !jndiName.startsWith(CONTAINER_PREFIX) && jndiName.indexOf(58) == -1) {
            jndiName = "java:comp/env/" + jndiName;
        }
        return jndiName;
    }
}
