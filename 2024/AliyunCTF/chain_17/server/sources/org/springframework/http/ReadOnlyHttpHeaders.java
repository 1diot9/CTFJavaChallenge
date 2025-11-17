package org.springframework.http;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ReadOnlyHttpHeaders.class */
public class ReadOnlyHttpHeaders extends HttpHeaders {
    private static final long serialVersionUID = -8578554704772377436L;

    @Nullable
    private MediaType cachedContentType;

    @Nullable
    private List<MediaType> cachedAccept;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReadOnlyHttpHeaders(MultiValueMap<String, String> headers) {
        super(headers);
    }

    @Override // org.springframework.http.HttpHeaders
    public MediaType getContentType() {
        if (this.cachedContentType != null) {
            return this.cachedContentType;
        }
        MediaType contentType = super.getContentType();
        this.cachedContentType = contentType;
        return contentType;
    }

    @Override // org.springframework.http.HttpHeaders
    public List<MediaType> getAccept() {
        if (this.cachedAccept != null) {
            return this.cachedAccept;
        }
        List<MediaType> accept = super.getAccept();
        this.cachedAccept = accept;
        return accept;
    }

    @Override // org.springframework.http.HttpHeaders
    public void clearContentHeaders() {
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public List<String> get(Object key) {
        List<String> values = (List) this.headers.get(key);
        if (values != null) {
            return Collections.unmodifiableList(values);
        }
        return null;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.http.HttpHeaders, org.springframework.util.MultiValueMap
    public void add(String headerName, @Nullable String headerValue) {
        throw new UnsupportedOperationException();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.http.HttpHeaders, org.springframework.util.MultiValueMap
    public void addAll(String key, List<? extends String> values) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.HttpHeaders, org.springframework.util.MultiValueMap
    public void addAll(MultiValueMap<String, String> values) {
        throw new UnsupportedOperationException();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.http.HttpHeaders, org.springframework.util.MultiValueMap
    public void set(String headerName, @Nullable String headerValue) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.HttpHeaders, org.springframework.util.MultiValueMap
    public void setAll(Map<String, String> values) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.HttpHeaders, org.springframework.util.MultiValueMap
    public Map<String, String> toSingleValueMap() {
        return Collections.unmodifiableMap(this.headers.toSingleValueMap());
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.headers.keySet());
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public List<String> put(String key, List<String> value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public List<String> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public Collection<List<String>> values() {
        return Collections.unmodifiableCollection(this.headers.values());
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return (Set) this.headers.entrySet().stream().map(AbstractMap.SimpleImmutableEntry::new).collect(Collectors.collectingAndThen(Collectors.toCollection(LinkedHashSet::new), (v0) -> {
            return Collections.unmodifiableSet(v0);
        }));
    }

    @Override // org.springframework.http.HttpHeaders, java.util.Map
    public void forEach(BiConsumer<? super String, ? super List<String>> action) {
        this.headers.forEach((k, vs) -> {
            action.accept(k, Collections.unmodifiableList(vs));
        });
    }
}
