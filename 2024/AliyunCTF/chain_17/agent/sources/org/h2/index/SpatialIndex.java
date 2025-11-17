package org.h2.index;

import org.h2.engine.SessionLocal;
import org.h2.result.SearchRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/SpatialIndex.class */
public interface SpatialIndex {
    Cursor findByGeometry(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2, SearchRow searchRow3);
}
