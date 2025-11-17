package org.h2.mvstore.rtree;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/rtree/Spatial.class */
public interface Spatial {
    float min(int i);

    void setMin(int i, float f);

    float max(int i);

    void setMax(int i, float f);

    Spatial clone(long j);

    long getId();

    boolean isNull();

    boolean equalsIgnoringId(Spatial spatial);
}
