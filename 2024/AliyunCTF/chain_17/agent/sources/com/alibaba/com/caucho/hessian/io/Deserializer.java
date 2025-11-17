package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/Deserializer.class */
public interface Deserializer {
    Class getType();

    Object readObject(AbstractHessianInput abstractHessianInput) throws IOException;

    Object readList(AbstractHessianInput abstractHessianInput, int i) throws IOException;

    Object readList(AbstractHessianInput abstractHessianInput, int i, Class<?> cls) throws IOException;

    Object readLengthList(AbstractHessianInput abstractHessianInput, int i) throws IOException;

    Object readLengthList(AbstractHessianInput abstractHessianInput, int i, Class<?> cls) throws IOException;

    Object readMap(AbstractHessianInput abstractHessianInput) throws IOException;

    Object readMap(AbstractHessianInput abstractHessianInput, Class<?> cls, Class<?> cls2) throws IOException;

    Object readObject(AbstractHessianInput abstractHessianInput, String[] strArr) throws IOException;
}
