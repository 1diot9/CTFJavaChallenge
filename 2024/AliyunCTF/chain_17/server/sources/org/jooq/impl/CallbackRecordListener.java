package org.jooq.impl;

import java.util.function.Consumer;
import org.jooq.RecordContext;
import org.jooq.RecordListener;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CallbackRecordListener.class */
public final class CallbackRecordListener implements RecordListener {
    private final Consumer<? super RecordContext> onStoreStart;
    private final Consumer<? super RecordContext> onStoreEnd;
    private final Consumer<? super RecordContext> onInsertStart;
    private final Consumer<? super RecordContext> onInsertEnd;
    private final Consumer<? super RecordContext> onUpdateStart;
    private final Consumer<? super RecordContext> onUpdateEnd;
    private final Consumer<? super RecordContext> onMergeStart;
    private final Consumer<? super RecordContext> onMergeEnd;
    private final Consumer<? super RecordContext> onDeleteStart;
    private final Consumer<? super RecordContext> onDeleteEnd;
    private final Consumer<? super RecordContext> onLoadStart;
    private final Consumer<? super RecordContext> onLoadEnd;
    private final Consumer<? super RecordContext> onRefreshStart;
    private final Consumer<? super RecordContext> onRefreshEnd;
    private final Consumer<? super RecordContext> onException;

    public CallbackRecordListener() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    private CallbackRecordListener(Consumer<? super RecordContext> onStoreStart, Consumer<? super RecordContext> onStoreEnd, Consumer<? super RecordContext> onInsertStart, Consumer<? super RecordContext> onInsertEnd, Consumer<? super RecordContext> onUpdateStart, Consumer<? super RecordContext> onUpdateEnd, Consumer<? super RecordContext> onMergeStart, Consumer<? super RecordContext> onMergeEnd, Consumer<? super RecordContext> onDeleteStart, Consumer<? super RecordContext> onDeleteEnd, Consumer<? super RecordContext> onLoadStart, Consumer<? super RecordContext> onLoadEnd, Consumer<? super RecordContext> onRefreshStart, Consumer<? super RecordContext> onRefreshEnd, Consumer<? super RecordContext> onException) {
        this.onStoreStart = onStoreStart;
        this.onStoreEnd = onStoreEnd;
        this.onInsertStart = onInsertStart;
        this.onInsertEnd = onInsertEnd;
        this.onUpdateStart = onUpdateStart;
        this.onUpdateEnd = onUpdateEnd;
        this.onMergeStart = onMergeStart;
        this.onMergeEnd = onMergeEnd;
        this.onDeleteStart = onDeleteStart;
        this.onDeleteEnd = onDeleteEnd;
        this.onLoadStart = onLoadStart;
        this.onLoadEnd = onLoadEnd;
        this.onRefreshStart = onRefreshStart;
        this.onRefreshEnd = onRefreshEnd;
        this.onException = onException;
    }

    @Override // org.jooq.RecordListener
    public final void storeStart(RecordContext ctx) {
        if (this.onStoreStart != null) {
            this.onStoreStart.accept(ctx);
        }
    }

    @Override // org.jooq.RecordListener
    public final void storeEnd(RecordContext ctx) {
        if (this.onStoreEnd != null) {
            this.onStoreEnd.accept(ctx);
        }
    }

    @Override // org.jooq.RecordListener
    public final void insertStart(RecordContext ctx) {
        if (this.onInsertStart != null) {
            this.onInsertStart.accept(ctx);
        }
    }

    @Override // org.jooq.RecordListener
    public final void insertEnd(RecordContext ctx) {
        if (this.onInsertEnd != null) {
            this.onInsertEnd.accept(ctx);
        }
    }

    @Override // org.jooq.RecordListener
    public final void updateStart(RecordContext ctx) {
        if (this.onUpdateStart != null) {
            this.onUpdateStart.accept(ctx);
        }
    }

    @Override // org.jooq.RecordListener
    public final void updateEnd(RecordContext ctx) {
        if (this.onUpdateEnd != null) {
            this.onUpdateEnd.accept(ctx);
        }
    }

    @Override // org.jooq.RecordListener
    public final void mergeStart(RecordContext ctx) {
        if (this.onMergeStart != null) {
            this.onMergeStart.accept(ctx);
        }
    }

    @Override // org.jooq.RecordListener
    public final void mergeEnd(RecordContext ctx) {
        if (this.onMergeEnd != null) {
            this.onMergeEnd.accept(ctx);
        }
    }

