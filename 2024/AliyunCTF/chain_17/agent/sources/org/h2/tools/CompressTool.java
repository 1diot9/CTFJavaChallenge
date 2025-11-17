package org.h2.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.h2.api.ErrorCode;
import org.h2.compress.CompressDeflate;
import org.h2.compress.CompressLZF;
import org.h2.compress.CompressNo;
import org.h2.compress.Compressor;
import org.h2.compress.LZFInputStream;
import org.h2.compress.LZFOutputStream;
import org.h2.message.DbException;
import org.h2.util.Bits;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/CompressTool.class */
public class CompressTool {
    private static final int MAX_BUFFER_SIZE = 393216;
    private byte[] buffer;

    private CompressTool() {
    }

    private byte[] getBuffer(int i) {
        if (i > 393216) {
            return Utils.newBytes(i);
        }
        if (this.buffer == null || this.buffer.length < i) {
            this.buffer = Utils.newBytes(i);
        }
        return this.buffer;
    }

    public static CompressTool getInstance() {
        return new CompressTool();
    }

    public byte[] compress(byte[] bArr, String str) {
        int length = bArr.length;
        if (bArr.length < 5) {
            str = "NO";
        }
        Compressor compressor = getCompressor(str);
        byte[] buffer = getBuffer((length < 100 ? length + 100 : length) * 2);
        return Utils.copyBytes(buffer, compress(bArr, bArr.length, compressor, buffer));
    }

    private static int compress(byte[] bArr, int i, Compressor compressor, byte[] bArr2) {
        bArr2[0] = (byte) compressor.getAlgorithm();
        int writeVariableInt = 1 + writeVariableInt(bArr2, 1, i);
        int compress = compressor.compress(bArr, 0, i, bArr2, writeVariableInt);
        if (compress > i + writeVariableInt || compress <= 0) {
            bArr2[0] = 0;
            System.arraycopy(bArr, 0, bArr2, writeVariableInt, i);
            compress = i + writeVariableInt;
        }
        return compress;
    }

    public byte[] expand(byte[] bArr) {
        if (bArr.length == 0) {
            throw DbException.get(ErrorCode.COMPRESSION_ERROR);
        }
        Compressor compressor = getCompressor(bArr[0]);
        try {
            int readVariableInt = readVariableInt(bArr, 1);
            int variableIntLength = 1 + getVariableIntLength(readVariableInt);
            byte[] newBytes = Utils.newBytes(readVariableInt);
            compressor.expand(bArr, variableIntLength, bArr.length - variableIntLength, newBytes, 0, readVariableInt);
            return newBytes;
        } catch (Exception e) {
            throw DbException.get(ErrorCode.COMPRESSION_ERROR, e, new String[0]);
        }
    }

    public static void expand(byte[] bArr, byte[] bArr2, int i) {
        Compressor compressor = getCompressor(bArr[0]);
        try {
            int readVariableInt = readVariableInt(bArr, 1);
            int variableIntLength = 1 + getVariableIntLength(readVariableInt);
            compressor.expand(bArr, variableIntLength, bArr.length - variableIntLength, bArr2, i, readVariableInt);
        } catch (Exception e) {
            throw DbException.get(ErrorCode.COMPRESSION_ERROR, e, new String[0]);
        }
    }

    public static int readVariableInt(byte[] bArr, int i) {
        int i2 = i + 1;
        int i3 = bArr[i] & 255;
        if (i3 < 128) {
            return i3;
        }
        if (i3 < 192) {
            return ((i3 & 63) << 8) + (bArr[i2] & 255);
        }
        if (i3 < 224) {
            return ((i3 & 31) << 16) + ((bArr[i2] & 255) << 8) + (bArr[i2 + 1] & 255);
        }
        if (i3 < 240) {
            int i4 = i2 + 1;
            return ((i3 & 15) << 24) + ((bArr[i2] & 255) << 16) + ((bArr[i4] & 255) << 8) + (bArr[i4 + 1] & 255);
        }
        return Bits.readInt(bArr, i2);
    }

