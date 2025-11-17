package org.h2.result;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.coyote.http11.Constants;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionRemote;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.value.Transfer;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/ResultRemote.class */
public final class ResultRemote extends FetchedResult {
    private int fetchSize;
    private SessionRemote session;
    private Transfer transfer;
    private int id;
    private final ResultColumn[] columns;
    private long rowCount;
    private long rowOffset;
    private ArrayList<Value[]> result;
    private final Trace trace;

    public ResultRemote(SessionRemote sessionRemote, Transfer transfer, int i, int i2, int i3) throws IOException {
        this.session = sessionRemote;
        this.trace = sessionRemote.getTrace();
        this.transfer = transfer;
        this.id = i;
        this.columns = new ResultColumn[i2];
        this.rowCount = transfer.readRowCount();
        for (int i4 = 0; i4 < i2; i4++) {
            this.columns[i4] = new ResultColumn(transfer);
        }
        this.rowId = -1L;
        this.fetchSize = i3;
        if (this.rowCount >= 0) {
            i3 = (int) Math.min(this.rowCount, i3);
            this.result = new ArrayList<>(i3);
        } else {
            this.result = new ArrayList<>();
        }
        sessionRemote.lock();
        try {
            try {
                if (fetchRows(i3)) {
                    this.rowCount = this.result.size();
                }
            } catch (IOException e) {
                throw DbException.convertIOException(e, null);
            }
        } finally {
            sessionRemote.unlock();
        }
    }

    @Override // org.h2.result.ResultInterface
    public boolean isLazy() {
        return this.rowCount < 0;
    }

    @Override // org.h2.result.ResultInterface
    public String getAlias(int i) {
        return this.columns[i].alias;
    }

    @Override // org.h2.result.ResultInterface
    public String getSchemaName(int i) {
        return this.columns[i].schemaName;
    }

    @Override // org.h2.result.ResultInterface
    public String getTableName(int i) {
        return this.columns[i].tableName;
    }

    @Override // org.h2.result.ResultInterface
    public String getColumnName(int i) {
        return this.columns[i].columnName;
    }

    @Override // org.h2.result.ResultInterface
    public TypeInfo getColumnType(int i) {
        return this.columns[i].columnType;
    }

    @Override // org.h2.result.ResultInterface
    public boolean isIdentity(int i) {
        return this.columns[i].identity;
    }

    @Override // org.h2.result.ResultInterface
    public int getNullable(int i) {
        return this.columns[i].nullable;
    }

    @Override // org.h2.result.ResultInterface
    public void reset() {
        if (this.rowCount < 0 || this.rowOffset > 0) {
            throw DbException.get(ErrorCode.RESULT_SET_NOT_SCROLLABLE);
        }
        this.rowId = -1L;
        this.currentRow = null;
        this.nextRow = null;
        this.afterLast = false;
        SessionRemote sessionRemote = this.session;
        if (sessionRemote == null) {
            return;
        }
        sessionRemote.lock();
        try {
            sessionRemote.checkClosed();
            try {
                sessionRemote.traceOperation("RESULT_RESET", this.id);
                this.transfer.writeInt(6).writeInt(this.id).flush();
            } catch (IOException e) {
                throw DbException.convertIOException(e, null);
            }
        } finally {
            sessionRemote.unlock();
        }
    }

    @Override // org.h2.result.ResultInterface
    public int getVisibleColumnCount() {
        return this.columns.length;
    }

    @Override // org.h2.result.ResultInterface
    public long getRowCount() {
        if (this.rowCount < 0) {
            throw DbException.getUnsupportedException("Row count is unknown for lazy result.");
        }
        return this.rowCount;
    }

    @Override // org.h2.result.ResultInterface
    public boolean hasNext() {
        if (this.afterLast) {
            return false;
        }
        if (this.nextRow == null && (this.rowCount < 0 || this.rowId < this.rowCount - 1)) {
            long j = this.rowId + 1;
            if (this.session != null) {
                remapIfOld();
                if (j - this.rowOffset >= this.result.size()) {
                    fetchAdditionalRows();
                }
            }
            int i = (int) (j - this.rowOffset);
            this.nextRow = i < this.result.size() ? this.result.get(i) : null;
        }
        return this.nextRow != null;
    }

    private void sendClose() {
        SessionRemote sessionRemote = this.session;
        if (sessionRemote == null) {
            return;
        }
        sessionRemote.lock();
        try {
            sessionRemote.traceOperation("RESULT_CLOSE", this.id);
            this.transfer.writeInt(7).writeInt(this.id);
        } catch (IOException e) {
            this.trace.error(e, Constants.CLOSE);
        } finally {
            sessionRemote.unlock();
            this.transfer = null;
            this.session = null;
        }
    }

    @Override // org.h2.result.ResultInterface, java.lang.AutoCloseable
    public void close() {
        this.result = null;
        sendClose();
    }

    private void remapIfOld() {
        try {
            if (this.id <= this.session.getCurrentId() - (SysProperties.SERVER_CACHED_OBJECTS / 2)) {
                int nextId = this.session.getNextId();
                this.session.traceOperation("CHANGE_ID", this.id);
                this.transfer.writeInt(9).writeInt(this.id).writeInt(nextId);
                this.id = nextId;
            }
        } catch (IOException e) {
            throw DbException.convertIOException(e, null);
        }
    }

    private void fetchAdditionalRows() {
        SessionRemote sessionRemote = this.session;
        sessionRemote.lock();
        try {
            sessionRemote.checkClosed();
            try {
                this.rowOffset += this.result.size();
                this.result.clear();
                int i = this.fetchSize;
                if (this.rowCount >= 0) {
                    i = (int) Math.min(i, this.rowCount - this.rowOffset);
                } else if (i == Integer.MAX_VALUE) {
                    i = SysProperties.SERVER_RESULT_SET_FETCH_SIZE;
                }
                sessionRemote.traceOperation("RESULT_FETCH_ROWS", this.id);
                this.transfer.writeInt(5).writeInt(this.id).writeInt(i);
                sessionRemote.done(this.transfer);
                fetchRows(i);
            } catch (IOException e) {
                throw DbException.convertIOException(e, null);
            }
        } finally {
            sessionRemote.unlock();
        }
    }

    private boolean fetchRows(int i) throws IOException {
        int length = this.columns.length;
        for (int i2 = 0; i2 < i; i2++) {
            switch (this.transfer.readByte()) {
                case -1:
                    throw SessionRemote.readException(this.transfer);
                case 0:
                    sendClose();
                    return true;
                case 1:
                    Value[] valueArr = new Value[length];
                    for (int i3 = 0; i3 < length; i3++) {
                        valueArr[i3] = this.transfer.readValue(this.columns[i3].columnType);
                    }
                    this.result.add(valueArr);
                default:
                    throw DbException.getInternalError();
            }
        }
        if (this.rowCount >= 0 && this.rowOffset + this.result.size() >= this.rowCount) {
            sendClose();
            return false;
        }
        return false;
    }

    public String toString() {
        return "columns: " + this.columns.length + (this.rowCount < 0 ? " lazy" : " rows: " + this.rowCount) + " pos: " + this.rowId;
    }

    @Override // org.h2.result.ResultInterface
    public int getFetchSize() {
        return this.fetchSize;
    }

    @Override // org.h2.result.ResultInterface
    public void setFetchSize(int i) {
        this.fetchSize = i;
    }

    @Override // org.h2.result.ResultInterface
    public boolean isClosed() {
        return this.result == null;
    }
}
