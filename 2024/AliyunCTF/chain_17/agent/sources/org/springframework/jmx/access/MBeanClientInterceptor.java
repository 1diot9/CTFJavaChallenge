package org.springframework.jmx.access;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.management.Attribute;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.JMException;
import javax.management.JMX;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXServiceURL;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/access/MBeanClientInterceptor.class */
public class MBeanClientInterceptor implements MethodInterceptor, BeanClassLoaderAware, InitializingBean, DisposableBean {

    @Nullable
    private MBeanServerConnection server;

    @Nullable
    private JMXServiceURL serviceUrl;

    @Nullable
    private Map<String, ?> environment;

    @Nullable
    private String agentId;

    @Nullable
    private ObjectName objectName;

    @Nullable
    private Class<?> managementInterface;

    @Nullable
    private MBeanServerConnection serverToUse;

    @Nullable
    private MBeanServerInvocationHandler invocationHandler;
    protected final Log logger = LogFactory.getLog(getClass());
    private boolean connectOnStartup = true;
    private boolean refreshOnConnectFailure = false;
    private boolean useStrictCasing = true;

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private final ConnectorDelegate connector = new ConnectorDelegate();
    private Map<String, MBeanAttributeInfo> allowedAttributes = Collections.emptyMap();
    private Map<MethodCacheKey, MBeanOperationInfo> allowedOperations = Collections.emptyMap();
    private final Map<Method, String[]> signatureCache = new HashMap();
    private final Object preparationMonitor = new Object();

    public void setServer(MBeanServerConnection server) {
        this.server = server;
    }

    public void setServiceUrl(String url) throws MalformedURLException {
        this.serviceUrl = new JMXServiceURL(url);
    }

    public void setEnvironment(@Nullable Map<String, ?> environment) {
        this.environment = environment;
    }

