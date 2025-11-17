package cn.hutool.log.dialect.log4j2;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.logging.log4j.LogManager;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/log4j2/Log4j2LogFactory.class */
public class Log4j2LogFactory extends LogFactory {
    public Log4j2LogFactory() {
        super("Log4j2");
        checkLogExist(LogManager.class);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(String name) {
        return new Log4j2Log(name);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(Class<?> clazz) {
        return new Log4j2Log(clazz);
    }
}
