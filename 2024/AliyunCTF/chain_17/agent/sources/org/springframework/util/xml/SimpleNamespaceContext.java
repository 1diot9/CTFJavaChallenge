package org.springframework.util.xml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.NamespaceContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/SimpleNamespaceContext.class */
public class SimpleNamespaceContext implements NamespaceContext {
    private final Map<String, String> prefixToNamespaceUri = new HashMap();
    private final Map<String, Set<String>> namespaceUriToPrefixes = new HashMap();
    private String defaultNamespaceUri = "";

    @Override // javax.xml.namespace.NamespaceContext
    public String getNamespaceURI(String prefix) {
        Assert.notNull(prefix, "'prefix' must not be null");
        if ("xml".equals(prefix)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if ("xmlns".equals(prefix)) {
            return "http://www.w3.org/2000/xmlns/";
        }
        if ("".equals(prefix)) {
            return this.defaultNamespaceUri;
        }
        if (this.prefixToNamespaceUri.containsKey(prefix)) {
            return this.prefixToNamespaceUri.get(prefix);
        }
        return "";
    }

    @Override // javax.xml.namespace.NamespaceContext
    @Nullable
    public String getPrefix(String namespaceUri) {
        Set<String> prefixes = getPrefixesSet(namespaceUri);
        if (prefixes.isEmpty()) {
            return null;
        }
        return prefixes.iterator().next();
    }

    @Override // javax.xml.namespace.NamespaceContext
    public Iterator<String> getPrefixes(String namespaceUri) {
        return getPrefixesSet(namespaceUri).iterator();
    }

    private Set<String> getPrefixesSet(String namespaceUri) {
        Assert.notNull(namespaceUri, "'namespaceUri' must not be null");
        if (this.defaultNamespaceUri.equals(namespaceUri)) {
            return Collections.singleton("");
        }
        if ("http://www.w3.org/XML/1998/namespace".equals(namespaceUri)) {
            return Collections.singleton("xml");
        }
        if ("http://www.w3.org/2000/xmlns/".equals(namespaceUri)) {
            return Collections.singleton("xmlns");
        }
        Set<String> prefixes = this.namespaceUriToPrefixes.get(namespaceUri);
        return prefixes != null ? Collections.unmodifiableSet(prefixes) : Collections.emptySet();
    }

    public void setBindings(Map<String, String> bindings) {
        bindings.forEach(this::bindNamespaceUri);
    }

    public void bindDefaultNamespaceUri(String namespaceUri) {
        bindNamespaceUri("", namespaceUri);
    }

    public void bindNamespaceUri(String prefix, String namespaceUri) {
        Assert.notNull(prefix, "'prefix' must not be null");
        Assert.notNull(namespaceUri, "'namespaceUri' must not be null");
        if ("".equals(prefix)) {
            this.defaultNamespaceUri = namespaceUri;
            return;
        }
        this.prefixToNamespaceUri.put(prefix, namespaceUri);
        Set<String> prefixes = this.namespaceUriToPrefixes.computeIfAbsent(namespaceUri, k -> {
            return new LinkedHashSet();
        });
        prefixes.add(prefix);
    }

    public void removeBinding(@Nullable String prefix) {
        String namespaceUri;
        Set<String> prefixes;
        if ("".equals(prefix)) {
            this.defaultNamespaceUri = "";
            return;
        }
        if (prefix != null && (namespaceUri = this.prefixToNamespaceUri.remove(prefix)) != null && (prefixes = this.namespaceUriToPrefixes.get(namespaceUri)) != null) {
            prefixes.remove(prefix);
            if (prefixes.isEmpty()) {
                this.namespaceUriToPrefixes.remove(namespaceUri);
            }
        }
    }

    public void clear() {
        this.prefixToNamespaceUri.clear();
        this.namespaceUriToPrefixes.clear();
    }

    public Iterator<String> getBoundPrefixes() {
        return this.prefixToNamespaceUri.keySet().iterator();
    }
}
