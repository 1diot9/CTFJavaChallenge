package cn.hutool.db.ds;

import cn.hutool.log.StaticLog;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/GlobalDSFactory.class */
public class GlobalDSFactory {
    private static volatile DSFactory factory;
    private static final Object lock = new Object();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() { // from class: cn.hutool.db.ds.GlobalDSFactory.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                if (null != GlobalDSFactory.factory) {
                    GlobalDSFactory.factory.destroy();
                    StaticLog.debug("DataSource: [{}] destroyed.", GlobalDSFactory.factory.dataSourceName);
                    DSFactory unused = GlobalDSFactory.factory = null;
                }
            }
        });
    }

    public static DSFactory get() {
        if (null == factory) {
            synchronized (lock) {
                if (null == factory) {
                    factory = DSFactory.create(null);
                }
            }
        }
        return factory;
    }

    public static DSFactory set(DSFactory customDSFactory) {
        synchronized (lock) {
            if (null != factory) {
                if (factory.equals(customDSFactory)) {
                    return factory;
                }
                factory.destroy();
            }
            StaticLog.debug("Custom use [{}] DataSource.", customDSFactory.dataSourceName);
            factory = customDSFactory;
            return factory;
        }
    }
}
