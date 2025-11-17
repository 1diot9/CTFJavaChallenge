package org.apache.tomcat.util.http;

import java.util.Enumeration;
import org.apache.tomcat.util.buf.MessageBytes;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MimeHeaders.java */
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/ValuesEnumerator.class */
public class ValuesEnumerator implements Enumeration<String> {
    private int pos = 0;
    private final int size;
    private MessageBytes next;
    private final MimeHeaders headers;
    private final String name;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValuesEnumerator(MimeHeaders headers, String name) {
        this.name = name;
        this.headers = headers;
        this.size = headers.size();
        findNext();
    }

    private void findNext() {
        this.next = null;
        while (true) {
            if (this.pos >= this.size) {
                break;
            }
            MessageBytes n1 = this.headers.getName(this.pos);
            if (!n1.equalsIgnoreCase(this.name)) {
                this.pos++;
            } else {
                this.next = this.headers.getValue(this.pos);
                break;
            }
        }
        this.pos++;
    }

    @Override // java.util.Enumeration
    public boolean hasMoreElements() {
        return this.next != null;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Enumeration
    public String nextElement() {
        MessageBytes current = this.next;
        findNext();
        return current.toString();
    }
}
