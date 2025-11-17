package org.apache.logging.log4j.message;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.EnglishEnums;
import org.apache.logging.log4j.util.IndexedReadOnlyStringMap;
import org.apache.logging.log4j.util.IndexedStringMap;
import org.apache.logging.log4j.util.MultiFormatStringBuilderFormattable;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.TriConsumer;

@AsynchronouslyFormattable
@PerformanceSensitive({"allocation"})
/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/MapMessage.class */
public class MapMessage<M extends MapMessage<M, V>, V> implements MultiFormatStringBuilderFormattable {
    private static final long serialVersionUID = -5031471831131487120L;
    private final IndexedStringMap data;

    /* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/MapMessage$MapFormat.class */
    public enum MapFormat {
        XML,
        JSON,
        JAVA,
        JAVA_UNQUOTED;

        public static MapFormat lookupIgnoreCase(final String format) {
            if (XML.name().equalsIgnoreCase(format)) {
                return XML;
            }
            if (JSON.name().equalsIgnoreCase(format)) {
                return JSON;
            }
            if (JAVA.name().equalsIgnoreCase(format)) {
                return JAVA;
            }
            if (JAVA_UNQUOTED.name().equalsIgnoreCase(format)) {
                return JAVA_UNQUOTED;
            }
            return null;
        }

        public static String[] names() {
            return new String[]{XML.name(), JSON.name(), JAVA.name(), JAVA_UNQUOTED.name()};
        }
    }

    public MapMessage() {
        this.data = new SortedArrayStringMap();
    }

    public MapMessage(final int initialCapacity) {
        this.data = new SortedArrayStringMap(initialCapacity);
    }

    public MapMessage(final Map<String, V> map) {
        this.data = new SortedArrayStringMap((Map<String, ?>) map);
    }

    @Override // org.apache.logging.log4j.message.MultiformatMessage
    public String[] getFormats() {
        return MapFormat.names();
    }

    @Override // org.apache.logging.log4j.message.Message
    public Object[] getParameters() {
        Object[] result = new Object[this.data.size()];
        for (int i = 0; i < this.data.size(); i++) {
            result[i] = this.data.getValueAt(i);
        }
        return result;
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormat() {
        return "";
    }

    /* JADX WARN: Multi-variable type inference failed */
    public Map<String, V> getData() {
        TreeMap treeMap = new TreeMap();
        for (int i = 0; i < this.data.size(); i++) {
            treeMap.put(this.data.getKeyAt(i), this.data.getValueAt(i));
        }
        return Collections.unmodifiableMap(treeMap);
    }

    public IndexedReadOnlyStringMap getIndexedReadOnlyStringMap() {
        return this.data;
    }

    public void clear() {
        this.data.clear();
    }

    public boolean containsKey(final String key) {
        return this.data.containsKey(key);
    }

    public void put(final String candidateKey, final String value) {
        if (value == null) {
            throw new IllegalArgumentException("No value provided for key " + candidateKey);
        }
        String key = toKey(candidateKey);
        validate(key, value);
        this.data.putValue(key, value);
    }

    public void putAll(final Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            this.data.putValue(entry.getKey(), entry.getValue());
        }
    }

    public String get(final String key) {
        Object result = this.data.getValue(key);
        return ParameterFormatter.deepToString(result);
    }

    public String remove(final String key) {
        String result = get(key);
        this.data.remove(key);
        return result;
    }

    public String asString() {
        return format((MapFormat) null, new StringBuilder()).toString();
    }

    public String asString(final String format) {
        try {
            return format((MapFormat) EnglishEnums.valueOf(MapFormat.class, format), new StringBuilder()).toString();
        } catch (IllegalArgumentException e) {
            return asString();
        }
    }

    public <CV> void forEach(final BiConsumer<String, ? super CV> action) {
        this.data.forEach(action);
    }

    public <CV, S> void forEach(final TriConsumer<String, ? super CV, S> action, final S state) {
        this.data.forEach(action, state);
    }

    private StringBuilder format(final MapFormat format, final StringBuilder sb) {
        if (format == null) {
            appendMap(sb);
        } else {
            switch (format) {
                case XML:
                    asXml(sb);
                    break;
                case JSON:
                    asJson(sb);
                    break;
                case JAVA:
                    asJava(sb);
                    break;
                case JAVA_UNQUOTED:
                    asJavaUnquoted(sb);
                    break;
                default:
                    appendMap(sb);
                    break;
            }
        }
        return sb;
    }

    public void asXml(final StringBuilder sb) {
        sb.append("<Map>\n");
        for (int i = 0; i < this.data.size(); i++) {
            sb.append("  <Entry key=\"").append(this.data.getKeyAt(i)).append("\">");
            int size = sb.length();
            ParameterFormatter.recursiveDeepToString(this.data.getValueAt(i), sb);
            StringBuilders.escapeXml(sb, size);
            sb.append("</Entry>\n");
        }
        sb.append("</Map>");
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        return asString();
    }

