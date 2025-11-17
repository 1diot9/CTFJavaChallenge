package cn.hutool.extra.ssh;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.AbstractFtp;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ftp.FtpException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/ssh/Sftp.class */
public class Sftp extends AbstractFtp {
    private Session session;
    private ChannelSftp channel;

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/ssh/Sftp$Mode.class */
    public enum Mode {
        OVERWRITE,
        RESUME,
        APPEND
    }

    public Sftp(String sshHost, int sshPort, String sshUser, String sshPass) {
        this(sshHost, sshPort, sshUser, sshPass, DEFAULT_CHARSET);
    }

    public Sftp(String sshHost, int sshPort, String sshUser, String sshPass, Charset charset) {
        this(new FtpConfig(sshHost, sshPort, sshUser, sshPass, charset));
    }

    public Sftp(FtpConfig config) {
        this(config, true);
    }

    public Sftp(FtpConfig config, boolean init) {
        super(config);
        if (init) {
            init(config);
        }
    }

    public Sftp(Session session) {
        this(session, DEFAULT_CHARSET);
    }

    public Sftp(Session session, Charset charset) {
        super(FtpConfig.create().setCharset(charset));
        init(session, charset);
    }

    public Sftp(Session session, Charset charset, long timeOut) {
        super(FtpConfig.create().setCharset(charset).setConnectionTimeout(timeOut));
        init(session, charset);
    }

    public Sftp(ChannelSftp channel, Charset charset, long timeOut) {
        super(FtpConfig.create().setCharset(charset).setConnectionTimeout(timeOut));
        init(channel, charset);
    }

    public Sftp(ChannelSftp channel, Charset charset) {
        super(FtpConfig.create().setCharset(charset));
        init(channel, charset);
    }

    public void init(String sshHost, int sshPort, String sshUser, String sshPass, Charset charset) {
        init(JschUtil.getSession(sshHost, sshPort, sshUser, sshPass), charset);
    }

    public void init() {
        init(this.ftpConfig);
    }

    public void init(FtpConfig config) {
        init(config.getHost(), config.getPort(), config.getUser(), config.getPassword(), config.getCharset());
    }

    public void init(Session session, Charset charset) {
        this.session = session;
        init(JschUtil.openSftp(session, (int) this.ftpConfig.getConnectionTimeout()), charset);
    }

