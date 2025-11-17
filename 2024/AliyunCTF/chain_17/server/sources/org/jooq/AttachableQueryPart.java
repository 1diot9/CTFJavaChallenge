package org.jooq;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.conf.ParamType;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AttachableQueryPart.class */
public interface AttachableQueryPart extends Attachable, QueryPart {
    @NotNull
    String getSQL();

    @NotNull
    String getSQL(ParamType paramType);

    @NotNull
    List<Object> getBindValues();

    @NotNull
    Map<String, Param<?>> getParams();

    @Nullable
    Param<?> getParam(String str);
}
