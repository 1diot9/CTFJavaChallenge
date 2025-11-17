package cn.hutool.core.net.multipart;

import cn.hutool.core.io.FastByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/net/multipart/MultipartRequestInputStream.class */
public class MultipartRequestInputStream extends BufferedInputStream {
    protected byte[] boundary;
    protected UploadFileHeader lastHeader;

    public MultipartRequestInputStream(InputStream in) {
        super(in);
    }

    public byte readByte() throws IOException {
        int i = super.read();
        if (i == -1) {
            throw new IOException("End of HTTP request stream reached");
        }
        return (byte) i;
    }

    public void skipBytes(long i) throws IOException {
        long len = super.skip(i);
        if (len != i) {
            throw new IOException("Unable to skip data in HTTP request");
        }
    }

    public byte[] readBoundary() throws IOException {
        byte b;
        ByteArrayOutputStream boundaryOutput = new ByteArrayOutputStream(1024);
        do {
            b = readByte();
        } while (b <= 32);
        boundaryOutput.write(b);
        while (true) {
            byte b2 = readByte();
            if (b2 == 13) {
                break;
            }
            boundaryOutput.write(b2);
        }
        if (boundaryOutput.size() == 0) {
            throw new IOException("Problems with parsing request: invalid boundary");
        }
        skipBytes(1L);
        this.boundary = new byte[boundaryOutput.size() + 2];
        System.arraycopy(boundaryOutput.toByteArray(), 0, this.boundary, 2, this.boundary.length - 2);
        this.boundary[0] = 13;
        this.boundary[1] = 10;
        return this.boundary;
    }

    public UploadFileHeader getLastHeader() {
        return this.lastHeader;
    }

    public UploadFileHeader readDataHeader(Charset encoding) throws IOException {
        String dataHeader = readDataHeaderString(encoding);
        if (dataHeader != null) {
            this.lastHeader = new UploadFileHeader(dataHeader);
        } else {
            this.lastHeader = null;
        }
        return this.lastHeader;
    }

    protected String readDataHeaderString(Charset charset) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        while (true) {
            byte b = readByte();
            if (b != 13) {
                data.write(b);
            } else {
                mark(4);
                skipBytes(1L);
                int i = read();
                if (i == -1) {
                    return null;
                }
                if (i == 13) {
                    reset();
                    skipBytes(3L);
                    return charset == null ? data.toString() : data.toString(charset.name());
                }
                reset();
                data.write(b);
            }
        }
    }

    public String readString(Charset charset) throws IOException {
        FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        copy(out);
        return out.toString(charset);
    }

    public long copy(OutputStream out) throws IOException {
        long j = 0;
        while (true) {
            long count = j;
            byte b = readByte();
            if (!isBoundary(b)) {
                out.write(b);
                j = count + 1;
            } else {
                return count;
            }
        }
    }

    public long copy(OutputStream out, long limit) throws IOException {
        long count = 0;
        do {
            byte b = readByte();
            if (isBoundary(b)) {
                break;
            }
            out.write(b);
            count++;
        } while (count <= limit);
        return count;
    }

    public long skipToBoundary() throws IOException {
        byte b;
        long count = 0;
        do {
            b = readByte();
            count++;
        } while (!isBoundary(b));
        return count;
    }

    public boolean isBoundary(byte b) throws IOException {
        int boundaryLen = this.boundary.length;
        mark(boundaryLen + 1);
        int bpos = 0;
        while (b == this.boundary[bpos]) {
            b = readByte();
            bpos++;
            if (bpos == boundaryLen) {
                return true;
            }
        }
        reset();
        return false;
    }
}
