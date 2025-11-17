package org.jooq.impl;

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jooq.BindContext;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.QueryPartInternal;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ParamCollector.class */
public final class ParamCollector extends AbstractBindContext {
    final Map<String, Param<?>> resultFlat;
    final Map<String, List<Param<?>>> result;
    final List<Map.Entry<String, Param<?>>> resultList;
    private final boolean includeInlinedParams;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParamCollector(Configuration configuration, boolean includeInlinedParams) {
        super(configuration, null, null);
        this.resultFlat = new LinkedHashMap();
        this.result = new LinkedHashMap();
        this.resultList = new ArrayList();
        this.includeInlinedParams = includeInlinedParams;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jooq.impl.AbstractBindContext
    public final void bindInternal(QueryPartInternal internal) {
        if (internal instanceof Param) {
            Param<?> param = (Param) internal;
            if (this.includeInlinedParams || !param.isInline()) {
                String i = String.valueOf(nextIndex());
                String paramName = param.getParamName();
                if (StringUtils.isBlank(paramName)) {
                    this.resultFlat.put(i, param);
                    this.resultList.add(new AbstractMap.SimpleImmutableEntry(i, param));
                    result(i).add(param);
                    return;
                } else {
                    this.resultFlat.put(param.getParamName(), param);
                    this.resultList.add(new AbstractMap.SimpleImmutableEntry(param.getParamName(), param));
                    result(param.getParamName()).add(param);
                    return;
                }
            }
            return;
        }
        super.bindInternal(internal);
    }

    private final List<Param<?>> result(String key) {
        return this.result.computeIfAbsent(key, k -> {
            return new ArrayList();
        });
    }

    @Override // org.jooq.impl.AbstractBindContext
    protected final BindContext bindValue0(Object value, Field<?> field) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
