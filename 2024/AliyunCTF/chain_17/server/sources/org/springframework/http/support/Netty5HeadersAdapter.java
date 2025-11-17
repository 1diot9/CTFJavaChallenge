package org.springframework.http.support;

import io.netty5.handler.codec.http.headers.HttpHeaders;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/support/Netty5HeadersAdapter.class */
public final class Netty5HeadersAdapter implements MultiValueMap<String, String> {
    private final HttpHeaders headers;

    public Netty5HeadersAdapter(HttpHeaders headers) {
        Assert.notNull(headers, "Headers must not be null");
        this.headers = headers;
    }

    @Override // org.springframework.util.MultiValueMap
    @Nullable
    public String getFirst(String key) {
        CharSequence value = this.headers.get(key);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    @Override // org.springframework.util.MultiValueMap
    public void add(String key, @Nullable String value) {
        if (value != null) {
            this.headers.add(key, value);
        }
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(String key, List<? extends String> values) {
        this.headers.add(key, values);
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(MultiValueMap<String, String> values) {
        HttpHeaders httpHeaders = this.headers;
        Objects.requireNonNull(httpHeaders);
        values.forEach((v1, v2) -> {
            r1.add(v1, v2);
        });
    }

    @Override // org.springframework.util.MultiValueMap
    public void set(String key, @Nullable String value) {
        if (value != null) {
            this.headers.set(key, value);
        }
    }

    @Override // org.springframework.util.MultiValueMap
    public void setAll(Map<String, String> values) {
        HttpHeaders httpHeaders = this.headers;
        Objects.requireNonNull(httpHeaders);
        values.forEach((v1, v2) -> {
            r1.set(v1, v2);
        });
    }

    @Override // org.springframework.util.MultiValueMap
    public Map<String, String> toSingleValueMap() {
        Map<String, String> singleValueMap = CollectionUtils.newLinkedHashMap(this.headers.size());
        this.headers.forEach(entry -> {
            singleValueMap.putIfAbsent(((CharSequence) entry.getKey()).toString(), ((CharSequence) entry.getValue()).toString());
        });
        return singleValueMap;
    }

    @Override // java.util.Map
    public int size() {
        return this.headers.names().size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.headers.isEmpty();
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
        return (value instanceof String) && StreamSupport.stream(this.headers.spliterator(), false).anyMatch(entry -> {
            return value.equals(entry.getValue());
        });
    }

    @Override // java.util.Map
    @Nullable
    public List<String> get(Object key) {
        Iterator<CharSequence> iterator = this.headers.valuesIterator((CharSequence) key);
        if (iterator.hasNext()) {
            List<String> result = new ArrayList<>();
            iterator.forEachRemaining(value -> {
                result.add(value.toString());
            });
            return result;
        }
        return null;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> put(String key, @Nullable List<String> value) {
        List<String> previousValues = get((Object) key);
        this.headers.set(key, value);
        return previousValues;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> remove(Object key) {
        if (key instanceof String) {
            String headerName = (String) key;
            List<String> previousValues = get((Object) headerName);
            this.headers.remove(headerName);
            return previousValues;
        }
        return null;
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        HttpHeaders httpHeaders = this.headers;
        Objects.requireNonNull(httpHeaders);
        map.forEach((v1, v2) -> {
            r1.set(v1, v2);
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
        List<List<String>> result = new ArrayList<>(this.headers.size());
        forEach((key, value) -> {
            result.add(value);
        });
        return result;
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Map.Entry<String, List<String>>>() { // from class: org.springframework.http.support.Netty5HeadersAdapter.1
            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<Map.Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return Netty5HeadersAdapter.this.headers.size();
            }
        };
    }

    public String toString() {
        return org.springframework.http.HttpHeaders.formatHeaders(this);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/support/Netty5HeadersAdapter$EntryIterator.class */
    private class EntryIterator implements Iterator<Map.Entry<String, List<String>>> {
        private final Iterator<CharSequence> names;

        private EntryIterator() {
            this.names = Netty5HeadersAdapter.this.headers.names().iterator();
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
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/support/Netty5HeadersAdapter$HeaderEntry.class */
    public class HeaderEntry implements Map.Entry<String, List<String>> {
        private final CharSequence key;

        HeaderEntry(CharSequence key) {
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
            List<String> values = Netty5HeadersAdapter.this.get((Object) this.key);
            return values != null ? values : Collections.emptyList();
        }

        @Override // java.util.Map.Entry
        public List<String> setValue(List<String> value) {
            List<String> previousValues = getValue();
            Netty5HeadersAdapter.this.headers.set(this.key, value);
            return previousValues;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/support/Netty5HeadersAdapter$HeaderNames.class */
    private class HeaderNames extends AbstractSet<String> {
        private HeaderNames() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<String> iterator() {
            return new HeaderNamesIterator(Netty5HeadersAdapter.this.headers.names().iterator());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return Netty5HeadersAdapter.this.headers.names().size();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/support/Netty5HeadersAdapter$HeaderNamesIterator.class */
    private final class HeaderNamesIterator implements Iterator<String> {
        private final Iterator<CharSequence> iterator;

        @Nullable
        private CharSequence currentName;

        private HeaderNamesIterator(Iterator<CharSequence> iterator) {
            this.iterator = iterator;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public String next() {
            this.currentName = this.iterator.next();
            return this.currentName.toString();
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.currentName == null) {
                throw new IllegalStateException("No current Header in iterator");
            }
            if (!Netty5HeadersAdapter.this.headers.contains(this.currentName)) {
                throw new IllegalStateException("Header not present: " + this.currentName);
            }
            Netty5HeadersAdapter.this.headers.remove(this.currentName);
        }
    }
}
