package org.apache.logging.log4j.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.util.StringBuilderFormattable;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/StructuredDataCollectionMessage.class */
public class StructuredDataCollectionMessage implements StringBuilderFormattable, MessageCollectionMessage<StructuredDataMessage> {
    private static final long serialVersionUID = 5725337076388822924L;
    private final List<StructuredDataMessage> structuredDataMessageList;

    public StructuredDataCollectionMessage(final List<StructuredDataMessage> messages) {
        this.structuredDataMessageList = messages;
    }

    @Override // java.lang.Iterable
    public Iterator<StructuredDataMessage> iterator() {
        return this.structuredDataMessageList.iterator();
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder();
        formatTo(sb);
        return sb.toString();
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormat() {
        StringBuilder sb = new StringBuilder();
        for (StructuredDataMessage msg : this.structuredDataMessageList) {
            if (msg.getFormat() != null) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(msg.getFormat());
            }
        }
        return sb.toString();
    }

    @Override // org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(final StringBuilder buffer) {
        for (StructuredDataMessage msg : this.structuredDataMessageList) {
            msg.formatTo(buffer);
        }
    }

    @Override // org.apache.logging.log4j.message.Message
    public Object[] getParameters() {
        List<Object[]> objectList = new ArrayList<>();
        int count = 0;
        for (StructuredDataMessage msg : this.structuredDataMessageList) {
            Object[] objects = msg.getParameters();
            if (objects != null) {
                objectList.add(objects);
                count += objects.length;
            }
        }
        Object[] objects2 = new Object[count];
        int index = 0;
        for (Object[] objs : objectList) {
            for (Object obj : objs) {
                int i = index;
                index++;
                objects2[i] = obj;
            }
        }
        return objects2;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Throwable getThrowable() {
        for (StructuredDataMessage msg : this.structuredDataMessageList) {
            Throwable t = msg.getThrowable();
            if (t != null) {
                return t;
            }
        }
        return null;
    }
}
