package org.springframework.boot.web.servlet.server;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.Locale;
import org.apache.commons.logging.Log;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/server/DocumentRoot.class */
class DocumentRoot {
    private static final String[] COMMON_DOC_ROOTS = {"src/main/webapp", "public", "static"};
    private final Log logger;
    private File directory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DocumentRoot(Log logger) {
        this.logger = logger;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public File getDirectory() {
        return this.directory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDirectory(File directory) {
        this.directory = directory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final File getValidDirectory() {
        File file = this.directory;
        File file2 = file != null ? file : getWarFileDocumentRoot();
        File file3 = file2 != null ? file2 : getExplodedWarFileDocumentRoot();
        File file4 = file3 != null ? file3 : getCommonDocumentRoot();
        if (file4 == null && this.logger.isDebugEnabled()) {
            logNoDocumentRoots();
        } else if (this.logger.isDebugEnabled()) {
            this.logger.debug("Document root: " + file4);
        }
        return file4;
    }

    private File getWarFileDocumentRoot() {
        return getArchiveFileDocumentRoot(".war");
    }

    private File getArchiveFileDocumentRoot(String extension) {
        File file = getCodeSourceArchive();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Code archive: " + file);
        }
        if (file != null && file.exists() && !file.isDirectory() && file.getName().toLowerCase(Locale.ENGLISH).endsWith(extension)) {
            return file.getAbsoluteFile();
        }
        return null;
    }

    private File getExplodedWarFileDocumentRoot() {
        return getExplodedWarFileDocumentRoot(getCodeSourceArchive());
    }

    private File getCodeSourceArchive() {
        return getCodeSourceArchive(getClass().getProtectionDomain().getCodeSource());
    }

    File getCodeSourceArchive(CodeSource codeSource) {
        URL location;
        String path;
        if (codeSource != null) {
            try {
                location = codeSource.getLocation();
            } catch (Exception e) {
                return null;
            }
        } else {
            location = null;
        }
        URL location2 = location;
        if (location2 == null) {
            return null;
        }
        URLConnection connection = location2.openConnection();
        if (connection instanceof JarURLConnection) {
            JarURLConnection jarURLConnection = (JarURLConnection) connection;
            path = jarURLConnection.getJarFile().getName();
        } else {
            path = location2.toURI().getPath();
        }
        int index = path.indexOf("!/");
        if (index != -1) {
            path = path.substring(0, index);
        }
        return new File(path);
    }

    final File getExplodedWarFileDocumentRoot(File codeSourceFile) {
        String path;
        int webInfPathIndex;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Code archive: " + codeSourceFile);
        }
        if (codeSourceFile != null && codeSourceFile.exists() && (webInfPathIndex = (path = codeSourceFile.getAbsolutePath()).indexOf(File.separatorChar + "WEB-INF" + File.separatorChar)) >= 0) {
            return new File(path.substring(0, webInfPathIndex));
        }
        return null;
    }

    private File getCommonDocumentRoot() {
        for (String commonDocRoot : COMMON_DOC_ROOTS) {
            File root = new File(commonDocRoot);
            if (root.exists() && root.isDirectory()) {
                return root.getAbsoluteFile();
            }
        }
        return null;
    }

    private void logNoDocumentRoots() {
        this.logger.debug("None of the document roots " + Arrays.asList(COMMON_DOC_ROOTS) + " point to a directory and will be ignored.");
    }
}
