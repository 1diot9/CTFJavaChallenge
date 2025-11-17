package org.h2.expression.aggregate;

import org.h2.expression.Expression;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/ListaggArguments.class */
public final class ListaggArguments {
    private Expression separator;
    private boolean onOverflowTruncate;
    private Expression filter;
    private boolean withoutCount;

    public void setSeparator(Expression expression) {
        this.separator = expression;
    }

    public Expression getSeparator() {
        return this.separator;
    }

    public String getEffectiveSeparator() {
        if (this.separator != null) {
            String string = this.separator.getValue(null).getString();
            return string != null ? string : "";
        }
        return ",";
    }

    public void setOnOverflowTruncate(boolean z) {
        this.onOverflowTruncate = z;
    }

    public boolean getOnOverflowTruncate() {
        return this.onOverflowTruncate;
    }

    public void setFilter(Expression expression) {
        this.filter = expression;
    }

    public Expression getFilter() {
        return this.filter;
    }

    public String getEffectiveFilter() {
        if (this.filter != null) {
            String string = this.filter.getValue(null).getString();
            return string != null ? string : "";
        }
        return "...";
    }

    public void setWithoutCount(boolean z) {
        this.withoutCount = z;
    }

    public boolean isWithoutCount() {
        return this.withoutCount;
    }
}
