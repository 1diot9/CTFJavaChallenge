package org.h2.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import java.lang.Thread;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/AbbaLockingDetector.class */
public class AbbaLockingDetector implements Runnable {
    private volatile boolean stop;
    private Thread thread;
    private final int tickIntervalMs = 2;
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private final Map<String, Map<String, String>> lockOrdering = new WeakHashMap();
    private final Set<String> knownDeadlocks = new HashSet();

    public AbbaLockingDetector startCollecting() {
        this.thread = new Thread(this, "AbbaLockingDetector");
        this.thread.setDaemon(true);
        this.thread.start();
        return this;
    }

    public synchronized void reset() {
        this.lockOrdering.clear();
        this.knownDeadlocks.clear();
    }

    public AbbaLockingDetector stopCollecting() {
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
        while (!this.stop) {
            try {
                tick();
            } catch (Throwable th) {
                return;
            }
        }
    }

    private void tick() {
        try {
            Thread.sleep(2L);
        } catch (InterruptedException e) {
        }
        processThreadList(this.threadMXBean.dumpAllThreads(true, false));
    }

    private void processThreadList(ThreadInfo[] threadInfoArr) {
        ArrayList arrayList = new ArrayList();
        for (ThreadInfo threadInfo : threadInfoArr) {
            arrayList.clear();
            generateOrdering(arrayList, threadInfo);
            if (arrayList.size() > 1) {
                markHigher(arrayList, threadInfo);
            }
        }
    }

    private static void generateOrdering(List<String> list, ThreadInfo threadInfo) {
        MonitorInfo[] lockedMonitors = threadInfo.getLockedMonitors();
        Arrays.sort(lockedMonitors, (monitorInfo, monitorInfo2) -> {
            return monitorInfo2.getLockedStackDepth() - monitorInfo.getLockedStackDepth();
        });
        for (MonitorInfo monitorInfo3 : lockedMonitors) {
            String objectName = getObjectName(monitorInfo3);
            if (!objectName.equals("sun.misc.Launcher$AppClassLoader") && !list.contains(objectName)) {
                list.add(objectName);
            }
        }
    }

    private synchronized void markHigher(List<String> list, ThreadInfo threadInfo) {
        String str;
        String str2 = list.get(list.size() - 1);
        Map<String, String> map = this.lockOrdering.get(str2);
        if (map == null) {
            map = new WeakHashMap();
            this.lockOrdering.put(str2, map);
        }
        String str3 = null;
        for (int i = 0; i < list.size() - 1; i++) {
            String str4 = list.get(i);
            Map<String, String> map2 = this.lockOrdering.get(str4);
            boolean z = false;
            if (map2 != null && (str = map2.get(str2)) != null) {
                z = true;
                String str5 = str2 + CharSequenceUtil.SPACE + str4;
                if (!this.knownDeadlocks.contains(str5)) {
                    System.out.println(str2 + " synchronized after \n " + str4 + ", but in the past before\nAFTER\n" + getStackTraceForThread(threadInfo) + "BEFORE\n" + str);
                    this.knownDeadlocks.add(str5);
                }
            }
            if (!z && !map.containsKey(str4)) {
                if (str3 == null) {
                    str3 = getStackTraceForThread(threadInfo);
                }
                map.put(str4, str3);
            }
        }
    }

    private static String getStackTraceForThread(ThreadInfo threadInfo) {
        StringBuilder append = new StringBuilder().append('\"').append(threadInfo.getThreadName()).append("\" Id=").append(threadInfo.getThreadId()).append(' ').append(threadInfo.getThreadState());
        if (threadInfo.getLockName() != null) {
            append.append(" on ").append(threadInfo.getLockName());
        }
        if (threadInfo.getLockOwnerName() != null) {
            append.append(" owned by \"").append(threadInfo.getLockOwnerName()).append("\" Id=").append(threadInfo.getLockOwnerId());
        }
        if (threadInfo.isSuspended()) {
            append.append(" (suspended)");
        }
        if (threadInfo.isInNative()) {
            append.append(" (in native)");
        }
        append.append('\n');
        StackTraceElement[] stackTrace = threadInfo.getStackTrace();
        MonitorInfo[] lockedMonitors = threadInfo.getLockedMonitors();
        boolean z = false;
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            if (z) {
                dumpStackTraceElement(threadInfo, append, i, stackTraceElement);
            }
            for (MonitorInfo monitorInfo : lockedMonitors) {
                if (monitorInfo.getLockedStackDepth() == i) {
                    if (!z) {
                        dumpStackTraceElement(threadInfo, append, i, stackTraceElement);
                        z = true;
                    }
                    append.append("\t-  locked ").append(monitorInfo);
                    append.append('\n');
                }
            }
        }
        return append.toString();
    }

    private static void dumpStackTraceElement(ThreadInfo threadInfo, StringBuilder sb, int i, StackTraceElement stackTraceElement) {
        sb.append('\t').append("at ").append(stackTraceElement).append('\n');
        if (i == 0 && threadInfo.getLockInfo() != null) {
            switch (AnonymousClass1.$SwitchMap$java$lang$Thread$State[threadInfo.getThreadState().ordinal()]) {
                case 1:
                    sb.append("\t-  blocked on ").append(threadInfo.getLockInfo()).append('\n');
                    return;
                case 2:
                    sb.append("\t-  waiting on ").append(threadInfo.getLockInfo()).append('\n');
                    return;
                case 3:
                    sb.append("\t-  waiting on ").append(threadInfo.getLockInfo()).append('\n');
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.h2.util.AbbaLockingDetector$1, reason: invalid class name */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/AbbaLockingDetector$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$lang$Thread$State = new int[Thread.State.values().length];

        static {
            try {
                $SwitchMap$java$lang$Thread$State[Thread.State.BLOCKED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$lang$Thread$State[Thread.State.WAITING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$lang$Thread$State[Thread.State.TIMED_WAITING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private static String getObjectName(MonitorInfo monitorInfo) {
        return monitorInfo.getClassName() + StrPool.AT + Integer.toHexString(monitorInfo.getIdentityHashCode());
    }
}
