package org.springframework.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/UpdateMessageDigestInputStream.class */
abstract class UpdateMessageDigestInputStream extends InputStream {
    public void updateMessageDigest(MessageDigest messageDigest) throws IOException {
        while (true) {
            int data = read();
            if (data != -1) {
                messageDigest.update((byte) data);
            } else {
                return;
            }
        }
    }

    public void updateMessageDigest(MessageDigest messageDigest, int len) throws IOException {
        int data;
        for (int bytesRead = 0; bytesRead < len && (data = read()) != -1; bytesRead++) {
            messageDigest.update((byte) data);
        }
    }
}
