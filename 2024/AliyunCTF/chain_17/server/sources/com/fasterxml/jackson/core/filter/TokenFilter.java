package com.fasterxml.jackson.core.filter;

import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/filter/TokenFilter.class */
public class TokenFilter {
    public static final TokenFilter INCLUDE_ALL = new TokenFilter();

    /* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/filter/TokenFilter$Inclusion.class */
    public enum Inclusion {
        ONLY_INCLUDE_ALL,
        INCLUDE_ALL_AND_PATH,
        INCLUDE_NON_NULL
    }

    public TokenFilter filterStartObject() {
        return this;
    }

    public TokenFilter filterStartArray() {
        return this;
    }

    public void filterFinishObject() {
    }

    public void filterFinishArray() {
    }

    public TokenFilter includeProperty(String name) {
        return this;
    }

    public TokenFilter includeElement(int index) {
        return this;
    }

    public TokenFilter includeRootValue(int index) {
        return this;
    }

    public boolean includeValue(JsonParser p) throws IOException {
        return _includeScalar();
    }

    public boolean includeBoolean(boolean value) {
        return _includeScalar();
    }

    public boolean includeNull() {
        return _includeScalar();
    }

    public boolean includeString(String value) {
        return _includeScalar();
    }

    public boolean includeString(Reader r, int maxLen) {
        return _includeScalar();
    }

    public boolean includeNumber(int value) {
        return _includeScalar();
    }

    public boolean includeNumber(long value) {
        return _includeScalar();
    }

    public boolean includeNumber(float value) {
        return _includeScalar();
    }

    public boolean includeNumber(double value) {
        return _includeScalar();
    }

    public boolean includeNumber(BigDecimal value) {
        return _includeScalar();
    }

    public boolean includeNumber(BigInteger value) {
        return _includeScalar();
    }

    public boolean includeBinary() {
        return _includeScalar();
    }

    public boolean includeRawValue() {
        return _includeScalar();
    }

    public boolean includeEmbeddedValue(Object value) {
        return _includeScalar();
    }

    public boolean includeEmptyArray(boolean contentsFiltered) {
        return false;
    }

    public boolean includeEmptyObject(boolean contentsFiltered) {
        return false;
    }

    public String toString() {
        if (this == INCLUDE_ALL) {
            return "TokenFilter.INCLUDE_ALL";
        }
        return super.toString();
    }

    protected boolean _includeScalar() {
        return true;
    }
}
