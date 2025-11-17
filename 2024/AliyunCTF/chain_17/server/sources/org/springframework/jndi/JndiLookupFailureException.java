package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.core.NestedRuntimeException;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jndi/JndiLookupFailureException.class */
public class JndiLookupFailureException extends NestedRuntimeException {
    public JndiLookupFailureException(String msg, NamingException cause) {
        super(msg, cause);
    }
}
