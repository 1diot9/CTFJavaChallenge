package org.h2.jmx;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jmx/DatabaseInfoMBean.class */
public interface DatabaseInfoMBean {
    boolean isExclusive();

    boolean isReadOnly();

    String getMode();

    long getFileWriteCount();

    long getFileReadCount();

    long getFileSize();

    int getCacheSizeMax();

    void setCacheSizeMax(int i);

    int getCacheSize();

    String getVersion();

    int getTraceLevel();

    void setTraceLevel(int i);

    String listSettings();

    String listSessions();
}
