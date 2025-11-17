package org.springframework.boot.autoconfigure.web.servlet;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.validation.DefaultMessageCodesResolver;

@ConfigurationProperties(prefix = "spring.mvc")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties.class */
public class WebMvcProperties {
    private DefaultMessageCodesResolver.Format messageCodesResolverFormat;
    private boolean logRequestDetails;
    private final Format format = new Format();
    private boolean dispatchTraceRequest = false;
    private boolean dispatchOptionsRequest = true;
    private boolean publishRequestHandledEvents = true;

    @Deprecated(since = "3.2.0", forRemoval = true)
    private boolean throwExceptionIfNoHandlerFound = true;
    private boolean logResolvedException = false;
    private String staticPathPattern = "/**";
    private String webjarsPathPattern = "/webjars/**";
    private final Async async = new Async();
    private final Servlet servlet = new Servlet();
    private final View view = new View();
    private final Contentnegotiation contentnegotiation = new Contentnegotiation();
    private final Pathmatch pathmatch = new Pathmatch();
    private final Problemdetails problemdetails = new Problemdetails();

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties$MatchingStrategy.class */
    public enum MatchingStrategy {
        ANT_PATH_MATCHER,
        PATH_PATTERN_PARSER
    }

    public DefaultMessageCodesResolver.Format getMessageCodesResolverFormat() {
        return this.messageCodesResolverFormat;
    }

    public void setMessageCodesResolverFormat(DefaultMessageCodesResolver.Format messageCodesResolverFormat) {
        this.messageCodesResolverFormat = messageCodesResolverFormat;
    }

    public Format getFormat() {
        return this.format;
    }

    public boolean isPublishRequestHandledEvents() {
        return this.publishRequestHandledEvents;
    }

    public void setPublishRequestHandledEvents(boolean publishRequestHandledEvents) {
        this.publishRequestHandledEvents = publishRequestHandledEvents;
    }

    @DeprecatedConfigurationProperty(reason = "DispatcherServlet property is deprecated for removal and should no longer need to be configured", since = "3.2.0")
    @Deprecated(since = "3.2.0", forRemoval = true)
    public boolean isThrowExceptionIfNoHandlerFound() {
        return this.throwExceptionIfNoHandlerFound;
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    public void setThrowExceptionIfNoHandlerFound(boolean throwExceptionIfNoHandlerFound) {
        this.throwExceptionIfNoHandlerFound = throwExceptionIfNoHandlerFound;
    }

    public boolean isLogRequestDetails() {
        return this.logRequestDetails;
    }

    public void setLogRequestDetails(boolean logRequestDetails) {
        this.logRequestDetails = logRequestDetails;
    }

    public boolean isLogResolvedException() {
        return this.logResolvedException;
    }

    public void setLogResolvedException(boolean logResolvedException) {
        this.logResolvedException = logResolvedException;
    }

    public boolean isDispatchOptionsRequest() {
        return this.dispatchOptionsRequest;
    }

    public void setDispatchOptionsRequest(boolean dispatchOptionsRequest) {
        this.dispatchOptionsRequest = dispatchOptionsRequest;
    }

    public boolean isDispatchTraceRequest() {
        return this.dispatchTraceRequest;
    }

    public void setDispatchTraceRequest(boolean dispatchTraceRequest) {
        this.dispatchTraceRequest = dispatchTraceRequest;
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

    public Async getAsync() {
        return this.async;
    }

    public Servlet getServlet() {
        return this.servlet;
    }

    public View getView() {
        return this.view;
    }

    public Contentnegotiation getContentnegotiation() {
        return this.contentnegotiation;
    }

    public Pathmatch getPathmatch() {
        return this.pathmatch;
    }

    public Problemdetails getProblemdetails() {
        return this.problemdetails;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties$Async.class */
    public static class Async {
        private Duration requestTimeout;

        public Duration getRequestTimeout() {
            return this.requestTimeout;
        }

        public void setRequestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties$Servlet.class */
    public static class Servlet {
        private String path = "/";
        private int loadOnStartup = -1;

        public String getPath() {
            return this.path;
        }

        public void setPath(String path) {
            Assert.notNull(path, "Path must not be null");
            Assert.isTrue(!path.contains("*"), "Path must not contain wildcards");
            this.path = path;
        }

        public int getLoadOnStartup() {
            return this.loadOnStartup;
        }

        public void setLoadOnStartup(int loadOnStartup) {
            this.loadOnStartup = loadOnStartup;
        }

        public String getServletMapping() {
            if (this.path.equals("") || this.path.equals("/")) {
                return "/";
            }
            if (this.path.endsWith("/")) {
                return this.path + "*";
            }
            return this.path + "/*";
        }

        public String getPath(String path) {
            String prefix = getServletPrefix();
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            return prefix + path;
        }

        public String getServletPrefix() {
            String result = this.path;
            int index = result.indexOf(42);
            if (index != -1) {
                result = result.substring(0, index);
            }
            if (result.endsWith("/")) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties$View.class */
    public static class View {
        private String prefix;
        private String suffix;

        public String getPrefix() {
            return this.prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getSuffix() {
            return this.suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties$Contentnegotiation.class */
    public static class Contentnegotiation {
        private boolean favorParameter = false;
        private Map<String, MediaType> mediaTypes = new LinkedHashMap();
        private String parameterName;

        public boolean isFavorParameter() {
            return this.favorParameter;
        }

        public void setFavorParameter(boolean favorParameter) {
            this.favorParameter = favorParameter;
        }

        public Map<String, MediaType> getMediaTypes() {
            return this.mediaTypes;
        }

        public void setMediaTypes(Map<String, MediaType> mediaTypes) {
            this.mediaTypes = mediaTypes;
        }

        public String getParameterName() {
            return this.parameterName;
        }

        public void setParameterName(String parameterName) {
            this.parameterName = parameterName;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties$Pathmatch.class */
    public static class Pathmatch {
        private MatchingStrategy matchingStrategy = MatchingStrategy.PATH_PATTERN_PARSER;

        public MatchingStrategy getMatchingStrategy() {
            return this.matchingStrategy;
        }

        public void setMatchingStrategy(MatchingStrategy matchingStrategy) {
            this.matchingStrategy = matchingStrategy;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties$Format.class */
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

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties$Problemdetails.class */
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
