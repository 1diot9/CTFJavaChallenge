package org.springframework.jndi;

import javax.naming.Context;
import javax.naming.NamingException;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jndi/JndiCallback.class */
public interface JndiCallback<T> {
    @Nullable
    T doInContext(Context ctx) throws NamingException;
}
