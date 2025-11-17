package org.jooq.impl;

import ch.qos.logback.core.CoreConstants;
import java.util.Map;
import org.jooq.BindingSQLContext;
import org.jooq.Configuration;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.ConverterContext;
import org.jooq.RenderContext;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBindingSQLContext.class */
public class DefaultBindingSQLContext<U> extends AbstractScope implements BindingSQLContext<U> {
    private final RenderContext render;
    private final U value;
    private final String variable;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultBindingSQLContext(Configuration configuration, Map<Object, Object> data, RenderContext render, U value) {
        this(configuration, data, render, value, CoreConstants.NA);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultBindingSQLContext(Configuration configuration, Map<Object, Object> data, RenderContext render, U value, String variable) {
        super(configuration, data);
        this.render = render;
        this.value = value;
        this.variable = variable;
    }

    @Override // org.jooq.BindingScope
    public final ConverterContext converterContext() {
        return this.render.converterContext();
    }

    @Override // org.jooq.BindingSQLContext
    public final RenderContext render() {
        return this.render;
    }

    @Override // org.jooq.BindingSQLContext
    public final U value() {
        return this.value;
    }

    @Override // org.jooq.BindingSQLContext
    public final String variable() {
        return this.variable;
    }

    @Override // org.jooq.BindingSQLContext
    public <T> BindingSQLContext<T> convert(Converter<? extends T, ? super U> converter) {
        return new DefaultBindingSQLContext(this.configuration, this.data, this.render, ContextConverter.scoped(converter).to(this.value, converterContext()), this.variable);
    }

    public String toString() {
        return "DefaultBindingSQLContext [value=" + String.valueOf(this.value) + ", variable=" + this.variable + "]";
    }
}
