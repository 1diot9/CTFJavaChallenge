package org.springframework.core.io.buffer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.util.context.Context;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils.class */
public abstract class DataBufferUtils {
    private static final Log logger = LogFactory.getLog((Class<?>) DataBufferUtils.class);
    private static final Consumer<DataBuffer> RELEASE_CONSUMER = DataBufferUtils::release;
    private static final int DEFAULT_CHUNK_SIZE = 1024;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils$Matcher.class */
    public interface Matcher {
        int match(DataBuffer dataBuffer);

        byte[] delimiter();

        void reset();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils$NestedMatcher.class */
    public interface NestedMatcher extends Matcher {
        boolean match(byte b);
    }

    public static Flux<DataBuffer> readInputStream(Callable<InputStream> inputStreamSupplier, DataBufferFactory bufferFactory, int bufferSize) {
        Assert.notNull(inputStreamSupplier, "'inputStreamSupplier' must not be null");
        return readByteChannel(() -> {
            return Channels.newChannel((InputStream) inputStreamSupplier.call());
        }, bufferFactory, bufferSize);
    }

    public static Flux<DataBuffer> readByteChannel(Callable<ReadableByteChannel> channelSupplier, DataBufferFactory bufferFactory, int bufferSize) {
        Assert.notNull(channelSupplier, "'channelSupplier' must not be null");
        Assert.notNull(bufferFactory, "'bufferFactory' must not be null");
        Assert.isTrue(bufferSize > 0, "'bufferSize' must be > 0");
        return Flux.using(channelSupplier, channel -> {
            return Flux.generate(new ReadableByteChannelGenerator(channel, bufferFactory, bufferSize));
        }, (v0) -> {
            closeChannel(v0);
        });
    }

    public static Flux<DataBuffer> readAsynchronousFileChannel(Callable<AsynchronousFileChannel> channelSupplier, DataBufferFactory bufferFactory, int bufferSize) {
        return readAsynchronousFileChannel(channelSupplier, 0L, bufferFactory, bufferSize);
    }

    public static Flux<DataBuffer> readAsynchronousFileChannel(Callable<AsynchronousFileChannel> channelSupplier, long position, DataBufferFactory bufferFactory, int bufferSize) {
        Assert.notNull(channelSupplier, "'channelSupplier' must not be null");
        Assert.notNull(bufferFactory, "'bufferFactory' must not be null");
        Assert.isTrue(position >= 0, "'position' must be >= 0");
        Assert.isTrue(bufferSize > 0, "'bufferSize' must be > 0");
        Flux<DataBuffer> flux = Flux.using(channelSupplier, channel -> {
            return Flux.create(sink -> {
                ReadCompletionHandler handler = new ReadCompletionHandler(channel, sink, position, bufferFactory, bufferSize);
                Objects.requireNonNull(handler);
                sink.onCancel(handler::cancel);
                Objects.requireNonNull(handler);
                sink.onRequest(handler::request);
            });
        }, channel2 -> {
        });
        return flux.doOnDiscard(DataBuffer.class, DataBufferUtils::release);
    }

    public static Flux<DataBuffer> read(Path path, DataBufferFactory bufferFactory, int bufferSize, OpenOption... options) {
        Assert.notNull(path, "Path must not be null");
        Assert.notNull(bufferFactory, "DataBufferFactory must not be null");
        Assert.isTrue(bufferSize > 0, "'bufferSize' must be > 0");
        if (options.length > 0) {
            int length = options.length;
            for (int i = 0; i < length; i++) {
                OpenOption option = options[i];
                Assert.isTrue((option == StandardOpenOption.APPEND || option == StandardOpenOption.WRITE) ? false : true, (Supplier<String>) () -> {
                    return "'" + option + "' not allowed";
                });
            }
        }
        return readAsynchronousFileChannel(() -> {
            return AsynchronousFileChannel.open(path, options);
        }, bufferFactory, bufferSize);
    }

    public static Flux<DataBuffer> read(Resource resource, DataBufferFactory bufferFactory, int bufferSize) {
        return read(resource, 0L, bufferFactory, bufferSize);
    }

