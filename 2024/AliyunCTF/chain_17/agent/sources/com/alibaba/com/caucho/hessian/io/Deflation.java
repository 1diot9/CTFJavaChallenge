package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/Deflation.class */
public class Deflation extends HessianEnvelope {
    @Override // com.alibaba.com.caucho.hessian.io.HessianEnvelope
    public Hessian2Output wrap(Hessian2Output out) throws IOException {
        OutputStream os = new DeflateOutputStream(out);
        Hessian2Output filterOut = new Hessian2Output(os);
        filterOut.setCloseStreamOnClose(true);
        return filterOut;
    }

    @Override // com.alibaba.com.caucho.hessian.io.HessianEnvelope
    public Hessian2Input unwrap(Hessian2Input in) throws IOException {
        in.readEnvelope();
        String method = in.readMethod();
        if (!method.equals(getClass().getName())) {
            throw new IOException("expected hessian Envelope method '" + getClass().getName() + "' at '" + method + "'");
        }
        return unwrapHeaders(in);
    }

    @Override // com.alibaba.com.caucho.hessian.io.HessianEnvelope
    public Hessian2Input unwrapHeaders(Hessian2Input in) throws IOException {
        InputStream is = new DeflateInputStream(in);
        Hessian2Input filter = new Hessian2Input(is);
        filter.setCloseStreamOnClose(true);
        return filter;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/Deflation$DeflateOutputStream.class */
    static class DeflateOutputStream extends OutputStream {
        private Hessian2Output _out;
        private OutputStream _bodyOut;
        private DeflaterOutputStream _deflateOut;

        DeflateOutputStream(Hessian2Output out) throws IOException {
            this._out = out;
            this._out.startEnvelope(Deflation.class.getName());
            this._out.writeInt(0);
            this._bodyOut = this._out.getBytesOutputStream();
            this._deflateOut = new DeflaterOutputStream(this._bodyOut);
        }

        @Override // java.io.OutputStream
        public void write(int ch2) throws IOException {
            this._deflateOut.write(ch2);
        }

        @Override // java.io.OutputStream
        public void write(byte[] buffer, int offset, int length) throws IOException {
            this._deflateOut.write(buffer, offset, length);
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            Hessian2Output out = this._out;
            this._out = null;
            if (out != null) {
                this._deflateOut.close();
                this._bodyOut.close();
                out.writeInt(0);
                out.completeEnvelope();
                out.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/Deflation$DeflateInputStream.class */
    public static class DeflateInputStream extends InputStream {
        private Hessian2Input _in;
        private InputStream _bodyIn;
        private InflaterInputStream _inflateIn;

        DeflateInputStream(Hessian2Input in) throws IOException {
            this._in = in;
            int len = in.readInt();
            if (len != 0) {
                throw new IOException("expected no headers");
            }
            this._bodyIn = this._in.readInputStream();
            this._inflateIn = new InflaterInputStream(this._bodyIn);
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            return this._inflateIn.read();
        }

        @Override // java.io.InputStream
        public int read(byte[] buffer, int offset, int length) throws IOException {
            return this._inflateIn.read(buffer, offset, length);
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            Hessian2Input in = this._in;
            this._in = null;
            if (in != null) {
                this._inflateIn.close();
                this._bodyIn.close();
                int len = in.readInt();
                if (len != 0) {
                    throw new IOException("Unexpected footer");
                }
                in.completeEnvelope();
                in.close();
            }
        }
    }
}
