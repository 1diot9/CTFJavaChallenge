package org.springframework.web.cors;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/cors/CorsConfiguration.class */
public class CorsConfiguration {
    public static final String ALL = "*";
    private static final List<String> ALL_LIST = Collections.singletonList("*");
    private static final OriginPattern ALL_PATTERN = new OriginPattern("*");
    private static final List<OriginPattern> ALL_PATTERN_LIST = Collections.singletonList(ALL_PATTERN);
    private static final List<String> DEFAULT_PERMIT_ALL = Collections.singletonList("*");
    private static final List<HttpMethod> DEFAULT_METHODS = List.of(HttpMethod.GET, HttpMethod.HEAD);
    private static final List<String> DEFAULT_PERMIT_METHODS = List.of(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name());

    @Nullable
    private List<String> allowedOrigins;

    @Nullable
    private List<OriginPattern> allowedOriginPatterns;

    @Nullable
    private List<String> allowedMethods;

    @Nullable
    private List<HttpMethod> resolvedMethods;

    @Nullable
    private List<String> allowedHeaders;

    @Nullable
    private List<String> exposedHeaders;

    @Nullable
    private Boolean allowCredentials;

    @Nullable
    private Boolean allowPrivateNetwork;

    @Nullable
    private Long maxAge;

    public CorsConfiguration() {
        this.resolvedMethods = DEFAULT_METHODS;
    }

    public CorsConfiguration(CorsConfiguration other) {
        this.resolvedMethods = DEFAULT_METHODS;
        this.allowedOrigins = other.allowedOrigins;
        this.allowedOriginPatterns = other.allowedOriginPatterns;
        this.allowedMethods = other.allowedMethods;
        this.resolvedMethods = other.resolvedMethods;
        this.allowedHeaders = other.allowedHeaders;
        this.exposedHeaders = other.exposedHeaders;
        this.allowCredentials = other.allowCredentials;
        this.allowPrivateNetwork = other.allowPrivateNetwork;
        this.maxAge = other.maxAge;
    }

    public void setAllowedOrigins(@Nullable List<String> origins) {
        if (origins == null) {
            this.allowedOrigins = null;
            return;
        }
        this.allowedOrigins = new ArrayList(origins.size());
        for (String origin : origins) {
            addAllowedOrigin(origin);
        }
    }

    private String trimTrailingSlash(String origin) {
        return origin.endsWith("/") ? origin.substring(0, origin.length() - 1) : origin;
    }

    @Nullable
    public List<String> getAllowedOrigins() {
        return this.allowedOrigins;
    }

    public void addAllowedOrigin(@Nullable String origin) {
        if (origin == null) {
            return;
        }
        if (this.allowedOrigins == null) {
            this.allowedOrigins = new ArrayList(4);
        } else if (this.allowedOrigins == DEFAULT_PERMIT_ALL && CollectionUtils.isEmpty(this.allowedOriginPatterns)) {
            setAllowedOrigins(DEFAULT_PERMIT_ALL);
        }
        parseCommaDelimitedOrigin(origin, value -> {
            this.allowedOrigins.add(trimTrailingSlash(value));
        });
    }

    public CorsConfiguration setAllowedOriginPatterns(@Nullable List<String> allowedOriginPatterns) {
        if (allowedOriginPatterns == null) {
            this.allowedOriginPatterns = null;
        } else {
            this.allowedOriginPatterns = new ArrayList(allowedOriginPatterns.size());
            for (String patternValue : allowedOriginPatterns) {
                addAllowedOriginPattern(patternValue);
            }
        }
        return this;
    }

    @Nullable
    public List<String> getAllowedOriginPatterns() {
        if (this.allowedOriginPatterns == null) {
            return null;
        }
        return this.allowedOriginPatterns.stream().map((v0) -> {
            return v0.getDeclaredPattern();
        }).toList();
    }

    public void addAllowedOriginPattern(@Nullable String originPattern) {
        if (originPattern == null) {
            return;
        }
        if (this.allowedOriginPatterns == null) {
            this.allowedOriginPatterns = new ArrayList(4);
        }
        parseCommaDelimitedOrigin(originPattern, value -> {
            this.allowedOriginPatterns.add(new OriginPattern(trimTrailingSlash(value)));
            if (this.allowedOrigins == DEFAULT_PERMIT_ALL) {
                this.allowedOrigins = null;
            }
        });
    }

