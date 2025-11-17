package org.springframework.web.bind.support;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/support/SimpleSessionStatus.class */
public class SimpleSessionStatus implements SessionStatus {
    private boolean complete = false;

    @Override // org.springframework.web.bind.support.SessionStatus
    public void setComplete() {
        this.complete = true;
    }

    @Override // org.springframework.web.bind.support.SessionStatus
    public boolean isComplete() {
        return this.complete;
    }
}
