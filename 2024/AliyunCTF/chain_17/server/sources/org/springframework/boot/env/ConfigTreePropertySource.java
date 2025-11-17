package org.springframework.boot.env;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.boot.origin.OriginProvider;
import org.springframework.boot.origin.TextResourceOrigin;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/ConfigTreePropertySource.class */
public class ConfigTreePropertySource extends EnumerablePropertySource<Path> implements OriginLookup<String> {
    private static final int MAX_DEPTH = 100;
    private final Map<String, PropertyFile> propertyFiles;
    private final String[] names;
    private final Set<Option> options;

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/ConfigTreePropertySource$Option.class */
    public enum Option {
        ALWAYS_READ,
        USE_LOWERCASE_NAMES,
        AUTO_TRIM_TRAILING_NEW_LINE
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/ConfigTreePropertySource$Value.class */
    public interface Value extends CharSequence, InputStreamSource {
    }

    public ConfigTreePropertySource(String name, Path sourceDirectory) {
        this(name, sourceDirectory, EnumSet.noneOf(Option.class));
    }

    public ConfigTreePropertySource(String name, Path sourceDirectory, Option... options) {
        this(name, sourceDirectory, EnumSet.copyOf((Collection) Arrays.asList(options)));
    }

    private ConfigTreePropertySource(String name, Path sourceDirectory, Set<Option> options) {
        super(name, sourceDirectory);
        Assert.isTrue(Files.exists(sourceDirectory, new LinkOption[0]), (Supplier<String>) () -> {
            return "Directory '" + sourceDirectory + "' does not exist";
        });
        Assert.isTrue(Files.isDirectory(sourceDirectory, new LinkOption[0]), (Supplier<String>) () -> {
            return "File '" + sourceDirectory + "' is not a directory";
        });
        this.propertyFiles = PropertyFile.findAll(sourceDirectory, options);
        this.options = options;
        this.names = StringUtils.toStringArray(this.propertyFiles.keySet());
    }

    @Override // org.springframework.core.env.EnumerablePropertySource
    public String[] getPropertyNames() {
        return (String[]) this.names.clone();
    }

    @Override // org.springframework.core.env.PropertySource
    public Value getProperty(String name) {
        PropertyFile propertyFile = this.propertyFiles.get(name);
        if (propertyFile != null) {
            return propertyFile.getContent();
        }
        return null;
    }

    @Override // org.springframework.boot.origin.OriginLookup
    public Origin getOrigin(String name) {
        PropertyFile propertyFile = this.propertyFiles.get(name);
        if (propertyFile != null) {
            return propertyFile.getOrigin();
        }
        return null;
    }

