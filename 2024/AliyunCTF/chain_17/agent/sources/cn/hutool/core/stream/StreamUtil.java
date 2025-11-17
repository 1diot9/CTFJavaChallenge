package cn.hutool.core.stream;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/stream/StreamUtil.class */
public class StreamUtil {
    @SafeVarargs
    public static <T> Stream<T> of(T... array) {
        Assert.notNull(array, "Array must be not null!", new Object[0]);
        return Stream.of((Object[]) array);
    }

    public static <T> Stream<T> of(Iterable<T> iterable) {
        return of((Iterable) iterable, false);
    }

    public static <T> Stream<T> of(Iterable<T> iterable, boolean parallel) {
        Assert.notNull(iterable, "Iterable must be not null!", new Object[0]);
        if (iterable instanceof Collection) {
            return parallel ? ((Collection) iterable).parallelStream() : ((Collection) iterable).stream();
        }
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    public static <T> Stream<T> of(Iterator<T> iterator) {
        return of((Iterator) iterator, false);
    }

    public static <T> Stream<T> of(Iterator<T> iterator, boolean parallel) {
        Assert.notNull(iterator, "iterator must not be null!", new Object[0]);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), parallel);
    }

    public static Stream<String> of(File file) {
        return of(file, CharsetUtil.CHARSET_UTF_8);
    }

    public static Stream<String> of(Path path) {
        return of(path, CharsetUtil.CHARSET_UTF_8);
    }

    public static Stream<String> of(File file, Charset charset) {
        Assert.notNull(file, "File must be not null!", new Object[0]);
        return of(file.toPath(), charset);
    }

    public static Stream<String> of(Path path, Charset charset) {
        try {
            return Files.lines(path, charset);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static <T> Stream<T> of(T seed, UnaryOperator<T> elementCreator, int limit) {
        return Stream.iterate(seed, elementCreator).limit(limit);
    }

    public static <T> String join(Stream<T> stream, CharSequence delimiter) {
        return (String) stream.collect(CollectorUtil.joining(delimiter));
    }

    public static <T> String join(Stream<T> stream, CharSequence delimiter, Function<T, ? extends CharSequence> toStringFunc) {
        return (String) stream.collect(CollectorUtil.joining(delimiter, toStringFunc));
    }
}