    public static Flux<DataBuffer> read(Resource resource, long position, DataBufferFactory bufferFactory, int bufferSize) {
        try {
            if (resource.isFile()) {
                File file = resource.getFile();
                return readAsynchronousFileChannel(() -> {
                    return AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.READ);
                }, position, bufferFactory, bufferSize);
            }
        } catch (IOException e) {
        }
        Objects.requireNonNull(resource);
        Flux<DataBuffer> result = readByteChannel(resource::readableChannel, bufferFactory, bufferSize);
        return position == 0 ? result : skipUntilByteCount(result, position);
    }

    public static Flux<DataBuffer> write(Publisher<DataBuffer> source, OutputStream outputStream) {
        Assert.notNull(source, "'source' must not be null");
        Assert.notNull(outputStream, "'outputStream' must not be null");
        WritableByteChannel channel = Channels.newChannel(outputStream);
        return write(source, channel);
    }

    public static Flux<DataBuffer> write(Publisher<DataBuffer> source, WritableByteChannel channel) {
        Assert.notNull(source, "'source' must not be null");
        Assert.notNull(channel, "'channel' must not be null");
        Flux<DataBuffer> flux = Flux.from(source);
        return Flux.create(sink -> {
            WritableByteChannelSubscriber subscriber = new WritableByteChannelSubscriber(sink, channel);
            sink.onDispose(subscriber);
            flux.subscribe(subscriber);
        });
    }

    public static Flux<DataBuffer> write(Publisher<DataBuffer> source, AsynchronousFileChannel channel) {
        return write(source, channel, 0L);
    }

    public static Flux<DataBuffer> write(Publisher<? extends DataBuffer> source, AsynchronousFileChannel channel, long position) {
        Assert.notNull(source, "'source' must not be null");
        Assert.notNull(channel, "'channel' must not be null");
        Assert.isTrue(position >= 0, "'position' must be >= 0");
        Flux<DataBuffer> flux = Flux.from(source);
        return Flux.create(sink -> {
            WriteCompletionHandler handler = new WriteCompletionHandler(sink, channel, position);
            sink.onDispose(handler);
            flux.subscribe(handler);
        });
    }

