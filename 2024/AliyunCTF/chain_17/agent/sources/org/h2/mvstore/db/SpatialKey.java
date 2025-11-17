package org.h2.mvstore.db;

import java.util.Arrays;
import org.h2.engine.CastDataProvider;
import org.h2.mvstore.rtree.Spatial;
import org.h2.value.CompareMode;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/SpatialKey.class */
public class SpatialKey extends Value implements Spatial {
    private final long id;
    private final float[] minMax;

    public SpatialKey(long j, float... fArr) {
        this.id = j;
        this.minMax = fArr;
    }

    public SpatialKey(long j, SpatialKey spatialKey) {
        this.id = j;
        this.minMax = (float[]) spatialKey.minMax.clone();
    }

    @Override // org.h2.mvstore.rtree.Spatial
    public float min(int i) {
        return this.minMax[i + i];
    }

    @Override // org.h2.mvstore.rtree.Spatial
    public void setMin(int i, float f) {
        this.minMax[i + i] = f;
    }

    @Override // org.h2.mvstore.rtree.Spatial
    public float max(int i) {
        return this.minMax[i + i + 1];
    }

    @Override // org.h2.mvstore.rtree.Spatial
    public void setMax(int i, float f) {
        this.minMax[i + i + 1] = f;
    }

    @Override // org.h2.mvstore.rtree.Spatial
    public Spatial clone(long j) {
        return new SpatialKey(j, this);
    }

    @Override // org.h2.mvstore.rtree.Spatial
    public long getId() {
        return this.id;
    }

    @Override // org.h2.mvstore.rtree.Spatial
    public boolean isNull() {
        return this.minMax.length == 0;
    }

    @Override // org.h2.value.Value
    public String toString() {
        return getString();
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return (int) ((this.id >>> 32) ^ this.id);
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SpatialKey)) {
            return false;
        }
        SpatialKey spatialKey = (SpatialKey) obj;
        if (this.id != spatialKey.id) {
            return false;
        }
        return equalsIgnoringId(spatialKey);
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        throw new UnsupportedOperationException();
    }

    @Override // org.h2.mvstore.rtree.Spatial
    public boolean equalsIgnoringId(Spatial spatial) {
        return Arrays.equals(this.minMax, ((SpatialKey) spatial).minMax);
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        sb.append(this.id).append(": (");
        for (int i2 = 0; i2 < this.minMax.length; i2 += 2) {
            if (i2 > 0) {
                sb.append(", ");
            }
            sb.append(this.minMax[i2]).append('/').append(this.minMax[i2 + 1]);
        }
        sb.append(")");
        return sb;
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_GEOMETRY;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 37;
    }

    @Override // org.h2.value.Value
    public String getString() {
        return getTraceSQL();
    }
}
