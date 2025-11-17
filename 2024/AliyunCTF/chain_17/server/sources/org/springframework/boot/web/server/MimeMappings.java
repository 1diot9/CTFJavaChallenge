package org.springframework.boot.web.server;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/MimeMappings.class */
public class MimeMappings implements Iterable<Mapping> {
    public static final MimeMappings DEFAULT = new DefaultMimeMappings();
    private final Map<String, Mapping> map;

    public MimeMappings() {
        this.map = new LinkedHashMap();
    }

    public MimeMappings(MimeMappings mappings) {
        this(mappings, true);
    }

    public MimeMappings(Map<String, String> mappings) {
        Assert.notNull(mappings, "Mappings must not be null");
        this.map = new LinkedHashMap();
        mappings.forEach(this::add);
    }

    MimeMappings(MimeMappings mappings, boolean mutable) {
        Assert.notNull(mappings, "Mappings must not be null");
        this.map = mutable ? new LinkedHashMap<>(mappings.map) : Collections.unmodifiableMap(mappings.map);
    }

    public String add(String extension, String mimeType) {
        Assert.notNull(extension, "Extension must not be null");
        Assert.notNull(mimeType, "MimeType must not be null");
        Mapping previous = this.map.put(extension.toLowerCase(Locale.ENGLISH), new Mapping(extension, mimeType));
        if (previous != null) {
            return previous.getMimeType();
        }
        return null;
    }

    public String remove(String extension) {
        Assert.notNull(extension, "Extension must not be null");
        Mapping previous = this.map.remove(extension.toLowerCase(Locale.ENGLISH));
        if (previous != null) {
            return previous.getMimeType();
        }
        return null;
    }

    public String get(String extension) {
        Assert.notNull(extension, "Extension must not be null");
        Mapping mapping = this.map.get(extension.toLowerCase(Locale.ENGLISH));
        if (mapping != null) {
            return mapping.getMimeType();
        }
        return null;
    }

    public Collection<Mapping> getAll() {
        return this.map.values();
    }

