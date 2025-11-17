package org.springframework.http.codec.multipart;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/MultipartUtils.class */
public abstract class MultipartUtils {
    MultipartUtils() {
    }

    public static Charset charset(HttpHeaders headers) {
        Charset charset;
        MediaType contentType = headers.getContentType();
        if (contentType != null && (charset = contentType.getCharset()) != null) {
            return charset;
        }
        return StandardCharsets.UTF_8;
    }

    @Nullable
    public static byte[] boundary(HttpMessage message, Charset headersCharset) {
        MediaType contentType = message.getHeaders().getContentType();
        if (contentType != null) {
            String boundary = contentType.getParameter("boundary");
            if (boundary != null) {
                int len = boundary.length();
                if (len > 2 && boundary.charAt(0) == '\"' && boundary.charAt(len - 1) == '\"') {
                    boundary = boundary.substring(1, len - 1);
                }
                return boundary.getBytes(headersCharset);
            }
            return null;
        }
        return null;
    }

    public static byte[] concat(byte[]... byteArrays) {
        int len = 0;
        for (byte[] bArr : byteArrays) {
            len += bArr.length;
        }
        byte[] result = new byte[len];
        int len2 = 0;
        for (byte[] byteArray : byteArrays) {
            System.arraycopy(byteArray, 0, result, len2, byteArray.length);
            len2 += byteArray.length;
        }
        return result;
    }

    public static void closeChannel(Channel channel) {
        try {
            if (channel.isOpen()) {
                channel.close();
            }
        } catch (IOException e) {
        }
    }

    public static void deleteFile(Path file) {
        try {
            Files.delete(file);
        } catch (IOException e) {
        }
    }

    public static boolean isFormField(HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        return (contentType == null || MediaType.TEXT_PLAIN.equalsTypeAndSubtype(contentType)) && headers.getContentDisposition().getFilename() == null;
    }
}
