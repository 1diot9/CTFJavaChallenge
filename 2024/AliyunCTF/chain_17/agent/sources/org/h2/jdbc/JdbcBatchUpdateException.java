package org.h2.jdbc;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.BatchUpdateException;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcBatchUpdateException.class */
public final class JdbcBatchUpdateException extends BatchUpdateException {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdbcBatchUpdateException(SQLException sQLException, int[] iArr) {
        super(sQLException.getMessage(), sQLException.getSQLState(), sQLException.getErrorCode(), iArr);
        setNextException(sQLException);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdbcBatchUpdateException(SQLException sQLException, long[] jArr) {
        super(sQLException.getMessage(), sQLException.getSQLState(), sQLException.getErrorCode(), jArr, (Throwable) null);
        setNextException(sQLException);
    }

    @Override // java.lang.Throwable
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter printWriter) {
        if (printWriter != null) {
            super.printStackTrace(printWriter);
            if (getNextException() != null) {
                getNextException().printStackTrace(printWriter);
            }
        }
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream printStream) {
        if (printStream != null) {
            super.printStackTrace(printStream);
            if (getNextException() != null) {
                getNextException().printStackTrace(printStream);
            }
        }
    }
}
