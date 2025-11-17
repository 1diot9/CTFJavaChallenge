package org.apache.tomcat.util.scan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.apache.tomcat.Jar;
import org.springframework.util.ResourceUtils;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/scan/JarFileUrlJar.class */
public class JarFileUrlJar implements Jar {
    private final JarFile jarFile;
    private final URL jarFileURL;
    private final boolean multiRelease;
    private Enumeration<JarEntry> entries;
    private Set<String> entryNamesSeen;
    private JarEntry entry = null;

    public JarFileUrlJar(URL url, boolean startsWithJar) throws IOException {
        if (startsWithJar) {
            JarURLConnection jarConn = (JarURLConnection) url.openConnection();
            jarConn.setUseCaches(false);
            this.jarFile = jarConn.getJarFile();
            this.jarFileURL = jarConn.getJarFileURL();
        } else {
            try {
                File f = new File(url.toURI());
                this.jarFile = new JarFile(f, true, 1, Runtime.version());
                this.jarFileURL = url;
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }
        }
        boolean multiReleaseValue = false;
        try {
            multiReleaseValue = this.jarFile.isMultiRelease();
        } catch (IllegalStateException e2) {
        }
        this.multiRelease = multiReleaseValue;
    }

    @Override // org.apache.tomcat.Jar
    public URL getJarFileURL() {
        return this.jarFileURL;
    }

    @Override // org.apache.tomcat.Jar
    public InputStream getInputStream(String name) throws IOException {
        ZipEntry entry = this.jarFile.getEntry(name);
        if (entry == null) {
            return null;
        }
        return this.jarFile.getInputStream(entry);
    }

    @Override // org.apache.tomcat.Jar
    public long getLastModified(String name) throws IOException {
        ZipEntry entry = this.jarFile.getEntry(name);
        if (entry == null) {
            return -1L;
        }
        return entry.getTime();
    }

    @Override // org.apache.tomcat.Jar
    public boolean exists(String name) throws IOException {
        ZipEntry entry = this.jarFile.getEntry(name);
        return entry != null;
    }

    @Override // org.apache.tomcat.Jar
    public String getURL(String entry) {
        return ResourceUtils.JAR_URL_PREFIX + getJarFileURL().toExternalForm() + ResourceUtils.JAR_URL_SEPARATOR + entry;
    }

    @Override // org.apache.tomcat.Jar, java.lang.AutoCloseable
    public void close() {
        if (this.jarFile != null) {
            try {
                this.jarFile.close();
            } catch (IOException e) {
            }
        }
    }

    @Override // org.apache.tomcat.Jar
    public void nextEntry() {
        if (this.entries == null) {
            this.entries = this.jarFile.entries();
            if (this.multiRelease) {
                this.entryNamesSeen = new HashSet();
            }
        }
        if (this.multiRelease) {
            while (this.entries.hasMoreElements()) {
                this.entry = this.entries.nextElement();
                String name = this.entry.getName();
                if (name.startsWith("META-INF/versions/")) {
                    int i = name.indexOf(47, 18);
                    if (i == -1) {
                        continue;
                    } else {
                        name = name.substring(i + 1);
                    }
                }
                if (name.length() != 0 && !this.entryNamesSeen.contains(name)) {
                    this.entryNamesSeen.add(name);
                    this.entry = this.jarFile.getJarEntry(this.entry.getName());
                    return;
                }
            }
            this.entry = null;
            return;
        }
        if (this.entries.hasMoreElements()) {
            this.entry = this.entries.nextElement();
        } else {
            this.entry = null;
        }
    }

    @Override // org.apache.tomcat.Jar
    public String getEntryName() {
        if (this.entry == null) {
            return null;
        }
        return this.entry.getName();
    }

    @Override // org.apache.tomcat.Jar
    public InputStream getEntryInputStream() throws IOException {
        if (this.entry == null) {
            return null;
        }
        return this.jarFile.getInputStream(this.entry);
    }

    @Override // org.apache.tomcat.Jar
    public Manifest getManifest() throws IOException {
        return this.jarFile.getManifest();
    }

    @Override // org.apache.tomcat.Jar
    public void reset() throws IOException {
        this.entries = null;
        this.entryNamesSeen = null;
        this.entry = null;
    }
}
