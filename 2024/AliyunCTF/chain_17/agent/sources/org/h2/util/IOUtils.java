package org.h2.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import org.h2.engine.SysProperties;
import org.h2.mvstore.DataUtils;
import org.h2.store.fs.FileUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/IOUtils.class */
public class IOUtils {
    private IOUtils() {
    }

    public static void closeSilently(AutoCloseable autoCloseable) {
        if (autoCloseable != null) {
            try {
                trace("closeSilently", null, autoCloseable);
                autoCloseable.close();
            } catch (Exception e) {
            }
        }
    }

    public static void skipFully(InputStream inputStream, long j) throws IOException {
        while (j > 0) {
            try {
                long skip = inputStream.skip(j);
                if (skip <= 0) {
                    throw new EOFException();
                }
                j -= skip;
            } catch (Exception e) {
                throw DataUtils.convertToIOException(e);
            }
        }
    }

    public static void skipFully(Reader reader, long j) throws IOException {
        while (j > 0) {
            try {
                long skip = reader.skip(j);
                if (skip <= 0) {
                    throw new EOFException();
                }
                j -= skip;
            } catch (Exception e) {
                throw DataUtils.convertToIOException(e);
            }
        }
    }

    public static long copyAndClose(InputStream inputStream, OutputStream outputStream) throws IOException {
        try {
            try {
                long copyAndCloseInput = copyAndCloseInput(inputStream, outputStream);
                outputStream.close();
                closeSilently(outputStream);
                return copyAndCloseInput;
            } catch (Exception e) {
                throw DataUtils.convertToIOException(e);
            }
        } catch (Throwable th) {
            closeSilently(outputStream);
            throw th;
        }
    }

    public static long copyAndCloseInput(InputStream inputStream, OutputStream outputStream) throws IOException {
        try {
            try {
                long copy = copy(inputStream, outputStream);
                closeSilently(inputStream);
                return copy;
            } catch (Exception e) {
                throw DataUtils.convertToIOException(e);
            }
        } catch (Throwable th) {
            closeSilently(inputStream);
            throw th;
        }
    }

    public static long copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        return copy(inputStream, outputStream, Long.MAX_VALUE);
    }

    public static long copy(InputStream inputStream, OutputStream outputStream, long j) throws IOException {
        try {
            long j2 = 0;
            int min = (int) Math.min(j, 4096L);
            byte[] bArr = new byte[min];
            while (j > 0) {
                int read = inputStream.read(bArr, 0, min);
                if (read < 0) {
                    break;
                }
                if (outputStream != null) {
                    outputStream.write(bArr, 0, read);
                }
                j2 += read;
                j -= read;
                min = (int) Math.min(j, 4096L);
            }
            return j2;
        } catch (Exception e) {
            throw DataUtils.convertToIOException(e);
        }
    }

    public static long copy(FileChannel fileChannel, OutputStream outputStream) throws IOException {
        return copy(fileChannel, outputStream, Long.MAX_VALUE);
    }

    public static long copy(FileChannel fileChannel, OutputStream outputStream, long j) throws IOException {
        try {
            long j2 = 0;
            byte[] bArr = new byte[(int) Math.min(j, 4096L)];
            ByteBuffer wrap = ByteBuffer.wrap(bArr);
            while (j > 0) {
                int read = fileChannel.read(wrap, j2);
                if (read < 0) {
                    break;
                }
                if (outputStream != null) {
                    outputStream.write(bArr, 0, read);
                }
                j2 += read;
                j -= read;
                wrap.rewind();
                if (j < wrap.limit()) {
                    wrap.limit((int) j);
                }
            }
            return j2;
        } catch (Exception e) {
            throw DataUtils.convertToIOException(e);
        }
    }

    public static long copyAndCloseInput(Reader reader, Writer writer, long j) throws IOException {
        try {
            try {
                long j2 = 0;
                int min = (int) Math.min(j, 4096L);
                char[] cArr = new char[min];
                while (j > 0) {
                    int read = reader.read(cArr, 0, min);
                    if (read < 0) {
                        break;
                    }
                    if (writer != null) {
                        writer.write(cArr, 0, read);
                    }
                    j2 += read;
                    j -= read;
                    min = (int) Math.min(j, 4096L);
                }
                return j2;
            } catch (Exception e) {
                throw DataUtils.convertToIOException(e);
            }
        } finally {
            reader.close();
        }
    }

    public static byte[] readBytesAndClose(InputStream inputStream, int i) throws IOException {
        try {
            if (i <= 0) {
                i = Integer.MAX_VALUE;
            }
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(Math.min(4096, i));
                copy(inputStream, byteArrayOutputStream, i);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                inputStream.close();
                return byteArray;
            } catch (Exception e) {
                throw DataUtils.convertToIOException(e);
            }
        } catch (Throwable th) {
            inputStream.close();
            throw th;
        }
    }

    public static String readStringAndClose(Reader reader, int i) throws IOException {
        if (i <= 0) {
            i = Integer.MAX_VALUE;
        }
        try {
            StringWriter stringWriter = new StringWriter(Math.min(4096, i));
            copyAndCloseInput(reader, stringWriter, i);
            String stringWriter2 = stringWriter.toString();
            reader.close();
            return stringWriter2;
        } catch (Throwable th) {
            reader.close();
            throw th;
        }
    }

    public static int readFully(InputStream inputStream, byte[] bArr, int i) throws IOException {
        try {
            int i2 = 0;
            int min = Math.min(i, bArr.length);
            while (min > 0) {
                int read = inputStream.read(bArr, i2, min);
                if (read < 0) {
                    break;
                }
                i2 += read;
                min -= read;
            }
            return i2;
        } catch (Exception e) {
            throw DataUtils.convertToIOException(e);
        }
    }

    public static int readFully(Reader reader, char[] cArr, int i) throws IOException {
        try {
            int i2 = 0;
            int min = Math.min(i, cArr.length);
            while (min > 0) {
                int read = reader.read(cArr, i2, min);
                if (read < 0) {
                    break;
                }
                i2 += read;
                min -= read;
            }
            return i2;
        } catch (Exception e) {
            throw DataUtils.convertToIOException(e);
        }
    }

    public static Reader getReader(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    public static Writer getBufferedWriter(OutputStream outputStream) {
        if (outputStream == null) {
            return null;
        }
        return new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
    }

    public static Reader getAsciiReader(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        return new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
    }

    public static void trace(String str, String str2, Object obj) {
        if (SysProperties.TRACE_IO) {
            System.out.println("IOUtils." + str + ' ' + str2 + ' ' + obj);
        }
    }

    public static InputStream getInputStreamFromString(String str) {
        if (str == null) {
            return null;
        }
        return new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
    }

    public static void copyFiles(String str, String str2) throws IOException {
        copyAndClose(FileUtils.newInputStream(str), FileUtils.newOutputStream(str2, false));
    }

    public static String nameSeparatorsToNative(String str) {
        return File.separatorChar == '/' ? str.replace('\\', '/') : str.replace('/', '\\');
    }
}
