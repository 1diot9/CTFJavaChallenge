package org.jooq.impl;

import org.jooq.Field;
import org.jooq.conf.TransformUnneededArithmeticExpressions;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Transformable.class */
interface Transformable<T> extends Field<T> {
    Field<?> transform(TransformUnneededArithmeticExpressions transformUnneededArithmeticExpressions);
}