    @Nullable
    public Map<String, ?> getEnvironment() {
        return this.environment;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public void setConnectOnStartup(boolean connectOnStartup) {
        this.connectOnStartup = connectOnStartup;
    }

    public void setRefreshOnConnectFailure(boolean refreshOnConnectFailure) {
        this.refreshOnConnectFailure = refreshOnConnectFailure;
    }

    public void setObjectName(Object objectName) throws MalformedObjectNameException {
        this.objectName = ObjectNameManager.getInstance(objectName);
    }

    public void setUseStrictCasing(boolean useStrictCasing) {
        this.useStrictCasing = useStrictCasing;
    }

    public void setManagementInterface(@Nullable Class<?> managementInterface) {
        this.managementInterface = managementInterface;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public final Class<?> getManagementInterface() {
        return this.managementInterface;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.server != null && this.refreshOnConnectFailure) {
            throw new IllegalArgumentException("'refreshOnConnectFailure' does not work when setting a 'server' reference. Prefer 'serviceUrl' etc instead.");
        }
        if (this.connectOnStartup) {
            prepare();
        }
    }

    public void prepare() {
        synchronized (this.preparationMonitor) {
            if (this.server != null) {
                this.serverToUse = this.server;
            } else {
                this.serverToUse = null;
                this.serverToUse = this.connector.connect(this.serviceUrl, this.environment, this.agentId);
            }
            this.invocationHandler = null;
            if (this.useStrictCasing) {
                Assert.state(this.objectName != null, "No ObjectName set");
                this.invocationHandler = new MBeanServerInvocationHandler(this.serverToUse, this.objectName, this.managementInterface != null && JMX.isMXBeanInterface(this.managementInterface));
            } else {
                retrieveMBeanInfo(this.serverToUse);
            }
        }
    }

    private void retrieveMBeanInfo(MBeanServerConnection server) throws MBeanInfoRetrievalException {
        try {
            MBeanInfo info = server.getMBeanInfo(this.objectName);
            MBeanAttributeInfo[] attributeInfo = info.getAttributes();
            this.allowedAttributes = CollectionUtils.newHashMap(attributeInfo.length);
            for (MBeanAttributeInfo infoEle : attributeInfo) {
                this.allowedAttributes.put(infoEle.getName(), infoEle);
            }
            MBeanOperationInfo[] operationInfo = info.getOperations();
            this.allowedOperations = CollectionUtils.newHashMap(operationInfo.length);
            for (MBeanOperationInfo infoEle2 : operationInfo) {
                Class<?>[] paramTypes = JmxUtils.parameterInfoToTypes(infoEle2.getSignature(), this.beanClassLoader);
                this.allowedOperations.put(new MethodCacheKey(infoEle2.getName(), paramTypes), infoEle2);
            }
        } catch (ReflectionException ex) {
            throw new MBeanInfoRetrievalException("Unable to read MBean info for bean [ " + this.objectName + "]", ex);
        } catch (IntrospectionException ex2) {
            throw new MBeanInfoRetrievalException("Unable to obtain MBean info for bean [" + this.objectName + "]", ex2);
        } catch (IOException ex3) {
            throw new MBeanInfoRetrievalException("An IOException occurred when communicating with the MBeanServer. It is likely that you are communicating with a remote MBeanServer. Check the inner exception for exact details.", ex3);
        } catch (ClassNotFoundException ex4) {
            throw new MBeanInfoRetrievalException("Unable to locate class specified in method signature", ex4);
        } catch (InstanceNotFoundException ex5) {
            throw new MBeanInfoRetrievalException("Unable to obtain MBean info for bean [" + this.objectName + "]: it is likely that this bean was unregistered during the proxy creation process", ex5);
        }
    }

    protected boolean isPrepared() {
        boolean z;
        synchronized (this.preparationMonitor) {
            z = this.serverToUse != null;
        }
        return z;
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        synchronized (this.preparationMonitor) {
            if (!isPrepared()) {
                prepare();
            }
        }
        try {
            return doInvoke(invocation);
        } catch (IOException | MBeanConnectFailureException ex) {
            return handleConnectFailure(invocation, ex);
        }
    }

    @Nullable
    protected Object handleConnectFailure(MethodInvocation invocation, Exception ex) throws Throwable {
        if (this.refreshOnConnectFailure) {
            if (this.logger.isDebugEnabled()) {
                this.logger.warn("Could not connect to JMX server - retrying", ex);
            } else if (this.logger.isWarnEnabled()) {
                this.logger.warn("Could not connect to JMX server - retrying");
            }
            prepare();
            return doInvoke(invocation);
        }
        throw ex;
    }

    @Nullable
    protected Object doInvoke(MethodInvocation invocation) throws Throwable {
        Object result;
        Method method = invocation.getMethod();
        try {
            if (this.invocationHandler != null) {
                result = this.invocationHandler.invoke(invocation.getThis(), method, invocation.getArguments());
            } else {
                PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
                if (pd != null) {
                    result = invokeAttribute(pd, invocation);
                } else {
                    result = invokeOperation(method, invocation.getArguments());
                }
            }
            return convertResultValueIfNecessary(result, new MethodParameter(method, -1));
        } catch (RuntimeErrorException ex) {
            throw ex.getTargetError();
        } catch (RuntimeMBeanException ex2) {
            throw ex2.getTargetException();
        } catch (RuntimeOperationsException ex3) {
            RuntimeMBeanException targetException = ex3.getTargetException();
            if (targetException instanceof RuntimeMBeanException) {
                RuntimeMBeanException runtimeMBeanException = targetException;
                throw runtimeMBeanException.getTargetException();
            }
            if (targetException instanceof RuntimeErrorException) {
                RuntimeErrorException runtimeErrorException = (RuntimeErrorException) targetException;
                throw runtimeErrorException.getTargetError();
            }
            throw targetException;
        } catch (IOException ex4) {
            if (ReflectionUtils.declaresException(method, ex4.getClass())) {
                throw ex4;
            }
            throw new MBeanConnectFailureException("I/O failure during JMX access", ex4);
        } catch (OperationsException ex5) {
            if (ReflectionUtils.declaresException(method, ex5.getClass())) {
                throw ex5;
            }
            throw new InvalidInvocationException(ex5.getMessage());
        } catch (MBeanException ex6) {
            throw ex6.getTargetException();
        } catch (JMException ex7) {
            if (ReflectionUtils.declaresException(method, ex7.getClass())) {
                throw ex7;
            }
            throw new InvocationFailureException("JMX access failed", ex7);
        }
    }

    @Nullable
    private Object invokeAttribute(PropertyDescriptor pd, MethodInvocation invocation) throws JMException, IOException {
        Assert.state(this.serverToUse != null, "No MBeanServerConnection available");
        String attributeName = JmxUtils.getAttributeName(pd, this.useStrictCasing);
        MBeanAttributeInfo inf = this.allowedAttributes.get(attributeName);
        if (inf == null) {
            throw new InvalidInvocationException("Attribute '" + pd.getName() + "' is not exposed on the management interface");
        }
        if (invocation.getMethod().equals(pd.getReadMethod())) {
            if (inf.isReadable()) {
                return this.serverToUse.getAttribute(this.objectName, attributeName);
            }
            throw new InvalidInvocationException("Attribute '" + attributeName + "' is not readable");
        }
        if (invocation.getMethod().equals(pd.getWriteMethod())) {
            if (inf.isWritable()) {
                this.serverToUse.setAttribute(this.objectName, new Attribute(attributeName, invocation.getArguments()[0]));
                return null;
            }
            throw new InvalidInvocationException("Attribute '" + attributeName + "' is not writable");
        }
        throw new IllegalStateException("Method [" + invocation.getMethod() + "] is neither a bean property getter nor a setter");
    }

    private Object invokeOperation(Method method, Object[] args) throws JMException, IOException {
        String[] signature;
        Assert.state(this.serverToUse != null, "No MBeanServerConnection available");
        MethodCacheKey key = new MethodCacheKey(method.getName(), method.getParameterTypes());
        MBeanOperationInfo info = this.allowedOperations.get(key);
        if (info == null) {
            throw new InvalidInvocationException("Operation '" + method.getName() + "' is not exposed on the management interface");
        }
        synchronized (this.signatureCache) {
            signature = this.signatureCache.get(method);
            if (signature == null) {
                signature = JmxUtils.getMethodSignature(method);
                this.signatureCache.put(method, signature);
            }
        }
        return this.serverToUse.invoke(this.objectName, method.getName(), args, signature);
    }

    @Nullable
    protected Object convertResultValueIfNecessary(@Nullable Object result, MethodParameter parameter) {
        Class<?> elementType;
        Class<?> elementType2;
        Class<?> targetClass = parameter.getParameterType();
        if (result == null) {
            return null;
        }
        try {
            if (ClassUtils.isAssignableValue(targetClass, result)) {
                return result;
            }
            if (result instanceof CompositeData) {
                Method fromMethod = targetClass.getMethod("from", CompositeData.class);
                return ReflectionUtils.invokeMethod(fromMethod, null, result);
            }
            if (result instanceof CompositeData[]) {
                CompositeData[] array = (CompositeData[]) result;
                if (targetClass.isArray()) {
                    return convertDataArrayToTargetArray(array, targetClass);
                }
                if (Collection.class.isAssignableFrom(targetClass) && (elementType2 = ResolvableType.forMethodParameter(parameter).asCollection().resolveGeneric(new int[0])) != null) {
                    return convertDataArrayToTargetCollection(array, targetClass, elementType2);
                }
            } else {
                if (result instanceof TabularData) {
                    Method fromMethod2 = targetClass.getMethod("from", TabularData.class);
                    return ReflectionUtils.invokeMethod(fromMethod2, null, result);
                }
                if (result instanceof TabularData[]) {
                    TabularData[] array2 = (TabularData[]) result;
                    if (targetClass.isArray()) {
                        return convertDataArrayToTargetArray(array2, targetClass);
                    }
                    if (Collection.class.isAssignableFrom(targetClass) && (elementType = ResolvableType.forMethodParameter(parameter).asCollection().resolveGeneric(new int[0])) != null) {
                        return convertDataArrayToTargetCollection(array2, targetClass, elementType);
                    }
                }
            }
            throw new InvocationFailureException("Incompatible result value [" + result + "] for target type [" + targetClass.getName() + "]");
        } catch (NoSuchMethodException e) {
            throw new InvocationFailureException("Could not obtain 'from(CompositeData)' / 'from(TabularData)' method on target type [" + targetClass.getName() + "] for conversion of MXBean data structure [" + result + "]");
        }
    }

    private Object convertDataArrayToTargetArray(Object[] array, Class<?> targetClass) throws NoSuchMethodException {
        Class<?> targetType = targetClass.componentType();
        Method fromMethod = targetType.getMethod("from", array.getClass().componentType());
        Object resultArray = Array.newInstance(targetType, array.length);
        for (int i = 0; i < array.length; i++) {
            Array.set(resultArray, i, ReflectionUtils.invokeMethod(fromMethod, null, array[i]));
        }
        return resultArray;
    }

    private Collection<?> convertDataArrayToTargetCollection(Object[] array, Class<?> collectionType, Class<?> elementType) throws NoSuchMethodException {
        Method fromMethod = elementType.getMethod("from", array.getClass().componentType());
        Collection<Object> resultColl = CollectionFactory.createCollection(collectionType, Array.getLength(array));
        for (Object element : array) {
            resultColl.add(ReflectionUtils.invokeMethod(fromMethod, null, element));
        }
        return resultColl;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        this.connector.close();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/access/MBeanClientInterceptor$MethodCacheKey.class */
    public static final class MethodCacheKey implements Comparable<MethodCacheKey> {
        private final String name;
        private final Class<?>[] parameterTypes;

        public MethodCacheKey(String name, @Nullable Class<?>[] parameterTypes) {
            this.name = name;
            this.parameterTypes = parameterTypes != null ? parameterTypes : new Class[0];
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof MethodCacheKey) {
                    MethodCacheKey that = (MethodCacheKey) other;
                    if (!this.name.equals(that.name) || !Arrays.equals(this.parameterTypes, that.parameterTypes)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String toString() {
            return this.name + "(" + StringUtils.arrayToCommaDelimitedString(this.parameterTypes) + ")";
        }

        @Override // java.lang.Comparable
        public int compareTo(MethodCacheKey other) {
            int result = this.name.compareTo(other.name);
            if (result != 0) {
                return result;
            }
            if (this.parameterTypes.length < other.parameterTypes.length) {
                return -1;
            }
            if (this.parameterTypes.length > other.parameterTypes.length) {
                return 1;
            }
            for (int i = 0; i < this.parameterTypes.length; i++) {
                int result2 = this.parameterTypes[i].getName().compareTo(other.parameterTypes[i].getName());
                if (result2 != 0) {
                    return result2;
                }
            }
            return 0;
        }
    }
}
