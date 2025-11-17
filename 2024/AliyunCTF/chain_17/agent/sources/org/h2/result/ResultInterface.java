package org.h2.result;

import org.h2.engine.Session;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/ResultInterface.class */
public interface ResultInterface extends AutoCloseable {
    void reset();

    Value[] currentRow();

    boolean next();

    long getRowId();

    boolean isAfterLast();

    int getVisibleColumnCount();

    long getRowCount();

    boolean hasNext();

    boolean needToClose();

    @Override // java.lang.AutoCloseable
    void close();

    String getAlias(int i);

    String getSchemaName(int i);

    String getTableName(int i);

    String getColumnName(int i);

    TypeInfo getColumnType(int i);

    boolean isIdentity(int i);

    int getNullable(int i);

    void setFetchSize(int i);

    int getFetchSize();

    boolean isLazy();

    boolean isClosed();

    ResultInterface createShallowCopy(Session session);
}
