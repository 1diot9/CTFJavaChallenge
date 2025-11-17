package jakarta.servlet;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/UnavailableException.class */
public class UnavailableException extends ServletException {
    private static final long serialVersionUID = 1;
    private final boolean permanent;
    private final int seconds;

    public UnavailableException(String msg) {
        super(msg);
        this.seconds = 0;
        this.permanent = true;
    }

    public UnavailableException(String msg, int seconds) {
        super(msg);
        if (seconds <= 0) {
            this.seconds = -1;
        } else {
            this.seconds = seconds;
        }
        this.permanent = false;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    public int getUnavailableSeconds() {
        if (this.permanent) {
            return -1;
        }
        return this.seconds;
    }
}
