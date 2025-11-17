package cn.hutool.log.dialect.logtube;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.github.logtube.Logtube;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/logtube/LogTubeLogFactory.class */
public class LogTubeLogFactory extends LogFactory {
    public LogTubeLogFactory() {
        super("LogTube");
        checkLogExist(Logtube.class);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(String name) {
        return new LogTubeLog(name);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(Class<?> clazz) {
        return new LogTubeLog(clazz);
    }
}
