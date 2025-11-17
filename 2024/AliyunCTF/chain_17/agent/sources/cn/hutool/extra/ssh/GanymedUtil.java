package cn.hutool.extra.ssh;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/ssh/GanymedUtil.class */
public class GanymedUtil {
    public static Connection connect(String sshHost, int sshPort) {
        Connection conn = new Connection(sshHost, sshPort);
        try {
            conn.connect();
            return conn;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static Session openSession(String sshHost, int sshPort, String sshUser, String sshPass) {
        if (StrUtil.isEmpty(sshUser)) {
            sshUser = "root";
        }
        Connection connect = connect(sshHost, sshPort);
        try {
            connect.authenticateWithPassword(sshUser, sshPass);
            return connect.openSession();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static String exec(Session session, String cmd, Charset charset, OutputStream errStream) {
        try {
            try {
                session.execCommand(cmd, charset.name());
                String result = IoUtil.read((InputStream) new StreamGobbler(session.getStdout()), charset);
                IoUtil.copy((InputStream) new StreamGobbler(session.getStderr()), errStream);
                close(session);
                return result;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Throwable th) {
            close(session);
            throw th;
        }
    }

    public static String execByShell(Session session, String cmd, Charset charset, OutputStream errStream) {
        try {
            try {
                session.requestDumbPTY();
                IoUtil.write(session.getStdin(), charset, true, cmd);
                String result = IoUtil.read((InputStream) new StreamGobbler(session.getStdout()), charset);
                if (null != errStream) {
                    IoUtil.copy((InputStream) new StreamGobbler(session.getStderr()), errStream);
                }
                return result;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } finally {
            close(session);
        }
    }

    public static void close(Session session) {
        if (session != null) {
            session.close();
        }
    }
}