    @Override // java.lang.Iterable
    public final Iterator<Mapping> iterator() {
        return getAll().iterator();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof MimeMappings) {
            MimeMappings other = (MimeMappings) obj;
            return getMap().equals(other.map);
        }
        return false;
    }

    public int hashCode() {
        return getMap().hashCode();
    }

    Map<String, Mapping> getMap() {
        return this.map;
    }

    public static MimeMappings unmodifiableMappings(MimeMappings mappings) {
        Assert.notNull(mappings, "Mappings must not be null");
        return new MimeMappings(mappings, false);
    }

    public static MimeMappings lazyCopy(MimeMappings mappings) {
        Assert.notNull(mappings, "Mappings must not be null");
        return new LazyMimeMappingsCopy(mappings);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/MimeMappings$Mapping.class */
    public static final class Mapping {
        private final String extension;
        private final String mimeType;

        public Mapping(String extension, String mimeType) {
            Assert.notNull(extension, "Extension must not be null");
            Assert.notNull(mimeType, "MimeType must not be null");
            this.extension = extension;
            this.mimeType = mimeType;
        }

        public String getExtension() {
            return this.extension;
        }

        public String getMimeType() {
            return this.mimeType;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Mapping)) {
                return false;
            }
            Mapping other = (Mapping) obj;
            return this.extension.equals(other.extension) && this.mimeType.equals(other.mimeType);
        }

        public int hashCode() {
            return this.extension.hashCode();
        }

        public String toString() {
            return "Mapping [extension=" + this.extension + ", mimeType=" + this.mimeType + "]";
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/MimeMappings$DefaultMimeMappings.class */
    static final class DefaultMimeMappings extends MimeMappings {
        static final String MIME_MAPPINGS_PROPERTIES = "mime-mappings.properties";
        private static final MimeMappings COMMON;
        private volatile Map<String, Mapping> loaded;

        static {
            MimeMappings mappings = new MimeMappings();
            mappings.add("avi", "video/x-msvideo");
            mappings.add("bin", "application/octet-stream");
            mappings.add("body", "text/html");
            mappings.add("class", "application/java");
            mappings.add("css", "text/css");
            mappings.add("dtd", "application/xml-dtd");
            mappings.add("gif", "image/gif");
            mappings.add("gtar", "application/x-gtar");
            mappings.add("gz", "application/x-gzip");
            mappings.add("htm", "text/html");
            mappings.add("html", "text/html");
            mappings.add(ResourceUtils.URL_PROTOCOL_JAR, "application/java-archive");
            mappings.add("java", "text/x-java-source");
            mappings.add("jnlp", "application/x-java-jnlp-file");
            mappings.add("jpe", "image/jpeg");
            mappings.add("jpeg", "image/jpeg");
            mappings.add("jpg", "image/jpeg");
            mappings.add("js", "text/javascript");
            mappings.add("json", "application/json");
            mappings.add("otf", "font/otf");
            mappings.add("pdf", MediaType.APPLICATION_PDF_VALUE);
            mappings.add("png", "image/png");
            mappings.add("ps", "application/postscript");
            mappings.add("tar", "application/x-tar");
            mappings.add("tif", "image/tiff");
            mappings.add("tiff", "image/tiff");
            mappings.add("ttf", "font/ttf");
            mappings.add("txt", "text/plain");
            mappings.add("xht", MediaType.APPLICATION_XHTML_XML_VALUE);
            mappings.add("xhtml", MediaType.APPLICATION_XHTML_XML_VALUE);
            mappings.add("xls", "application/vnd.ms-excel");
            mappings.add("xml", "application/xml");
            mappings.add("xsl", "application/xml");
            mappings.add("xslt", "application/xslt+xml");
            mappings.add("wasm", "application/wasm");
            mappings.add(ResourceUtils.URL_PROTOCOL_ZIP, "application/zip");
            COMMON = unmodifiableMappings(mappings);
        }

        DefaultMimeMappings() {
            super(new MimeMappings(), false);
        }

        @Override // org.springframework.boot.web.server.MimeMappings
        public Collection<Mapping> getAll() {
            return load().values();
        }

        @Override // org.springframework.boot.web.server.MimeMappings
        public String get(String extension) {
            Assert.notNull(extension, "Extension must not be null");
            String extension2 = extension.toLowerCase(Locale.ENGLISH);
            Map<String, Mapping> loaded = this.loaded;
            if (loaded != null) {
                return get(loaded, extension2);
            }
            String commonMimeType = COMMON.get(extension2);
            if (commonMimeType != null) {
                return commonMimeType;
            }
            return get(load(), extension2);
        }

        private String get(Map<String, Mapping> mappings, String extension) {
            Mapping mapping = mappings.get(extension);
            if (mapping != null) {
                return mapping.getMimeType();
            }
            return null;
        }

        @Override // org.springframework.boot.web.server.MimeMappings
        Map<String, Mapping> getMap() {
            return load();
        }

        private Map<String, Mapping> load() {
            Map<String, Mapping> loaded = this.loaded;
            if (loaded != null) {
                return loaded;
            }
            try {
                Map<String, Mapping> loaded2 = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : PropertiesLoaderUtils.loadProperties(new ClassPathResource(MIME_MAPPINGS_PROPERTIES, getClass())).entrySet()) {
                    loaded2.put((String) entry.getKey(), new Mapping((String) entry.getKey(), (String) entry.getValue()));
                }
                Map<String, Mapping> loaded3 = Collections.unmodifiableMap(loaded2);
                this.loaded = loaded3;
                return loaded3;
            } catch (IOException ex) {
                throw new IllegalArgumentException("Unable to load the default MIME types", ex);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/MimeMappings$LazyMimeMappingsCopy.class */
    static final class LazyMimeMappingsCopy extends MimeMappings {
        private final MimeMappings source;
        private final AtomicBoolean copied = new AtomicBoolean();

        LazyMimeMappingsCopy(MimeMappings source) {
            this.source = source;
        }

        @Override // org.springframework.boot.web.server.MimeMappings
        public String add(String extension, String mimeType) {
            copyIfNecessary();
            return super.add(extension, mimeType);
        }

        @Override // org.springframework.boot.web.server.MimeMappings
        public String remove(String extension) {
            copyIfNecessary();
            return super.remove(extension);
        }

        private void copyIfNecessary() {
            if (this.copied.compareAndSet(false, true)) {
                this.source.forEach(mapping -> {
                    add(mapping.getExtension(), mapping.getMimeType());
                });
            }
        }

        @Override // org.springframework.boot.web.server.MimeMappings
        public String get(String extension) {
            return !this.copied.get() ? this.source.get(extension) : super.get(extension);
        }

        @Override // org.springframework.boot.web.server.MimeMappings
        public Collection<Mapping> getAll() {
            return !this.copied.get() ? this.source.getAll() : super.getAll();
        }

        @Override // org.springframework.boot.web.server.MimeMappings
        Map<String, Mapping> getMap() {
            return !this.copied.get() ? this.source.getMap() : super.getMap();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/MimeMappings$MimeMappingsRuntimeHints.class */
    static class MimeMappingsRuntimeHints implements RuntimeHintsRegistrar {
        MimeMappingsRuntimeHints() {
        }

        @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources().registerPattern("org/springframework/boot/web/server/mime-mappings.properties");
        }
    }
}
