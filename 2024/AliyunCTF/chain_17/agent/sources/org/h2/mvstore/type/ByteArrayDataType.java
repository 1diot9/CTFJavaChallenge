package org.h2.mvstore.type;

import java.nio.ByteBuffer;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/ByteArrayDataType.class */
public final class ByteArrayDataType extends BasicDataType<byte[]> {
    public static final ByteArrayDataType INSTANCE = new ByteArrayDataType();

    private ByteArrayDataType() {
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public int getMemory(byte[] bArr) {
        return bArr.length;
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public void write(WriteBuffer writeBuffer, byte[] bArr) {
        writeBuffer.putVarInt(bArr.length);
        writeBuffer.put(bArr);
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public byte[] read(ByteBuffer byteBuffer) {
        byte[] bArr = new byte[DataUtils.readVarInt(byteBuffer)];
        byteBuffer.get(bArr);
        return bArr;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [byte[], byte[][]] */
    @Override // org.h2.mvstore.type.DataType
    public byte[][] createStorage(int i) {
        return new byte[i];
    }
}
