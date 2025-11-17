package cn.hutool.extra.ssh;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.LocalPortGenerater;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/ssh/JschUtil.class */
public class JschUtil {
    public static final String SSH_NONE = "none";
    private static final LocalPortGenerater portGenerater = new LocalPortGenerater(10000);

    public static int generateLocalPort() {
        return portGenerater.generate();
    }

    public static Session getSession(String sshHost, int sshPort, String sshUser, String sshPass) {
        return JschSessionPool.INSTANCE.getSession(sshHost, sshPort, sshUser, sshPass);
    }

    public static Session getSession(String sshHost, int sshPort, String sshUser, String privateKeyPath, byte[] passphrase) {
        return JschSessionPool.INSTANCE.getSession(sshHost, sshPort, sshUser, privateKeyPath, passphrase);
    }

    public static Session openSession(String sshHost, int sshPort, String sshUser, String sshPass) {
        return openSession(sshHost, sshPort, sshUser, sshPass, 0);
    }

    public static Session openSession(String sshHost, int sshPort, String sshUser, String sshPass, int timeout) {
        Session session = createSession(sshHost, sshPort, sshUser, sshPass);
        try {
            session.connect(timeout);
            return session;
        } catch (JSchException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    public static Session openSession(String sshHost, int sshPort, String sshUser, String privateKeyPath, byte[] passphrase) {
        return openSession(sshHost, sshPort, sshUser, privateKeyPath, passphrase, 0);
    }

    public static Session openSession(String sshHost, int sshPort, String sshUser, String privateKeyPath, byte[] passphrase, int timeOut) {
        Session session = createSession(sshHost, sshPort, sshUser, privateKeyPath, passphrase);
        try {
            session.connect(timeOut);
            return session;
        } catch (JSchException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    public static Session createSession(String sshHost, int sshPort, String sshUser, String sshPass) {
        JSch jsch = new JSch();
        Session session = createSession(jsch, sshHost, sshPort, sshUser);
        if (StrUtil.isNotEmpty(sshPass)) {
            session.setPassword(sshPass);
        }
        return session;
    }

    public static Session createSession(String sshHost, int sshPort, String sshUser, String privateKeyPath, byte[] passphrase) {
        Assert.notEmpty(privateKeyPath, "PrivateKey Path must be not empty!", new Object[0]);
        JSch jsch = new JSch();
        try {
            jsch.addIdentity(privateKeyPath, passphrase);
            return createSession(jsch, sshHost, sshPort, sshUser);
        } catch (JSchException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    public static Session createSession(JSch jsch, String sshHost, int sshPort, String sshUser) {
        Assert.notEmpty(sshHost, "SSH Host must be not empty!", new Object[0]);
        Assert.isTrue(sshPort > 0, "SSH port must be > 0", new Object[0]);
        if (StrUtil.isEmpty(sshUser)) {
            sshUser = "root";
        }
        if (null == jsch) {
            jsch = new JSch();
        }
        try {
            Session session = jsch.getSession(sshUser, sshHost, sshPort);
            session.setConfig("StrictHostKeyChecking", "no");
            return session;
        } catch (JSchException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    public static boolean bindPort(Session session, String remoteHost, int remotePort, int localPort) throws JschRuntimeException {
        return bindPort(session, remoteHost, remotePort, "127.0.0.1", localPort);
    }

    public static boolean bindPort(Session session, String remoteHost, int remotePort, String localHost, int localPort) throws JschRuntimeException {
        if (session != null && session.isConnected()) {
            try {
                session.setPortForwardingL(localHost, localPort, remoteHost, remotePort);
                return true;
            } catch (JSchException e) {
                throw new JschRuntimeException(e, "From [{}:{}] mapping to [{}:{}] error！", remoteHost, Integer.valueOf(remotePort), localHost, Integer.valueOf(localPort));
            }
        }
        return false;
    }

    public static boolean bindRemotePort(Session session, int bindPort, String host, int port) throws JschRuntimeException {
        if (session != null && session.isConnected()) {
            try {
                session.setPortForwardingR(bindPort, host, port);
                return true;
            } catch (JSchException e) {
                throw new JschRuntimeException(e, "From [{}] mapping to [{}] error！", Integer.valueOf(bindPort), Integer.valueOf(port));
            }
        }
        return false;
    }

    public static boolean unBindPort(Session session, int localPort) {
        try {
            session.delPortForwardingL(localPort);
            return true;
        } catch (JSchException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    public static int openAndBindPortToLocal(Connector sshConn, String remoteHost, int remotePort) throws JschRuntimeException {
        Session session = openSession(sshConn.getHost(), sshConn.getPort(), sshConn.getUser(), sshConn.getPassword());
        int localPort = generateLocalPort();
        bindPort(session, remoteHost, remotePort, localPort);
        return localPort;
    }

    public static ChannelSftp openSftp(Session session) {
        return openSftp(session, 0);
    }

    public static ChannelSftp openSftp(Session session, int timeout) {
        return openChannel(session, ChannelType.SFTP, timeout);
    }

    public static Sftp createSftp(String sshHost, int sshPort, String sshUser, String sshPass) {
        return new Sftp(sshHost, sshPort, sshUser, sshPass);
    }

    public static Sftp createSftp(Session session) {
        return new Sftp(session);
    }

    public static ChannelShell openShell(Session session) {
        return openChannel(session, ChannelType.SHELL);
    }

    public static Channel openChannel(Session session, ChannelType channelType) {
        return openChannel(session, channelType, 0);
    }

    public static Channel openChannel(Session session, ChannelType channelType, int timeout) {
        Channel channel = createChannel(session, channelType);
        try {
            channel.connect(Math.max(timeout, 0));
            return channel;
        } catch (JSchException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    public static Channel createChannel(Session session, ChannelType channelType) {
        try {
            if (false == session.isConnected()) {
                session.connect();
            }
            Channel channel = session.openChannel(channelType.getValue());
            return channel;
        } catch (JSchException e) {
            throw new JschRuntimeException((Throwable) e);
        }
    }

    public static String exec(Session session, String cmd, Charset charset) {
        return exec(session, cmd, charset, System.err);
    }

    /* JADX WARN: Finally extract failed */
    public static String exec(Session session, String cmd, Charset charset, OutputStream errStream) {
        if (null == charset) {
            charset = CharsetUtil.CHARSET_UTF_8;
        }
        ChannelExec channel = createChannel(session, ChannelType.EXEC);
        channel.setCommand(StrUtil.bytes(cmd, charset));
        channel.setInputStream((InputStream) null);
        channel.setErrStream(errStream);
        InputStream in = null;
        try {
            try {
                channel.connect();
                in = channel.getInputStream();
                String read = IoUtil.read(in, charset);
                IoUtil.close((Closeable) in);
                close((Channel) channel);
                return read;
            } catch (JSchException e) {
                throw new JschRuntimeException((Throwable) e);
            } catch (IOException e2) {
                throw new IORuntimeException(e2);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            close((Channel) channel);
            throw th;
        }
    }

    public static String execByShell(Session session, String cmd, Charset charset) {
        ChannelShell shell = openShell(session);
        shell.setPty(true);
        OutputStream out = null;
        InputStream in = null;
        try {
            try {
                out = shell.getOutputStream();
                in = shell.getInputStream();
                out.write(StrUtil.bytes(cmd, charset));
                out.flush();
                String read = IoUtil.read(in, charset);
                IoUtil.close((Closeable) out);
                IoUtil.close((Closeable) in);
                close((Channel) shell);
                return read;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) out);
            IoUtil.close((Closeable) in);
            close((Channel) shell);
            throw th;
        }
    }

    public static void close(Session session) {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        JschSessionPool.INSTANCE.remove(session);
    }

    public static void close(Channel channel) {
        if (channel != null && channel.isConnected()) {
            channel.disconnect();
        }
    }

    public static void close(String key) {
        JschSessionPool.INSTANCE.close(key);
    }

    public static void closeAll() {
        JschSessionPool.INSTANCE.closeAll();
    }
}
