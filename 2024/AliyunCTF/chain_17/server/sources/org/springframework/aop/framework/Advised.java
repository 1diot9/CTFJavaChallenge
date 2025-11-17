package org.springframework.aop.framework;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.TargetSource;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/Advised.class */
public interface Advised extends TargetClassAware {
    boolean isFrozen();

    boolean isProxyTargetClass();

    Class<?>[] getProxiedInterfaces();

    boolean isInterfaceProxied(Class<?> intf);

    void setTargetSource(TargetSource targetSource);

    TargetSource getTargetSource();

    void setExposeProxy(boolean exposeProxy);

    boolean isExposeProxy();

    void setPreFiltered(boolean preFiltered);

    boolean isPreFiltered();

    Advisor[] getAdvisors();

    void addAdvisor(Advisor advisor) throws AopConfigException;

    void addAdvisor(int pos, Advisor advisor) throws AopConfigException;

    boolean removeAdvisor(Advisor advisor);

    void removeAdvisor(int index) throws AopConfigException;

    int indexOf(Advisor advisor);

    boolean replaceAdvisor(Advisor a, Advisor b) throws AopConfigException;

    void addAdvice(Advice advice) throws AopConfigException;

    void addAdvice(int pos, Advice advice) throws AopConfigException;

    boolean removeAdvice(Advice advice);

    int indexOf(Advice advice);

    String toProxyConfigString();

    default int getAdvisorCount() {
        return getAdvisors().length;
    }
}
