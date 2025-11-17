package org.h2.mvstore.db;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.h2.api.ErrorCode;
import org.h2.command.ddl.CreateTableData;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.mvstore.FileStore;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.MVStoreTool;
import org.h2.mvstore.tx.Transaction;
import org.h2.mvstore.tx.TransactionStore;
import org.h2.mvstore.type.MetaType;
import org.h2.store.InDoubtTransaction;
import org.h2.store.fs.FileUtils;
import org.h2.util.HasSQL;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Typed;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/Store.class */
public final class Store {
    private final ConcurrentHashMap<String, MVTable> tableMap = new ConcurrentHashMap<>();
    private final MVStore mvStore;
    private final TransactionStore transactionStore;
    private long statisticsStart;
    private int temporaryMapId;
    private final boolean encrypted;
    private final String fileName;

    static char[] decodePassword(byte[] bArr) {
        char[] cArr = new char[bArr.length / 2];
        for (int i = 0; i < cArr.length; i++) {
            cArr[i] = (char) (((bArr[i + i] & 255) << 16) | (bArr[i + i + 1] & 255));
        }
        return cArr;
    }

    public Store(Database database, byte[] bArr) {
        String databasePath = database.getDatabasePath();
        MVStore.Builder builder = new MVStore.Builder();
        boolean z = false;
        if (databasePath != null) {
            String str = databasePath + Constants.SUFFIX_MV_FILE;
            this.fileName = str;
            MVStoreTool.compactCleanUp(str);
            builder.fileName(str);
            builder.pageSplitSize(database.getPageSize());
            if (database.isReadOnly()) {
                builder.readOnly();
            } else {
                if (!FileUtils.exists(str) || FileUtils.canWrite(str)) {
                    FileUtils.createDirectories(FileUtils.getParent(str));
                }
                int i = database.getSettings().autoCompactFillRate;
                if (i <= 100) {
                    builder.autoCompactFillRate(i);
                }
            }
            if (bArr != null) {
                z = true;
                builder.encryptionKey(decodePassword(bArr));
            }
            if (database.getSettings().compressData) {
                builder.compress();
                builder.pageSplitSize(65536);
            }
            builder.backgroundExceptionHandler((thread, th) -> {
                database.setBackgroundException(DbException.convert(th));
            });
            builder.autoCommitDisabled();
        } else {
            this.fileName = null;
        }
        this.encrypted = z;
        try {
            this.mvStore = builder.open();
            if (!database.getSettings().reuseSpace) {
                this.mvStore.setReuseSpace(false);
            }
            this.mvStore.setVersionsToKeep(0);
            this.transactionStore = new TransactionStore(this.mvStore, new MetaType(database, this.mvStore.backgroundExceptionHandler), new ValueDataType(database, null), database.getLockTimeout());
        } catch (MVStoreException e) {
            throw convertMVStoreException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DbException convertMVStoreException(MVStoreException mVStoreException) {
        switch (mVStoreException.getErrorCode()) {
            case 1:
            case 2:
                throw DbException.get(ErrorCode.IO_EXCEPTION_1, mVStoreException, this.fileName);
            case 3:
            default:
                throw DbException.get(ErrorCode.GENERAL_ERROR_1, mVStoreException, mVStoreException.getMessage());
            case 4:
                throw DbException.get(ErrorCode.DATABASE_IS_CLOSED, mVStoreException, this.fileName);
            case 5:
                throw DbException.get(ErrorCode.FILE_VERSION_ERROR_1, mVStoreException, this.fileName);
            case 6:
                if (this.encrypted) {
                    throw DbException.get(ErrorCode.FILE_ENCRYPTION_ERROR_1, mVStoreException, this.fileName);
                }
                throw DbException.get(ErrorCode.FILE_CORRUPTED_1, mVStoreException, this.fileName);
            case 7:
                throw DbException.get(ErrorCode.DATABASE_ALREADY_OPEN_1, mVStoreException, this.fileName);
        }
    }

    public static DbException getInvalidExpressionTypeException(String str, Typed typed) {
        TypeInfo type = typed.getType();
        if (type.getValueType() == -1) {
            return DbException.get(ErrorCode.UNKNOWN_DATA_TYPE_1, (typed instanceof HasSQL ? (HasSQL) typed : type).getTraceSQL());
        }
        return DbException.get(ErrorCode.INVALID_VALUE_2, type.getTraceSQL(), str);
    }

    public MVStore getMvStore() {
        return this.mvStore;
    }

    public TransactionStore getTransactionStore() {
        return this.transactionStore;
    }

    public MVTable getTable(String str) {
        return this.tableMap.get(str);
    }

    public MVTable createTable(CreateTableData createTableData) {
        try {
            MVTable mVTable = new MVTable(createTableData, this);
            this.tableMap.put(mVTable.getMapName(), mVTable);
            return mVTable;
        } catch (MVStoreException e) {
            throw convertMVStoreException(e);
        }
    }

    public void removeTable(MVTable mVTable) {
        try {
            this.tableMap.remove(mVTable.getMapName());
        } catch (MVStoreException e) {
            throw convertMVStoreException(e);
        }
    }

    public void flush() {
        if (this.mvStore.isPersistent() && !this.mvStore.isReadOnly()) {
            this.mvStore.commit();
        }
    }

    public void closeImmediately() {
        this.mvStore.closeImmediately();
    }

    public void removeTemporaryMaps(BitSet bitSet) {
        for (String str : this.mvStore.getMapNames()) {
            if (str.startsWith("temp.")) {
                this.mvStore.removeMap(str);
            } else if (str.startsWith("table.") || str.startsWith("index.")) {
                if (!bitSet.get(StringUtils.parseUInt31(str, str.indexOf(46) + 1, str.length()))) {
                    this.mvStore.removeMap(str);
                }
            }
        }
    }

    public synchronized String nextTemporaryMapName() {
        StringBuilder append = new StringBuilder().append("temp.");
        int i = this.temporaryMapId;
        this.temporaryMapId = i + 1;
        return append.append(i).toString();
    }

    public void prepareCommit(SessionLocal sessionLocal, String str) {
        Transaction transaction = sessionLocal.getTransaction();
        transaction.setName(str);
        transaction.prepare();
        this.mvStore.commit();
    }

    public ArrayList<InDoubtTransaction> getInDoubtTransactions() {
        List<Transaction> openTransactions = this.transactionStore.getOpenTransactions();
        ArrayList<InDoubtTransaction> newSmallArrayList = Utils.newSmallArrayList();
        for (Transaction transaction : openTransactions) {
            if (transaction.getStatus() == 2) {
                newSmallArrayList.add(new MVInDoubtTransaction(this.mvStore, transaction));
            }
        }
        return newSmallArrayList;
    }

    public void setCacheSize(int i) {
        this.mvStore.setCacheSize(i);
    }

    public void sync() {
        flush();
        this.mvStore.sync();
    }

    public void compactFile(int i) {
        this.mvStore.compactFile(i);
    }

    public void close(int i) {
        try {
            FileStore<?> fileStore = this.mvStore.getFileStore();
            if (!this.mvStore.isClosed() && fileStore != null) {
                boolean z = i == -1;
                if (fileStore.isReadOnly()) {
                    z = false;
                } else {
                    this.transactionStore.close();
                }
                if (z) {
                    i = 0;
                }
                String str = null;
                FileStore<?> fileStore2 = null;
                if (z) {
                    str = fileStore.getFileName();
                    String str2 = str + Constants.SUFFIX_MV_STORE_TEMP_FILE;
                    FileUtils.delete(str2);
                    fileStore2 = fileStore.open2(str2, false);
                }
                this.mvStore.close(i);
                if (z && FileUtils.exists(str)) {
                    compact(str, fileStore2);
                }
            }
        } catch (MVStoreException e) {
            this.mvStore.closeImmediately();
            throw DbException.get(ErrorCode.IO_EXCEPTION_1, e, "Closing");
        }
    }

    private static void compact(String str, FileStore<?> fileStore) {
        MVStore open = new MVStore.Builder().compress().adoptFileStore(fileStore).open();
        Throwable th = null;
        try {
            FileStore<?> open2 = fileStore.open2(str, true);
            MVStore.Builder builder = new MVStore.Builder();
            builder.readOnly().adoptFileStore(open2);
            MVStore open3 = builder.open();
            Throwable th2 = null;
            try {
                MVStoreTool.compact(open3, open);
                if (open3 != null) {
                    if (0 != 0) {
                        try {
                            open3.close();
                        } catch (Throwable th3) {
                            th2.addSuppressed(th3);
                        }
                    } else {
                        open3.close();
                    }
                }
                MVStoreTool.moveAtomicReplace(fileStore.getFileName(), str);
            } catch (Throwable th4) {
                if (open3 != null) {
                    if (0 != 0) {
                        try {
                            open3.close();
                        } catch (Throwable th5) {
                            th2.addSuppressed(th5);
                        }
                    } else {
                        open3.close();
                    }
                }
                throw th4;
            }
        } finally {
            if (open != null) {
                if (0 != 0) {
                    try {
                        open.close();
                    } catch (Throwable th6) {
                        th.addSuppressed(th6);
                    }
                } else {
                    open.close();
                }
            }
        }
    }

    public void statisticsStart() {
        FileStore<?> fileStore = this.mvStore.getFileStore();
        this.statisticsStart = fileStore == null ? 0L : fileStore.getReadCount();
    }

    public Map<String, Integer> statisticsEnd() {
        HashMap hashMap = new HashMap();
        FileStore<?> fileStore = this.mvStore.getFileStore();
        hashMap.put("reads", Integer.valueOf(fileStore == null ? 0 : (int) (fileStore.getReadCount() - this.statisticsStart)));
        return hashMap;
    }
}
