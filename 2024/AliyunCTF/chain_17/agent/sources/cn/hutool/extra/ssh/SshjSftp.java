package cn.hutool.extra.ssh;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.AbstractFtp;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ftp.FtpException;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/ssh/SshjSftp.class */
public class SshjSftp extends AbstractFtp {
    private SSHClient ssh;
    private SFTPClient sftp;

    public SshjSftp(String sshHost) {
        this(new FtpConfig(sshHost, 22, null, null, CharsetUtil.CHARSET_UTF_8));
    }

    public SshjSftp(String sshHost, String sshUser, String sshPass) {
        this(new FtpConfig(sshHost, 22, sshUser, sshPass, CharsetUtil.CHARSET_UTF_8));
    }

    public SshjSftp(String sshHost, int sshPort, String sshUser, String sshPass) {
        this(new FtpConfig(sshHost, sshPort, sshUser, sshPass, CharsetUtil.CHARSET_UTF_8));
    }

    public SshjSftp(String sshHost, int sshPort, String sshUser, String sshPass, Charset charset) {
        this(new FtpConfig(sshHost, sshPort, sshUser, sshPass, charset));
    }

    protected SshjSftp(FtpConfig config) {
        super(config);
        init();
    }

    public void init() {
        this.ssh = new SSHClient();
        this.ssh.addHostKeyVerifier(new PromiscuousVerifier());
        try {
            this.ssh.connect(this.ftpConfig.getHost(), this.ftpConfig.getPort());
            this.ssh.authPassword(this.ftpConfig.getUser(), this.ftpConfig.getPassword());
            this.ssh.setRemoteCharset(this.ftpConfig.getCharset());
            this.sftp = this.ssh.newSFTPClient();
        } catch (IOException e) {
            throw new FtpException("sftp 初始化失败.", e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public AbstractFtp reconnectIfTimeout() {
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

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public boolean cd(String directory) {
        String exec = String.format("cd %s", directory);
        command(exec);
        String pwd = pwd();
        return pwd.equals(directory);
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public String pwd() {
        return command("pwd");
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public boolean mkdir(String dir) {
        try {
            this.sftp.mkdir(dir);
            return containsFile(dir);
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public List<String> ls(String path) {
        try {
            List<RemoteResourceInfo> infoList = this.sftp.ls(path);
            if (CollUtil.isNotEmpty((Collection<?>) infoList)) {
                return CollUtil.map(infoList, (v0) -> {
                    return v0.getName();
                }, true);
            }
            return null;
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public boolean delFile(String path) {
        try {
            this.sftp.rm(path);
            return !containsFile(path);
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public boolean delDir(String dirPath) {
        try {
            this.sftp.rmdir(dirPath);
            return !containsFile(dirPath);
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public boolean upload(String destPath, File file) {
        try {
            this.sftp.put(new FileSystemFile(file), destPath);
            return containsFile(destPath);
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public void download(String path, File outFile) {
        try {
            this.sftp.get(path, new FileSystemFile(outFile));
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public void recursiveDownloadFolder(String sourcePath, File destDir) {
        List<String> files = ls(sourcePath);
        if (files != null && !files.isEmpty()) {
            files.forEach(path -> {
                download(sourcePath + "/" + path, destDir);
            });
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        try {
            this.sftp.close();
            this.ssh.disconnect();
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }

    public boolean containsFile(String fileDir) {
        try {
            this.sftp.lstat(fileDir);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String command(String exec) {
        Session session = null;
        try {
            try {
                session = this.ssh.startSession();
                Session.Command command = session.exec(exec);
                InputStream inputStream = command.getInputStream();
                String read = IoUtil.read(inputStream, DEFAULT_CHARSET);
                IoUtil.close((Closeable) session);
                return read;
            } catch (Exception e) {
                throw new FtpException(e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) session);
            throw th;
        }
    }
}
