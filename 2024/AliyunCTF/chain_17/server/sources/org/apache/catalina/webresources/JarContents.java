package org.apache.catalina.webresources;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/JarContents.class */
public final class JarContents {
    private final BitSet bits1;
    private final BitSet bits2;
    private static final int HASH_PRIME_1 = 31;
    private static final int HASH_PRIME_2 = 17;
    private static final int TABLE_SIZE = 2048;

    public JarContents(JarFile jar) {
        Enumeration<JarEntry> entries = jar.entries();
        this.bits1 = new BitSet(2048);
        this.bits2 = new BitSet(2048);
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            int startPos = 0;
            boolean precedingSlash = name.charAt(0) == '/';
            startPos = precedingSlash ? 1 : startPos;
            int pathHash1 = hashcode(name, startPos, 31);
            int pathHash2 = hashcode(name, startPos, 17);
            this.bits1.set(pathHash1 % 2048);
            this.bits2.set(pathHash2 % 2048);
            if (entry.isDirectory()) {
                int pathHash12 = hashcode(name, startPos, name.length() - 1, 31);
                int pathHash22 = hashcode(name, startPos, name.length() - 1, 17);
                this.bits1.set(pathHash12 % 2048);
                this.bits2.set(pathHash22 % 2048);
            }
        }
    }

    private int hashcode(String content, int startPos, int hashPrime) {
        return hashcode(content, startPos, content.length(), hashPrime);
    }

    private int hashcode(String content, int startPos, int endPos, int hashPrime) {
        int h = hashPrime / 2;
        for (int i = startPos; i < endPos; i++) {
            h = (hashPrime * h) + content.charAt(i);
        }
        if (h < 0) {
            h *= -1;
        }
        return h;
    }

    public boolean mightContainResource(String path, String webappRoot) {
        int startPos = 0;
        if (path.startsWith(webappRoot)) {
            startPos = webappRoot.length();
        }
        if (path.charAt(startPos) == '/') {
            startPos++;
        }
        return this.bits1.get(hashcode(path, startPos, 31) % 2048) && this.bits2.get(hashcode(path, startPos, 17) % 2048);
    }
}