    @Override // org.jooq.RecordListener
    public final void deleteStart(RecordContext ctx) {
        if (this.onDeleteStart != null) {
            this.onDeleteStart.accept(ctx);
        }
    }

    @Override // org.jooq.RecordListener
    public final void deleteEnd(RecordContext ctx) {
        if (this.onDeleteEnd != null) {
            this.onDeleteEnd.accept(ctx);
        }
    }

    @Override // org.jooq.RecordListener
    public final void loadStart(RecordContext ctx) {
        if (this.onLoadStart != null) {
            this.onLoadStart.accept(ctx);
        }
    }

    @Override // org.jooq.RecordListener
    public final void loadEnd(RecordContext ctx) {
        if (this.onLoadEnd != null) {
            this.onLoadEnd.accept(ctx);
        }
    }

    @Override // org.jooq.RecordListener
    public final void refreshStart(RecordContext ctx) {
        if (this.onRefreshStart != null) {
            this.onRefreshStart.accept(ctx);
        }
    }

    @Override // org.jooq.RecordListener
    public final void refreshEnd(RecordContext ctx) {
        if (this.onRefreshEnd != null) {
            this.onRefreshEnd.accept(ctx);
        }
    }

    @Override // org.jooq.RecordListener
    public final void exception(RecordContext ctx) {
        if (this.onException != null) {
            this.onException.accept(ctx);
        }
    }

    public final CallbackRecordListener onStoreStart(Consumer<? super RecordContext> newOnStoreStart) {
        return new CallbackRecordListener(newOnStoreStart, this.onStoreEnd, this.onInsertStart, this.onInsertEnd, this.onUpdateStart, this.onUpdateEnd, this.onMergeStart, this.onMergeEnd, this.onDeleteStart, this.onDeleteEnd, this.onLoadStart, this.onLoadEnd, this.onRefreshStart, this.onRefreshEnd, this.onException);
    }

    public final CallbackRecordListener onStoreEnd(Consumer<? super RecordContext> newOnStoreEnd) {
        return new CallbackRecordListener(this.onStoreStart, newOnStoreEnd, this.onInsertStart, this.onInsertEnd, this.onUpdateStart, this.onUpdateEnd, this.onMergeStart, this.onMergeEnd, this.onDeleteStart, this.onDeleteEnd, this.onLoadStart, this.onLoadEnd, this.onRefreshStart, this.onRefreshEnd, this.onException);
    }

    public final CallbackRecordListener onInsertStart(Consumer<? super RecordContext> newOnInsertStart) {
        return new CallbackRecordListener(this.onStoreStart, this.onStoreEnd, newOnInsertStart, this.onInsertEnd, this.onUpdateStart, this.onUpdateEnd, this.onMergeStart, this.onMergeEnd, this.onDeleteStart, this.onDeleteEnd, this.onLoadStart, this.onLoadEnd, this.onRefreshStart, this.onRefreshEnd, this.onException);
    }

    public final CallbackRecordListener onInsertEnd(Consumer<? super RecordContext> newOnInsertEnd) {
        return new CallbackRecordListener(this.onStoreStart, this.onStoreEnd, this.onInsertStart, newOnInsertEnd, this.onUpdateStart, this.onUpdateEnd, this.onMergeStart, this.onMergeEnd, this.onDeleteStart, this.onDeleteEnd, this.onLoadStart, this.onLoadEnd, this.onRefreshStart, this.onRefreshEnd, this.onException);
    }

    public final CallbackRecordListener onUpdateStart(Consumer<? super RecordContext> newOnUpdateStart) {
        return new CallbackRecordListener(this.onStoreStart, this.onStoreEnd, this.onInsertStart, this.onInsertEnd, newOnUpdateStart, this.onUpdateEnd, this.onMergeStart, this.onMergeEnd, this.onDeleteStart, this.onDeleteEnd, this.onLoadStart, this.onLoadEnd, this.onRefreshStart, this.onRefreshEnd, this.onException);
    }

    public final CallbackRecordListener onUpdateEnd(Consumer<? super RecordContext> newOnUpdateEnd) {
        return new CallbackRecordListener(this.onStoreStart, this.onStoreEnd, this.onInsertStart, this.onInsertEnd, this.onUpdateStart, newOnUpdateEnd, this.onMergeStart, this.onMergeEnd, this.onDeleteStart, this.onDeleteEnd, this.onLoadStart, this.onLoadEnd, this.onRefreshStart, this.onRefreshEnd, this.onException);
    }

