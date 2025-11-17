package org.springframework.boot.autoconfigure.sql.init;

import java.util.Locale;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/sql/init/OnDatabaseInitializationCondition.class */
public class OnDatabaseInitializationCondition extends SpringBootCondition {
    private final String name;
    private final String[] propertyNames;

    public OnDatabaseInitializationCondition(String name, String... propertyNames) {
        this.name = name;
        this.propertyNames = propertyNames;
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        String propertyName = getConfiguredProperty(environment);
        DatabaseInitializationMode mode = getDatabaseInitializationMode(environment, propertyName);
        boolean match = match(mode);
        String messagePrefix = propertyName != null ? propertyName : "default value";
        return new ConditionOutcome(match, ConditionMessage.forCondition(this.name + "Database Initialization", new Object[0]).because(messagePrefix + " is " + mode));
    }

    private boolean match(DatabaseInitializationMode mode) {
        return !mode.equals(DatabaseInitializationMode.NEVER);
    }

    private DatabaseInitializationMode getDatabaseInitializationMode(Environment environment, String propertyName) {
        if (StringUtils.hasText(propertyName)) {
            String candidate = environment.getProperty(propertyName, "embedded").toUpperCase(Locale.ENGLISH);
            if (StringUtils.hasText(candidate)) {
                return DatabaseInitializationMode.valueOf(candidate);
            }
        }
        return DatabaseInitializationMode.EMBEDDED;
    }

    private String getConfiguredProperty(Environment environment) {
        for (String propertyName : this.propertyNames) {
            if (environment.containsProperty(propertyName)) {
                return propertyName;
            }
        }
        return null;
    }
}
