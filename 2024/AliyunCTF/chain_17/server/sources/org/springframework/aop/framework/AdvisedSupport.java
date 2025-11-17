package org.springframework.aop.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionInfo;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.target.EmptyTargetSource;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/AdvisedSupport.class */
public class AdvisedSupport extends ProxyConfig implements Advised {
    private static final long serialVersionUID = 2651364800145442165L;
    public static final TargetSource EMPTY_TARGET_SOURCE = EmptyTargetSource.INSTANCE;
    TargetSource targetSource;
    private boolean preFiltered;
    private AdvisorChainFactory advisorChainFactory;
    private transient Map<MethodCacheKey, List<Object>> methodCache;
    private List<Class<?>> interfaces;
    private List<Advisor> advisors;
    private List<Advisor> advisorKey;

    @Nullable
    volatile transient Object proxyMetadataCache;

    public AdvisedSupport() {
        this.targetSource = EMPTY_TARGET_SOURCE;
        this.preFiltered = false;
        this.interfaces = new ArrayList();
        this.advisors = new ArrayList();
        this.advisorKey = this.advisors;
        this.advisorChainFactory = DefaultAdvisorChainFactory.INSTANCE;
        this.methodCache = new ConcurrentHashMap(32);
    }

    public AdvisedSupport(Class<?>... interfaces) {
        this();
        setInterfaces(interfaces);
    }

    private AdvisedSupport(AdvisorChainFactory advisorChainFactory, Map<MethodCacheKey, List<Object>> methodCache) {
        this.targetSource = EMPTY_TARGET_SOURCE;
        this.preFiltered = false;
        this.interfaces = new ArrayList();
        this.advisors = new ArrayList();
        this.advisorKey = this.advisors;
        this.advisorChainFactory = advisorChainFactory;
        this.methodCache = methodCache;
    }

    public void setTarget(Object target) {
        setTargetSource(new SingletonTargetSource(target));
    }

    @Override // org.springframework.aop.framework.Advised
    public void setTargetSource(@Nullable TargetSource targetSource) {
        this.targetSource = targetSource != null ? targetSource : EMPTY_TARGET_SOURCE;
    }

    @Override // org.springframework.aop.framework.Advised
    public TargetSource getTargetSource() {
        return this.targetSource;
    }

    public void setTargetClass(@Nullable Class<?> targetClass) {
        this.targetSource = EmptyTargetSource.forClass(targetClass);
    }

    @Override // org.springframework.aop.TargetClassAware
    @Nullable
    public Class<?> getTargetClass() {
        return this.targetSource.getTargetClass();
    }

    @Override // org.springframework.aop.framework.Advised
    public void setPreFiltered(boolean preFiltered) {
        this.preFiltered = preFiltered;
    }

    @Override // org.springframework.aop.framework.Advised
    public boolean isPreFiltered() {
        return this.preFiltered;
    }

    public void setAdvisorChainFactory(AdvisorChainFactory advisorChainFactory) {
        Assert.notNull(advisorChainFactory, "AdvisorChainFactory must not be null");
        this.advisorChainFactory = advisorChainFactory;
    }

    public AdvisorChainFactory getAdvisorChainFactory() {
        return this.advisorChainFactory;
    }

    public void setInterfaces(Class<?>... interfaces) {
        Assert.notNull(interfaces, "Interfaces must not be null");
        this.interfaces.clear();
        for (Class<?> ifc : interfaces) {
            addInterface(ifc);
        }
    }

    public void addInterface(Class<?> intf) {
        Assert.notNull(intf, "Interface must not be null");
        if (!intf.isInterface()) {
            throw new IllegalArgumentException("[" + intf.getName() + "] is not an interface");
        }
        if (!this.interfaces.contains(intf)) {
            this.interfaces.add(intf);
            adviceChanged();
        }
    }

    public boolean removeInterface(Class<?> intf) {
        return this.interfaces.remove(intf);
    }

    @Override // org.springframework.aop.framework.Advised
    public Class<?>[] getProxiedInterfaces() {
        return ClassUtils.toClassArray(this.interfaces);
    }

