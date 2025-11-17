package com.fasterxml.jackson.core.filter;

import com.fasterxml.jackson.core.JsonPointer;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/filter/JsonPointerBasedFilter.class */
public class JsonPointerBasedFilter extends TokenFilter {
    protected final JsonPointer _pathToMatch;

    public JsonPointerBasedFilter(String ptrExpr) {
        this(JsonPointer.compile(ptrExpr));
    }

    public JsonPointerBasedFilter(JsonPointer match) {
        this._pathToMatch = match;
    }

    @Override // com.fasterxml.jackson.core.filter.TokenFilter
    public TokenFilter includeElement(int index) {
        JsonPointer next = this._pathToMatch.matchElement(index);
        if (next == null) {
            return null;
        }
        if (next.matches()) {
            return TokenFilter.INCLUDE_ALL;
        }
        return new JsonPointerBasedFilter(next);
    }

    @Override // com.fasterxml.jackson.core.filter.TokenFilter
    public TokenFilter includeProperty(String name) {
        JsonPointer next = this._pathToMatch.matchProperty(name);
        if (next == null) {
            return null;
        }
        if (next.matches()) {
            return TokenFilter.INCLUDE_ALL;
        }
        return new JsonPointerBasedFilter(next);
    }

    @Override // com.fasterxml.jackson.core.filter.TokenFilter
    public TokenFilter filterStartArray() {
        return this;
    }

    @Override // com.fasterxml.jackson.core.filter.TokenFilter
    public TokenFilter filterStartObject() {
        return this;
    }

    @Override // com.fasterxml.jackson.core.filter.TokenFilter
    protected boolean _includeScalar() {
        return this._pathToMatch.matches();
    }

    @Override // com.fasterxml.jackson.core.filter.TokenFilter
    public String toString() {
        return "[JsonPointerFilter at: " + this._pathToMatch + "]";
    }
}
