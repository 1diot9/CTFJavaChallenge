package jakarta.servlet;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletException.class */
public class ServletException extends Exception {
    private static final long serialVersionUID = 1;

    public ServletException() {
    }

    public ServletException(String message) {
        super(message);
    }

    public ServletException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public ServletException(Throwable rootCause) {
        super(rootCause);
    }

    public Throwable getRootCause() {
        return getCause();
    }
}
