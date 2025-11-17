package org.apache.logging.log4j.message;

import aQute.bnd.annotation.spi.ServiceConsumer;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.util.ServiceLoaderUtil;
import org.apache.logging.log4j.util.StringBuilderFormattable;

@AsynchronouslyFormattable
@ServiceConsumer(value = ThreadInfoFactory.class, resolution = "optional", cardinality = "single")
/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/ThreadDumpMessage.class */
public class ThreadDumpMessage implements Message, StringBuilderFormattable {
    private static final long serialVersionUID = -1103400781608841088L;
    private static ThreadInfoFactory FACTORY;
    private volatile Map<ThreadInformation, StackTraceElement[]> threads;
    private final String title;
    private String formattedMessage;

    /* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/ThreadDumpMessage$ThreadInfoFactory.class */
    public interface ThreadInfoFactory {
        Map<ThreadInformation, StackTraceElement[]> createThreadInfo();
    }

    public ThreadDumpMessage(final String title) {
        this.title = title == null ? "" : title;
        this.threads = getFactory().createThreadInfo();
    }

    private ThreadDumpMessage(final String formattedMsg, final String title) {
        this.formattedMessage = formattedMsg;
        this.title = title == null ? "" : title;
    }

    private static ThreadInfoFactory getFactory() {
        if (FACTORY == null) {
            FACTORY = initFactory();
        }
        return FACTORY;
    }

    private static ThreadInfoFactory initFactory() {
        return (ThreadInfoFactory) ServiceLoaderUtil.loadServices(ThreadInfoFactory.class, MethodHandles.lookup(), false).findFirst().orElseGet(() -> {
            return new BasicThreadInfoFactory();
        });
    }

    public String toString() {
        return getFormattedMessage();
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        if (this.formattedMessage != null) {
            return this.formattedMessage;
        }
        StringBuilder sb = new StringBuilder(255);
        formatTo(sb);
        return sb.toString();
    }

    @Override // org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(final StringBuilder sb) {
        sb.append(this.title);
        if (this.title.length() > 0) {
            sb.append('\n');
        }
        for (Map.Entry<ThreadInformation, StackTraceElement[]> entry : this.threads.entrySet()) {
            ThreadInformation info = entry.getKey();
            info.printThreadInfo(sb);
            info.printStack(sb, entry.getValue());
            sb.append('\n');
        }
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormat() {
        return this.title == null ? "" : this.title;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Object[] getParameters() {
        return null;
    }

    protected Object writeReplace() {
        return new ThreadDumpMessageProxy(this);
    }

    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    /* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/ThreadDumpMessage$ThreadDumpMessageProxy.class */
    private static class ThreadDumpMessageProxy implements Serializable {
        private static final long serialVersionUID = -3476620450287648269L;
        private final String formattedMsg;
        private final String title;

        ThreadDumpMessageProxy(final ThreadDumpMessage msg) {
            this.formattedMsg = msg.getFormattedMessage();
            this.title = msg.title;
        }

        protected Object readResolve() {
            return new ThreadDumpMessage(this.formattedMsg, this.title);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/ThreadDumpMessage$BasicThreadInfoFactory.class */
    public static class BasicThreadInfoFactory implements ThreadInfoFactory {
        private BasicThreadInfoFactory() {
        }

        @Override // org.apache.logging.log4j.message.ThreadDumpMessage.ThreadInfoFactory
        public Map<ThreadInformation, StackTraceElement[]> createThreadInfo() {
            Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
            Map<ThreadInformation, StackTraceElement[]> threads = new HashMap<>(map.size());
            for (Map.Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
                threads.put(new BasicThreadInformation(entry.getKey()), entry.getValue());
            }
            return threads;
        }
    }

    @Override // org.apache.logging.log4j.message.Message
    public Throwable getThrowable() {
        return null;
    }
}
