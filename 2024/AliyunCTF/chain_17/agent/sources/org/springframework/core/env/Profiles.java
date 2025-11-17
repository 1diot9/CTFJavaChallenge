package org.springframework.core.env;

import java.util.function.Predicate;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/Profiles.class */
public interface Profiles {
    boolean matches(Predicate<String> isProfileActive);

    static Profiles of(String... profileExpressions) {
        return ProfilesParser.parse(profileExpressions);
    }
}
