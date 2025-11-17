package cn.hutool.log.dialect.console;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.ansi.AnsiColor;
import cn.hutool.core.lang.ansi.AnsiEncoder;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.level.Level;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/console/ConsoleColorLog.class */
public class ConsoleColorLog extends ConsoleLog {
    private static final AnsiColor COLOR_CLASSNAME = AnsiColor.CYAN;
    private static final AnsiColor COLOR_TIME = AnsiColor.WHITE;
    private static final AnsiColor COLOR_NONE = AnsiColor.DEFAULT;
    private static Function<Level, AnsiColor> colorFactory = level -> {
        switch (level) {
            case DEBUG:
            case INFO:
                return AnsiColor.GREEN;
            case WARN:
                return AnsiColor.YELLOW;
            case ERROR:
                return AnsiColor.RED;
            case TRACE:
                return AnsiColor.MAGENTA;
            default:
                return COLOR_NONE;
        }
    };

    public static void setColorFactory(Function<Level, AnsiColor> colorFactory2) {
        colorFactory = colorFactory2;
    }

    public ConsoleColorLog(String name) {
        super(name);
    }

    public ConsoleColorLog(Class<?> clazz) {
        super(clazz);
    }

    @Override // cn.hutool.log.dialect.console.ConsoleLog, cn.hutool.log.Log
    public synchronized void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
        if (false == isEnabled(level)) {
            return;
        }
        String template = AnsiEncoder.encode(COLOR_TIME, "[%s]", colorFactory.apply(level), "[%-5s]%s", COLOR_CLASSNAME, "%-30s: ", COLOR_NONE, "%s%n");
        System.out.format(template, DateUtil.now(), level.name(), " - ", ClassUtil.getShortClassName(getName()), StrUtil.format(format, arguments));
    }
}
