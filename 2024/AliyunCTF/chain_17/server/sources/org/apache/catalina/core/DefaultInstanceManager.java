package org.apache.catalina.core;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jakarta.xml.ws.WebServiceRef;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Globals;
import org.apache.catalina.security.SecurityUtil;
import org.apache.catalina.util.Introspection;
import org.apache.juli.logging.Log;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.collections.ManagedConcurrentWeakHashMap;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.jndi.JndiLocatorSupport;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/DefaultInstanceManager.class */
public class DefaultInstanceManager implements InstanceManager {
    private static final AnnotationCacheEntry[] ANNOTATIONS_EMPTY = new AnnotationCacheEntry[0];
    protected static final StringManager sm = StringManager.getManager((Class<?>) DefaultInstanceManager.class);
    private static final boolean EJB_PRESENT;
    private static final boolean JPA_PRESENT;
    private static final boolean WS_PRESENT;
    private final Context context;
    private final Map<String, Map<String, String>> injectionMap;
    protected final ClassLoader classLoader;
    protected final ClassLoader containerClassLoader;
    protected final boolean privileged;
    protected final boolean ignoreAnnotations;
    private final Set<String> restrictedClasses;
    private final ManagedConcurrentWeakHashMap<Class<?>, AnnotationCacheEntry[]> annotationCache = new ManagedConcurrentWeakHashMap<>();
    private final Map<String, String> postConstructMethods;
    private final Map<String, String> preDestroyMethods;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/DefaultInstanceManager$AnnotationCacheEntryType.class */
    public enum AnnotationCacheEntryType {
        FIELD,
        SETTER,
        POST_CONSTRUCT,
        PRE_DESTROY
    }

