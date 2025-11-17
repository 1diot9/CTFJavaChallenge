package org.apache.tomcat.util.modeler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.management.DynamicMBean;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.modules.ModelerSource;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.web.context.support.XmlWebApplicationContext;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/modeler/Registry.class */
public class Registry implements RegistryMBean, MBeanRegistration {
    private static final Log log = LogFactory.getLog((Class<?>) Registry.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) Registry.class);
    private static Registry registry = null;
    private Object guard;
    private volatile MBeanServer server = null;
    private final Object serverLock = new Object();
    private Map<String, ManagedBean> descriptors = new HashMap();
    private Map<String, ManagedBean> descriptorsByClass = new HashMap();
    private Map<String, URL> searchedPaths = new HashMap();
    private final Hashtable<String, Hashtable<String, Integer>> idDomains = new Hashtable<>();
    private final Hashtable<String, int[]> ids = new Hashtable<>();

    public static synchronized Registry getRegistry(Object key, Object guard) {
        if (registry == null) {
            registry = new Registry();
            registry.guard = guard;
        }
        if (registry.guard != null && registry.guard != guard) {
            return null;
        }
        return registry;
    }

    public static synchronized void disableRegistry() {
        if (registry == null) {
            registry = new NoDescriptorRegistry();
        } else if (!(registry instanceof NoDescriptorRegistry)) {
            log.warn(sm.getString("registry.noDisable"));
        }
    }

    @Override // org.apache.tomcat.util.modeler.RegistryMBean
    public void stop() {
        this.descriptorsByClass = new HashMap();
        this.descriptors = new HashMap();
        this.searchedPaths = new HashMap();
    }

    @Override // org.apache.tomcat.util.modeler.RegistryMBean
    public void registerComponent(Object bean, String oname, String type) throws Exception {
        registerComponent(bean, new ObjectName(oname), type);
    }

    @Override // org.apache.tomcat.util.modeler.RegistryMBean
    public void unregisterComponent(String oname) {
        try {
            unregisterComponent(new ObjectName(oname));
        } catch (MalformedObjectNameException e) {
            log.info(sm.getString("registry.objectNameCreateError"), e);
        }
    }

    @Override // org.apache.tomcat.util.modeler.RegistryMBean
    public void invoke(List<ObjectName> mbeans, String operation, boolean failFirst) throws Exception {
        if (mbeans == null) {
            return;
        }
        for (ObjectName current : mbeans) {
            if (current != null) {
                try {
                    if (getMethodInfo(current, operation) != null) {
                        getMBeanServer().invoke(current, operation, new Object[0], new String[0]);
                    }
                } catch (Exception t) {
                    if (failFirst) {
                        throw t;
                    }
                    log.info(sm.getString("registry.initError"), t);
                }
            }
        }
    }

    @Override // org.apache.tomcat.util.modeler.RegistryMBean
    public synchronized int getId(String domain, String name) {
        if (domain == null) {
            domain = "";
        }
        Hashtable<String, Integer> domainTable = this.idDomains.computeIfAbsent(domain, k -> {
            return new Hashtable();
        });
        if (name == null) {
            name = "";
        }
        Integer i = domainTable.get(name);
        if (i != null) {
            return i.intValue();
        }
        int[] id = this.ids.computeIfAbsent(domain, k2 -> {
            return new int[1];
        });
        int code = id[0];
        id[0] = code + 1;
        domainTable.put(name, Integer.valueOf(code));
        return code;
    }

    public void addManagedBean(ManagedBean bean) {
        this.descriptors.put(bean.getName(), bean);
        if (bean.getType() != null) {
            this.descriptorsByClass.put(bean.getType(), bean);
        }
    }

    public ManagedBean findManagedBean(String name) {
        ManagedBean mb = this.descriptors.get(name);
        if (mb == null) {
            mb = this.descriptorsByClass.get(name);
        }
        return mb;
    }

    public String getType(ObjectName oname, String attName) {
        try {
            MBeanInfo info = getMBeanServer().getMBeanInfo(oname);
            MBeanAttributeInfo[] attInfo = info.getAttributes();
            for (MBeanAttributeInfo mBeanAttributeInfo : attInfo) {
                if (attName.equals(mBeanAttributeInfo.getName())) {
                    String type = mBeanAttributeInfo.getType();
                    return type;
                }
            }
            return null;
        } catch (Exception e) {
            log.info(sm.getString("registry.noMetadata", oname));
            return null;
        }
    }

    public MBeanOperationInfo getMethodInfo(ObjectName oname, String opName) {
        try {
            MBeanInfo info = getMBeanServer().getMBeanInfo(oname);
            MBeanOperationInfo[] attInfo = info.getOperations();
            for (MBeanOperationInfo mBeanOperationInfo : attInfo) {
                if (opName.equals(mBeanOperationInfo.getName())) {
                    return mBeanOperationInfo;
                }
            }
            return null;
        } catch (Exception e) {
            log.info(sm.getString("registry.noMetadata", oname));
            return null;
        }
    }

    public MBeanOperationInfo getMethodInfo(ObjectName oname, String opName, int argCount) throws InstanceNotFoundException {
        try {
            MBeanInfo info = getMBeanServer().getMBeanInfo(oname);
            MBeanOperationInfo[] attInfo = info.getOperations();
            for (MBeanOperationInfo mBeanOperationInfo : attInfo) {
                if (opName.equals(mBeanOperationInfo.getName()) && argCount == mBeanOperationInfo.getSignature().length) {
                    return mBeanOperationInfo;
                }
            }
            return null;
        } catch (InstanceNotFoundException infe) {
            throw infe;
        } catch (Exception e) {
            log.warn(sm.getString("registry.noMetadata", oname), e);
            return null;
        }
    }

    public void unregisterComponent(ObjectName oname) {
        if (oname != null) {
            try {
                if (getMBeanServer().isRegistered(oname)) {
                    getMBeanServer().unregisterMBean(oname);
                }
            } catch (Throwable t) {
                log.error(sm.getString("registry.unregisterError"), t);
            }
        }
    }

    public MBeanServer getMBeanServer() {
        if (this.server == null) {
            synchronized (this.serverLock) {
                if (this.server == null) {
                    long t1 = System.currentTimeMillis();
                    if (MBeanServerFactory.findMBeanServer((String) null).size() > 0) {
                        this.server = (MBeanServer) MBeanServerFactory.findMBeanServer((String) null).get(0);
                        if (log.isDebugEnabled()) {
                            log.debug("Using existing MBeanServer " + (System.currentTimeMillis() - t1));
                        }
                    } else {
                        this.server = ManagementFactory.getPlatformMBeanServer();
                        if (log.isDebugEnabled()) {
                            log.debug("Created MBeanServer" + (System.currentTimeMillis() - t1));
                        }
                    }
                }
            }
        }
        return this.server;
    }

    public ManagedBean findManagedBean(Object bean, Class<?> beanClass, String type) throws Exception {
        if (bean != null && beanClass == null) {
            beanClass = bean.getClass();
        }
        if (type == null) {
            type = beanClass.getName();
        }
        ManagedBean managed = findManagedBean(type);
        if (managed == null) {
            if (log.isDebugEnabled()) {
                log.debug("Looking for descriptor ");
            }
            findDescriptor(beanClass, type);
            managed = findManagedBean(type);
        }
        if (managed == null) {
            if (log.isDebugEnabled()) {
                log.debug("Introspecting ");
            }
            load("MbeansDescriptorsIntrospectionSource", beanClass, type);
            managed = findManagedBean(type);
            if (managed == null) {
                log.warn(sm.getString("registry.noTypeMetadata", type));
                return null;
            }
            managed.setName(type);
            addManagedBean(managed);
        }
        return managed;
    }

    public Object convertValue(String type, String value) {
        Object objValue = value;
        if (type == null || "java.lang.String".equals(type)) {
            objValue = value;
        } else if ("javax.management.ObjectName".equals(type) || "ObjectName".equals(type)) {
            try {
                objValue = new ObjectName(value);
            } catch (MalformedObjectNameException e) {
                return null;
            }
        } else if ("java.lang.Integer".equals(type) || "int".equals(type)) {
            objValue = Integer.valueOf(value);
        } else if ("java.lang.Long".equals(type) || "long".equals(type)) {
            objValue = Long.valueOf(value);
        } else if ("java.lang.Boolean".equals(type) || "boolean".equals(type)) {
            objValue = Boolean.valueOf(value);
        }
        return objValue;
    }

    public List<ObjectName> load(String sourceType, Object source, String param) throws Exception {
        String type;
        Object inputsource;
        if (log.isTraceEnabled()) {
            log.trace("load " + source);
        }
        if (source instanceof URL) {
            URL url = (URL) source;
            String location = url.toString();
            type = param;
            inputsource = url.openStream();
            if (sourceType == null && location.endsWith(XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX)) {
                sourceType = "MbeansDescriptorsDigesterSource";
            }
        } else if (source instanceof File) {
            String location2 = ((File) source).getAbsolutePath();
            inputsource = new FileInputStream((File) source);
            type = param;
            if (sourceType == null && location2.endsWith(XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX)) {
                sourceType = "MbeansDescriptorsDigesterSource";
            }
        } else if (source instanceof InputStream) {
            type = param;
            inputsource = source;
        } else if (source instanceof Class) {
            ((Class) source).getName();
            type = param;
            inputsource = source;
            if (sourceType == null) {
                sourceType = "MbeansDescriptorsIntrospectionSource";
            }
        } else {
            throw new IllegalArgumentException(sm.getString("registry.invalidSource"));
        }
        if (sourceType == null) {
            sourceType = "MbeansDescriptorsDigesterSource";
        }
        ModelerSource ds = getModelerSource(sourceType);
        List<ObjectName> mbeans = ds.loadDescriptors(this, type, inputsource);
        return mbeans;
    }

    public void registerComponent(Object bean, ObjectName oname, String type) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Managed= " + oname);
        }
        if (bean == null) {
            log.error(sm.getString("registry.nullBean", oname));
            return;
        }
        if (type == null) {
            try {
                type = bean.getClass().getName();
            } catch (Exception ex) {
                log.error(sm.getString("registry.registerError", oname), ex);
                throw ex;
            }
        }
        ManagedBean managed = findManagedBean(null, bean.getClass(), type);
        DynamicMBean mbean = managed.createMBean(bean);
        if (getMBeanServer().isRegistered(oname)) {
            if (log.isDebugEnabled()) {
                log.debug("Unregistering existing component " + oname);
            }
            getMBeanServer().unregisterMBean(oname);
        }
        getMBeanServer().registerMBean(mbean, oname);
    }

    public void loadDescriptors(String packageName, ClassLoader classLoader) {
        String res = packageName.replace('.', '/');
        if (log.isTraceEnabled()) {
            log.trace("Finding descriptor " + res);
        }
        if (this.searchedPaths.get(packageName) != null) {
            return;
        }
        String descriptors = res + "/mbeans-descriptors.xml";
        URL dURL = classLoader.getResource(descriptors);
        if (dURL == null) {
            return;
        }
        log.debug("Found " + dURL);
        this.searchedPaths.put(packageName, dURL);
        try {
            load("MbeansDescriptorsDigesterSource", dURL, null);
        } catch (Exception e) {
            log.error(sm.getString("registry.loadError", dURL));
        }
    }

    private void findDescriptor(Class<?> beanClass, String type) {
        int lastComp;
        if (type == null) {
            type = beanClass.getName();
        }
        ClassLoader classLoader = null;
        if (beanClass != null) {
            classLoader = beanClass.getClassLoader();
        }
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        String className = type;
        String pkg = className;
        while (pkg.indexOf(46) > 0 && (lastComp = pkg.lastIndexOf(46)) > 0) {
            pkg = pkg.substring(0, lastComp);
            if (this.searchedPaths.get(pkg) != null) {
                return;
            } else {
                loadDescriptors(pkg, classLoader);
            }
        }
    }

    private ModelerSource getModelerSource(String type) throws Exception {
        if (type == null) {
            type = "MbeansDescriptorsDigesterSource";
        }
        if (!type.contains(".")) {
            type = "org.apache.tomcat.util.modeler.modules." + type;
        }
        Class<?> c = Class.forName(type);
        ModelerSource ds = (ModelerSource) c.getConstructor(new Class[0]).newInstance(new Object[0]);
        return ds;
    }

    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        synchronized (this.serverLock) {
            this.server = server;
        }
        return name;
    }

    public void postRegister(Boolean registrationDone) {
    }

    public void preDeregister() throws Exception {
    }

    public void postDeregister() {
    }
}
