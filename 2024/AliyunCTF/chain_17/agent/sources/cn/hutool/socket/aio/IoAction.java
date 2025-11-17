package cn.hutool.socket.aio;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/aio/IoAction.class */
public interface IoAction<T> {
    void accept(AioSession aioSession);

    void doAction(AioSession aioSession, T t);

    void failed(Throwable th, AioSession aioSession);
}
