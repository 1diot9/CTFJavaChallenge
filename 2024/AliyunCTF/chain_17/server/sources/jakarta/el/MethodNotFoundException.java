package jakarta.el;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/MethodNotFoundException.class */
public class MethodNotFoundException extends ELException {
    private static final long serialVersionUID = -3631968116081480328L;

    public MethodNotFoundException() {
    }

    public MethodNotFoundException(String message) {
        super(message);
    }

    public MethodNotFoundException(Throwable cause) {
        super(cause);
    }

    public MethodNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
