package org.springframework.boot.autoconfigure.webservices;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "spring.webservices")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/webservices/WebServicesProperties.class */
public class WebServicesProperties {
    private String path = "/services";
    private final Servlet servlet = new Servlet();

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        Assert.notNull(path, "Path must not be null");
        Assert.isTrue(path.length() > 1, "Path must have length greater than 1");
        Assert.isTrue(path.startsWith("/"), "Path must start with '/'");
        this.path = path;
    }

    public Servlet getServlet() {
        return this.servlet;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/webservices/WebServicesProperties$Servlet.class */
    public static class Servlet {
        private Map<String, String> init = new HashMap();
        private int loadOnStartup = -1;

        public Map<String, String> getInit() {
            return this.init;
        }

        public void setInit(Map<String, String> init) {
            this.init = init;
        }

        public int getLoadOnStartup() {
            return this.loadOnStartup;
        }

        public void setLoadOnStartup(int loadOnStartup) {
            this.loadOnStartup = loadOnStartup;
        }
    }
}
