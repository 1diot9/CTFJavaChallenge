package org.jooq.impl;

import java.util.ArrayList;
import java.util.List;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteListenerProvider;
import org.jooq.conf.InvocationOrder;
import org.jooq.conf.SettingsTools;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.LoggerListener;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ExecuteListeners.class */
public final class ExecuteListeners implements ExecuteListener {
    private static final ExecuteListener EMPTY_LISTENER = new DefaultExecuteListener();
    private static final JooqLogger LOGGER_LISTENER_LOGGER = JooqLogger.getLogger((Class<?>) LoggerListener.class);
    private final ExecuteListener[][] listeners;
    private boolean resultStart;
    private boolean fetchEnd;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ExecuteListener get(ExecuteContext ctx) {
        ExecuteListener[][] listeners = listeners(ctx);
        if (listeners == null) {
            return EMPTY_LISTENER;
        }
        return new ExecuteListeners(listeners);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ExecuteListener getAndStart(ExecuteContext ctx) {
        ExecuteListener result = get(ctx);
        result.start(ctx);
        return result;
    }

    /* JADX WARN: Type inference failed for: r0v18, types: [org.jooq.ExecuteListener[], org.jooq.ExecuteListener[][]] */
    private static final ExecuteListener[][] listeners(ExecuteContext ctx) {
        ExecuteListener[] executeListenerArr;
        List<ExecuteListener> list = null;
        if (SettingsTools.getFetchServerOutputSize(0, ctx.settings()) > 0) {
            List<ExecuteListener> init = init(null);
            list = init;
            init.add(new FetchServerOutputListener());
        }
        if (!Boolean.FALSE.equals(ctx.settings().isExecuteLogging()) && LOGGER_LISTENER_LOGGER.isDebugEnabled()) {
            List<ExecuteListener> init2 = init(list);
            list = init2;
            init2.add(new LoggerListener());
        }
        for (ExecuteListenerProvider provider : ctx.configuration().executeListenerProviders()) {
            if (provider != null) {
                List<ExecuteListener> init3 = init(list);
                list = init3;
                init3.add(provider.provide());
            }
        }
        if (list == null) {
            return null;
        }
        ExecuteListener[] def = (ExecuteListener[]) list.toArray(Tools.EMPTY_EXECUTE_LISTENER);
        ExecuteListener[] rev = null;
        ?? r0 = new ExecuteListener[2];
        if (ctx.settings().getExecuteListenerStartInvocationOrder() != InvocationOrder.REVERSE) {
            executeListenerArr = def;
        } else {
            executeListenerArr = (ExecuteListener[]) Tools.reverse((ExecuteListener[]) def.clone());
            rev = executeListenerArr;
        }
        r0[0] = executeListenerArr;
        r0[1] = ctx.settings().getExecuteListenerEndInvocationOrder() != InvocationOrder.REVERSE ? def : rev != null ? rev : (ExecuteListener[]) Tools.reverse((ExecuteListener[]) def.clone());
        return r0;
    }

    private static final List<ExecuteListener> init(List<ExecuteListener> result) {
        return result == null ? new ArrayList() : result;
    }

    private ExecuteListeners(ExecuteListener[][] listeners) {
        this.listeners = listeners;
    }

    @Override // org.jooq.ExecuteListener
    public final void start(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[0]) {
            listener.start(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void renderStart(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[0]) {
            listener.renderStart(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void renderEnd(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[1]) {
            listener.renderEnd(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void prepareStart(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[0]) {
            listener.prepareStart(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void prepareEnd(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[1]) {
            listener.prepareEnd(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void bindStart(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[0]) {
            listener.bindStart(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void bindEnd(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[1]) {
            listener.bindEnd(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void executeStart(ExecuteContext ctx) {
        if (ctx instanceof DefaultExecuteContext) {
            DefaultExecuteContext d = (DefaultExecuteContext) ctx;
            d.incrementStatementExecutionCount();
        }
        for (ExecuteListener listener : this.listeners[0]) {
            listener.executeStart(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void executeEnd(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[1]) {
            listener.executeEnd(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void fetchStart(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[0]) {
            listener.fetchStart(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void outStart(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[0]) {
            listener.outStart(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void outEnd(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[1]) {
            listener.outEnd(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void resultStart(ExecuteContext ctx) {
        this.resultStart = true;
        for (ExecuteListener listener : this.listeners[0]) {
            listener.resultStart(ctx);
        }
        ((DefaultExecuteContext) ctx).resultLevel++;
    }

    @Override // org.jooq.ExecuteListener
    public final void recordStart(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[0]) {
            listener.recordStart(ctx);
        }
        ((DefaultExecuteContext) ctx).recordLevel++;
    }

    @Override // org.jooq.ExecuteListener
    public final void recordEnd(ExecuteContext ctx) {
        ((DefaultExecuteContext) ctx).recordLevel--;
        for (ExecuteListener listener : this.listeners[1]) {
            listener.recordEnd(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void resultEnd(ExecuteContext ctx) {
        ((DefaultExecuteContext) ctx).resultLevel--;
        this.resultStart = false;
        for (ExecuteListener listener : this.listeners[1]) {
            listener.resultEnd(ctx);
        }
        if (this.fetchEnd) {
            fetchEnd(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void fetchEnd(ExecuteContext ctx) {
        if (this.resultStart) {
            this.fetchEnd = true;
            return;
        }
        for (ExecuteListener listener : this.listeners[1]) {
            listener.fetchEnd(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void end(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[1]) {
            listener.end(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void exception(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[0]) {
            listener.exception(ctx);
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void warning(ExecuteContext ctx) {
        for (ExecuteListener listener : this.listeners[0]) {
            listener.warning(ctx);
        }
    }
}
