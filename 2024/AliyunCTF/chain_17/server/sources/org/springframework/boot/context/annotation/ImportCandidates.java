package org.springframework.boot.context.annotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.springframework.core.io.UrlResource;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/annotation/ImportCandidates.class */
public final class ImportCandidates implements Iterable<String> {
    private static final String LOCATION = "META-INF/spring/%s.imports";
    private static final String COMMENT_START = "#";
    private final List<String> candidates;

    private ImportCandidates(List<String> candidates) {
        Assert.notNull(candidates, "'candidates' must not be null");
        this.candidates = Collections.unmodifiableList(candidates);
    }

    @Override // java.lang.Iterable
    public Iterator<String> iterator() {
        return this.candidates.iterator();
    }

    public List<String> getCandidates() {
        return this.candidates;
    }

    public static ImportCandidates load(Class<?> annotation, ClassLoader classLoader) {
        Assert.notNull(annotation, "'annotation' must not be null");
        ClassLoader classLoaderToUse = decideClassloader(classLoader);
        String location = String.format(LOCATION, annotation.getName());
        Enumeration<URL> urls = findUrlsInClasspath(classLoaderToUse, location);
        List<String> importCandidates = new ArrayList<>();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            importCandidates.addAll(readCandidateConfigurations(url));
        }
        return new ImportCandidates(importCandidates);
    }

    private static ClassLoader decideClassloader(ClassLoader classLoader) {
        if (classLoader == null) {
            return ImportCandidates.class.getClassLoader();
        }
        return classLoader;
    }

    private static Enumeration<URL> findUrlsInClasspath(ClassLoader classLoader, String location) {
        try {
            return classLoader.getResources(location);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to load configurations from location [" + location + "]", ex);
        }
    }

    private static List<String> readCandidateConfigurations(URL url) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new UrlResource(url).getInputStream(), StandardCharsets.UTF_8));
            try {
                List<String> candidates = new ArrayList<>();
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        String line2 = stripComment(line).trim();
                        if (!line2.isEmpty()) {
                            candidates.add(line2);
                        }
                    } else {
                        reader.close();
                        return candidates;
                    }
                }
            } finally {
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load configurations from location [" + url + "]", ex);
        }
    }

    private static String stripComment(String line) {
        int commentStart = line.indexOf("#");
        if (commentStart == -1) {
            return line;
        }
        return line.substring(0, commentStart);
    }
}
