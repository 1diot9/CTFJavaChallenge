package org.jooq.impl;

import ch.qos.logback.core.CoreConstants;
import org.jooq.SQL;
import org.slf4j.Marker;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Operators.class */
public final class Operators {
    static final SQL OP_AMP = DSL.raw(BeanFactory.FACTORY_BEAN_PREFIX);
    static final SQL OP_AST = DSL.raw("*");
    static final SQL OP_COMMAT = DSL.raw("@");
    static final SQL OP_DOLLAR = DSL.raw(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX);
    static final SQL OP_EQUALS = DSL.raw("=");
    static final SQL OP_EXCL = DSL.raw("!");
    static final SQL OP_GT = DSL.raw(">");
    static final SQL OP_HAT = DSL.raw("^");
    static final SQL OP_LT = DSL.raw("<");
    static final SQL OP_NUM = DSL.raw("#");
    static final SQL OP_PERCNT = DSL.raw(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL);
    static final SQL OP_PLUS = DSL.raw(Marker.ANY_NON_NULL_MARKER);
    static final SQL OP_QUEST = DSL.raw(CoreConstants.NA);
    static final SQL OP_SOL = DSL.raw("/");
    static final SQL OP_VERBAR = DSL.raw("|");

    Operators() {
    }
}
