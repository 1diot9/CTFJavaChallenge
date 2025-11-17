package org.h2.message;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/message/TraceWriter.class */
interface TraceWriter {
    void setName(String str);

    void write(int i, String str, String str2, Throwable th);

    void write(int i, int i2, String str, Throwable th);

    boolean isEnabled(int i);
}
