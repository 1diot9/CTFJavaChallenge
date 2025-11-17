package org.apache.catalina.webresources;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/TomcatJarInputStream.class */
public class TomcatJarInputStream extends JarInputStream {
    private JarEntry metaInfEntry;
    private JarEntry manifestEntry;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TomcatJarInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override // java.util.jar.JarInputStream, java.util.zip.ZipInputStream
    protected ZipEntry createZipEntry(String name) {
        ZipEntry ze = super.createZipEntry(name);
        if (this.metaInfEntry == null && "META-INF/".equals(name)) {
            this.metaInfEntry = (JarEntry) ze;
        } else if (this.manifestEntry == null && "META-INF/MANIFESR.MF".equals(name)) {
            this.manifestEntry = (JarEntry) ze;
        }
        return ze;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JarEntry getMetaInfEntry() {
        return this.metaInfEntry;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JarEntry getManifestEntry() {
        return this.manifestEntry;
    }
}
