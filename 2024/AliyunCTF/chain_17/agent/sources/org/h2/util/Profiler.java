package org.h2.util;

import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.text.StrPool;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.Thread;
import java.lang.instrument.Instrumentation;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/Profiler.class */
public class Profiler implements Runnable {
    private static Instrumentation instrumentation;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", StrPool.LF);
    private static final int MAX_ELEMENTS = 1000;
    public boolean paused;
    public boolean sumClasses;
    public boolean sumMethods;
    private int pid;
    private volatile boolean stop;
    private int total;
    private Thread thread;
    private long start;
    private long time;
    private int threadDumps;
    public int interval = 2;
    public int depth = 48;
    private final String[] ignoreLines = "java,sun,com.sun.,com.google.common.,com.mongodb.,org.bson.,".split(",");
    private final String[] ignorePackages = "java,sun,com.sun.,com.google.common.,com.mongodb.,org.bson".split(",");
    private final String[] ignoreThreads = "java.lang.Object.wait,java.lang.Thread.dumpThreads,java.lang.Thread.getThreads,java.lang.Thread.sleep,java.lang.UNIXProcess.waitForProcessExit,java.net.PlainDatagramSocketImpl.receive0,java.net.PlainSocketImpl.accept,java.net.PlainSocketImpl.socketAccept,java.net.SocketInputStream.socketRead,java.net.SocketOutputStream.socketWrite,org.eclipse.jetty.io.nio.SelectorManager$SelectSet.doSelect,sun.awt.windows.WToolkit.eventLoop,sun.misc.Unsafe.park,sun.nio.ch.EPollArrayWrapper.epollWait,sun.nio.ch.KQueueArrayWrapper.kevent0,sun.nio.ch.ServerSocketChannelImpl.accept,dalvik.system.VMStack.getThreadStackTrace,dalvik.system.NativeStart.run".split(",");
    private final HashMap<String, Integer> counts = new HashMap<>();
    private final HashMap<String, Integer> summary = new HashMap<>();
    private int minCount = 1;

