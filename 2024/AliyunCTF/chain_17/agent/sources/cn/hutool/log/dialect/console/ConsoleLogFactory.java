package cn.hutool.log.dialect.console;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/console/ConsoleLogFactory.class */
public class ConsoleLogFactory extends LogFactory {
    public ConsoleLogFactory() {
        super("Hutool Console Logging");
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(String name) {
        return new ConsoleLog(name);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(Class<?> clazz) {
        return new ConsoleLog(clazz);
    }
}
