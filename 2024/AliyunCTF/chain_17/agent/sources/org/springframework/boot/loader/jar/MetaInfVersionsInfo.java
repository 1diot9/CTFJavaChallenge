package org.springframework.boot.loader.jar;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.IntFunction;
import org.springframework.boot.loader.zip.ZipContent;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:org/springframework/boot/loader/jar/MetaInfVersionsInfo.class */
public final class MetaInfVersionsInfo {
    static final MetaInfVersionsInfo NONE = new MetaInfVersionsInfo(Collections.emptySet());
    private static final String META_INF_VERSIONS = "META-INF/versions/";
    private final int[] versions;
    private final String[] directories;

    private MetaInfVersionsInfo(Set<Integer> versions) {
        this.versions = versions.stream().mapToInt((v0) -> {
            return v0.intValue();
        }).toArray();
        this.directories = (String[]) versions.stream().map(version -> {
            return "META-INF/versions/" + version + "/";
        }).toArray(x$0 -> {
            return new String[x$0];
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int[] versions() {
        return this.versions;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String[] directories() {
        return this.directories;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MetaInfVersionsInfo get(ZipContent zipContent) {
        int size = zipContent.size();
        Objects.requireNonNull(zipContent);
        return get(size, zipContent::getEntry);
    }

    static MetaInfVersionsInfo get(int size, IntFunction<ZipContent.Entry> entries) {
        Set<Integer> versions = new TreeSet<>();
        for (int i = 0; i < size; i++) {
            ZipContent.Entry contentEntry = entries.apply(i);
            if (contentEntry.hasNameStartingWith(META_INF_VERSIONS) && !contentEntry.isDirectory()) {
                String name = contentEntry.getName();
                int slash = name.indexOf(47, META_INF_VERSIONS.length());
                String version = name.substring(META_INF_VERSIONS.length(), slash);
                try {
                    int versionNumber = Integer.parseInt(version);
                    if (versionNumber >= NestedJarFile.BASE_VERSION) {
                        versions.add(Integer.valueOf(versionNumber));
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return !versions.isEmpty() ? new MetaInfVersionsInfo(versions) : NONE;
    }
}
