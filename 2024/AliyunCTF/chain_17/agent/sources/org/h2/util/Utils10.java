package org.h2.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/Utils10.class */
public final class Utils10 {
    public static String byteArrayOutputStreamToString(ByteArrayOutputStream byteArrayOutputStream, Charset charset) {
        try {
            return byteArrayOutputStream.toString(charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean getTcpQuickack(Socket socket) throws IOException {
        throw new UnsupportedOperationException();
    }

    public static boolean setTcpQuickack(Socket socket, boolean z) {
        return false;
    }

    private Utils10() {
    }
}
