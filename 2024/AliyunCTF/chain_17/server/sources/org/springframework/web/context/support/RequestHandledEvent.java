package org.springframework.web.context.support;

import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/RequestHandledEvent.class */
public class RequestHandledEvent extends ApplicationEvent {

    @Nullable
    private final String sessionId;

    @Nullable
    private final String userName;
    private final long processingTimeMillis;

    @Nullable
    private Throwable failureCause;

    public RequestHandledEvent(Object source, @Nullable String sessionId, @Nullable String userName, long processingTimeMillis) {
        super(source);
        this.sessionId = sessionId;
        this.userName = userName;
        this.processingTimeMillis = processingTimeMillis;
    }

    public RequestHandledEvent(Object source, @Nullable String sessionId, @Nullable String userName, long processingTimeMillis, @Nullable Throwable failureCause) {
        this(source, sessionId, userName, processingTimeMillis);
        this.failureCause = failureCause;
    }

    public long getProcessingTimeMillis() {
        return this.processingTimeMillis;
    }

    @Nullable
    public String getSessionId() {
        return this.sessionId;
    }

    @Nullable
    public String getUserName() {
        return this.userName;
    }

    public boolean wasFailure() {
        return this.failureCause != null;
    }

    @Nullable
    public Throwable getFailureCause() {
        return this.failureCause;
    }

    public String getShortDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("session=[").append(this.sessionId).append("]; ");
        sb.append("user=[").append(this.userName).append("]; ");
        return sb.toString();
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("session=[").append(this.sessionId).append("]; ");
        sb.append("user=[").append(this.userName).append("]; ");
        sb.append("time=[").append(this.processingTimeMillis).append("ms]");
        if (wasFailure()) {
            sb.append("; failure=[").append(this.failureCause).append("]");
        }
        return sb.toString();
    }

    @Override // java.util.EventObject
    public String toString() {
        return "RequestHandledEvent: " + getDescription();
    }
}
