package org.h2.jdbc;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLSyntaxErrorException;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcSQLSyntaxErrorException.class */
public final class JdbcSQLSyntaxErrorException extends SQLSyntaxErrorException implements JdbcException {
    private static final long serialVersionUID = 1;
    private final String originalMessage;
    private final String stackTrace;
    private String message;
    private String sql;

    public JdbcSQLSyntaxErrorException(String str, String str2, String str3, int i, Throwable th, String str4) {
        super(str, str3, i);
        this.originalMessage = str;
        this.stackTrace = str4;
        setSQL(str2);
        initCause(th);
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return this.message;
    }

    @Override // org.h2.jdbc.JdbcException
    public String getOriginalMessage() {
        return this.originalMessage;
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter printWriter) {
        super.printStackTrace(printWriter);
        DbException.printNextExceptions(this, printWriter);
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream printStream) {
        super.printStackTrace(printStream);
        DbException.printNextExceptions(this, printStream);
    }

    @Override // org.h2.jdbc.JdbcException
    public String getSQL() {
        return this.sql;
    }

    @Override // org.h2.jdbc.JdbcException
    public void setSQL(String str) {
        this.sql = str;
        this.message = DbException.buildMessageForException(this);
    }

    @Override // java.lang.Throwable, org.h2.jdbc.JdbcException
    public String toString() {
        if (this.stackTrace == null) {
            return super.toString();
        }
        return this.stackTrace;
    }
}
