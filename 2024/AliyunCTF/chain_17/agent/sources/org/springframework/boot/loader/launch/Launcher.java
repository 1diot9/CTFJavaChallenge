package org.springframework.boot.loader.launch;

import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Set;
import org.springframework.boot.loader.net.protocol.Handlers;

/* loaded from: agent.jar:org/springframework/boot/loader/launch/Launcher.class */
public abstract class Launcher {
    private static final String JAR_MODE_RUNNER_CLASS_NAME = JarModeRunner.class.getName();

    protected abstract Archive getArchive();

    protected abstract String getMainClass() throws Exception;

    protected abstract Set<URL> getClassPathUrls() throws Exception;

    /* JADX INFO: Access modifiers changed from: protected */
    public void launch(String[] args) throws Exception {
        if (!isExploded()) {
            Handlers.register();
        }
        try {
            ClassLoader classLoader = createClassLoader(getClassPathUrls());
            String jarMode = System.getProperty("jarmode");
            String mainClassName = hasLength(jarMode) ? JAR_MODE_RUNNER_CLASS_NAME : getMainClass();
            launch(classLoader, mainClassName, args);
        } catch (UncheckedIOException ex) {
            throw ex.getCause();
        }
    }

    private boolean hasLength(String jarMode) {
        return (jarMode == null || jarMode.isEmpty()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ClassLoader createClassLoader(Collection<URL> urls) throws Exception {
        return createClassLoader((URL[]) urls.toArray(new URL[0]));
    }

    private ClassLoader createClassLoader(URL[] urls) {
        ClassLoader parent = getClass().getClassLoader();
        return new LaunchedClassLoader(isExploded(), getArchive(), urls, parent);
    }

    protected void launch(ClassLoader classLoader, String mainClassName, String[] args) throws Exception {
        Thread.currentThread().setContextClassLoader(classLoader);
        Class<?> mainClass = Class.forName(mainClassName, false, classLoader);
        Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
        mainMethod.setAccessible(true);
        mainMethod.invoke(null, args);
    }

    protected boolean isExploded() {
        Archive archive = getArchive();
        return archive != null && archive.isExploded();
    }
}
