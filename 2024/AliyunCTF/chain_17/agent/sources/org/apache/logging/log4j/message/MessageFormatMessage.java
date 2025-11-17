package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Locale;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/MessageFormatMessage.class */
public class MessageFormatMessage implements Message {
    private static final Logger LOGGER = StatusLogger.getLogger();
    private static final long serialVersionUID = 1;
    private static final int HASHVAL = 31;
    private String messagePattern;
    private transient Object[] parameters;
    private String[] serializedParameters;
    private transient String formattedMessage;
    private transient Throwable throwable;
    private final Locale locale;

    public MessageFormatMessage(final Locale locale, final String messagePattern, final Object... parameters) {
        this.locale = locale;
        this.messagePattern = messagePattern;
        this.parameters = parameters;
        int length = parameters == null ? 0 : parameters.length;
        if (length > 0 && (parameters[length - 1] instanceof Throwable)) {
            this.throwable = (Throwable) parameters[length - 1];
        }
    }

    public MessageFormatMessage(final String messagePattern, final Object... parameters) {
        this(Locale.getDefault(Locale.Category.FORMAT), messagePattern, parameters);
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        if (this.formattedMessage == null) {
            this.formattedMessage = formatMessage(this.messagePattern, this.parameters);
        }
        return this.formattedMessage;
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormat() {
        return this.messagePattern;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Object[] getParameters() {
        if (this.parameters != null) {
            return this.parameters;
        }
        return this.serializedParameters;
    }

    protected String formatMessage(final String msgPattern, final Object... args) {
        try {
            MessageFormat temp = new MessageFormat(msgPattern, this.locale);
            return temp.format(args);
        } catch (IllegalFormatException ife) {
            LOGGER.error("Unable to format msg: " + msgPattern, (Throwable) ife);
            return msgPattern;
        }
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MessageFormatMessage that = (MessageFormatMessage) o;
        if (this.messagePattern != null) {
            if (!this.messagePattern.equals(that.messagePattern)) {
                return false;
            }
        } else if (that.messagePattern != null) {
            return false;
        }
        return Arrays.equals(this.serializedParameters, that.serializedParameters);
    }

    public int hashCode() {
        int result = this.messagePattern != null ? this.messagePattern.hashCode() : 0;
        return (31 * result) + (this.serializedParameters != null ? Arrays.hashCode(this.serializedParameters) : 0);
    }

    public String toString() {
        return getFormattedMessage();
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        getFormattedMessage();
        out.writeUTF(this.formattedMessage);
        out.writeUTF(this.messagePattern);
        int length = this.parameters == null ? 0 : this.parameters.length;
        out.writeInt(length);
        this.serializedParameters = new String[length];
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                this.serializedParameters[i] = String.valueOf(this.parameters[i]);
                out.writeUTF(this.serializedParameters[i]);
            }
        }
    }

    private void readObject(final ObjectInputStream in) throws IOException {
        this.parameters = null;
        this.throwable = null;
        this.formattedMessage = in.readUTF();
        this.messagePattern = in.readUTF();
        int length = in.readInt();
        this.serializedParameters = new String[length];
        for (int i = 0; i < length; i++) {
            this.serializedParameters[i] = in.readUTF();
        }
    }

    @Override // org.apache.logging.log4j.message.Message
    public Throwable getThrowable() {
        return this.throwable;
    }
}
