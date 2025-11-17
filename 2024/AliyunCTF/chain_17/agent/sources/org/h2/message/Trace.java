package org.h2.message;

import ch.qos.logback.classic.ClassicConstants;
import cn.hutool.setting.Setting;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import org.h2.expression.ParameterInterface;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.web.servlet.tags.form.FormTag;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/message/Trace.class */
public final class Trace {
    public static final int COMMAND = 0;
    public static final int CONSTRAINT = 1;
    public static final int DATABASE = 2;
    public static final int FUNCTION = 3;
    public static final int FILE_LOCK = 4;
    public static final int INDEX = 5;
    public static final int JDBC = 6;
    public static final int LOCK = 7;
    public static final int SCHEMA = 8;
    public static final int SEQUENCE = 9;
    public static final int SETTING = 10;
    public static final int TABLE = 11;
    public static final int TRIGGER = 12;
    public static final int USER = 13;
    public static final int JDBCX = 14;
    static final String[] MODULE_NAMES = {FormTag.DEFAULT_COMMAND_NAME, "constraint", "database", "function", "fileLock", BeanDefinitionParserDelegate.INDEX_ATTRIBUTE, "jdbc", "lock", "schema", "sequence", Setting.EXT_NAME, "table", "trigger", ClassicConstants.USER_MDC_KEY, "JDBCX"};
    private final TraceWriter traceWriter;
    private final String module;
    private final String lineSeparator;
    private int traceLevel;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Trace(TraceWriter traceWriter, int i) {
        this(traceWriter, MODULE_NAMES[i]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Trace(TraceWriter traceWriter, String str) {
        this.traceLevel = -1;
        this.traceWriter = traceWriter;
        this.module = str;
        this.lineSeparator = System.lineSeparator();
    }

    public void setLevel(int i) {
        this.traceLevel = i;
    }

    private boolean isEnabled(int i) {
        if (this.traceLevel == -1) {
            return this.traceWriter.isEnabled(i);
        }
        return i <= this.traceLevel;
    }

    public boolean isInfoEnabled() {
        return isEnabled(2);
    }

    public boolean isDebugEnabled() {
        return isEnabled(3);
    }

    public void error(Throwable th, String str) {
        if (isEnabled(1)) {
            this.traceWriter.write(1, this.module, str, th);
        }
    }

    public void error(Throwable th, String str, Object... objArr) {
        if (isEnabled(1)) {
            this.traceWriter.write(1, this.module, MessageFormat.format(str, objArr), th);
        }
    }

    public void info(String str) {
        if (isEnabled(2)) {
            this.traceWriter.write(2, this.module, str, (Throwable) null);
        }
    }

    public void info(String str, Object... objArr) {
        if (isEnabled(2)) {
            this.traceWriter.write(2, this.module, MessageFormat.format(str, objArr), (Throwable) null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void info(Throwable th, String str) {
        if (isEnabled(2)) {
            this.traceWriter.write(2, this.module, str, th);
        }
    }

    public static String formatParams(ArrayList<? extends ParameterInterface> arrayList) {
        if (arrayList.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        Iterator<? extends ParameterInterface> it = arrayList.iterator();
        while (it.hasNext()) {
            ParameterInterface next = it.next();
            if (next.isValueSet()) {
                String str = i == 0 ? " {" : ", ";
                i++;
                sb.append(str).append(i).append(": ").append(next.getParamValue().getTraceSQL());
            }
        }
        if (i != 0) {
            sb.append('}');
        }
        return sb.toString();
    }

    public void infoSQL(String str, String str2, long j, long j2) {
        if (!isEnabled(2)) {
            return;
        }
        StringBuilder sb = new StringBuilder(str.length() + str2.length() + 20);
        sb.append(this.lineSeparator).append("/*SQL");
        boolean z = false;
        if (str2.length() > 0) {
            z = true;
            sb.append(" l:").append(str.length());
        }
        if (j > 0) {
            z = true;
            sb.append(" #:").append(j);
        }
        if (j2 > 0) {
            z = true;
            sb.append(" t:").append(j2);
        }
        if (!z) {
            sb.append(' ');
        }
        sb.append("*/");
        StringUtils.javaEncode(str, sb, false);
        StringUtils.javaEncode(str2, sb, false);
        sb.append(';');
        this.traceWriter.write(2, this.module, sb.toString(), (Throwable) null);
    }

    public void debug(String str, Object... objArr) {
        if (isEnabled(3)) {
            this.traceWriter.write(3, this.module, MessageFormat.format(str, objArr), (Throwable) null);
        }
    }

    public void debug(String str) {
        if (isEnabled(3)) {
            this.traceWriter.write(3, this.module, str, (Throwable) null);
        }
    }

    public void debug(Throwable th, String str) {
        if (isEnabled(3)) {
            this.traceWriter.write(3, this.module, str, th);
        }
    }

    public void infoCode(String str) {
        if (isEnabled(2)) {
            this.traceWriter.write(2, this.module, this.lineSeparator + "/**/" + str, (Throwable) null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void debugCode(String str) {
        if (isEnabled(3)) {
            this.traceWriter.write(3, this.module, this.lineSeparator + "/**/" + str, (Throwable) null);
        }
    }
}
