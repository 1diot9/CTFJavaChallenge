package org.jooq.impl;

import org.jooq.ExecuteContext;
import org.jooq.ExecuteEventHandler;
import org.jooq.ExecuteListener;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CallbackExecuteListener.class */
public final class CallbackExecuteListener implements ExecuteListener {
    private final ExecuteEventHandler onStart;
    private final ExecuteEventHandler onEnd;
    private final ExecuteEventHandler onTransformStart;
    private final ExecuteEventHandler onTransformEnd;
    private final ExecuteEventHandler onRenderStart;
    private final ExecuteEventHandler onRenderEnd;
    private final ExecuteEventHandler onPrepareStart;
    private final ExecuteEventHandler onPrepareEnd;
    private final ExecuteEventHandler onBindStart;
    private final ExecuteEventHandler onBindEnd;
    private final ExecuteEventHandler onExecuteStart;
    private final ExecuteEventHandler onExecuteEnd;
    private final ExecuteEventHandler onOutStart;
    private final ExecuteEventHandler onOutEnd;
    private final ExecuteEventHandler onFetchStart;
    private final ExecuteEventHandler onResultStart;
    private final ExecuteEventHandler onRecordStart;
    private final ExecuteEventHandler onRecordEnd;
    private final ExecuteEventHandler onResultEnd;
    private final ExecuteEventHandler onFetchEnd;
    private final ExecuteEventHandler onException;
    private final ExecuteEventHandler onWarning;

    public CallbackExecuteListener() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    private CallbackExecuteListener(ExecuteEventHandler onStart, ExecuteEventHandler onEnd, ExecuteEventHandler onTransformStart, ExecuteEventHandler onTransformEnd, ExecuteEventHandler onRenderStart, ExecuteEventHandler onRenderEnd, ExecuteEventHandler onPrepareStart, ExecuteEventHandler onPrepareEnd, ExecuteEventHandler onBindStart, ExecuteEventHandler onBindEnd, ExecuteEventHandler onExecuteStart, ExecuteEventHandler onExecuteEnd, ExecuteEventHandler onOUtStart, ExecuteEventHandler onOUtEnd, ExecuteEventHandler onFetchStart, ExecuteEventHandler onResultStart, ExecuteEventHandler onRecordStart, ExecuteEventHandler onRecordEnd, ExecuteEventHandler onResultEnd, ExecuteEventHandler onFetchEnd, ExecuteEventHandler onException, ExecuteEventHandler onWarning) {
        this.onStart = onStart;
        this.onEnd = onEnd;
        this.onRenderStart = onRenderStart;
        this.onRenderEnd = onRenderEnd;
        this.onTransformStart = onTransformStart;
        this.onTransformEnd = onTransformEnd;
        this.onPrepareStart = onPrepareStart;
        this.onPrepareEnd = onPrepareEnd;
        this.onBindStart = onBindStart;
        this.onBindEnd = onBindEnd;
        this.onExecuteStart = onExecuteStart;
        this.onExecuteEnd = onExecuteEnd;
        this.onOutStart = onOUtStart;
        this.onOutEnd = onOUtEnd;
        this.onFetchStart = onFetchStart;
        this.onResultStart = onResultStart;
        this.onRecordStart = onRecordStart;
        this.onRecordEnd = onRecordEnd;
        this.onResultEnd = onResultEnd;
        this.onFetchEnd = onFetchEnd;
        this.onException = onException;
        this.onWarning = onWarning;
    }