    public void init(ChannelSftp channel, Charset charset) {
        this.ftpConfig.setCharset(charset);
        try {
            channel.setFilenameEncoding(charset.toString());
            this.channel = channel;
        } catch (SftpException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public Sftp reconnectIfTimeout() {
        if (StrUtil.isBlank(this.ftpConfig.getHost())) {
            throw new FtpException("Host is blank!");
        }
        try {
            cd("/");
        } catch (FtpException e) {
            close();
            init();
        }
        return this;
    }

    public ChannelSftp getClient() {
        return this.channel;
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public String pwd() {
        try {
            return this.channel.pwd();
        } catch (SftpException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    public String home() {
        try {
            return this.channel.getHome();
        } catch (SftpException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public List<String> ls(String path) {
        return ls(path, null);
    }

    public List<String> lsDirs(String path) {
        return ls(path, t -> {
            return t.getAttrs().isDir();
        });
    }

    public List<String> lsFiles(String path) {
        return ls(path, t -> {
            return false == t.getAttrs().isDir();
        });
    }

    public List<String> ls(String path, Filter<ChannelSftp.LsEntry> filter) {
        List<ChannelSftp.LsEntry> entries = lsEntries(path, filter);
        if (CollUtil.isEmpty((Collection<?>) entries)) {
            return ListUtil.empty();
        }
        return CollUtil.map(entries, (v0) -> {
            return v0.getFilename();
        }, true);
    }

    public List<ChannelSftp.LsEntry> lsEntries(String path) {
        return lsEntries(path, null);
    }

    public List<ChannelSftp.LsEntry> lsEntries(String path, Filter<ChannelSftp.LsEntry> filter) {
        List<ChannelSftp.LsEntry> entryList = new ArrayList<>();
        try {
            this.channel.ls(path, entry -> {
                String fileName = entry.getFilename();
                if (false == StrUtil.equals(".", fileName) && false == StrUtil.equals("..", fileName)) {
                    if (null == filter || filter.accept(entry)) {
                        entryList.add(entry);
                        return 0;
                    }
                    return 0;
                }
                return 0;
            });
        } catch (SftpException e) {
            if (false == StrUtil.startWithIgnoreCase(e.getMessage(), "No such file")) {
                throw new JschRuntimeException((Throwable) e);
            }
        }
        return entryList;
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public boolean mkdir(String dir) {
        if (isDir(dir)) {
            return true;
        }
        try {
            this.channel.mkdir(dir);
            return true;
        } catch (SftpException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public boolean isDir(String dir) {
        try {
            SftpATTRS sftpATTRS = this.channel.stat(dir);
            return sftpATTRS.isDir();
        } catch (SftpException e) {
            String msg = e.getMessage();
            if (StrUtil.containsAnyIgnoreCase(msg, "No such file", "does not exist")) {
                return false;
            }
            throw new FtpException((Throwable) e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public synchronized boolean cd(String directory) throws FtpException {
        if (StrUtil.isBlank(directory)) {
            return true;
        }
        try {
            this.channel.cd(directory.replace('\\', '/'));
            return true;
        } catch (SftpException e) {
            throw new FtpException((Throwable) e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public boolean delFile(String filePath) {
        try {
            this.channel.rm(filePath);
            return true;
        } catch (SftpException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public boolean delDir(String dirPath) {
        if (false == cd(dirPath)) {
            return false;
        }
        try {
            Vector<ChannelSftp.LsEntry> list = this.channel.ls(this.channel.pwd());
            Iterator<ChannelSftp.LsEntry> it = list.iterator();
            while (it.hasNext()) {
                ChannelSftp.LsEntry entry = it.next();
                String fileName = entry.getFilename();
                if (false == ".".equals(fileName) && false == "..".equals(fileName)) {
                    if (entry.getAttrs().isDir()) {
                        delDir(fileName);
                    } else {
                        delFile(fileName);
                    }
                }
            }
            if (false == cd("..")) {
                return false;
            }
            try {
                this.channel.rmdir(dirPath);
                return true;
            } catch (SftpException e) {
                throw new JschRuntimeException((Throwable) e);
            }
        } catch (SftpException e2) {
            throw new JschRuntimeException((Throwable) e2);
        }
    }

    public void syncUpload(File file, String remotePath) {
        if (false == FileUtil.exist(file)) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File fileItem : files) {
                if (fileItem.isDirectory()) {
                    String mkdir = FileUtil.normalize(remotePath + "/" + fileItem.getName());
                    syncUpload(fileItem, mkdir);
                } else {
                    syncUpload(fileItem, remotePath);
                }
            }
            return;
        }
        mkDirs(remotePath);
        upload(remotePath, file);
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public boolean upload(String destPath, File file) {
        put(FileUtil.getAbsolutePath(file), destPath);
        return true;
    }

    public boolean upload(String destPath, String fileName, InputStream fileStream) {
        put(fileStream, StrUtil.addSuffixIfNot(destPath, "/") + StrUtil.removePrefix(fileName, "/"), (SftpProgressMonitor) null, Mode.OVERWRITE);
        return true;
    }

    public Sftp put(String srcFilePath, String destPath) {
        return put(srcFilePath, destPath, Mode.OVERWRITE);
    }

    public Sftp put(String srcFilePath, String destPath, Mode mode) {
        return put(srcFilePath, destPath, (SftpProgressMonitor) null, mode);
    }

    public Sftp put(String srcFilePath, String destPath, SftpProgressMonitor monitor, Mode mode) {
        try {
            this.channel.put(srcFilePath, destPath, monitor, mode.ordinal());
            return this;
        } catch (SftpException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    public Sftp put(InputStream srcStream, String destPath, SftpProgressMonitor monitor, Mode mode) {
        try {
            this.channel.put(srcStream, destPath, monitor, mode.ordinal());
            return this;
        } catch (SftpException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public void download(String src, File destFile) {
        get(src, FileUtil.getAbsolutePath(destFile));
    }

    public void download(String src, OutputStream out) {
        get(src, out);
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public void recursiveDownloadFolder(String sourcePath, File destDir) throws JschRuntimeException {
        for (ChannelSftp.LsEntry item : lsEntries(sourcePath)) {
            String fileName = item.getFilename();
            String srcFile = StrUtil.format("{}/{}", sourcePath, fileName);
            File destFile = FileUtil.file(destDir, fileName);
            if (false == item.getAttrs().isDir()) {
                if (false == FileUtil.exist(destFile) || item.getAttrs().getMTime() > destFile.lastModified() / 1000) {
                    download(srcFile, destFile);
                }
            } else {
                FileUtil.mkdir(destFile);
                recursiveDownloadFolder(srcFile, destFile);
            }
        }
    }

    public Sftp get(String src, String dest) {
        try {
            this.channel.get(src, dest);
            return this;
        } catch (SftpException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    public Sftp get(String src, OutputStream out) {
        try {
            this.channel.get(src, out);
            return this;
        } catch (SftpException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        JschUtil.close((Channel) this.channel);
        JschUtil.close(this.session);
    }

    public String toString() {
        return "Sftp{host='" + this.ftpConfig.getHost() + "', port=" + this.ftpConfig.getPort() + ", user='" + this.ftpConfig.getUser() + "'}";
    }
}
