package org.h2.store;

import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.h2.api.ErrorCode;
import org.h2.engine.Constants;
import org.h2.message.DbException;
import org.h2.message.TraceSystem;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FileUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/FileLister.class */
public class FileLister {
    private FileLister() {
    }

    public static void tryUnlockDatabase(List<String> list, String str) throws SQLException {
        for (String str2 : list) {
            if (str2.endsWith(Constants.SUFFIX_LOCK_FILE)) {
                FileLock fileLock = new FileLock(new TraceSystem(null), str2, 1000);
                try {
                    fileLock.lock(FileLockMethod.FILE);
                    fileLock.unlock();
                } catch (DbException e) {
                    throw DbException.getJdbcSQLException(ErrorCode.CANNOT_CHANGE_SETTING_WHEN_OPEN_1, str);
                }
            } else if (str2.endsWith(Constants.SUFFIX_MV_FILE)) {
                try {
                    FileChannel open = FilePath.get(str2).open("r");
                    Throwable th = null;
                    try {
                        try {
                            open.tryLock(0L, Long.MAX_VALUE, true).release();
                            if (open != null) {
                                if (0 != 0) {
                                    try {
                                        open.close();
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                    }
                                } else {
                                    open.close();
                                }
                            }
                        } finally {
                        }
                    } finally {
                    }
                } catch (Exception e2) {
                    throw DbException.getJdbcSQLException(ErrorCode.CANNOT_CHANGE_SETTING_WHEN_OPEN_1, e2, str);
                }
            } else {
                continue;
            }
        }
    }

    public static String getDir(String str) {
        if (str == null || str.equals("")) {
            return ".";
        }
        return FileUtils.toRealPath(str);
    }

    public static ArrayList<String> getDatabaseFiles(String str, String str2, boolean z) {
        ArrayList<String> arrayList = new ArrayList<>();
        String str3 = str2 == null ? null : str2 + '.';
        for (FilePath filePath : FilePath.get(str).newDirectoryStream()) {
            boolean z2 = false;
            String filePath2 = filePath.toString();
            if (filePath2.endsWith(Constants.SUFFIX_MV_FILE)) {
                z2 = true;
            } else if (z) {
                if (filePath2.endsWith(Constants.SUFFIX_LOCK_FILE)) {
                    z2 = true;
                } else if (filePath2.endsWith(Constants.SUFFIX_TEMP_FILE)) {
                    z2 = true;
                } else if (filePath2.endsWith(Constants.SUFFIX_TRACE_FILE)) {
                    z2 = true;
                }
            }
            if (z2 && (str2 == null || filePath.getName().startsWith(str3))) {
                arrayList.add(filePath2);
            }
        }
        return arrayList;
    }
}
