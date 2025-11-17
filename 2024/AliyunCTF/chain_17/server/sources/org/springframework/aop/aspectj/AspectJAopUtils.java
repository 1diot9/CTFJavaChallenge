package org.springframework.aop.aspectj;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.AfterAdvice;
import org.springframework.aop.BeforeAdvice;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/AspectJAopUtils.class */
public abstract class AspectJAopUtils {
    public static boolean isBeforeAdvice(Advisor anAdvisor) {
        AspectJPrecedenceInformation precedenceInfo = getAspectJPrecedenceInformationFor(anAdvisor);
        if (precedenceInfo != null) {
            return precedenceInfo.isBeforeAdvice();
        }
        return anAdvisor.getAdvice() instanceof BeforeAdvice;
    }

    public static boolean isAfterAdvice(Advisor anAdvisor) {
        AspectJPrecedenceInformation precedenceInfo = getAspectJPrecedenceInformationFor(anAdvisor);
        if (precedenceInfo != null) {
            return precedenceInfo.isAfterAdvice();
        }
        return anAdvisor.getAdvice() instanceof AfterAdvice;
    }

    @Nullable
    public static AspectJPrecedenceInformation getAspectJPrecedenceInformationFor(Advisor anAdvisor) {
        if (anAdvisor instanceof AspectJPrecedenceInformation) {
            AspectJPrecedenceInformation ajpi = (AspectJPrecedenceInformation) anAdvisor;
            return ajpi;
        }
        Advice advice = anAdvisor.getAdvice();
        if (advice instanceof AspectJPrecedenceInformation) {
            AspectJPrecedenceInformation ajpi2 = (AspectJPrecedenceInformation) advice;
            return ajpi2;
        }
        return null;
    }
}
