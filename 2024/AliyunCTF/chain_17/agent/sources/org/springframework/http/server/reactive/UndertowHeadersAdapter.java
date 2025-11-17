package org.springframework.http.server.reactive;

import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/UndertowHeadersAdapter.class */
public class UndertowHeadersAdapter implements MultiValueMap<String, String> {
    private final HeaderMap headers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UndertowHeadersAdapter(HeaderMap headers) {
        this.headers = headers;
    }

    @Override // org.springframework.util.MultiValueMap
    public String getFirst(String key) {
        return this.headers.getFirst(key);
    }

    @Override // org.springframework.util.MultiValueMap
    public void add(String key, @Nullable String value) {
        this.headers.add(HttpString.tryFromString(key), value);
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(String key, List<? extends String> values) {
        this.headers.addAll(HttpString.tryFromString(key), values);
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(MultiValueMap<String, String> values) {
        values.forEach((key, list) -> {
            this.headers.addAll(HttpString.tryFromString(key), list);
        });
    }

    @Override // org.springframework.util.MultiValueMap
    public void set(String key, @Nullable String value) {
        this.headers.put(HttpString.tryFromString(key), value);
    }

    @Override // org.springframework.util.MultiValueMap
    public void setAll(Map<String, String> values) {
        values.forEach((key, list) -> {
            this.headers.put(HttpString.tryFromString(key), list);
        });
    }

    @Override // org.springframework.util.MultiValueMap
    public Map<String, String> toSingleValueMap() {
        Map<String, String> singleValueMap = CollectionUtils.newLinkedHashMap(this.headers.size());
        this.headers.forEach(values -> {
            singleValueMap.put(values.getHeaderName().toString(), values.getFirst());
        });
        return singleValueMap;
    }

    @Override // java.util.Map
    public int size() {
        return this.headers.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.headers.size() == 0;
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            String headerName = (String) key;
            if (this.headers.contains(headerName)) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        if (value instanceof String) {
            Stream stream = this.headers.getHeaderNames().stream();
            HeaderMap headerMap = this.headers;
            Objects.requireNonNull(headerMap);
            if (stream.map(headerMap::get).anyMatch(values -> {
                return values.contains(value);
            })) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> get(Object key) {
        if (!(key instanceof String)) {
            return null;
        }
        String headerName = (String) key;
        return this.headers.get(headerName);
    }

    @Override // java.util.Map
    @Nullable
    public List<String> put(String key, List<String> value) {
        HeaderValues previousValues = this.headers.get(key);
        this.headers.putAll(HttpString.tryFromString(key), value);
        return previousValues;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> remove(Object key) {
        if (key instanceof String) {
            String headerName = (String) key;
            Collection<String> removed = this.headers.remove(headerName);
            if (removed != null) {
                return new ArrayList(removed);
            }
            return null;
        }
        return null;
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        map.forEach((key, values) -> {
            this.headers.putAll(HttpString.tryFromString(key), values);
        });
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
        Stream stream = this.headers.getHeaderNames().stream();
        HeaderMap headerMap = this.headers;
        Objects.requireNonNull(headerMap);
        return (Collection) stream.map(headerMap::get).collect(Collectors.toList());
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Map.Entry<String, List<String>>>() { // from class: org.springframework.http.server.reactive.UndertowHeadersAdapter.1
            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<Map.Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return UndertowHeadersAdapter.this.headers.size();
            }
        };
    }

    public String toString() {
        return HttpHeaders.formatHeaders(this);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/UndertowHeadersAdapter$EntryIterator.class */
    private class EntryIterator implements Iterator<Map.Entry<String, List<String>>> {
        private final Iterator<HttpString> names;

        private EntryIterator() {
            this.names = UndertowHeadersAdapter.this.headers.getHeaderNames().iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.names.hasNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Map.Entry<String, List<String>> next() {
            return new HeaderEntry(this.names.next());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/UndertowHeadersAdapter$HeaderEntry.class */
    public class HeaderEntry implements Map.Entry<String, List<String>> {
        private final HttpString key;

        HeaderEntry(HttpString key) {
            this.key = key;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Map.Entry
        public String getKey() {
            return this.key.toString();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Map.Entry
        public List<String> getValue() {
            return UndertowHeadersAdapter.this.headers.get(this.key);
        }

        @Override // java.util.Map.Entry
        public List<String> setValue(List<String> value) {
            HeaderValues headerValues = UndertowHeadersAdapter.this.headers.get(this.key);
            UndertowHeadersAdapter.this.headers.putAll(this.key, value);
            return headerValues;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/UndertowHeadersAdapter$HeaderNames.class */
    private class HeaderNames extends AbstractSet<String> {
        private HeaderNames() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<String> iterator() {
            return new HeaderNamesIterator(UndertowHeadersAdapter.this.headers.getHeaderNames().iterator());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return UndertowHeadersAdapter.this.headers.getHeaderNames().size();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/UndertowHeadersAdapter$HeaderNamesIterator.class */
    private final class HeaderNamesIterator implements Iterator<String> {
        private final Iterator<HttpString> iterator;

        @Nullable
        private String currentName;

        private HeaderNamesIterator(Iterator<HttpString> iterator) {
            this.iterator = iterator;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public String next() {
            this.currentName = this.iterator.next().toString();
            return this.currentName;
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.currentName == null) {
                throw new IllegalStateException("No current Header in iterator");
            }
            if (!UndertowHeadersAdapter.this.headers.contains(this.currentName)) {
                throw new IllegalStateException("Header not present: " + this.currentName);
            }
            UndertowHeadersAdapter.this.headers.remove(this.currentName);
        }
    }
}
