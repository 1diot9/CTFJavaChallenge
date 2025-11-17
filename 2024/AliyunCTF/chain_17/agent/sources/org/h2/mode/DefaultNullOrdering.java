package org.h2.mode;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/DefaultNullOrdering.class */
public enum DefaultNullOrdering {
    LOW(2, 4),
    HIGH(4, 2),
    FIRST(2, 2),
    LAST(4, 4);

    private static final DefaultNullOrdering[] VALUES = values();
    private final int defaultAscNulls;
    private final int defaultDescNulls;
    private final int nullAsc;
    private final int nullDesc;

    public static DefaultNullOrdering valueOf(int i) {
        return VALUES[i];
    }

    DefaultNullOrdering(int i, int i2) {
        this.defaultAscNulls = i;
        this.defaultDescNulls = i2;
        this.nullAsc = i == 2 ? -1 : 1;
        this.nullDesc = i2 == 2 ? -1 : 1;
    }

    public int addExplicitNullOrdering(int i) {
        if ((i & 6) == 0) {
            i |= (i & 1) == 0 ? this.defaultAscNulls : this.defaultDescNulls;
        }
        return i;
    }

    public int compareNull(boolean z, int i) {
        return (i & 2) != 0 ? z ? -1 : 1 : (i & 4) != 0 ? z ? 1 : -1 : (i & 1) == 0 ? z ? this.nullAsc : -this.nullAsc : z ? this.nullDesc : -this.nullDesc;
    }
}
