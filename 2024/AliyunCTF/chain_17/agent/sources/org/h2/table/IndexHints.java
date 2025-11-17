package org.h2.table;

import java.util.LinkedHashSet;
import java.util.Set;
import org.h2.index.Index;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/IndexHints.class */
public final class IndexHints {
    private final LinkedHashSet<String> allowedIndexes;

    private IndexHints(LinkedHashSet<String> linkedHashSet) {
        this.allowedIndexes = linkedHashSet;
    }

    public static IndexHints createUseIndexHints(LinkedHashSet<String> linkedHashSet) {
        return new IndexHints(linkedHashSet);
    }

    public Set<String> getAllowedIndexes() {
        return this.allowedIndexes;
    }

    public String toString() {
        return "IndexHints{allowedIndexes=" + this.allowedIndexes + '}';
    }

    public boolean allowIndex(Index index) {
        return this.allowedIndexes.contains(index.getName());
    }
}
