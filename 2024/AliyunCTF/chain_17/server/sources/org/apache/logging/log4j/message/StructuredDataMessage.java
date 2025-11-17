package org.apache.logging.log4j.message;

import java.util.Map;
import org.apache.logging.log4j.util.EnglishEnums;
import org.apache.logging.log4j.util.StringBuilders;

@AsynchronouslyFormattable
/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/StructuredDataMessage.class */
public class StructuredDataMessage extends MapMessage<StructuredDataMessage, String> {
    private static final long serialVersionUID = 1703221292892071920L;
    private static final int MAX_LENGTH = 32;
    private static final int HASHVAL = 31;
    private StructuredDataId id;
    private String message;
    private String type;
    private final int maxLength;

    /* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/StructuredDataMessage$Format.class */
    public enum Format {
        XML,
        FULL
    }

    public StructuredDataMessage(final String id, final String msg, final String type) {
        this(id, msg, type, 32);
    }

    public StructuredDataMessage(final String id, final String msg, final String type, final int maxLength) {
        this.id = new StructuredDataId(id, (String[]) null, (String[]) null, maxLength);
        this.message = msg;
        this.type = type;
        this.maxLength = maxLength;
    }

    public StructuredDataMessage(final String id, final String msg, final String type, final Map<String, String> data) {
        this(id, msg, type, data, 32);
    }

    public StructuredDataMessage(final String id, final String msg, final String type, final Map<String, String> data, final int maxLength) {
        super(data);
        this.id = new StructuredDataId(id, (String[]) null, (String[]) null, maxLength);
        this.message = msg;
        this.type = type;
        this.maxLength = maxLength;
    }

    public StructuredDataMessage(final StructuredDataId id, final String msg, final String type) {
        this(id, msg, type, 32);
    }

    public StructuredDataMessage(final StructuredDataId id, final String msg, final String type, final int maxLength) {
        this.id = id;
        this.message = msg;
        this.type = type;
        this.maxLength = maxLength;
    }

    public StructuredDataMessage(final StructuredDataId id, final String msg, final String type, final Map<String, String> data) {
        this(id, msg, type, data, 32);
    }

    public StructuredDataMessage(final StructuredDataId id, final String msg, final String type, final Map<String, String> data, final int maxLength) {
        super(data);
        this.id = id;
        this.message = msg;
        this.type = type;
        this.maxLength = maxLength;
    }

    private StructuredDataMessage(final StructuredDataMessage msg, final Map<String, String> map) {
        super(map);
        this.id = msg.id;
        this.message = msg.message;
        this.type = msg.type;
        this.maxLength = 32;
    }

    protected StructuredDataMessage() {
        this.maxLength = 32;
    }

    @Override // org.apache.logging.log4j.message.MapMessage, org.apache.logging.log4j.message.MultiformatMessage
    public String[] getFormats() {
        String[] formats = new String[Format.values().length];
        int i = 0;
        for (Format format : Format.values()) {
            int i2 = i;
            i++;
            formats[i2] = format.name();
        }
        return formats;
    }

    public StructuredDataId getId() {
        return this.id;
    }

    protected void setId(final String id) {
        this.id = new StructuredDataId(id, null, null);
    }

    protected void setId(final StructuredDataId id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    protected void setType(final String type) {
        if (type.length() > 32) {
            throw new IllegalArgumentException("structured data type exceeds maximum length of 32 characters: " + type);
        }
        this.type = type;
    }

    @Override // org.apache.logging.log4j.message.MapMessage, org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(final StringBuilder buffer) {
        asString(Format.FULL, null, buffer);
    }

    @Override // org.apache.logging.log4j.message.MapMessage, org.apache.logging.log4j.util.MultiFormatStringBuilderFormattable
    public void formatTo(final String[] formats, final StringBuilder buffer) {
        asString(getFormat(formats), null, buffer);
    }

    @Override // org.apache.logging.log4j.message.MapMessage, org.apache.logging.log4j.message.Message
    public String getFormat() {
        return this.message;
    }

