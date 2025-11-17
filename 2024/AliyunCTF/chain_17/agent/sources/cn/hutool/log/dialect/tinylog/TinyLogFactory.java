package cn.hutool.log.dialect.tinylog;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.pmw.tinylog.Logger;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/tinylog/TinyLogFactory.class */
public class TinyLogFactory extends LogFactory {
    public TinyLogFactory() {
        super("TinyLog");
        checkLogExist(Logger.class);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(String name) {
        return new TinyLog(name);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(Class<?> clazz) {
        return new TinyLog(clazz);
    }
}
