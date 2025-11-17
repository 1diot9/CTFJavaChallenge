package org.jooq;

import java.math.BigInteger;
import java.util.regex.Pattern;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/FilenameComparator.class */
final class FilenameComparator implements java.util.Comparator<String> {
    private static final Pattern NUMBERS = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
    public static final FilenameComparator INSTANCE = new FilenameComparator();

    FilenameComparator() {
    }

    @Override // java.util.Comparator
    public final int compare(String o1, String o2) {
        if (o1 == null || o2 == null) {
            if (o1 == null) {
                return o2 == null ? 0 : -1;
            }
            return 1;
        }
        String[] split1 = NUMBERS.split(o1);
        String[] split2 = NUMBERS.split(o2);
        for (int i = 0; i < Math.min(split1.length, split2.length); i++) {
            char c1 = split1[i].charAt(0);
            char c2 = split2[i].charAt(0);
            int cmp = 0;
            if (c1 >= '0' && c1 <= '9' && c2 >= '0' && c2 <= '9') {
                cmp = new BigInteger(split1[i]).compareTo(new BigInteger(split2[i]));
            }
            if (cmp == 0) {
                cmp = split1[i].compareTo(split2[i]);
            }
            if (cmp != 0) {
                return cmp;
            }
        }
        return split1.length - split2.length;
    }
}
