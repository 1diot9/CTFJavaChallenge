package org.jooq.impl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Meta;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.Source;
import org.jooq.Version;
import org.jooq.conf.InterpreterSearchSchema;
import org.jooq.exception.DataDefinitionException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/VersionImpl.class */
public final class VersionImpl extends AbstractNode<Version> implements Version {
    final DSLContext ctx;
    final Meta meta;
    final List<Parent> parents;

    private VersionImpl(Configuration configuration, String id, Meta meta, Version root, List<Parent> parents) {
        super(configuration, id, null, root);
        this.ctx = configuration.dsl();
        this.meta = meta != null ? meta : init(this.ctx);
        this.parents = parents;
    }

    private static final Meta init(DSLContext ctx) {
        Meta result = ctx.meta("");
        List<InterpreterSearchSchema> searchPath = ctx.settings().getInterpreterSearchPath();
        for (InterpreterSearchSchema schema : searchPath) {
            result = result.apply(DSL.createSchema(DSL.schema(DSL.name(schema.getCatalog(), schema.getSchema()))));
        }
        return result;
    }

    VersionImpl(Configuration configuration, String id, Meta meta, Version root, Version parent, Queries queries) {
        this(configuration, id, meta, root, (List<Parent>) Arrays.asList(new Parent((VersionImpl) parent, queries)));
    }

    VersionImpl(Configuration configuration, String id, Meta meta, Version root, Version[] parents) {
        this(configuration, id, meta, root, wrap(parents));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public VersionImpl(Configuration configuration, String id) {
        this(configuration, id, (Meta) null, (Version) null, (List<Parent>) Arrays.asList(new Parent[0]));
    }

    private static List<Parent> wrap(Version[] parents) {
        return Tools.map(parents, p -> {
            return new Parent((VersionImpl) p, null);
        });
    }

    @Override // org.jooq.Version
    public final Meta meta() {
        return this.meta;
    }

    @Override // org.jooq.Node
    public final List<Version> parents() {
        return new AbstractList<Version>() { // from class: org.jooq.impl.VersionImpl.1
            @Override // java.util.AbstractList, java.util.List
            public Version get(int index) {
                return VersionImpl.this.parents.get(index).version;
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
            public int size() {
                return VersionImpl.this.parents.size();
            }
        };
    }

    @Override // org.jooq.Version
    public final Version apply(String newId, Query... migration) {
        return apply(newId, this.ctx.queries(migration));
    }

    @Override // org.jooq.Version
    public final Version apply(String newId, Collection<? extends Query> migration) {
        return apply(newId, this.ctx.queries(migration));
    }

    @Override // org.jooq.Version
    public final Version apply(String newId, String migration) {
        return apply(newId, this.ctx.parser().parse(migration));
    }

    @Override // org.jooq.Version
    public final Version apply(String newId, Queries migration) {
        return new VersionImpl(this.ctx.configuration(), newId, meta().apply(migration), (Version) this.root, this, migration);
    }

    @Override // org.jooq.Version
    public final Queries migrateTo(Version version) {
        if (equals(version)) {
            return this.ctx.queries(new Query[0]);
        }
        VersionImpl subgraph = ((VersionImpl) version).subgraphTo(this);
        if (subgraph == null) {
            throw new DataDefinitionException("No forward path available between versions " + id() + " and " + version.id() + ". Use Settings.migrationAllowsUndo to enable this feature.");
        }
        return migrateTo(subgraph, this.ctx.queries(new Query[0]));
    }

    private final VersionImpl subgraphTo(VersionImpl ancestor) {
        List<Parent> list = null;
        for (Parent parent : this.parents) {
            if (parent.version.equals(ancestor)) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(new Parent(new VersionImpl(this.ctx.configuration(), parent.version.id(), parent.version.meta, (Version) this.root, (List<Parent>) Collections.emptyList()), parent.queries));
            } else {
                VersionImpl p = parent.version.subgraphTo(ancestor);
                if (p != null) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(new Parent(p, parent.queries));
                }
            }
        }
        if (list == null) {
            return null;
        }
        return new VersionImpl(this.ctx.configuration(), id(), this.meta, (Version) this.root, list);
    }

    private final Queries migrateTo(VersionImpl target, Queries result) {
        if (!target.forceApply()) {
            return meta().migrateTo(target.meta());
        }
        for (Parent parent : target.parents) {
            Queries result2 = migrateTo(parent.version, result);
            if (parent.queries != null) {
                result = result2.concat(parent.queries);
            } else {
                result = result2.concat(parent.version.meta().migrateTo(target.meta()));
            }
        }
        return result;
    }

    private final boolean forceApply() {
        return Tools.anyMatch(this.parents, p -> {
            return p.queries != null || p.version.forceApply();
        });
    }

    @Override // org.jooq.Version
    public final Version commit(String newId, String... newMeta) {
        return commit(newId, this.ctx.meta(newMeta));
    }

    @Override // org.jooq.Version
    public final Version commit(String newId, Source... newMeta) {
        return commit(newId, this.ctx.meta(newMeta));
    }

    @Override // org.jooq.Version
    public final Version commit(String newId, Meta newMeta) {
        return new VersionImpl(this.ctx.configuration(), newId, newMeta, (Version) this.root, new Version[]{this});
    }

    @Override // org.jooq.Version
    public final Version merge(String newId, Version with) {
        Meta m = commonAncestor(with).meta();
        return new VersionImpl(this.ctx.configuration(), newId, m.apply(m.migrateTo(meta()).concat(m.migrateTo(with.meta()))), (Version) this.root, new Version[]{this, with});
    }

    public int hashCode() {
        int result = (31 * 1) + id().hashCode();
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VersionImpl other = (VersionImpl) obj;
        if (!id().equals(other.id())) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "-- Version: " + id() + "\n" + String.valueOf(meta());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/VersionImpl$Parent.class */
    public static final class Parent extends Record {
        private final VersionImpl version;
        private final Queries queries;

        private Parent(VersionImpl version, Queries queries) {
            this.version = version;
            this.queries = queries;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Parent.class), Parent.class, "version;queries", "FIELD:Lorg/jooq/impl/VersionImpl$Parent;->version:Lorg/jooq/impl/VersionImpl;", "FIELD:Lorg/jooq/impl/VersionImpl$Parent;->queries:Lorg/jooq/Queries;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Parent.class, Object.class), Parent.class, "version;queries", "FIELD:Lorg/jooq/impl/VersionImpl$Parent;->version:Lorg/jooq/impl/VersionImpl;", "FIELD:Lorg/jooq/impl/VersionImpl$Parent;->queries:Lorg/jooq/Queries;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public VersionImpl version() {
            return this.version;
        }

        public Queries queries() {
            return this.queries;
        }

        @Override // java.lang.Record
        public String toString() {
            return this.version.toString();
        }
    }
}
