package org.springframework.boot.liquibase;

import liquibase.exception.ChangeLogParseException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/liquibase/LiquibaseChangelogMissingFailureAnalyzer.class */
class LiquibaseChangelogMissingFailureAnalyzer extends AbstractFailureAnalyzer<ChangeLogParseException> {
    private static final String MESSAGE_SUFFIX = " does not exist";

    LiquibaseChangelogMissingFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, ChangeLogParseException cause) {
        if (cause.getMessage().endsWith(MESSAGE_SUFFIX)) {
            String changelogPath = extractChangelogPath(cause);
            return new FailureAnalysis(getDescription(changelogPath), "Make sure a Liquibase changelog is present at the configured path.", cause);
        }
        return null;
    }

    private String extractChangelogPath(ChangeLogParseException cause) {
        return cause.getMessage().substring(0, cause.getMessage().length() - MESSAGE_SUFFIX.length());
    }

    private String getDescription(String changelogPath) {
        return "Liquibase failed to start because no changelog could be found at '" + changelogPath + "'.";
    }
}
