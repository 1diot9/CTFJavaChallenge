package org.apache.coyote;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/AsyncContextCallback.class */
public interface AsyncContextCallback {
    void fireOnComplete();

    boolean isAvailable();

    void incrementInProgressAsyncCount();

    void decrementInProgressAsyncCount();
}
