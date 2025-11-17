package org.apache.catalina.mbeans;

import java.util.Iterator;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import org.apache.catalina.Group;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Role;
import org.apache.catalina.Server;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/GlobalResourcesLifecycleListener.class */
public class GlobalResourcesLifecycleListener implements LifecycleListener {
    private static final Log log = LogFactory.getLog((Class<?>) GlobalResourcesLifecycleListener.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) GlobalResourcesLifecycleListener.class);
    protected Lifecycle component = null;

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        if (Lifecycle.START_EVENT.equals(event.getType())) {
            if (!(event.getLifecycle() instanceof Server)) {
                log.warn(sm.getString("listener.notServer", event.getLifecycle().getClass().getSimpleName()));
            }
            this.component = event.getLifecycle();
            createMBeans();
            return;
        }
        if (Lifecycle.STOP_EVENT.equals(event.getType())) {
            destroyMBeans();
            this.component = null;
        }
    }

    protected void createMBeans() {
        try {
            Context context = (Context) new InitialContext().lookup("java:/");
            try {
                createMBeans("", context);
            } catch (NamingException e) {
                log.error(sm.getString("globalResources.createError"), e);
            }
        } catch (NamingException e2) {
            log.error(sm.getString("globalResources.noNamingContext"));
        }
    }

    protected void createMBeans(String prefix, Context context) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug("Creating MBeans for Global JNDI Resources in Context '" + prefix + "'");
        }
        try {
            NamingEnumeration<Binding> bindings = context.listBindings("");
            while (bindings.hasMore()) {
                Binding binding = (Binding) bindings.next();
                String name = prefix + binding.getName();
                Object value = context.lookup(binding.getName());
                if (log.isDebugEnabled()) {
                    log.debug("Checking resource " + name);
                }
                if (value instanceof Context) {
                    createMBeans(name + "/", (Context) value);
                } else if (value instanceof UserDatabase) {
                    try {
                        createMBeans(name, (UserDatabase) value);
                    } catch (Exception e) {
                        log.error(sm.getString("globalResources.userDatabaseCreateError", name), e);
                    }
                }
            }
        } catch (OperationNotSupportedException e2) {
            log.error(sm.getString("globalResources.createError.operation"), e2);
        } catch (RuntimeException ex) {
            log.error(sm.getString("globalResources.createError.runtime"), ex);
        }
    }

    protected void createMBeans(String name, UserDatabase database) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Creating UserDatabase MBeans for resource " + name);
            log.debug("Database=" + database);
        }
        try {
            MBeanUtils.createMBean(database);
            if (database.isSparse()) {
                return;
            }
            Iterator<Role> roles = database.getRoles();
            while (roles.hasNext()) {
                Role role = roles.next();
                if (log.isDebugEnabled()) {
                    log.debug("  Creating Role MBean for role " + role);
                }
                try {
                    MBeanUtils.createMBean(role);
                } catch (Exception e) {
                    throw new IllegalArgumentException(sm.getString("globalResources.createError.userDatabase.role", role), e);
                }
            }
            Iterator<Group> groups = database.getGroups();
            while (groups.hasNext()) {
                Group group = groups.next();
                if (log.isDebugEnabled()) {
                    log.debug("  Creating Group MBean for group " + group);
                }
                try {
                    MBeanUtils.createMBean(group);
                } catch (Exception e2) {
                    throw new IllegalArgumentException(sm.getString("globalResources.createError.userDatabase.group", group), e2);
                }
            }
            Iterator<User> users = database.getUsers();
            while (users.hasNext()) {
                User user = users.next();
                if (log.isDebugEnabled()) {
                    log.debug("  Creating User MBean for user " + user);
                }
                try {
                    MBeanUtils.createMBean(user);
                } catch (Exception e3) {
                    throw new IllegalArgumentException(sm.getString("globalResources.createError.userDatabase.user", user), e3);
                }
            }
        } catch (Exception e4) {
            throw new IllegalArgumentException(sm.getString("globalResources.createError.userDatabase", name), e4);
        }
    }

    protected void destroyMBeans() {
        if (log.isDebugEnabled()) {
            log.debug("Destroying MBeans for Global JNDI Resources");
        }
    }
}
