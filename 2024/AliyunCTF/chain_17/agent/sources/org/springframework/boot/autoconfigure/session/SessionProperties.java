package org.springframework.boot.autoconfigure.session;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.boot.web.servlet.DispatcherType;

@ConfigurationProperties(prefix = "spring.session")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/SessionProperties.class */
public class SessionProperties {

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration timeout;
    private Servlet servlet = new Servlet();

    public Duration getTimeout() {
        return this.timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public Servlet getServlet() {
        return this.servlet;
    }

    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    public Duration determineTimeout(Supplier<Duration> fallbackTimeout) {
        return this.timeout != null ? this.timeout : fallbackTimeout.get();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/SessionProperties$Servlet.class */
    public static class Servlet {
        private int filterOrder = -2147483598;
        private Set<DispatcherType> filterDispatcherTypes = new HashSet(Arrays.asList(DispatcherType.ASYNC, DispatcherType.ERROR, DispatcherType.REQUEST));

        public int getFilterOrder() {
            return this.filterOrder;
        }

        public void setFilterOrder(int filterOrder) {
            this.filterOrder = filterOrder;
        }

        public Set<DispatcherType> getFilterDispatcherTypes() {
            return this.filterDispatcherTypes;
        }

        public void setFilterDispatcherTypes(Set<DispatcherType> filterDispatcherTypes) {
            this.filterDispatcherTypes = filterDispatcherTypes;
        }
    }
}
