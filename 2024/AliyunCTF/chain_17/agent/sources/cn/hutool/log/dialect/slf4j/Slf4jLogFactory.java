package cn.hutool.log.dialect.slf4j;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/slf4j/Slf4jLogFactory.class */
public class Slf4jLogFactory extends LogFactory {
    public Slf4jLogFactory() {
        this(true);
    }

    public Slf4jLogFactory(boolean failIfNOP) {
        super("Slf4j");
        checkLogExist(LoggerFactory.class);
        if (false == failIfNOP) {
            return;
        }
        final StringBuilder buf = new StringBuilder();
        PrintStream err = System.err;
        try {
            System.setErr(new PrintStream(new OutputStream() { // from class: cn.hutool.log.dialect.slf4j.Slf4jLogFactory.1
                @Override // java.io.OutputStream
                public void write(int b) {
                    buf.append((char) b);
                }
            }, true, "US-ASCII"));
            try {
                if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
                    throw new NoClassDefFoundError(buf.toString());
                }
                err.print(buf);
                err.flush();
                System.setErr(err);
            } catch (Throwable th) {
                System.setErr(err);
                throw th;
            }
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(String name) {
        return new Slf4jLog(name);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(Class<?> clazz) {
        return new Slf4jLog(clazz);
    }
}
