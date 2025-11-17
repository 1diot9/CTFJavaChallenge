package cn.hutool.core.io;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/StreamProgress.class */
public interface StreamProgress {
    void start();

    void progress(long j, long j2);

    void finish();
}