    public static int writeVariableInt(byte[] bArr, int i, int i2) {
        if (i2 < 0) {
            bArr[i] = -16;
            Bits.writeInt(bArr, i + 1, i2);
            return 5;
        }
        if (i2 < 128) {
            bArr[i] = (byte) i2;
            return 1;
        }
        if (i2 < 16384) {
            bArr[i] = (byte) (128 | (i2 >> 8));
            bArr[i + 1] = (byte) i2;
            return 2;
        }
        if (i2 < 2097152) {
            int i3 = i + 1;
            bArr[i] = (byte) (192 | (i2 >> 16));
            bArr[i3] = (byte) (i2 >> 8);
            bArr[i3 + 1] = (byte) i2;
            return 3;
        }
        if (i2 < 268435456) {
            Bits.writeInt(bArr, i, i2 | (-536870912));
            return 4;
        }
        bArr[i] = -16;
        Bits.writeInt(bArr, i + 1, i2);
        return 5;
    }

    public static int getVariableIntLength(int i) {
        if (i < 0) {
            return 5;
        }
        if (i < 128) {
            return 1;
        }
        if (i < 16384) {
            return 2;
        }
        if (i < 2097152) {
            return 3;
        }
        if (i < 268435456) {
            return 4;
        }
        return 5;
    }

    private static Compressor getCompressor(String str) {
        if (str == null) {
            str = "LZF";
        }
        int indexOf = str.indexOf(32);
        String str2 = null;
        if (indexOf > 0) {
            str2 = str.substring(indexOf + 1);
            str = str.substring(0, indexOf);
        }
        Compressor compressor = getCompressor(getCompressAlgorithm(str));
        compressor.setOptions(str2);
        return compressor;
    }

    private static int getCompressAlgorithm(String str) {
        String upperEnglish = StringUtils.toUpperEnglish(str);
        if ("NO".equals(upperEnglish)) {
            return 0;
        }
        if ("LZF".equals(upperEnglish)) {
            return 1;
        }
        if ("DEFLATE".equals(upperEnglish)) {
            return 2;
        }
        throw DbException.get(ErrorCode.UNSUPPORTED_COMPRESSION_ALGORITHM_1, upperEnglish);
    }

    private static Compressor getCompressor(int i) {
        switch (i) {
            case 0:
                return new CompressNo();
            case 1:
                return new CompressLZF();
            case 2:
                return new CompressDeflate();
            default:
                throw DbException.get(ErrorCode.UNSUPPORTED_COMPRESSION_ALGORITHM_1, Integer.toString(i));
        }
    }

    public static OutputStream wrapOutputStream(OutputStream outputStream, String str, String str2) {
        try {
            if ("GZIP".equals(str)) {
                outputStream = new GZIPOutputStream(outputStream);
            } else if ("ZIP".equals(str)) {
                ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
                zipOutputStream.putNextEntry(new ZipEntry(str2));
                outputStream = zipOutputStream;
            } else if ("DEFLATE".equals(str)) {
                outputStream = new DeflaterOutputStream(outputStream);
            } else if ("LZF".equals(str)) {
                outputStream = new LZFOutputStream(outputStream);
            } else if (str != null) {
                throw DbException.get(ErrorCode.UNSUPPORTED_COMPRESSION_ALGORITHM_1, str);
            }
            return outputStream;
        } catch (IOException e) {
            throw DbException.convertIOException(e, null);
        }
    }

    public static InputStream wrapInputStream(InputStream inputStream, String str, String str2) {
        ZipEntry nextEntry;
        try {
            if ("GZIP".equals(str)) {
                inputStream = new GZIPInputStream(inputStream);
            } else if ("ZIP".equals(str)) {
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                do {
                    nextEntry = zipInputStream.getNextEntry();
                    if (nextEntry == null) {
                        return null;
                    }
                } while (!str2.equals(nextEntry.getName()));
                inputStream = zipInputStream;
            } else if ("DEFLATE".equals(str)) {
                inputStream = new InflaterInputStream(inputStream);
            } else if ("LZF".equals(str)) {
                inputStream = new LZFInputStream(inputStream);
            } else if (str != null) {
                throw DbException.get(ErrorCode.UNSUPPORTED_COMPRESSION_ALGORITHM_1, str);
            }
            return inputStream;
        } catch (IOException e) {
            throw DbException.convertIOException(e, null);
        }
    }
}
