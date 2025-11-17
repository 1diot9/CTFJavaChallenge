package org.springframework.web.servlet.handler;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodMapping.class */
public abstract class AbstractHandlerMethodMapping<T> extends AbstractHandlerMapping implements InitializingBean {
    private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";
    private static final HandlerMethod PREFLIGHT_AMBIGUOUS_MATCH = new HandlerMethod(new EmptyHandler(), ClassUtils.getMethod(EmptyHandler.class, "handle", new Class[0]));
    private static final CorsConfiguration ALLOW_CORS_CONFIG = new CorsConfiguration();

    @Nullable
    private HandlerMethodMappingNamingStrategy<T> namingStrategy;
    private boolean detectHandlerMethodsInAncestorContexts = false;
    private final AbstractHandlerMethodMapping<T>.MappingRegistry mappingRegistry = new MappingRegistry();

    protected abstract boolean isHandler(Class<?> beanType);

    @Nullable
    protected abstract T getMappingForMethod(Method method, Class<?> handlerType);

    @Nullable
    protected abstract T getMatchingMapping(T mapping, HttpServletRequest request);

    protected abstract Comparator<T> getMappingComparator(HttpServletRequest request);

    static {
        ALLOW_CORS_CONFIG.addAllowedOriginPattern("*");
        ALLOW_CORS_CONFIG.addAllowedMethod("*");
        ALLOW_CORS_CONFIG.addAllowedHeader("*");
        ALLOW_CORS_CONFIG.setAllowCredentials(true);
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMapping
    public void setPatternParser(@Nullable PathPatternParser patternParser) {
        Assert.state(this.mappingRegistry.getRegistrations().isEmpty(), "PathPatternParser must be set before the initialization of request mappings through InitializingBean#afterPropertiesSet.");
        super.setPatternParser(patternParser);
    }

    public void setDetectHandlerMethodsInAncestorContexts(boolean detectHandlerMethodsInAncestorContexts) {
        this.detectHandlerMethodsInAncestorContexts = detectHandlerMethodsInAncestorContexts;
    }

    public void setHandlerMethodMappingNamingStrategy(HandlerMethodMappingNamingStrategy<T> namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    @Nullable
    public HandlerMethodMappingNamingStrategy<T> getNamingStrategy() {
        return this.namingStrategy;
    }

    public Map<T, HandlerMethod> getHandlerMethods() {
        this.mappingRegistry.acquireReadLock();
        try {
            return (Map) this.mappingRegistry.getRegistrations().entrySet().stream().collect(Collectors.toUnmodifiableMap((v0) -> {
                return v0.getKey();
            }, entry -> {
                return ((MappingRegistration) entry.getValue()).handlerMethod;
            }));
        } finally {
            this.mappingRegistry.releaseReadLock();
        }
    }

    @Nullable
    public List<HandlerMethod> getHandlerMethodsForMappingName(String mappingName) {
        return this.mappingRegistry.getHandlerMethodsByMappingName(mappingName);
    }

    AbstractHandlerMethodMapping<T>.MappingRegistry getMappingRegistry() {
        return this.mappingRegistry;
    }

    public void registerMapping(T mapping, Object handler, Method method) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Register \"" + mapping + "\" to " + method.toGenericString());
        }
        this.mappingRegistry.register(mapping, handler, method);
    }

    public void unregisterMapping(T mapping) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Unregister mapping \"" + mapping + "\"");
        }
        this.mappingRegistry.unregister(mapping);
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        initHandlerMethods();
    }

    protected void initHandlerMethods() {
        for (String beanName : getCandidateBeanNames()) {
            if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
                processCandidateBean(beanName);
            }
        }
        handlerMethodsInitialized(getHandlerMethods());
    }

    protected String[] getCandidateBeanNames() {
        if (this.detectHandlerMethodsInAncestorContexts) {
            return BeanFactoryUtils.beanNamesForTypeIncludingAncestors(obtainApplicationContext(), (Class<?>) Object.class);
        }
        return obtainApplicationContext().getBeanNamesForType(Object.class);
    }

    protected void processCandidateBean(String beanName) {
        Class<?> beanType = null;
        try {
            beanType = obtainApplicationContext().getType(beanName);
        } catch (Throwable ex) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Could not resolve type for bean '" + beanName + "'", ex);
            }
        }
        if (beanType != null && isHandler(beanType)) {
            detectHandlerMethods(beanName);
        }
    }

    protected void detectHandlerMethods(Object handler) {
        Class<?> cls;
        if (handler instanceof String) {
            String beanName = (String) handler;
            cls = obtainApplicationContext().getType(beanName);
        } else {
            cls = handler.getClass();
        }
        Class<?> handlerType = cls;
        if (handlerType != null) {
            Class<?> userType = ClassUtils.getUserClass(handlerType);
            Map<Method, T> methods = MethodIntrospector.selectMethods(userType, method -> {
                try {
                    return getMappingForMethod(method, userType);
                } catch (Throwable ex) {
                    throw new IllegalStateException("Invalid mapping on handler class [" + userType.getName() + "]: " + method, ex);
                }
            });
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(formatMappings(userType, methods));
            } else if (this.mappingsLogger.isDebugEnabled()) {
                this.mappingsLogger.debug(formatMappings(userType, methods));
            }
            methods.forEach((method2, obj) -> {
                Method invocableMethod = AopUtils.selectInvocableMethod(method2, userType);
                registerHandlerMethod(handler, invocableMethod, obj);
            });
        }
    }

    private String formatMappings(Class<?> userType, Map<Method, T> methods) {
        String simpleName;
        String packageName = ClassUtils.getPackageName(userType);
        if (StringUtils.hasText(packageName)) {
            simpleName = (String) Arrays.stream(packageName.split("\\.")).map(packageSegment -> {
                return packageSegment.substring(0, 1);
            }).collect(Collectors.joining(".", "", "." + userType.getSimpleName()));
        } else {
            simpleName = userType.getSimpleName();
        }
        String formattedType = simpleName;
        Function<Method, String> methodFormatter = method -> {
            return (String) Arrays.stream(method.getParameterTypes()).map((v0) -> {
                return v0.getSimpleName();
            }).collect(Collectors.joining(",", "(", ")"));
        };
        return (String) methods.entrySet().stream().map(e -> {
            Method method2 = (Method) e.getKey();
            return e.getValue() + ": " + method2.getName() + ((String) methodFormatter.apply(method2));
        }).collect(Collectors.joining("\n\t", "\n\t" + formattedType + ":\n\t", ""));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void registerHandlerMethod(Object handler, Method method, T mapping) {
        this.mappingRegistry.register(mapping, handler, method);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HandlerMethod createHandlerMethod(Object handler, Method method) {
        if (handler instanceof String) {
            String beanName = (String) handler;
            return new HandlerMethod(beanName, obtainApplicationContext().getAutowireCapableBeanFactory(), obtainApplicationContext(), method);
        }
        return new HandlerMethod(handler, method);
    }

    @Nullable
    protected CorsConfiguration initCorsConfiguration(Object handler, Method method, T mapping) {
        return null;
    }

    protected void handlerMethodsInitialized(Map<T, HandlerMethod> handlerMethods) {
        int total = handlerMethods.size();
        if ((this.logger.isTraceEnabled() && total == 0) || (this.logger.isDebugEnabled() && total > 0)) {
            this.logger.debug(total + " mappings in " + formatMappingName());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMapping
    @Nullable
    public HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        String lookupPath = initLookupPath(request);
        this.mappingRegistry.acquireReadLock();
        try {
            HandlerMethod handlerMethod = lookupHandlerMethod(lookupPath, request);
            return handlerMethod != null ? handlerMethod.createWithResolvedBean() : null;
        } finally {
            this.mappingRegistry.releaseReadLock();
        }
    }

    @Nullable
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        List<AbstractHandlerMethodMapping<T>.Match> matches = new ArrayList<>();
        List<T> directPathMatches = this.mappingRegistry.getMappingsByDirectPath(lookupPath);
        if (directPathMatches != null) {
            addMatchingMappings(directPathMatches, matches, request);
        }
        if (matches.isEmpty()) {
            addMatchingMappings(this.mappingRegistry.getRegistrations().keySet(), matches, request);
        }
        if (!matches.isEmpty()) {
            AbstractHandlerMethodMapping<T>.Match bestMatch = matches.get(0);
            if (matches.size() > 1) {
                Comparator<AbstractHandlerMethodMapping<T>.Match> comparator = new MatchComparator(getMappingComparator(request));
                matches.sort(comparator);
                bestMatch = matches.get(0);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace(matches.size() + " matching mappings: " + matches);
                }
                if (CorsUtils.isPreFlightRequest(request)) {
                    for (AbstractHandlerMethodMapping<T>.Match match : matches) {
                        if (match.hasCorsConfig()) {
                            return PREFLIGHT_AMBIGUOUS_MATCH;
                        }
                    }
                } else {
                    AbstractHandlerMethodMapping<T>.Match secondBestMatch = matches.get(1);
                    if (comparator.compare(bestMatch, secondBestMatch) == 0) {
                        Method m1 = bestMatch.getHandlerMethod().getMethod();
                        Method m2 = secondBestMatch.getHandlerMethod().getMethod();
                        String uri = request.getRequestURI();
                        throw new IllegalStateException("Ambiguous handler methods mapped for '" + uri + "': {" + m1 + ", " + m2 + "}");
                    }
                }
            }
            request.setAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE, bestMatch.getHandlerMethod());
            handleMatch(((Match) bestMatch).mapping, lookupPath, request);
            return bestMatch.getHandlerMethod();
        }
        return handleNoMatch(this.mappingRegistry.getRegistrations().keySet(), lookupPath, request);
    }

    private void addMatchingMappings(Collection<T> mappings, List<AbstractHandlerMethodMapping<T>.Match> matches, HttpServletRequest request) {
        for (T mapping : mappings) {
            T match = getMatchingMapping(mapping, request);
            if (match != null) {
                matches.add(new Match(match, this.mappingRegistry.getRegistrations().get(mapping)));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleMatch(T mapping, String lookupPath, HttpServletRequest request) {
        request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, lookupPath);
    }

    @Nullable
    protected HandlerMethod handleNoMatch(Set<T> mappings, String lookupPath, HttpServletRequest request) throws Exception {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMapping
    public boolean hasCorsConfigurationSource(Object handler) {
        if (!super.hasCorsConfigurationSource(handler)) {
            if (handler instanceof HandlerMethod) {
                HandlerMethod handerMethod = (HandlerMethod) handler;
                if (this.mappingRegistry.getCorsConfiguration(handerMethod) != null) {
                }
            }
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMapping
    public CorsConfiguration getCorsConfiguration(Object handler, HttpServletRequest request) {
        CorsConfiguration corsConfig = super.getCorsConfiguration(handler, request);
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.equals(PREFLIGHT_AMBIGUOUS_MATCH)) {
                return ALLOW_CORS_CONFIG;
            }
            CorsConfiguration corsConfigFromMethod = this.mappingRegistry.getCorsConfiguration(handlerMethod);
            corsConfig = corsConfig != null ? corsConfig.combine(corsConfigFromMethod) : corsConfigFromMethod;
        }
        return corsConfig;
    }

    @Deprecated
    protected Set<String> getMappingPathPatterns(T mapping) {
        return Collections.emptySet();
    }

    protected Set<String> getDirectPaths(T mapping) {
        Set<String> urls = Collections.emptySet();
        for (String path : getMappingPathPatterns(mapping)) {
            if (!getPathMatcher().isPattern(path)) {
                urls = urls.isEmpty() ? new HashSet<>(1) : urls;
                urls.add(path);
            }
        }
        return urls;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodMapping$MappingRegistry.class */
    public class MappingRegistry {
        private final Map<T, MappingRegistration<T>> registry = new HashMap();
        private final MultiValueMap<String, T> pathLookup = new LinkedMultiValueMap();
        private final Map<String, List<HandlerMethod>> nameLookup = new ConcurrentHashMap();
        private final Map<HandlerMethod, CorsConfiguration> corsLookup = new ConcurrentHashMap();
        private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        MappingRegistry() {
        }

        public Map<T, MappingRegistration<T>> getRegistrations() {
            return this.registry;
        }

        @Nullable
        public List<T> getMappingsByDirectPath(String urlPath) {
            return (List) this.pathLookup.get(urlPath);
        }

        public List<HandlerMethod> getHandlerMethodsByMappingName(String mappingName) {
            return this.nameLookup.get(mappingName);
        }

        @Nullable
        public CorsConfiguration getCorsConfiguration(HandlerMethod handlerMethod) {
            HandlerMethod original = handlerMethod.getResolvedFromHandlerMethod();
            return this.corsLookup.get(original != null ? original : handlerMethod);
        }

        public void acquireReadLock() {
            this.readWriteLock.readLock().lock();
        }

        public void releaseReadLock() {
            this.readWriteLock.readLock().unlock();
        }

        public void register(T mapping, Object handler, Method method) {
            this.readWriteLock.writeLock().lock();
            try {
                HandlerMethod handlerMethod = AbstractHandlerMethodMapping.this.createHandlerMethod(handler, method);
                validateMethodMapping(handlerMethod, mapping);
                HandlerMethod handlerMethod2 = handlerMethod.createWithValidateFlags();
                Set<String> directPaths = AbstractHandlerMethodMapping.this.getDirectPaths(mapping);
                for (String path : directPaths) {
                    this.pathLookup.add(path, mapping);
                }
                String name = null;
                if (AbstractHandlerMethodMapping.this.getNamingStrategy() != null) {
                    name = AbstractHandlerMethodMapping.this.getNamingStrategy().getName(handlerMethod2, mapping);
                    addMappingName(name, handlerMethod2);
                }
                CorsConfiguration corsConfig = AbstractHandlerMethodMapping.this.initCorsConfiguration(handler, method, mapping);
                if (corsConfig != null) {
                    corsConfig.validateAllowCredentials();
                    corsConfig.validateAllowPrivateNetwork();
                    this.corsLookup.put(handlerMethod2, corsConfig);
                }
                this.registry.put(mapping, new MappingRegistration<>(mapping, handlerMethod2, directPaths, name, corsConfig != null));
                this.readWriteLock.writeLock().unlock();
            } catch (Throwable th) {
                this.readWriteLock.writeLock().unlock();
                throw th;
            }
        }

        private void validateMethodMapping(HandlerMethod handlerMethod, T mapping) {
            MappingRegistration<T> registration = this.registry.get(mapping);
            HandlerMethod existingHandlerMethod = registration != null ? registration.getHandlerMethod() : null;
            if (existingHandlerMethod != null && !existingHandlerMethod.equals(handlerMethod)) {
                throw new IllegalStateException("Ambiguous mapping. Cannot map '" + handlerMethod.getBean() + "' method \n" + handlerMethod + "\nto " + mapping + ": There is already '" + existingHandlerMethod.getBean() + "' bean method\n" + existingHandlerMethod + " mapped.");
            }
        }

        private void addMappingName(String name, HandlerMethod handlerMethod) {
            List<HandlerMethod> oldList = this.nameLookup.get(name);
            if (oldList == null) {
                oldList = Collections.emptyList();
            }
            for (HandlerMethod current : oldList) {
                if (handlerMethod.equals(current)) {
                    return;
                }
            }
            List<HandlerMethod> newList = new ArrayList<>(oldList.size() + 1);
            newList.addAll(oldList);
            newList.add(handlerMethod);
            this.nameLookup.put(name, newList);
        }

        public void unregister(T mapping) {
            this.readWriteLock.writeLock().lock();
            try {
                MappingRegistration<T> registration = this.registry.remove(mapping);
                if (registration == null) {
                    return;
                }
                for (String path : registration.getDirectPaths()) {
                    List<T> mappings = (List) this.pathLookup.get(path);
                    if (mappings != null) {
                        mappings.remove(registration.getMapping());
                        if (mappings.isEmpty()) {
                            this.pathLookup.remove(path);
                        }
                    }
                }
                removeMappingName(registration);
                this.corsLookup.remove(registration.getHandlerMethod());
                this.readWriteLock.writeLock().unlock();
            } finally {
                this.readWriteLock.writeLock().unlock();
            }
        }

        private void removeMappingName(MappingRegistration<T> definition) {
            String name = definition.getMappingName();
            if (name == null) {
                return;
            }
            HandlerMethod handlerMethod = definition.getHandlerMethod();
            List<HandlerMethod> oldList = this.nameLookup.get(name);
            if (oldList == null) {
                return;
            }
            if (oldList.size() <= 1) {
                this.nameLookup.remove(name);
                return;
            }
            List<HandlerMethod> newList = new ArrayList<>(oldList.size() - 1);
            for (HandlerMethod current : oldList) {
                if (!current.equals(handlerMethod)) {
                    newList.add(current);
                }
            }
            this.nameLookup.put(name, newList);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodMapping$MappingRegistration.class */
    public static class MappingRegistration<T> {
        private final T mapping;
        private final HandlerMethod handlerMethod;
        private final Set<String> directPaths;

        @Nullable
        private final String mappingName;
        private final boolean corsConfig;

        public MappingRegistration(T mapping, HandlerMethod handlerMethod, @Nullable Set<String> directPaths, @Nullable String mappingName, boolean corsConfig) {
            Assert.notNull(mapping, "Mapping must not be null");
            Assert.notNull(handlerMethod, "HandlerMethod must not be null");
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
            this.directPaths = directPaths != null ? directPaths : Collections.emptySet();
            this.mappingName = mappingName;
            this.corsConfig = corsConfig;
        }

        public T getMapping() {
            return this.mapping;
        }

        public HandlerMethod getHandlerMethod() {
            return this.handlerMethod;
        }

        public Set<String> getDirectPaths() {
            return this.directPaths;
        }

        @Nullable
        public String getMappingName() {
            return this.mappingName;
        }

        public boolean hasCorsConfig() {
            return this.corsConfig;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodMapping$Match.class */
    public class Match {
        private final T mapping;
        private final MappingRegistration<T> registration;

        public Match(T mapping, MappingRegistration<T> registration) {
            this.mapping = mapping;
            this.registration = registration;
        }

        public HandlerMethod getHandlerMethod() {
            return this.registration.getHandlerMethod();
        }

        public boolean hasCorsConfig() {
            return this.registration.hasCorsConfig();
        }

        public String toString() {
            return this.mapping.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodMapping$MatchComparator.class */
    public class MatchComparator implements Comparator<AbstractHandlerMethodMapping<T>.Match> {
        private final Comparator<T> comparator;

        public MatchComparator(Comparator<T> comparator) {
            this.comparator = comparator;
        }

        @Override // java.util.Comparator
        public int compare(AbstractHandlerMethodMapping<T>.Match match1, AbstractHandlerMethodMapping<T>.Match match2) {
            return this.comparator.compare(((Match) match1).mapping, ((Match) match2).mapping);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodMapping$EmptyHandler.class */
    private static class EmptyHandler {
        private EmptyHandler() {
        }

        public void handle() {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}
