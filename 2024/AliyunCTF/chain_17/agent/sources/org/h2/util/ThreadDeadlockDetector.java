package org.h2.util;

import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.h2.engine.SysProperties;
import org.h2.mvstore.db.MVTable;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/ThreadDeadlockDetector.class */
public class ThreadDeadlockDetector {
    private static final String INDENT = "    ";
    private static ThreadDeadlockDetector detector;
    private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

    private ThreadDeadlockDetector() {
        new Timer("ThreadDeadlockDetector", true).schedule(new TimerTask() { // from class: org.h2.util.ThreadDeadlockDetector.1
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                ThreadDeadlockDetector.this.checkForDeadlocks();
            }
        }, 10L, AbstractComponentTracker.LINGERING_TIMEOUT);
    }

    public static synchronized void init() {
        if (detector == null) {
            detector = new ThreadDeadlockDetector();
        }
    }

    void checkForDeadlocks() {
        long[] findDeadlockedThreads = this.threadBean.findDeadlockedThreads();
        if (findDeadlockedThreads == null) {
            return;
        }
        dumpThreadsAndLocks("ThreadDeadlockDetector - deadlock found :", this.threadBean, findDeadlockedThreads, System.out);
    }

    public static void dumpAllThreadsAndLocks(String str) {
        dumpAllThreadsAndLocks(str, System.out);
    }

    public static void dumpAllThreadsAndLocks(String str, PrintStream printStream) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        dumpThreadsAndLocks(str, threadMXBean, threadMXBean.getAllThreadIds(), printStream);
    }

    private static void dumpThreadsAndLocks(String str, ThreadMXBean threadMXBean, long[] jArr, PrintStream printStream) {
        HashMap<Long, String> hashMap;
        HashMap<Long, ArrayList<String>> hashMap2;
        HashMap<Long, ArrayList<String>> hashMap3;
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.println(str);
        if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
            hashMap = MVTable.WAITING_FOR_LOCK.getSnapshotOfAllThreads();
            hashMap2 = MVTable.EXCLUSIVE_LOCKS.getSnapshotOfAllThreads();
            hashMap3 = MVTable.SHARED_LOCKS.getSnapshotOfAllThreads();
        } else {
            hashMap = new HashMap<>();
            hashMap2 = new HashMap<>();
            hashMap3 = new HashMap<>();
        }
        for (ThreadInfo threadInfo : threadMXBean.getThreadInfo(jArr, true, true)) {
            printThreadInfo(printWriter, threadInfo);
            printLockInfo(printWriter, threadInfo.getLockedSynchronizers(), hashMap.get(Long.valueOf(threadInfo.getThreadId())), hashMap2.get(Long.valueOf(threadInfo.getThreadId())), hashMap3.get(Long.valueOf(threadInfo.getThreadId())));
        }
        printWriter.flush();
        printStream.println(stringWriter.getBuffer());
        printStream.flush();
    }

    private static void printThreadInfo(PrintWriter printWriter, ThreadInfo threadInfo) {
        printThread(printWriter, threadInfo);
        StackTraceElement[] stackTrace = threadInfo.getStackTrace();
        MonitorInfo[] lockedMonitors = threadInfo.getLockedMonitors();
        for (int i = 0; i < stackTrace.length; i++) {
            printWriter.println("    at " + stackTrace[i].toString());
            for (MonitorInfo monitorInfo : lockedMonitors) {
                if (monitorInfo.getLockedStackDepth() == i) {
                    printWriter.println("      - locked " + monitorInfo);
                }
            }
        }
        printWriter.println();
    }

    private static void printThread(PrintWriter printWriter, ThreadInfo threadInfo) {
        printWriter.print("\"" + threadInfo.getThreadName() + "\" Id=" + threadInfo.getThreadId() + " in " + threadInfo.getThreadState());
        if (threadInfo.getLockName() != null) {
            printWriter.append(" on lock=").append((CharSequence) threadInfo.getLockName());
        }
        if (threadInfo.isSuspended()) {
            printWriter.append(" (suspended)");
        }
        if (threadInfo.isInNative()) {
            printWriter.append(" (running in native)");
        }
        printWriter.println();
        if (threadInfo.getLockOwnerName() != null) {
            printWriter.println("     owned by " + threadInfo.getLockOwnerName() + " Id=" + threadInfo.getLockOwnerId());
        }
    }

    private static void printLockInfo(PrintWriter printWriter, LockInfo[] lockInfoArr, String str, ArrayList<String> arrayList, ArrayList<String> arrayList2) {
        printWriter.println("    Locked synchronizers: count = " + lockInfoArr.length);
        for (LockInfo lockInfo : lockInfoArr) {
            printWriter.println("      - " + lockInfo);
        }
        if (str != null) {
            printWriter.println("    Waiting for table: " + str);
        }
        if (arrayList != null) {
            printWriter.println("    Exclusive table locks: count = " + arrayList.size());
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                printWriter.println("      - " + it.next());
            }
        }
        if (arrayList2 != null) {
            printWriter.println("    Shared table locks: count = " + arrayList2.size());
            Iterator<String> it2 = arrayList2.iterator();
            while (it2.hasNext()) {
                printWriter.println("      - " + it2.next());
            }
        }
        printWriter.println();
    }
}
