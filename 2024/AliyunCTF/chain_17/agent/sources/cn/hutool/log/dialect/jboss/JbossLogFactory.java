package cn.hutool.log.dialect.jboss;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.jboss.logging.Logger;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/jboss/JbossLogFactory.class */
public class JbossLogFactory extends LogFactory {
    public JbossLogFactory() {
        super("JBoss Logging");
        checkLogExist(Logger.class);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(String name) {
        return new JbossLog(name);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(Class<?> clazz) {
        return new JbossLog(clazz);
    }
}
