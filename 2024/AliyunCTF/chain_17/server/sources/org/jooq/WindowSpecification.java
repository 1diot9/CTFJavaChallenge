package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/WindowSpecification.class */
public interface WindowSpecification extends QueryPart {
    @ApiStatus.Experimental
    @Nullable
    WindowDefinition $windowDefinition();

    @ApiStatus.Experimental
    @NotNull
    QOM.UnmodifiableList<? extends GroupField> $partitionBy();

    @ApiStatus.Experimental
    @NotNull
    QOM.UnmodifiableList<? extends SortField<?>> $orderBy();

    @ApiStatus.Experimental
    @Nullable
    QOM.FrameUnits $frameUnits();

    @ApiStatus.Experimental
    @Nullable
    Integer $frameStart();

    @ApiStatus.Experimental
    @Nullable
    Integer $frameEnd();

    @ApiStatus.Experimental
    @Nullable
    QOM.FrameExclude $exclude();
}
