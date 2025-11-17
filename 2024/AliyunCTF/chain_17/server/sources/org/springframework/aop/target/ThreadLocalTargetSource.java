package org.springframework.aop.target;

import java.util.HashSet;
import java.util.Set;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.NamedThreadLocal;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/ThreadLocalTargetSource.class */
public class ThreadLocalTargetSource extends AbstractPrototypeBasedTargetSource implements ThreadLocalTargetSourceStats, DisposableBean {
    private final ThreadLocal<Object> targetInThread = new NamedThreadLocal<Object>("Thread-local instance of bean") { // from class: org.springframework.aop.target.ThreadLocalTargetSource.1
        @Override // org.springframework.core.NamedThreadLocal
        public String toString() {
            return super.toString() + " '" + ThreadLocalTargetSource.this.getTargetBeanName() + "'";
        }
    };
    private final Set<Object> targetSet = new HashSet();
    private int invocationCount;
    private int hitCount;

    @Override // org.springframework.aop.TargetSource
    public Object getTarget() throws BeansException {
        this.invocationCount++;
        Object target = this.targetInThread.get();
        if (target == null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("No target for prototype '" + getTargetBeanName() + "' bound to thread: creating one and binding it to thread '" + Thread.currentThread().getName() + "'");
            }
            target = newPrototypeInstance();
            this.targetInThread.set(target);
            synchronized (this.targetSet) {
                this.targetSet.add(target);
            }
        } else {
            this.hitCount++;
        }
        return target;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        this.logger.debug("Destroying ThreadLocalTargetSource bindings");
        synchronized (this.targetSet) {
            for (Object target : this.targetSet) {
                destroyPrototypeInstance(target);
            }
            this.targetSet.clear();
        }
        this.targetInThread.remove();
    }

    @Override // org.springframework.aop.target.ThreadLocalTargetSourceStats
    public int getInvocationCount() {
        return this.invocationCount;
    }

    @Override // org.springframework.aop.target.ThreadLocalTargetSourceStats
    public int getHitCount() {
        return this.hitCount;
    }

    @Override // org.springframework.aop.target.ThreadLocalTargetSourceStats
    public int getObjectCount() {
        int size;
        synchronized (this.targetSet) {
            size = this.targetSet.size();
        }
        return size;
    }

    public IntroductionAdvisor getStatsMixin() {
        DelegatingIntroductionInterceptor dii = new DelegatingIntroductionInterceptor(this);
        return new DefaultIntroductionAdvisor(dii, (Class<?>) ThreadLocalTargetSourceStats.class);
    }
}