    private static void parseCommaDelimitedOrigin(String rawValue, Consumer<String> valueConsumer) {
        if (rawValue.indexOf(44) == -1) {
            valueConsumer.accept(rawValue);
            return;
        }
        int start = 0;
        boolean withinPortRange = false;
        for (int current = 0; current < rawValue.length(); current++) {
            switch (rawValue.charAt(current)) {
                case ',':
                    if (withinPortRange) {
                        break;
                    } else {
                        valueConsumer.accept(rawValue.substring(start, current).trim());
                        start = current + 1;
                        break;
                    }
                case '[':
                    withinPortRange = true;
                    break;
                case ']':
                    withinPortRange = false;
                    break;
            }
        }
        if (start < rawValue.length()) {
            valueConsumer.accept(rawValue.substring(start));
        }
    }

    public void setAllowedMethods(@Nullable List<String> allowedMethods) {
        this.allowedMethods = allowedMethods != null ? new ArrayList(allowedMethods) : null;
        if (!CollectionUtils.isEmpty(allowedMethods)) {
            this.resolvedMethods = new ArrayList(allowedMethods.size());
            for (String method : allowedMethods) {
                if ("*".equals(method)) {
                    this.resolvedMethods = null;
                    return;
                }
                this.resolvedMethods.add(HttpMethod.valueOf(method));
            }
            return;
        }
        this.resolvedMethods = DEFAULT_METHODS;
    }

    @Nullable
    public List<String> getAllowedMethods() {
        return this.allowedMethods;
    }

    public void addAllowedMethod(HttpMethod method) {
        addAllowedMethod(method.name());
    }

    public void addAllowedMethod(String method) {
        if (StringUtils.hasText(method)) {
            if (this.allowedMethods == null) {
                this.allowedMethods = new ArrayList(4);
                this.resolvedMethods = new ArrayList(4);
            } else if (this.allowedMethods == DEFAULT_PERMIT_METHODS) {
                setAllowedMethods(DEFAULT_PERMIT_METHODS);
            }
            this.allowedMethods.add(method);
            if ("*".equals(method)) {
                this.resolvedMethods = null;
            } else if (this.resolvedMethods != null) {
                this.resolvedMethods.add(HttpMethod.valueOf(method));
            }
        }
    }

    public void setAllowedHeaders(@Nullable List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders != null ? new ArrayList(allowedHeaders) : null;
    }

    @Nullable
    public List<String> getAllowedHeaders() {
        return this.allowedHeaders;
    }

    public void addAllowedHeader(String allowedHeader) {
        if (this.allowedHeaders == null) {
            this.allowedHeaders = new ArrayList(4);
        } else if (this.allowedHeaders == DEFAULT_PERMIT_ALL) {
            setAllowedHeaders(DEFAULT_PERMIT_ALL);
        }
        this.allowedHeaders.add(allowedHeader);
    }

    public void setExposedHeaders(@Nullable List<String> exposedHeaders) {
        this.exposedHeaders = exposedHeaders != null ? new ArrayList(exposedHeaders) : null;
    }

    @Nullable
    public List<String> getExposedHeaders() {
        return this.exposedHeaders;
    }

    public void addExposedHeader(String exposedHeader) {
        if (this.exposedHeaders == null) {
            this.exposedHeaders = new ArrayList(4);
        }
        this.exposedHeaders.add(exposedHeader);
    }

