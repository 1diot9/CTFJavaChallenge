package org.jooq.impl;

import org.jooq.ContentType;
import org.jooq.File;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FileImpl.class */
class FileImpl implements File {
    private final String path;
    private final String name;
    private final String content;
    private final ContentType type;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileImpl(String path, String content, ContentType type) {
        this.path = path;
        this.content = content;
        this.type = type;
        this.name = path.substring(path.lastIndexOf(47) + 1);
    }

    @Override // org.jooq.File
    public final String path() {
        return this.path;
    }

    @Override // org.jooq.File
    public final String name() {
        return this.name;
    }

    @Override // org.jooq.File
    public final String content() {
        return this.content;
    }

    @Override // org.jooq.File
    public final ContentType type() {
        return this.type;
    }

    public String toString() {
        return "-- " + String.valueOf(this.type) + ": " + this.path + "\n" + this.content;
    }
}
