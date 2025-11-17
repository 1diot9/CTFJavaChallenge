package org.jooq;

import java.io.Serializable;
import java.util.EventListener;
import org.jooq.impl.CallbackExecuteListener;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ExecuteListener.class */
public interface ExecuteListener extends EventListener, Serializable {
    default void start(ExecuteContext ctx) {
    }

    default void renderStart(ExecuteContext ctx) {
    }

    default void renderEnd(ExecuteContext ctx) {
    }

    default void prepareStart(ExecuteContext ctx) {
    }

    default void prepareEnd(ExecuteContext ctx) {
    }

    default void bindStart(ExecuteContext ctx) {
    }

    default void bindEnd(ExecuteContext ctx) {
    }

    default void executeStart(ExecuteContext ctx) {
    }

    default void executeEnd(ExecuteContext ctx) {
    }

    default void outStart(ExecuteContext ctx) {
    }

    default void outEnd(ExecuteContext ctx) {
    }

    default void fetchStart(ExecuteContext ctx) {
    }

    default void resultStart(ExecuteContext ctx) {
    }

    default void recordStart(ExecuteContext ctx) {
    }

    default void recordEnd(ExecuteContext ctx) {
    }

    default void resultEnd(ExecuteContext ctx) {
    }

    default void fetchEnd(ExecuteContext ctx) {
    }

    default void end(ExecuteContext ctx) {
    }

    default void exception(ExecuteContext ctx) {
    }

    default void warning(ExecuteContext ctx) {
    }

    static CallbackExecuteListener onStart(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onStart(handler);
    }

    static CallbackExecuteListener onEnd(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onEnd(handler);
    }

    static CallbackExecuteListener onRenderStart(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onRenderStart(handler);
    }

    static CallbackExecuteListener onRenderEnd(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onRenderEnd(handler);
    }

    static CallbackExecuteListener onPrepareStart(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onPrepareStart(handler);
    }

    static CallbackExecuteListener onPrepareEnd(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onPrepareEnd(handler);
    }

    static CallbackExecuteListener onBindStart(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onBindStart(handler);
    }

    static CallbackExecuteListener onBindEnd(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onBindEnd(handler);
    }

    static CallbackExecuteListener onExecuteStart(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onExecuteStart(handler);
    }

    static CallbackExecuteListener onExecuteEnd(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onExecuteEnd(handler);
    }

    static CallbackExecuteListener onOutStart(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onOutStart(handler);
    }

    static CallbackExecuteListener onOutEnd(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onOutEnd(handler);
    }

    static CallbackExecuteListener onFetchStart(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onFetchStart(handler);
    }

    static CallbackExecuteListener onFetchEnd(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onFetchEnd(handler);
    }

    static CallbackExecuteListener onResultStart(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onResultStart(handler);
    }

    static CallbackExecuteListener onResultEnd(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onResultEnd(handler);
    }

    static CallbackExecuteListener onRecordStart(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onRecordStart(handler);
    }

    static CallbackExecuteListener onRecordEnd(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onRecordEnd(handler);
    }

    static CallbackExecuteListener onException(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onException(handler);
    }

    static CallbackExecuteListener onWarning(ExecuteEventHandler handler) {
        return new CallbackExecuteListener().onWarning(handler);
    }
}
