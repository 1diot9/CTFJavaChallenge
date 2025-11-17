package cn.hutool.log;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/GlobalLogFactory.class */
public class GlobalLogFactory {
    private static volatile LogFactory currentLogFactory;
    private static final Object lock = new Object();

    public static LogFactory get() {
        if (null == currentLogFactory) {
            synchronized (lock) {
                if (null == currentLogFactory) {
                    currentLogFactory = LogFactory.create();
                }
            }
        }
        return currentLogFactory;
    }

    public static LogFactory set(Class<? extends LogFactory> logFactoryClass) {
        try {
            return set(logFactoryClass.newInstance());
        } catch (Exception e) {
            throw new IllegalArgumentException("Can not instance LogFactory class!", e);
        }
    }

    public static LogFactory set(LogFactory logFactory) {
        logFactory.getLog(GlobalLogFactory.class).debug("Custom Use [{}] Logger.", logFactory.name);
        currentLogFactory = logFactory;
        return currentLogFactory;
    }
}
