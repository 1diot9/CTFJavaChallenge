package org.apache.logging.log4j.util;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/LambdaUtil.class */
public final class LambdaUtil {
    private LambdaUtil() {
    }

    public static Object[] getAll(final Supplier<?>... suppliers) {
        if (suppliers == null) {
            return null;
        }
        Object[] result = new Object[suppliers.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = get(suppliers[i]);
        }
        return result;
    }

    public static Object get(final Supplier<?> supplier) {
        if (supplier == null) {
            return null;
        }
        Object result = supplier.get();
        return result instanceof Message ? ((Message) result).getFormattedMessage() : result;
    }

    public static Message get(final MessageSupplier supplier) {
        if (supplier == null) {
            return null;
        }
        return supplier.get();
    }

    public static Message getMessage(final Supplier<?> supplier, final MessageFactory messageFactory) {
        if (supplier == null) {
            return null;
        }
        Object result = supplier.get();
        return result instanceof Message ? (Message) result : messageFactory.newMessage(result);
    }
}
