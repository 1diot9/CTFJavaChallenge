package org.apache.tomcat.util.scan;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Manifest;
import org.apache.tomcat.Jar;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/scan/ReferenceCountedJar.class */
public class ReferenceCountedJar implements Jar {
    private final URL url;
    private Jar wrappedJar;
    private int referenceCount = 0;

    public ReferenceCountedJar(URL url) throws IOException {
        this.url = url;
        open();
    }

    private synchronized ReferenceCountedJar open() throws IOException {
        if (this.wrappedJar == null) {
            this.wrappedJar = JarFactory.newInstance(this.url);
        }
        this.referenceCount++;
        return this;
    }

    @Override // org.apache.tomcat.Jar, java.lang.AutoCloseable
    public synchronized void close() {
        this.referenceCount--;
        if (this.referenceCount == 0) {
            this.wrappedJar.close();
            this.wrappedJar = null;
        }
    }

    @Override // org.apache.tomcat.Jar
    public URL getJarFileURL() {
        return this.url;
    }

    @Override // org.apache.tomcat.Jar
    public InputStream getInputStream(String name) throws IOException {
        ReferenceCountedJar jar = open();
        try {
            InputStream inputStream = jar.wrappedJar.getInputStream(name);
            if (jar != null) {
                jar.close();
            }
            return inputStream;
        } catch (Throwable th) {
            if (jar != null) {
                try {
                    jar.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // org.apache.tomcat.Jar
    public long getLastModified(String name) throws IOException {
        ReferenceCountedJar jar = open();
        try {
            long lastModified = jar.wrappedJar.getLastModified(name);
            if (jar != null) {
                jar.close();
            }
            return lastModified;
        } catch (Throwable th) {
            if (jar != null) {
                try {
                    jar.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // org.apache.tomcat.Jar
    public boolean exists(String name) throws IOException {
        ReferenceCountedJar jar = open();
        try {
            boolean exists = jar.wrappedJar.exists(name);
            if (jar != null) {
                jar.close();
            }
            return exists;
        } catch (Throwable th) {
            if (jar != null) {
                try {
                    jar.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // org.apache.tomcat.Jar
    public void nextEntry() {
        try {
            ReferenceCountedJar jar = open();
            try {
                jar.wrappedJar.nextEntry();
                if (jar != null) {
                    jar.close();
                }
            } finally {
            }
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    @Override // org.apache.tomcat.Jar
    public String getEntryName() {
        try {
            ReferenceCountedJar jar = open();
            try {
                String entryName = jar.wrappedJar.getEntryName();
                if (jar != null) {
                    jar.close();
                }
                return entryName;
            } finally {
            }
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    @Override // org.apache.tomcat.Jar
    public InputStream getEntryInputStream() throws IOException {
        ReferenceCountedJar jar = open();
        try {
            InputStream entryInputStream = jar.wrappedJar.getEntryInputStream();
            if (jar != null) {
                jar.close();
            }
            return entryInputStream;
        } catch (Throwable th) {
            if (jar != null) {
                try {
                    jar.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // org.apache.tomcat.Jar
    public String getURL(String entry) {
        try {
            ReferenceCountedJar jar = open();
            try {
                String url = jar.wrappedJar.getURL(entry);
                if (jar != null) {
                    jar.close();
                }
                return url;
            } finally {
            }
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    @Override // org.apache.tomcat.Jar
    public Manifest getManifest() throws IOException {
        ReferenceCountedJar jar = open();
        try {
            Manifest manifest = jar.wrappedJar.getManifest();
            if (jar != null) {
                jar.close();
            }
            return manifest;
        } catch (Throwable th) {
            if (jar != null) {
                try {
                    jar.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // org.apache.tomcat.Jar
    public void reset() throws IOException {
        ReferenceCountedJar jar = open();
        try {
            jar.wrappedJar.reset();
            if (jar != null) {
                jar.close();
            }
        } catch (Throwable th) {
            if (jar != null) {
                try {
                    jar.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }
}
