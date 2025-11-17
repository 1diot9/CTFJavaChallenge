package jakarta.el;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/ELException.class */
public class ELException extends RuntimeException {
    private static final long serialVersionUID = -6228042809457459161L;

    public ELException() {
    }

    public ELException(String message) {
        super(message);
    }

    public ELException(Throwable cause) {
        super(cause);
    }

    public ELException(String message, Throwable cause) {
        super(message, cause);
    }
}
