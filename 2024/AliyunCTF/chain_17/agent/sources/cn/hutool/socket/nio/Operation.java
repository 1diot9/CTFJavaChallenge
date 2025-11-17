package cn.hutool.socket.nio;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/nio/Operation.class */
public enum Operation {
    READ(1),
    WRITE(4),
    CONNECT(8),
    ACCEPT(16);

    private final int value;

    Operation(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