    static {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("jakarta.ejb.EJB");
        } catch (ClassNotFoundException e) {
        }
        EJB_PRESENT = clazz != null;
        Class<?> clazz2 = null;
        try {
            clazz2 = Class.forName("jakarta.persistence.PersistenceContext");
        } catch (ClassNotFoundException e2) {
        }
        JPA_PRESENT = clazz2 != null;
        Class<?> clazz3 = null;
        try {
            clazz3 = Class.forName("jakarta.xml.ws.WebServiceRef");
        } catch (ClassNotFoundException e3) {
        }
        WS_PRESENT = clazz3 != null;
    }

    public DefaultInstanceManager(Context context, Map<String, Map<String, String>> injectionMap, org.apache.catalina.Context catalinaContext, ClassLoader containerClassLoader) {
        this.classLoader = catalinaContext.getLoader().getClassLoader();
        this.privileged = catalinaContext.getPrivileged();
        this.containerClassLoader = containerClassLoader;
        this.ignoreAnnotations = catalinaContext.getIgnoreAnnotations();
        Log log = catalinaContext.getLogger();
        Set<String> classNames = new HashSet<>();
        loadProperties(classNames, "org/apache/catalina/core/RestrictedServlets.properties", "defaultInstanceManager.restrictedServletsResource", log);
        loadProperties(classNames, "org/apache/catalina/core/RestrictedListeners.properties", "defaultInstanceManager.restrictedListenersResource", log);
        loadProperties(classNames, "org/apache/catalina/core/RestrictedFilters.properties", "defaultInstanceManager.restrictedFiltersResource", log);
        this.restrictedClasses = Collections.unmodifiableSet(classNames);
        this.context = context;
        this.injectionMap = injectionMap;
        this.postConstructMethods = catalinaContext.findPostConstructMethods();
        this.preDestroyMethods = catalinaContext.findPreDestroyMethods();
    }

    @Override // org.apache.tomcat.InstanceManager
    public Object newInstance(Class<?> clazz) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
        return newInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]), clazz);
    }

    @Override // org.apache.tomcat.InstanceManager
    public Object newInstance(String className) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, IllegalArgumentException, NoSuchMethodException, SecurityException {
        Class<?> clazz = loadClassMaybePrivileged(className, this.classLoader);
        return newInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]), clazz);
    }

    @Override // org.apache.tomcat.InstanceManager
    public Object newInstance(String className, ClassLoader classLoader) throws IllegalAccessException, NamingException, InvocationTargetException, InstantiationException, ClassNotFoundException, IllegalArgumentException, NoSuchMethodException, SecurityException {
        Class<?> clazz = classLoader.loadClass(className);
        return newInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]), clazz);
    }

    @Override // org.apache.tomcat.InstanceManager
    public void newInstance(Object o) throws IllegalAccessException, InvocationTargetException, NamingException {
        newInstance(o, o.getClass());
    }

    private Object newInstance(Object instance, Class<?> clazz) throws IllegalAccessException, InvocationTargetException, NamingException {
        if (!this.ignoreAnnotations) {
            Map<String, String> injections = assembleInjectionsFromClassHierarchy(clazz);
            populateAnnotationsCache(clazz, injections);
            processAnnotations(instance, injections);
            postConstruct(instance, clazz);
        }
        return instance;
    }

    private Map<String, String> assembleInjectionsFromClassHierarchy(Class<?> clazz) {
        Map<String, String> injections = new HashMap<>();
        while (clazz != null) {
            Map<? extends String, ? extends String> map = this.injectionMap.get(clazz.getName());
            if (map != null) {
                injections.putAll(map);
            }
            clazz = clazz.getSuperclass();
        }
        return injections;
    }

    @Override // org.apache.tomcat.InstanceManager
    public void destroyInstance(Object instance) throws IllegalAccessException, InvocationTargetException {
        if (!this.ignoreAnnotations) {
            preDestroy(instance, instance.getClass());
        }
    }

    protected void postConstruct(Object instance, Class<?> clazz) throws IllegalAccessException, InvocationTargetException {
        if (this.context == null) {
            return;
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class) {
            postConstruct(instance, superClass);
        }
        AnnotationCacheEntry[] annotations = this.annotationCache.get(clazz);
        for (AnnotationCacheEntry entry : annotations) {
            if (entry.getType() == AnnotationCacheEntryType.POST_CONSTRUCT) {
                Method postConstruct = getMethod(clazz, entry);
                postConstruct.trySetAccessible();
                postConstruct.invoke(instance, new Object[0]);
            }
        }
    }

    protected void preDestroy(Object instance, Class<?> clazz) throws IllegalAccessException, InvocationTargetException {
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class) {
            preDestroy(instance, superClass);
        }
        AnnotationCacheEntry[] annotations = this.annotationCache.get(clazz);
        if (annotations == null) {
            return;
        }
        for (AnnotationCacheEntry entry : annotations) {
            if (entry.getType() == AnnotationCacheEntryType.PRE_DESTROY) {
                Method preDestroy = getMethod(clazz, entry);
                preDestroy.trySetAccessible();
                preDestroy.invoke(instance, new Object[0]);
            }
        }
    }

    @Override // org.apache.tomcat.InstanceManager
    public void backgroundProcess() {
        this.annotationCache.maintain();
    }

    protected void populateAnnotationsCache(Class<?> clazz, Map<String, String> injections) throws IllegalAccessException, InvocationTargetException, NamingException {
        AnnotationCacheEntry[] annotationsArray;
        PersistenceUnit annotation;
        PersistenceContext annotation2;
        WebServiceRef annotation3;
        EJB annotation4;
        PersistenceUnit annotation5;
        PersistenceContext annotation6;
        WebServiceRef annotation7;
        EJB annotation8;
        List<AnnotationCacheEntry> annotations = null;
        Set<String> injectionsMatchedToSetter = new HashSet<>();
        while (clazz != null) {
            AnnotationCacheEntry[] annotationsArray2 = this.annotationCache.get(clazz);
            if (annotationsArray2 == null) {
                if (annotations == null) {
                    annotations = new ArrayList<>();
                } else {
                    annotations.clear();
                }
                Method[] methods = Introspection.getDeclaredMethods(clazz);
                Method postConstruct = null;
                String postConstructFromXml = this.postConstructMethods.get(clazz.getName());
                Method preDestroy = null;
                String preDestroyFromXml = this.preDestroyMethods.get(clazz.getName());
                for (Method method : methods) {
                    if (this.context != null) {
                        if (injections != null && Introspection.isValidSetter(method)) {
                            String fieldName = Introspection.getPropertyName(method);
                            injectionsMatchedToSetter.add(fieldName);
                            if (injections.containsKey(fieldName)) {
                                annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), injections.get(fieldName), AnnotationCacheEntryType.SETTER));
                            }
                        }
                        Resource resourceAnnotation = (Resource) method.getAnnotation(Resource.class);
                        if (resourceAnnotation != null) {
                            annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), resourceAnnotation.name(), AnnotationCacheEntryType.SETTER));
                        } else if (EJB_PRESENT && (annotation8 = method.getAnnotation(EJB.class)) != null) {
                            annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), annotation8.name(), AnnotationCacheEntryType.SETTER));
                        } else if (WS_PRESENT && (annotation7 = method.getAnnotation(WebServiceRef.class)) != null) {
                            annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), annotation7.name(), AnnotationCacheEntryType.SETTER));
                        } else if (JPA_PRESENT && (annotation6 = method.getAnnotation(PersistenceContext.class)) != null) {
                            annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), annotation6.name(), AnnotationCacheEntryType.SETTER));
                        } else if (JPA_PRESENT && (annotation5 = method.getAnnotation(PersistenceUnit.class)) != null) {
                            annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), annotation5.name(), AnnotationCacheEntryType.SETTER));
                        }
                    }
                    postConstruct = findPostConstruct(postConstruct, postConstructFromXml, method);
                    preDestroy = findPreDestroy(preDestroy, preDestroyFromXml, method);
                }
                if (postConstruct != null) {
                    annotations.add(new AnnotationCacheEntry(postConstruct.getName(), postConstruct.getParameterTypes(), null, AnnotationCacheEntryType.POST_CONSTRUCT));
                } else if (postConstructFromXml != null) {
                    throw new IllegalArgumentException(sm.getString("defaultInstanceManager.postConstructNotFound", postConstructFromXml, clazz.getName()));
                }
                if (preDestroy != null) {
                    annotations.add(new AnnotationCacheEntry(preDestroy.getName(), preDestroy.getParameterTypes(), null, AnnotationCacheEntryType.PRE_DESTROY));
                } else if (preDestroyFromXml != null) {
                    throw new IllegalArgumentException(sm.getString("defaultInstanceManager.preDestroyNotFound", preDestroyFromXml, clazz.getName()));
                }
                if (this.context != null) {
                    Field[] fields = Introspection.getDeclaredFields(clazz);
                    for (Field field : fields) {
                        String fieldName2 = field.getName();
                        if (injections != null && injections.containsKey(fieldName2) && !injectionsMatchedToSetter.contains(fieldName2)) {
                            annotations.add(new AnnotationCacheEntry(fieldName2, null, injections.get(fieldName2), AnnotationCacheEntryType.FIELD));
                        } else {
                            Resource resourceAnnotation2 = (Resource) field.getAnnotation(Resource.class);
                            if (resourceAnnotation2 != null) {
                                annotations.add(new AnnotationCacheEntry(fieldName2, null, resourceAnnotation2.name(), AnnotationCacheEntryType.FIELD));
                            } else if (EJB_PRESENT && (annotation4 = field.getAnnotation(EJB.class)) != null) {
                                annotations.add(new AnnotationCacheEntry(fieldName2, null, annotation4.name(), AnnotationCacheEntryType.FIELD));
                            } else if (WS_PRESENT && (annotation3 = field.getAnnotation(WebServiceRef.class)) != null) {
                                annotations.add(new AnnotationCacheEntry(fieldName2, null, annotation3.name(), AnnotationCacheEntryType.FIELD));
                            } else if (JPA_PRESENT && (annotation2 = field.getAnnotation(PersistenceContext.class)) != null) {
                                annotations.add(new AnnotationCacheEntry(fieldName2, null, annotation2.name(), AnnotationCacheEntryType.FIELD));
                            } else if (JPA_PRESENT && (annotation = field.getAnnotation(PersistenceUnit.class)) != null) {
                                annotations.add(new AnnotationCacheEntry(fieldName2, null, annotation.name(), AnnotationCacheEntryType.FIELD));
                            }
                        }
                    }
                }
                if (annotations.isEmpty()) {
                    annotationsArray = ANNOTATIONS_EMPTY;
                } else {
                    annotationsArray = (AnnotationCacheEntry[]) annotations.toArray(new AnnotationCacheEntry[0]);
                }
                synchronized (this.annotationCache) {
                    this.annotationCache.put(clazz, annotationsArray);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    protected void processAnnotations(Object instance, Map<String, String> injections) throws IllegalAccessException, InvocationTargetException, NamingException {
        if (this.context == null) {
            return;
        }
        Class<?> cls = instance.getClass();
        while (true) {
            Class<?> clazz = cls;
            if (clazz != null) {
                AnnotationCacheEntry[] annotations = this.annotationCache.get(clazz);
                for (AnnotationCacheEntry entry : annotations) {
                    if (entry.getType() == AnnotationCacheEntryType.SETTER) {
                        lookupMethodResource(this.context, instance, getMethod(clazz, entry), entry.getName(), clazz);
                    } else if (entry.getType() == AnnotationCacheEntryType.FIELD) {
                        lookupFieldResource(this.context, instance, getField(clazz, entry), entry.getName(), clazz);
                    }
                }
                cls = clazz.getSuperclass();
            } else {
                return;
            }
        }
    }

    protected int getAnnotationCacheSize() {
        return this.annotationCache.size();
    }

    protected Class<?> loadClassMaybePrivileged(String className, ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> clazz;
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                clazz = (Class) AccessController.doPrivileged(new PrivilegedLoadClass(className, classLoader));
            } catch (PrivilegedActionException e) {
                Throwable t = e.getCause();
                if (t instanceof ClassNotFoundException) {
                    throw ((ClassNotFoundException) t);
                }
                throw new RuntimeException(t);
            }
        } else {
            clazz = loadClass(className, classLoader);
        }
        checkAccess(clazz);
        return clazz;
    }

    protected Class<?> loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        if (className.startsWith("org.apache.catalina")) {
            return this.containerClassLoader.loadClass(className);
        }
        try {
            Class<?> clazz = this.containerClassLoader.loadClass(className);
            if (ContainerServlet.class.isAssignableFrom(clazz)) {
                return clazz;
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
        return classLoader.loadClass(className);
    }

    private void checkAccess(Class<?> clazz) {
        if (this.privileged) {
            return;
        }
        if (ContainerServlet.class.isAssignableFrom(clazz)) {
            throw new SecurityException(sm.getString("defaultInstanceManager.restrictedContainerServlet", clazz));
        }
        while (clazz != null) {
            if (this.restrictedClasses.contains(clazz.getName())) {
                throw new SecurityException(sm.getString("defaultInstanceManager.restrictedClass", clazz));
            }
            clazz = clazz.getSuperclass();
        }
    }

    protected static void lookupFieldResource(Context context, Object instance, Field field, String name, Class<?> clazz) throws NamingException, IllegalAccessException {
        Object lookedupResource;
        String normalizedName = normalize(name);
        if (normalizedName != null && normalizedName.length() > 0) {
            lookedupResource = context.lookup(normalizedName);
        } else {
            lookedupResource = context.lookup(clazz.getName() + "/" + field.getName());
        }
        field.trySetAccessible();
        field.set(instance, lookedupResource);
    }

    protected static void lookupMethodResource(Context context, Object instance, Method method, String name, Class<?> clazz) throws NamingException, IllegalAccessException, InvocationTargetException {
        Object lookedupResource;
        if (!Introspection.isValidSetter(method)) {
            throw new IllegalArgumentException(sm.getString("defaultInstanceManager.invalidInjection"));
        }
        String normalizedName = normalize(name);
        if (normalizedName != null && normalizedName.length() > 0) {
            lookedupResource = context.lookup(normalizedName);
        } else {
            lookedupResource = context.lookup(clazz.getName() + "/" + Introspection.getPropertyName(method));
        }
        method.trySetAccessible();
        method.invoke(instance, lookedupResource);
    }

    private static void loadProperties(Set<String> classNames, String resourceName, String messageKey, Log log) {
        Properties properties = new Properties();
        ClassLoader cl = DefaultInstanceManager.class.getClassLoader();
        try {
            InputStream is = cl.getResourceAsStream(resourceName);
            try {
                if (is == null) {
                    log.error(sm.getString(messageKey, resourceName));
                } else {
                    properties.load(is);
                }
                if (is != null) {
                    is.close();
                }
            } finally {
            }
        } catch (IOException ioe) {
            log.error(sm.getString(messageKey, resourceName), ioe);
        }
        if (properties.isEmpty()) {
            return;
        }
        for (Map.Entry<Object, Object> e : properties.entrySet()) {
            if ("restricted".equals(e.getValue())) {
                classNames.add(e.getKey().toString());
            } else {
                log.warn(sm.getString("defaultInstanceManager.restrictedWrongValue", resourceName, e.getKey(), e.getValue()));
            }
        }
    }

    private static String normalize(String jndiName) {
        if (jndiName != null && jndiName.startsWith(JndiLocatorSupport.CONTAINER_PREFIX)) {
            return jndiName.substring(14);
        }
        return jndiName;
    }

    private static Method getMethod(Class<?> clazz, AnnotationCacheEntry entry) {
        Method result = null;
        if (Globals.IS_SECURITY_ENABLED) {
            result = (Method) AccessController.doPrivileged(new PrivilegedGetMethod(clazz, entry));
        } else {
            try {
                result = clazz.getDeclaredMethod(entry.getAccessibleObjectName(), entry.getParamTypes());
            } catch (NoSuchMethodException e) {
            }
        }
        return result;
    }

    private static Field getField(Class<?> clazz, AnnotationCacheEntry entry) {
        Field result = null;
        if (Globals.IS_SECURITY_ENABLED) {
            result = (Field) AccessController.doPrivileged(new PrivilegedGetField(clazz, entry));
        } else {
            try {
                result = clazz.getDeclaredField(entry.getAccessibleObjectName());
            } catch (NoSuchFieldException e) {
            }
        }
        return result;
    }

    private static Method findPostConstruct(Method currentPostConstruct, String postConstructFromXml, Method method) {
        return findLifecycleCallback(currentPostConstruct, postConstructFromXml, method, PostConstruct.class);
    }

    private static Method findPreDestroy(Method currentPreDestroy, String preDestroyFromXml, Method method) {
        return findLifecycleCallback(currentPreDestroy, preDestroyFromXml, method, PreDestroy.class);
    }

    private static Method findLifecycleCallback(Method currentMethod, String methodNameFromXml, Method method, Class<? extends Annotation> annotation) {
        Method result = currentMethod;
        if (methodNameFromXml != null) {
            if (method.getName().equals(methodNameFromXml)) {
                if (!Introspection.isValidLifecycleCallback(method)) {
                    throw new IllegalArgumentException(sm.getString("defaultInstanceManager.invalidAnnotation", annotation.getName()));
                }
                result = method;
            }
        } else if (method.isAnnotationPresent(annotation)) {
            if (currentMethod != null || !Introspection.isValidLifecycleCallback(method)) {
                throw new IllegalArgumentException(sm.getString("defaultInstanceManager.invalidAnnotation", annotation.getName()));
            }
            result = method;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/DefaultInstanceManager$AnnotationCacheEntry.class */
    public static final class AnnotationCacheEntry {
        private final String accessibleObjectName;
        private final Class<?>[] paramTypes;
        private final String name;
        private final AnnotationCacheEntryType type;

        AnnotationCacheEntry(String accessibleObjectName, Class<?>[] paramTypes, String name, AnnotationCacheEntryType type) {
            this.accessibleObjectName = accessibleObjectName;
            this.paramTypes = paramTypes;
            this.name = name;
            this.type = type;
        }

        public String getAccessibleObjectName() {
            return this.accessibleObjectName;
        }

        public Class<?>[] getParamTypes() {
            return this.paramTypes;
        }

        public String getName() {
            return this.name;
        }

        public AnnotationCacheEntryType getType() {
            return this.type;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/DefaultInstanceManager$PrivilegedGetField.class */
    public static class PrivilegedGetField implements PrivilegedAction<Field> {
        private final Class<?> clazz;
        private final AnnotationCacheEntry entry;

        PrivilegedGetField(Class<?> clazz, AnnotationCacheEntry entry) {
            this.clazz = clazz;
            this.entry = entry;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Field run() {
            Field result = null;
            try {
                result = this.clazz.getDeclaredField(this.entry.getAccessibleObjectName());
            } catch (NoSuchFieldException e) {
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/DefaultInstanceManager$PrivilegedGetMethod.class */
    public static class PrivilegedGetMethod implements PrivilegedAction<Method> {
        private final Class<?> clazz;
        private final AnnotationCacheEntry entry;

        PrivilegedGetMethod(Class<?> clazz, AnnotationCacheEntry entry) {
            this.clazz = clazz;
            this.entry = entry;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Method run() {
            Method result = null;
            try {
                result = this.clazz.getDeclaredMethod(this.entry.getAccessibleObjectName(), this.entry.getParamTypes());
            } catch (NoSuchMethodException e) {
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/DefaultInstanceManager$PrivilegedLoadClass.class */
    public class PrivilegedLoadClass implements PrivilegedExceptionAction<Class<?>> {
        private final String className;
        private final ClassLoader classLoader;

        PrivilegedLoadClass(String className, ClassLoader classLoader) {
            this.className = className;
            this.classLoader = classLoader;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Class<?> run() throws Exception {
            return DefaultInstanceManager.this.loadClass(this.className, this.classLoader);
        }
    }
}