    public void setAllowCredentials(@Nullable Boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    @Nullable
    public Boolean getAllowCredentials() {
        return this.allowCredentials;
    }

    public void setAllowPrivateNetwork(@Nullable Boolean allowPrivateNetwork) {
        this.allowPrivateNetwork = allowPrivateNetwork;
    }

    @Nullable
    public Boolean getAllowPrivateNetwork() {
        return this.allowPrivateNetwork;
    }

    public void setMaxAge(Duration maxAge) {
        this.maxAge = Long.valueOf(maxAge.getSeconds());
    }

    public void setMaxAge(@Nullable Long maxAge) {
        this.maxAge = maxAge;
    }

    @Nullable
    public Long getMaxAge() {
        return this.maxAge;
    }

    public CorsConfiguration applyPermitDefaultValues() {
        if (this.allowedOrigins == null && this.allowedOriginPatterns == null) {
            this.allowedOrigins = DEFAULT_PERMIT_ALL;
        }
        if (this.allowedMethods == null) {
            this.allowedMethods = DEFAULT_PERMIT_METHODS;
            this.resolvedMethods = DEFAULT_PERMIT_METHODS.stream().map(HttpMethod::valueOf).toList();
        }
        if (this.allowedHeaders == null) {
            this.allowedHeaders = DEFAULT_PERMIT_ALL;
        }
        if (this.maxAge == null) {
            this.maxAge = 1800L;
        }
        return this;
    }

    public void validateAllowCredentials() {
        if (this.allowCredentials == Boolean.TRUE && this.allowedOrigins != null && this.allowedOrigins.contains("*")) {
            throw new IllegalArgumentException("When allowCredentials is true, allowedOrigins cannot contain the special value \"*\" since that cannot be set on the \"Access-Control-Allow-Origin\" response header. To allow credentials to a set of origins, list them explicitly or consider using \"allowedOriginPatterns\" instead.");
        }
    }

    public void validateAllowPrivateNetwork() {
        if (this.allowPrivateNetwork == Boolean.TRUE && this.allowedOrigins != null && this.allowedOrigins.contains("*")) {
            throw new IllegalArgumentException("When allowPrivateNetwork is true, allowedOrigins cannot contain the special value \"*\" as it is not recommended from a security perspective. To allow private network access to a set of origins, list them explicitly or consider using \"allowedOriginPatterns\" instead.");
        }
    }

    public CorsConfiguration combine(@Nullable CorsConfiguration other) {
        if (other == null) {
            return this;
        }
        CorsConfiguration config = new CorsConfiguration(this);
        List<String> origins = combine(getAllowedOrigins(), other.getAllowedOrigins());
        List<OriginPattern> patterns = combinePatterns(this.allowedOriginPatterns, other.allowedOriginPatterns);
        config.allowedOrigins = (origins != DEFAULT_PERMIT_ALL || CollectionUtils.isEmpty(patterns)) ? origins : null;
        config.allowedOriginPatterns = patterns;
        config.setAllowedMethods(combine(getAllowedMethods(), other.getAllowedMethods()));
        config.setAllowedHeaders(combine(getAllowedHeaders(), other.getAllowedHeaders()));
        config.setExposedHeaders(combine(getExposedHeaders(), other.getExposedHeaders()));
        Boolean allowCredentials = other.getAllowCredentials();
        if (allowCredentials != null) {
            config.setAllowCredentials(allowCredentials);
        }
        Boolean allowPrivateNetwork = other.getAllowPrivateNetwork();
        if (allowPrivateNetwork != null) {
            config.setAllowPrivateNetwork(allowPrivateNetwork);
        }
        Long maxAge = other.getMaxAge();
        if (maxAge != null) {
            config.setMaxAge(maxAge);
        }
        return config;
    }

    private List<String> combine(@Nullable List<String> source, @Nullable List<String> other) {
        if (other == null) {
            return source != null ? source : Collections.emptyList();
        }
        if (source == null) {
            return other;
        }
        if (source == DEFAULT_PERMIT_ALL || source == DEFAULT_PERMIT_METHODS) {
            return other;
        }
        if (other == DEFAULT_PERMIT_ALL || other == DEFAULT_PERMIT_METHODS) {
            return source;
        }
        if (source.contains("*") || other.contains("*")) {
            return ALL_LIST;
        }
        Set<String> combined = new LinkedHashSet<>(source.size() + other.size());
        combined.addAll(source);
        combined.addAll(other);
        return new ArrayList(combined);
    }

    private List<OriginPattern> combinePatterns(@Nullable List<OriginPattern> source, @Nullable List<OriginPattern> other) {
        if (other == null) {
            return source != null ? source : Collections.emptyList();
        }
        if (source == null) {
            return other;
        }
        if (source.contains(ALL_PATTERN) || other.contains(ALL_PATTERN)) {
            return ALL_PATTERN_LIST;
        }
        Set<OriginPattern> combined = new LinkedHashSet<>(source.size() + other.size());
        combined.addAll(source);
        combined.addAll(other);
        return new ArrayList(combined);
    }

    @Nullable
    public String checkOrigin(@Nullable String origin) {
        if (!StringUtils.hasText(origin)) {
            return null;
        }
        String originToCheck = trimTrailingSlash(origin);
        if (!ObjectUtils.isEmpty(this.allowedOrigins)) {
            if (this.allowedOrigins.contains("*")) {
                validateAllowCredentials();
                validateAllowPrivateNetwork();
                return "*";
            }
            for (String allowedOrigin : this.allowedOrigins) {
                if (originToCheck.equalsIgnoreCase(allowedOrigin)) {
                    return origin;
                }
            }
        }
        if (!ObjectUtils.isEmpty(this.allowedOriginPatterns)) {
            for (OriginPattern p : this.allowedOriginPatterns) {
                if (p.getDeclaredPattern().equals("*") || p.getPattern().matcher(originToCheck).matches()) {
                    return origin;
                }
            }
            return null;
        }
        return null;
    }

    @Nullable
    public List<HttpMethod> checkHttpMethod(@Nullable HttpMethod requestMethod) {
        if (requestMethod == null) {
            return null;
        }
        if (this.resolvedMethods == null) {
            return Collections.singletonList(requestMethod);
        }
        if (this.resolvedMethods.contains(requestMethod)) {
            return this.resolvedMethods;
        }
        return null;
    }

    @Nullable
    public List<String> checkHeaders(@Nullable List<String> requestHeaders) {
        if (requestHeaders == null) {
            return null;
        }
        if (requestHeaders.isEmpty()) {
            return Collections.emptyList();
        }
        if (ObjectUtils.isEmpty(this.allowedHeaders)) {
            return null;
        }
        boolean allowAnyHeader = this.allowedHeaders.contains("*");
        int maxResultSize = allowAnyHeader ? requestHeaders.size() : Math.min(requestHeaders.size(), this.allowedHeaders.size());
        List<String> result = new ArrayList<>(maxResultSize);
        for (String requestHeader : requestHeaders) {
            if (StringUtils.hasText(requestHeader)) {
                String requestHeader2 = requestHeader.trim();
                if (allowAnyHeader) {
                    result.add(requestHeader2);
                } else {
                    Iterator<String> it = this.allowedHeaders.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            String allowedHeader = it.next();
                            if (requestHeader2.equalsIgnoreCase(allowedHeader)) {
                                result.add(requestHeader2);
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/cors/CorsConfiguration$OriginPattern.class */
    public static class OriginPattern {
        private static final Pattern PORTS_PATTERN = Pattern.compile("(.*):\\[(\\*|\\d+(,\\d+)*)]");
        private final String declaredPattern;
        private final Pattern pattern;

        OriginPattern(String declaredPattern) {
            this.declaredPattern = declaredPattern;
            this.pattern = initPattern(declaredPattern);
        }

        private static Pattern initPattern(String patternValue) {
            String portList = null;
            Matcher matcher = PORTS_PATTERN.matcher(patternValue);
            if (matcher.matches()) {
                patternValue = matcher.group(1);
                portList = matcher.group(2);
            }
            String patternValue2 = ("\\Q" + patternValue + "\\E").replace("*", "\\E.*\\Q");
            if (portList != null) {
                patternValue2 = patternValue2 + (portList.equals("*") ? "(:\\d+)?" : ":(" + portList.replace(',', '|') + ")");
            }
            return Pattern.compile(patternValue2);
        }

        public String getDeclaredPattern() {
            return this.declaredPattern;
        }

        public Pattern getPattern() {
            return this.pattern;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || !getClass().equals(other.getClass())) {
                return false;
            }
            return ObjectUtils.nullSafeEquals(this.declaredPattern, ((OriginPattern) other).declaredPattern);
        }

        public int hashCode() {
            return this.declaredPattern.hashCode();
        }

        public String toString() {
            return this.declaredPattern;
        }
    }
}
