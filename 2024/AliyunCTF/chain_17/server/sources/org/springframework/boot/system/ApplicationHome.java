package org.springframework.boot.system;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/system/ApplicationHome.class */
public class ApplicationHome {
    private final File source;
    private final File dir;

    public ApplicationHome() {
        this(null);
    }

    public ApplicationHome(Class<?> sourceClass) {
        this.source = findSource(sourceClass != null ? sourceClass : getStartClass());
        this.dir = findHomeDir(this.source);
    }

    private Class<?> getStartClass() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            return getStartClass(classLoader.getResources("META-INF/MANIFEST.MF"));
        } catch (Exception e) {
            return null;
        }
    }

    private Class<?> getStartClass(Enumeration<URL> manifestResources) {
        InputStream inputStream;
        String startClass;
        while (manifestResources.hasMoreElements()) {
            try {
                inputStream = manifestResources.nextElement().openStream();
                try {
                    Manifest manifest = new Manifest(inputStream);
                    startClass = manifest.getMainAttributes().getValue("Start-Class");
                } finally {
                }
            } catch (Exception e) {
            }
            if (startClass != null) {
                Class<?> forName = ClassUtils.forName(startClass, getClass().getClassLoader());
                if (inputStream != null) {
                    inputStream.close();
                }
                return forName;
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return null;
    }

    private File findSource(Class<?> sourceClass) {
        ProtectionDomain protectionDomain;
        if (sourceClass != null) {
            try {
                protectionDomain = sourceClass.getProtectionDomain();
            } catch (Exception e) {
                return null;
            }
        } else {
            protectionDomain = null;
        }
        ProtectionDomain domain = protectionDomain;
        CodeSource codeSource = domain != null ? domain.getCodeSource() : null;
        URL location = codeSource != null ? codeSource.getLocation() : null;
        File source = location != null ? findSource(location) : null;
        if (source != null && source.exists() && !isUnitTest()) {
            return source.getAbsoluteFile();
        }
        return null;
    }

    private boolean isUnitTest() {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (int i = stackTrace.length - 1; i >= 0; i--) {
                if (stackTrace[i].getClassName().startsWith("org.junit.")) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private File findSource(URL location) throws IOException, URISyntaxException {
        URLConnection connection = location.openConnection();
        if (connection instanceof JarURLConnection) {
            JarURLConnection jarURLConnection = (JarURLConnection) connection;
            return getRootJarFile(jarURLConnection.getJarFile());
        }
        return new File(location.toURI());
    }

    private File getRootJarFile(JarFile jarFile) {
        String name = jarFile.getName();
        int separator = name.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
        if (separator > 0) {
            name = name.substring(0, separator);
        }
        return new File(name);
    }

    private File findHomeDir(File source) {
        File homeDir = source != null ? source : findDefaultHomeDir();
        if (homeDir.isFile()) {
            homeDir = homeDir.getParentFile();
        }
        return (homeDir.exists() ? homeDir : new File(".")).getAbsoluteFile();
    }

    private File findDefaultHomeDir() {
        String userDir = System.getProperty("user.dir");
        return new File(StringUtils.hasLength(userDir) ? userDir : ".");
    }

    public File getSource() {
        return this.source;
    }

    public File getDir() {
        return this.dir;
    }

    public String toString() {
        return getDir().toString();
    }
}
