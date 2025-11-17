package org.springframework.boot.autoconfigure.web.reactive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "spring.webflux")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxProperties.class */
public class WebFluxProperties {
    private String basePath;
    private final Format format = new Format();
    private final Problemdetails problemdetails = new Problemdetails();
    private String staticPathPattern = "/**";
    private String webjarsPathPattern = "/webjars/**";

    public String getBasePath() {
        return this.basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = cleanBasePath(basePath);
    }

    private String cleanBasePath(String basePath) {
        String candidate = null;
        if (StringUtils.hasLength(basePath)) {
            candidate = basePath.strip();
        }
        if (StringUtils.hasText(candidate)) {
            if (!candidate.startsWith("/")) {
                candidate = "/" + candidate;
            }
            if (candidate.endsWith("/")) {
                candidate = candidate.substring(0, candidate.length() - 1);
            }
        }
        return candidate;
    }

    public Format getFormat() {
        return this.format;
    }

    public Problemdetails getProblemdetails() {
        return this.problemdetails;
    }

    public String getStaticPathPattern() {
        return this.staticPathPattern;
    }

    public void setStaticPathPattern(String staticPathPattern) {
        this.staticPathPattern = staticPathPattern;
    }

    public String getWebjarsPathPattern() {
        return this.webjarsPathPattern;
    }

    public void setWebjarsPathPattern(String webjarsPathPattern) {
        this.webjarsPathPattern = webjarsPathPattern;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxProperties$Format.class */
    public static class Format {
        private String date;
        private String time;
        private String dateTime;

        public String getDate() {
            return this.date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return this.time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getDateTime() {
            return this.dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxProperties$Problemdetails.class */
    public static class Problemdetails {
        private boolean enabled = false;

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
