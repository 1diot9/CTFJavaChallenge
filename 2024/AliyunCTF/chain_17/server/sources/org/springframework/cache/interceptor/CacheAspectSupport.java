package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.KotlinDetector;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.SpringProperties;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.function.SingletonSupplier;
import org.springframework.util.function.SupplierUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheAspectSupport.class */
public abstract class CacheAspectSupport extends AbstractCacheInvoker implements BeanFactoryAware, InitializingBean, SmartInitializingSingleton {
    public static final String IGNORE_REACTIVESTREAMS_PROPERTY_NAME = "spring.cache.reactivestreams.ignore";
    private static final boolean shouldIgnoreReactiveStreams = SpringProperties.getFlag(IGNORE_REACTIVESTREAMS_PROPERTY_NAME);
    private static final boolean reactiveStreamsPresent = ClassUtils.isPresent("org.reactivestreams.Publisher", CacheAspectSupport.class.getClassLoader());

    @Nullable
    private final ReactiveCachingHandler reactiveCachingHandler;

    @Nullable
    private CacheOperationSource cacheOperationSource;

    @Nullable
    private SingletonSupplier<CacheResolver> cacheResolver;

    @Nullable
    private BeanFactory beanFactory;
    protected final Log logger = LogFactory.getLog(getClass());
    private final Map<CacheOperationCacheKey, CacheOperationMetadata> metadataCache = new ConcurrentHashMap(1024);
    private final StandardEvaluationContext originalEvaluationContext = new StandardEvaluationContext();
    private final CacheOperationExpressionEvaluator evaluator = new CacheOperationExpressionEvaluator(new CacheEvaluationContextFactory(this.originalEvaluationContext));
    private SingletonSupplier<KeyGenerator> keyGenerator = SingletonSupplier.of(SimpleKeyGenerator::new);
    private boolean initialized = false;

    /* JADX INFO: Access modifiers changed from: protected */
    public CacheAspectSupport() {
        this.reactiveCachingHandler = (!reactiveStreamsPresent || shouldIgnoreReactiveStreams) ? null : new ReactiveCachingHandler();
    }

    public void configure(@Nullable Supplier<CacheErrorHandler> errorHandler, @Nullable Supplier<KeyGenerator> keyGenerator, @Nullable Supplier<CacheResolver> cacheResolver, @Nullable Supplier<CacheManager> cacheManager) {
        this.errorHandler = new SingletonSupplier<>((Supplier) errorHandler, SimpleCacheErrorHandler::new);
        this.keyGenerator = new SingletonSupplier<>((Supplier) keyGenerator, SimpleKeyGenerator::new);
        this.cacheResolver = new SingletonSupplier<>((Supplier) cacheResolver, () -> {
            return SimpleCacheResolver.of((CacheManager) SupplierUtils.resolve(cacheManager));
        });
    }

    public void setCacheOperationSources(CacheOperationSource... cacheOperationSources) {
        Assert.notEmpty(cacheOperationSources, "At least 1 CacheOperationSource needs to be specified");
        this.cacheOperationSource = cacheOperationSources.length > 1 ? new CompositeCacheOperationSource(cacheOperationSources) : cacheOperationSources[0];
    }

    public void setCacheOperationSource(@Nullable CacheOperationSource cacheOperationSource) {
        this.cacheOperationSource = cacheOperationSource;
    }

