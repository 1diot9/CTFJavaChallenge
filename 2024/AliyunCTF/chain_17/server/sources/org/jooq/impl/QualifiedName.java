package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Name;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QualifiedName.class */
public final class QualifiedName extends AbstractName {
    private final UnqualifiedName[] qualifiedName;
    private transient Integer hash;

    /* JADX INFO: Access modifiers changed from: package-private */
    public QualifiedName(String[] qualifiedName) {
        this(qualifiedName, Name.Quoted.DEFAULT);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QualifiedName(String[] qualifiedName, Name.Quoted quoted) {
        this(names(qualifiedName, quoted));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QualifiedName(Name[] qualifiedName) {
        this(last(nonEmpty(qualifiedName)));
    }

    private QualifiedName(UnqualifiedName[] qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    private static final UnqualifiedName[] names(String[] qualifiedName, Name.Quoted quoted) {
        return (UnqualifiedName[]) Tools.map(nonEmpty(qualifiedName), s -> {
            return new UnqualifiedName(s, quoted);
        }, x$0 -> {
            return new UnqualifiedName[x$0];
        });
    }

    private static final UnqualifiedName[] last(Name[] qualifiedName) {
        if (qualifiedName instanceof UnqualifiedName[]) {
            UnqualifiedName[] u = (UnqualifiedName[]) qualifiedName;
            return u;
        }
        UnqualifiedName[] result = new UnqualifiedName[qualifiedName.length];
        for (int i = 0; i < qualifiedName.length; i++) {
            Name name = qualifiedName[i];
            if (name instanceof QualifiedName) {
                QualifiedName q = (QualifiedName) name;
                result[i] = q.qualifiedName[q.qualifiedName.length - 1];
            } else {
                Name name2 = qualifiedName[i];
                if (name2 instanceof UnqualifiedName) {
                    UnqualifiedName u2 = (UnqualifiedName) name2;
                    result[i] = u2;
                } else {
                    result[i] = new UnqualifiedName(qualifiedName[i].last());
                }
            }
        }
        return result;
    }

    private static final String[] nonEmpty(String[] qualifiedName) {
        String[] result;
        int nulls = 0;
        for (String name : qualifiedName) {
            if (StringUtils.isEmpty(name)) {
                nulls++;
            }
        }
        if (nulls > 0) {
            result = new String[qualifiedName.length - nulls];
            for (int i = qualifiedName.length - 1; i >= 0; i--) {
                if (StringUtils.isEmpty(qualifiedName[i])) {
                    nulls--;
                } else {
                    result[i - nulls] = qualifiedName[i];
                }
            }
        } else {
            result = qualifiedName;
        }
        return result;
    }

    private static final Name[] nonEmpty(Name[] names) {
        Name[] result;
        int nulls = 0;
        for (Name name : names) {
            if (name == null || name.equals(NO_NAME)) {
                nulls++;
            }
        }
        if (nulls > 0) {
            result = new Name[names.length - nulls];
            for (int i = names.length - 1; i >= 0; i--) {
                if (names[i] == null || names[i].equals(NO_NAME)) {
                    nulls--;
                } else {
                    result[i - nulls] = names[i];
                }
            }
        } else {
            result = names;
        }
        return result;
    }

    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (ctx.qualify()) {
            String separator = "";
            for (UnqualifiedName name : this.qualifiedName) {
                ctx.sql(separator).visit(name);
                separator = ".";
            }
            return;
        }
        ctx.visit(this.qualifiedName[this.qualifiedName.length - 1]);
    }

    @Override // org.jooq.Name
    public final String first() {
        if (this.qualifiedName.length > 0) {
            return this.qualifiedName[0].last();
        }
        return null;
    }

    @Override // org.jooq.Name
    public final String last() {
        if (this.qualifiedName.length > 0) {
            return this.qualifiedName[this.qualifiedName.length - 1].last();
        }
        return null;
    }

    @Override // org.jooq.Name
    public final boolean empty() {
        return Tools.allMatch(this.qualifiedName, n -> {
            return n.empty();
        });
    }

    @Override // org.jooq.Name
    public final boolean qualified() {
        return this.qualifiedName.length > 1;
    }

    @Override // org.jooq.Name
    public final boolean qualifierQualified() {
        return this.qualifiedName.length > 2;
    }

    @Override // org.jooq.Name
    public final Name qualifier() {
        if (this.qualifiedName.length <= 1) {
            return null;
        }
        if (this.qualifiedName.length == 2) {
            return this.qualifiedName[0];
        }
        UnqualifiedName[] qualifier = new UnqualifiedName[this.qualifiedName.length - 1];
        System.arraycopy(this.qualifiedName, 0, qualifier, 0, qualifier.length);
        return new QualifiedName(qualifier);
    }

    @Override // org.jooq.Name
    public final Name unqualifiedName() {
        if (this.qualifiedName.length == 0) {
            return this;
        }
        return this.qualifiedName[this.qualifiedName.length - 1];
    }

    @Override // org.jooq.Name
    public final Name.Quoted quoted() {
        Name.Quoted result = null;
        for (UnqualifiedName name : this.qualifiedName) {
            if (result == null) {
                result = name.quoted();
            } else if (result != name.quoted()) {
                return Name.Quoted.MIXED;
            }
        }
        return result == null ? Name.Quoted.DEFAULT : result;
    }

    @Override // org.jooq.Name
    public final Name quotedName() {
        return new QualifiedName((Name[]) Tools.map(this.qualifiedName, n -> {
            return n.quotedName();
        }, x$0 -> {
            return new Name[x$0];
        }));
    }

    @Override // org.jooq.Name
    public final Name unquotedName() {
        return new QualifiedName((Name[]) Tools.map(this.qualifiedName, n -> {
            return n.unquotedName();
        }, x$0 -> {
            return new Name[x$0];
        }));
    }

    @Override // org.jooq.Name
    public final String[] getName() {
        return (String[]) Tools.map(this.qualifiedName, n -> {
            return n.last();
        }, x$0 -> {
            return new String[x$0];
        });
    }

    @Override // org.jooq.Name
    public final Name[] parts() {
        return (Name[]) this.qualifiedName.clone();
    }

    @Override // org.jooq.impl.AbstractName, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        int i;
        int hashCode;
        if (this.hash == null) {
            int h = 1;
            for (int i2 = 0; i2 < this.qualifiedName.length; i2++) {
                UnqualifiedName n = this.qualifiedName[i2];
                if (n.name == null) {
                    i = 31 * h;
                    hashCode = 0;
                } else {
                    i = 31 * h;
                    hashCode = n.name.hashCode();
                }
                h = i + hashCode;
            }
            this.hash = Integer.valueOf(h);
        }
        return this.hash.intValue();
    }
}
