package cn.hutool.extra.ftp;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/ftp/Ftp.class */
public class Ftp extends AbstractFtp {
    public static final int DEFAULT_PORT = 21;
    private FTPClient client;
    private FtpMode mode;
    private boolean backToPwd;

    public Ftp(String host) {
        this(host, 21);
    }

    public Ftp(String host, int port) {
        this(host, port, "anonymous", "");
    }

    public Ftp(String host, int port, String user, String password) {
        this(host, port, user, password, CharsetUtil.CHARSET_UTF_8);
    }

    public Ftp(String host, int port, String user, String password, Charset charset) {
        this(host, port, user, password, charset, null, null);
    }

    public Ftp(String host, int port, String user, String password, Charset charset, String serverLanguageCode, String systemKey) {
        this(host, port, user, password, charset, serverLanguageCode, systemKey, null);
    }

    public Ftp(String host, int port, String user, String password, Charset charset, String serverLanguageCode, String systemKey, FtpMode mode) {
        this(new FtpConfig(host, port, user, password, charset, serverLanguageCode, systemKey), mode);
    }

    public Ftp(FtpConfig config, FtpMode mode) {
        super(config);
        this.mode = mode;
        init();
    }

    public Ftp(FTPClient client) {
        super(FtpConfig.create());
        this.client = client;
    }

    public Ftp init() {
        return init(this.ftpConfig, this.mode);
    }

    public Ftp init(String host, int port, String user, String password) {
        return init(host, port, user, password, null);
    }

    public Ftp init(String host, int port, String user, String password, FtpMode mode) {
        return init(new FtpConfig(host, port, user, password, this.ftpConfig.getCharset(), null, null), mode);
    }

    public Ftp init(FtpConfig config, FtpMode mode) {
        FTPClient client = new FTPClient();
        client.setRemoteVerificationEnabled(false);
        Charset charset = config.getCharset();
        if (null != charset) {
            client.setControlEncoding(charset.toString());
        }
        client.setConnectTimeout((int) config.getConnectionTimeout());
        String systemKey = config.getSystemKey();
        if (StrUtil.isNotBlank(systemKey)) {
            FTPClientConfig conf = new FTPClientConfig(systemKey);
            String serverLanguageCode = config.getServerLanguageCode();
            if (StrUtil.isNotBlank(serverLanguageCode)) {
                conf.setServerLanguageCode(config.getServerLanguageCode());
            }
            client.configure(conf);
        }
        try {
            client.connect(config.getHost(), config.getPort());
            client.setSoTimeout((int) config.getSoTimeout());
            client.login(config.getUser(), config.getPassword());
            int replyCode = client.getReplyCode();
            if (false == FTPReply.isPositiveCompletion(replyCode)) {
                try {
                    client.disconnect();
                } catch (IOException e) {
                }
                throw new FtpException("Login failed for user [{}], reply code is: [{}]", config.getUser(), Integer.valueOf(replyCode));
            }
            this.client = client;
            if (mode != null) {
                setMode(mode);
            }
            return this;
        } catch (IOException e2) {
            throw new IORuntimeException(e2);
        }
    }

    public Ftp setMode(FtpMode mode) {
        this.mode = mode;
        switch (mode) {
            case Active:
                this.client.enterLocalActiveMode();
                break;
            case Passive:
                this.client.enterLocalPassiveMode();
                break;
        }
        return this;
    }

    public Ftp setBackToPwd(boolean backToPwd) {
        this.backToPwd = backToPwd;
        return this;
    }

