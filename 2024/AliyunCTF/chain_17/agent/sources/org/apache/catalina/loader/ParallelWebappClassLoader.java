package org.apache.catalina.loader;

import org.apache.catalina.LifecycleException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.JreCompat;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/loader/ParallelWebappClassLoader.class */
public class ParallelWebappClassLoader extends WebappClassLoaderBase {
    private static final Log log = LogFactory.getLog((Class<?>) ParallelWebappClassLoader.class);

    static {
        if (!JreCompat.isGraalAvailable() && !registerAsParallelCapable()) {
            log.warn(sm.getString("webappClassLoaderParallel.registrationFailed"));
        }
    }

    public ParallelWebappClassLoader() {
    }

    public ParallelWebappClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override // org.apache.tomcat.InstrumentableClassLoader
    public ParallelWebappClassLoader copyWithoutTransformers() {
        ParallelWebappClassLoader result = new ParallelWebappClassLoader(getParent());
        super.copyStateWithoutTransformers(result);
        try {
            result.start();
            return result;
        } catch (LifecycleException e) {
            throw new IllegalStateException(e);
        }
    }
}
