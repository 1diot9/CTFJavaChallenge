package org.springframework.boot.info;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/info/InfoProperties.class */
public class InfoProperties implements Iterable<Entry> {
    private final Properties entries;

    public InfoProperties(Properties entries) {
        Assert.notNull(entries, "Entries must not be null");
        this.entries = copy(entries);
    }

    public String get(String key) {
        return this.entries.getProperty(key);
    }

    public Instant getInstant(String key) {
        String s = get(key);
        if (s != null) {
            try {
                return Instant.ofEpochMilli(Long.parseLong(s));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override // java.lang.Iterable
    public Iterator<Entry> iterator() {
        return new PropertiesIterator(this.entries);
    }

    public PropertySource<?> toPropertySource() {
        return new PropertiesPropertySource(getClass().getSimpleName(), copy(this.entries));
    }

    private Properties copy(Properties properties) {
        Properties copy = new Properties();
        copy.putAll(properties);
        return copy;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/info/InfoProperties$PropertiesIterator.class */
    private static final class PropertiesIterator implements Iterator<Entry> {
        private final Iterator<Map.Entry<Object, Object>> iterator;

        private PropertiesIterator(Properties properties) {
            this.iterator = properties.entrySet().iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Entry next() {
            Map.Entry<Object, Object> entry = this.iterator.next();
            return new Entry((String) entry.getKey(), (String) entry.getValue());
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("InfoProperties are immutable.");
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/info/InfoProperties$Entry.class */
    public static final class Entry {
        private final String key;
        private final String value;

        private Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }
    }
}
