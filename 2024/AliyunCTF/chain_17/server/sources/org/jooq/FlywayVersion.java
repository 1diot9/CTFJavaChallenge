package org.jooq;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/FlywayVersion.class */
final class FlywayVersion implements Comparable<FlywayVersion> {
    public static final FlywayVersion EMPTY = new FlywayVersion(null);
    private static final Pattern SPLIT_REGEX = Pattern.compile("\\.(?=\\d)");
    private final List<BigInteger> versionParts;

    public static FlywayVersion fromVersion(String version) {
        return StringUtils.isEmpty(version) ? EMPTY : new FlywayVersion(version);
    }

    private FlywayVersion(String version) {
        if (!StringUtils.isEmpty(version)) {
            String normalizedVersion = version.replace('_', '.');
            this.versionParts = tokenize(normalizedVersion);
        } else {
            this.versionParts = new ArrayList();
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FlywayVersion version1 = (FlywayVersion) o;
        return compareTo(version1) == 0;
    }

    public int hashCode() {
        if (this.versionParts == null) {
            return 0;
        }
        return this.versionParts.hashCode();
    }

    @Override // java.lang.Comparable
    public int compareTo(FlywayVersion o) {
        if (o == null) {
            return 1;
        }
        if (this == EMPTY) {
            return o == EMPTY ? 0 : Integer.MIN_VALUE;
        }
        if (o == EMPTY) {
            return Integer.MAX_VALUE;
        }
        List<BigInteger> parts1 = this.versionParts;
        List<BigInteger> parts2 = o.versionParts;
        int largestNumberOfParts = Math.max(parts1.size(), parts2.size());
        for (int i = 0; i < largestNumberOfParts; i++) {
            int compared = getOrZero(parts1, i).compareTo(getOrZero(parts2, i));
            if (compared != 0) {
                return compared;
            }
        }
        return 0;
    }

    private BigInteger getOrZero(List<BigInteger> elements, int i) {
        return i < elements.size() ? elements.get(i) : BigInteger.ZERO;
    }

    private List<BigInteger> tokenize(String versionStr) {
        List<BigInteger> parts = new ArrayList<>();
        for (String part : SPLIT_REGEX.split(versionStr)) {
            parts.add(new BigInteger(part));
        }
        for (int i = parts.size() - 1; i > 0 && parts.get(i).equals(BigInteger.ZERO); i--) {
            parts.remove(i);
        }
        return parts;
    }
}
