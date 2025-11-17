package org.springframework.boot.autoconfigure.security;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/DefaultWebSecurityCondition.class */
class DefaultWebSecurityCondition extends AllNestedConditions {
    DefaultWebSecurityCondition() {
        super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnClass({SecurityFilterChain.class, HttpSecurity.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/DefaultWebSecurityCondition$Classes.class */
    static class Classes {
        Classes() {
        }
    }

    @ConditionalOnMissingBean({SecurityFilterChain.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/DefaultWebSecurityCondition$Beans.class */
    static class Beans {
        Beans() {
        }
    }
}
