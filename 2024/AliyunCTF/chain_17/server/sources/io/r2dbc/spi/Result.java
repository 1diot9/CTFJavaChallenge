package io.r2dbc.spi;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import org.reactivestreams.Publisher;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Result.class */
public interface Result {

    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Result$Message.class */
    public interface Message extends Segment {
        R2dbcException exception();

        int errorCode();

        @Nullable
        String sqlState();

        String message();
    }

    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Result$OutSegment.class */
    public interface OutSegment extends Segment {
        OutParameters outParameters();
    }

    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Result$RowSegment.class */
    public interface RowSegment extends Segment {
        Row row();
    }

    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Result$Segment.class */
    public interface Segment {
    }

    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Result$UpdateCount.class */
    public interface UpdateCount extends Segment {
        long value();
    }

    Publisher<Long> getRowsUpdated();

    <T> Publisher<T> map(BiFunction<Row, RowMetadata, ? extends T> biFunction);

    Result filter(Predicate<Segment> predicate);

    <T> Publisher<T> flatMap(Function<Segment, ? extends Publisher<? extends T>> function);

    default <T> Publisher<T> map(Function<? super Readable, ? extends T> mappingFunction) {
        Assert.requireNonNull(mappingFunction, "mappingFunction must not be null");
        return map((row, metadata) -> {
            return mappingFunction.apply(row);
        });
    }
}
