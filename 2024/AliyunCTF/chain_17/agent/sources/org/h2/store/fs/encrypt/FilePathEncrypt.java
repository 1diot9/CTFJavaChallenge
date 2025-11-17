package org.h2.store.fs.encrypt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FilePathWrapper;
import org.h2.store.fs.FileUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/encrypt/FilePathEncrypt.class */
public class FilePathEncrypt extends FilePathWrapper {
    private static final String SCHEME = "encrypt";

    public static void register() {
        FilePath.register(new FilePathEncrypt());
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public FileChannel open(String str) throws IOException {
        String[] parse = parse(this.name);
        FileChannel open = FileUtils.open(parse[1], str);
        return new FileEncrypt(this.name, parse[0].getBytes(StandardCharsets.UTF_8), open);
    }

    @Override // org.h2.store.fs.FilePath
    public String getScheme() {
        return SCHEME;
    }

    @Override // org.h2.store.fs.FilePathWrapper
    protected String getPrefix() {
        return getScheme() + ":" + parse(this.name)[0] + ":";
    }

    @Override // org.h2.store.fs.FilePathWrapper
    public FilePath unwrap(String str) {
        return FilePath.get(parse(str)[1]);
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public long size() {
        long max = Math.max(0L, getBase().size() - 4096);
        if ((max & 4095) != 0) {
            max -= 4096;
        }
        return max;
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public OutputStream newOutputStream(boolean z) throws IOException {
        return newFileChannelOutputStream(open("rw"), z);
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public InputStream newInputStream() throws IOException {
        return Channels.newInputStream(open("r"));
    }

    private String[] parse(String str) {
        if (!str.startsWith(getScheme())) {
            throw new IllegalArgumentException(str + " doesn't start with " + getScheme());
        }
        String substring = str.substring(getScheme().length() + 1);
        int indexOf = substring.indexOf(58);
        if (indexOf < 0) {
            throw new IllegalArgumentException(substring + " doesn't contain encryption algorithm and password");
        }
        return new String[]{substring.substring(0, indexOf), substring.substring(indexOf + 1)};
    }

    public static byte[] getPasswordBytes(char[] cArr) {
        int length = cArr.length;
        byte[] bArr = new byte[length * 2];
        for (int i = 0; i < length; i++) {
            char c = cArr[i];
            bArr[i + i] = (byte) (c >>> '\b');
            bArr[i + i + 1] = (byte) c;
        }
        return bArr;
    }
}
