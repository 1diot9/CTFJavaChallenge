package org.apache.naming.factory;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.ResourceLinkRef;
import org.apache.naming.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/factory/ResourceLinkFactory.class */
public class ResourceLinkFactory implements ObjectFactory {
    protected static final StringManager sm = StringManager.getManager((Class<?>) ResourceLinkFactory.class);
    private static Context globalContext = null;
    private static Map<ClassLoader, Map<String, String>> globalResourceRegistrations = new ConcurrentHashMap();

    public static void setGlobalContext(Context newGlobalContext) {
        SecurityManager sm2 = System.getSecurityManager();
        if (sm2 != null) {
            sm2.checkPermission(new RuntimePermission(ResourceLinkFactory.class.getName() + ".setGlobalContext"));
        }
        globalContext = newGlobalContext;
    }

    public static void registerGlobalResourceAccess(Context globalContext2, String localName, String globalName) {
        validateGlobalContext(globalContext2);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        globalResourceRegistrations.computeIfAbsent(cl, k -> {
            return new HashMap();
        }).put(localName, globalName);
    }

    public static void deregisterGlobalResourceAccess(Context globalContext2, String localName) {
        validateGlobalContext(globalContext2);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Map<String, String> registrations = globalResourceRegistrations.get(cl);
        if (registrations != null) {
            registrations.remove(localName);
        }
    }

    public static void deregisterGlobalResourceAccess(Context globalContext2) {
        validateGlobalContext(globalContext2);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        globalResourceRegistrations.remove(cl);
    }

    private static void validateGlobalContext(Context globalContext2) {
        if (globalContext != null && globalContext != globalContext2) {
            throw new SecurityException(sm.getString("resourceLinkFactory.invalidGlobalContext"));
        }
    }

    private static boolean validateGlobalResourceAccess(String globalName) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        while (true) {
            ClassLoader cl = contextClassLoader;
            if (cl != null) {
                Map<String, String> registrations = globalResourceRegistrations.get(cl);
                if (registrations != null && registrations.containsValue(globalName)) {
                    return true;
                }
                contextClassLoader = cl.getParent();
            } else {
                return false;
            }
        }
    }

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws NamingException {
        Reference ref;
        RefAddr refAddr;
        if ((obj instanceof ResourceLinkRef) && (refAddr = (ref = (Reference) obj).get(ResourceLinkRef.GLOBALNAME)) != null) {
            String globalName = refAddr.getContent().toString();
            if (!validateGlobalResourceAccess(globalName)) {
                return null;
            }
            Object result = globalContext.lookup(globalName);
            String expectedClassName = ref.getClassName();
            if (expectedClassName == null) {
                throw new IllegalArgumentException(sm.getString("resourceLinkFactory.nullType", name, globalName));
            }
            try {
                Class<?> expectedClazz = Class.forName(expectedClassName, true, Thread.currentThread().getContextClassLoader());
                if (!expectedClazz.isAssignableFrom(result.getClass())) {
                    throw new IllegalArgumentException(sm.getString("resourceLinkFactory.wrongType", name, globalName, expectedClassName, result.getClass().getName()));
                }
                return result;
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(sm.getString("resourceLinkFactory.unknownType", name, globalName, expectedClassName), e);
            }
        }
        return null;
    }
}
