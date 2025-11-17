package org.springframework.boot.loader.net.protocol.jar;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;

/* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/jar/JarUrl.class */
public final class JarUrl {
    private JarUrl() {
    }

    public static URL create(File file) {
        return create(file, (String) null);
    }

    public static URL create(File file, JarEntry nestedEntry) {
        return create(file, nestedEntry != null ? nestedEntry.getName() : null);
    }

    public static URL create(File file, String nestedEntryName) {
        return create(file, nestedEntryName, null);
    }

    public static URL create(File file, String nestedEntryName, String path) {
        try {
            return new URL((URL) null, "jar:" + getJarReference(file, nestedEntryName) + "!/" + (path != null ? path : ""), Handler.INSTANCE);
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("Unable to create JarFileArchive URL", ex);
        }
    }

    private static String getJarReference(File file, String nestedEntryName) {
        String jarFilePath = file.toURI().getRawPath().replace("!", "%21");
        return nestedEntryName != null ? "nested:" + jarFilePath + "/!" + nestedEntryName : "file:" + jarFilePath;
    }
}
