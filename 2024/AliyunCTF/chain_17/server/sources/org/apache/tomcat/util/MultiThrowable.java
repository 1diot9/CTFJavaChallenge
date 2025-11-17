package org.apache.tomcat.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/MultiThrowable.class */
public class MultiThrowable extends Throwable {
    private static final long serialVersionUID = 1;
    private List<Throwable> throwables = new ArrayList();

    public void add(Throwable t) {
        this.throwables.add(t);
    }

    public List<Throwable> getThrowables() {
        return Collections.unmodifiableList(this.throwables);
    }

    public Throwable getThrowable() {
        if (size() == 0) {
            return null;
        }
        if (size() == 1) {
            return this.throwables.get(0);
        }
        return this;
    }

    public int size() {
        return this.throwables.size();
    }

    @Override // java.lang.Throwable
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(": ");
        sb.append(size());
        sb.append(" wrapped Throwables: ");
        for (Throwable t : this.throwables) {
            sb.append('[');
            sb.append(t.getMessage());
            sb.append(']');
        }
        return sb.toString();
    }
}
