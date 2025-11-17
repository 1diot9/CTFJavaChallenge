package org.apache.logging.log4j.message;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import java.io.Serializable;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.Strings;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/DefaultFlowMessageFactory.class */
public class DefaultFlowMessageFactory implements FlowMessageFactory, Serializable {
    private static final String EXIT_DEFAULT_PREFIX = "Exit";
    private static final String ENTRY_DEFAULT_PREFIX = "Enter";
    private static final long serialVersionUID = 8578655591131397576L;
    private final String entryText;
    private final String exitText;
    private final MessageFactory messageFactory;

    public DefaultFlowMessageFactory() {
        this(ENTRY_DEFAULT_PREFIX, EXIT_DEFAULT_PREFIX);
    }

    public DefaultFlowMessageFactory(final String entryText, final String exitText) {
        this.entryText = entryText;
        this.exitText = exitText;
        this.messageFactory = createDefaultMessageFactory();
    }

    private static MessageFactory createDefaultMessageFactory() {
        try {
            return AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/DefaultFlowMessageFactory$AbstractFlowMessage.class */
    private static class AbstractFlowMessage implements FlowMessage, StringBuilderFormattable {
        private static final long serialVersionUID = 1;
        private final Message message;
        private final String text;

        AbstractFlowMessage(final String text, final Message message) {
            this.message = message;
            this.text = text;
        }

        @Override // org.apache.logging.log4j.message.Message
        public String getFormattedMessage() {
            if (this.message != null) {
                return this.text + CharSequenceUtil.SPACE + this.message.getFormattedMessage();
            }
            return this.text;
        }

        @Override // org.apache.logging.log4j.message.Message
        public String getFormat() {
            if (this.message != null) {
                return this.text + CharSequenceUtil.SPACE + this.message.getFormat();
            }
            return this.text;
        }

        @Override // org.apache.logging.log4j.message.Message
        public Object[] getParameters() {
            if (this.message != null) {
                return this.message.getParameters();
            }
            return null;
        }

        @Override // org.apache.logging.log4j.message.Message
        public Throwable getThrowable() {
            if (this.message != null) {
                return this.message.getThrowable();
            }
            return null;
        }

        @Override // org.apache.logging.log4j.message.FlowMessage
        public Message getMessage() {
            return this.message;
        }

        @Override // org.apache.logging.log4j.message.FlowMessage
        public String getText() {
            return this.text;
        }

        @Override // org.apache.logging.log4j.util.StringBuilderFormattable
        public void formatTo(final StringBuilder buffer) {
            buffer.append(this.text);
            if (this.message != null) {
                buffer.append(CharSequenceUtil.SPACE);
                StringBuilders.appendValue(buffer, this.message);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/DefaultFlowMessageFactory$SimpleEntryMessage.class */
    public static final class SimpleEntryMessage extends AbstractFlowMessage implements EntryMessage {
        private static final long serialVersionUID = 1;

        SimpleEntryMessage(final String entryText, final Message message) {
            super(entryText, message);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/DefaultFlowMessageFactory$SimpleExitMessage.class */
    public static final class SimpleExitMessage extends AbstractFlowMessage implements ExitMessage {
        private static final long serialVersionUID = 1;
        private final Object result;
        private final boolean isVoid;

        SimpleExitMessage(final String exitText, final EntryMessage message) {
            this(exitText, message.getMessage());
        }

        SimpleExitMessage(final String exitText, final Message message) {
            super(exitText, message);
            this.result = null;
            this.isVoid = true;
        }

        SimpleExitMessage(final String exitText, final Object result, final EntryMessage message) {
            this(exitText, result, message.getMessage());
        }

        SimpleExitMessage(final String exitText, final Object result, final Message message) {
            super(exitText, message);
            this.result = result;
            this.isVoid = false;
        }

        @Override // org.apache.logging.log4j.message.DefaultFlowMessageFactory.AbstractFlowMessage, org.apache.logging.log4j.message.Message
        public String getFormattedMessage() {
            String formattedMessage = super.getFormattedMessage();
            if (this.isVoid) {
                return formattedMessage;
            }
            return formattedMessage + ": " + this.result;
        }
    }

    public String getEntryText() {
        return this.entryText;
    }

    public String getExitText() {
        return this.exitText;
    }

    @Override // org.apache.logging.log4j.message.FlowMessageFactory
    public EntryMessage newEntryMessage(final String format, final Object... params) {
        Message message;
        boolean hasFormat = Strings.isNotEmpty(format);
        if (params == null || params.length == 0) {
            message = hasFormat ? this.messageFactory.newMessage(format) : null;
        } else if (hasFormat) {
            message = this.messageFactory.newMessage(format, params);
        } else {
            StringBuilder sb = new StringBuilder("params(");
            for (int i = 0; i < params.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(StrPool.EMPTY_JSON);
            }
            sb.append(")");
            message = this.messageFactory.newMessage(sb.toString(), params);
        }
        return newEntryMessage(message);
    }

    @Override // org.apache.logging.log4j.message.FlowMessageFactory
    public EntryMessage newEntryMessage(final Message message) {
        return new SimpleEntryMessage(this.entryText, makeImmutable(message));
    }

    private Message makeImmutable(final Message message) {
        if (message instanceof ReusableMessage) {
            return ((ReusableMessage) message).memento();
        }
        return message;
    }

    @Override // org.apache.logging.log4j.message.FlowMessageFactory
    public ExitMessage newExitMessage(final String format, final Object result) {
        Message message;
        boolean hasFormat = Strings.isNotEmpty(format);
        if (result == null) {
            message = hasFormat ? this.messageFactory.newMessage(format) : null;
        } else {
            message = this.messageFactory.newMessage(hasFormat ? format : "with({})", result);
        }
        return newExitMessage(message);
    }

    @Override // org.apache.logging.log4j.message.FlowMessageFactory
    public ExitMessage newExitMessage(Message message) {
        return new SimpleExitMessage(this.exitText, message);
    }

    @Override // org.apache.logging.log4j.message.FlowMessageFactory
    public ExitMessage newExitMessage(final EntryMessage message) {
        return new SimpleExitMessage(this.exitText, message);
    }

    @Override // org.apache.logging.log4j.message.FlowMessageFactory
    public ExitMessage newExitMessage(final Object result, final EntryMessage message) {
        return new SimpleExitMessage(this.exitText, result, message);
    }

    @Override // org.apache.logging.log4j.message.FlowMessageFactory
    public ExitMessage newExitMessage(final Object result, final Message message) {
        return new SimpleExitMessage(this.exitText, result, message);
    }
}
