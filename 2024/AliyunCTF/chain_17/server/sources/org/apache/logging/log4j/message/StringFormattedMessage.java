package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Locale;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/StringFormattedMessage.class */
public class StringFormattedMessage implements Message {
    private static final Logger LOGGER = StatusLogger.getLogger();
    private static final long serialVersionUID = -665975803997290697L;
    private static final int HASHVAL = 31;
    private String messagePattern;
    private transient Object[] argArray;
    private String[] stringArgs;
    private transient String formattedMessage;
    private transient Throwable throwable;
    private final Locale locale;

    public StringFormattedMessage(final Locale locale, final String messagePattern, final Object... arguments) {
        this.locale = locale;
        this.messagePattern = messagePattern;
        this.argArray = arguments;
        if (arguments != null && arguments.length > 0 && (arguments[arguments.length - 1] instanceof Throwable)) {
            this.throwable = (Throwable) arguments[arguments.length - 1];
        }
    }

    public StringFormattedMessage(final String messagePattern, final Object... arguments) {
        this(Locale.getDefault(Locale.Category.FORMAT), messagePattern, arguments);
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        if (this.formattedMessage == null) {
            this.formattedMessage = formatMessage(this.messagePattern, this.argArray);
        }
        return this.formattedMessage;
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormat() {
        return this.messagePattern;
    }

    @Override // org.apache.logging.log4j.message.Message
    public Object[] getParameters() {
        if (this.argArray != null) {
            return this.argArray;
        }
        return this.stringArgs;
    }

    protected String formatMessage(final String msgPattern, final Object... args) {
        if (args != null && args.length == 0) {
            return msgPattern;
        }
        try {
            return String.format(this.locale, msgPattern, args);
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
        StringFormattedMessage that = (StringFormattedMessage) o;
        if (this.messagePattern != null) {
            if (!this.messagePattern.equals(that.messagePattern)) {
                return false;
            }
        } else if (that.messagePattern != null) {
            return false;
        }
        return Arrays.equals(this.stringArgs, that.stringArgs);
    }

    public int hashCode() {
        int result = this.messagePattern != null ? this.messagePattern.hashCode() : 0;
        return (31 * result) + (this.stringArgs != null ? Arrays.hashCode(this.stringArgs) : 0);
    }

    public String toString() {
        return getFormattedMessage();
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        getFormattedMessage();
        out.writeUTF(this.formattedMessage);
        out.writeUTF(this.messagePattern);
        out.writeInt(this.argArray.length);
        this.stringArgs = new String[this.argArray.length];
        int i = 0;
        for (Object obj : this.argArray) {
            String string = String.valueOf(obj);
            this.stringArgs[i] = string;
            out.writeUTF(string);
            i++;
        }
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.formattedMessage = in.readUTF();
        this.messagePattern = in.readUTF();
        int length = in.readInt();
        this.stringArgs = new String[length];
        for (int i = 0; i < length; i++) {
            this.stringArgs[i] = in.readUTF();
        }
    }

    @Override // org.apache.logging.log4j.message.Message
    public Throwable getThrowable() {
        return this.throwable;
    }
}
