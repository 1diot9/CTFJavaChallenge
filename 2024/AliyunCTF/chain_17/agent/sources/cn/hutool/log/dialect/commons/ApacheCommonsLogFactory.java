package cn.hutool.log.dialect.commons;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/commons/ApacheCommonsLogFactory.class */
public class ApacheCommonsLogFactory extends LogFactory {
    public ApacheCommonsLogFactory() {
        super("Apache Common Logging");
        checkLogExist(org.apache.commons.logging.LogFactory.class);
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(String name) {
        try {
            return new ApacheCommonsLog4JLog(name);
        } catch (Exception e) {
            return new ApacheCommonsLog(name);
        }
    }

    @Override // cn.hutool.log.LogFactory
    public Log createLog(Class<?> clazz) {
        try {
            return new ApacheCommonsLog4JLog(clazz);
        } catch (Exception e) {
            return new ApacheCommonsLog(clazz);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // cn.hutool.log.LogFactory
    public void checkLogExist(Class<?> logClassName) {
        super.checkLogExist(logClassName);
        getLog(ApacheCommonsLogFactory.class);
    }
}