    @Override // org.jooq.ExecuteListener
    public final void start(ExecuteContext ctx) {
        if (this.onStart != null) {
            this.onStart.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void renderStart(ExecuteContext ctx) {
        if (this.onRenderStart != null) {
            this.onRenderStart.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void renderEnd(ExecuteContext ctx) {
        if (this.onRenderEnd != null) {
            this.onRenderEnd.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void prepareStart(ExecuteContext ctx) {
        if (this.onPrepareStart != null) {
            this.onPrepareStart.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void prepareEnd(ExecuteContext ctx) {
        if (this.onPrepareEnd != null) {
            this.onPrepareEnd.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void bindStart(ExecuteContext ctx) {
        if (this.onBindStart != null) {
            this.onBindStart.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void bindEnd(ExecuteContext ctx) {
        if (this.onBindEnd != null) {
            this.onBindEnd.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void executeStart(ExecuteContext ctx) {
        if (this.onExecuteStart != null) {
            this.onExecuteStart.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void executeEnd(ExecuteContext ctx) {
        if (this.onExecuteEnd != null) {
            this.onExecuteEnd.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void outStart(ExecuteContext ctx) {
        if (this.onOutStart != null) {
            this.onOutStart.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void outEnd(ExecuteContext ctx) {
        if (this.onOutEnd != null) {
            this.onOutEnd.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void fetchStart(ExecuteContext ctx) {
        if (this.onFetchStart != null) {
            this.onFetchStart.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void resultStart(ExecuteContext ctx) {
        if (this.onResultStart != null) {
            this.onResultStart.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void recordStart(ExecuteContext ctx) {
        if (this.onRecordStart != null) {
            this.onRecordStart.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void recordEnd(ExecuteContext ctx) {
        if (this.onRecordEnd != null) {
            this.onRecordEnd.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void resultEnd(ExecuteContext ctx) {
        if (this.onResultEnd != null) {
            this.onResultEnd.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void fetchEnd(ExecuteContext ctx) {
        if (this.onFetchEnd != null) {
            this.onFetchEnd.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void end(ExecuteContext ctx) {
        if (this.onEnd != null) {
            this.onEnd.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void exception(ExecuteContext ctx) {
        if (this.onException != null) {
            this.onException.fire(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void warning(ExecuteContext ctx) {
        if (this.onWarning != null) {
            this.onWarning.fire(ctx);
        }
    }

    public final CallbackExecuteListener onStart(ExecuteEventHandler newOnStart) {
        return new CallbackExecuteListener(newOnStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onRenderStart(ExecuteEventHandler newOnRenderStart) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, newOnRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onRenderEnd(ExecuteEventHandler newOnRenderEnd) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, newOnRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onPrepareStart(ExecuteEventHandler newOnPrepareStart) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, newOnPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onPrepareEnd(ExecuteEventHandler newOnPrepareEnd) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, newOnPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onBindStart(ExecuteEventHandler newOnBindStart) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, newOnBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onBindEnd(ExecuteEventHandler newOnBindEnd) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, newOnBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onExecuteStart(ExecuteEventHandler newOnExecuteStart) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, newOnExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onExecuteEnd(ExecuteEventHandler newOnExecuteEnd) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, newOnExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onOutStart(ExecuteEventHandler newOnOutStart) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, newOnOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onOutEnd(ExecuteEventHandler newOnOutEnd) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, newOnOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onFetchStart(ExecuteEventHandler newOnFetchStart) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, newOnFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onResultStart(ExecuteEventHandler newOnResultStart) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, newOnResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onRecordStart(ExecuteEventHandler newOnRecordStart) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, newOnRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onRecordEnd(ExecuteEventHandler newOnRecordEnd) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, newOnRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onResultEnd(ExecuteEventHandler newOnResultEnd) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, newOnResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onFetchEnd(ExecuteEventHandler newOnFetchEnd) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, newOnFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onEnd(ExecuteEventHandler newOnEnd) {
        return new CallbackExecuteListener(this.onStart, newOnEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, this.onWarning);
    }

    public final CallbackExecuteListener onException(ExecuteEventHandler newOnException) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, newOnException, this.onWarning);
    }

    public final CallbackExecuteListener onWarning(ExecuteEventHandler newOnWarning) {
        return new CallbackExecuteListener(this.onStart, this.onEnd, this.onTransformStart, this.onTransformEnd, this.onRenderStart, this.onRenderEnd, this.onPrepareStart, this.onPrepareEnd, this.onBindStart, this.onBindEnd, this.onExecuteStart, this.onExecuteEnd, this.onOutStart, this.onOutEnd, this.onFetchStart, this.onResultStart, this.onRecordStart, this.onRecordEnd, this.onResultEnd, this.onFetchEnd, this.onException, newOnWarning);
    }
}
