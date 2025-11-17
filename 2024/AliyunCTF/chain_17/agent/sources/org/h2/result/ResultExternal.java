package org.h2.result;

import java.util.Collection;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/ResultExternal.class */
public interface ResultExternal {
    void reset();

    Value[] next();

    int addRow(Value[] valueArr);

    int addRows(Collection<Value[]> collection);

    void close();

    int removeRow(Value[] valueArr);

    boolean contains(Value[] valueArr);

    ResultExternal createShallowCopy();
}
