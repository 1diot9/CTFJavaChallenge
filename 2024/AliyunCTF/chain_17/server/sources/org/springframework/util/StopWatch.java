package org.springframework.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/StopWatch.class */
public class StopWatch {
    private final String id;

    @Nullable
    private List<TaskInfo> taskList;
    private long startTimeNanos;

    @Nullable
    private String currentTaskName;

    @Nullable
    private TaskInfo lastTaskInfo;
    private int taskCount;
    private long totalTimeNanos;

    public StopWatch() {
        this("");
    }

    public StopWatch(String id) {
        this.taskList = new ArrayList(1);
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setKeepTaskList(boolean keepTaskList) {
        this.taskList = keepTaskList ? new ArrayList() : null;
    }

    public void start() throws IllegalStateException {
        start("");
    }

    public void start(String taskName) throws IllegalStateException {
        if (this.currentTaskName != null) {
            throw new IllegalStateException("Can't start StopWatch: it's already running");
        }
        this.currentTaskName = taskName;
        this.startTimeNanos = System.nanoTime();
    }

    public void stop() throws IllegalStateException {
        if (this.currentTaskName == null) {
            throw new IllegalStateException("Can't stop StopWatch: it's not running");
        }
        long lastTime = System.nanoTime() - this.startTimeNanos;
        this.totalTimeNanos += lastTime;
        this.lastTaskInfo = new TaskInfo(this.currentTaskName, lastTime);
        if (this.taskList != null) {
            this.taskList.add(this.lastTaskInfo);
        }
        this.taskCount++;
        this.currentTaskName = null;
    }

    public boolean isRunning() {
        return this.currentTaskName != null;
    }

    @Nullable
    public String currentTaskName() {
        return this.currentTaskName;
    }

    public TaskInfo lastTaskInfo() throws IllegalStateException {
        Assert.state(this.lastTaskInfo != null, "No tasks run");
        return this.lastTaskInfo;
    }

    @Deprecated(since = "6.1")
    public TaskInfo getLastTaskInfo() throws IllegalStateException {
        return lastTaskInfo();
    }

    @Deprecated(since = "6.1")
    public String getLastTaskName() throws IllegalStateException {
        return lastTaskInfo().getTaskName();
    }

    @Deprecated(since = "6.1")
    public long getLastTaskTimeNanos() throws IllegalStateException {
        return lastTaskInfo().getTimeNanos();
    }

    @Deprecated(since = "6.1")
    public long getLastTaskTimeMillis() throws IllegalStateException {
        return lastTaskInfo().getTimeMillis();
    }

    public TaskInfo[] getTaskInfo() {
        if (this.taskList == null) {
            throw new UnsupportedOperationException("Task info is not being kept!");
        }
        return (TaskInfo[]) this.taskList.toArray(new TaskInfo[0]);
    }

    public int getTaskCount() {
        return this.taskCount;
    }

    public long getTotalTimeNanos() {
        return this.totalTimeNanos;
    }

    public long getTotalTimeMillis() {
        return TimeUnit.NANOSECONDS.toMillis(this.totalTimeNanos);
    }

    public double getTotalTimeSeconds() {
        return getTotalTime(TimeUnit.SECONDS);
    }

    public double getTotalTime(TimeUnit timeUnit) {
        return this.totalTimeNanos / TimeUnit.NANOSECONDS.convert(1L, timeUnit);
    }

    public String prettyPrint() {
        return prettyPrint(TimeUnit.SECONDS);
    }

    public String prettyPrint(TimeUnit timeUnit) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        nf.setMaximumFractionDigits(9);
        nf.setGroupingUsed(false);
        NumberFormat pf = NumberFormat.getPercentInstance(Locale.ENGLISH);
        pf.setMinimumIntegerDigits(2);
        pf.setGroupingUsed(false);
        StringBuilder sb = new StringBuilder(128);
        sb.append("StopWatch '").append(getId()).append("': ");
        String total = timeUnit == TimeUnit.NANOSECONDS ? nf.format(getTotalTimeNanos()) : nf.format(getTotalTime(timeUnit));
        sb.append(total).append(" ").append(timeUnit.name().toLowerCase(Locale.ENGLISH));
        int width = Math.max(sb.length(), 40);
        sb.append("\n");
        if (this.taskList != null) {
            String line = "-".repeat(width) + "\n";
            String unitName = timeUnit.name();
            String unitName2 = String.format("%-12s", unitName.charAt(0) + unitName.substring(1).toLowerCase(Locale.ENGLISH));
            sb.append(line);
            sb.append(unitName2).append("  %       Task name\n");
            sb.append(line);
            int digits = total.indexOf(46);
            if (digits < 0) {
                digits = total.length();
            }
            nf.setMinimumIntegerDigits(digits);
            nf.setMaximumFractionDigits(10 - digits);
            for (TaskInfo task : this.taskList) {
                Object[] objArr = new Object[1];
                objArr[0] = timeUnit == TimeUnit.NANOSECONDS ? nf.format(task.getTimeNanos()) : nf.format(task.getTime(timeUnit));
                sb.append(String.format("%-14s", objArr));
                sb.append(String.format("%-8s", pf.format(task.getTimeSeconds() / getTotalTimeSeconds())));
                sb.append(task.getTaskName()).append('\n');
            }
        } else {
            sb.append("No task info kept");
        }
        return sb.toString();
    }

    public String shortSummary() {
        return "StopWatch '" + getId() + "': " + getTotalTimeSeconds() + " seconds";
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(shortSummary());
        if (this.taskList != null) {
            for (TaskInfo task : this.taskList) {
                sb.append("; [").append(task.getTaskName()).append("] took ").append(task.getTimeSeconds()).append(" seconds");
                long percent = Math.round((100.0d * task.getTimeSeconds()) / getTotalTimeSeconds());
                sb.append(" = ").append(percent).append('%');
            }
        } else {
            sb.append("; no task info kept");
        }
        return sb.toString();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/StopWatch$TaskInfo.class */
    public static final class TaskInfo {
        private final String taskName;
        private final long timeNanos;

        TaskInfo(String taskName, long timeNanos) {
            this.taskName = taskName;
            this.timeNanos = timeNanos;
        }

        public String getTaskName() {
            return this.taskName;
        }

        public long getTimeNanos() {
            return this.timeNanos;
        }

        public long getTimeMillis() {
            return TimeUnit.NANOSECONDS.toMillis(this.timeNanos);
        }

        public double getTimeSeconds() {
            return getTime(TimeUnit.SECONDS);
        }

        public double getTime(TimeUnit timeUnit) {
            return this.timeNanos / TimeUnit.NANOSECONDS.convert(1L, timeUnit);
        }
    }
}
