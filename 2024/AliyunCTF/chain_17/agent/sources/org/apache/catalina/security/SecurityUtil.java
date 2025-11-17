package org.apache.catalina.security;

import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.security.auth.Subject;
import org.apache.catalina.Globals;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/security/SecurityUtil.class */
public final class SecurityUtil {
    private static final int INIT = 0;
    private static final int SERVICE = 1;
    private static final int DOFILTER = 1;
    private static final int EVENT = 2;
    private static final int DOFILTEREVENT = 2;
    private static final int DESTROY = 3;
    private static final String INIT_METHOD = "init";
    private static final String DOFILTER_METHOD = "doFilter";
    private static final String SERVICE_METHOD = "service";
    private static final String EVENT_METHOD = "event";
    private static final String DOFILTEREVENT_METHOD = "doFilterEvent";
    private static final String DESTROY_METHOD = "destroy";
    private static final Map<Class<?>, Method[]> classCache = new ConcurrentHashMap();
    private static final Log log = LogFactory.getLog((Class<?>) SecurityUtil.class);
    private static final boolean packageDefinitionEnabled;
    private static final StringManager sm;

    static {
        packageDefinitionEnabled = (System.getProperty("package.definition") == null && System.getProperty("package.access") == null) ? false : true;
        sm = StringManager.getManager(Constants.PACKAGE);
    }

    public static void doAsPrivilege(String methodName, Servlet targetObject) throws Exception {
        doAsPrivilege(methodName, targetObject, (Class<?>[]) null, (Object[]) null, (Principal) null);
    }

    public static void doAsPrivilege(String methodName, Servlet targetObject, Class<?>[] targetType, Object[] targetArguments) throws Exception {
        doAsPrivilege(methodName, targetObject, targetType, targetArguments, (Principal) null);
    }

    public static void doAsPrivilege(String methodName, Servlet targetObject, Class<?>[] targetParameterTypes, Object[] targetArguments, Principal principal) throws Exception {
        Method method;
        Method[] methodsCache = classCache.get(Servlet.class);
        if (methodsCache == null) {
            method = createMethodAndCacheIt(null, Servlet.class, methodName, targetParameterTypes);
        } else {
            method = findMethod(methodsCache, methodName);
            if (method == null) {
                method = createMethodAndCacheIt(methodsCache, Servlet.class, methodName, targetParameterTypes);
            }
        }
        execute(method, targetObject, targetArguments, principal);
    }

    public static void doAsPrivilege(String methodName, Filter targetObject) throws Exception {
        doAsPrivilege(methodName, targetObject, (Class<?>[]) null, (Object[]) null);
    }

    public static void doAsPrivilege(String methodName, Filter targetObject, Class<?>[] targetType, Object[] targetArguments) throws Exception {
        doAsPrivilege(methodName, targetObject, targetType, targetArguments, (Principal) null);
    }

    public static void doAsPrivilege(String methodName, Filter targetObject, Class<?>[] targetParameterTypes, Object[] targetParameterValues, Principal principal) throws Exception {
        Method method;
        Method[] methodsCache = classCache.get(Filter.class);
        if (methodsCache == null) {
            method = createMethodAndCacheIt(null, Filter.class, methodName, targetParameterTypes);
        } else {
            method = findMethod(methodsCache, methodName);
            if (method == null) {
                method = createMethodAndCacheIt(methodsCache, Filter.class, methodName, targetParameterTypes);
            }
        }
        execute(method, targetObject, targetParameterValues, principal);
    }

    private static void execute(Method method, Object targetObject, Object[] targetArguments, Principal principal) throws Exception {
        Throwable e;
        try {
            Subject subject = null;
            PrivilegedExceptionAction<Void> pea = () -> {
                method.invoke(targetObject, targetArguments);
                return null;
            };
            if (targetArguments != null && (targetArguments[0] instanceof HttpServletRequest)) {
                HttpServletRequest request = (HttpServletRequest) targetArguments[0];
                boolean hasSubject = false;
                HttpSession session = request.getSession(false);
                if (session != null) {
                    subject = (Subject) session.getAttribute(Globals.SUBJECT_ATTR);
                    hasSubject = subject != null;
                }
                if (subject == null) {
                    subject = new Subject();
                    if (principal != null) {
                        subject.getPrincipals().add(principal);
                    }
                }
                if (session != null && !hasSubject) {
                    session.setAttribute(Globals.SUBJECT_ATTR, subject);
                }
            }
            Subject.doAsPrivileged(subject, pea, (AccessControlContext) null);
        } catch (PrivilegedActionException pe) {
            if (pe.getException() instanceof InvocationTargetException) {
                e = pe.getException().getCause();
                ExceptionUtils.handleThrowable(e);
            } else {
                e = pe;
            }
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("SecurityUtil.doAsPrivilege"), e);
            }
            if (e instanceof UnavailableException) {
                throw ((UnavailableException) e);
            }
            if (e instanceof ServletException) {
                throw ((ServletException) e);
            }
            if (e instanceof IOException) {
                throw ((IOException) e);
            }
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            }
            throw new ServletException(e.getMessage(), e);
        }
    }

    private static Method findMethod(Method[] methodsCache, String methodName) {
        if (methodName.equals(INIT_METHOD)) {
            return methodsCache[0];
        }
        if (methodName.equals(DESTROY_METHOD)) {
            return methodsCache[3];
        }
        if (methodName.equals(SERVICE_METHOD)) {
            return methodsCache[1];
        }
        if (methodName.equals(DOFILTER_METHOD)) {
            return methodsCache[1];
        }
        if (methodName.equals(EVENT_METHOD)) {
            return methodsCache[2];
        }
        if (methodName.equals(DOFILTEREVENT_METHOD)) {
            return methodsCache[2];
        }
        return null;
    }

    private static Method createMethodAndCacheIt(Method[] methodsCache, Class<?> targetType, String methodName, Class<?>[] parameterTypes) throws Exception {
        if (methodsCache == null) {
            methodsCache = new Method[4];
        }
        Method method = targetType.getMethod(methodName, parameterTypes);
        if (methodName.equals(INIT_METHOD)) {
            methodsCache[0] = method;
        } else if (methodName.equals(DESTROY_METHOD)) {
            methodsCache[3] = method;
        } else if (methodName.equals(SERVICE_METHOD)) {
            methodsCache[1] = method;
        } else if (methodName.equals(DOFILTER_METHOD)) {
            methodsCache[1] = method;
        } else if (methodName.equals(EVENT_METHOD)) {
            methodsCache[2] = method;
        } else if (methodName.equals(DOFILTEREVENT_METHOD)) {
            methodsCache[2] = method;
        }
        classCache.put(targetType, methodsCache);
        return method;
    }

    public static void remove(Object cachedObject) {
        classCache.remove(cachedObject);
    }

    public static boolean isPackageProtectionEnabled() {
        if (packageDefinitionEnabled && Globals.IS_SECURITY_ENABLED) {
            return true;
        }
        return false;
    }
}