    @Override // org.springframework.aop.framework.Advised
    public boolean isInterfaceProxied(Class<?> intf) {
        for (Class<?> proxyIntf : this.interfaces) {
            if (intf.isAssignableFrom(proxyIntf)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.aop.framework.Advised
    public final Advisor[] getAdvisors() {
        return (Advisor[]) this.advisors.toArray(new Advisor[0]);
    }

    @Override // org.springframework.aop.framework.Advised
    public int getAdvisorCount() {
        return this.advisors.size();
    }

    @Override // org.springframework.aop.framework.Advised
    public void addAdvisor(Advisor advisor) {
        int pos = this.advisors.size();
        addAdvisor(pos, advisor);
    }

    @Override // org.springframework.aop.framework.Advised
    public void addAdvisor(int pos, Advisor advisor) throws AopConfigException {
        if (advisor instanceof IntroductionAdvisor) {
            IntroductionAdvisor introductionAdvisor = (IntroductionAdvisor) advisor;
            validateIntroductionAdvisor(introductionAdvisor);
        }
        addAdvisorInternal(pos, advisor);
    }

    @Override // org.springframework.aop.framework.Advised
    public boolean removeAdvisor(Advisor advisor) {
        int index = indexOf(advisor);
        if (index == -1) {
            return false;
        }
        removeAdvisor(index);
        return true;
    }

    @Override // org.springframework.aop.framework.Advised
    public void removeAdvisor(int index) throws AopConfigException {
        if (isFrozen()) {
            throw new AopConfigException("Cannot remove Advisor: Configuration is frozen.");
        }
        if (index < 0 || index > this.advisors.size() - 1) {
            throw new AopConfigException("Advisor index " + index + " is out of bounds: This configuration only has " + this.advisors.size() + " advisors.");
        }
        Advisor advisor = this.advisors.remove(index);
        if (advisor instanceof IntroductionAdvisor) {
            IntroductionAdvisor introductionAdvisor = (IntroductionAdvisor) advisor;
            for (Class<?> ifc : introductionAdvisor.getInterfaces()) {
                removeInterface(ifc);
            }
        }
        adviceChanged();
    }

    @Override // org.springframework.aop.framework.Advised
    public int indexOf(Advisor advisor) {
        Assert.notNull(advisor, "Advisor must not be null");
        return this.advisors.indexOf(advisor);
    }

    @Override // org.springframework.aop.framework.Advised
    public boolean replaceAdvisor(Advisor a, Advisor b) throws AopConfigException {
        Assert.notNull(a, "Advisor a must not be null");
        Assert.notNull(b, "Advisor b must not be null");
        int index = indexOf(a);
        if (index == -1) {
            return false;
        }
        removeAdvisor(index);
        addAdvisor(index, b);
        return true;
    }

    public void addAdvisors(Advisor... advisors) {
        addAdvisors(Arrays.asList(advisors));
    }

    public void addAdvisors(Collection<Advisor> advisors) {
        if (isFrozen()) {
            throw new AopConfigException("Cannot add advisor: Configuration is frozen.");
        }
        if (!CollectionUtils.isEmpty(advisors)) {
            for (Advisor advisor : advisors) {
                if (advisor instanceof IntroductionAdvisor) {
                    IntroductionAdvisor introductionAdvisor = (IntroductionAdvisor) advisor;
                    validateIntroductionAdvisor(introductionAdvisor);
                }
                Assert.notNull(advisor, "Advisor must not be null");
                this.advisors.add(advisor);
            }
            adviceChanged();
        }
    }

    private void validateIntroductionAdvisor(IntroductionAdvisor advisor) {
        advisor.validateInterfaces();
        Class<?>[] ifcs = advisor.getInterfaces();
        for (Class<?> ifc : ifcs) {
            addInterface(ifc);
        }
    }

    private void addAdvisorInternal(int pos, Advisor advisor) throws AopConfigException {
        Assert.notNull(advisor, "Advisor must not be null");
        if (isFrozen()) {
            throw new AopConfigException("Cannot add advisor: Configuration is frozen.");
        }
        if (pos > this.advisors.size()) {
            throw new IllegalArgumentException("Illegal position " + pos + " in advisor list with size " + this.advisors.size());
        }
        this.advisors.add(pos, advisor);
        adviceChanged();
    }

    protected final List<Advisor> getAdvisorsInternal() {
        return this.advisors;
    }

    @Override // org.springframework.aop.framework.Advised
    public void addAdvice(Advice advice) throws AopConfigException {
        int pos = this.advisors.size();
        addAdvice(pos, advice);
    }

    @Override // org.springframework.aop.framework.Advised
    public void addAdvice(int pos, Advice advice) throws AopConfigException {
        Assert.notNull(advice, "Advice must not be null");
        if (advice instanceof IntroductionInfo) {
            IntroductionInfo introductionInfo = (IntroductionInfo) advice;
            addAdvisor(pos, new DefaultIntroductionAdvisor(advice, introductionInfo));
        } else {
            if (advice instanceof DynamicIntroductionAdvice) {
                throw new AopConfigException("DynamicIntroductionAdvice may only be added as part of IntroductionAdvisor");
            }
            addAdvisor(pos, new DefaultPointcutAdvisor(advice));
        }
    }

    @Override // org.springframework.aop.framework.Advised
    public boolean removeAdvice(Advice advice) throws AopConfigException {
        int index = indexOf(advice);
        if (index == -1) {
            return false;
        }
        removeAdvisor(index);
        return true;
    }

    @Override // org.springframework.aop.framework.Advised
    public int indexOf(Advice advice) {
        Assert.notNull(advice, "Advice must not be null");
        for (int i = 0; i < this.advisors.size(); i++) {
            Advisor advisor = this.advisors.get(i);
            if (advisor.getAdvice() == advice) {
                return i;
            }
        }
        return -1;
    }

    public boolean adviceIncluded(@Nullable Advice advice) {
        if (advice != null) {
            for (Advisor advisor : this.advisors) {
                if (advisor.getAdvice() == advice) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public int countAdvicesOfType(@Nullable Class<?> adviceClass) {
        int count = 0;
        if (adviceClass != null) {
            for (Advisor advisor : this.advisors) {
                if (adviceClass.isInstance(advisor.getAdvice())) {
                    count++;
                }
            }
        }
        return count;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, @Nullable Class<?> targetClass) {
        return this.methodCache.computeIfAbsent(new MethodCacheKey(method), k -> {
            return this.advisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice(this, method, targetClass);
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void adviceChanged() {
        this.methodCache.clear();
        this.proxyMetadataCache = null;
    }

    protected void copyConfigurationFrom(AdvisedSupport other) {
        copyConfigurationFrom(other, other.targetSource, new ArrayList(other.advisors));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void copyConfigurationFrom(AdvisedSupport other, TargetSource targetSource, List<Advisor> advisors) {
        copyFrom(other);
        this.targetSource = targetSource;
        this.advisorChainFactory = other.advisorChainFactory;
        this.interfaces = new ArrayList(other.interfaces);
        for (Advisor advisor : advisors) {
            if (advisor instanceof IntroductionAdvisor) {
                IntroductionAdvisor introductionAdvisor = (IntroductionAdvisor) advisor;
                validateIntroductionAdvisor(introductionAdvisor);
            }
            Assert.notNull(advisor, "Advisor must not be null");
            this.advisors.add(advisor);
        }
        adviceChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdvisedSupport getConfigurationOnlyCopy() {
        AdvisedSupport copy = new AdvisedSupport(this.advisorChainFactory, this.methodCache);
        copy.copyFrom(this);
        copy.targetSource = EmptyTargetSource.forClass(getTargetClass(), getTargetSource().isStatic());
        copy.interfaces = new ArrayList(this.interfaces);
        copy.advisors = new ArrayList(this.advisors);
        copy.advisorKey = new ArrayList(this.advisors.size());
        for (Advisor advisor : this.advisors) {
            copy.advisorKey.add(new AdvisorKeyEntry(advisor));
        }
        return copy;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reduceToAdvisorKey() {
        this.advisors = this.advisorKey;
        this.methodCache = Collections.emptyMap();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Object getAdvisorKey() {
        return this.advisorKey;
    }

    @Override // org.springframework.aop.framework.Advised
    public String toProxyConfigString() {
        return toString();
    }

    @Override // org.springframework.aop.framework.ProxyConfig
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append(": ").append(this.interfaces.size()).append(" interfaces ");
        sb.append(ClassUtils.classNamesToString(this.interfaces)).append("; ");
        sb.append(this.advisors.size()).append(" advisors ");
        sb.append(this.advisors).append("; ");
        sb.append("targetSource [").append(this.targetSource).append("]; ");
        sb.append(super.toString());
        return sb.toString();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.methodCache = new ConcurrentHashMap(32);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/AdvisedSupport$MethodCacheKey.class */
    public static final class MethodCacheKey implements Comparable<MethodCacheKey> {
        private final Method method;
        private final int hashCode;

        public MethodCacheKey(Method method) {
            this.method = method;
            this.hashCode = method.hashCode();
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof MethodCacheKey) {
                    MethodCacheKey that = (MethodCacheKey) other;
                    if (this.method == that.method) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public String toString() {
            return this.method.toString();
        }

        @Override // java.lang.Comparable
        public int compareTo(MethodCacheKey other) {
            int result = this.method.getName().compareTo(other.method.getName());
            if (result == 0) {
                result = this.method.toString().compareTo(other.method.toString());
            }
            return result;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/AdvisedSupport$AdvisorKeyEntry.class */
    private static final class AdvisorKeyEntry implements Advisor {
        private final Class<?> adviceType;

        @Nullable
        private final String classFilterKey;

        @Nullable
        private final String methodMatcherKey;

        public AdvisorKeyEntry(Advisor advisor) {
            this.adviceType = advisor.getAdvice().getClass();
            if (advisor instanceof PointcutAdvisor) {
                PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
                Pointcut pointcut = pointcutAdvisor.getPointcut();
                this.classFilterKey = pointcut.getClassFilter().toString();
                this.methodMatcherKey = pointcut.getMethodMatcher().toString();
                return;
            }
            this.classFilterKey = null;
            this.methodMatcherKey = null;
        }

        @Override // org.springframework.aop.Advisor
        public Advice getAdvice() {
            throw new UnsupportedOperationException();
        }

        public boolean equals(Object other) {
            if (this != other) {
                if (other instanceof AdvisorKeyEntry) {
                    AdvisorKeyEntry that = (AdvisorKeyEntry) other;
                    if (this.adviceType != that.adviceType || !ObjectUtils.nullSafeEquals(this.classFilterKey, that.classFilterKey) || !ObjectUtils.nullSafeEquals(this.methodMatcherKey, that.methodMatcherKey)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.adviceType.hashCode();
        }
    }
}
