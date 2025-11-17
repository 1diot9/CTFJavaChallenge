package cn.hutool;

import cn.hutool.core.lang.ConsoleTable;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/Hutool.class */
public class Hutool {
    public static final String AUTHOR = "Looly";

    private Hutool() {
    }

    public static Set<Class<?>> getAllUtils() {
        return ClassUtil.scanPackage("cn.hutool", clazz -> {
            return false == clazz.isInterface() && StrUtil.endWith(clazz.getSimpleName(), "Util");
        });
    }

    public static void printAllUtils() {
        Set<Class<?>> allUtils = getAllUtils();
        ConsoleTable consoleTable = ConsoleTable.create().addHeader("工具类名", "所在包");
        for (Class<?> clazz : allUtils) {
            consoleTable.addBody(clazz.getSimpleName(), clazz.getPackage().getName());
        }
        consoleTable.print();
    }
}