    public final CallbackRecordListener onMergeStart(Consumer<? super RecordContext> newOnMergeStart) {
        return new CallbackRecordListener(this.onStoreStart, this.onStoreEnd, this.onInsertStart, this.onInsertEnd, this.onUpdateStart, this.onUpdateEnd, newOnMergeStart, this.onMergeEnd, this.onDeleteStart, this.onDeleteEnd, this.onLoadStart, this.onLoadEnd, this.onRefreshStart, this.onRefreshEnd, this.onException);
    }

    public final CallbackRecordListener onMergeEnd(Consumer<? super RecordContext> newOnMergeEnd) {
        return new CallbackRecordListener(this.onStoreStart, this.onStoreEnd, this.onInsertStart, this.onInsertEnd, this.onUpdateStart, this.onUpdateEnd, this.onMergeStart, newOnMergeEnd, this.onDeleteStart, this.onDeleteEnd, this.onLoadStart, this.onLoadEnd, this.onRefreshStart, this.onRefreshEnd, this.onException);
    }

    public final CallbackRecordListener onDeleteStart(Consumer<? super RecordContext> newOnDeleteStart) {
        return new CallbackRecordListener(this.onStoreStart, this.onStoreEnd, this.onInsertStart, this.onInsertEnd, this.onUpdateStart, this.onUpdateEnd, this.onMergeStart, this.onMergeEnd, newOnDeleteStart, this.onDeleteEnd, this.onLoadStart, this.onLoadEnd, this.onRefreshStart, this.onRefreshEnd, this.onException);
    }

    public final CallbackRecordListener onDeleteEnd(Consumer<? super RecordContext> newOnDeleteEnd) {
        return new CallbackRecordListener(this.onStoreStart, this.onStoreEnd, this.onInsertStart, this.onInsertEnd, this.onUpdateStart, this.onUpdateEnd, this.onMergeStart, this.onMergeEnd, this.onDeleteStart, newOnDeleteEnd, this.onLoadStart, this.onLoadEnd, this.onRefreshStart, this.onRefreshEnd, this.onException);
    }

    public final CallbackRecordListener onLoadStart(Consumer<? super RecordContext> newOnLoadStart) {
        return new CallbackRecordListener(this.onStoreStart, this.onStoreEnd, this.onInsertStart, this.onInsertEnd, this.onUpdateStart, this.onUpdateEnd, this.onMergeStart, this.onMergeEnd, this.onDeleteStart, this.onDeleteEnd, newOnLoadStart, this.onLoadEnd, this.onRefreshStart, this.onRefreshEnd, this.onException);
    }

    public final CallbackRecordListener onLoadEnd(Consumer<? super RecordContext> newOnLoadEnd) {
        return new CallbackRecordListener(this.onStoreStart, this.onStoreEnd, this.onInsertStart, this.onInsertEnd, this.onUpdateStart, this.onUpdateEnd, this.onMergeStart, this.onMergeEnd, this.onDeleteStart, this.onDeleteEnd, this.onLoadStart, newOnLoadEnd, this.onRefreshStart, this.onRefreshEnd, this.onException);
    }

    public final CallbackRecordListener onRefreshStart(Consumer<? super RecordContext> newOnRefreshStart) {
        return new CallbackRecordListener(this.onStoreStart, this.onStoreEnd, this.onInsertStart, this.onInsertEnd, this.onUpdateStart, this.onUpdateEnd, this.onMergeStart, this.onMergeEnd, this.onDeleteStart, this.onDeleteEnd, this.onLoadStart, this.onLoadEnd, newOnRefreshStart, this.onRefreshEnd, this.onException);
    }

    public final CallbackRecordListener onRefreshEnd(Consumer<? super RecordContext> newOnRefreshEnd) {
        return new CallbackRecordListener(this.onStoreStart, this.onStoreEnd, this.onInsertStart, this.onInsertEnd, this.onUpdateStart, this.onUpdateEnd, this.onMergeStart, this.onMergeEnd, this.onDeleteStart, this.onDeleteEnd, this.onLoadStart, this.onLoadEnd, this.onRefreshStart, newOnRefreshEnd, this.onException);
    }

    public final CallbackRecordListener onException(Consumer<? super RecordContext> newOnException) {
        return new CallbackRecordListener(this.onStoreStart, this.onStoreEnd, this.onInsertStart, this.onInsertEnd, this.onUpdateStart, this.onUpdateEnd, this.onMergeStart, this.onMergeEnd, this.onDeleteStart, this.onDeleteEnd, this.onLoadStart, this.onLoadEnd, this.onRefreshStart, this.onRefreshEnd, newOnException);
    }
}
