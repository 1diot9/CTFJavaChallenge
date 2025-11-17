package org.apache.tomcat.util;

import cn.hutool.core.date.DatePattern;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.MonitorInfo;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.PlatformLoggingMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.Constants;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/Diagnostics.class */
public class Diagnostics {
    private static final String INDENT1 = "  ";
    private static final String INDENT2 = "\t";
    private static final String INDENT3 = "   ";
    private static final String CRLF = "\r\n";
    private static final String vminfoSystemProperty = "java.vm.info";
    private static final String PACKAGE = "org.apache.tomcat.util";
    private static final StringManager sm = StringManager.getManager(PACKAGE);
    private static final Log log = LogFactory.getLog((Class<?>) Diagnostics.class);
    private static final SimpleDateFormat timeformat = new SimpleDateFormat(DatePattern.NORM_DATETIME_MS_PATTERN);
    private static final ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
    private static final CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
    private static final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
    private static final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    private static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private static final PlatformLoggingMXBean loggingMXBean = ManagementFactory.getPlatformMXBean(PlatformLoggingMXBean.class);
    private static final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private static final List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
    private static final List<MemoryManagerMXBean> memoryManagerMXBeans = ManagementFactory.getMemoryManagerMXBeans();
    private static final List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();

    public static boolean isThreadContentionMonitoringEnabled() {
        return threadMXBean.isThreadContentionMonitoringEnabled();
    }

    public static void setThreadContentionMonitoringEnabled(boolean enable) {
        threadMXBean.setThreadContentionMonitoringEnabled(enable);
        boolean checkValue = threadMXBean.isThreadContentionMonitoringEnabled();
        if (enable != checkValue) {
            log.error("Could not set threadContentionMonitoringEnabled to " + enable + ", got " + checkValue + " instead");
        }
    }

    public static boolean isThreadCpuTimeEnabled() {
        return threadMXBean.isThreadCpuTimeEnabled();
    }

    public static void setThreadCpuTimeEnabled(boolean enable) {
        threadMXBean.setThreadCpuTimeEnabled(enable);
        boolean checkValue = threadMXBean.isThreadCpuTimeEnabled();
        if (enable != checkValue) {
            log.error("Could not set threadCpuTimeEnabled to " + enable + ", got " + checkValue + " instead");
        }
    }

    public static void resetPeakThreadCount() {
        threadMXBean.resetPeakThreadCount();
    }

    public static void setVerboseClassLoading(boolean verbose) {
        classLoadingMXBean.setVerbose(verbose);
        boolean checkValue = classLoadingMXBean.isVerbose();
        if (verbose != checkValue) {
            log.error("Could not set verbose class loading to " + verbose + ", got " + checkValue + " instead");
        }
    }

    public static void setLoggerLevel(String loggerName, String levelName) {
        loggingMXBean.setLoggerLevel(loggerName, levelName);
        String checkValue = loggingMXBean.getLoggerLevel(loggerName);
        if (!checkValue.equals(levelName)) {
            log.error("Could not set logger level for logger '" + loggerName + "' to '" + levelName + "', got '" + checkValue + "' instead");
        }
    }

    public static void setVerboseGarbageCollection(boolean verbose) {
        memoryMXBean.setVerbose(verbose);
        boolean checkValue = memoryMXBean.isVerbose();
        if (verbose != checkValue) {
            log.error("Could not set verbose garbage collection logging to " + verbose + ", got " + checkValue + " instead");
        }
    }

    public static void gc() {
        memoryMXBean.gc();
    }

    public static void resetPeakUsage(String name) {
        for (MemoryPoolMXBean mbean : memoryPoolMXBeans) {
            if (name.equals(Constants.SSL_PROTO_ALL) || name.equals(mbean.getName())) {
                mbean.resetPeakUsage();
            }
        }
    }

