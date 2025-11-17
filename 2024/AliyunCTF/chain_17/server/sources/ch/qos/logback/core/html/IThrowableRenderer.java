package ch.qos.logback.core.html;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/html/IThrowableRenderer.class */
public interface IThrowableRenderer<E> {
    void render(StringBuilder sb, E e);
}
