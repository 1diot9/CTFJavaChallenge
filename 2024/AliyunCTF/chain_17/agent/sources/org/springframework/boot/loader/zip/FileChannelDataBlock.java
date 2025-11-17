package org.springframework.boot.loader.zip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;
import org.springframework.boot.loader.log.DebugLogger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:org/springframework/boot/loader/zip/FileChannelDataBlock.class */
public class FileChannelDataBlock implements CloseableDataBlock {
    private static final DebugLogger debug = DebugLogger.get(FileChannelDataBlock.class);
    static Tracker tracker;
    private final ManagedFileChannel channel;
    private final long offset;
    private final long size;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:org/springframework/boot/loader/zip/FileChannelDataBlock$Tracker.class */
    public interface Tracker {
        void openedFileChannel(Path path, FileChannel fileChannel);

        void closedFileChannel(Path path, FileChannel fileChannel);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileChannelDataBlock(Path path) throws IOException {
        this.channel = new ManagedFileChannel(path);
        this.offset = 0L;
        this.size = Files.size(path);
    }

    FileChannelDataBlock(ManagedFileChannel channel, long offset, long size) {
        this.channel = channel;
        this.offset = offset;
        this.size = size;
    }

    @Override // org.springframework.boot.loader.zip.DataBlock
    public long size() throws IOException {
        return this.size;
    }

    @Override // org.springframework.boot.loader.zip.DataBlock
    public int read(ByteBuffer dst, long pos) throws IOException {
        if (pos < 0) {
            throw new IllegalArgumentException("Position must not be negative");
        }
        ensureOpen(ClosedChannelException::new);
        int remaining = (int) (this.size - pos);
        if (remaining <= 0) {
            return -1;
        }
        int originalDestinationLimit = -1;
        if (dst.remaining() > remaining) {
            originalDestinationLimit = dst.limit();
            dst.limit(dst.position() + remaining);
        }
        int result = this.channel.read(dst, this.offset + pos);
        if (originalDestinationLimit != -1) {
            dst.limit(originalDestinationLimit);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void open() throws IOException {
        this.channel.open();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.channel.close();
    }

    <E extends Exception> void ensureOpen(Supplier<E> exceptionSupplier) throws Exception {
        this.channel.ensureOpen(exceptionSupplier);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileChannelDataBlock slice(long offset) throws IOException {
        return slice(offset, this.size - offset);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileChannelDataBlock slice(long offset, long size) {
        if (offset == 0 && size == this.size) {
            return this;
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must not be negative");
        }
        if (size < 0 || offset + size > this.size) {
            throw new IllegalArgumentException("Size must not be negative and must be within bounds");
        }
        debug.log("Slicing %s at %s with size %s", this.channel, Long.valueOf(offset), Long.valueOf(size));
        return new FileChannelDataBlock(this.channel, this.offset + offset, size);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:org/springframework/boot/loader/zip/FileChannelDataBlock$ManagedFileChannel.class */
    public static class ManagedFileChannel {
        static final int BUFFER_SIZE = 10240;
        private final Path path;
        private int referenceCount;
        private FileChannel fileChannel;
        private ByteBuffer buffer;
        private int bufferSize;
        private long bufferPosition = -1;
        private final Object lock = new Object();

        ManagedFileChannel(Path path) {
            if (!Files.isRegularFile(path, new LinkOption[0])) {
                throw new IllegalArgumentException(path + " must be a regular file");
            }
            this.path = path;
        }

        int read(ByteBuffer dst, long position) throws IOException {
            synchronized (this.lock) {
                if (position < this.bufferPosition || position >= this.bufferPosition + this.bufferSize) {
                    fillBuffer(position);
                }
                if (this.bufferSize <= 0) {
                    return this.bufferSize;
                }
                int offset = (int) (position - this.bufferPosition);
                int length = Math.min(this.bufferSize - offset, dst.remaining());
                dst.put(dst.position(), this.buffer, offset, length);
                dst.position(dst.position() + length);
                return length;
            }
        }

        private void fillBuffer(long position) throws IOException {
            int i = 0;
            while (i < 10) {
                boolean interrupted = i != 0 ? Thread.interrupted() : false;
                try {
                    this.buffer.clear();
                    this.bufferSize = this.fileChannel.read(this.buffer, position);
                    this.bufferPosition = position;
                    if (!interrupted) {
                        return;
                    }
                    Thread.currentThread().interrupt();
                    return;
                } catch (ClosedByInterruptException e) {
                    try {
                        repairFileChannel();
                        if (interrupted) {
                            Thread.currentThread().interrupt();
                        }
                        i++;
                    } catch (Throwable th) {
                        if (interrupted) {
                            Thread.currentThread().interrupt();
                        }
                        throw th;
                    }
                }
            }
            throw new ClosedByInterruptException();
        }

        private void repairFileChannel() throws IOException {
            if (FileChannelDataBlock.tracker != null) {
                FileChannelDataBlock.tracker.closedFileChannel(this.path, this.fileChannel);
            }
            this.fileChannel = FileChannel.open(this.path, StandardOpenOption.READ);
            if (FileChannelDataBlock.tracker != null) {
                FileChannelDataBlock.tracker.openedFileChannel(this.path, this.fileChannel);
            }
        }

        void open() throws IOException {
            synchronized (this.lock) {
                if (this.referenceCount == 0) {
                    FileChannelDataBlock.debug.log("Opening '%s'", this.path);
                    this.fileChannel = FileChannel.open(this.path, StandardOpenOption.READ);
                    this.buffer = ByteBuffer.allocateDirect(10240);
                    if (FileChannelDataBlock.tracker != null) {
                        FileChannelDataBlock.tracker.openedFileChannel(this.path, this.fileChannel);
                    }
                }
                this.referenceCount++;
                FileChannelDataBlock.debug.log("Reference count for '%s' incremented to %s", this.path, Integer.valueOf(this.referenceCount));
            }
        }

        void close() throws IOException {
            synchronized (this.lock) {
                if (this.referenceCount == 0) {
                    return;
                }
                this.referenceCount--;
                if (this.referenceCount == 0) {
                    FileChannelDataBlock.debug.log("Closing '%s'", this.path);
                    this.buffer = null;
                    this.bufferPosition = -1L;
                    this.bufferSize = 0;
                    this.fileChannel.close();
                    if (FileChannelDataBlock.tracker != null) {
                        FileChannelDataBlock.tracker.closedFileChannel(this.path, this.fileChannel);
                    }
                    this.fileChannel = null;
                }
                FileChannelDataBlock.debug.log("Reference count for '%s' decremented to %s", this.path, Integer.valueOf(this.referenceCount));
            }
        }

        <E extends Exception> void ensureOpen(Supplier<E> exceptionSupplier) throws Exception {
            synchronized (this.lock) {
                if (this.referenceCount == 0 || !this.fileChannel.isOpen()) {
                    throw exceptionSupplier.get();
                }
            }
        }

        public String toString() {
            return this.path.toString();
        }
    }
}
