package org.springframework.cglib.core;

import java.lang.ref.WeakReference;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import org.springframework.asm.ClassReader;
import org.springframework.cglib.core.internal.Function;
import org.springframework.cglib.core.internal.LoadingCache;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/AbstractClassGenerator.class */
public abstract class AbstractClassGenerator<T> implements ClassGenerator {
    private static final ThreadLocal CURRENT = new ThreadLocal();
    private static volatile Map<ClassLoader, ClassLoaderData> CACHE = new WeakHashMap();
    private static final boolean DEFAULT_USE_CACHE = Boolean.parseBoolean(System.getProperty("cglib.useCache", "true"));
    private static final boolean inNativeImage;
    private Source source;
    private ClassLoader classLoader;
    private Class contextClass;
    private String namePrefix;
    private Object key;
    private String className;
    private boolean attemptLoad;
    private GeneratorStrategy strategy = DefaultGeneratorStrategy.INSTANCE;
    private NamingPolicy namingPolicy = DefaultNamingPolicy.INSTANCE;
    private boolean useCache = DEFAULT_USE_CACHE;

    protected abstract ClassLoader getDefaultClassLoader();

    protected abstract Object firstInstance(Class type) throws Exception;

    protected abstract Object nextInstance(Object instance) throws Exception;

    static {
        String imageCode = System.getProperty("org.graalvm.nativeimage.imagecode");
        inNativeImage = "buildtime".equals(imageCode) || "runtime".equals(imageCode);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/AbstractClassGenerator$ClassLoaderData.class */
    public static class ClassLoaderData {
        private final Set<String> reservedClassNames = new HashSet();
        private final LoadingCache<AbstractClassGenerator, Object, Object> generatedClasses;
        private final WeakReference<ClassLoader> classLoader;
        private final Predicate uniqueNamePredicate;
        private static final Function<AbstractClassGenerator, Object> GET_KEY = gen -> {
            return gen.key;
        };

        public ClassLoaderData(ClassLoader classLoader) {
            Set<String> set = this.reservedClassNames;
            Objects.requireNonNull(set);
            this.uniqueNamePredicate = set::contains;
            if (classLoader == null) {
                throw new IllegalArgumentException("classLoader == null is not yet supported");
            }
            this.classLoader = new WeakReference<>(classLoader);
            Function<AbstractClassGenerator, Object> load = gen -> {
                Class klass = gen.generate(this);
                return gen.wrapCachedClass(klass);
            };
            this.generatedClasses = new LoadingCache<>(GET_KEY, load);
        }

        public ClassLoader getClassLoader() {
            return this.classLoader.get();
        }

        public void reserveName(String name) {
            this.reservedClassNames.add(name);
        }

        public Predicate getUniqueNamePredicate() {
            return this.uniqueNamePredicate;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Object get(AbstractClassGenerator gen, boolean useCache) {
            if (!useCache) {
                return gen.generate(this);
            }
            Object cachedValue = this.generatedClasses.get(gen);
            return gen.unwrapCachedValue(cachedValue);
        }
    }

    protected T wrapCachedClass(Class cls) {
        return (T) new WeakReference(cls);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    public Object unwrapCachedValue(T cached) {
        return ((WeakReference) cached).get();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/AbstractClassGenerator$Source.class */
    public static class Source {
        String name;

        public Source(String name) {
            this.name = name;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractClassGenerator(Source source) {
        this.source = source;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String getClassName() {
        return this.className;
    }

    private void setClassName(String className) {
        this.className = className;
    }

    private String generateClassName(Predicate nameTestPredicate) {
        return this.namingPolicy.getClassName(this.namePrefix, this.source.name, this.key, nameTestPredicate);
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setContextClass(Class contextClass) {
        this.contextClass = contextClass;
    }

    public void setNamingPolicy(NamingPolicy namingPolicy) {
        if (namingPolicy == null) {
            namingPolicy = DefaultNamingPolicy.INSTANCE;
        }
        this.namingPolicy = namingPolicy;
    }

    public NamingPolicy getNamingPolicy() {
        return this.namingPolicy;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public boolean getUseCache() {
        return this.useCache;
    }

    public void setAttemptLoad(boolean attemptLoad) {
        this.attemptLoad = attemptLoad;
    }

    public boolean getAttemptLoad() {
        return this.attemptLoad;
    }

    public void setStrategy(GeneratorStrategy strategy) {
        if (strategy == null) {
            strategy = DefaultGeneratorStrategy.INSTANCE;
        }
        this.strategy = strategy;
    }

    public GeneratorStrategy getStrategy() {
        return this.strategy;
    }

    public static AbstractClassGenerator getCurrent() {
        return (AbstractClassGenerator) CURRENT.get();
    }

    public ClassLoader getClassLoader() {
        ClassLoader t = this.classLoader;
        if (t == null) {
            t = getDefaultClassLoader();
        }
        if (t == null) {
            t = getClass().getClassLoader();
        }
        if (t == null) {
            t = Thread.currentThread().getContextClassLoader();
        }
        if (t == null) {
            throw new IllegalStateException("Cannot determine classloader");
        }
        return t;
    }

    protected ProtectionDomain getProtectionDomain() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object create(Object key) {
        try {
            try {
                ClassLoader loader = getClassLoader();
                ClassLoaderData data = CACHE.get(loader);
                if (data == null) {
                    synchronized (AbstractClassGenerator.class) {
                        Map<ClassLoader, ClassLoaderData> cache = CACHE;
                        data = cache.get(loader);
                        if (data == null) {
                            Map<ClassLoader, ClassLoaderData> newCache = new WeakHashMap<>(cache);
                            data = new ClassLoaderData(loader);
                            newCache.put(loader, data);
                            CACHE = newCache;
                        }
                    }
                }
                this.key = key;
                Object obj = data.get(this, getUseCache());
                if (obj instanceof Class) {
                    Class<?> clazz = (Class) obj;
                    return firstInstance(clazz);
                }
                return nextInstance(obj);
            } catch (Error | RuntimeException ex) {
                throw ex;
            }
        } catch (Exception ex2) {
            throw new CodeGenerationException(ex2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Multi-variable type inference failed */
    public Class generate(ClassLoaderData data) {
        Class gen;
        Class gen2;
        Object save = CURRENT.get();
        CURRENT.set(this);
        try {
            try {
                try {
                    ClassLoader classLoader = data.getClassLoader();
                    if (classLoader == null) {
                        throw new IllegalStateException("ClassLoader is null while trying to define class " + getClassName() + ". It seems that the loader has been expired from a weak reference somehow. Please file an issue at cglib's issue tracker.");
                    }
                    synchronized (classLoader) {
                        String name = generateClassName(data.getUniqueNamePredicate());
                        data.reserveName(name);
                        setClassName(name);
                    }
                    if (this.attemptLoad) {
                        try {
                            synchronized (classLoader) {
                                gen2 = ReflectUtils.loadClass(getClassName(), classLoader);
                            }
                            CURRENT.set(save);
                            return gen2;
                        } catch (ClassNotFoundException e) {
                        }
                    }
                    if (inNativeImage) {
                        throw new UnsupportedOperationException("CGLIB runtime enhancement not supported on native image. Make sure to include a pre-generated class on the classpath instead: " + getClassName());
                    }
                    byte[] b = this.strategy.generate(this);
                    String className = ClassNameReader.getClassName(new ClassReader(b));
                    ProtectionDomain protectionDomain = getProtectionDomain();
                    synchronized (classLoader) {
                        gen = ReflectUtils.defineClass(className, b, classLoader, protectionDomain, this.contextClass);
                    }
                    CURRENT.set(save);
                    return gen;
                } catch (Exception ex) {
                    throw new CodeGenerationException(ex);
                }
            } catch (Error | RuntimeException ex2) {
                throw ex2;
            }
        } catch (Throwable th) {
            CURRENT.set(save);
            throw th;
        }
    }
}
