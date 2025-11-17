package org.h2.mvstore.rtree;

import java.util.Arrays;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/rtree/DefaultSpatial.class */
final class DefaultSpatial implements Spatial {
    private final long id;
    private final float[] minMax;

    public DefaultSpatial(long j, float... fArr) {
        this.id = j;
        this.minMax = fArr;
    }

    private DefaultSpatial(long j, DefaultSpatial defaultSpatial) {
        this.id = j;
        this.minMax = (float[]) defaultSpatial.minMax.clone();
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
        return new DefaultSpatial(j, this);
    }

    @Override // org.h2.mvstore.rtree.Spatial
    public long getId() {
        return this.id;
    }

    @Override // org.h2.mvstore.rtree.Spatial
    public boolean isNull() {
        return this.minMax.length == 0;
    }

    @Override // org.h2.mvstore.rtree.Spatial
    public boolean equalsIgnoringId(Spatial spatial) {
        return Arrays.equals(this.minMax, ((DefaultSpatial) spatial).minMax);
    }
}
