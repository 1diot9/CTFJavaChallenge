package org.springframework.boot.autoconfigure.jersey;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.jersey")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jersey/JerseyProperties.class */
public class JerseyProperties {
    private Type type = Type.SERVLET;
    private Map<String, String> init = new HashMap();
    private final Filter filter = new Filter();
    private final Servlet servlet = new Servlet();
    private String applicationPath;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jersey/JerseyProperties$Type.class */
    public enum Type {
        SERVLET,
        FILTER
    }

    public Filter getFilter() {
        return this.filter;
    }

    public Servlet getServlet() {
        return this.servlet;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Map<String, String> getInit() {
        return this.init;
    }

    public void setInit(Map<String, String> init) {
        this.init = init;
    }

    public String getApplicationPath() {
        return this.applicationPath;
    }

    public void setApplicationPath(String applicationPath) {
        this.applicationPath = applicationPath;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jersey/JerseyProperties$Filter.class */
    public static class Filter {
        private int order;

        public int getOrder() {
            return this.order;
        }

        public void setOrder(int order) {
            this.order = order;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jersey/JerseyProperties$Servlet.class */
    public static class Servlet {
        private int loadOnStartup = -1;

        public int getLoadOnStartup() {
            return this.loadOnStartup;
        }

        public void setLoadOnStartup(int loadOnStartup) {
            this.loadOnStartup = loadOnStartup;
        }
    }
}
