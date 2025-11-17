package org.apache.tomcat.util.net;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import org.apache.tomcat.util.buf.ByteBufferUtils;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SocketBufferHandler.class */
public class SocketBufferHandler {
    static SocketBufferHandler EMPTY = new SocketBufferHandler(0, 0, false) { // from class: org.apache.tomcat.util.net.SocketBufferHandler.1
        @Override // org.apache.tomcat.util.net.SocketBufferHandler
        public void expand(int newSize) {
        }

        @Override // org.apache.tomcat.util.net.SocketBufferHandler
        public void unReadReadBuffer(ByteBuffer returnedData) {
        }
    };
    private volatile ByteBuffer readBuffer;
    private volatile ByteBuffer writeBuffer;
    private final boolean direct;
    private volatile boolean readBufferConfiguredForWrite = true;
    private volatile boolean writeBufferConfiguredForWrite = true;

    public SocketBufferHandler(int readBufferSize, int writeBufferSize, boolean direct) {
        this.direct = direct;
        if (direct) {
            this.readBuffer = ByteBuffer.allocateDirect(readBufferSize);
            this.writeBuffer = ByteBuffer.allocateDirect(writeBufferSize);
        } else {
            this.readBuffer = ByteBuffer.allocate(readBufferSize);
            this.writeBuffer = ByteBuffer.allocate(writeBufferSize);
        }
    }

    public void configureReadBufferForWrite() {
        setReadBufferConfiguredForWrite(true);
    }

    public void configureReadBufferForRead() {
        setReadBufferConfiguredForWrite(false);
    }

    private void setReadBufferConfiguredForWrite(boolean readBufferConFiguredForWrite) {
        if (this.readBufferConfiguredForWrite != readBufferConFiguredForWrite) {
            if (readBufferConFiguredForWrite) {
                int remaining = this.readBuffer.remaining();
                if (remaining == 0) {
                    this.readBuffer.clear();
                } else {
                    this.readBuffer.compact();
                }
            } else {
                this.readBuffer.flip();
            }
            this.readBufferConfiguredForWrite = readBufferConFiguredForWrite;
        }
    }

    public ByteBuffer getReadBuffer() {
        return this.readBuffer;
    }

    public boolean isReadBufferEmpty() {
        return this.readBufferConfiguredForWrite ? this.readBuffer.position() == 0 : this.readBuffer.remaining() == 0;
    }

    public void unReadReadBuffer(ByteBuffer returnedData) {
        if (isReadBufferEmpty()) {
            configureReadBufferForWrite();
            this.readBuffer.put(returnedData);
            return;
        }
        int bytesReturned = returnedData.remaining();
        if (this.readBufferConfiguredForWrite) {
            if (this.readBuffer.position() + bytesReturned > this.readBuffer.capacity()) {
                throw new BufferOverflowException();
            }
            for (int i = 0; i < this.readBuffer.position(); i++) {
                this.readBuffer.put(i + bytesReturned, this.readBuffer.get(i));
            }
            for (int i2 = 0; i2 < bytesReturned; i2++) {
                this.readBuffer.put(i2, returnedData.get());
            }
            this.readBuffer.position(this.readBuffer.position() + bytesReturned);
            return;
        }
        int shiftRequired = bytesReturned - this.readBuffer.position();
        if (shiftRequired > 0) {
            if (this.readBuffer.capacity() - this.readBuffer.limit() < shiftRequired) {
                throw new BufferOverflowException();
            }
            int oldLimit = this.readBuffer.limit();
            this.readBuffer.limit(oldLimit + shiftRequired);
            for (int i3 = this.readBuffer.position(); i3 < oldLimit; i3++) {
                this.readBuffer.put(i3 + shiftRequired, this.readBuffer.get(i3));
            }
        } else {
            shiftRequired = 0;
        }
        int insertOffset = (this.readBuffer.position() + shiftRequired) - bytesReturned;
        for (int i4 = insertOffset; i4 < bytesReturned + insertOffset; i4++) {
            this.readBuffer.put(i4, returnedData.get());
        }
        this.readBuffer.position(insertOffset);
    }

    public void configureWriteBufferForWrite() {
        setWriteBufferConfiguredForWrite(true);
    }

    public void configureWriteBufferForRead() {
        setWriteBufferConfiguredForWrite(false);
    }

    private void setWriteBufferConfiguredForWrite(boolean writeBufferConfiguredForWrite) {
        if (this.writeBufferConfiguredForWrite != writeBufferConfiguredForWrite) {
            if (writeBufferConfiguredForWrite) {
                int remaining = this.writeBuffer.remaining();
                if (remaining == 0) {
                    this.writeBuffer.clear();
                } else {
                    this.writeBuffer.compact();
                    this.writeBuffer.position(remaining);
                    this.writeBuffer.limit(this.writeBuffer.capacity());
                }
            } else {
                this.writeBuffer.flip();
            }
            this.writeBufferConfiguredForWrite = writeBufferConfiguredForWrite;
        }
    }

    public boolean isWriteBufferWritable() {
        if (this.writeBufferConfiguredForWrite) {
            return this.writeBuffer.hasRemaining();
        }
        return this.writeBuffer.remaining() == 0;
    }

    public ByteBuffer getWriteBuffer() {
        return this.writeBuffer;
    }

    public boolean isWriteBufferEmpty() {
        return this.writeBufferConfiguredForWrite ? this.writeBuffer.position() == 0 : this.writeBuffer.remaining() == 0;
    }

    public void reset() {
        this.readBuffer.clear();
        this.readBufferConfiguredForWrite = true;
        this.writeBuffer.clear();
        this.writeBufferConfiguredForWrite = true;
    }

    public void expand(int newSize) {
        configureReadBufferForWrite();
        this.readBuffer = ByteBufferUtils.expand(this.readBuffer, newSize);
        configureWriteBufferForWrite();
        this.writeBuffer = ByteBufferUtils.expand(this.writeBuffer, newSize);
    }

    public void free() {
        if (this.direct) {
            ByteBufferUtils.cleanDirectBuffer(this.readBuffer);
            ByteBufferUtils.cleanDirectBuffer(this.writeBuffer);
        }
    }
}
