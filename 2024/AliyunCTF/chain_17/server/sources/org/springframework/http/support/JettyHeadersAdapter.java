package org.springframework.http.support;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/support/JettyHeadersAdapter.class */
public final class JettyHeadersAdapter implements MultiValueMap<String, String> {
    private final HttpFields headers;

    public JettyHeadersAdapter(HttpFields headers) {
        Assert.notNull(headers, "Headers must not be null");
        this.headers = headers;
    }

    @Override // org.springframework.util.MultiValueMap
    public String getFirst(String key) {
        return this.headers.get(key);
    }

    @Override // org.springframework.util.MultiValueMap
    public void add(String key, @Nullable String value) {
        if (value != null) {
            HttpFields.Mutable mutableHttpFields = mutableFields();
            mutableHttpFields.add(key, value);
        }
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
        HttpFields.Mutable mutableHttpFields = mutableFields();
        if (value != null) {
            mutableHttpFields.put(key, value);
        } else {
            mutableHttpFields.remove(key);
        }
    }

    @Override // org.springframework.util.MultiValueMap
    public void setAll(Map<String, String> values) {
        values.forEach(this::set);
    }

    @Override // org.springframework.util.MultiValueMap
    public Map<String, String> toSingleValueMap() {
        Map<String, String> singleValueMap = CollectionUtils.newLinkedHashMap(this.headers.size());
        Iterator<HttpField> iterator = this.headers.iterator();
        iterator.forEachRemaining(field -> {
            if (!singleValueMap.containsKey(field.getName())) {
                singleValueMap.put(field.getName(), field.getValue());
            }
        });
        return singleValueMap;
    }

    @Override // java.util.Map
    public int size() {
        return this.headers.getFieldNamesCollection().size();
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
            String searchString = (String) value;
            if (this.headers.stream().anyMatch(field -> {
                return field.contains(searchString);
            })) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> get(Object key) {
        if (containsKey(key)) {
            return this.headers.getValuesList((String) key);
        }
        return null;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> put(String key, List<String> value) {
        HttpFields.Mutable mutableHttpFields = mutableFields();
        List<String> oldValues = get((Object) key);
        mutableHttpFields.put(key, value);
        return oldValues;
    }

    @Override // java.util.Map
    @Nullable
    public List<String> remove(Object key) {
        HttpFields.Mutable mutableHttpFields = mutableFields();
        if (key instanceof String) {
            String name = (String) key;
            List<String> oldValues = get(key);
            mutableHttpFields.remove(name);
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
        HttpFields.Mutable mutableHttpFields = mutableFields();
        mutableHttpFields.clear();
    }

    @Override // java.util.Map
    public Set<String> keySet() {
        return new HeaderNames();
    }

    @Override // java.util.Map
    public Collection<List<String>> values() {
        Stream stream = this.headers.getFieldNamesCollection().stream();
        HttpFields httpFields = this.headers;
        Objects.requireNonNull(httpFields);
        return stream.map(httpFields::getValuesList).toList();
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Map.Entry<String, List<String>>>() { // from class: org.springframework.http.support.JettyHeadersAdapter.1
            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<Map.Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return JettyHeadersAdapter.this.headers.size();
            }
        };
    }

    private HttpFields.Mutable mutableFields() {
        HttpFields.Mutable mutable = this.headers;
        if (mutable instanceof HttpFields.Mutable) {
            HttpFields.Mutable mutableHttpFields = mutable;
            return mutableHttpFields;
        }
        throw new IllegalStateException("Immutable headers");
    }

    public String toString() {
        return HttpHeaders.formatHeaders(this);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/support/JettyHeadersAdapter$EntryIterator.class */
    private class EntryIterator implements Iterator<Map.Entry<String, List<String>>> {
        private final Iterator<String> names;

        private EntryIterator() {
            this.names = JettyHeadersAdapter.this.headers.getFieldNamesCollection().iterator();
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
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/support/JettyHeadersAdapter$HeaderEntry.class */
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
            return JettyHeadersAdapter.this.headers.getValuesList(this.key);
        }

        @Override // java.util.Map.Entry
        public List<String> setValue(List<String> value) {
            HttpFields.Mutable mutableHttpFields = JettyHeadersAdapter.this.mutableFields();
            List<String> previousValues = JettyHeadersAdapter.this.headers.getValuesList(this.key);
            mutableHttpFields.put(this.key, value);
            return previousValues;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/support/JettyHeadersAdapter$HeaderNames.class */
    private class HeaderNames extends AbstractSet<String> {
        private HeaderNames() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<String> iterator() {
            return new HeaderNamesIterator(JettyHeadersAdapter.this.headers.getFieldNamesCollection().iterator());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return JettyHeadersAdapter.this.headers.getFieldNamesCollection().size();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/support/JettyHeadersAdapter$HeaderNamesIterator.class */
    private final class HeaderNamesIterator implements Iterator<String> {
        private final Iterator<String> iterator;

        @Nullable
        private String currentName;

        private HeaderNamesIterator(Iterator<String> iterator) {
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
            return this.currentName;
        }

        @Override // java.util.Iterator
        public void remove() {
            HttpFields.Mutable mutableHttpFields = JettyHeadersAdapter.this.mutableFields();
            if (this.currentName == null) {
                throw new IllegalStateException("No current Header in iterator");
            }
            if (!JettyHeadersAdapter.this.headers.contains(this.currentName)) {
                throw new IllegalStateException("Header not present: " + this.currentName);
            }
            mutableHttpFields.remove(this.currentName);
        }
    }
}
