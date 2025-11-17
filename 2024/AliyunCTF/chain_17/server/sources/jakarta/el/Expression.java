package jakarta.el;

import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/Expression.class */
public abstract class Expression implements Serializable {
    private static final long serialVersionUID = -6663767980471823812L;

    public abstract String getExpressionString();

    public abstract boolean equals(Object obj);

    public abstract int hashCode();

    public abstract boolean isLiteralText();
}
