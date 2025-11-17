package org.apache.tomcat.util.net;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SendfileDataBase.class */
public abstract class SendfileDataBase {
    public SendfileKeepAliveState keepAliveState = SendfileKeepAliveState.NONE;
    public final String fileName;
    public long pos;
    public long length;

    public SendfileDataBase(String filename, long pos, long length) {
        this.fileName = filename;
        this.pos = pos;
        this.length = length;
    }
}
