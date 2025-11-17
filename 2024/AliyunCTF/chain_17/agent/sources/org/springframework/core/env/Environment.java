package org.springframework.core.env;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/Environment.class */
public interface Environment extends PropertyResolver {
    String[] getActiveProfiles();

    String[] getDefaultProfiles();

    @Deprecated
    boolean acceptsProfiles(String... profiles);

    boolean acceptsProfiles(Profiles profiles);

    default boolean matchesProfiles(String... profileExpressions) {
        return acceptsProfiles(Profiles.of(profileExpressions));
    }
}