    @Override // org.springframework.boot.origin.OriginLookup
    public boolean isImmutable() {
        return !this.options.contains(Option.ALWAYS_READ);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/ConfigTreePropertySource$PropertyFile.class */
    public static final class PropertyFile {
        private static final TextResourceOrigin.Location START_OF_FILE = new TextResourceOrigin.Location(0, 0);
        private final Path path;
        private final PathResource resource;
        private final Origin origin;
        private final PropertyFileContent cachedContent;
        private final boolean autoTrimTrailingNewLine;

        private PropertyFile(Path path, Set<Option> options) {
            this.path = path;
            this.resource = new PathResource(path);
            this.origin = new TextResourceOrigin(this.resource, START_OF_FILE);
            this.autoTrimTrailingNewLine = options.contains(Option.AUTO_TRIM_TRAILING_NEW_LINE);
            this.cachedContent = options.contains(Option.ALWAYS_READ) ? null : new PropertyFileContent(path, this.resource, this.origin, true, this.autoTrimTrailingNewLine);
        }

        PropertyFileContent getContent() {
            if (this.cachedContent != null) {
                return this.cachedContent;
            }
            return new PropertyFileContent(this.path, this.resource, this.origin, false, this.autoTrimTrailingNewLine);
        }

        Origin getOrigin() {
            return this.origin;
        }

        static Map<String, PropertyFile> findAll(Path sourceDirectory, Set<Option> options) {
            try {
                Map<String, PropertyFile> propertyFiles = new TreeMap<>();
                Stream<Path> pathStream = Files.find(sourceDirectory, 100, PropertyFile::isPropertyFile, FileVisitOption.FOLLOW_LINKS);
                try {
                    pathStream.forEach(path -> {
                        String name = getName(sourceDirectory.relativize(path));
                        if (StringUtils.hasText(name)) {
                            if (options.contains(Option.USE_LOWERCASE_NAMES)) {
                                name = name.toLowerCase();
                            }
                            propertyFiles.put(name, new PropertyFile(path, options));
                        }
                    });
                    if (pathStream != null) {
                        pathStream.close();
                    }
                    return Collections.unmodifiableMap(propertyFiles);
                } finally {
                }
            } catch (IOException ex) {
                throw new IllegalStateException("Unable to find files in '" + sourceDirectory + "'", ex);
            }
        }

        private static boolean isPropertyFile(Path path, BasicFileAttributes attributes) {
            return !hasHiddenPathElement(path) && (attributes.isRegularFile() || attributes.isSymbolicLink());
        }

        private static boolean hasHiddenPathElement(Path path) {
            for (Path element : path) {
                if (element.toString().startsWith(CallerDataConverter.DEFAULT_RANGE_DELIMITER)) {
                    return true;
                }
            }
            return false;
        }

        private static String getName(Path relativePath) {
            int nameCount = relativePath.getNameCount();
            if (nameCount == 1) {
                return relativePath.toString();
            }
            StringBuilder name = new StringBuilder();
            int i = 0;
            while (i < nameCount) {
                name.append(i != 0 ? "." : "");
                name.append(relativePath.getName(i));
                i++;
            }
            return name.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/ConfigTreePropertySource$PropertyFileContent.class */
    public static final class PropertyFileContent implements Value, OriginProvider {
        private final Path path;
        private final Lock resourceLock = new ReentrantLock();
        private final Resource resource;
        private final Origin origin;
        private final boolean cacheContent;
        private final boolean autoTrimTrailingNewLine;
        private volatile byte[] content;

        private PropertyFileContent(Path path, Resource resource, Origin origin, boolean cacheContent, boolean autoTrimTrailingNewLine) {
            this.path = path;
            this.resource = resource;
            this.origin = origin;
            this.cacheContent = cacheContent;
            this.autoTrimTrailingNewLine = autoTrimTrailingNewLine;
        }

        @Override // org.springframework.boot.origin.OriginProvider
        public Origin getOrigin() {
            return this.origin;
        }

        @Override // java.lang.CharSequence
        public int length() {
            return toString().length();
        }

        @Override // java.lang.CharSequence
        public char charAt(int index) {
            return toString().charAt(index);
        }

        @Override // java.lang.CharSequence
        public CharSequence subSequence(int start, int end) {
            return toString().subSequence(start, end);
        }

        @Override // java.lang.CharSequence
        public String toString() {
            String string = new String(getBytes());
            if (this.autoTrimTrailingNewLine) {
                string = autoTrimTrailingNewLine(string);
            }
            return string;
        }

        private String autoTrimTrailingNewLine(String string) {
            if (!string.endsWith("\n")) {
                return string;
            }
            int numberOfLines = 0;
            for (int i = 0; i < string.length(); i++) {
                char ch2 = string.charAt(i);
                if (ch2 == '\n') {
                    numberOfLines++;
                }
            }
            if (numberOfLines > 1) {
                return string;
            }
            return string.endsWith("\r\n") ? string.substring(0, string.length() - 2) : string.substring(0, string.length() - 1);
        }

        @Override // org.springframework.core.io.InputStreamSource
        public InputStream getInputStream() throws IOException {
            if (!this.cacheContent) {
                assertStillExists();
                return this.resource.getInputStream();
            }
            return new ByteArrayInputStream(getBytes());
        }

        private byte[] getBytes() {
            try {
                if (!this.cacheContent) {
                    assertStillExists();
                    return FileCopyUtils.copyToByteArray(this.resource.getInputStream());
                }
                if (this.content == null) {
                    assertStillExists();
                    this.resourceLock.lock();
                    try {
                        if (this.content == null) {
                            this.content = FileCopyUtils.copyToByteArray(this.resource.getInputStream());
                        }
                        this.resourceLock.unlock();
                    } catch (Throwable th) {
                        this.resourceLock.unlock();
                        throw th;
                    }
                }
                return this.content;
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

        private void assertStillExists() {
            Assert.state(Files.exists(this.path, new LinkOption[0]), (Supplier<String>) () -> {
                return "The property file '" + this.path + "' no longer exists";
            });
        }
    }
}
