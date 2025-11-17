package cn.hutool.log.dialect.jdk;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import java.io.Closeable;
import java.io.InputStream;
import java.util.logging.LogManager;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/jdk/JdkLogFactory.class */
public class JdkLogFactory extends LogFactory {
    public JdkLogFactory() {
        super("JDK Logging");
        readConfig();
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(String name) {
        return new JdkLog(name);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(Class<?> clazz) {
        return new JdkLog(clazz);
    }

    private void readConfig() {
        InputStream in = ResourceUtil.getStreamSafe("logging.properties");
        try {
            if (null == in) {
                System.err.println("[WARN] Can not find [logging.properties], use [%JRE_HOME%/lib/logging.properties] as default!");
                return;
            }
            try {
                LogManager.getLogManager().readConfiguration(in);
                IoUtil.close((Closeable) in);
            } catch (Exception e) {
                Console.error(e, "Read [logging.properties] from classpath error!", new Object[0]);
                try {
                    LogManager.getLogManager().readConfiguration();
                } catch (Exception e2) {
                    Console.error(e, "Read [logging.properties] from [%JRE_HOME%/lib/logging.properties] error!", new Object[0]);
                }
                IoUtil.close((Closeable) in);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            throw th;
        }
    }
}
