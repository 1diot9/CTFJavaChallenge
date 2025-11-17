package cn.hutool.log.dialect.log4j;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.log4j.Logger;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/log4j/Log4jLogFactory.class */
public class Log4jLogFactory extends LogFactory {
    public Log4jLogFactory() {
        super("Log4j");
        checkLogExist(Logger.class);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(String name) {
        return new Log4jLog(name);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(Class<?> clazz) {
        return new Log4jLog(clazz);
    }
}
