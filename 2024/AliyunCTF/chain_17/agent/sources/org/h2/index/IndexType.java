package org.h2.index;

import java.util.Objects;
import org.h2.engine.NullsDistinct;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/IndexType.class */
public class IndexType {
    private boolean primaryKey;
    private boolean persistent;
    private boolean hash;
    private boolean scan;
    private boolean spatial;
    private boolean belongsToConstraint;
    private NullsDistinct nullsDistinct;

    public static IndexType createPrimaryKey(boolean z, boolean z2) {
        IndexType indexType = new IndexType();
        indexType.primaryKey = true;
        indexType.persistent = z;
        indexType.hash = z2;
        return indexType;
    }

    public static IndexType createUnique(boolean z, boolean z2, int i, NullsDistinct nullsDistinct) {
        IndexType indexType = new IndexType();
        indexType.persistent = z;
        indexType.hash = z2;
        indexType.nullsDistinct = (i == 1 && nullsDistinct == NullsDistinct.ALL_DISTINCT) ? NullsDistinct.DISTINCT : (NullsDistinct) Objects.requireNonNull(nullsDistinct);
        return indexType;
    }

    public static IndexType createNonUnique(boolean z) {
        return createNonUnique(z, false, false);
    }

    public static IndexType createNonUnique(boolean z, boolean z2, boolean z3) {
        IndexType indexType = new IndexType();
        indexType.persistent = z;
        indexType.hash = z2;
        indexType.spatial = z3;
        return indexType;
    }

    public static IndexType createScan(boolean z) {
        IndexType indexType = new IndexType();
        indexType.persistent = z;
        indexType.scan = true;
        return indexType;
    }

    public void setBelongsToConstraint(boolean z) {
        this.belongsToConstraint = z;
    }

    public boolean getBelongsToConstraint() {
        return this.belongsToConstraint;
    }

    public boolean isHash() {
        return this.hash;
    }

    public boolean isSpatial() {
        return this.spatial;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    public boolean isPrimaryKey() {
        return this.primaryKey;
    }

    public boolean isUnique() {
        return this.primaryKey || this.nullsDistinct != null;
    }

    public String getSQL(boolean z) {
        StringBuilder sb = new StringBuilder();
        if (this.primaryKey) {
            sb.append("PRIMARY KEY");
            if (this.hash) {
                sb.append(" HASH");
            }
        } else {
            if (this.nullsDistinct != null) {
                sb.append("UNIQUE ");
                if (z) {
                    this.nullsDistinct.getSQL(sb, 0).append(' ');
                }
            }
            if (this.hash) {
                sb.append("HASH ");
            }
            if (this.spatial) {
                sb.append("SPATIAL ");
            }
            sb.append("INDEX");
        }
        return sb.toString();
    }

    public boolean isScan() {
        return this.scan;
    }

    public NullsDistinct getNullsDistinct() {
        return this.nullsDistinct;
    }

    public NullsDistinct getEffectiveNullsDistinct() {
        if (this.nullsDistinct != null) {
            return this.nullsDistinct;
        }
        if (this.primaryKey) {
            return NullsDistinct.NOT_DISTINCT;
        }
        return null;
    }
}
