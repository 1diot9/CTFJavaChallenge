package cn.hutool.extra.ftp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Closeable;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/ftp/AbstractFtp.class */
public abstract class AbstractFtp implements Closeable {
    public static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
    protected FtpConfig ftpConfig;

    public abstract AbstractFtp reconnectIfTimeout();

    public abstract boolean cd(String str);

    public abstract String pwd();

    public abstract boolean mkdir(String str);

    public abstract List<String> ls(String str);

    public abstract boolean delFile(String str);

    public abstract boolean delDir(String str);

    public abstract boolean upload(String str, File file);

    public abstract void download(String str, File file);

    public abstract void recursiveDownloadFolder(String str, File file);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractFtp(FtpConfig config) {
        this.ftpConfig = config;
    }

    public boolean toParent() {
        return cd("..");
    }

    public boolean isDir(String dir) {
        String workDir = pwd();
        try {
            boolean cd = cd(dir);
            cd(workDir);
            return cd;
        } catch (Throwable th) {
            cd(workDir);
            throw th;
        }
    }

    public boolean exist(String path) {
        if (StrUtil.isBlank(path)) {
            return false;
        }
        if (isDir(path)) {
            return true;
        }
        if (CharUtil.isFileSeparator(path.charAt(path.length() - 1))) {
            return false;
        }
        String fileName = FileUtil.getName(path);
        if (".".equals(fileName) || "..".equals(fileName)) {
            return false;
        }
        String dir = StrUtil.emptyToDefault(StrUtil.removeSuffix(path, fileName), ".");
        try {
            List<String> names = ls(dir);
            return containsIgnoreCase(names, fileName);
        } catch (FtpException e) {
            return false;
        }
    }

    public void mkDirs(String dir) {
        String[] dirs = StrUtil.trim(dir).split("[\\\\/]+");
        String now = pwd();
        if (dirs.length > 0 && StrUtil.isEmpty(dirs[0])) {
            cd("/");
        }
        for (String s : dirs) {
            if (StrUtil.isNotEmpty(s)) {
                boolean exist = true;
                try {
                    if (false == cd(s)) {
                        exist = false;
                    }
                } catch (FtpException e) {
                    exist = false;
                }
                if (false == exist) {
                    mkdir(s);
                    cd(s);
                }
            }
        }
        cd(now);
    }

    public void download(String path, File outFile, String tempFileSuffix) {
        String tempFileSuffix2;
        if (StrUtil.isBlank(tempFileSuffix)) {
            tempFileSuffix2 = ".temp";
        } else {
            tempFileSuffix2 = StrUtil.addPrefixIfNot(tempFileSuffix, ".");
        }
        String fileName = outFile.isDirectory() ? FileUtil.getName(path) : outFile.getName();
        String tempFileName = fileName + tempFileSuffix2;
        File outFile2 = new File(outFile.isDirectory() ? outFile : outFile.getParentFile(), tempFileName);
        try {
            download(path, outFile2);
            FileUtil.rename(outFile2, fileName, true);
        } catch (Throwable e) {
            FileUtil.del(outFile2);
            throw new FtpException(e);
        }
    }

    private static boolean containsIgnoreCase(List<String> names, String nameToFind) {
        if (CollUtil.isEmpty((Collection<?>) names) || StrUtil.isEmpty(nameToFind)) {
            return false;
        }
        for (String name : names) {
            if (nameToFind.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
