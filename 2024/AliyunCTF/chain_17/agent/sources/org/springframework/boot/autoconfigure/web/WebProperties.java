package org.springframework.boot.autoconfigure.web;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.http.CacheControl;

@ConfigurationProperties("spring.web")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/WebProperties.class */
public class WebProperties {
    private Locale locale;
    private LocaleResolver localeResolver = LocaleResolver.ACCEPT_HEADER;
    private final Resources resources = new Resources();

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/WebProperties$LocaleResolver.class */
    public enum LocaleResolver {
        FIXED,
        ACCEPT_HEADER
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public LocaleResolver getLocaleResolver() {
        return this.localeResolver;
    }

    public void setLocaleResolver(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    public Resources getResources() {
        return this.resources;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/WebProperties$Resources.class */
    public static class Resources {
        private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {"classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/"};
        private String[] staticLocations = CLASSPATH_RESOURCE_LOCATIONS;
        private boolean addMappings = true;
        private boolean customized = false;
        private final Chain chain = new Chain();
        private final Cache cache = new Cache();

        public String[] getStaticLocations() {
            return this.staticLocations;
        }

        public void setStaticLocations(String[] staticLocations) {
            this.staticLocations = appendSlashIfNecessary(staticLocations);
            this.customized = true;
        }

        private String[] appendSlashIfNecessary(String[] staticLocations) {
            String[] normalized = new String[staticLocations.length];
            for (int i = 0; i < staticLocations.length; i++) {
                String location = staticLocations[i];
                normalized[i] = location.endsWith("/") ? location : location + "/";
            }
            return normalized;
        }

        public boolean isAddMappings() {
            return this.addMappings;
        }

        public void setAddMappings(boolean addMappings) {
            this.customized = true;
            this.addMappings = addMappings;
        }

        public Chain getChain() {
            return this.chain;
        }

        public Cache getCache() {
            return this.cache;
        }

        public boolean hasBeenCustomized() {
            return this.customized || getChain().hasBeenCustomized() || getCache().hasBeenCustomized();
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/WebProperties$Resources$Chain.class */
        public static class Chain {
            private Boolean enabled;
            boolean customized = false;
            private boolean cache = true;
            private boolean compressed = false;
            private final Strategy strategy = new Strategy();

            public Boolean getEnabled() {
                return getEnabled(getStrategy().getFixed().isEnabled(), getStrategy().getContent().isEnabled(), this.enabled);
            }

            private boolean hasBeenCustomized() {
                return this.customized || getStrategy().hasBeenCustomized();
            }

            public void setEnabled(boolean enabled) {
                this.enabled = Boolean.valueOf(enabled);
                this.customized = true;
            }

            public boolean isCache() {
                return this.cache;
            }

            public void setCache(boolean cache) {
                this.cache = cache;
                this.customized = true;
            }

            public Strategy getStrategy() {
                return this.strategy;
            }

            public boolean isCompressed() {
                return this.compressed;
            }

            public void setCompressed(boolean compressed) {
                this.compressed = compressed;
                this.customized = true;
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public static Boolean getEnabled(boolean fixedEnabled, boolean contentEnabled, Boolean chainEnabled) {
                return (fixedEnabled || contentEnabled) ? Boolean.TRUE : chainEnabled;
            }

            /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/WebProperties$Resources$Chain$Strategy.class */
            public static class Strategy {
                private final Fixed fixed = new Fixed();
                private final Content content = new Content();

                public Fixed getFixed() {
                    return this.fixed;
                }

                public Content getContent() {
                    return this.content;
                }

                private boolean hasBeenCustomized() {
                    return getFixed().hasBeenCustomized() || getContent().hasBeenCustomized();
                }

                /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/WebProperties$Resources$Chain$Strategy$Content.class */
                public static class Content {
                    private boolean enabled;
                    private boolean customized = false;
                    private String[] paths = {"/**"};

                    public boolean isEnabled() {
                        return this.enabled;
                    }

                    public void setEnabled(boolean enabled) {
                        this.customized = true;
                        this.enabled = enabled;
                    }

                    public String[] getPaths() {
                        return this.paths;
                    }

                    public void setPaths(String[] paths) {
                        this.customized = true;
                        this.paths = paths;
                    }

                    private boolean hasBeenCustomized() {
                        return this.customized;
                    }
                }

                /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/WebProperties$Resources$Chain$Strategy$Fixed.class */
                public static class Fixed {
                    private boolean enabled;
                    private String version;
                    private boolean customized = false;
                    private String[] paths = {"/**"};

                    public boolean isEnabled() {
                        return this.enabled;
                    }

                    public void setEnabled(boolean enabled) {
                        this.customized = true;
                        this.enabled = enabled;
                    }

                    public String[] getPaths() {
                        return this.paths;
                    }

                    public void setPaths(String[] paths) {
                        this.customized = true;
                        this.paths = paths;
                    }

                    public String getVersion() {
                        return this.version;
                    }

                    public void setVersion(String version) {
                        this.customized = true;
                        this.version = version;
                    }

                    private boolean hasBeenCustomized() {
                        return this.customized;
                    }
                }
            }
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/WebProperties$Resources$Cache.class */
        public static class Cache {

            @DurationUnit(ChronoUnit.SECONDS)
            private Duration period;
            private boolean customized = false;
            private final Cachecontrol cachecontrol = new Cachecontrol();
            private boolean useLastModified = true;

            public Duration getPeriod() {
                return this.period;
            }

            public void setPeriod(Duration period) {
                this.customized = true;
                this.period = period;
            }

            public Cachecontrol getCachecontrol() {
                return this.cachecontrol;
            }

            public boolean isUseLastModified() {
                return this.useLastModified;
            }

            public void setUseLastModified(boolean useLastModified) {
                this.useLastModified = useLastModified;
            }

            private boolean hasBeenCustomized() {
                return this.customized || getCachecontrol().hasBeenCustomized();
            }

            /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/WebProperties$Resources$Cache$Cachecontrol.class */
            public static class Cachecontrol {
                private boolean customized = false;

                @DurationUnit(ChronoUnit.SECONDS)
                private Duration maxAge;
                private Boolean noCache;
                private Boolean noStore;
                private Boolean mustRevalidate;
                private Boolean noTransform;
                private Boolean cachePublic;
                private Boolean cachePrivate;
                private Boolean proxyRevalidate;

                @DurationUnit(ChronoUnit.SECONDS)
                private Duration staleWhileRevalidate;

                @DurationUnit(ChronoUnit.SECONDS)
                private Duration staleIfError;

                @DurationUnit(ChronoUnit.SECONDS)
                private Duration sMaxAge;

                public Duration getMaxAge() {
                    return this.maxAge;
                }

                public void setMaxAge(Duration maxAge) {
                    this.customized = true;
                    this.maxAge = maxAge;
                }

                public Boolean getNoCache() {
                    return this.noCache;
                }

                public void setNoCache(Boolean noCache) {
                    this.customized = true;
                    this.noCache = noCache;
                }

                public Boolean getNoStore() {
                    return this.noStore;
                }

                public void setNoStore(Boolean noStore) {
                    this.customized = true;
                    this.noStore = noStore;
                }

                public Boolean getMustRevalidate() {
                    return this.mustRevalidate;
                }

                public void setMustRevalidate(Boolean mustRevalidate) {
                    this.customized = true;
                    this.mustRevalidate = mustRevalidate;
                }

                public Boolean getNoTransform() {
                    return this.noTransform;
                }

                public void setNoTransform(Boolean noTransform) {
                    this.customized = true;
                    this.noTransform = noTransform;
                }

                public Boolean getCachePublic() {
                    return this.cachePublic;
                }

                public void setCachePublic(Boolean cachePublic) {
                    this.customized = true;
                    this.cachePublic = cachePublic;
                }

                public Boolean getCachePrivate() {
                    return this.cachePrivate;
                }

                public void setCachePrivate(Boolean cachePrivate) {
                    this.customized = true;
                    this.cachePrivate = cachePrivate;
                }

                public Boolean getProxyRevalidate() {
                    return this.proxyRevalidate;
                }

                public void setProxyRevalidate(Boolean proxyRevalidate) {
                    this.customized = true;
                    this.proxyRevalidate = proxyRevalidate;
                }

                public Duration getStaleWhileRevalidate() {
                    return this.staleWhileRevalidate;
                }

                public void setStaleWhileRevalidate(Duration staleWhileRevalidate) {
                    this.customized = true;
                    this.staleWhileRevalidate = staleWhileRevalidate;
                }

                public Duration getStaleIfError() {
                    return this.staleIfError;
                }

                public void setStaleIfError(Duration staleIfError) {
                    this.customized = true;
                    this.staleIfError = staleIfError;
                }

                public Duration getSMaxAge() {
                    return this.sMaxAge;
                }

                public void setSMaxAge(Duration sMaxAge) {
                    this.customized = true;
                    this.sMaxAge = sMaxAge;
                }

                public CacheControl toHttpCacheControl() {
                    PropertyMapper map = PropertyMapper.get();
                    CacheControl control = createCacheControl();
                    PropertyMapper.Source whenTrue = map.from(this::getMustRevalidate).whenTrue();
                    Objects.requireNonNull(control);
                    whenTrue.toCall(control::mustRevalidate);
                    PropertyMapper.Source whenTrue2 = map.from(this::getNoTransform).whenTrue();
                    Objects.requireNonNull(control);
                    whenTrue2.toCall(control::noTransform);
                    PropertyMapper.Source whenTrue3 = map.from(this::getCachePublic).whenTrue();
                    Objects.requireNonNull(control);
                    whenTrue3.toCall(control::cachePublic);
                    PropertyMapper.Source whenTrue4 = map.from(this::getCachePrivate).whenTrue();
                    Objects.requireNonNull(control);
                    whenTrue4.toCall(control::cachePrivate);
                    PropertyMapper.Source whenTrue5 = map.from(this::getProxyRevalidate).whenTrue();
                    Objects.requireNonNull(control);
                    whenTrue5.toCall(control::proxyRevalidate);
                    map.from(this::getStaleWhileRevalidate).whenNonNull().to(duration -> {
                        control.staleWhileRevalidate(duration.getSeconds(), TimeUnit.SECONDS);
                    });
                    map.from(this::getStaleIfError).whenNonNull().to(duration2 -> {
                        control.staleIfError(duration2.getSeconds(), TimeUnit.SECONDS);
                    });
                    map.from(this::getSMaxAge).whenNonNull().to(duration3 -> {
                        control.sMaxAge(duration3.getSeconds(), TimeUnit.SECONDS);
                    });
                    if (control.getHeaderValue() == null) {
                        return null;
                    }
                    return control;
                }

                private CacheControl createCacheControl() {
                    if (Boolean.TRUE.equals(this.noStore)) {
                        return CacheControl.noStore();
                    }
                    if (Boolean.TRUE.equals(this.noCache)) {
                        return CacheControl.noCache();
                    }
                    if (this.maxAge != null) {
                        return CacheControl.maxAge(this.maxAge.getSeconds(), TimeUnit.SECONDS);
                    }
                    return CacheControl.empty();
                }

                private boolean hasBeenCustomized() {
                    return this.customized;
                }
            }
        }
    }
}
