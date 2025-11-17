package org.jooq;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/FileComparator.class */
final class FileComparator implements java.util.Comparator<java.io.File> {
    public static final FileComparator INSTANCE = new FileComparator();

    FileComparator() {
    }

    @Override // java.util.Comparator
    public final int compare(java.io.File o1, java.io.File o2) {
        String s1 = o1 == null ? null : o1.getName();
        String s2 = o2 == null ? null : o2.getName();
        return FilenameComparator.INSTANCE.compare(s1, s2);
    }
}
