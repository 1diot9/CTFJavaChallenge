package org.jooq.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jooq.File;
import org.jooq.Files;
import org.jooq.Version;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FilesImpl.class */
final class FilesImpl implements Files {
    private final Version from;
    private final Version to;
    private final List<File> files;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FilesImpl(Version from, Version to, Collection<? extends File> f) {
        this.from = from;
        this.to = to;
        this.files = new ArrayList(f);
    }

    @Override // java.lang.Iterable
    public final Iterator<File> iterator() {
        return this.files.iterator();
    }

    @Override // org.jooq.Files
    public final Version from() {
        return this.from;
    }

    @Override // org.jooq.Files
    public final Version to() {
        return this.to;
    }

    public String toString() {
        return String.valueOf(this.files);
    }
}
