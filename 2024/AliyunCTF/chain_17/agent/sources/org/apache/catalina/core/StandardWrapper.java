package org.apache.catalina.core;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Wrapper;
import org.apache.catalina.security.SecurityUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.PeriodicEventListener;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.modeler.Util;
import org.springframework.util.backoff.ExponentialBackOff;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardWrapper.class */
public class StandardWrapper extends ContainerBase implements ServletConfig, Wrapper, NotificationEmitter {
    protected final NotificationBroadcasterSupport broadcaster;
    protected boolean isJspServlet;
    protected ObjectName jspMonitorON;
    protected MBeanNotificationInfo[] notificationInfo;
    protected static final String[] DEFAULT_SERVLET_METHODS = {"GET", "HEAD", "POST"};
    protected static Class<?>[] classType = {ServletConfig.class};
    private final Log log = LogFactory.getLog((Class<?>) StandardWrapper.class);
    protected long available = 0;
    protected final AtomicInteger countAllocated = new AtomicInteger(0);
    protected final StandardWrapperFacade facade = new StandardWrapperFacade(this);
    protected volatile Servlet instance = null;
    protected volatile boolean instanceInitialized = false;
    protected int loadOnStartup = -1;
    protected final ArrayList<String> mappings = new ArrayList<>();
    protected HashMap<String, String> parameters = new HashMap<>();
    protected HashMap<String, String> references = new HashMap<>();
    protected String runAs = null;
    protected long sequenceNumber = 0;
    protected String servletClass = null;
    protected volatile boolean unloading = false;
    protected long unloadDelay = ExponentialBackOff.DEFAULT_INITIAL_INTERVAL;
    protected boolean swallowOutput = false;
    protected long loadTime = 0;
    protected int classLoadTime = 0;
    protected MultipartConfigElement multipartConfigElement = null;
    protected boolean asyncSupported = false;
    protected boolean enabled = true;
    private boolean overridable = false;
    private final ReentrantReadWriteLock parametersLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock mappingsLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock referencesLock = new ReentrantReadWriteLock();
    protected StandardWrapperValve swValve = new StandardWrapperValve();

    public StandardWrapper() {
        this.pipeline.setBasic(this.swValve);
        this.broadcaster = new NotificationBroadcasterSupport();
    }

    @Override // org.apache.catalina.Wrapper
    public boolean isOverridable() {
        return this.overridable;
    }

    @Override // org.apache.catalina.Wrapper
    public void setOverridable(boolean overridable) {
        this.overridable = overridable;
    }

    @Override // org.apache.catalina.Wrapper
    public long getAvailable() {
        return this.available;
    }

    @Override // org.apache.catalina.Wrapper
    public void setAvailable(long available) {
        long oldAvailable = this.available;
        if (available > System.currentTimeMillis()) {
            this.available = available;
        } else {
            this.available = 0L;
        }
        this.support.firePropertyChange("available", Long.valueOf(oldAvailable), Long.valueOf(this.available));
    }

    public int getCountAllocated() {
        return this.countAllocated.get();
    }

    @Override // org.apache.catalina.Wrapper
    public int getLoadOnStartup() {
        if (this.isJspServlet && this.loadOnStartup == -1) {
            return Integer.MAX_VALUE;
        }
        return this.loadOnStartup;
    }

    @Override // org.apache.catalina.Wrapper
    public void setLoadOnStartup(int value) {
        int oldLoadOnStartup = this.loadOnStartup;
        this.loadOnStartup = value;
        this.support.firePropertyChange("loadOnStartup", Integer.valueOf(oldLoadOnStartup), Integer.valueOf(this.loadOnStartup));
    }

