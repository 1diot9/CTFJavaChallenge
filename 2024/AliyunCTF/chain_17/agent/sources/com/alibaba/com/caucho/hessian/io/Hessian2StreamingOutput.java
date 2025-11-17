package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/Hessian2StreamingOutput.class */
public class Hessian2StreamingOutput {
    private Hessian2Output _out;

    public Hessian2StreamingOutput(OutputStream os) {
        this._out = new Hessian2Output(os);
    }

    public boolean isCloseStreamOnClose() {
        return this._out.isCloseStreamOnClose();
    }

    public void setCloseStreamOnClose(boolean isClose) {
        this._out.setCloseStreamOnClose(isClose);
    }

    public void writeObject(Object object) throws IOException {
        this._out.writeStreamingObject(object);
    }

    public void flush() throws IOException {
        this._out.flush();
    }

    public void close() throws IOException {
        this._out.close();
    }
}
