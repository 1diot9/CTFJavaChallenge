package org.springframework.boot.jarmode.layertools;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-jarmode-layertools-3.2.2.jar:org/springframework/boot/jarmode/layertools/Context.class */
class Context {
    private final File archiveFile;
    private final File workingDir;
    private final String relativeDir;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Context() {
        this(getSourceArchiveFile(), Paths.get(".", new String[0]).toAbsolutePath().normalize().toFile());
    }

    Context(File archiveFile, File workingDir) {
        Assert.state(isExistingFile(archiveFile), "Unable to find source archive");
        Assert.state(isJarOrWar(archiveFile), "Source archive " + archiveFile + " must end with .jar or .war");
        this.archiveFile = archiveFile;
        this.workingDir = workingDir;
        this.relativeDir = deduceRelativeDir(archiveFile.getParentFile(), this.workingDir);
    }

    private boolean isExistingFile(File archiveFile) {
        return archiveFile != null && archiveFile.isFile() && archiveFile.exists();
    }

    private boolean isJarOrWar(File jarFile) {
        String name = jarFile.getName().toLowerCase();
        return name.endsWith(".jar") || name.endsWith(".war");
    }

    private static File getSourceArchiveFile() {
        try {
            ProtectionDomain domain = Context.class.getProtectionDomain();
            CodeSource codeSource = domain != null ? domain.getCodeSource() : null;
            URL location = codeSource != null ? codeSource.getLocation() : null;
            File source = location != null ? findSource(location) : null;
            if (source != null && source.exists()) {
                return source.getAbsoluteFile();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static File findSource(URL location) throws IOException, URISyntaxException {
        URLConnection connection = location.openConnection();
        if (connection instanceof JarURLConnection) {
            JarURLConnection jarURLConnection = (JarURLConnection) connection;
            return getRootJarFile(jarURLConnection.getJarFile());
        }
        return new File(location.toURI());
    }

    private static File getRootJarFile(JarFile jarFile) {
        String name = jarFile.getName();
        int separator = name.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
        if (separator > 0) {
            name = name.substring(0, separator);
        }
        return new File(name);
    }

    private String deduceRelativeDir(File sourceDirectory, File workingDir) {
        String sourcePath = sourceDirectory.getAbsolutePath();
        String workingPath = workingDir.getAbsolutePath();
        if (sourcePath.equals(workingPath) || !sourcePath.startsWith(workingPath)) {
            return null;
        }
        String relativePath = sourcePath.substring(workingPath.length() + 1);
        if (relativePath.isEmpty()) {
            return null;
        }
        return relativePath;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public File getArchiveFile() {
        return this.archiveFile;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public File getWorkingDir() {
        return this.workingDir;
    }

    String getRelativeArchiveDir() {
        return this.relativeDir;
    }
}
