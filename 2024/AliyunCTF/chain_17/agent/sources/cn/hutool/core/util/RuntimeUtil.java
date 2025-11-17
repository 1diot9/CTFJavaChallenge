package cn.hutool.core.util;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Pid;
import cn.hutool.core.text.StrBuilder;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/RuntimeUtil.class */
public class RuntimeUtil {
    public static String execForStr(String... cmds) throws IORuntimeException {
        return execForStr(CharsetUtil.systemCharset(), cmds);
    }

    public static String execForStr(Charset charset, String... cmds) throws IORuntimeException {
        return getResult(exec(cmds), charset);
    }

    public static List<String> execForLines(String... cmds) throws IORuntimeException {
        return execForLines(CharsetUtil.systemCharset(), cmds);
    }

    public static List<String> execForLines(Charset charset, String... cmds) throws IORuntimeException {
        return getResultLines(exec(cmds), charset);
    }

    public static Process exec(String... cmds) {
        try {
            Process process = new ProcessBuilder(handleCmds(cmds)).redirectErrorStream(true).start();
            return process;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static Process exec(String[] envp, String... cmds) {
        return exec(envp, null, cmds);
    }

    public static Process exec(String[] envp, File dir, String... cmds) {
        try {
            return Runtime.getRuntime().exec(handleCmds(cmds), envp, dir);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static List<String> getResultLines(Process process) {
        return getResultLines(process, CharsetUtil.systemCharset());
    }

    public static List<String> getResultLines(Process process, Charset charset) {
        InputStream in = null;
        try {
            in = process.getInputStream();
            List<String> list = (List) IoUtil.readLines(in, charset, new ArrayList());
            IoUtil.close((Closeable) in);
            destroy(process);
            return list;
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            destroy(process);
            throw th;
        }
    }

    public static String getResult(Process process) {
        return getResult(process, CharsetUtil.systemCharset());
    }

    public static String getResult(Process process, Charset charset) {
        InputStream in = null;
        try {
            in = process.getInputStream();
            String read = IoUtil.read(in, charset);
            IoUtil.close((Closeable) in);
            destroy(process);
            return read;
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            destroy(process);
            throw th;
        }
    }

    public static String getErrorResult(Process process) {
        return getErrorResult(process, CharsetUtil.systemCharset());
    }

    public static String getErrorResult(Process process, Charset charset) {
        InputStream in = null;
        try {
            in = process.getErrorStream();
            String read = IoUtil.read(in, charset);
            IoUtil.close((Closeable) in);
            destroy(process);
            return read;
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            destroy(process);
            throw th;
        }
    }

    public static void destroy(Process process) {
        if (null != process) {
            process.destroy();
        }
    }

    public static void addShutdownHook(Runnable hook) {
        Runtime.getRuntime().addShutdownHook(hook instanceof Thread ? (Thread) hook : new Thread(hook));
    }

    public static int getProcessorCount() {
        int cpu = Runtime.getRuntime().availableProcessors();
        if (cpu <= 0) {
            cpu = 7;
        }
        return cpu;
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static long getUsableMemory() {
        return (getMaxMemory() - getTotalMemory()) + getFreeMemory();
    }

    public static int getPid() throws UtilException {
        return Pid.INSTANCE.get();
    }

    private static String[] handleCmds(String... cmds) {
        if (ArrayUtil.isEmpty((Object[]) cmds)) {
            throw new NullPointerException("Command is empty !");
        }
        if (1 == cmds.length) {
            String cmd = cmds[0];
            if (StrUtil.isBlank(cmd)) {
                throw new NullPointerException("Command is blank !");
            }
            cmds = cmdSplit(cmd);
        }
        return cmds;
    }

    private static String[] cmdSplit(String cmd) {
        List<String> cmds = new ArrayList<>();
        int length = cmd.length();
        Stack<Character> stack = new Stack<>();
        boolean inWrap = false;
        StrBuilder cache = StrUtil.strBuilder();
        for (int i = 0; i < length; i++) {
            char c = cmd.charAt(i);
            switch (c) {
                case ' ':
                    if (inWrap) {
                        cache.append(c);
                        break;
                    } else {
                        cmds.add(cache.toString());
                        cache.reset();
                        break;
                    }
                case '\"':
                case '\'':
                    if (inWrap) {
                        if (c == stack.peek().charValue()) {
                            stack.pop();
                            inWrap = false;
                        }
                        cache.append(c);
                        break;
                    } else {
                        stack.push(Character.valueOf(c));
                        cache.append(c);
                        inWrap = true;
                        break;
                    }
                default:
                    cache.append(c);
                    break;
            }
        }
        if (cache.hasContent()) {
            cmds.add(cache.toString());
        }
        return (String[]) cmds.toArray(new String[0]);
    }
}
