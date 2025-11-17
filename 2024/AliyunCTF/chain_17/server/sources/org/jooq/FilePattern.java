package org.jooq;

import ch.qos.logback.core.CoreConstants;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.jetbrains.annotations.ApiStatus;
import org.jooq.tools.JooqLogger;

@ApiStatus.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/FilePattern.class */
public final class FilePattern {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) FilePattern.class);
    private final Sort sort;
    private final java.util.Comparator<java.io.File> comparator;
    private final java.io.File basedir;
    private final String pattern;
    private final String encoding;
    private final Pattern regexForMatches;
    private final Pattern regexForLoad;

    public FilePattern() {
        this((Sort) null, (java.io.File) null, SecurityConstraint.ROLE_ALL_AUTHENTICATED_USERS, "UTF-8");
    }

    private FilePattern(Sort sort, java.io.File basedir, String pattern, String encoding) {
        this.sort = sort;
        this.comparator = fileComparator(sort);
        this.basedir = basedir == null ? new java.io.File(".") : basedir;
        this.pattern = pattern;
        this.encoding = encoding;
        this.regexForMatches = Pattern.compile("^" + regex() + "$");
        this.regexForLoad = Pattern.compile("^.*?" + regex() + "$");
    }

    public final Sort sort() {
        return this.sort;
    }

    public final FilePattern sort(Sort newSort) {
        return new FilePattern(newSort, this.basedir, this.pattern, this.encoding);
    }

    public final java.io.File basedir() {
        return this.basedir;
    }

    public final FilePattern basedir(java.io.File newBasedir) {
        return new FilePattern(this.sort, newBasedir, this.pattern, this.encoding);
    }

    public final String pattern() {
        return this.pattern;
    }

    public final FilePattern pattern(String newPattern) {
        return new FilePattern(this.sort, this.basedir, newPattern, this.encoding);
    }

    public final String encoding() {
        return this.encoding;
    }

    public final FilePattern encoding(String newEncoding) {
        return new FilePattern(this.sort, this.basedir, this.pattern, newEncoding);
    }

    private static final java.util.Comparator<java.io.File> fileComparator(Sort sort) {
        if (sort == null) {
            sort = Sort.SEMANTIC;
        }
        switch (sort) {
            case SEMANTIC:
                return FileComparator.INSTANCE;
            case ALPHANUMERIC:
                return java.util.Comparator.naturalOrder();
            case FLYWAY:
                return FlywayFileComparator.INSTANCE;
            case NONE:
                return null;
            default:
                throw new IllegalArgumentException("Unsupported sort: " + String.valueOf(sort));
        }
    }

    public final boolean matches(String path) {
        return this.regexForMatches.matcher(path.replace("\\", "/")).matches();
    }

    public final List<Source> collect() {
        List<Source> list = new ArrayList<>();
        Objects.requireNonNull(list);
        load((v1) -> {
            r1.add(v1);
        });
        return list;
    }

    public final void load(Consumer<Source> loader) {
        boolean loaded;
        URL url = null;
        try {
            url = FilePattern.class.getResource(this.pattern);
        } catch (Exception e) {
        }
        try {
            if (url != null) {
                log.info("Reading from classpath: " + this.pattern);
                InputStream is = FilePattern.class.getResourceAsStream(this.pattern);
                try {
                    loader.accept(Source.of(Source.of(is).readString()));
                    loaded = true;
                    if (is != null) {
                        is.close();
                    }
                } finally {
                }
            } else {
                java.io.File file = new java.io.File(this.pattern);
                if (file.exists()) {
                    load(file, this.comparator, null, loader);
                    loaded = true;
                } else if (!this.pattern.contains("*") && !this.pattern.contains(CoreConstants.NA)) {
                    load(new java.io.File(this.basedir, this.pattern), this.comparator, null, loader);
                    loaded = true;
                } else {
                    String prefix = this.pattern.replaceAll("[^\\/]*?[*?].*", "");
                    java.io.File file2 = new java.io.File(prefix);
                    if (!file2.isAbsolute()) {
                        file2 = new java.io.File(this.basedir, prefix).getAbsoluteFile();
                    }
                    load(file2, this.comparator, this.regexForLoad, loader);
                    loaded = true;
                }
            }
            if (!loaded) {
                log.error("Could not find source(s) : " + this.pattern);
            }
        } catch (IOException e2) {
            throw new org.jooq.exception.IOException("Error while loading pattern", e2);
        }
    }

    private String regex() {
        return this.pattern.replace("\\", "/").replace(".", "\\.").replace(CoreConstants.NA, "[^/]").replace(SecurityConstraint.ROLE_ALL_AUTHENTICATED_USERS, ".+?").replace("*", "[^/]*");
    }

    private final void load(java.io.File file, java.util.Comparator<java.io.File> fileComparator, Pattern regex, Consumer<Source> loader) throws IOException {
        if (file.isFile()) {
            if (regex == null || regex.matcher(file.getCanonicalPath().replace("\\", "/")).matches()) {
                log.info("Reading from: " + String.valueOf(file) + " [*]");
                load0(file, loader);
                return;
            }
            return;
        }
        if (file.isDirectory()) {
            log.info("Reading from: " + String.valueOf(file));
            java.io.File[] files = file.listFiles();
            if (files != null) {
                if (fileComparator != null) {
                    Arrays.sort(files, fileComparator);
                }
                for (java.io.File f : files) {
                    load(f, this.comparator, regex, loader);
                }
            }
        }
    }

    private final void load0(java.io.File file, Consumer<Source> loader) {
        try {
            loader.accept(Source.of(file, this.encoding));
        } catch (RuntimeException e) {
            log.error("Error while loading file: " + String.valueOf(file));
            throw e;
        }
    }

    public String toString() {
        return this.pattern;
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/FilePattern$Sort.class */
    public enum Sort {
        SEMANTIC,
        ALPHANUMERIC,
        FLYWAY,
        NONE;

        public static final Sort of(String sort) {
            if ("alphanumeric".equals(sort)) {
                return ALPHANUMERIC;
            }
            if ("none".equals(sort)) {
                return NONE;
            }
            if ("flyway".equals(sort)) {
                return FLYWAY;
            }
            return SEMANTIC;
        }
    }
}
