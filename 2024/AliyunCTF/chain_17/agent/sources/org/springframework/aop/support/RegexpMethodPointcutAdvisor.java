package org.springframework.aop.support;

import java.io.Serializable;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/RegexpMethodPointcutAdvisor.class */
public class RegexpMethodPointcutAdvisor extends AbstractGenericPointcutAdvisor {

    @Nullable
    private String[] patterns;

    @Nullable
    private AbstractRegexpMethodPointcut pointcut;
    private final Object pointcutMonitor = new SerializableMonitor();

    public RegexpMethodPointcutAdvisor() {
    }

    public RegexpMethodPointcutAdvisor(Advice advice) {
        setAdvice(advice);
    }

    public RegexpMethodPointcutAdvisor(String pattern, Advice advice) {
        setPattern(pattern);
        setAdvice(advice);
    }

    public RegexpMethodPointcutAdvisor(String[] patterns, Advice advice) {
        setPatterns(patterns);
        setAdvice(advice);
    }

    public void setPattern(String pattern) {
        setPatterns(pattern);
    }

    public void setPatterns(String... patterns) {
        this.patterns = patterns;
    }

    @Override // org.springframework.aop.PointcutAdvisor
    public Pointcut getPointcut() {
        AbstractRegexpMethodPointcut abstractRegexpMethodPointcut;
        synchronized (this.pointcutMonitor) {
            if (this.pointcut == null) {
                this.pointcut = createPointcut();
                if (this.patterns != null) {
                    this.pointcut.setPatterns(this.patterns);
                }
            }
            abstractRegexpMethodPointcut = this.pointcut;
        }
        return abstractRegexpMethodPointcut;
    }

    protected AbstractRegexpMethodPointcut createPointcut() {
        return new JdkRegexpMethodPointcut();
    }

    @Override // org.springframework.aop.support.AbstractGenericPointcutAdvisor
    public String toString() {
        return getClass().getName() + ": advice [" + getAdvice() + "], pointcut patterns " + ObjectUtils.nullSafeToString((Object[]) this.patterns);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/RegexpMethodPointcutAdvisor$SerializableMonitor.class */
    private static class SerializableMonitor implements Serializable {
        private SerializableMonitor() {
        }
    }
}
