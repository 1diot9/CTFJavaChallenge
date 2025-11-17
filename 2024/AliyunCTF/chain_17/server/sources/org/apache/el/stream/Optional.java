package org.apache.el.stream;

import jakarta.el.ELException;
import jakarta.el.LambdaExpression;
import org.apache.el.util.MessageFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/stream/Optional.class */
public class Optional {
    private final Object obj;
    static final Optional EMPTY = new Optional(null);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Optional(Object obj) {
        this.obj = obj;
    }

    public Object get() throws ELException {
        if (this.obj == null) {
            throw new ELException(MessageFactory.get("stream.optional.empty"));
        }
        return this.obj;
    }

    public void ifPresent(LambdaExpression le) {
        if (this.obj != null) {
            le.invoke(this.obj);
        }
    }

    public Object orElse(Object other) {
        if (this.obj == null) {
            return other;
        }
        return this.obj;
    }

    public Object orElseGet(Object le) {
        if (this.obj == null) {
            if (le instanceof LambdaExpression) {
                return ((LambdaExpression) le).invoke((Object[]) null);
            }
            return le;
        }
        return this.obj;
    }
}
