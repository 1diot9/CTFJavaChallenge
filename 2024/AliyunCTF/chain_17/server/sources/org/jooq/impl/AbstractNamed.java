package org.jooq.impl;

import java.util.ArrayList;
import java.util.List;
import org.jooq.Comment;
import org.jooq.Name;
import org.jooq.Named;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractNamed.class */
abstract class AbstractNamed extends AbstractQueryPart implements Named {
    private final Name name;
    private final Comment comment;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractNamed(Name name, Comment comment) {
        this.name = name == null ? AbstractName.NO_NAME : name;
        this.comment = comment == null ? CommentImpl.NO_COMMENT : comment;
    }

    @Override // org.jooq.Named
    public final String getName() {
        return (String) StringUtils.defaultIfNull(getQualifiedName().last(), "");
    }

    @Override // org.jooq.Named
    public Name getQualifiedName() {
        return this.name;
    }

    @Override // org.jooq.Named
    public final Name getUnqualifiedName() {
        return this.name.unqualifiedName();
    }

    @Override // org.jooq.Named
    public final String getComment() {
        return this.comment.getComment();
    }

    @Override // org.jooq.Named
    public final Comment getCommentPart() {
        return this.comment;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        return getQualifiedName().hashCode();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (that instanceof AbstractNamed) {
            AbstractNamed n = (AbstractNamed) that;
            if (!getQualifiedName().equals(n.getQualifiedName())) {
                return false;
            }
        }
        return super.equals(that);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Name nameOrDefault(Named named) {
        return named == null ? AbstractName.NO_NAME : named.getUnqualifiedName();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Name qualify(Named qualifier, Name name) {
        return (qualifier == null || name == null || name.empty() || name.qualified()) ? name : qualifier.getQualifiedName().append(name);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <N extends Named> List<N> findAll(String name, Iterable<? extends N> in) {
        List<N> result = new ArrayList<>();
        for (N n : in) {
            if (n.getName().equals(name)) {
                result.add(n);
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <N extends Named> List<N> findAll(Name name, Iterable<? extends N> in) {
        List<N> result = new ArrayList<>();
        for (N n : in) {
            if (n.getQualifiedName().equals(name) || n.getUnqualifiedName().equals(name)) {
                result.add(n);
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <N extends Named> N find(String name, Iterable<? extends N> in) {
        return (N) Tools.findAny(in, n -> {
            return n.getName().equals(name);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <N extends Named> N find(Name name, Iterable<? extends N> in) {
        N unqualified = null;
        for (N n : in) {
            if (n.getQualifiedName().equals(name)) {
                return n;
            }
            if (unqualified == null && n.getUnqualifiedName().equals(name.unqualifiedName())) {
                unqualified = n;
            }
        }
        return unqualified;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <N extends Named> N findIgnoreCase(String name, Iterable<? extends N> in) {
        return (N) Tools.findAny(in, n -> {
            return n.getName().equalsIgnoreCase(name);
        });
    }

    static final <N extends Named> N findIgnoreCase(Name name, Iterable<? extends N> in) {
        N unqualified = null;
        for (N n : in) {
            if (n.getQualifiedName().equalsIgnoreCase(name)) {
                return n;
            }
            if (unqualified == null && n.getUnqualifiedName().equalsIgnoreCase(name.unqualifiedName())) {
                unqualified = n;
            }
        }
        return unqualified;
    }

    @Override // org.jooq.Named
    public final Name $name() {
        return getQualifiedName();
    }
}