    public static Mono<Void> write(Publisher<DataBuffer> source, Path destination, OpenOption... options) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(destination, "Destination must not be null");
        Set<OpenOption> optionSet = checkWriteOptions(options);
        return Mono.create(sink -> {
            try {
                AsynchronousFileChannel channel = AsynchronousFileChannel.open(destination, optionSet, null, new FileAttribute[0]);
                sink.onDispose(() -> {
                    closeChannel(channel);
                });
                Flux<DataBuffer> write = write((Publisher<DataBuffer>) source, channel);
                Consumer consumer = DataBufferUtils::release;
                Objects.requireNonNull(sink);
                Consumer consumer2 = sink::error;
                Objects.requireNonNull(sink);
                write.subscribe(consumer, consumer2, sink::success, Context.of(sink.contextView()));
            } catch (IOException ex) {
                sink.error(ex);
            }
        });
    }

    private static Set<OpenOption> checkWriteOptions(OpenOption[] options) {
        int length = options.length;
        Set<OpenOption> result = new HashSet<>(length + 3);
        if (length == 0) {
            result.add(StandardOpenOption.CREATE);
            result.add(StandardOpenOption.TRUNCATE_EXISTING);
        } else {
            for (OpenOption opt : options) {
                if (opt == StandardOpenOption.READ) {
                    throw new IllegalArgumentException("READ not allowed");
                }
                result.add(opt);
            }
        }
        result.add(StandardOpenOption.WRITE);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void closeChannel(@Nullable Channel channel) {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException e) {
            }
        }
    }

    public static Publisher<DataBuffer> outputStreamPublisher(Consumer<OutputStream> outputStreamConsumer, DataBufferFactory bufferFactory, Executor executor) {
        return outputStreamPublisher(outputStreamConsumer, bufferFactory, executor, 1024);
    }

    public static Publisher<DataBuffer> outputStreamPublisher(Consumer<OutputStream> outputStreamConsumer, DataBufferFactory bufferFactory, Executor executor, int chunkSize) {
        Assert.notNull(outputStreamConsumer, "OutputStreamConsumer must not be null");
        Assert.notNull(bufferFactory, "BufferFactory must not be null");
        Assert.notNull(executor, "Executor must not be null");
        Assert.isTrue(chunkSize > 0, "Chunk size must be > 0");
        return new OutputStreamPublisher(outputStreamConsumer, bufferFactory, executor, chunkSize);
    }

    public static <T extends DataBuffer> Flux<T> takeUntilByteCount(Publisher<T> publisher, long maxByteCount) {
        Assert.notNull(publisher, "Publisher must not be null");
        Assert.isTrue(maxByteCount >= 0, "'maxByteCount' must be >= 0");
        return Flux.defer(() -> {
            AtomicLong countDown = new AtomicLong(maxByteCount);
            return Flux.from(publisher).map(buffer -> {
                long remainder = countDown.addAndGet(-buffer.readableByteCount());
                if (remainder < 0) {
                    int index = buffer.readableByteCount() + ((int) remainder);
                    DataBuffer split = buffer.split(index);
                    release(buffer);
                    return split;
                }
                return buffer;
            }).takeUntil(buffer2 -> {
                return countDown.get() <= 0;
            });
        });
    }

    public static <T extends DataBuffer> Flux<T> skipUntilByteCount(Publisher<T> publisher, long maxByteCount) {
        Assert.notNull(publisher, "Publisher must not be null");
        Assert.isTrue(maxByteCount >= 0, "'maxByteCount' must be >= 0");
        return Flux.defer(() -> {
            AtomicLong countDown = new AtomicLong(maxByteCount);
            return Flux.from(publisher).skipUntil(buffer -> {
                long remainder = countDown.addAndGet(-buffer.readableByteCount());
                return remainder < 0;
            }).map(buffer2 -> {
                long remainder = countDown.get();
                if (remainder < 0) {
                    countDown.set(0L);
                    int start = buffer2.readableByteCount() + ((int) remainder);
                    DataBuffer split = buffer2.split(start);
                    release(split);
                    return buffer2;
                }
                return buffer2;
            });
        }).doOnDiscard(DataBuffer.class, DataBufferUtils::release);
    }

    public static <T extends DataBuffer> T retain(T dataBuffer) {
        if (dataBuffer instanceof PooledDataBuffer) {
            PooledDataBuffer pooledDataBuffer = (PooledDataBuffer) dataBuffer;
            return pooledDataBuffer.retain();
        }
        return dataBuffer;
    }

    public static <T extends DataBuffer> T touch(T dataBuffer, Object hint) {
        if (dataBuffer instanceof TouchableDataBuffer) {
            TouchableDataBuffer touchableDataBuffer = (TouchableDataBuffer) dataBuffer;
            return touchableDataBuffer.touch(hint);
        }
        return dataBuffer;
    }

    public static boolean release(@Nullable DataBuffer dataBuffer) {
        if (dataBuffer instanceof PooledDataBuffer) {
            PooledDataBuffer pooledDataBuffer = (PooledDataBuffer) dataBuffer;
            if (pooledDataBuffer.isAllocated()) {
                try {
                    return pooledDataBuffer.release();
                } catch (IllegalStateException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to release PooledDataBuffer: " + dataBuffer, ex);
                        return false;
                    }
                    return false;
                }
            }
            return false;
        }
        if (dataBuffer instanceof CloseableDataBuffer) {
            CloseableDataBuffer closeableDataBuffer = (CloseableDataBuffer) dataBuffer;
            try {
                closeableDataBuffer.close();
                return true;
            } catch (IllegalStateException ex2) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to release CloseableDataBuffer " + dataBuffer, ex2);
                    return false;
                }
                return false;
            }
        }
        return false;
    }

    public static Consumer<DataBuffer> releaseConsumer() {
        return RELEASE_CONSUMER;
    }

    public static Mono<DataBuffer> join(Publisher<? extends DataBuffer> dataBuffers) {
        return join(dataBuffers, -1);
    }

    public static Mono<DataBuffer> join(Publisher<? extends DataBuffer> buffers, int maxByteCount) {
        Assert.notNull(buffers, "'buffers' must not be null");
        if (buffers instanceof Mono) {
            Mono mono = (Mono) buffers;
            return mono;
        }
        return Flux.from(buffers).collect(() -> {
            return new LimitedDataBufferList(maxByteCount);
        }, (v0, v1) -> {
            v0.add(v1);
        }).filter(list -> {
            return !list.isEmpty();
        }).map(list2 -> {
            return list2.get(0).factory().join(list2);
        }).doOnDiscard(DataBuffer.class, DataBufferUtils::release);
    }

    public static Matcher matcher(byte[] delimiter) {
        return createMatcher(delimiter);
    }

    public static Matcher matcher(byte[]... delimiters) {
        Assert.isTrue(delimiters.length > 0, "Delimiters must not be empty");
        return delimiters.length == 1 ? createMatcher(delimiters[0]) : new CompositeMatcher(delimiters);
    }

    private static NestedMatcher createMatcher(byte[] delimiter) {
        int length = delimiter.length;
        Assert.isTrue(length > 0, "Delimiter must not be empty");
        switch (length) {
            case 1:
                return delimiter[0] == 10 ? SingleByteMatcher.NEWLINE_MATCHER : new SingleByteMatcher(delimiter);
            case 2:
                return new TwoByteMatcher(delimiter);
            default:
                return new KnuthMorrisPrattMatcher(delimiter);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils$CompositeMatcher.class */
    private static class CompositeMatcher implements Matcher {
        private static final byte[] NO_DELIMITER = new byte[0];
        private final NestedMatcher[] matchers;
        byte[] longestDelimiter = NO_DELIMITER;

        CompositeMatcher(byte[][] delimiters) {
            this.matchers = initMatchers(delimiters);
        }

        private static NestedMatcher[] initMatchers(byte[][] delimiters) {
            NestedMatcher[] matchers = new NestedMatcher[delimiters.length];
            for (int i = 0; i < delimiters.length; i++) {
                matchers[i] = DataBufferUtils.createMatcher(delimiters[i]);
            }
            return matchers;
        }

        @Override // org.springframework.core.io.buffer.DataBufferUtils.Matcher
        public int match(DataBuffer dataBuffer) {
            this.longestDelimiter = NO_DELIMITER;
            for (int pos = dataBuffer.readPosition(); pos < dataBuffer.writePosition(); pos++) {
                byte b = dataBuffer.getByte(pos);
                for (NestedMatcher matcher : this.matchers) {
                    if (matcher.match(b) && matcher.delimiter().length > this.longestDelimiter.length) {
                        this.longestDelimiter = matcher.delimiter();
                    }
                }
                if (this.longestDelimiter != NO_DELIMITER) {
                    reset();
                    return pos;
                }
            }
            return -1;
        }

        @Override // org.springframework.core.io.buffer.DataBufferUtils.Matcher
        public byte[] delimiter() {
            Assert.state(this.longestDelimiter != NO_DELIMITER, "'delimiter' not set");
            return this.longestDelimiter;
        }

        @Override // org.springframework.core.io.buffer.DataBufferUtils.Matcher
        public void reset() {
            for (NestedMatcher matcher : this.matchers) {
                matcher.reset();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils$SingleByteMatcher.class */
    public static class SingleByteMatcher implements NestedMatcher {
        static final SingleByteMatcher NEWLINE_MATCHER = new SingleByteMatcher(new byte[]{10});
        private final byte[] delimiter;

        SingleByteMatcher(byte[] delimiter) {
            Assert.isTrue(delimiter.length == 1, "Expected a 1 byte delimiter");
            this.delimiter = delimiter;
        }

        @Override // org.springframework.core.io.buffer.DataBufferUtils.Matcher
        public int match(DataBuffer dataBuffer) {
            for (int pos = dataBuffer.readPosition(); pos < dataBuffer.writePosition(); pos++) {
                byte b = dataBuffer.getByte(pos);
                if (match(b)) {
                    return pos;
                }
            }
            return -1;
        }

        @Override // org.springframework.core.io.buffer.DataBufferUtils.NestedMatcher
        public boolean match(byte b) {
            return this.delimiter[0] == b;
        }

        @Override // org.springframework.core.io.buffer.DataBufferUtils.Matcher
        public byte[] delimiter() {
            return this.delimiter;
        }

        @Override // org.springframework.core.io.buffer.DataBufferUtils.Matcher
        public void reset() {
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils$AbstractNestedMatcher.class */
    private static abstract class AbstractNestedMatcher implements NestedMatcher {
        private final byte[] delimiter;
        private int matches = 0;

        protected AbstractNestedMatcher(byte[] delimiter) {
            this.delimiter = delimiter;
        }

        protected void setMatches(int index) {
            this.matches = index;
        }

        protected int getMatches() {
            return this.matches;
        }

        @Override // org.springframework.core.io.buffer.DataBufferUtils.Matcher
        public int match(DataBuffer dataBuffer) {
            for (int pos = dataBuffer.readPosition(); pos < dataBuffer.writePosition(); pos++) {
                byte b = dataBuffer.getByte(pos);
                if (match(b)) {
                    reset();
                    return pos;
                }
            }
            return -1;
        }

        @Override // org.springframework.core.io.buffer.DataBufferUtils.NestedMatcher
        public boolean match(byte b) {
            if (b == this.delimiter[this.matches]) {
                this.matches++;
                return this.matches == delimiter().length;
            }
            return false;
        }

        @Override // org.springframework.core.io.buffer.DataBufferUtils.Matcher
        public byte[] delimiter() {
            return this.delimiter;
        }

        @Override // org.springframework.core.io.buffer.DataBufferUtils.Matcher
        public void reset() {
            this.matches = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils$TwoByteMatcher.class */
    public static class TwoByteMatcher extends AbstractNestedMatcher {
        protected TwoByteMatcher(byte[] delimiter) {
            super(delimiter);
            Assert.isTrue(delimiter.length == 2, "Expected a 2-byte delimiter");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils$KnuthMorrisPrattMatcher.class */
    public static class KnuthMorrisPrattMatcher extends AbstractNestedMatcher {
        private final int[] table;

        public KnuthMorrisPrattMatcher(byte[] delimiter) {
            super(delimiter);
            this.table = longestSuffixPrefixTable(delimiter);
        }

        private static int[] longestSuffixPrefixTable(byte[] delimiter) {
            int j;
            int[] result = new int[delimiter.length];
            result[0] = 0;
            for (int i = 1; i < delimiter.length; i++) {
                int i2 = result[i - 1];
                while (true) {
                    j = i2;
                    if (j <= 0 || delimiter[i] == delimiter[j]) {
                        break;
                    }
                    i2 = result[j - 1];
                }
                if (delimiter[i] == delimiter[j]) {
                    j++;
                }
                result[i] = j;
            }
            return result;
        }

        @Override // org.springframework.core.io.buffer.DataBufferUtils.AbstractNestedMatcher, org.springframework.core.io.buffer.DataBufferUtils.NestedMatcher
        public boolean match(byte b) {
            while (getMatches() > 0 && b != delimiter()[getMatches()]) {
                setMatches(this.table[getMatches() - 1]);
            }
            return super.match(b);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils$ReadableByteChannelGenerator.class */
    public static class ReadableByteChannelGenerator implements Consumer<SynchronousSink<DataBuffer>> {
        private final ReadableByteChannel channel;
        private final DataBufferFactory dataBufferFactory;
        private final int bufferSize;

        public ReadableByteChannelGenerator(ReadableByteChannel channel, DataBufferFactory dataBufferFactory, int bufferSize) {
            this.channel = channel;
            this.dataBufferFactory = dataBufferFactory;
            this.bufferSize = bufferSize;
        }

        @Override // java.util.function.Consumer
        public void accept(SynchronousSink<DataBuffer> sink) {
            DataBuffer dataBuffer = this.dataBufferFactory.allocateBuffer(this.bufferSize);
            try {
                try {
                    DataBuffer.ByteBufferIterator iterator = dataBuffer.writableByteBuffers();
                    try {
                        Assert.state(iterator.hasNext(), "No ByteBuffer available");
                        ByteBuffer byteBuffer = iterator.next();
                        int read = this.channel.read(byteBuffer);
                        if (iterator != null) {
                            iterator.close();
                        }
                        if (read >= 0) {
                            dataBuffer.writePosition(read);
                            sink.next(dataBuffer);
                        } else {
                            sink.complete();
                        }
                        if (read == -1) {
                            DataBufferUtils.release(dataBuffer);
                        }
                    } catch (Throwable th) {
                        if (iterator != null) {
                            try {
                                iterator.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } catch (IOException ex) {
                    sink.error(ex);
                    if (-1 == -1) {
                        DataBufferUtils.release(dataBuffer);
                    }
                }
            } catch (Throwable th3) {
                if (-1 == -1) {
                    DataBufferUtils.release(dataBuffer);
                }
                throw th3;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils$ReadCompletionHandler.class */
    public static class ReadCompletionHandler implements CompletionHandler<Integer, Attachment> {
        private final AsynchronousFileChannel channel;
        private final FluxSink<DataBuffer> sink;
        private final DataBufferFactory dataBufferFactory;
        private final int bufferSize;
        private final AtomicLong position;
        private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils$ReadCompletionHandler$State.class */
        public enum State {
            IDLE,
            READING,
            DISPOSED
        }

        public ReadCompletionHandler(AsynchronousFileChannel channel, FluxSink<DataBuffer> sink, long position, DataBufferFactory dataBufferFactory, int bufferSize) {
            this.channel = channel;
            this.sink = sink;
            this.position = new AtomicLong(position);
            this.dataBufferFactory = dataBufferFactory;
            this.bufferSize = bufferSize;
        }

        public void request(long n) {
            tryRead();
        }

        public void cancel() {
            this.state.getAndSet(State.DISPOSED);
            DataBufferUtils.closeChannel(this.channel);
        }

        private void tryRead() {
            if (this.sink.requestedFromDownstream() > 0 && this.state.compareAndSet(State.IDLE, State.READING)) {
                read();
            }
        }

        private void read() {
            DataBuffer dataBuffer = this.dataBufferFactory.allocateBuffer(this.bufferSize);
            DataBuffer.ByteBufferIterator iterator = dataBuffer.writableByteBuffers();
            Assert.state(iterator.hasNext(), "No ByteBuffer available");
            ByteBuffer byteBuffer = iterator.next();
            Attachment attachment = new Attachment(dataBuffer, iterator);
            this.channel.read(byteBuffer, this.position.get(), attachment, this);
        }

        @Override // java.nio.channels.CompletionHandler
        public void completed(Integer read, Attachment attachment) {
            attachment.iterator().close();
            DataBuffer dataBuffer = attachment.dataBuffer();
            if (this.state.get().equals(State.DISPOSED)) {
                DataBufferUtils.release(dataBuffer);
                DataBufferUtils.closeChannel(this.channel);
                return;
            }
            if (read.intValue() == -1) {
                DataBufferUtils.release(dataBuffer);
                DataBufferUtils.closeChannel(this.channel);
                this.state.set(State.DISPOSED);
                this.sink.complete();
                return;
            }
            this.position.addAndGet(read.intValue());
            dataBuffer.writePosition(read.intValue());
            this.sink.next(dataBuffer);
            if (this.sink.requestedFromDownstream() > 0) {
                read();
            } else if (this.state.compareAndSet(State.READING, State.IDLE)) {
                tryRead();
            }
        }

        @Override // java.nio.channels.CompletionHandler
        public void failed(Throwable exc, Attachment attachment) {
            attachment.iterator().close();
            DataBufferUtils.release(attachment.dataBuffer());
            DataBufferUtils.closeChannel(this.channel);
            this.state.set(State.DISPOSED);
            this.sink.error(exc);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils$ReadCompletionHandler$Attachment.class */
        public static final class Attachment extends Record {
            private final DataBuffer dataBuffer;
            private final DataBuffer.ByteBufferIterator iterator;

            private Attachment(DataBuffer dataBuffer, DataBuffer.ByteBufferIterator iterator) {
                this.dataBuffer = dataBuffer;
                this.iterator = iterator;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Attachment.class), Attachment.class, "dataBuffer;iterator", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$ReadCompletionHandler$Attachment;->dataBuffer:Lorg/springframework/core/io/buffer/DataBuffer;", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$ReadCompletionHandler$Attachment;->iterator:Lorg/springframework/core/io/buffer/DataBuffer$ByteBufferIterator;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Attachment.class), Attachment.class, "dataBuffer;iterator", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$ReadCompletionHandler$Attachment;->dataBuffer:Lorg/springframework/core/io/buffer/DataBuffer;", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$ReadCompletionHandler$Attachment;->iterator:Lorg/springframework/core/io/buffer/DataBuffer$ByteBufferIterator;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Attachment.class, Object.class), Attachment.class, "dataBuffer;iterator", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$ReadCompletionHandler$Attachment;->dataBuffer:Lorg/springframework/core/io/buffer/DataBuffer;", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$ReadCompletionHandler$Attachment;->iterator:Lorg/springframework/core/io/buffer/DataBuffer$ByteBufferIterator;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public DataBuffer dataBuffer() {
                return this.dataBuffer;
            }

            public DataBuffer.ByteBufferIterator iterator() {
                return this.iterator;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils$WritableByteChannelSubscriber.class */
    public static class WritableByteChannelSubscriber extends BaseSubscriber<DataBuffer> {
        private final FluxSink<DataBuffer> sink;
        private final WritableByteChannel channel;

        public WritableByteChannelSubscriber(FluxSink<DataBuffer> sink, WritableByteChannel channel) {
            this.sink = sink;
            this.channel = channel;
        }

        protected void hookOnSubscribe(Subscription subscription) {
            request(1L);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void hookOnNext(DataBuffer dataBuffer) {
            try {
                DataBuffer.ByteBufferIterator iterator = dataBuffer.readableByteBuffers();
                try {
                    ByteBuffer byteBuffer = iterator.next();
                    while (byteBuffer.hasRemaining()) {
                        this.channel.write(byteBuffer);
                    }
                    if (iterator != null) {
                        iterator.close();
                    }
                    this.sink.next(dataBuffer);
                    request(1L);
                } finally {
                }
            } catch (IOException ex) {
                this.sink.next(dataBuffer);
                this.sink.error(ex);
            }
        }

        protected void hookOnError(Throwable throwable) {
            this.sink.error(throwable);
        }

        protected void hookOnComplete() {
            this.sink.complete();
        }

        public Context currentContext() {
            return Context.of(this.sink.contextView());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils$WriteCompletionHandler.class */
    public static class WriteCompletionHandler extends BaseSubscriber<DataBuffer> implements CompletionHandler<Integer, Attachment> {
        private final FluxSink<DataBuffer> sink;
        private final AsynchronousFileChannel channel;
        private final AtomicBoolean writing = new AtomicBoolean();
        private final AtomicBoolean completed = new AtomicBoolean();
        private final AtomicReference<Throwable> error = new AtomicReference<>();
        private final AtomicLong position;

        public WriteCompletionHandler(FluxSink<DataBuffer> sink, AsynchronousFileChannel channel, long position) {
            this.sink = sink;
            this.channel = channel;
            this.position = new AtomicLong(position);
        }

        protected void hookOnSubscribe(Subscription subscription) {
            request(1L);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void hookOnNext(DataBuffer dataBuffer) {
            DataBuffer.ByteBufferIterator iterator = dataBuffer.readableByteBuffers();
            if (iterator.hasNext()) {
                ByteBuffer byteBuffer = iterator.next();
                long pos = this.position.get();
                Attachment attachment = new Attachment(byteBuffer, dataBuffer, iterator);
                this.writing.set(true);
                this.channel.write(byteBuffer, pos, attachment, this);
            }
        }

        protected void hookOnError(Throwable throwable) {
            this.error.set(throwable);
            if (!this.writing.get()) {
                this.sink.error(throwable);
            }
        }

        protected void hookOnComplete() {
            this.completed.set(true);
            if (!this.writing.get()) {
                this.sink.complete();
            }
        }

        @Override // java.nio.channels.CompletionHandler
        public void completed(Integer written, Attachment attachment) {
            DataBuffer.ByteBufferIterator iterator = attachment.iterator();
            iterator.close();
            long pos = this.position.addAndGet(written.intValue());
            ByteBuffer byteBuffer = attachment.byteBuffer();
            if (byteBuffer.hasRemaining()) {
                this.channel.write(byteBuffer, pos, attachment, this);
                return;
            }
            if (iterator.hasNext()) {
                ByteBuffer next = iterator.next();
                this.channel.write(next, pos, attachment, this);
                return;
            }
            this.sink.next(attachment.dataBuffer());
            this.writing.set(false);
            Throwable throwable = this.error.get();
            if (throwable != null) {
                this.sink.error(throwable);
            } else if (this.completed.get()) {
                this.sink.complete();
            } else {
                request(1L);
            }
        }

        @Override // java.nio.channels.CompletionHandler
        public void failed(Throwable exc, Attachment attachment) {
            attachment.iterator().close();
            this.sink.next(attachment.dataBuffer());
            this.writing.set(false);
            this.sink.error(exc);
        }

        public Context currentContext() {
            return Context.of(this.sink.contextView());
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferUtils$WriteCompletionHandler$Attachment.class */
        public static final class Attachment extends Record {
            private final ByteBuffer byteBuffer;
            private final DataBuffer dataBuffer;
            private final DataBuffer.ByteBufferIterator iterator;

            private Attachment(ByteBuffer byteBuffer, DataBuffer dataBuffer, DataBuffer.ByteBufferIterator iterator) {
                this.byteBuffer = byteBuffer;
                this.dataBuffer = dataBuffer;
                this.iterator = iterator;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Attachment.class), Attachment.class, "byteBuffer;dataBuffer;iterator", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$WriteCompletionHandler$Attachment;->byteBuffer:Ljava/nio/ByteBuffer;", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$WriteCompletionHandler$Attachment;->dataBuffer:Lorg/springframework/core/io/buffer/DataBuffer;", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$WriteCompletionHandler$Attachment;->iterator:Lorg/springframework/core/io/buffer/DataBuffer$ByteBufferIterator;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Attachment.class), Attachment.class, "byteBuffer;dataBuffer;iterator", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$WriteCompletionHandler$Attachment;->byteBuffer:Ljava/nio/ByteBuffer;", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$WriteCompletionHandler$Attachment;->dataBuffer:Lorg/springframework/core/io/buffer/DataBuffer;", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$WriteCompletionHandler$Attachment;->iterator:Lorg/springframework/core/io/buffer/DataBuffer$ByteBufferIterator;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Attachment.class, Object.class), Attachment.class, "byteBuffer;dataBuffer;iterator", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$WriteCompletionHandler$Attachment;->byteBuffer:Ljava/nio/ByteBuffer;", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$WriteCompletionHandler$Attachment;->dataBuffer:Lorg/springframework/core/io/buffer/DataBuffer;", "FIELD:Lorg/springframework/core/io/buffer/DataBufferUtils$WriteCompletionHandler$Attachment;->iterator:Lorg/springframework/core/io/buffer/DataBuffer$ByteBufferIterator;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public ByteBuffer byteBuffer() {
                return this.byteBuffer;
            }

            public DataBuffer dataBuffer() {
                return this.dataBuffer;
            }

            public DataBuffer.ByteBufferIterator iterator() {
                return this.iterator;
            }
        }
    }
}
