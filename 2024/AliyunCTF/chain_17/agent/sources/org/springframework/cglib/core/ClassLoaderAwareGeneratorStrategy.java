package org.springframework.cglib.core;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/ClassLoaderAwareGeneratorStrategy.class */
public class ClassLoaderAwareGeneratorStrategy extends DefaultGeneratorStrategy {
    private final ClassLoader classLoader;

    public ClassLoaderAwareGeneratorStrategy(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override // org.springframework.cglib.core.DefaultGeneratorStrategy, org.springframework.cglib.core.GeneratorStrategy
    public byte[] generate(ClassGenerator cg) throws Exception {
        if (this.classLoader == null) {
            return super.generate(cg);
        }
        Thread currentThread = Thread.currentThread();
        try {
            ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
            boolean overrideClassLoader = !this.classLoader.equals(threadContextClassLoader);
            if (overrideClassLoader) {
                currentThread.setContextClassLoader(this.classLoader);
            }
            try {
                byte[] generate = super.generate(cg);
                if (overrideClassLoader) {
                    currentThread.setContextClassLoader(threadContextClassLoader);
                }
                return generate;
            } catch (Throwable th) {
                if (overrideClassLoader) {
                    currentThread.setContextClassLoader(threadContextClassLoader);
                }
                throw th;
            }
        } catch (Throwable th2) {
            return super.generate(cg);
        }
    }
}
