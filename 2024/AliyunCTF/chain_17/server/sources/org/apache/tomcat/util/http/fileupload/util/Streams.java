package org.apache.tomcat.util.http.fileupload.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/util/Streams.class */
public final class Streams {
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    private Streams() {
    }

    public static long copy(InputStream inputStream, OutputStream outputStream, boolean closeOutputStream) throws IOException {
        return copy(inputStream, outputStream, closeOutputStream, new byte[8192]);
    }

    public static long copy(InputStream inputStream, OutputStream outputStream, boolean closeOutputStream, byte[] buffer) throws IOException {
        long total = 0;
        while (true) {
            try {
                try {
                    int res = inputStream.read(buffer);
                    if (res == -1) {
                        break;
                    }
                    if (res > 0) {
                        total += res;
                        if (outputStream != null) {
                            outputStream.write(buffer, 0, res);
                        }
                    }
                } finally {
                }
            } catch (Throwable th) {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        if (outputStream != null) {
            if (closeOutputStream) {
                outputStream.close();
            } else {
                outputStream.flush();
            }
        }
        inputStream.close();
        long j = total;
        if (inputStream != null) {
            inputStream.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
        return j;
    }

    public static String checkFileName(String fileName) {
        if (fileName != null && fileName.indexOf(0) != -1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fileName.length(); i++) {
                char c = fileName.charAt(i);
                switch (c) {
                    case 0:
                        sb.append("\\0");
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            }
            throw new InvalidFileNameException(fileName, "Invalid file name: " + sb);
        }
        return fileName;
    }
}
