package org.springframework.util;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/FileCopyUtils.class */
public abstract class FileCopyUtils {
    public static final int BUFFER_SIZE = 8192;

    public static int copy(File in, File out) throws IOException {
        Assert.notNull(in, "No input File specified");
        Assert.notNull(out, "No output File specified");
        return copy(Files.newInputStream(in.toPath(), new OpenOption[0]), Files.newOutputStream(out.toPath(), new OpenOption[0]));
    }

    public static void copy(byte[] in, File out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No output File specified");
        copy(new ByteArrayInputStream(in), Files.newOutputStream(out.toPath(), new OpenOption[0]));
    }

    public static byte[] copyToByteArray(File in) throws IOException {
        Assert.notNull(in, "No input File specified");
        return copyToByteArray(Files.newInputStream(in.toPath(), new OpenOption[0]));
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");
        try {
            try {
                int count = (int) in.transferTo(out);
                out.flush();
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                return count;
            } finally {
            }
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static void copy(byte[] in, OutputStream out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No OutputStream specified");
        try {
            out.write(in);
        } finally {
            close(out);
        }
    }

    public static byte[] copyToByteArray(@Nullable InputStream in) throws IOException {
        if (in == null) {
            return new byte[0];
        }
        try {
            byte[] readAllBytes = in.readAllBytes();
            if (in != null) {
                in.close();
            }
            return readAllBytes;
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static int copy(Reader in, Writer out) throws IOException {
        Assert.notNull(in, "No Reader specified");
        Assert.notNull(out, "No Writer specified");
        try {
            int charCount = 0;
            char[] buffer = new char[8192];
            while (true) {
                int charsRead = in.read(buffer);
                if (charsRead != -1) {
                    out.write(buffer, 0, charsRead);
                    charCount += charsRead;
                } else {
                    out.flush();
                    int i = charCount;
                    close(in);
                    close(out);
                    return i;
                }
            }
        } catch (Throwable th) {
            close(in);
            close(out);
            throw th;
        }
    }

    public static void copy(String in, Writer out) throws IOException {
        Assert.notNull(in, "No input String specified");
        Assert.notNull(out, "No Writer specified");
        try {
            out.write(in);
        } finally {
            close(out);
        }
    }

    public static String copyToString(@Nullable Reader in) throws IOException {
        if (in == null) {
            return "";
        }
        StringWriter out = new StringWriter(8192);
        copy(in, out);
        return out.toString();
    }

    private static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
        }
    }
}
