package org.springframework.aop.aspectj;

import java.util.Iterator;
import java.util.List;
import org.springframework.aop.Advisor;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/AspectJProxyUtils.class */
public abstract class AspectJProxyUtils {
    public static boolean makeAdvisorChainAspectJCapableIfNecessary(List<Advisor> advisors) {
        if (!advisors.isEmpty()) {
            boolean foundAspectJAdvice = false;
            Iterator<Advisor> it = advisors.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Advisor advisor = it.next();
                if (isAspectJAdvice(advisor)) {
                    foundAspectJAdvice = true;
                    break;
                }
            }
            if (foundAspectJAdvice && !advisors.contains(ExposeInvocationInterceptor.ADVISOR)) {
                advisors.add(0, ExposeInvocationInterceptor.ADVISOR);
                return true;
            }
            return false;
        }
        return false;
    }

    private static boolean isAspectJAdvice(Advisor advisor) {
        if (!(advisor instanceof InstantiationModelAwarePointcutAdvisor) && !(advisor.getAdvice() instanceof AbstractAspectJAdvice)) {
            if (advisor instanceof PointcutAdvisor) {
                PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
                if (pointcutAdvisor.getPointcut() instanceof AspectJExpressionPointcut) {
                }
            }
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isVariableName(@Nullable String name) {
        if (!StringUtils.hasLength(name) || !Character.isJavaIdentifierStart(name.charAt(0))) {
            return false;
        }
        for (int i = 1; i < name.length(); i++) {
            if (!Character.isJavaIdentifierPart(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
