package org.springframework.boot.autoconfigure.jdbc;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

@ConfigurationProperties(prefix = "spring.jdbc")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/JdbcProperties.class */
public class JdbcProperties {
    private final Template template = new Template();

    public Template getTemplate() {
        return this.template;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/JdbcProperties$Template.class */
    public static class Template {
        private int fetchSize = -1;
        private int maxRows = -1;

        @DurationUnit(ChronoUnit.SECONDS)
        private Duration queryTimeout;

        public int getFetchSize() {
            return this.fetchSize;
        }

        public void setFetchSize(int fetchSize) {
            this.fetchSize = fetchSize;
        }

        public int getMaxRows() {
            return this.maxRows;
        }

        public void setMaxRows(int maxRows) {
            this.maxRows = maxRows;
        }

        public Duration getQueryTimeout() {
            return this.queryTimeout;
        }

        public void setQueryTimeout(Duration queryTimeout) {
            this.queryTimeout = queryTimeout;
        }
    }
}
