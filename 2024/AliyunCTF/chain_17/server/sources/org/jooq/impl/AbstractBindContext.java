package org.jooq.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.jooq.BindContext;
import org.jooq.Configuration;
import org.jooq.ExecuteContext;
import org.jooq.Field;
import org.jooq.QueryPart;
import org.jooq.QueryPartInternal;
import org.jooq.exception.DataAccessException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractBindContext.class */
public abstract class AbstractBindContext extends AbstractContext<BindContext> implements BindContext {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractBindContext(Configuration configuration, ExecuteContext ctx, PreparedStatement stmt) {
        super(configuration, ctx, stmt);
    }

    @Override // org.jooq.impl.AbstractContext
    protected void visit0(QueryPartInternal internal) {
        bindInternal(internal);
    }

    @Override // org.jooq.Context
    public final BindContext bindValue(Object value, Field<?> field) throws DataAccessException {
        try {
            return bindValue0(value, field);
        } catch (SQLException e) {
            throw Tools.translate((String) null, e);
        }
    }

    @Override // org.jooq.Context
    public final String peekAlias() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.Context
    public final String nextAlias() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.Context
    public final String render() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.Context
    public final String render(QueryPart part) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext keyword(String keyword) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext sql(String sql) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext sql(String sql, boolean literal) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext sqlIndentStart(String sql) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext sqlIndentEnd(String sql) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext sqlIndentStart() {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext sqlIndentEnd() {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext sql(char sql) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext sqlIndentStart(char sql) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext sqlIndentEnd(char sql) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext sql(int sql) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext sql(long sql) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext sql(float sql) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext sql(double sql) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext format(boolean format) {
        return this;
    }

    @Override // org.jooq.Context
    public final boolean format() {
        return false;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext formatNewLine() {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext formatNewLineAfterPrintMargin() {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext formatSeparator() {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext separatorRequired(boolean separatorRequired) {
        return this;
    }

    @Override // org.jooq.Context
    public final boolean separatorRequired() {
        return false;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext formatIndentStart() {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext formatIndentStart(int indent) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext formatIndentLockStart() {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext formatIndentEnd() {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext formatIndentEnd(int indent) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext formatIndentLockEnd() {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext formatPrintMargin(int margin) {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final BindContext literal(String literal) {
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void bindInternal(QueryPartInternal internal) {
        internal.accept(this);
    }

    protected BindContext bindValue0(Object value, Field<?> field) throws SQLException {
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb);
        return sb.toString();
    }
}
