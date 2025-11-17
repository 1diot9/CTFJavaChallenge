package cn.hutool.log.dialect.tinylog;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.tinylog.Logger;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/tinylog/TinyLog2Factory.class */
public class TinyLog2Factory extends LogFactory {
    public TinyLog2Factory() {
        super("TinyLog");
        checkLogExist(Logger.class);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(String name) {
        return new TinyLog2(name);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(Class<?> clazz) {
        return new TinyLog2(clazz);
    }
}
