package org.h2.mvstore.db;

import java.io.IOException;
import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Iterator;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.mvstore.MVStore;
import org.h2.result.ResultExternal;
import org.h2.result.SortOrder;
import org.h2.store.fs.FileUtils;
import org.h2.util.TempFileDeleter;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVTempResult.class */
public abstract class MVTempResult implements ResultExternal {
    private final Database database;
    final MVStore store;
    final Expression[] expressions;
    final int visibleColumnCount;
    final int resultColumnCount;
    int rowCount;
    final MVTempResult parent;
    int childCount;
    boolean closed;
    private final TempFileDeleter tempFileDeleter;
    private final CloseImpl closeable;
    private final Reference<?> fileRef;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVTempResult$CloseImpl.class */
    public static final class CloseImpl implements AutoCloseable {
        private final MVStore store;
        private final String fileName;

        CloseImpl(MVStore mVStore, String str) {
            this.store = mVStore;
            this.fileName = str;
        }

        @Override // java.lang.AutoCloseable
        public void close() throws Exception {
            this.store.closeImmediately();
            FileUtils.tryDelete(this.fileName);
        }
    }

    public static ResultExternal of(Database database, Expression[] expressionArr, boolean z, int[] iArr, int i, int i2, SortOrder sortOrder) {
        return (!z && iArr == null && sortOrder == null) ? new MVPlainTempResult(database, expressionArr, i, i2) : new MVSortedTempResult(database, expressionArr, z, iArr, i, i2, sortOrder);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MVTempResult(MVTempResult mVTempResult) {
        this.parent = mVTempResult;
        this.database = mVTempResult.database;
        this.store = mVTempResult.store;
        this.expressions = mVTempResult.expressions;
        this.visibleColumnCount = mVTempResult.visibleColumnCount;
        this.resultColumnCount = mVTempResult.resultColumnCount;
        this.tempFileDeleter = null;
        this.closeable = null;
        this.fileRef = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MVTempResult(Database database, Expression[] expressionArr, int i, int i2) {
        this.database = database;
        try {
            String createTempFile = FileUtils.createTempFile("h2tmp", Constants.SUFFIX_TEMP_FILE, true);
            this.store = new MVStore.Builder().adoptFileStore(database.getStore().getMvStore().getFileStore().open2(createTempFile, false)).cacheSize(0).autoCommitDisabled().open();
            this.expressions = expressionArr;
            this.visibleColumnCount = i;
            this.resultColumnCount = i2;
            this.tempFileDeleter = database.getTempFileDeleter();
            this.closeable = new CloseImpl(this.store, createTempFile);
            this.fileRef = this.tempFileDeleter.addFile(this.closeable, this);
            this.parent = null;
        } catch (IOException e) {
            throw DbException.convert(e);
        }
    }

    @Override // org.h2.result.ResultExternal
    public int addRows(Collection<Value[]> collection) {
        Iterator<Value[]> it = collection.iterator();
        while (it.hasNext()) {
            addRow(it.next());
        }
        return this.rowCount;
    }

    @Override // org.h2.result.ResultExternal
    public synchronized void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.parent != null) {
            this.parent.closeChild();
        } else if (this.childCount == 0) {
            delete();
        }
    }

    private synchronized void closeChild() {
        int i = this.childCount - 1;
        this.childCount = i;
        if (i == 0 && this.closed) {
            delete();
        }
    }

    private void delete() {
        this.tempFileDeleter.deleteFile(this.fileRef, this.closeable);
    }
}
