package jakarta.el;

import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/ValueReference.class */
public class ValueReference implements Serializable {
    private static final long serialVersionUID = 1;
    private final Object base;
    private final Object property;

    public ValueReference(Object base, Object property) {
        this.base = base;
        this.property = property;
    }

    public Object getBase() {
        return this.base;
    }

    public Object getProperty() {
        return this.property;
    }
}