    public static boolean setUsageThreshold(String name, long threshold) {
        for (MemoryPoolMXBean mbean : memoryPoolMXBeans) {
            if (name.equals(mbean.getName())) {
                try {
                    mbean.setUsageThreshold(threshold);
                    return true;
                } catch (IllegalArgumentException | UnsupportedOperationException e) {
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean setCollectionUsageThreshold(String name, long threshold) {
        for (MemoryPoolMXBean mbean : memoryPoolMXBeans) {
            if (name.equals(mbean.getName())) {
                try {
                    mbean.setCollectionUsageThreshold(threshold);
                    return true;
                } catch (IllegalArgumentException | UnsupportedOperationException e) {
                    return false;
                }
            }
        }
        return false;
    }

    private static String getThreadDumpHeader(ThreadInfo ti) {
        StringBuilder sb = new StringBuilder("\"" + ti.getThreadName() + "\"");
        sb.append(" Id=" + ti.getThreadId());
        sb.append(" cpu=" + threadMXBean.getThreadCpuTime(ti.getThreadId()) + " ns");
        sb.append(" usr=" + threadMXBean.getThreadUserTime(ti.getThreadId()) + " ns");
        long blockedCount = ti.getBlockedCount();
        ti.getBlockedTime();
        sb.append(" blocked " + blockedCount + " for " + sb + " ms");
        long waitedCount = ti.getWaitedCount();
        ti.getWaitedTime();
        sb.append(" waited " + waitedCount + " for " + sb + " ms");
        if (ti.isSuspended()) {
            sb.append(" (suspended)");
        }
        if (ti.isInNative()) {
            sb.append(" (running in native)");
        }
        sb.append("\r\n");
        sb.append("   java.lang.Thread.State: " + ti.getThreadState());
        sb.append("\r\n");
        return sb.toString();
    }

    private static String getThreadDump(ThreadInfo ti) {
        StringBuilder sb = new StringBuilder(getThreadDumpHeader(ti));
        for (LockInfo li : ti.getLockedSynchronizers()) {
            sb.append("\tlocks " + li.toString() + "\r\n");
        }
        boolean start = true;
        StackTraceElement[] stes = ti.getStackTrace();
        Object[] monitorDepths = new Object[stes.length];
        MonitorInfo[] mis = ti.getLockedMonitors();
        for (MonitorInfo monitorInfo : mis) {
            monitorDepths[monitorInfo.getLockedStackDepth()] = monitorInfo;
        }
        for (int i = 0; i < stes.length; i++) {
            StackTraceElement ste = stes[i];
            sb.append("\tat " + ste.toString() + "\r\n");
            if (start) {
                if (ti.getLockName() != null) {
                    sb.append("\t- waiting on (a " + ti.getLockName() + ")");
                    if (ti.getLockOwnerName() != null) {
                        sb.append(" owned by " + ti.getLockOwnerName() + " Id=" + ti.getLockOwnerId());
                    }
                    sb.append("\r\n");
                }
                start = false;
            }
            if (monitorDepths[i] != null) {
                MonitorInfo mi = (MonitorInfo) monitorDepths[i];
                sb.append("\t- locked (a " + mi.toString() + ") index " + mi.getLockedStackDepth() + " frame " + mi.getLockedStackFrame().toString());
                sb.append("\r\n");
            }
        }
        return sb.toString();
    }

    private static String getThreadDump(ThreadInfo[] tinfos) {
        StringBuilder sb = new StringBuilder();
        for (ThreadInfo tinfo : tinfos) {
            sb.append(getThreadDump(tinfo));
            sb.append("\r\n");
        }
        return sb.toString();
    }

    public static String findDeadlock() {
        ThreadInfo[] tinfos;
        long[] ids = threadMXBean.findDeadlockedThreads();
        if (ids != null && (tinfos = threadMXBean.getThreadInfo(threadMXBean.findDeadlockedThreads(), true, true)) != null) {
            return "Deadlock found between the following threads:\r\n" + getThreadDump(tinfos);
        }
        return "";
    }

    public static String getThreadDump() {
        return getThreadDump(sm);
    }

    public static String getThreadDump(Enumeration<Locale> requestedLocales) {
        return getThreadDump(StringManager.getManager(PACKAGE, requestedLocales));
    }

    public static String getThreadDump(StringManager requestedSm) {
        StringBuilder sb = new StringBuilder();
        synchronized (timeformat) {
            sb.append(timeformat.format(new Date()));
        }
        sb.append("\r\n");
        sb.append(requestedSm.getString("diagnostics.threadDumpTitle"));
        sb.append(' ');
        sb.append(runtimeMXBean.getVmName());
        sb.append(" (");
        sb.append(runtimeMXBean.getVmVersion());
        String vminfo = System.getProperty(vminfoSystemProperty);
        if (vminfo != null) {
            sb.append(" " + vminfo);
        }
        sb.append("):\r\n");
        sb.append("\r\n");
        ThreadInfo[] tis = threadMXBean.dumpAllThreads(true, true);
        sb.append(getThreadDump(tis));
        sb.append(findDeadlock());
        return sb.toString();
    }

    private static String formatMemoryUsage(String name, MemoryUsage usage) {
        if (usage != null) {
            return ("  " + name + " init: " + usage.getInit() + "\r\n") + ("  " + name + " used: " + usage.getUsed() + "\r\n") + ("  " + name + " committed: " + usage.getCommitted() + "\r\n") + ("  " + name + " max: " + usage.getMax() + "\r\n");
        }
        return "";
    }

    public static String getVMInfo() {
        return getVMInfo(sm);
    }

    public static String getVMInfo(Enumeration<Locale> requestedLocales) {
        return getVMInfo(StringManager.getManager(PACKAGE, requestedLocales));
    }

    public static String getVMInfo(StringManager requestedSm) {
        StringBuilder sb = new StringBuilder();
        synchronized (timeformat) {
            sb.append(timeformat.format(new Date()));
        }
        sb.append("\r\n");
        sb.append(requestedSm.getString("diagnostics.vmInfoRuntime"));
        sb.append(":\r\n");
        sb.append("  vmName: " + runtimeMXBean.getVmName() + "\r\n");
        sb.append("  vmVersion: " + runtimeMXBean.getVmVersion() + "\r\n");
        sb.append("  vmVendor: " + runtimeMXBean.getVmVendor() + "\r\n");
        sb.append("  specName: " + runtimeMXBean.getSpecName() + "\r\n");
        sb.append("  specVersion: " + runtimeMXBean.getSpecVersion() + "\r\n");
        sb.append("  specVendor: " + runtimeMXBean.getSpecVendor() + "\r\n");
        sb.append("  managementSpecVersion: " + runtimeMXBean.getManagementSpecVersion() + "\r\n");
        sb.append("  name: " + runtimeMXBean.getName() + "\r\n");
        sb.append("  startTime: " + runtimeMXBean.getStartTime() + "\r\n");
        sb.append("  uptime: " + runtimeMXBean.getUptime() + "\r\n");
        sb.append("  isBootClassPathSupported: " + runtimeMXBean.isBootClassPathSupported() + "\r\n");
        sb.append("\r\n");
        sb.append(requestedSm.getString("diagnostics.vmInfoOs"));
        sb.append(":\r\n");
        sb.append("  name: " + operatingSystemMXBean.getName() + "\r\n");
        sb.append("  version: " + operatingSystemMXBean.getVersion() + "\r\n");
        sb.append("  architecture: " + operatingSystemMXBean.getArch() + "\r\n");
        sb.append("  availableProcessors: " + operatingSystemMXBean.getAvailableProcessors() + "\r\n");
        sb.append("  systemLoadAverage: " + operatingSystemMXBean.getSystemLoadAverage() + "\r\n");
        sb.append("\r\n");
        sb.append(requestedSm.getString("diagnostics.vmInfoThreadMxBean"));
        sb.append(":\r\n");
        sb.append("  isCurrentThreadCpuTimeSupported: " + threadMXBean.isCurrentThreadCpuTimeSupported() + "\r\n");
        sb.append("  isThreadCpuTimeSupported: " + threadMXBean.isThreadCpuTimeSupported() + "\r\n");
        sb.append("  isThreadCpuTimeEnabled: " + threadMXBean.isThreadCpuTimeEnabled() + "\r\n");
        sb.append("  isObjectMonitorUsageSupported: " + threadMXBean.isObjectMonitorUsageSupported() + "\r\n");
        sb.append("  isSynchronizerUsageSupported: " + threadMXBean.isSynchronizerUsageSupported() + "\r\n");
        sb.append("  isThreadContentionMonitoringSupported: " + threadMXBean.isThreadContentionMonitoringSupported() + "\r\n");
        sb.append("  isThreadContentionMonitoringEnabled: " + threadMXBean.isThreadContentionMonitoringEnabled() + "\r\n");
        sb.append("\r\n");
        sb.append(requestedSm.getString("diagnostics.vmInfoThreadCounts"));
        sb.append(":\r\n");
        sb.append("  daemon: " + threadMXBean.getDaemonThreadCount() + "\r\n");
        sb.append("  total: " + threadMXBean.getThreadCount() + "\r\n");
        sb.append("  peak: " + threadMXBean.getPeakThreadCount() + "\r\n");
        sb.append("  totalStarted: " + threadMXBean.getTotalStartedThreadCount() + "\r\n");
        sb.append("\r\n");
        sb.append(requestedSm.getString("diagnostics.vmInfoStartup"));
        sb.append(":\r\n");
        for (String arg : runtimeMXBean.getInputArguments()) {
            sb.append("  " + arg + "\r\n");
        }
        sb.append("\r\n");
        sb.append(requestedSm.getString("diagnostics.vmInfoPath"));
        sb.append(":\r\n");
        if (runtimeMXBean.isBootClassPathSupported()) {
            sb.append("  bootClassPath: " + runtimeMXBean.getBootClassPath() + "\r\n");
        }
        sb.append("  classPath: " + runtimeMXBean.getClassPath() + "\r\n");
        sb.append("  libraryPath: " + runtimeMXBean.getLibraryPath() + "\r\n");
        sb.append("\r\n");
        sb.append(requestedSm.getString("diagnostics.vmInfoClassLoading"));
        sb.append(":\r\n");
        sb.append("  loaded: " + classLoadingMXBean.getLoadedClassCount() + "\r\n");
        sb.append("  unloaded: " + classLoadingMXBean.getUnloadedClassCount() + "\r\n");
        sb.append("  totalLoaded: " + classLoadingMXBean.getTotalLoadedClassCount() + "\r\n");
        sb.append("  isVerbose: " + classLoadingMXBean.isVerbose() + "\r\n");
        sb.append("\r\n");
        sb.append(requestedSm.getString("diagnostics.vmInfoClassCompilation"));
        sb.append(":\r\n");
        sb.append("  name: " + compilationMXBean.getName() + "\r\n");
        sb.append("  totalCompilationTime: " + compilationMXBean.getTotalCompilationTime() + "\r\n");
        sb.append("  isCompilationTimeMonitoringSupported: " + compilationMXBean.isCompilationTimeMonitoringSupported() + "\r\n");
        sb.append("\r\n");
        for (MemoryManagerMXBean mbean : memoryManagerMXBeans) {
            sb.append(requestedSm.getString("diagnostics.vmInfoMemoryManagers", mbean.getName()));
            sb.append(":\r\n");
            sb.append("  isValid: " + mbean.isValid() + "\r\n");
            sb.append("  mbean.getMemoryPoolNames: \r\n");
            String[] names = mbean.getMemoryPoolNames();
            Arrays.sort(names);
            for (String name : names) {
                sb.append("\t" + name + "\r\n");
            }
            sb.append("\r\n");
        }
        for (GarbageCollectorMXBean mbean2 : garbageCollectorMXBeans) {
            sb.append(requestedSm.getString("diagnostics.vmInfoGarbageCollectors", mbean2.getName()));
            sb.append(":\r\n");
            sb.append("  isValid: " + mbean2.isValid() + "\r\n");
            sb.append("  mbean.getMemoryPoolNames: \r\n");
            String[] names2 = mbean2.getMemoryPoolNames();
            Arrays.sort(names2);
            for (String name2 : names2) {
                sb.append("\t" + name2 + "\r\n");
            }
            sb.append("  getCollectionCount: " + mbean2.getCollectionCount() + "\r\n");
            sb.append("  getCollectionTime: " + mbean2.getCollectionTime() + "\r\n");
            sb.append("\r\n");
        }
        sb.append(requestedSm.getString("diagnostics.vmInfoMemory"));
        sb.append(":\r\n");
        sb.append("  isVerbose: " + memoryMXBean.isVerbose() + "\r\n");
        sb.append("  getObjectPendingFinalizationCount: " + memoryMXBean.getObjectPendingFinalizationCount() + "\r\n");
        sb.append(formatMemoryUsage("heap", memoryMXBean.getHeapMemoryUsage()));
        sb.append(formatMemoryUsage("non-heap", memoryMXBean.getNonHeapMemoryUsage()));
        sb.append("\r\n");
        for (MemoryPoolMXBean mbean3 : memoryPoolMXBeans) {
            sb.append(requestedSm.getString("diagnostics.vmInfoMemoryPools", mbean3.getName()));
            sb.append(":\r\n");
            sb.append("  isValid: " + mbean3.isValid() + "\r\n");
            sb.append("  getType: " + mbean3.getType() + "\r\n");
            sb.append("  mbean.getMemoryManagerNames: \r\n");
            String[] names3 = mbean3.getMemoryManagerNames();
            Arrays.sort(names3);
            for (String name3 : names3) {
                sb.append("\t" + name3 + "\r\n");
            }
            sb.append("  isUsageThresholdSupported: " + mbean3.isUsageThresholdSupported() + "\r\n");
            try {
                sb.append("  isUsageThresholdExceeded: " + mbean3.isUsageThresholdExceeded() + "\r\n");
            } catch (UnsupportedOperationException e) {
            }
            sb.append("  isCollectionUsageThresholdSupported: " + mbean3.isCollectionUsageThresholdSupported() + "\r\n");
            try {
                sb.append("  isCollectionUsageThresholdExceeded: " + mbean3.isCollectionUsageThresholdExceeded() + "\r\n");
            } catch (UnsupportedOperationException e2) {
            }
            try {
                sb.append("  getUsageThreshold: " + mbean3.getUsageThreshold() + "\r\n");
            } catch (UnsupportedOperationException e3) {
            }
            try {
                sb.append("  getUsageThresholdCount: " + mbean3.getUsageThresholdCount() + "\r\n");
            } catch (UnsupportedOperationException e4) {
            }
            try {
                sb.append("  getCollectionUsageThreshold: " + mbean3.getCollectionUsageThreshold() + "\r\n");
            } catch (UnsupportedOperationException e5) {
            }
            try {
                sb.append("  getCollectionUsageThresholdCount: " + mbean3.getCollectionUsageThresholdCount() + "\r\n");
            } catch (UnsupportedOperationException e6) {
            }
            sb.append(formatMemoryUsage("current", mbean3.getUsage()));
            sb.append(formatMemoryUsage("collection", mbean3.getCollectionUsage()));
            sb.append(formatMemoryUsage("peak", mbean3.getPeakUsage()));
            sb.append("\r\n");
        }
        sb.append(requestedSm.getString("diagnostics.vmInfoSystem"));
        sb.append(":\r\n");
        Map<String, String> props = runtimeMXBean.getSystemProperties();
        ArrayList<String> keys = new ArrayList<>(props.keySet());
        Collections.sort(keys);
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String prop = it.next();
            sb.append("  " + prop + ": " + props.get(prop) + "\r\n");
        }
        sb.append("\r\n");
        sb.append(requestedSm.getString("diagnostics.vmInfoLogger"));
        sb.append(":\r\n");
        List<String> loggers = loggingMXBean.getLoggerNames();
        Collections.sort(loggers);
        for (String logger : loggers) {
            sb.append("  " + logger + ": level=" + loggingMXBean.getLoggerLevel(logger) + ", parent=" + loggingMXBean.getParentLoggerName(logger) + "\r\n");
        }
        sb.append("\r\n");
        return sb.toString();
    }
}