    @Nullable
    public CacheOperationSource getCacheOperationSource() {
        return this.cacheOperationSource;
    }

    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = SingletonSupplier.of(keyGenerator);
    }

    public KeyGenerator getKeyGenerator() {
        return this.keyGenerator.obtain();
    }

    public void setCacheResolver(@Nullable CacheResolver cacheResolver) {
        this.cacheResolver = SingletonSupplier.ofNullable(cacheResolver);
    }

    @Nullable
    public CacheResolver getCacheResolver() {
        return (CacheResolver) SupplierUtils.resolve(this.cacheResolver);
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheResolver = SingletonSupplier.of(new SimpleCacheResolver(cacheManager));
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.originalEvaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        Assert.state(getCacheOperationSource() != null, "The 'cacheOperationSources' property is required: If there are no cacheable methods, then don't use a cache aspect.");
    }

    @Override // org.springframework.beans.factory.SmartInitializingSingleton
    public void afterSingletonsInstantiated() {
        if (getCacheResolver() == null) {
            Assert.state(this.beanFactory != null, "CacheResolver or BeanFactory must be set on cache aspect");
            try {
                setCacheManager((CacheManager) this.beanFactory.getBean(CacheManager.class));
            } catch (NoUniqueBeanDefinitionException ex) {
                throw new IllegalStateException("No CacheResolver specified, and no unique bean of type CacheManager found. Mark one as primary or declare a specific CacheManager to use.", ex);
            } catch (NoSuchBeanDefinitionException ex2) {
                throw new IllegalStateException("No CacheResolver specified, and no bean of type CacheManager found. Register a CacheManager bean or remove the @EnableCaching annotation from your configuration.", ex2);
            }
        }
        this.initialized = true;
    }

    protected String methodIdentification(Method method, Class<?> targetClass) {
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        return ClassUtils.getQualifiedMethodName(specificMethod);
    }

    protected Collection<? extends Cache> getCaches(CacheOperationInvocationContext<CacheOperation> context, CacheResolver cacheResolver) {
        Collection<? extends Cache> caches = cacheResolver.resolveCaches(context);
        if (caches.isEmpty()) {
            throw new IllegalStateException("No cache could be resolved for '" + context.getOperation() + "' using resolver '" + cacheResolver + "'. At least one cache should be provided per cache operation.");
        }
        return caches;
    }

    protected CacheOperationContext getOperationContext(CacheOperation operation, Method method, Object[] args, Object target, Class<?> targetClass) {
        CacheOperationMetadata metadata = getCacheOperationMetadata(operation, method, targetClass);
        return new CacheOperationContext(metadata, args, target);
    }

    protected CacheOperationMetadata getCacheOperationMetadata(CacheOperation operation, Method method, Class<?> targetClass) {
        KeyGenerator operationKeyGenerator;
        CacheResolver operationCacheResolver;
        CacheOperationCacheKey cacheKey = new CacheOperationCacheKey(operation, method, targetClass);
        CacheOperationMetadata metadata = this.metadataCache.get(cacheKey);
        if (metadata == null) {
            if (StringUtils.hasText(operation.getKeyGenerator())) {
                operationKeyGenerator = (KeyGenerator) getBean(operation.getKeyGenerator(), KeyGenerator.class);
            } else {
                operationKeyGenerator = getKeyGenerator();
            }
            if (StringUtils.hasText(operation.getCacheResolver())) {
                operationCacheResolver = (CacheResolver) getBean(operation.getCacheResolver(), CacheResolver.class);
            } else if (StringUtils.hasText(operation.getCacheManager())) {
                CacheManager cacheManager = (CacheManager) getBean(operation.getCacheManager(), CacheManager.class);
                operationCacheResolver = new SimpleCacheResolver(cacheManager);
            } else {
                operationCacheResolver = getCacheResolver();
                Assert.state(operationCacheResolver != null, "No CacheResolver/CacheManager set");
            }
            metadata = new CacheOperationMetadata(operation, method, targetClass, operationKeyGenerator, operationCacheResolver);
            this.metadataCache.put(cacheKey, metadata);
        }
        return metadata;
    }

    protected <T> T getBean(String str, Class<T> cls) {
        if (this.beanFactory == null) {
            throw new IllegalStateException("BeanFactory must be set on cache aspect for " + cls.getSimpleName() + " retrieval");
        }
        return (T) BeanFactoryAnnotationUtils.qualifiedBeanOfType(this.beanFactory, cls, str);
    }

    protected void clearMetadataCache() {
        this.metadataCache.clear();
        this.evaluator.clear();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] args) {
        if (this.initialized) {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
            CacheOperationSource cacheOperationSource = getCacheOperationSource();
            if (cacheOperationSource != null) {
                Collection<CacheOperation> operations = cacheOperationSource.getCacheOperations(method, targetClass);
                if (!CollectionUtils.isEmpty(operations)) {
                    return execute(invoker, method, new CacheOperationContexts(operations, method, args, target, targetClass));
                }
            }
        }
        return invokeOperation(invoker);
    }

    @Nullable
    protected Object invokeOperation(CacheOperationInvoker invoker) {
        return invoker.invoke();
    }

    @Nullable
    private Object execute(CacheOperationInvoker invoker, Method method, CacheOperationContexts contexts) {
        if (contexts.isSynchronized()) {
            return executeSynchronized(invoker, method, contexts);
        }
        processCacheEvicts(contexts.get(CacheEvictOperation.class), true, CacheOperationExpressionEvaluator.NO_RESULT);
        Object cacheHit = findCachedValue(invoker, method, contexts);
        if (cacheHit == null || (cacheHit instanceof Cache.ValueWrapper)) {
            return evaluate(cacheHit, invoker, method, contexts);
        }
        return cacheHit;
    }

    @Nullable
    private Object executeSynchronized(CacheOperationInvoker invoker, Method method, CacheOperationContexts contexts) {
        Object returnValue;
        CacheOperationContext context = contexts.get(CacheableOperation.class).iterator().next();
        if (isConditionPassing(context, CacheOperationExpressionEvaluator.NO_RESULT)) {
            Object key = generateKey(context, CacheOperationExpressionEvaluator.NO_RESULT);
            Cache cache = context.getCaches().iterator().next();
            if (CompletableFuture.class.isAssignableFrom(method.getReturnType())) {
                return cache.retrieve(key, () -> {
                    return (CompletableFuture) invokeOperation(invoker);
                });
            }
            if (this.reactiveCachingHandler != null && (returnValue = this.reactiveCachingHandler.executeSynchronized(invoker, method, cache, key)) != ReactiveCachingHandler.NOT_HANDLED) {
                return returnValue;
            }
            try {
                return wrapCacheValue(method, cache.get(key, () -> {
                    return unwrapReturnValue(invokeOperation(invoker));
                }));
            } catch (Cache.ValueRetrievalException ex) {
                ReflectionUtils.rethrowRuntimeException(ex.getCause());
                return null;
            }
        }
        return invokeOperation(invoker);
    }

    @Nullable
    private Object findCachedValue(CacheOperationInvoker invoker, Method method, CacheOperationContexts contexts) {
        for (CacheOperationContext context : contexts.get(CacheableOperation.class)) {
            if (isConditionPassing(context, CacheOperationExpressionEvaluator.NO_RESULT)) {
                Object key = generateKey(context, CacheOperationExpressionEvaluator.NO_RESULT);
                Object cached = findInCaches(context, key, invoker, method, contexts);
                if (cached != null) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("Cache entry for key '" + key + "' found in cache(s) " + context.getCacheNames());
                    }
                    return cached;
                }
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("No cache entry for key '" + key + "' in cache(s) " + context.getCacheNames());
                }
            }
        }
        return null;
    }

    @Nullable
    private Object findInCaches(CacheOperationContext context, Object key, CacheOperationInvoker invoker, Method method, CacheOperationContexts contexts) {
        Object returnValue;
        CompletableFuture<?> result;
        for (Cache cache : context.getCaches()) {
            if (CompletableFuture.class.isAssignableFrom(context.getMethod().getReturnType()) && (result = cache.retrieve(key)) != null) {
                return result.thenCompose(value -> {
                    return (CompletableFuture) evaluate(value != null ? CompletableFuture.completedFuture(unwrapCacheValue(value)) : null, invoker, method, contexts);
                });
            }
            if (this.reactiveCachingHandler != null && (returnValue = this.reactiveCachingHandler.findInCaches(context, cache, key, invoker, method, contexts)) != ReactiveCachingHandler.NOT_HANDLED) {
                return returnValue;
            }
            Cache.ValueWrapper result2 = doGet(cache, key);
            if (result2 != null) {
                return result2;
            }
        }
        return null;
    }

    @Nullable
    private Object evaluate(@Nullable Object cacheHit, CacheOperationInvoker invoker, Method method, CacheOperationContexts contexts) {
        Object returnValue;
        Object cacheValue;
        if (contexts.processed) {
            return cacheHit;
        }
        if (cacheHit != null && !hasCachePut(contexts)) {
            cacheValue = unwrapCacheValue(cacheHit);
            returnValue = wrapCacheValue(method, cacheValue);
        } else {
            returnValue = invokeOperation(invoker);
            cacheValue = unwrapReturnValue(returnValue);
        }
        List<CachePutRequest> cachePutRequests = new ArrayList<>(1);
        if (cacheHit == null) {
            collectPutRequests(contexts.get(CacheableOperation.class), cacheValue, cachePutRequests);
        }
        collectPutRequests(contexts.get(CachePutOperation.class), cacheValue, cachePutRequests);
        for (CachePutRequest cachePutRequest : cachePutRequests) {
            Object returnOverride = cachePutRequest.apply(cacheValue);
            if (returnOverride != null) {
                returnValue = returnOverride;
            }
        }
        Object returnOverride2 = processCacheEvicts(contexts.get(CacheEvictOperation.class), false, returnValue);
        if (returnOverride2 != null) {
            returnValue = returnOverride2;
        }
        contexts.processed = true;
        return returnValue;
    }

    @Nullable
    private Object unwrapCacheValue(@Nullable Object cacheValue) {
        if (!(cacheValue instanceof Cache.ValueWrapper)) {
            return cacheValue;
        }
        Cache.ValueWrapper wrapper = (Cache.ValueWrapper) cacheValue;
        return wrapper.get();
    }

    @Nullable
    private Object wrapCacheValue(Method method, @Nullable Object cacheValue) {
        if (method.getReturnType() == Optional.class && (cacheValue == null || cacheValue.getClass() != Optional.class)) {
            return Optional.ofNullable(cacheValue);
        }
        return cacheValue;
    }

    @Nullable
    private Object unwrapReturnValue(@Nullable Object returnValue) {
        return ObjectUtils.unwrapOptional(returnValue);
    }

    private boolean hasCachePut(CacheOperationContexts contexts) {
        Collection<CacheOperationContext> cachePutContexts = contexts.get(CachePutOperation.class);
        Collection<CacheOperationContext> excluded = new ArrayList<>(1);
        for (CacheOperationContext context : cachePutContexts) {
            try {
                if (!context.isConditionPassing(CacheOperationExpressionEvaluator.RESULT_UNAVAILABLE)) {
                    excluded.add(context);
                }
            } catch (VariableNotAvailableException e) {
            }
        }
        return cachePutContexts.size() != excluded.size();
    }

    @Nullable
    private Object processCacheEvicts(Collection<CacheOperationContext> contexts, boolean beforeInvocation, @Nullable Object result) {
        Object returnValue;
        if (contexts.isEmpty()) {
            return null;
        }
        List<CacheOperationContext> applicable = contexts.stream().filter(context -> {
            CacheOperation patt24176$temp = context.metadata.operation;
            if (patt24176$temp instanceof CacheEvictOperation) {
                CacheEvictOperation evict = (CacheEvictOperation) patt24176$temp;
                if (beforeInvocation == evict.isBeforeInvocation()) {
                    return true;
                }
            }
            return false;
        }).toList();
        if (applicable.isEmpty()) {
            return null;
        }
        if (result instanceof CompletableFuture) {
            CompletableFuture<?> future = (CompletableFuture) result;
            return future.whenComplete((value, ex) -> {
                if (ex == null) {
                    performCacheEvicts(applicable, result);
                }
            });
        }
        if (this.reactiveCachingHandler != null && (returnValue = this.reactiveCachingHandler.processCacheEvicts(applicable, result)) != ReactiveCachingHandler.NOT_HANDLED) {
            return returnValue;
        }
        performCacheEvicts(applicable, result);
        return null;
    }

    private void performCacheEvicts(List<CacheOperationContext> contexts, @Nullable Object result) {
        for (CacheOperationContext context : contexts) {
            CacheEvictOperation operation = (CacheEvictOperation) context.metadata.operation;
            if (isConditionPassing(context, result)) {
                Object key = context.getGeneratedKey();
                for (Cache cache : context.getCaches()) {
                    if (operation.isCacheWide()) {
                        logInvalidating(context, operation, null);
                        doClear(cache, operation.isBeforeInvocation());
                    } else {
                        if (key == null) {
                            key = generateKey(context, result);
                        }
                        logInvalidating(context, operation, key);
                        doEvict(cache, key, operation.isBeforeInvocation());
                    }
                }
            }
        }
    }

    private void logInvalidating(CacheOperationContext context, CacheEvictOperation operation, @Nullable Object key) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Invalidating " + (key != null ? "cache key [" + key + "]" : "entire cache") + " for operation " + operation + " on method " + context.metadata.method);
        }
    }

    private void collectPutRequests(Collection<CacheOperationContext> contexts, @Nullable Object result, Collection<CachePutRequest> putRequests) {
        for (CacheOperationContext context : contexts) {
            if (isConditionPassing(context, result)) {
                putRequests.add(new CachePutRequest(context));
            }
        }
    }

    private boolean isConditionPassing(CacheOperationContext context, @Nullable Object result) {
        boolean passing = context.isConditionPassing(result);
        if (!passing && this.logger.isTraceEnabled()) {
            this.logger.trace("Cache condition failed on method " + context.metadata.method + " for operation " + context.metadata.operation);
        }
        return passing;
    }

    private Object generateKey(CacheOperationContext context, @Nullable Object result) {
        Object key = context.generateKey(result);
        if (key == null) {
            throw new IllegalArgumentException("Null key returned for cache operation [%s]. If you are using named parameters, ensure that the compiler uses the '-parameters' flag.".formatted(context.metadata.operation));
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Computed cache key '" + key + "' for operation " + context.metadata.operation);
        }
        return key;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheAspectSupport$CacheOperationContexts.class */
    public class CacheOperationContexts {
        private final MultiValueMap<Class<? extends CacheOperation>, CacheOperationContext> contexts;
        private final boolean sync;
        boolean processed;

        /* JADX WARN: Multi-variable type inference failed */
        public CacheOperationContexts(Collection<? extends CacheOperation> operations, Method method, Object[] args, Object target, Class<?> targetClass) {
            this.contexts = new LinkedMultiValueMap(operations.size());
            for (CacheOperation op : operations) {
                this.contexts.add(op.getClass(), CacheAspectSupport.this.getOperationContext(op, method, args, target, targetClass));
            }
            this.sync = determineSyncFlag(method);
        }

        public Collection<CacheOperationContext> get(Class<? extends CacheOperation> operationClass) {
            Collection<CacheOperationContext> result = (Collection) this.contexts.get(operationClass);
            return result != null ? result : Collections.emptyList();
        }

        public boolean isSynchronized() {
            return this.sync;
        }

        private boolean determineSyncFlag(Method method) {
            List<CacheOperationContext> cacheableContexts = (List) this.contexts.get(CacheableOperation.class);
            if (cacheableContexts == null) {
                return false;
            }
            boolean syncEnabled = false;
            Iterator<CacheOperationContext> it = cacheableContexts.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                CacheOperationContext context = it.next();
                CacheOperation operation = context.getOperation();
                if (operation instanceof CacheableOperation) {
                    CacheableOperation cacheable = (CacheableOperation) operation;
                    if (cacheable.isSync()) {
                        syncEnabled = true;
                        break;
                    }
                }
            }
            if (syncEnabled) {
                if (this.contexts.size() > 1) {
                    throw new IllegalStateException("A sync=true operation cannot be combined with other cache operations on '" + method + "'");
                }
                if (cacheableContexts.size() > 1) {
                    throw new IllegalStateException("Only one sync=true operation is allowed on '" + method + "'");
                }
                CacheOperationContext cacheableContext = cacheableContexts.iterator().next();
                CacheOperation operation2 = cacheableContext.getOperation();
                if (cacheableContext.getCaches().size() > 1) {
                    throw new IllegalStateException("A sync=true operation is restricted to a single cache on '" + operation2 + "'");
                }
                if (!(operation2 instanceof CacheableOperation)) {
                    return true;
                }
                CacheableOperation cacheable2 = (CacheableOperation) operation2;
                if (StringUtils.hasText(cacheable2.getUnless())) {
                    throw new IllegalStateException("A sync=true operation does not support the unless attribute on '" + operation2 + "'");
                }
                return true;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheAspectSupport$CacheOperationMetadata.class */
    public static class CacheOperationMetadata {
        private final CacheOperation operation;
        private final Method method;
        private final Class<?> targetClass;
        private final Method targetMethod;
        private final AnnotatedElementKey methodKey;
        private final KeyGenerator keyGenerator;
        private final CacheResolver cacheResolver;

        public CacheOperationMetadata(CacheOperation operation, Method method, Class<?> targetClass, KeyGenerator keyGenerator, CacheResolver cacheResolver) {
            this.operation = operation;
            this.method = BridgeMethodResolver.findBridgedMethod(method);
            this.targetClass = targetClass;
            this.targetMethod = !Proxy.isProxyClass(targetClass) ? AopUtils.getMostSpecificMethod(method, targetClass) : this.method;
            this.methodKey = new AnnotatedElementKey(this.targetMethod, targetClass);
            this.keyGenerator = keyGenerator;
            this.cacheResolver = cacheResolver;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheAspectSupport$CacheOperationContext.class */
    public class CacheOperationContext implements CacheOperationInvocationContext<CacheOperation> {
        private final CacheOperationMetadata metadata;
        private final Object[] args;
        private final Object target;
        private final Collection<? extends Cache> caches;
        private final Collection<String> cacheNames;

        @Nullable
        private Boolean conditionPassing;

        @Nullable
        private Object key;

        public CacheOperationContext(CacheOperationMetadata metadata, Object[] args, Object target) {
            this.metadata = metadata;
            this.args = extractArgs(metadata.method, args);
            this.target = target;
            this.caches = CacheAspectSupport.this.getCaches(this, metadata.cacheResolver);
            this.cacheNames = prepareCacheNames(this.caches);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.cache.interceptor.CacheOperationInvocationContext
        public CacheOperation getOperation() {
            return this.metadata.operation;
        }

        @Override // org.springframework.cache.interceptor.CacheOperationInvocationContext
        public Object getTarget() {
            return this.target;
        }

        @Override // org.springframework.cache.interceptor.CacheOperationInvocationContext
        public Method getMethod() {
            return this.metadata.method;
        }

        @Override // org.springframework.cache.interceptor.CacheOperationInvocationContext
        public Object[] getArgs() {
            return this.args;
        }

        private Object[] extractArgs(Method method, Object[] args) {
            if (!method.isVarArgs()) {
                return args;
            }
            Object[] varArgs = ObjectUtils.toObjectArray(args[args.length - 1]);
            Object[] combinedArgs = new Object[(args.length - 1) + varArgs.length];
            System.arraycopy(args, 0, combinedArgs, 0, args.length - 1);
            System.arraycopy(varArgs, 0, combinedArgs, args.length - 1, varArgs.length);
            return combinedArgs;
        }

        protected boolean isConditionPassing(@Nullable Object result) {
            if (this.conditionPassing == null) {
                if (StringUtils.hasText(this.metadata.operation.getCondition())) {
                    EvaluationContext evaluationContext = createEvaluationContext(result);
                    this.conditionPassing = Boolean.valueOf(CacheAspectSupport.this.evaluator.condition(this.metadata.operation.getCondition(), this.metadata.methodKey, evaluationContext));
                } else {
                    this.conditionPassing = true;
                }
            }
            return this.conditionPassing.booleanValue();
        }

        protected boolean canPutToCache(@Nullable Object value) {
            String unless = "";
            CacheOperation cacheOperation = this.metadata.operation;
            if (cacheOperation instanceof CacheableOperation) {
                CacheableOperation cacheableOperation = (CacheableOperation) cacheOperation;
                unless = cacheableOperation.getUnless();
            } else {
                CacheOperation cacheOperation2 = this.metadata.operation;
                if (cacheOperation2 instanceof CachePutOperation) {
                    CachePutOperation cachePutOperation = (CachePutOperation) cacheOperation2;
                    unless = cachePutOperation.getUnless();
                }
            }
            if (StringUtils.hasText(unless)) {
                EvaluationContext evaluationContext = createEvaluationContext(value);
                return !CacheAspectSupport.this.evaluator.unless(unless, this.metadata.methodKey, evaluationContext);
            }
            return true;
        }

        @Nullable
        protected Object generateKey(@Nullable Object result) {
            if (StringUtils.hasText(this.metadata.operation.getKey())) {
                EvaluationContext evaluationContext = createEvaluationContext(result);
                this.key = CacheAspectSupport.this.evaluator.key(this.metadata.operation.getKey(), this.metadata.methodKey, evaluationContext);
            } else {
                this.key = this.metadata.keyGenerator.generate(this.target, this.metadata.method, this.args);
            }
            return this.key;
        }

        @Nullable
        protected Object getGeneratedKey() {
            return this.key;
        }

        private EvaluationContext createEvaluationContext(@Nullable Object result) {
            return CacheAspectSupport.this.evaluator.createEvaluationContext(this.caches, this.metadata.method, this.args, this.target, this.metadata.targetClass, this.metadata.targetMethod, result);
        }

        protected Collection<? extends Cache> getCaches() {
            return this.caches;
        }

        protected Collection<String> getCacheNames() {
            return this.cacheNames;
        }

        private Collection<String> prepareCacheNames(Collection<? extends Cache> caches) {
            Collection<String> names = new ArrayList<>(caches.size());
            for (Cache cache : caches) {
                names.add(cache.getName());
            }
            return names;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheAspectSupport$CacheOperationCacheKey.class */
    public static final class CacheOperationCacheKey implements Comparable<CacheOperationCacheKey> {
        private final CacheOperation cacheOperation;
        private final AnnotatedElementKey methodCacheKey;

        private CacheOperationCacheKey(CacheOperation cacheOperation, Method method, Class<?> targetClass) {
            this.cacheOperation = cacheOperation;
            this.methodCacheKey = new AnnotatedElementKey(method, targetClass);
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof CacheOperationCacheKey) {
                    CacheOperationCacheKey that = (CacheOperationCacheKey) other;
                    if (!this.cacheOperation.equals(that.cacheOperation) || !this.methodCacheKey.equals(that.methodCacheKey)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (this.cacheOperation.hashCode() * 31) + this.methodCacheKey.hashCode();
        }

        public String toString() {
            return this.cacheOperation + " on " + this.methodCacheKey;
        }

        @Override // java.lang.Comparable
        public int compareTo(CacheOperationCacheKey other) {
            int result = this.cacheOperation.getName().compareTo(other.cacheOperation.getName());
            if (result == 0) {
                result = this.methodCacheKey.compareTo(other.methodCacheKey);
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheAspectSupport$CachePutRequest.class */
    public class CachePutRequest {
        private final CacheOperationContext context;

        public CachePutRequest(CacheOperationContext context) {
            this.context = context;
        }

        @Nullable
        public Object apply(@Nullable Object result) {
            Object returnValue;
            if (result instanceof CompletableFuture) {
                CompletableFuture<?> future = (CompletableFuture) result;
                return future.whenComplete((value, ex) -> {
                    if (ex == null) {
                        performCachePut(value);
                    }
                });
            }
            if (CacheAspectSupport.this.reactiveCachingHandler != null && (returnValue = CacheAspectSupport.this.reactiveCachingHandler.processPutRequest(this, result)) != ReactiveCachingHandler.NOT_HANDLED) {
                return returnValue;
            }
            performCachePut(result);
            return null;
        }

        public void performCachePut(@Nullable Object value) {
            if (this.context.canPutToCache(value)) {
                Object key = this.context.getGeneratedKey();
                if (key == null) {
                    key = CacheAspectSupport.this.generateKey(this.context, value);
                }
                if (CacheAspectSupport.this.logger.isTraceEnabled()) {
                    CacheAspectSupport.this.logger.trace("Creating cache entry for key '" + key + "' in cache(s) " + this.context.getCacheNames());
                }
                for (Cache cache : this.context.getCaches()) {
                    CacheAspectSupport.this.doPut(cache, key, value);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheAspectSupport$CachePutListSubscriber.class */
    public class CachePutListSubscriber implements Subscriber<Object> {
        private final CachePutRequest request;
        private final List<Object> cacheValue = new ArrayList();

        public CachePutListSubscriber(CachePutRequest request) {
            this.request = request;
        }

        @Override // org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            s.request(2147483647L);
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(Object o) {
            this.cacheValue.add(o);
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            this.request.performCachePut(this.cacheValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheAspectSupport$ReactiveCachingHandler.class */
    public class ReactiveCachingHandler {
        public static final Object NOT_HANDLED = new Object();
        private final ReactiveAdapterRegistry registry = ReactiveAdapterRegistry.getSharedInstance();

        private ReactiveCachingHandler() {
        }

        @Nullable
        public Object executeSynchronized(CacheOperationInvoker invoker, Method method, Cache cache, Object key) {
            ReactiveAdapter adapter = this.registry.getAdapter(method.getReturnType());
            if (adapter != null) {
                if (adapter.isMultiValue()) {
                    return adapter.fromPublisher(Flux.from(Mono.fromFuture(cache.retrieve(key, () -> {
                        return Flux.from(adapter.toPublisher(CacheAspectSupport.this.invokeOperation(invoker))).collectList().toFuture();
                    }))).flatMap((v0) -> {
                        return Flux.fromIterable(v0);
                    }));
                }
                return adapter.fromPublisher(Mono.fromFuture(cache.retrieve(key, () -> {
                    return Mono.from(adapter.toPublisher(CacheAspectSupport.this.invokeOperation(invoker))).toFuture();
                })));
            }
            if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isSuspendingFunction(method)) {
                return Mono.fromFuture(cache.retrieve(key, () -> {
                    return ((Mono) CacheAspectSupport.this.invokeOperation(invoker)).toFuture();
                }));
            }
            return NOT_HANDLED;
        }

        @Nullable
        public Object processCacheEvicts(List<CacheOperationContext> contexts, @Nullable Object result) {
            ReactiveAdapter adapter = result != null ? this.registry.getAdapter(result.getClass()) : null;
            if (adapter != null) {
                return adapter.fromPublisher(Mono.from(adapter.toPublisher(result)).doOnSuccess(value -> {
                    CacheAspectSupport.this.performCacheEvicts(contexts, result);
                }));
            }
            return NOT_HANDLED;
        }

        @Nullable
        public Object findInCaches(CacheOperationContext context, Cache cache, Object key, CacheOperationInvoker invoker, Method method, CacheOperationContexts contexts) {
            ReactiveAdapter adapter = this.registry.getAdapter(context.getMethod().getReturnType());
            if (adapter != null) {
                CompletableFuture<?> cachedFuture = cache.retrieve(key);
                if (cachedFuture == null) {
                    return null;
                }
                if (adapter.isMultiValue()) {
                    return adapter.fromPublisher(Flux.from(Mono.fromFuture(cachedFuture)).switchIfEmpty(Flux.defer(() -> {
                        return (Flux) CacheAspectSupport.this.evaluate(null, invoker, method, contexts);
                    })).flatMap(v -> {
                        return CacheAspectSupport.this.evaluate(valueToFlux(v, contexts), invoker, method, contexts);
                    }));
                }
                return adapter.fromPublisher(Mono.fromFuture(cachedFuture).switchIfEmpty(Mono.defer(() -> {
                    return (Mono) CacheAspectSupport.this.evaluate(null, invoker, method, contexts);
                })).flatMap(v2 -> {
                    return CacheAspectSupport.this.evaluate(Mono.justOrEmpty(CacheAspectSupport.this.unwrapCacheValue(v2)), invoker, method, contexts);
                }));
            }
            return NOT_HANDLED;
        }

        private Flux<?> valueToFlux(Object value, CacheOperationContexts contexts) {
            Object data = CacheAspectSupport.this.unwrapCacheValue(value);
            if (contexts.processed || !(data instanceof Iterable)) {
                return data != null ? Flux.just(data) : Flux.empty();
            }
            Iterable<?> iterable = (Iterable) data;
            return Flux.fromIterable(iterable);
        }

        @Nullable
        public Object processPutRequest(CachePutRequest request, @Nullable Object result) {
            ReactiveAdapter adapter = result != null ? this.registry.getAdapter(result.getClass()) : null;
            if (adapter != null) {
                if (adapter.isMultiValue()) {
                    Flux<?> source = Flux.from(adapter.toPublisher(result));
                    source.subscribe(new CachePutListSubscriber(request));
                    return adapter.fromPublisher(source);
                }
                Mono from = Mono.from(adapter.toPublisher(result));
                Objects.requireNonNull(request);
                return adapter.fromPublisher(from.doOnSuccess(request::performCachePut));
            }
            return NOT_HANDLED;
        }
    }
}
