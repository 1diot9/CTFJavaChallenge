package org.jooq;

import java.util.EventListener;
import java.util.function.Consumer;
import org.jooq.impl.CallbackRecordListener;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RecordListener.class */
public interface RecordListener extends EventListener {
    default void storeStart(RecordContext ctx) {
    }

    default void storeEnd(RecordContext ctx) {
    }

    default void insertStart(RecordContext ctx) {
    }

    default void insertEnd(RecordContext ctx) {
    }

    default void updateStart(RecordContext ctx) {
    }

    default void updateEnd(RecordContext ctx) {
    }

    default void mergeStart(RecordContext ctx) {
    }

    default void mergeEnd(RecordContext ctx) {
    }

    default void deleteStart(RecordContext ctx) {
    }

    default void deleteEnd(RecordContext ctx) {
    }

    default void loadStart(RecordContext ctx) {
    }

    default void loadEnd(RecordContext ctx) {
    }

    default void refreshStart(RecordContext ctx) {
    }

    default void refreshEnd(RecordContext ctx) {
    }

    default void exception(RecordContext ctx) {
    }

    static CallbackRecordListener onStoreStart(Consumer<? super RecordContext> onStoreStart) {
        return new CallbackRecordListener().onStoreStart(onStoreStart);
    }

    static CallbackRecordListener onStoreEnd(Consumer<? super RecordContext> onStoreEnd) {
        return new CallbackRecordListener().onStoreEnd(onStoreEnd);
    }

    static CallbackRecordListener onInsertStart(Consumer<? super RecordContext> onInsertStart) {
        return new CallbackRecordListener().onInsertStart(onInsertStart);
    }

    static CallbackRecordListener onInsertEnd(Consumer<? super RecordContext> onInsertEnd) {
        return new CallbackRecordListener().onInsertEnd(onInsertEnd);
    }

    static CallbackRecordListener onUpdateStart(Consumer<? super RecordContext> onUpdateStart) {
        return new CallbackRecordListener().onUpdateStart(onUpdateStart);
    }

    static CallbackRecordListener onUpdateEnd(Consumer<? super RecordContext> onUpdateEnd) {
        return new CallbackRecordListener().onUpdateEnd(onUpdateEnd);
    }

    static CallbackRecordListener onMergeStart(Consumer<? super RecordContext> onMergeStart) {
        return new CallbackRecordListener().onMergeStart(onMergeStart);
    }

    static CallbackRecordListener onMergeEnd(Consumer<? super RecordContext> onMergeEnd) {
        return new CallbackRecordListener().onMergeEnd(onMergeEnd);
    }

    static CallbackRecordListener onDeleteStart(Consumer<? super RecordContext> onDeleteStart) {
        return new CallbackRecordListener().onDeleteStart(onDeleteStart);
    }

    static CallbackRecordListener onDeleteEnd(Consumer<? super RecordContext> onDeleteEnd) {
        return new CallbackRecordListener().onDeleteEnd(onDeleteEnd);
    }

    static CallbackRecordListener onLoadStart(Consumer<? super RecordContext> onLoadStart) {
        return new CallbackRecordListener().onLoadStart(onLoadStart);
    }

    static CallbackRecordListener onLoadEnd(Consumer<? super RecordContext> onLoadEnd) {
        return new CallbackRecordListener().onLoadEnd(onLoadEnd);
    }

    static CallbackRecordListener onRefreshStart(Consumer<? super RecordContext> onRefreshStart) {
        return new CallbackRecordListener().onRefreshStart(onRefreshStart);
    }

    static CallbackRecordListener onRefreshEnd(Consumer<? super RecordContext> onRefreshEnd) {
        return new CallbackRecordListener().onRefreshEnd(onRefreshEnd);
    }

    static CallbackRecordListener onException(Consumer<? super RecordContext> onException) {
        return new CallbackRecordListener().onException(onException);
    }
}
