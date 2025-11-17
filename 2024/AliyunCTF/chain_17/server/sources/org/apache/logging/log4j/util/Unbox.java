package org.apache.logging.log4j.util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

@PerformanceSensitive({"allocation"})
/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/Unbox.class */
public class Unbox {
    private static final int BITS_PER_INT = 32;
    private static final int RINGBUFFER_MIN_SIZE = 32;
    private static final Logger LOGGER = StatusLogger.getLogger();
    private static final int RINGBUFFER_SIZE = calculateRingBufferSize("log4j.unbox.ringbuffer.size");
    private static final int MASK = RINGBUFFER_SIZE - 1;
    private static ThreadLocal<State> threadLocalState = new ThreadLocal<>();
    private static WebSafeState webSafeState = new WebSafeState();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/Unbox$WebSafeState.class */
    public static class WebSafeState {
        private final ThreadLocal<StringBuilder[]> ringBuffer;
        private final ThreadLocal<int[]> current;

        private WebSafeState() {
            this.ringBuffer = new ThreadLocal<>();
            this.current = new ThreadLocal<>();
        }

        public StringBuilder getStringBuilder() {
            StringBuilder[] array = this.ringBuffer.get();
            if (array == null) {
                array = new StringBuilder[Unbox.RINGBUFFER_SIZE];
                for (int i = 0; i < array.length; i++) {
                    array[i] = new StringBuilder(21);
                }
                this.ringBuffer.set(array);
                this.current.set(new int[1]);
            }
            int[] index = this.current.get();
            int i2 = Unbox.MASK;
            int i3 = index[0];
            index[0] = i3 + 1;
            StringBuilder result = array[i2 & i3];
            result.setLength(0);
            return result;
        }

        public boolean isBoxedPrimitive(final StringBuilder text) {
            StringBuilder[] array = this.ringBuffer.get();
            if (array == null) {
                return false;
            }
            for (StringBuilder sb : array) {
                if (text == sb) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/Unbox$State.class */
    public static class State {
        private final StringBuilder[] ringBuffer = new StringBuilder[Unbox.RINGBUFFER_SIZE];
        private int current;

        State() {
            for (int i = 0; i < this.ringBuffer.length; i++) {
                this.ringBuffer[i] = new StringBuilder(21);
            }
        }

        public StringBuilder getStringBuilder() {
            StringBuilder[] sbArr = this.ringBuffer;
            int i = Unbox.MASK;
            int i2 = this.current;
            this.current = i2 + 1;
            StringBuilder result = sbArr[i & i2];
            result.setLength(0);
            return result;
        }

        public boolean isBoxedPrimitive(final StringBuilder text) {
            for (int i = 0; i < this.ringBuffer.length; i++) {
                if (text == this.ringBuffer[i]) {
                    return true;
                }
            }
            return false;
        }
    }

    private Unbox() {
    }

    private static int calculateRingBufferSize(final String propertyName) {
        String userPreferredRBSize = PropertiesUtil.getProperties().getStringProperty(propertyName, String.valueOf(32));
        try {
            int size = Integer.parseInt(userPreferredRBSize.trim());
            if (size < 32) {
                size = 32;
                LOGGER.warn("Invalid {} {}, using minimum size {}.", (Object) propertyName, (Object) userPreferredRBSize, (Object) 32);
            }
            return ceilingNextPowerOfTwo(size);
        } catch (Exception e) {
            LOGGER.warn("Invalid {} {}, using default size {}.", (Object) propertyName, (Object) userPreferredRBSize, (Object) 32);
            return 32;
        }
    }

    private static int ceilingNextPowerOfTwo(final int x) {
        return 1 << (32 - Integer.numberOfLeadingZeros(x - 1));
    }

    @PerformanceSensitive({"allocation"})
    public static StringBuilder box(final float value) {
        return getSB().append(value);
    }

    @PerformanceSensitive({"allocation"})
    public static StringBuilder box(final double value) {
        return getSB().append(value);
    }

    @PerformanceSensitive({"allocation"})
    public static StringBuilder box(final short value) {
        return getSB().append((int) value);
    }

    @PerformanceSensitive({"allocation"})
    public static StringBuilder box(final int value) {
        return getSB().append(value);
    }

    @PerformanceSensitive({"allocation"})
    public static StringBuilder box(final char value) {
        return getSB().append(value);
    }

    @PerformanceSensitive({"allocation"})
    public static StringBuilder box(final long value) {
        return getSB().append(value);
    }

    @PerformanceSensitive({"allocation"})
    public static StringBuilder box(final byte value) {
        return getSB().append((int) value);
    }

    @PerformanceSensitive({"allocation"})
    public static StringBuilder box(final boolean value) {
        return getSB().append(value);
    }

    private static State getState() {
        State state = threadLocalState.get();
        if (state == null) {
            state = new State();
            threadLocalState.set(state);
        }
        return state;
    }

    private static StringBuilder getSB() {
        return Constants.ENABLE_THREADLOCALS ? getState().getStringBuilder() : webSafeState.getStringBuilder();
    }

    static int getRingbufferSize() {
        return RINGBUFFER_SIZE;
    }
}
