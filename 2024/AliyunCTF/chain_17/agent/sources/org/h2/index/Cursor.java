package org.h2.index;

import org.h2.result.Row;
import org.h2.result.SearchRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/Cursor.class */
public interface Cursor {
    Row get();

    SearchRow getSearchRow();

    boolean next();

    boolean previous();
}
