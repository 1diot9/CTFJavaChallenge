package org.apache.logging.log4j.message;

import java.lang.Thread;
import org.apache.logging.log4j.util.StringBuilders;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/BasicThreadInformation.class */
class BasicThreadInformation implements ThreadInformation {
    private static final int HASH_SHIFT = 32;
    private static final int HASH_MULTIPLIER = 31;
    private final long id;
    private final String name;
    private final String longName;
    private final Thread.State state;
    private final int priority;
    private final boolean isAlive;
    private final boolean isDaemon;
    private final String threadGroupName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BasicThreadInformation(final Thread thread) {
        this.id = thread.getId();
        this.name = thread.getName();
        this.longName = thread.toString();
        this.state = thread.getState();
        this.priority = thread.getPriority();
        this.isAlive = thread.isAlive();
        this.isDaemon = thread.isDaemon();
        ThreadGroup group = thread.getThreadGroup();
        this.threadGroupName = group == null ? null : group.getName();
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicThreadInformation that = (BasicThreadInformation) o;
        if (this.id != that.id) {
            return false;
        }
        if (this.name != null) {
            if (!this.name.equals(that.name)) {
                return false;
            }
            return true;
        }
        if (that.name != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (int) (this.id ^ (this.id >>> 32));
        return (31 * result) + (this.name != null ? this.name.hashCode() : 0);
    }

    @Override // org.apache.logging.log4j.message.ThreadInformation
    public void printThreadInfo(final StringBuilder sb) {
        StringBuilders.appendDqValue(sb, this.name).append(' ');
        if (this.isDaemon) {
            sb.append("daemon ");
        }
        sb.append("prio=").append(this.priority).append(" tid=").append(this.id).append(' ');
        if (this.threadGroupName != null) {
            StringBuilders.appendKeyDqValue(sb, "group", this.threadGroupName);
        }
        sb.append('\n');
        sb.append("\tThread state: ").append(this.state.name()).append('\n');
    }

    @Override // org.apache.logging.log4j.message.ThreadInformation
    public void printStack(final StringBuilder sb, final StackTraceElement[] trace) {
        for (StackTraceElement element : trace) {
            sb.append("\tat ").append(element).append('\n');
        }
    }
}
