package cn.hutool.script;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import javax.script.ScriptException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/script/ScriptRuntimeException.class */
public class ScriptRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 8247610319171014183L;
    private String fileName;
    private int lineNumber;
    private int columnNumber;

    public ScriptRuntimeException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
        this.lineNumber = -1;
        this.columnNumber = -1;
    }

    public ScriptRuntimeException(String message) {
        super(message);
        this.lineNumber = -1;
        this.columnNumber = -1;
    }

    public ScriptRuntimeException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
        this.lineNumber = -1;
        this.columnNumber = -1;
    }

    public ScriptRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
        this.lineNumber = -1;
        this.columnNumber = -1;
    }

    public ScriptRuntimeException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
        this.lineNumber = -1;
        this.columnNumber = -1;
    }

    public ScriptRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
        this.lineNumber = -1;
        this.columnNumber = -1;
    }

    public ScriptRuntimeException(String message, String fileName, int lineNumber) {
        super(message);
        this.lineNumber = -1;
        this.columnNumber = -1;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public ScriptRuntimeException(String message, String fileName, int lineNumber, int columnNumber) {
        super(message);
        this.lineNumber = -1;
        this.columnNumber = -1;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public ScriptRuntimeException(ScriptException e) {
        super((Throwable) e);
        this.lineNumber = -1;
        this.columnNumber = -1;
        this.fileName = e.getFileName();
        this.lineNumber = e.getLineNumber();
        this.columnNumber = e.getColumnNumber();
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        StringBuilder ret = new StringBuilder().append(super.getMessage());
        if (this.fileName != null) {
            ret.append(" in ").append(this.fileName);
            if (this.lineNumber != -1) {
                ret.append(" at line number ").append(this.lineNumber);
            }
            if (this.columnNumber != -1) {
                ret.append(" at column number ").append(this.columnNumber);
            }
        }
        return ret.toString();
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public String getFileName() {
        return this.fileName;
    }
}
