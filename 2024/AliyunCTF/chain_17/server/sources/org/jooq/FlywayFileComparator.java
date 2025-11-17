package org.jooq;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/FlywayFileComparator.class */
final class FlywayFileComparator implements java.util.Comparator<java.io.File> {
    public static final FlywayFileComparator INSTANCE = new FlywayFileComparator();

    FlywayFileComparator() {
    }

    @Override // java.util.Comparator
    public final int compare(java.io.File o1, java.io.File o2) {
        String s1 = o1 == null ? null : o1.getName();
        String s2 = o2 == null ? null : o2.getName();
        if (s1 != null && s2 != null) {
            int i1 = s1.indexOf("__");
            int i2 = s2.indexOf("__");
            if (i1 > 1 && i2 > 1) {
                FlywayVersion v1 = FlywayVersion.fromVersion(s1.substring(1, i1));
                FlywayVersion v2 = FlywayVersion.fromVersion(s2.substring(1, i2));
                return v1.compareTo(v2);
            }
            if (i1 > 1) {
                return -1;
            }
            if (i2 > 1) {
                return 1;
            }
        }
        return FileComparator.INSTANCE.compare(o1, o2);
    }
}
