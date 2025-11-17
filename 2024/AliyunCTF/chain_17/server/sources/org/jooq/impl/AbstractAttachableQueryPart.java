package org.jooq.impl;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jooq.AttachableQueryPart;
import org.jooq.Configuration;
import org.jooq.Param;
import org.jooq.conf.ParamType;
import org.jooq.conf.SettingsTools;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractAttachableQueryPart.class */
abstract class AbstractAttachableQueryPart extends AbstractQueryPart implements AttachableQueryPart {
    private Configuration configuration;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractAttachableQueryPart(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override // org.jooq.Attachable
    public final void attach(Configuration c) {
        this.configuration = c;
    }

    @Override // org.jooq.Attachable
    public final void detach() {
        attach(null);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.Attachable
    public final Configuration configuration() {
        return this.configuration;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @NotNull
    public final Configuration configurationOrDefault() {
        return Tools.configuration(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @NotNull
    public final Configuration configurationOrThrow() {
        return Tools.configurationOrThrow(this);
    }

    @Override // org.jooq.AttachableQueryPart
    public final List<Object> getBindValues() {
        return create().extractBindValues(this);
    }

    @Override // org.jooq.AttachableQueryPart
    public final Map<String, Param<?>> getParams() {
        return create().extractParams(this);
    }

    @Override // org.jooq.AttachableQueryPart
    public final Param<?> getParam(String name) {
        return create().extractParam(this, name);
    }

    @Override // org.jooq.AttachableQueryPart
    public final String getSQL() {
        return getSQL(SettingsTools.getParamType(Tools.settings(configuration())));
    }

    @Override // org.jooq.AttachableQueryPart
    public final String getSQL(ParamType paramType) {
        switch (paramType) {
            case INDEXED:
                return create().render(this);
            case INLINED:
                return create().renderInlined(this);
            case NAMED:
                return create().renderNamedParams(this);
            case NAMED_OR_INLINED:
                return create().renderNamedOrInlinedParams(this);
            case FORCE_INDEXED:
                return create().renderContext().paramType(paramType).visit(this).render();
            default:
                throw new IllegalArgumentException("ParamType not supported: " + String.valueOf(paramType));
        }
    }
}
