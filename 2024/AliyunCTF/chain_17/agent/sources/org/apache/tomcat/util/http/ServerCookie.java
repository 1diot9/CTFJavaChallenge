package org.apache.tomcat.util.http;

import java.io.Serializable;
import org.apache.tomcat.util.buf.MessageBytes;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/ServerCookie.class */
public class ServerCookie implements Serializable {
    private static final long serialVersionUID = 1;
    private final MessageBytes name = MessageBytes.newInstance();
    private final MessageBytes value = MessageBytes.newInstance();

    public void recycle() {
        this.name.recycle();
        this.value.recycle();
    }

    public MessageBytes getName() {
        return this.name;
    }

    public MessageBytes getValue() {
        return this.value;
    }

    public String toString() {
        return "Cookie " + getName() + "=" + getValue();
    }
}
