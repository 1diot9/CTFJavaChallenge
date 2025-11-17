package org.jooq.impl;

import java.util.Arrays;
import java.util.function.Supplier;
import org.jooq.Configuration;
import org.jooq.ExecuteType;
import org.jooq.Record;
import org.jooq.RecordListener;
import org.jooq.RecordListenerProvider;
import org.jooq.conf.InvocationOrder;
import org.jooq.exception.ControlFlowSignal;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RecordDelegate.class */
public final class RecordDelegate<R extends Record> {
    private final Configuration configuration;
    private final Supplier<R> recordSupplier;
    private final Boolean fetched;
    private final RecordLifecycleType type;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RecordDelegate$RecordLifecycleType.class */
    public enum RecordLifecycleType {
        LOAD,
        REFRESH,
        STORE,
        INSERT,
        UPDATE,
        MERGE,
        DELETE
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecordDelegate(Configuration configuration, Supplier<R> recordSupplier, Boolean fetched) {
        this(configuration, recordSupplier, fetched, RecordLifecycleType.LOAD);
    }

    RecordDelegate(Configuration configuration, Supplier<R> recordSupplier, Boolean fetched, RecordLifecycleType type) {
        this.configuration = configuration;
        this.recordSupplier = recordSupplier;
        this.fetched = fetched;
        this.type = type;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> RecordDelegate<R> delegate(Configuration configuration, R record, RecordLifecycleType type) {
        return new RecordDelegate<>(configuration, () -> {
            return record;
        }, null, type);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <E extends Exception> R operate(ThrowingFunction<R, R, E> operation) throws Exception {
        Iterable<RecordListener> asList;
        Iterable<RecordListener> asList2;
        R record = this.recordSupplier.get();
        if (this.fetched != null && (record instanceof AbstractRecord)) {
            ((AbstractRecord) record).fetched = this.fetched.booleanValue();
        }
        RecordListener[] listeners = null;
        DefaultRecordContext ctx = null;
        Exception exc = null;
        if (this.configuration != null) {
            RecordListenerProvider[] providers = this.configuration.recordListenerProviders();
            if (!Tools.isEmpty(providers)) {
                listeners = (RecordListener[]) Tools.map(providers, p -> {
                    return p.provide();
                }, x$0 -> {
                    return new RecordListener[x$0];
                });
                ctx = new DefaultRecordContext(this.configuration, executeType(), record);
            }
        }
        if (listeners != null) {
            if (ctx == null || ctx.settings().getRecordListenerStartInvocationOrder() != InvocationOrder.REVERSE) {
                asList2 = Arrays.asList(listeners);
            } else {
                asList2 = Tools.reverseIterable(listeners);
            }
            for (RecordListener listener : asList2) {
                switch (this.type) {
                    case LOAD:
                        listener.loadStart(ctx);
                        break;
                    case REFRESH:
                        listener.refreshStart(ctx);
                        break;
                    case STORE:
                        listener.storeStart(ctx);
                        break;
                    case INSERT:
                        listener.insertStart(ctx);
                        break;
                    case UPDATE:
                        listener.updateStart(ctx);
                        break;
                    case MERGE:
                        listener.mergeStart(ctx);
                        break;
                    case DELETE:
                        listener.deleteStart(ctx);
                        break;
                    default:
                        throw new IllegalStateException("Type not supported: " + String.valueOf(this.type));
                }
            }
        }
        if (Tools.attachRecords(this.configuration)) {
            record.attach(this.configuration);
        }
        if (operation != null) {
            try {
                operation.apply(record);
            } catch (Exception e) {
                exc = e;
                if (!(e instanceof ControlFlowSignal)) {
                    if (ctx != null) {
                        ctx.exception = e;
                    }
                    if (listeners != null) {
                        for (RecordListener recordListener : listeners) {
                            recordListener.exception(ctx);
                        }
                    }
                }
            }
        }
        if (listeners != null) {
            if (ctx == null || ctx.settings().getRecordListenerEndInvocationOrder() != InvocationOrder.REVERSE) {
                asList = Arrays.asList(listeners);
            } else {
                asList = Tools.reverseIterable(listeners);
            }
            for (RecordListener listener2 : asList) {
                switch (this.type) {
                    case LOAD:
                        listener2.loadEnd(ctx);
                        break;
                    case REFRESH:
                        listener2.refreshEnd(ctx);
                        break;
                    case STORE:
                        listener2.storeEnd(ctx);
                        break;
                    case INSERT:
                        listener2.insertEnd(ctx);
                        break;
                    case UPDATE:
                        listener2.updateEnd(ctx);
                        break;
                    case MERGE:
                        listener2.mergeEnd(ctx);
                        break;
                    case DELETE:
                        listener2.deleteEnd(ctx);
                        break;
                    default:
                        throw new IllegalStateException("Type not supported: " + String.valueOf(this.type));
                }
            }
        }
        if (exc != null) {
            throw exc;
        }
        return record;
    }

    private final ExecuteType executeType() {
        return (this.type == RecordLifecycleType.LOAD || this.type == RecordLifecycleType.REFRESH) ? ExecuteType.READ : ExecuteType.WRITE;
    }
}
