package jakarta.el;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/PropertyNotFoundException.class */
public class PropertyNotFoundException extends ELException {
    private static final long serialVersionUID = -3799200961303506745L;

    public PropertyNotFoundException() {
    }

    public PropertyNotFoundException(String message) {
        super(message);
    }

    public PropertyNotFoundException(Throwable cause) {
        super(cause);
    }

    public PropertyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
