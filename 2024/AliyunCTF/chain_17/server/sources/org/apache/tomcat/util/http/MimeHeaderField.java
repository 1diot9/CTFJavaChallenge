package org.apache.tomcat.util.http;

import org.apache.tomcat.util.buf.MessageBytes;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MimeHeaders.java */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/MimeHeaderField.class */
public class MimeHeaderField {
    private final MessageBytes nameB = MessageBytes.newInstance();
    private final MessageBytes valueB = MessageBytes.newInstance();

    public void recycle() {
        this.nameB.recycle();
        this.valueB.recycle();
    }

    public MessageBytes getName() {
        return this.nameB;
    }

    public MessageBytes getValue() {
        return this.valueB;
    }

    public String toString() {
        return this.nameB + ": " + this.valueB;
    }
}
