package org.springframework.jndi;

import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jndi/JndiTemplate.class */
public class JndiTemplate {
    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private Properties environment;

    public JndiTemplate() {
    }

    public JndiTemplate(@Nullable Properties environment) {
        this.environment = environment;
    }

    public void setEnvironment(@Nullable Properties environment) {
        this.environment = environment;
    }

    @Nullable
    public Properties getEnvironment() {
        return this.environment;
    }

    @Nullable
    public <T> T execute(JndiCallback<T> contextCallback) throws NamingException {
        Context ctx = getContext();
        try {
            T doInContext = contextCallback.doInContext(ctx);
            releaseContext(ctx);
            return doInContext;
        } catch (Throwable th) {
            releaseContext(ctx);
            throw th;
        }
    }

    public Context getContext() throws NamingException {
        return createInitialContext();
    }

    public void releaseContext(@Nullable Context ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (NamingException e) {
                this.logger.debug("Could not close JNDI InitialContext", e);
            }
        }
    }

    protected Context createInitialContext() throws NamingException {
        Hashtable<?, ?> icEnv = null;
        Properties env = getEnvironment();
        if (env != null) {
            icEnv = new Hashtable<>(env.size());
            CollectionUtils.mergePropertiesIntoMap(env, icEnv);
        }
        return new InitialContext(icEnv);
    }

    public Object lookup(final String name) throws NamingException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Looking up JNDI object with name [" + name + "]");
        }
        Object result = execute(ctx -> {
            return ctx.lookup(name);
        });
        if (result == null) {
            throw new NameNotFoundException("JNDI object with [" + name + "] not found: JNDI implementation returned null");
        }
        return result;
    }

    public <T> T lookup(String str, @Nullable Class<T> cls) throws NamingException {
        T t = (T) lookup(str);
        if (cls != null && !cls.isInstance(t)) {
            throw new TypeMismatchNamingException(str, cls, t.getClass());
        }
        return t;
    }

    public void bind(final String name, final Object object) throws NamingException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Binding JNDI object with name [" + name + "]");
        }
        execute(ctx -> {
            ctx.bind(name, object);
            return null;
        });
    }

    public void rebind(final String name, final Object object) throws NamingException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Rebinding JNDI object with name [" + name + "]");
        }
        execute(ctx -> {
            ctx.rebind(name, object);
            return null;
        });
    }

    public void unbind(final String name) throws NamingException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Unbinding JNDI object with name [" + name + "]");
        }
        execute(ctx -> {
            ctx.unbind(name);
            return null;
        });
    }
}
