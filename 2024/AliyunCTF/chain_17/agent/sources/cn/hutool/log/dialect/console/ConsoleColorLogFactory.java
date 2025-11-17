package cn.hutool.log.dialect.console;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/console/ConsoleColorLogFactory.class */
public class ConsoleColorLogFactory extends LogFactory {
    public ConsoleColorLogFactory() {
        super("Hutool Console Color Logging");
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(String name) {
        return new ConsoleColorLog(name);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(Class<?> clazz) {
        return new ConsoleColorLog(clazz);
    }
}
