package org.springframework.beans.factory.aot;

import java.util.function.UnaryOperator;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanRegistrationAotContribution.class */
public interface BeanRegistrationAotContribution {
    void applyTo(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode);

    default BeanRegistrationCodeFragments customizeBeanRegistrationCodeFragments(GenerationContext generationContext, BeanRegistrationCodeFragments codeFragments) {
        return codeFragments;
    }

    static BeanRegistrationAotContribution withCustomCodeFragments(final UnaryOperator<BeanRegistrationCodeFragments> defaultCodeFragments) {
        Assert.notNull(defaultCodeFragments, "'defaultCodeFragments' must not be null");
        return new BeanRegistrationAotContribution() { // from class: org.springframework.beans.factory.aot.BeanRegistrationAotContribution.1
            @Override // org.springframework.beans.factory.aot.BeanRegistrationAotContribution
            public BeanRegistrationCodeFragments customizeBeanRegistrationCodeFragments(GenerationContext generationContext, BeanRegistrationCodeFragments codeFragments) {
                return (BeanRegistrationCodeFragments) defaultCodeFragments.apply(codeFragments);
            }

            @Override // org.springframework.beans.factory.aot.BeanRegistrationAotContribution
            public void applyTo(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode) {
            }
        };
    }

    @Nullable
    static BeanRegistrationAotContribution concat(@Nullable BeanRegistrationAotContribution a, @Nullable BeanRegistrationAotContribution b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return (generationContext, beanRegistrationCode) -> {
            a.applyTo(generationContext, beanRegistrationCode);
            b.applyTo(generationContext, beanRegistrationCode);
        };
    }
}
