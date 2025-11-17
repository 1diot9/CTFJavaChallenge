package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.io.DataOutputAsStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/TokenStreamFactory.class */
public abstract class TokenStreamFactory implements Versioned, Serializable {
    private static final long serialVersionUID = 2;

    public abstract boolean requiresPropertyOrdering();

    public abstract boolean canHandleBinaryNatively();

    public abstract boolean canParseAsync();

    public abstract Class<? extends FormatFeature> getFormatReadFeatureType();

    public abstract Class<? extends FormatFeature> getFormatWriteFeatureType();

    public abstract boolean canUseSchema(FormatSchema formatSchema);

    public abstract String getFormatName();

    public abstract boolean isEnabled(JsonParser.Feature feature);

    public abstract boolean isEnabled(JsonGenerator.Feature feature);

    public abstract int getParserFeatures();

    public abstract int getGeneratorFeatures();

    public abstract int getFormatParserFeatures();

    public abstract int getFormatGeneratorFeatures();

    public abstract StreamReadConstraints streamReadConstraints();

    public abstract JsonParser createParser(byte[] bArr) throws IOException;

    public abstract JsonParser createParser(byte[] bArr, int i, int i2) throws IOException;

    public abstract JsonParser createParser(char[] cArr) throws IOException;

    public abstract JsonParser createParser(char[] cArr, int i, int i2) throws IOException;

    public abstract JsonParser createParser(DataInput dataInput) throws IOException;

    public abstract JsonParser createParser(File file) throws IOException;

    public abstract JsonParser createParser(InputStream inputStream) throws IOException;

    public abstract JsonParser createParser(Reader reader) throws IOException;

    public abstract JsonParser createParser(String str) throws IOException;

    public abstract JsonParser createParser(URL url) throws IOException;

    public abstract JsonParser createNonBlockingByteArrayParser() throws IOException;

    public abstract JsonParser createNonBlockingByteBufferParser() throws IOException;

    public abstract JsonGenerator createGenerator(DataOutput dataOutput, JsonEncoding jsonEncoding) throws IOException;

    public abstract JsonGenerator createGenerator(DataOutput dataOutput) throws IOException;

    public abstract JsonGenerator createGenerator(File file, JsonEncoding jsonEncoding) throws IOException;

    public abstract JsonGenerator createGenerator(OutputStream outputStream) throws IOException;

    public abstract JsonGenerator createGenerator(OutputStream outputStream, JsonEncoding jsonEncoding) throws IOException;

    public abstract JsonGenerator createGenerator(Writer writer) throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public OutputStream _createDataOutputWrapper(DataOutput out) {
        return new DataOutputAsStream(out);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public InputStream _optimizedStreamFromURL(URL url) throws IOException {
        String host;
        if ("file".equals(url.getProtocol()) && ((host = url.getHost()) == null || host.length() == 0)) {
            String path = url.getPath();
            if (path.indexOf(37) < 0) {
                return new FileInputStream(url.getPath());
            }
        }
        return url.openStream();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public InputStream _fileInputStream(File f) throws IOException {
        return new FileInputStream(f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public OutputStream _fileOutputStream(File f) throws IOException {
        return new FileOutputStream(f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _checkRangeBoundsForByteArray(byte[] data, int offset, int len) throws IllegalArgumentException {
        if (data == null) {
            _reportRangeError("Invalid `byte[]` argument: `null`");
        }
        int dataLen = data.length;
        int end = offset + len;
        int anyNegs = offset | len | end | (dataLen - end);
        if (anyNegs < 0) {
            _reportRangeError(String.format("Invalid 'offset' (%d) and/or 'len' (%d) arguments for `byte[]` of length %d", Integer.valueOf(offset), Integer.valueOf(len), Integer.valueOf(dataLen)));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _checkRangeBoundsForCharArray(char[] data, int offset, int len) throws IOException {
        if (data == null) {
            _reportRangeError("Invalid `char[]` argument: `null`");
        }
        int dataLen = data.length;
        int end = offset + len;
        int anyNegs = offset | len | end | (dataLen - end);
        if (anyNegs < 0) {
            _reportRangeError(String.format("Invalid 'offset' (%d) and/or 'len' (%d) arguments for `char[]` of length %d", Integer.valueOf(offset), Integer.valueOf(len), Integer.valueOf(dataLen)));
        }
    }

    protected <T> T _reportRangeError(String msg) throws IllegalArgumentException {
        throw new IllegalArgumentException(msg);
    }
}
