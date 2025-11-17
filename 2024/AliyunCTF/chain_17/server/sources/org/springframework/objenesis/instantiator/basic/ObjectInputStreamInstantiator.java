package org.springframework.objenesis.instantiator.basic;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import org.springframework.asm.Opcodes;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.SERIALIZATION)
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/basic/ObjectInputStreamInstantiator.class */
public class ObjectInputStreamInstantiator<T> implements ObjectInstantiator<T> {
    private final ObjectInputStream inputStream;

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/basic/ObjectInputStreamInstantiator$MockStream.class */
    private static class MockStream extends InputStream {
        private static final int[] NEXT = {1, 2, 2};
        private final byte[][] buffers;
        private static byte[] HEADER;
        private static byte[] REPEATING_DATA;
        private int pointer = 0;
        private int sequence = 0;
        private byte[] data = HEADER;

        static {
            initialize();
        }

        private static void initialize() {
            try {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                DataOutputStream dout = new DataOutputStream(byteOut);
                dout.writeShort(-21267);
                dout.writeShort(5);
                HEADER = byteOut.toByteArray();
                ByteArrayOutputStream byteOut2 = new ByteArrayOutputStream();
                DataOutputStream dout2 = new DataOutputStream(byteOut2);
                dout2.writeByte(115);
                dout2.writeByte(Opcodes.LREM);
                dout2.writeInt(8257536);
                REPEATING_DATA = byteOut2.toByteArray();
            } catch (IOException e) {
                throw new Error("IOException: " + e.getMessage());
            }
        }

        /* JADX WARN: Type inference failed for: r1v18, types: [byte[], byte[][]] */
        public MockStream(Class<?> clazz) {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(byteOut);
            try {
                dout.writeByte(115);
                dout.writeByte(Opcodes.FREM);
                dout.writeUTF(clazz.getName());
                dout.writeLong(ObjectStreamClass.lookup(clazz).getSerialVersionUID());
                dout.writeByte(2);
                dout.writeShort(0);
                dout.writeByte(120);
                dout.writeByte(112);
                byte[] firstData = byteOut.toByteArray();
                this.buffers = new byte[]{HEADER, firstData, REPEATING_DATA};
            } catch (IOException e) {
                throw new Error("IOException: " + e.getMessage());
            }
        }

        private void advanceBuffer() {
            this.pointer = 0;
            this.sequence = NEXT[this.sequence];
            this.data = this.buffers[this.sequence];
        }

        @Override // java.io.InputStream
        public int read() {
            byte[] bArr = this.data;
            int i = this.pointer;
            this.pointer = i + 1;
            byte b = bArr[i];
            if (this.pointer >= this.data.length) {
                advanceBuffer();
            }
            return b;
        }

        @Override // java.io.InputStream
        public int available() {
            return Integer.MAX_VALUE;
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) {
            int left = len;
            int length = this.data.length;
            int i = this.pointer;
            while (true) {
                int remaining = length - i;
                if (remaining > left) {
                    break;
                }
                System.arraycopy(this.data, this.pointer, b, off, remaining);
                off += remaining;
                left -= remaining;
                advanceBuffer();
                length = this.data.length;
                i = this.pointer;
            }
            if (left > 0) {
                System.arraycopy(this.data, this.pointer, b, off, left);
                this.pointer += left;
            }
            return len;
        }
    }

    public ObjectInputStreamInstantiator(Class<T> clazz) {
        if (Serializable.class.isAssignableFrom(clazz)) {
            try {
                this.inputStream = new ObjectInputStream(new MockStream(clazz));
                return;
            } catch (IOException e) {
                throw new Error("IOException: " + e.getMessage());
            }
        }
        throw new ObjenesisException(new NotSerializableException(clazz + " not serializable"));
    }

    @Override // org.springframework.objenesis.instantiator.ObjectInstantiator
    public T newInstance() {
        try {
            return (T) this.inputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new Error("ClassNotFoundException: " + e.getMessage());
        } catch (Exception e2) {
            throw new ObjenesisException(e2);
        }
    }
}
