package org.springframework.boot.jarmode.layertools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-jarmode-layertools-3.2.2.jar:org/springframework/boot/jarmode/layertools/IndexedLayers.class */
public class IndexedLayers implements Layers {
    private final Map<String, List<String>> layers = new LinkedHashMap();

    IndexedLayers(String indexFile) {
        String[] lines = (String[]) Arrays.stream(indexFile.split("\n")).map(line -> {
            return line.replace("\r", "");
        }).filter(StringUtils::hasText).toArray(x$0 -> {
            return new String[x$0];
        });
        List<String> contents = null;
        for (String line2 : lines) {
            if (line2.startsWith("- ")) {
                contents = new ArrayList<>();
                this.layers.put(line2.substring(3, line2.length() - 2), contents);
            } else if (line2.startsWith("  - ")) {
                contents.add(line2.substring(5, line2.length() - 1));
            } else {
                throw new IllegalStateException("Layer index file is malformed");
            }
        }
        Assert.state(!this.layers.isEmpty(), "Empty layer index file loaded");
    }

    @Override // org.springframework.boot.jarmode.layertools.Layers, java.lang.Iterable
    public Iterator<String> iterator() {
        return this.layers.keySet().iterator();
    }

    @Override // org.springframework.boot.jarmode.layertools.Layers
    public String getLayer(ZipEntry entry) {
        return getLayer(entry.getName());
    }

    private String getLayer(String name) {
        for (Map.Entry<String, List<String>> entry : this.layers.entrySet()) {
            for (String candidate : entry.getValue()) {
                if (candidate.equals(name) || (candidate.endsWith("/") && name.startsWith(candidate))) {
                    return entry.getKey();
                }
            }
        }
        throw new IllegalStateException("No layer defined in index for file '" + name + "'");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static IndexedLayers get(Context context) {
        try {
            try {
                JarFile jarFile = new JarFile(context.getArchiveFile());
                try {
                    Manifest manifest = jarFile.getManifest();
                    String location = manifest.getMainAttributes().getValue("Spring-Boot-Layers-Index");
                    ZipEntry entry = location != null ? jarFile.getEntry(location) : null;
                    if (entry != null) {
                        String indexFile = StreamUtils.copyToString(jarFile.getInputStream(entry), StandardCharsets.UTF_8);
                        IndexedLayers indexedLayers = new IndexedLayers(indexFile);
                        jarFile.close();
                        return indexedLayers;
                    }
                    jarFile.close();
                    return null;
                } catch (Throwable th) {
                    try {
                        jarFile.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        } catch (FileNotFoundException | NoSuchFileException e) {
            return null;
        }
    }
}
