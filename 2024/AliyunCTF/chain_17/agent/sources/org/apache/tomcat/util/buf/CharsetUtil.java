package org.apache.tomcat.util.buf;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/CharsetUtil.class */
public class CharsetUtil {
    private CharsetUtil() {
    }

    public static boolean isAsciiSuperset(Charset charset) {
        CharsetDecoder decoder = charset.newDecoder();
        ByteBuffer inBytes = ByteBuffer.allocate(1);
        for (int i = 0; i < 128; i++) {
            inBytes.clear();
            inBytes.put((byte) i);
            inBytes.flip();
            try {
                CharBuffer outChars = decoder.decode(inBytes);
                try {
                    if (outChars.get() != i) {
                        return false;
                    }
                } catch (BufferUnderflowException e) {
                    return false;
                }
            } catch (CharacterCodingException e2) {
                return false;
            }
        }
        return true;
    }
}
