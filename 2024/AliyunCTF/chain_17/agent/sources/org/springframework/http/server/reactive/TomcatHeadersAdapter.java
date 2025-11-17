package org.springframework.http.server.reactive;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/TomcatHeadersAdapter.class */
public class TomcatHeadersAdapter implements MultiValueMap<String, String> {
    private final MimeHeaders headers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TomcatHeadersAdapter(MimeHeaders headers) {
        this.headers = headers;
    }

    @Override // org.springframework.util.MultiValueMap
    public String getFirst(String key) {
        return this.headers.getHeader(key);
    }

    @Override // org.springframework.util.MultiValueMap
    public void add(String key, @Nullable String value) {
        this.headers.addValue(key).setString(value);
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(String key, List<? extends String> values) {
        values.forEach(value -> {
            add(key, value);
        });
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(MultiValueMap<String, String> values) {
        values.forEach(this::addAll);
    }

    @Override // org.springframework.util.MultiValueMap
    public void set(String key, @Nullable String value) {
        this.headers.setValue(key).setString(value);
    }

    @Override // org.springframework.util.MultiValueMap
    public void setAll(Map<String, String> values) {
        values.forEach(this::set);
    }

    @Override // org.springframework.util.MultiValueMap
    public Map<String, String> toSingleValueMap() {
        Map<String, String> singleValueMap = CollectionUtils.newLinkedHashMap(this.headers.size());
        keySet().forEach(key -> {
            singleValueMap.put(key, getFirst(key));
        });
        return singleValueMap;
    }

    @Override // java.util.Map
    public int size() {
        Enumeration<String> names = this.headers.names();
        int size = 0;
        while (names.hasMoreElements()) {
            size++;
            names.nextElement();
        }
        return size;
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.headers.size() == 0;
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        if (!(key instanceof String)) {
            return false;
        }
        String headerName = (String) key;
        return this.headers.findHeader(headerName, 0) != -1;
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        if (value instanceof String) {
            String text = (String) value;
            MessageBytes messageBytes = MessageBytes.newInstance();
            messageBytes.setString(text);
            for (int i = 0; i < this.headers.size(); i++) {
                if (this.headers.getValue(i).equals(messageBytes)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> get(Object key) {
        if (containsKey(key)) {
            return Collections.list(this.headers.values((String) key));
        }
        return null;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> put(String key, List<String> value) {
        List<String> previousValues = get((Object) key);
        this.headers.removeHeader(key);
        value.forEach(v -> {
            this.headers.addValue(key).setString(v);
        });
        return previousValues;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> remove(Object key) {
        if (key instanceof String) {
            String headerName = (String) key;
            List<String> previousValues = get(key);
            this.headers.removeHeader(headerName);
            return previousValues;
        }
        return null;
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        map.forEach(this::put);
    }

    @Override // java.util.Map
    public void clear() {
        this.headers.clear();
    }

    @Override // java.util.Map
    public Set<String> keySet() {
        return new HeaderNames();
    }

    @Override // java.util.Map
    public Collection<List<String>> values() {
        return keySet().stream().map((v1) -> {
            return get(v1);
        }).toList();
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Map.Entry<String, List<String>>>() { // from class: org.springframework.http.server.reactive.TomcatHeadersAdapter.1
            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<Map.Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return TomcatHeadersAdapter.this.headers.size();
            }
        };
    }

    public String toString() {
        return HttpHeaders.formatHeaders(this);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/TomcatHeadersAdapter$EntryIterator.class */
    private class EntryIterator implements Iterator<Map.Entry<String, List<String>>> {
        private final Enumeration<String> names;

        private EntryIterator() {
            this.names = TomcatHeadersAdapter.this.headers.names();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.names.hasMoreElements();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Map.Entry<String, List<String>> next() {
            return new HeaderEntry(this.names.nextElement());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/TomcatHeadersAdapter$HeaderEntry.class */
    public final class HeaderEntry implements Map.Entry<String, List<String>> {
        private final String key;

        HeaderEntry(String key) {
            this.key = key;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Map.Entry
        public String getKey() {
            return this.key;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Map.Entry
        @Nullable
        public List<String> getValue() {
            return TomcatHeadersAdapter.this.get((Object) this.key);
        }

        @Override // java.util.Map.Entry
        @Nullable
        public List<String> setValue(List<String> value) {
            List<String> previous = getValue();
            TomcatHeadersAdapter.this.headers.removeHeader(this.key);
            TomcatHeadersAdapter.this.addAll(this.key, (List<? extends String>) value);
            return previous;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/TomcatHeadersAdapter$HeaderNames.class */
    public class HeaderNames extends AbstractSet<String> {
        private HeaderNames() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<String> iterator() {
            return new HeaderNamesIterator(TomcatHeadersAdapter.this.headers.names());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            Enumeration<String> names = TomcatHeadersAdapter.this.headers.names();
            int size = 0;
            while (names.hasMoreElements()) {
                names.nextElement();
                size++;
            }
            return size;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/TomcatHeadersAdapter$HeaderNamesIterator.class */
    private final class HeaderNamesIterator implements Iterator<String> {
        private final Enumeration<String> enumeration;

        @Nullable
        private String currentName;

        private HeaderNamesIterator(Enumeration<String> enumeration) {
            this.enumeration = enumeration;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.enumeration.hasMoreElements();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public String next() {
            this.currentName = this.enumeration.nextElement();
            return this.currentName;
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.currentName == null) {
                throw new IllegalStateException("No current Header in iterator");
            }
            int index = TomcatHeadersAdapter.this.headers.findHeader(this.currentName, 0);
            if (index == -1) {
                throw new IllegalStateException("Header not present: " + this.currentName);
            }
            TomcatHeadersAdapter.this.headers.removeHeader(index);
        }
    }
}