    @Override // org.apache.logging.log4j.message.MultiformatMessage
    public String getFormattedMessage(final String[] formats) {
        return format(getFormat(formats), new StringBuilder()).toString();
    }

    private MapFormat getFormat(final String[] formats) {
        if (formats == null || formats.length == 0) {
            return null;
        }
        for (String str : formats) {
            MapFormat mapFormat = MapFormat.lookupIgnoreCase(str);
            if (mapFormat != null) {
                return mapFormat;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void appendMap(final StringBuilder sb) {
        for (int i = 0; i < this.data.size(); i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(this.data.getKeyAt(i)).append('=').append('\"');
            ParameterFormatter.recursiveDeepToString(this.data.getValueAt(i), sb);
            sb.append('\"');
        }
    }

    protected void asJson(final StringBuilder sb) {
        MapMessageJsonFormatter.format(sb, this.data);
    }

    protected void asJavaUnquoted(final StringBuilder sb) {
        asJava(sb, false);
    }

    protected void asJava(final StringBuilder sb) {
        asJava(sb, true);
    }

    private void asJava(final StringBuilder sb, final boolean quoted) {
        sb.append('{');
        for (int i = 0; i < this.data.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.data.getKeyAt(i)).append('=');
            if (quoted) {
                sb.append('\"');
            }
            ParameterFormatter.recursiveDeepToString(this.data.getValueAt(i), sb);
            if (quoted) {
                sb.append('\"');
            }
        }
        sb.append('}');
    }

    public M newInstance(Map<String, V> map) {
        return (M) new MapMessage(map);
    }

    public String toString() {
        return asString();
    }

    @Override // org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(final StringBuilder buffer) {
        format((MapFormat) null, buffer);
    }

    @Override // org.apache.logging.log4j.util.MultiFormatStringBuilderFormattable
    public void formatTo(final String[] formats, final StringBuilder buffer) {
        format(getFormat(formats), buffer);
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapMessage<?, ?> that = (MapMessage) o;
        return this.data.equals(that.data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    @Override // org.apache.logging.log4j.message.Message
    public Throwable getThrowable() {
        return null;
    }

    protected void validate(final String key, final boolean value) {
    }

    protected void validate(final String key, final byte value) {
    }

    protected void validate(final String key, final char value) {
    }

    protected void validate(final String key, final double value) {
    }

    protected void validate(final String key, final float value) {
    }

    protected void validate(final String key, final int value) {
    }

    protected void validate(final String key, final long value) {
    }

    protected void validate(final String key, final Object value) {
    }

    protected void validate(final String key, final short value) {
    }

    protected void validate(final String key, final String value) {
    }

    protected String toKey(final String candidateKey) {
        return candidateKey;
    }

    public M with(final String candidateKey, final boolean value) {
        String key = toKey(candidateKey);
        validate(key, value);
        this.data.putValue(key, Boolean.valueOf(value));
        return this;
    }

    public M with(final String candidateKey, final byte value) {
        String key = toKey(candidateKey);
        validate(key, value);
        this.data.putValue(key, Byte.valueOf(value));
        return this;
    }

    public M with(final String candidateKey, final char value) {
        String key = toKey(candidateKey);
        validate(key, value);
        this.data.putValue(key, Character.valueOf(value));
        return this;
    }

    public M with(final String candidateKey, final double value) {
        String key = toKey(candidateKey);
        validate(key, value);
        this.data.putValue(key, Double.valueOf(value));
        return this;
    }

    public M with(final String candidateKey, final float value) {
        String key = toKey(candidateKey);
        validate(key, value);
        this.data.putValue(key, Float.valueOf(value));
        return this;
    }

    public M with(final String candidateKey, final int value) {
        String key = toKey(candidateKey);
        validate(key, value);
        this.data.putValue(key, Integer.valueOf(value));
        return this;
    }

    public M with(final String candidateKey, final long value) {
        String key = toKey(candidateKey);
        validate(key, value);
        this.data.putValue(key, Long.valueOf(value));
        return this;
    }

    public M with(final String candidateKey, final Object value) {
        String key = toKey(candidateKey);
        validate(key, value);
        this.data.putValue(key, value);
        return this;
    }

    public M with(final String candidateKey, final short value) {
        String key = toKey(candidateKey);
        validate(key, value);
        this.data.putValue(key, Short.valueOf(value));
        return this;
    }

    public M with(final String candidateKey, final String value) {
        String key = toKey(candidateKey);
        put(key, value);
        return this;
    }
}