    public static void premain(String str, Instrumentation instrumentation2) {
        instrumentation = instrumentation2;
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public static void main(String... strArr) {
        new Profiler().run(strArr);
    }

    private void run(String... strArr) {
        if (strArr.length == 0) {
            System.out.println("Show profiling data");
            System.out.println("Usage: java " + getClass().getName() + " <pid> | <stackTraceFileNames>");
            System.out.println("Processes:");
            System.out.println(exec("jps", "-l"));
            return;
        }
        this.start = System.nanoTime();
        if (strArr[0].matches(RegexPool.NUMBERS)) {
            this.pid = Integer.parseInt(strArr[0]);
            long j = 0;
            while (true) {
                tick();
                long nanoTime = System.nanoTime();
                if (nanoTime - j > TimeUnit.SECONDS.toNanos(5L)) {
                    this.time = System.nanoTime() - this.start;
                    System.out.println(getTopTraces(3));
                    j = nanoTime;
                }
            }
        } else {
            try {
                for (String str : strArr) {
                    if (str.startsWith("-")) {
                        if ("-classes".equals(str)) {
                            this.sumClasses = true;
                        } else if ("-methods".equals(str)) {
                            this.sumMethods = true;
                        } else if ("-packages".equals(str)) {
                            this.sumClasses = false;
                            this.sumMethods = false;
                        } else {
                            throw new IllegalArgumentException(str);
                        }
                    } else {
                        Path path = Paths.get(str, new String[0]);
                        BufferedReader newBufferedReader = Files.newBufferedReader(path);
                        Throwable th = null;
                        try {
                            try {
                                LineNumberReader lineNumberReader = new LineNumberReader(newBufferedReader);
                                while (true) {
                                    String readLine = lineNumberReader.readLine();
                                    if (readLine == null) {
                                        break;
                                    } else if (readLine.startsWith("Full thread dump")) {
                                        this.threadDumps++;
                                    }
                                }
                                if (newBufferedReader != null) {
                                    if (0 != 0) {
                                        try {
                                            newBufferedReader.close();
                                        } catch (Throwable th2) {
                                            th.addSuppressed(th2);
                                        }
                                    } else {
                                        newBufferedReader.close();
                                    }
                                }
                                newBufferedReader = Files.newBufferedReader(path);
                                Throwable th3 = null;
                                try {
                                    try {
                                        processList(readStackTrace(new LineNumberReader(newBufferedReader)));
                                        if (newBufferedReader != null) {
                                            if (0 != 0) {
                                                try {
                                                    newBufferedReader.close();
                                                } catch (Throwable th4) {
                                                    th3.addSuppressed(th4);
                                                }
                                            } else {
                                                newBufferedReader.close();
                                            }
                                        }
                                    } finally {
                                    }
                                } finally {
                                }
                            } finally {
                            }
                        } finally {
                        }
                    }
                }
                System.out.println(getTopTraces(5));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static List<Object[]> getRunnableStackTraces() {
        StackTraceElement[] value;
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            if (entry.getKey().getState() == Thread.State.RUNNABLE && (value = entry.getValue()) != null && value.length != 0) {
                arrayList.add(value);
            }
        }
        return arrayList;
    }

    private static List<Object[]> readRunnableStackTraces(int i) {
        try {
            return readStackTrace(new LineNumberReader(new StringReader(exec("jstack", Integer.toString(i)))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Object[]> readStackTrace(LineNumberReader lineNumberReader) throws IOException {
        ArrayList arrayList = new ArrayList();
        while (true) {
            String readLine = lineNumberReader.readLine();
            if (readLine == null) {
                break;
            }
            if (readLine.startsWith("\"")) {
                String readLine2 = lineNumberReader.readLine();
                if (readLine2 == null) {
                    break;
                }
                if (readLine2.trim().startsWith("java.lang.Thread.State: RUNNABLE")) {
                    ArrayList arrayList2 = new ArrayList();
                    while (true) {
                        String readLine3 = lineNumberReader.readLine();
                        if (readLine3 == null) {
                            break;
                        }
                        String trim = readLine3.trim();
                        if (!trim.startsWith("- ")) {
                            if (!trim.startsWith("at ")) {
                                break;
                            }
                            arrayList2.add(StringUtils.trimSubstring(trim, 3));
                        }
                    }
                    if (!arrayList2.isEmpty()) {
                        arrayList.add((String[]) arrayList2.toArray(new String[0]));
                    }
                }
            }
        }
        return arrayList;
    }

    private static String exec(String... strArr) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        try {
            Process exec = Runtime.getRuntime().exec(strArr);
            copyInThread(exec.getInputStream(), byteArrayOutputStream2);
            copyInThread(exec.getErrorStream(), byteArrayOutputStream);
            exec.waitFor();
            String byteArrayOutputStreamToString = Utils10.byteArrayOutputStreamToString(byteArrayOutputStream, StandardCharsets.UTF_8);
            if (byteArrayOutputStreamToString.length() > 0) {
                throw new RuntimeException(byteArrayOutputStreamToString);
            }
            return Utils10.byteArrayOutputStreamToString(byteArrayOutputStream2, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [org.h2.util.Profiler$1] */
    private static void copyInThread(final InputStream inputStream, final OutputStream outputStream) {
        new Thread("Profiler stream copy") { // from class: org.h2.util.Profiler.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                byte[] bArr = new byte[4096];
                while (true) {
                    try {
                        int read = inputStream.read(bArr, 0, bArr.length);
                        if (read >= 0) {
                            outputStream.write(bArr, 0, read);
                        } else {
                            return;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }.start();
    }

    public Profiler startCollecting() {
        this.thread = new Thread(this, "Profiler");
        this.thread.setDaemon(true);
        this.thread.start();
        return this;
    }

    public Profiler stopCollecting() {
        this.stop = true;
        if (this.thread != null) {
            try {
                this.thread.join();
            } catch (InterruptedException e) {
            }
            this.thread = null;
        }
        return this;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.start = System.nanoTime();
        while (!this.stop) {
            try {
                tick();
            } catch (Throwable th) {
            }
        }
        this.time = System.nanoTime() - this.start;
    }

    private void tick() {
        List<Object[]> runnableStackTraces;
        if (this.interval > 0) {
            if (this.paused) {
                return;
            } else {
                try {
                    Thread.sleep(this.interval, 0);
                } catch (Exception e) {
                }
            }
        }
        if (this.pid != 0) {
            runnableStackTraces = readRunnableStackTraces(this.pid);
        } else {
            runnableStackTraces = getRunnableStackTraces();
        }
        this.threadDumps++;
        processList(runnableStackTraces);
    }

    private void processList(List<Object[]> list) {
        char charAt;
        for (Object[] objArr : list) {
            if (!startsWithAny(objArr[0].toString(), this.ignoreThreads)) {
                StringBuilder sb = new StringBuilder();
                Object obj = null;
                boolean z = false;
                int i = 0;
                for (int i2 = 0; i2 < objArr.length && i < this.depth; i2++) {
                    String obj2 = objArr[i2].toString();
                    if (!obj2.equals(obj) && !startsWithAny(obj2, this.ignoreLines)) {
                        obj = obj2;
                        sb.append("at ").append(obj2).append(LINE_SEPARATOR);
                        if (!z && !startsWithAny(obj2, this.ignorePackages)) {
                            z = true;
                            int i3 = 0;
                            while (i3 < obj2.length() && (charAt = obj2.charAt(i3)) != '(' && !Character.isUpperCase(charAt)) {
                                i3++;
                            }
                            if (i3 > 0 && obj2.charAt(i3 - 1) == '.') {
                                i3--;
                            }
                            if (this.sumClasses) {
                                int indexOf = obj2.indexOf(46, i3 + 1);
                                i3 = indexOf >= 0 ? indexOf : i3;
                            }
                            if (this.sumMethods) {
                                int indexOf2 = obj2.indexOf(40, i3 + 1);
                                i3 = indexOf2 >= 0 ? indexOf2 : i3;
                            }
                            increment(this.summary, obj2.substring(0, i3), 0);
                        }
                        i++;
                    }
                }
                if (sb.length() > 0) {
                    this.minCount = increment(this.counts, sb.toString().trim(), this.minCount);
                    this.total++;
                }
            }
        }
    }

    private static boolean startsWithAny(String str, String[] strArr) {
        for (String str2 : strArr) {
            if (str2.length() > 0 && str.startsWith(str2)) {
                return true;
            }
        }
        return false;
    }

    private static int increment(HashMap<String, Integer> hashMap, String str, int i) {
        Integer num = hashMap.get(str);
        if (num == null) {
            hashMap.put(str, 1);
        } else {
            hashMap.put(str, Integer.valueOf(num.intValue() + 1));
        }
        while (hashMap.size() > 1000) {
            Iterator<Map.Entry<String, Integer>> it = hashMap.entrySet().iterator();
            while (it.hasNext()) {
                if (it.next().getValue().intValue() <= i) {
                    it.remove();
                }
            }
            if (hashMap.size() > 1000) {
                i++;
            }
        }
        return i;
    }

    public String getTop(int i) {
        stopCollecting();
        return getTopTraces(i);
    }

    private String getTopTraces(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("Profiler: top ").append(i).append(" stack trace(s) of ");
        if (this.time > 0) {
            sb.append(" of ").append(TimeUnit.NANOSECONDS.toMillis(this.time)).append(" ms");
        }
        if (this.threadDumps > 0) {
            sb.append(" of ").append(this.threadDumps).append(" thread dumps");
        }
        sb.append(":").append(LINE_SEPARATOR);
        if (this.counts.size() == 0) {
            sb.append("(none)").append(LINE_SEPARATOR);
        }
        appendTop(sb, new HashMap(this.counts), i, this.total, false);
        sb.append("summary:").append(LINE_SEPARATOR);
        appendTop(sb, new HashMap(this.summary), i, this.total, true);
        sb.append('.');
        return sb.toString();
    }

    private static void appendTop(StringBuilder sb, HashMap<String, Integer> hashMap, int i, int i2, boolean z) {
        int i3 = 0;
        int i4 = 0;
        while (true) {
            int i5 = 0;
            Map.Entry<String, Integer> entry = null;
            for (Map.Entry<String, Integer> entry2 : hashMap.entrySet()) {
                if (entry2.getValue().intValue() > i5) {
                    entry = entry2;
                    i5 = entry2.getValue().intValue();
                }
            }
            if (entry != null) {
                hashMap.remove(entry.getKey());
                i3++;
                if (i3 >= i) {
                    if (entry.getValue().intValue() >= i4) {
                        i4 = entry.getValue().intValue();
                    } else {
                        return;
                    }
                }
                int intValue = entry.getValue().intValue();
                int max = (100 * intValue) / Math.max(i2, 1);
                if (z) {
                    if (max > 1) {
                        sb.append(max).append("%: ").append(entry.getKey()).append(LINE_SEPARATOR);
                    }
                } else {
                    sb.append(intValue).append('/').append(i2).append(" (").append(max).append("%):").append(LINE_SEPARATOR).append(entry.getKey()).append(LINE_SEPARATOR);
                }
            } else {
                return;
            }
        }
    }
}
