package org.springframework.http.support;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/support/HttpComponentsHeadersAdapter.class */
public final class HttpComponentsHeadersAdapter implements MultiValueMap<String, String> {
    private final HttpMessage message;

    public HttpComponentsHeadersAdapter(HttpMessage message) {
        Assert.notNull(message, "Message must not be null");
        this.message = message;
    }

    @Override // org.springframework.util.MultiValueMap
    @Nullable
    public String getFirst(String key) {
        Header header = this.message.getFirstHeader(key);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

    @Override // org.springframework.util.MultiValueMap
    public void add(String key, @Nullable String value) {
        this.message.addHeader(key, value);
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
        this.message.setHeader(key, value);
    }

    @Override // org.springframework.util.MultiValueMap
    public void setAll(Map<String, String> values) {
        values.forEach(this::set);
    }

    @Override // org.springframework.util.MultiValueMap
    public Map<String, String> toSingleValueMap() {
        Map<String, String> map = CollectionUtils.newLinkedHashMap(size());
        this.message.headerIterator().forEachRemaining(h -> {
            map.putIfAbsent(h.getName(), h.getValue());
        });
        return map;
    }

    @Override // java.util.Map
    public int size() {
        return this.message.getHeaders().length;
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.message.getHeaders().length == 0;
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            String headerName = (String) key;
            if (this.message.containsHeader(headerName)) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return (value instanceof String) && Arrays.stream(this.message.getHeaders()).anyMatch(h -> {
            return h.getValue().equals(value);
        });
    }

    @Override // java.util.Map
    @Nullable
    public List<String> get(Object key) {
        List<String> values = null;
        if (containsKey(key)) {
            Header[] headers = this.message.getHeaders((String) key);
            values = new ArrayList<>(headers.length);
            for (Header header : headers) {
                values.add(header.getValue());
            }
        }
        return values;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> put(String key, List<String> values) {
        List<String> oldValues = remove((Object) key);
        values.forEach(value -> {
            add(key, value);
        });
        return oldValues;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> remove(Object key) {
        if (key instanceof String) {
            String headerName = (String) key;
            List<String> oldValues = get(key);
            this.message.removeHeaders(headerName);
            return oldValues;
        }
        return null;
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        map.forEach(this::put);
    }

    @Override // java.util.Map
    public void clear() {
        this.message.setHeaders(new Header[0]);
    }

    @Override // java.util.Map
    public Set<String> keySet() {
        Set<String> keys = new LinkedHashSet<>(size());
        for (Header header : this.message.getHeaders()) {
            keys.add(header.getName());
        }
        return keys;
    }

    @Override // java.util.Map
    public Collection<List<String>> values() {
        Collection<List<String>> values = new ArrayList<>(size());
        for (Header header : this.message.getHeaders()) {
            values.add(get((Object) header.getName()));
        }
        return values;
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Map.Entry<String, List<String>>>() { // from class: org.springframework.http.support.HttpComponentsHeadersAdapter.1
            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<Map.Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return HttpComponentsHeadersAdapter.this.size();
            }
        };
    }

    public String toString() {
        return HttpHeaders.formatHeaders(this);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/support/HttpComponentsHeadersAdapter$EntryIterator.class */
    private class EntryIterator implements Iterator<Map.Entry<String, List<String>>> {
        private final Iterator<Header> iterator;

        private EntryIterator() {
            this.iterator = HttpComponentsHeadersAdapter.this.message.headerIterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Map.Entry<String, List<String>> next() {
            return new HeaderEntry(this.iterator.next().getName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/support/HttpComponentsHeadersAdapter$HeaderEntry.class */
    public class HeaderEntry implements Map.Entry<String, List<String>> {
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
        public List<String> getValue() {
            List<String> values = HttpComponentsHeadersAdapter.this.get((Object) this.key);
            return values != null ? values : Collections.emptyList();
        }

        @Override // java.util.Map.Entry
        public List<String> setValue(List<String> value) {
            List<String> previousValues = getValue();
            HttpComponentsHeadersAdapter.this.put(this.key, value);
            return previousValues;
        }
    }
}
