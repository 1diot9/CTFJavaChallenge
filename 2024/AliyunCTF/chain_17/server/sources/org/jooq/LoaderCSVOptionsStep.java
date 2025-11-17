package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/LoaderCSVOptionsStep.class */
public interface LoaderCSVOptionsStep<R extends Record> extends LoaderListenerStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVOptionsStep<R> ignoreRows(int i);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVOptionsStep<R> quote(char c);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVOptionsStep<R> separator(char c);

    @Support
    @CheckReturnValue
    @NotNull
    LoaderCSVOptionsStep<R> nullString(String str);
}
