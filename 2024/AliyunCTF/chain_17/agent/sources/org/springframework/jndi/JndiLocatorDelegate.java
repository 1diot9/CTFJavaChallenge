package org.springframework.jndi;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.springframework.core.SpringProperties;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jndi/JndiLocatorDelegate.class */
public class JndiLocatorDelegate extends JndiLocatorSupport {
    public static final String IGNORE_JNDI_PROPERTY_NAME = "spring.jndi.ignore";
    private static final boolean shouldIgnoreDefaultJndiEnvironment = SpringProperties.getFlag(IGNORE_JNDI_PROPERTY_NAME);

    @Override // org.springframework.jndi.JndiLocatorSupport
    public Object lookup(String jndiName) throws NamingException {
        return super.lookup(jndiName);
    }

    @Override // org.springframework.jndi.JndiLocatorSupport
    public <T> T lookup(String str, @Nullable Class<T> cls) throws NamingException {
        return (T) super.lookup(str, cls);
    }

    public static JndiLocatorDelegate createDefaultResourceRefLocator() {
        JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate();
        jndiLocator.setResourceRef(true);
        return jndiLocator;
    }

    public static boolean isDefaultJndiEnvironmentAvailable() {
        if (shouldIgnoreDefaultJndiEnvironment) {
            return false;
        }
        try {
            new InitialContext().getEnvironment();
            return true;
        } catch (Throwable th) {
            return false;
        }
    }
}
