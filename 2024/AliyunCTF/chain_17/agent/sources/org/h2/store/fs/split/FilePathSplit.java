package org.h2.store.fs.split;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FilePathWrapper;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/split/FilePathSplit.class */
public class FilePathSplit extends FilePathWrapper {
    private static final String PART_SUFFIX = ".part";

    @Override // org.h2.store.fs.FilePathWrapper
    protected String getPrefix() {
        return getScheme() + ":" + parse(this.name)[0] + ":";
    }

    @Override // org.h2.store.fs.FilePathWrapper
    public FilePath unwrap(String str) {
        return FilePath.get(parse(str)[1]);
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public boolean setReadOnly() {
        boolean z = false;
        int i = 0;
        while (true) {
            FilePath base = getBase(i);
            if (base.exists()) {
                z = base.setReadOnly();
                i++;
            } else {
                return z;
            }
        }
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public void delete() {
        int i = 0;
        while (true) {
            FilePath base = getBase(i);
            if (base.exists()) {
                base.delete();
                i++;
            } else {
                return;
            }
        }
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public long lastModified() {
        long j = 0;
        int i = 0;
        while (true) {
            FilePath base = getBase(i);
            if (base.exists()) {
                j = Math.max(j, base.lastModified());
                i++;
            } else {
                return j;
            }
        }
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public long size() {
        long j = 0;
        int i = 0;
        while (true) {
            FilePath base = getBase(i);
            if (base.exists()) {
                j += base.size();
                i++;
            } else {
                return j;
            }
        }
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public ArrayList<FilePath> newDirectoryStream() {
        List<FilePath> newDirectoryStream = getBase().newDirectoryStream();
        ArrayList<FilePath> arrayList = new ArrayList<>();
        for (FilePath filePath : newDirectoryStream) {
            if (!filePath.getName().endsWith(PART_SUFFIX)) {
                arrayList.add(wrap(filePath));
            }
        }
        return arrayList;
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public InputStream newInputStream() throws IOException {
        InputStream newInputStream = getBase().newInputStream();
        int i = 1;
        while (true) {
            FilePath base = getBase(i);
            if (base.exists()) {
                newInputStream = new SequenceInputStream(newInputStream, base.newInputStream());
                i++;
            } else {
                return newInputStream;
            }
        }
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public FileChannel open(String str) throws IOException {
        ArrayList arrayList = new ArrayList();
        arrayList.add(getBase().open(str));
        int i = 1;
        while (true) {
            FilePath base = getBase(i);
            if (!base.exists()) {
                break;
            }
            arrayList.add(base.open(str));
            i++;
        }
        FileChannel[] fileChannelArr = (FileChannel[]) arrayList.toArray(new FileChannel[0]);
        long size = fileChannelArr[0].size();
        long j = size;
        if (fileChannelArr.length == 1) {
            long defaultMaxLength = getDefaultMaxLength();
            if (size < defaultMaxLength) {
                size = defaultMaxLength;
            }
        } else {
            if (size == 0) {
                closeAndThrow(0, fileChannelArr, fileChannelArr[0], size);
            }
            for (int i2 = 1; i2 < fileChannelArr.length - 1; i2++) {
                FileChannel fileChannel = fileChannelArr[i2];
                long size2 = fileChannel.size();
                j += size2;
                if (size2 != size) {
                    closeAndThrow(i2, fileChannelArr, fileChannel, size);
                }
            }
            FileChannel fileChannel2 = fileChannelArr[fileChannelArr.length - 1];
            long size3 = fileChannel2.size();
            j += size3;
            if (size3 > size) {
                closeAndThrow(fileChannelArr.length - 1, fileChannelArr, fileChannel2, size);
            }
        }
        return new FileSplit(this, str, fileChannelArr, j, size);
    }

    private long getDefaultMaxLength() {
        return 1 << Integer.decode(parse(this.name)[0]).intValue();
    }

    private void closeAndThrow(int i, FileChannel[] fileChannelArr, FileChannel fileChannel, long j) throws IOException {
        String str = "Expected file length: " + j + " got: " + fileChannel.size() + " for " + getName(i);
        for (FileChannel fileChannel2 : fileChannelArr) {
            fileChannel2.close();
        }
        throw new IOException(str);
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public OutputStream newOutputStream(boolean z) throws IOException {
        return newFileChannelOutputStream(open("rw"), z);
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public void moveTo(FilePath filePath, boolean z) {
        FilePathSplit filePathSplit = (FilePathSplit) filePath;
        int i = 0;
        while (true) {
            FilePath base = getBase(i);
            if (base.exists()) {
                base.moveTo(filePathSplit.getBase(i), z);
            } else if (filePathSplit.getBase(i).exists()) {
                filePathSplit.getBase(i).delete();
            } else {
                return;
            }
            i++;
        }
    }

    private String[] parse(String str) {
        String l;
        if (!str.startsWith(getScheme())) {
            throw DbException.getInternalError(str + " doesn't start with " + getScheme());
        }
        String substring = str.substring(getScheme().length() + 1);
        if (substring.length() > 0 && Character.isDigit(substring.charAt(0))) {
            int indexOf = substring.indexOf(58);
            l = substring.substring(0, indexOf);
            try {
                substring = substring.substring(indexOf + 1);
            } catch (NumberFormatException e) {
            }
        } else {
            l = Long.toString(SysProperties.SPLIT_FILE_SIZE_SHIFT);
        }
        return new String[]{l, substring};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FilePath getBase(int i) {
        return FilePath.get(getName(i));
    }

    private String getName(int i) {
        return i > 0 ? getBase().name + "." + i + PART_SUFFIX : getBase().name;
    }

    @Override // org.h2.store.fs.FilePath
    public String getScheme() {
        return "split";
    }
}
