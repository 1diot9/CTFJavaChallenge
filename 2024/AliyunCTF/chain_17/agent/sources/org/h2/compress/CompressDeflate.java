package org.h2.compress;

import cn.hutool.core.text.CharSequenceUtil;
import java.util.StringTokenizer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.h2.api.ErrorCode;
import org.h2.mvstore.DataUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/compress/CompressDeflate.class */
public class CompressDeflate implements Compressor {
    private int level = -1;
    private int strategy = 0;

    @Override // org.h2.compress.Compressor
    public void setOptions(String str) {
        if (str == null) {
            return;
        }
        try {
            StringTokenizer stringTokenizer = new StringTokenizer(str);
            while (stringTokenizer.hasMoreElements()) {
                String nextToken = stringTokenizer.nextToken();
                if ("level".equals(nextToken) || "l".equals(nextToken)) {
                    this.level = Integer.parseInt(stringTokenizer.nextToken());
                } else if ("strategy".equals(nextToken) || "s".equals(nextToken)) {
                    this.strategy = Integer.parseInt(stringTokenizer.nextToken());
                }
                new Deflater(this.level).setStrategy(this.strategy);
            }
        } catch (Exception e) {
            throw DataUtils.newMVStoreException(ErrorCode.UNSUPPORTED_COMPRESSION_OPTIONS_1, str, new Object[0]);
        }
    }

    @Override // org.h2.compress.Compressor
    public int compress(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        Deflater deflater = new Deflater(this.level);
        deflater.setStrategy(this.strategy);
        deflater.setInput(bArr, i, i2);
        deflater.finish();
        int deflate = deflater.deflate(bArr2, i3, bArr2.length - i3);
        if (deflate == 0) {
            this.strategy = 0;
            this.level = -1;
            return compress(bArr, i, i2, bArr2, i3);
        }
        deflater.end();
        return i3 + deflate;
    }

    @Override // org.h2.compress.Compressor
    public int getAlgorithm() {
        return 2;
    }

    @Override // org.h2.compress.Compressor
    public void expand(byte[] bArr, int i, int i2, byte[] bArr2, int i3, int i4) {
        Inflater inflater = new Inflater();
        inflater.setInput(bArr, i, i2);
        inflater.finished();
        try {
            int inflate = inflater.inflate(bArr2, i3, i4);
            if (inflate != i4) {
                throw new DataFormatException(inflate + CharSequenceUtil.SPACE + i4);
            }
            inflater.end();
        } catch (DataFormatException e) {
            throw DataUtils.newMVStoreException(ErrorCode.COMPRESSION_ERROR, e.getMessage(), e);
        }
    }
}