    protected void setMessageFormat(final String msg) {
        this.message = msg;
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    public String asString() {
        return asString(Format.FULL, null);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    public String asString(final String format) {
        try {
            return asString((Format) EnglishEnums.valueOf(Format.class, format), null);
        } catch (IllegalArgumentException e) {
            return asString();
        }
    }

    public final String asString(final Format format, final StructuredDataId structuredDataId) {
        StringBuilder sb = new StringBuilder();
        asString(format, structuredDataId, sb);
        return sb.toString();
    }

    public final void asString(final Format format, final StructuredDataId structuredDataId, final StringBuilder sb) {
        StructuredDataId sdId;
        String msg;
        boolean full = Format.FULL.equals(format);
        if (full) {
            String myType = getType();
            if (myType == null) {
                return;
            } else {
                sb.append(getType()).append(' ');
            }
        }
        StructuredDataId sdId2 = getId();
        if (sdId2 != null) {
            sdId = sdId2.makeId(structuredDataId);
        } else {
            sdId = structuredDataId;
        }
        if (sdId == null || sdId.getName() == null) {
            return;
        }
        if (Format.XML.equals(format)) {
            asXml(sdId, sb);
            return;
        }
        sb.append('[');
        StringBuilders.appendValue(sb, sdId);
        sb.append(' ');
        appendMap(sb);
        sb.append(']');
        if (full && (msg = getFormat()) != null) {
            sb.append(' ').append(msg);
        }
    }

    private void asXml(final StructuredDataId structuredDataId, final StringBuilder sb) {
        sb.append("<StructuredData>\n");
        sb.append("<type>").append(this.type).append("</type>\n");
        sb.append("<id>").append(structuredDataId).append("</id>\n");
        super.asXml(sb);
        sb.append("\n</StructuredData>\n");
    }

    @Override // org.apache.logging.log4j.message.MapMessage, org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        return asString(Format.FULL, null);
    }

    @Override // org.apache.logging.log4j.message.MapMessage, org.apache.logging.log4j.message.MultiformatMessage
    public String getFormattedMessage(final String[] formats) {
        return asString(getFormat(formats), null);
    }

    private Format getFormat(final String[] formats) {
        if (formats != null && formats.length > 0) {
            for (String format : formats) {
                if (Format.XML.name().equalsIgnoreCase(format)) {
                    return Format.XML;
                }
                if (Format.FULL.name().equalsIgnoreCase(format)) {
                    return Format.FULL;
                }
            }
            return null;
        }
        return Format.FULL;
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    public String toString() {
        return asString(null, null);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.apache.logging.log4j.message.MapMessage
    public StructuredDataMessage newInstance(final Map<String, String> map) {
        return new StructuredDataMessage(this, map);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StructuredDataMessage that = (StructuredDataMessage) o;
        if (!super.equals(o)) {
            return false;
        }
        if (this.type != null) {
            if (!this.type.equals(that.type)) {
                return false;
            }
        } else if (that.type != null) {
            return false;
        }
        if (this.id != null) {
            if (!this.id.equals(that.id)) {
                return false;
            }
        } else if (that.id != null) {
            return false;
        }
        if (this.message != null) {
            if (!this.message.equals(that.message)) {
                return false;
            }
            return true;
        }
        if (that.message != null) {
            return false;
        }
        return true;
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    public int hashCode() {
        int result = super.hashCode();
        return (31 * ((31 * ((31 * result) + (this.type != null ? this.type.hashCode() : 0))) + (this.id != null ? this.id.hashCode() : 0))) + (this.message != null ? this.message.hashCode() : 0);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(final String key, final boolean value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(final String key, final byte value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(final String key, final char value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(final String key, final double value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(final String key, final float value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(final String key, final int value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(final String key, final long value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(final String key, final Object value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(final String key, final short value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(final String key, final String value) {
        validateKey(key);
    }

    protected void validateKey(final String key) {
        if (this.maxLength > 0 && key.length() > this.maxLength) {
            throw new IllegalArgumentException("Structured data keys are limited to " + this.maxLength + " characters. key: " + key);
        }
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c < '!' || c > '~' || c == '=' || c == ']' || c == '\"') {
                throw new IllegalArgumentException("Structured data keys must contain printable US ASCII charactersand may not contain a space, =, ], or \"");
            }
        }
    }
}