    public boolean isBackToPwd() {
        return this.backToPwd;
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public Ftp reconnectIfTimeout() {
        String pwd = null;
        try {
            pwd = pwd();
        } catch (IORuntimeException e) {
        }
        if (pwd == null) {
            return init();
        }
        return this;
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public synchronized boolean cd(String directory) {
        if (StrUtil.isBlank(directory)) {
            return true;
        }
        try {
            return this.client.changeWorkingDirectory(directory);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public String pwd() {
        try {
            return this.client.printWorkingDirectory();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public List<String> ls(String path) {
        return ArrayUtil.map(lsFiles(path), (v0) -> {
            return v0.getName();
        });
    }

    public List<FTPFile> lsFiles(String path, Filter<FTPFile> filter) {
        FTPFile[] ftpFiles = lsFiles(path);
        if (ArrayUtil.isEmpty((Object[]) ftpFiles)) {
            return ListUtil.empty();
        }
        List<FTPFile> result = new ArrayList<>(ftpFiles.length - 2 <= 0 ? ftpFiles.length : ftpFiles.length - 2);
        for (FTPFile ftpFile : ftpFiles) {
            String fileName = ftpFile.getName();
            if (false == StrUtil.equals(".", fileName) && false == StrUtil.equals("..", fileName) && (null == filter || filter.accept(ftpFile))) {
                result.add(ftpFile);
            }
        }
        return result;
    }

    public FTPFile[] lsFiles(String path) throws FtpException, IORuntimeException {
        String pwd = null;
        if (StrUtil.isNotBlank(path)) {
            pwd = pwd();
            if (false == cd(path)) {
                throw new FtpException("Change dir to [{}] error, maybe path not exist!", path);
            }
        }
        try {
            try {
                FTPFile[] ftpFiles = this.client.listFiles();
                cd(pwd);
                return ftpFiles;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Throwable th) {
            cd(pwd);
            throw th;
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public boolean mkdir(String dir) throws IORuntimeException {
        try {
            return this.client.makeDirectory(dir);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public int stat(String path) throws IORuntimeException {
        try {
            return this.client.stat(path);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public boolean existFile(String path) throws IORuntimeException {
        try {
            FTPFile[] ftpFileArr = this.client.listFiles(path);
            return ArrayUtil.isNotEmpty((Object[]) ftpFileArr);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public boolean delFile(String path) throws IORuntimeException {
        String pwd = pwd();
        String fileName = FileUtil.getName(path);
        String dir = StrUtil.removeSuffix(path, fileName);
        if (false == cd(dir)) {
            throw new FtpException("Change dir to [{}] error, maybe dir not exist!", path);
        }
        try {
            try {
                boolean isSuccess = this.client.deleteFile(fileName);
                cd(pwd);
                return isSuccess;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Throwable th) {
            cd(pwd);
            throw th;
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public boolean delDir(String dirPath) throws IORuntimeException {
        try {
            FTPFile[] dirs = this.client.listFiles(dirPath);
            for (FTPFile ftpFile : dirs) {
                String name = ftpFile.getName();
                String childPath = StrUtil.format("{}/{}", dirPath, name);
                if (ftpFile.isDirectory()) {
                    if (false == ".".equals(name) && false == "..".equals(name)) {
                        delDir(childPath);
                    }
                } else {
                    delFile(childPath);
                }
            }
            try {
                return this.client.removeDirectory(dirPath);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (IOException e2) {
            throw new IORuntimeException(e2);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public boolean upload(String destPath, File file) {
        Assert.notNull(file, "file to upload is null !", new Object[0]);
        return upload(destPath, file.getName(), file);
    }

    public boolean upload(String destPath, String fileName, File file) throws IORuntimeException {
        try {
            InputStream in = FileUtil.getInputStream(file);
            Throwable th = null;
            try {
                boolean upload = upload(destPath, fileName, in);
                if (in != null) {
                    if (0 != 0) {
                        try {
                            in.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        in.close();
                    }
                }
                return upload;
            } finally {
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public boolean upload(String destPath, String fileName, InputStream fileStream) throws IORuntimeException {
        try {
            this.client.setFileType(2);
            String pwd = null;
            if (this.backToPwd) {
                pwd = pwd();
            }
            if (StrUtil.isNotBlank(destPath)) {
                mkDirs(destPath);
                if (false == cd(destPath)) {
                    throw new FtpException("Change dir to [{}] error, maybe dir not exist!", destPath);
                }
            }
            try {
                try {
                    boolean storeFile = this.client.storeFile(fileName, fileStream);
                    if (this.backToPwd) {
                        cd(pwd);
                    }
                    return storeFile;
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            } catch (Throwable th) {
                if (this.backToPwd) {
                    cd(pwd);
                }
                throw th;
            }
        } catch (IOException e2) {
            throw new IORuntimeException(e2);
        }
    }

    public void uploadFileOrDirectory(String remotePath, File uploadFile) {
        if (false == FileUtil.isDirectory(uploadFile)) {
            upload(remotePath, uploadFile);
            return;
        }
        File[] files = uploadFile.listFiles();
        if (ArrayUtil.isEmpty((Object[]) files)) {
            return;
        }
        List<File> dirs = new ArrayList<>(files.length);
        for (File f : files) {
            if (f.isDirectory()) {
                dirs.add(f);
            } else {
                upload(remotePath, f);
            }
        }
        for (File f2 : dirs) {
            String dir = FileUtil.normalize(remotePath + "/" + f2.getName());
            uploadFileOrDirectory(dir, f2);
        }
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public void download(String path, File outFile) {
        String fileName = FileUtil.getName(path);
        String dir = StrUtil.removeSuffix(path, fileName);
        download(dir, fileName, outFile);
    }

    @Override // cn.hutool.extra.ftp.AbstractFtp
    public void recursiveDownloadFolder(String sourcePath, File destDir) {
        for (FTPFile ftpFile : lsFiles(sourcePath, null)) {
            String fileName = ftpFile.getName();
            String srcFile = StrUtil.format("{}/{}", sourcePath, fileName);
            File destFile = FileUtil.file(destDir, fileName);
            if (false == ftpFile.isDirectory()) {
                if (false == FileUtil.exist(destFile) || ftpFile.getTimestamp().getTimeInMillis() > destFile.lastModified()) {
                    download(srcFile, destFile);
                }
            } else {
                FileUtil.mkdir(destFile);
                recursiveDownloadFolder(srcFile, destFile);
            }
        }
    }

    public void download(String path, String fileName, File outFile) throws IORuntimeException {
        if (outFile.isDirectory()) {
            outFile = new File(outFile, fileName);
        }
        if (false == outFile.exists()) {
            FileUtil.touch(outFile);
        }
        try {
            OutputStream out = FileUtil.getOutputStream(outFile);
            Throwable th = null;
            try {
                try {
                    download(path, fileName, out);
                    if (out != null) {
                        if (0 != 0) {
                            try {
                                out.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            out.close();
                        }
                    }
                } finally {
                }
            } finally {
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public void download(String path, String fileName, OutputStream out) {
        download(path, fileName, out, null);
    }

    public void download(String path, String fileName, OutputStream out, Charset fileNameCharset) throws IORuntimeException {
        String pwd = null;
        if (this.backToPwd) {
            pwd = pwd();
        }
        if (false == cd(path)) {
            throw new FtpException("Change dir to [{}] error, maybe dir not exist!", path);
        }
        if (null != fileNameCharset) {
            fileName = new String(fileName.getBytes(fileNameCharset), StandardCharsets.ISO_8859_1);
        }
        try {
            try {
                this.client.setFileType(2);
                this.client.retrieveFile(fileName, out);
                if (this.backToPwd) {
                    cd(pwd);
                }
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Throwable th) {
            if (this.backToPwd) {
                cd(pwd);
            }
            throw th;
        }
    }

    public FTPClient getClient() {
        return this.client;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (null != this.client) {
            this.client.logout();
            if (this.client.isConnected()) {
                this.client.disconnect();
            }
            this.client = null;
        }
    }
}
