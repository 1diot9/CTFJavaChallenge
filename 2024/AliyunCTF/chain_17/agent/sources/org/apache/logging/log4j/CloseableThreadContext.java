package org.apache.logging.log4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/CloseableThreadContext.class */
public class CloseableThreadContext {
    private CloseableThreadContext() {
    }

    public static Instance push(final String message) {
        return new Instance().push(message);
    }

    public static Instance push(final String message, final Object... args) {
        return new Instance().push(message, args);
    }

    public static Instance put(final String key, final String value) {
        return new Instance().put(key, value);
    }

    public static Instance pushAll(final List<String> messages) {
        return new Instance().pushAll(messages);
    }

    public static Instance putAll(final Map<String, String> values) {
        return new Instance().putAll(values);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/CloseableThreadContext$Instance.class */
    public static class Instance implements AutoCloseable {
        private int pushCount;
        private final Map<String, String> originalValues;

        private Instance() {
            this.pushCount = 0;
            this.originalValues = new HashMap();
        }

        public Instance push(final String message) {
            ThreadContext.push(message);
            this.pushCount++;
            return this;
        }

        public Instance push(final String message, final Object[] args) {
            ThreadContext.push(message, args);
            this.pushCount++;
            return this;
        }

        public Instance put(final String key, final String value) {
            if (!this.originalValues.containsKey(key)) {
                this.originalValues.put(key, ThreadContext.get(key));
            }
            ThreadContext.put(key, value);
            return this;
        }

        public Instance putAll(final Map<String, String> values) {
            Map<String, String> currentValues = ThreadContext.getContext();
            ThreadContext.putAll(values);
            for (String key : values.keySet()) {
                if (!this.originalValues.containsKey(key)) {
                    this.originalValues.put(key, currentValues.get(key));
                }
            }
            return this;
        }

        public Instance pushAll(final List<String> messages) {
            for (String message : messages) {
                push(message);
            }
            return this;
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            closeStack();
            closeMap();
        }

        private void closeMap() {
            Iterator<Map.Entry<String, String>> it = this.originalValues.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                String key = entry.getKey();
                String originalValue = entry.getValue();
                if (null == originalValue) {
                    ThreadContext.remove(key);
                } else {
                    ThreadContext.put(key, originalValue);
                }
                it.remove();
            }
        }

        private void closeStack() {
            for (int i = 0; i < this.pushCount; i++) {
                ThreadContext.pop();
            }
            this.pushCount = 0;
        }
    }
}