    public void setLoadOnStartupString(String value) {
        try {
            setLoadOnStartup(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            setLoadOnStartup(0);
        }
    }

    public String getLoadOnStartupString() {
        return Integer.toString(getLoadOnStartup());
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public void setParent(Container container) {
        if (container != null && !(container instanceof Context)) {
            throw new IllegalArgumentException(sm.getString("standardWrapper.notContext"));
        }
        if (container instanceof StandardContext) {
            this.swallowOutput = ((StandardContext) container).getSwallowOutput();
            this.unloadDelay = ((StandardContext) container).getUnloadDelay();
        }
        super.setParent(container);
    }

    @Override // org.apache.catalina.Wrapper
    public String getRunAs() {
        return this.runAs;
    }

    @Override // org.apache.catalina.Wrapper
    public void setRunAs(String runAs) {
        String oldRunAs = this.runAs;
        this.runAs = runAs;
        this.support.firePropertyChange("runAs", oldRunAs, this.runAs);
    }

    @Override // org.apache.catalina.Wrapper
    public String getServletClass() {
        return this.servletClass;
    }

    @Override // org.apache.catalina.Wrapper
    public void setServletClass(String servletClass) {
        String oldServletClass = this.servletClass;
        this.servletClass = servletClass;
        this.support.firePropertyChange("servletClass", oldServletClass, this.servletClass);
        if (Constants.JSP_SERVLET_CLASS.equals(servletClass)) {
            this.isJspServlet = true;
        }
    }

    public void setServletName(String name) {
        setName(name);
    }

    @Override // org.apache.catalina.Wrapper
    public boolean isUnavailable() {
        if (!isEnabled()) {
            return true;
        }
        if (this.available == 0) {
            return false;
        }
        if (this.available <= System.currentTimeMillis()) {
            this.available = 0L;
            return false;
        }
        return true;
    }

    @Override // org.apache.catalina.Wrapper
    public String[] getServletMethods() throws ServletException {
        this.instance = loadServlet();
        Class<?> cls = this.instance.getClass();
        if (!HttpServlet.class.isAssignableFrom(cls)) {
            return DEFAULT_SERVLET_METHODS;
        }
        Set<String> allow = new HashSet<>();
        allow.add("OPTIONS");
        if (this.isJspServlet) {
            allow.add("GET");
            allow.add("HEAD");
            allow.add("POST");
        } else {
            allow.add("TRACE");
            Method[] methods = getAllDeclaredMethods(cls);
            for (int i = 0; methods != null && i < methods.length; i++) {
                Method m = methods[i];
                if (m.getName().equals("doGet")) {
                    allow.add("GET");
                    allow.add("HEAD");
                } else if (m.getName().equals("doPost")) {
                    allow.add("POST");
                } else if (m.getName().equals("doPut")) {
                    allow.add("PUT");
                } else if (m.getName().equals("doDelete")) {
                    allow.add("DELETE");
                }
            }
        }
        return (String[]) allow.toArray(new String[0]);
    }

    @Override // org.apache.catalina.Wrapper
    public Servlet getServlet() {
        return this.instance;
    }

    @Override // org.apache.catalina.Wrapper
    public void setServlet(Servlet servlet) {
        this.instance = servlet;
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public synchronized void backgroundProcess() {
        super.backgroundProcess();
        if (getState().isAvailable() && (getServlet() instanceof PeriodicEventListener)) {
            ((PeriodicEventListener) getServlet()).periodicEvent();
        }
    }

    public static Throwable getRootCause(ServletException e) {
        Throwable rootCause = e;
        int loops = 0;
        do {
            loops++;
            Throwable rootCauseCheck = rootCause.getCause();
            if (rootCauseCheck != null) {
                rootCause = rootCauseCheck;
            }
            if (rootCauseCheck == null) {
                break;
            }
        } while (loops < 20);
        return rootCause;
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public void addChild(Container child) {
        throw new IllegalStateException(sm.getString("standardWrapper.notChild"));
    }

    @Override // org.apache.catalina.Wrapper
    public void addInitParameter(String name, String value) {
        this.parametersLock.writeLock().lock();
        try {
            this.parameters.put(name, value);
            fireContainerEvent("addInitParameter", name);
        } finally {
            this.parametersLock.writeLock().unlock();
        }
    }

    @Override // org.apache.catalina.Wrapper
    public void addMapping(String mapping) {
        this.mappingsLock.writeLock().lock();
        try {
            this.mappings.add(mapping);
            if (this.parent.getState().equals(LifecycleState.STARTED)) {
                fireContainerEvent(Wrapper.ADD_MAPPING_EVENT, mapping);
            }
        } finally {
            this.mappingsLock.writeLock().unlock();
        }
    }

    @Override // org.apache.catalina.Wrapper
    public void addSecurityReference(String name, String link) {
        this.referencesLock.writeLock().lock();
        try {
            this.references.put(name, link);
            fireContainerEvent("addSecurityReference", name);
        } finally {
            this.referencesLock.writeLock().unlock();
        }
    }

    @Override // org.apache.catalina.Wrapper
    public Servlet allocate() throws ServletException {
        if (this.unloading) {
            throw new ServletException(sm.getString("standardWrapper.unloading", getName()));
        }
        boolean newInstance = false;
        if (this.instance == null || !this.instanceInitialized) {
            synchronized (this) {
                if (this.instance == null) {
                    try {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug("Allocating instance");
                        }
                        this.instance = loadServlet();
                        newInstance = true;
                        this.countAllocated.incrementAndGet();
                    } catch (ServletException e) {
                        throw e;
                    } catch (Throwable e2) {
                        ExceptionUtils.handleThrowable(e2);
                        throw new ServletException(sm.getString("standardWrapper.allocate"), e2);
                    }
                }
                if (!this.instanceInitialized) {
                    initServlet(this.instance);
                }
            }
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace("  Returning instance");
        }
        if (!newInstance) {
            this.countAllocated.incrementAndGet();
        }
        return this.instance;
    }

    @Override // org.apache.catalina.Wrapper
    public void deallocate(Servlet servlet) throws ServletException {
        this.countAllocated.decrementAndGet();
    }

    @Override // org.apache.catalina.Wrapper
    public String findInitParameter(String name) {
        this.parametersLock.readLock().lock();
        try {
            return this.parameters.get(name);
        } finally {
            this.parametersLock.readLock().unlock();
        }
    }

    @Override // org.apache.catalina.Wrapper
    public String[] findInitParameters() {
        this.parametersLock.readLock().lock();
        try {
            return (String[]) this.parameters.keySet().toArray(new String[0]);
        } finally {
            this.parametersLock.readLock().unlock();
        }
    }

    @Override // org.apache.catalina.Wrapper
    public String[] findMappings() {
        this.mappingsLock.readLock().lock();
        try {
            return (String[]) this.mappings.toArray(new String[0]);
        } finally {
            this.mappingsLock.readLock().unlock();
        }
    }

    @Override // org.apache.catalina.Wrapper
    public String findSecurityReference(String name) {
        this.referencesLock.readLock().lock();
        try {
            String reference = this.references.get(name);
            if (getParent() instanceof Context) {
                Context context = (Context) getParent();
                if (reference != null) {
                    reference = context.findRoleMapping(reference);
                } else {
                    reference = context.findRoleMapping(name);
                }
            }
            return reference;
        } finally {
            this.referencesLock.readLock().unlock();
        }
    }

    @Override // org.apache.catalina.Wrapper
    public String[] findSecurityReferences() {
        this.referencesLock.readLock().lock();
        try {
            return (String[]) this.references.keySet().toArray(new String[0]);
        } finally {
            this.referencesLock.readLock().unlock();
        }
    }

    @Override // org.apache.catalina.Wrapper
    public synchronized void load() throws ServletException {
        this.instance = loadServlet();
        if (!this.instanceInitialized) {
            initServlet(this.instance);
        }
        if (this.isJspServlet) {
            try {
                this.jspMonitorON = new ObjectName(getDomain() + ":type=JspMonitor" + getWebModuleKeyProperties() + ",name=" + getName() + getJ2EEKeyProperties());
                Registry.getRegistry(null, null).registerComponent(this.instance, this.jspMonitorON, (String) null);
            } catch (Exception e) {
                this.log.warn(sm.getString("standardWrapper.jspMonitorError", this.instance));
            }
        }
    }

    public synchronized Servlet loadServlet() throws ServletException {
        String log;
        String log2;
        MultipartConfig annotation;
        if (this.instance != null) {
            return this.instance;
        }
        PrintStream out = System.out;
        if (this.swallowOutput) {
            SystemLogHandler.startCapture();
        }
        try {
            long t1 = System.currentTimeMillis();
            if (this.servletClass == null) {
                unavailable(null);
                throw new ServletException(sm.getString("standardWrapper.notClass", getName()));
            }
            InstanceManager instanceManager = ((StandardContext) getParent()).getInstanceManager();
            try {
                try {
                    Servlet servlet = (Servlet) instanceManager.newInstance(this.servletClass);
                    if (this.multipartConfigElement == null && (annotation = (MultipartConfig) servlet.getClass().getAnnotation(MultipartConfig.class)) != null) {
                        this.multipartConfigElement = new MultipartConfigElement(annotation);
                    }
                    if (servlet instanceof ContainerServlet) {
                        ((ContainerServlet) servlet).setWrapper(this);
                    }
                    this.classLoadTime = (int) (System.currentTimeMillis() - t1);
                    initServlet(servlet);
                    fireContainerEvent("load", this);
                    this.loadTime = System.currentTimeMillis() - t1;
                    if (this.swallowOutput && (log2 = SystemLogHandler.stopCapture()) != null && log2.length() > 0) {
                        if (getServletContext() != null) {
                            getServletContext().log(log2);
                        } else {
                            out.println(log2);
                        }
                    }
                    return servlet;
                } catch (Throwable e) {
                    Throwable e2 = ExceptionUtils.unwrapInvocationTargetException(e);
                    ExceptionUtils.handleThrowable(e2);
                    unavailable(null);
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(sm.getString("standardWrapper.instantiate", this.servletClass), e2);
                    }
                    throw new ServletException(sm.getString("standardWrapper.instantiate", this.servletClass), e2);
                }
            } catch (ClassCastException e3) {
                unavailable(null);
                throw new ServletException(sm.getString("standardWrapper.notServlet", this.servletClass), e3);
            }
        } catch (Throwable th) {
            if (this.swallowOutput && (log = SystemLogHandler.stopCapture()) != null && log.length() > 0) {
                if (getServletContext() != null) {
                    getServletContext().log(log);
                } else {
                    out.println(log);
                }
            }
            throw th;
        }
    }

    private synchronized void initServlet(Servlet servlet) throws ServletException {
        if (this.instanceInitialized) {
            return;
        }
        try {
            if (Globals.IS_SECURITY_ENABLED) {
                boolean success = false;
                try {
                    Object[] args = {this.facade};
                    SecurityUtil.doAsPrivilege("init", servlet, classType, args);
                    success = true;
                    if (1 == 0) {
                        SecurityUtil.remove(servlet);
                    }
                } catch (Throwable th) {
                    if (!success) {
                        SecurityUtil.remove(servlet);
                    }
                    throw th;
                }
            } else {
                servlet.init(this.facade);
            }
            this.instanceInitialized = true;
        } catch (UnavailableException f) {
            unavailable(f);
            throw f;
        } catch (ServletException f2) {
            throw f2;
        } catch (Throwable f3) {
            ExceptionUtils.handleThrowable(f3);
            getServletContext().log(sm.getString("standardWrapper.initException", getName()), f3);
            throw new ServletException(sm.getString("standardWrapper.initException", getName()), f3);
        }
    }

    @Override // org.apache.catalina.Wrapper
    public void removeInitParameter(String name) {
        this.parametersLock.writeLock().lock();
        try {
            this.parameters.remove(name);
            fireContainerEvent("removeInitParameter", name);
        } finally {
            this.parametersLock.writeLock().unlock();
        }
    }

    @Override // org.apache.catalina.Wrapper
    public void removeMapping(String mapping) {
        this.mappingsLock.writeLock().lock();
        try {
            this.mappings.remove(mapping);
            if (this.parent.getState().equals(LifecycleState.STARTED)) {
                fireContainerEvent(Wrapper.REMOVE_MAPPING_EVENT, mapping);
            }
        } finally {
            this.mappingsLock.writeLock().unlock();
        }
    }

    @Override // org.apache.catalina.Wrapper
    public void removeSecurityReference(String name) {
        this.referencesLock.writeLock().lock();
        try {
            this.references.remove(name);
            fireContainerEvent("removeSecurityReference", name);
        } finally {
            this.referencesLock.writeLock().unlock();
        }
    }

    @Override // org.apache.catalina.Wrapper
    public void unavailable(UnavailableException unavailable) {
        getServletContext().log(sm.getString("standardWrapper.unavailable", getName()));
        if (unavailable == null) {
            setAvailable(Long.MAX_VALUE);
            return;
        }
        if (unavailable.isPermanent()) {
            setAvailable(Long.MAX_VALUE);
            return;
        }
        int unavailableSeconds = unavailable.getUnavailableSeconds();
        if (unavailableSeconds <= 0) {
            unavailableSeconds = 60;
        }
        setAvailable(System.currentTimeMillis() + (unavailableSeconds * 1000));
    }

    @Override // org.apache.catalina.Wrapper
    public synchronized void unload() throws ServletException {
        String log;
        String log2;
        if (this.instance == null) {
            return;
        }
        this.unloading = true;
        if (this.countAllocated.get() > 0) {
            long delay = this.unloadDelay / 20;
            for (int nRetries = 0; nRetries < 21 && this.countAllocated.get() > 0; nRetries++) {
                if (nRetries % 10 == 0) {
                    this.log.info(sm.getString("standardWrapper.waiting", this.countAllocated.toString(), getName()));
                }
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                }
            }
        }
        if (this.instanceInitialized) {
            PrintStream out = System.out;
            if (this.swallowOutput) {
                SystemLogHandler.startCapture();
            }
            try {
                try {
                    if (Globals.IS_SECURITY_ENABLED) {
                        try {
                            SecurityUtil.doAsPrivilege("destroy", this.instance);
                            SecurityUtil.remove(this.instance);
                        } catch (Throwable th) {
                            SecurityUtil.remove(this.instance);
                            throw th;
                        }
                    } else {
                        this.instance.destroy();
                    }
                    if (!((Context) getParent()).getIgnoreAnnotations()) {
                        try {
                            ((Context) getParent()).getInstanceManager().destroyInstance(this.instance);
                        } catch (Throwable t) {
                            ExceptionUtils.handleThrowable(t);
                            this.log.error(sm.getString("standardWrapper.destroyInstance", getName()), t);
                        }
                    }
                    if (this.swallowOutput && (log2 = SystemLogHandler.stopCapture()) != null && log2.length() > 0) {
                        if (getServletContext() != null) {
                            getServletContext().log(log2);
                        } else {
                            out.println(log2);
                        }
                    }
                    this.instance = null;
                    this.instanceInitialized = false;
                } catch (Throwable t2) {
                    Throwable t3 = ExceptionUtils.unwrapInvocationTargetException(t2);
                    ExceptionUtils.handleThrowable(t3);
                    fireContainerEvent("unload", this);
                    this.unloading = false;
                    throw new ServletException(sm.getString("standardWrapper.destroyException", getName()), t3);
                }
            } catch (Throwable th2) {
                if (!((Context) getParent()).getIgnoreAnnotations()) {
                    try {
                        ((Context) getParent()).getInstanceManager().destroyInstance(this.instance);
                    } catch (Throwable t4) {
                        ExceptionUtils.handleThrowable(t4);
                        this.log.error(sm.getString("standardWrapper.destroyInstance", getName()), t4);
                    }
                }
                if (this.swallowOutput && (log = SystemLogHandler.stopCapture()) != null && log.length() > 0) {
                    if (getServletContext() != null) {
                        getServletContext().log(log);
                    } else {
                        out.println(log);
                    }
                }
                this.instance = null;
                this.instanceInitialized = false;
                throw th2;
            }
        }
        this.instance = null;
        if (this.isJspServlet && this.jspMonitorON != null) {
            Registry.getRegistry(null, null).unregisterComponent(this.jspMonitorON);
        }
        this.unloading = false;
        fireContainerEvent("unload", this);
    }

    @Override // jakarta.servlet.ServletConfig
    public String getInitParameter(String name) {
        return findInitParameter(name);
    }

    @Override // jakarta.servlet.ServletConfig
    public Enumeration<String> getInitParameterNames() {
        this.parametersLock.readLock().lock();
        try {
            return Collections.enumeration(this.parameters.keySet());
        } finally {
            this.parametersLock.readLock().unlock();
        }
    }

    @Override // jakarta.servlet.ServletConfig
    public ServletContext getServletContext() {
        if (this.parent == null || !(this.parent instanceof Context)) {
            return null;
        }
        return ((Context) this.parent).getServletContext();
    }

    @Override // jakarta.servlet.ServletConfig
    public String getServletName() {
        return getName();
    }

    public long getProcessingTime() {
        return this.swValve.getProcessingTime();
    }

    public long getMaxTime() {
        return this.swValve.getMaxTime();
    }

    public long getMinTime() {
        return this.swValve.getMinTime();
    }

    @Deprecated
    public int getRequestCount() {
        return this.swValve.getRequestCount();
    }

    @Deprecated
    public int getErrorCount() {
        return this.swValve.getErrorCount();
    }

    @Override // org.apache.catalina.Wrapper
    public void incrementErrorCount() {
        this.swValve.incrementErrorCount();
    }

    public long getLoadTime() {
        return this.loadTime;
    }

    public int getClassLoadTime() {
        return this.classLoadTime;
    }

    @Override // org.apache.catalina.Wrapper
    public MultipartConfigElement getMultipartConfigElement() {
        return this.multipartConfigElement;
    }

    @Override // org.apache.catalina.Wrapper
    public void setMultipartConfigElement(MultipartConfigElement multipartConfigElement) {
        this.multipartConfigElement = multipartConfigElement;
    }

    @Override // org.apache.catalina.Wrapper
    public boolean isAsyncSupported() {
        return this.asyncSupported;
    }

    @Override // org.apache.catalina.Wrapper
    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    @Override // org.apache.catalina.Wrapper
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override // org.apache.catalina.Wrapper
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected Method[] getAllDeclaredMethods(Class<?> c) {
        if (c.equals(HttpServlet.class)) {
            return null;
        }
        Method[] parentMethods = getAllDeclaredMethods(c.getSuperclass());
        Method[] thisMethods = c.getDeclaredMethods();
        if (thisMethods.length == 0) {
            return parentMethods;
        }
        if (parentMethods != null && parentMethods.length > 0) {
            Method[] allMethods = new Method[parentMethods.length + thisMethods.length];
            System.arraycopy(parentMethods, 0, allMethods, 0, parentMethods.length);
            System.arraycopy(thisMethods, 0, allMethods, parentMethods.length, thisMethods.length);
            thisMethods = allMethods;
        }
        return thisMethods;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        if (getObjectName() != null) {
            ObjectName objectName = getObjectName();
            long j = this.sequenceNumber;
            this.sequenceNumber = j + 1;
            Notification notification = new Notification("j2ee.state.starting", objectName, j);
            this.broadcaster.sendNotification(notification);
        }
        super.startInternal();
        setAvailable(0L);
        if (getObjectName() != null) {
            ObjectName objectName2 = getObjectName();
            long j2 = this.sequenceNumber;
            this.sequenceNumber = j2 + 1;
            Notification notification2 = new Notification("j2ee.state.running", objectName2, j2);
            this.broadcaster.sendNotification(notification2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        setAvailable(Long.MAX_VALUE);
        if (getObjectName() != null) {
            ObjectName objectName = getObjectName();
            long j = this.sequenceNumber;
            this.sequenceNumber = j + 1;
            Notification notification = new Notification("j2ee.state.stopping", objectName, j);
            this.broadcaster.sendNotification(notification);
        }
        try {
            unload();
        } catch (ServletException e) {
            getServletContext().log(sm.getString("standardWrapper.unloadException", getName()), e);
        }
        super.stopInternal();
        if (getObjectName() != null) {
            ObjectName objectName2 = getObjectName();
            long j2 = this.sequenceNumber;
            this.sequenceNumber = j2 + 1;
            Notification notification2 = new Notification("j2ee.state.stopped", objectName2, j2);
            this.broadcaster.sendNotification(notification2);
        }
        ObjectName objectName3 = getObjectName();
        long j3 = this.sequenceNumber;
        this.sequenceNumber = j3 + 1;
        Notification notification3 = new Notification("j2ee.object.deleted", objectName3, j3);
        this.broadcaster.sendNotification(notification3);
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getObjectNameKeyProperties() {
        StringBuilder keyProperties = new StringBuilder("j2eeType=Servlet");
        keyProperties.append(getWebModuleKeyProperties());
        keyProperties.append(",name=");
        String name = getName();
        if (Util.objectNameValueNeedsQuote(name)) {
            name = ObjectName.quote(name);
        }
        keyProperties.append(name);
        keyProperties.append(getJ2EEKeyProperties());
        return keyProperties.toString();
    }

    private String getWebModuleKeyProperties() {
        StringBuilder keyProperties = new StringBuilder(",WebModule=//");
        String hostName = getParent().getParent().getName();
        if (hostName == null) {
            keyProperties.append("DEFAULT");
        } else {
            keyProperties.append(hostName);
        }
        String contextName = getParent().getName();
        if (!contextName.startsWith("/")) {
            keyProperties.append('/');
        }
        keyProperties.append(contextName);
        return keyProperties.toString();
    }

    private String getJ2EEKeyProperties() {
        StringBuilder keyProperties = new StringBuilder(",J2EEApplication=");
        StandardContext ctx = null;
        if (this.parent instanceof StandardContext) {
            ctx = (StandardContext) getParent();
        }
        if (ctx == null) {
            keyProperties.append("none");
        } else {
            keyProperties.append(ctx.getJ2EEApplication());
        }
        keyProperties.append(",J2EEServer=");
        if (ctx == null) {
            keyProperties.append("none");
        } else {
            keyProperties.append(ctx.getJ2EEServer());
        }
        return keyProperties.toString();
    }

    public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object object) throws ListenerNotFoundException {
        this.broadcaster.removeNotificationListener(listener, filter, object);
    }

    public MBeanNotificationInfo[] getNotificationInfo() {
        if (this.notificationInfo == null) {
            this.notificationInfo = new MBeanNotificationInfo[]{new MBeanNotificationInfo(new String[]{"j2ee.object.created"}, Notification.class.getName(), "servlet is created"), new MBeanNotificationInfo(new String[]{"j2ee.state.starting"}, Notification.class.getName(), "servlet is starting"), new MBeanNotificationInfo(new String[]{"j2ee.state.running"}, Notification.class.getName(), "servlet is running"), new MBeanNotificationInfo(new String[]{"j2ee.state.stopped"}, Notification.class.getName(), "servlet start to stopped"), new MBeanNotificationInfo(new String[]{"j2ee.object.stopped"}, Notification.class.getName(), "servlet is stopped"), new MBeanNotificationInfo(new String[]{"j2ee.object.deleted"}, Notification.class.getName(), "servlet is deleted")};
        }
        return this.notificationInfo;
    }

    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object object) throws IllegalArgumentException {
        this.broadcaster.addNotificationListener(listener, filter, object);
    }

    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        this.broadcaster.removeNotificationListener(listener);
    }
}
