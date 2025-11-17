package org.springframework.boot.loader.net.protocol.nested;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.loader.net.util.UrlDecoder;

/* loaded from: server.jar:org/springframework/boot/loader/net/protocol/nested/NestedLocation.class */
public final class NestedLocation extends Record {
    private final Path path;
    private final String nestedEntryName;
    private static final Map<String, NestedLocation> cache = new ConcurrentHashMap();

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, NestedLocation.class), NestedLocation.class, "path;nestedEntryName", "FIELD:Lorg/springframework/boot/loader/net/protocol/nested/NestedLocation;->path:Ljava/nio/file/Path;", "FIELD:Lorg/springframework/boot/loader/net/protocol/nested/NestedLocation;->nestedEntryName:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, NestedLocation.class), NestedLocation.class, "path;nestedEntryName", "FIELD:Lorg/springframework/boot/loader/net/protocol/nested/NestedLocation;->path:Ljava/nio/file/Path;", "FIELD:Lorg/springframework/boot/loader/net/protocol/nested/NestedLocation;->nestedEntryName:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, NestedLocation.class, Object.class), NestedLocation.class, "path;nestedEntryName", "FIELD:Lorg/springframework/boot/loader/net/protocol/nested/NestedLocation;->path:Ljava/nio/file/Path;", "FIELD:Lorg/springframework/boot/loader/net/protocol/nested/NestedLocation;->nestedEntryName:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public Path path() {
        return this.path;
    }

    public String nestedEntryName() {
        return this.nestedEntryName;
    }

    public NestedLocation(Path path, String nestedEntryName) {
        if (path == null) {
            throw new IllegalArgumentException("'path' must not be null");
        }
        this.path = path;
        this.nestedEntryName = (nestedEntryName == null || nestedEntryName.isEmpty()) ? null : nestedEntryName;
    }

    public static NestedLocation fromUrl(URL url) {
        if (url == null || !"nested".equalsIgnoreCase(url.getProtocol())) {
            throw new IllegalArgumentException("'url' must not be null and must use 'nested' protocol");
        }
        return parse(UrlDecoder.decode(url.toString().substring(7)));
    }

    public static NestedLocation fromUri(URI uri) {
        if (uri == null || !"nested".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("'uri' must not be null and must use 'nested' scheme");
        }
        return parse(uri.getSchemeSpecificPart());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static NestedLocation parse(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("'path' must not be empty");
        }
        int index = path.lastIndexOf("/!");
        return cache.computeIfAbsent(path, l -> {
            return create(index, l);
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static NestedLocation create(int index, String location) {
        String locationPath = index != -1 ? location.substring(0, index) : location;
        if (isWindows() && !isUncPath(location)) {
            while (locationPath.startsWith("/")) {
                locationPath = locationPath.substring(1, locationPath.length());
            }
        }
        String nestedEntryName = index != -1 ? location.substring(index + 2) : null;
        return new NestedLocation(!locationPath.isEmpty() ? Path.of(locationPath, new String[0]) : null, nestedEntryName);
    }

    private static boolean isWindows() {
        return File.separatorChar == '\\';
    }

    private static boolean isUncPath(String input) {
        return !input.contains(":");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void clearCache() {
        cache.clear();
    }
}
