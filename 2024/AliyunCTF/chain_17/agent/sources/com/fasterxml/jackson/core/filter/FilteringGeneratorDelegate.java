package com.fasterxml.jackson.core.filter;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.filter.TokenFilter;
import com.fasterxml.jackson.core.util.JsonGeneratorDelegate;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/filter/FilteringGeneratorDelegate.class */
public class FilteringGeneratorDelegate extends JsonGeneratorDelegate {
    protected TokenFilter rootFilter;
    protected boolean _allowMultipleMatches;
    protected TokenFilter.Inclusion _inclusion;
    protected TokenFilterContext _filterContext;
    protected TokenFilter _itemFilter;
    protected int _matchCount;

    @Deprecated
    public FilteringGeneratorDelegate(JsonGenerator d, TokenFilter f, boolean includePath, boolean allowMultipleMatches) {
        this(d, f, includePath ? TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH : TokenFilter.Inclusion.ONLY_INCLUDE_ALL, allowMultipleMatches);
    }

    public FilteringGeneratorDelegate(JsonGenerator d, TokenFilter f, TokenFilter.Inclusion inclusion, boolean allowMultipleMatches) {
        super(d, false);
        this.rootFilter = f;
        this._itemFilter = f;
        this._filterContext = TokenFilterContext.createRootContext(f);
        this._inclusion = inclusion;
        this._allowMultipleMatches = allowMultipleMatches;
    }

    public TokenFilter getFilter() {
        return this.rootFilter;
    }

    public JsonStreamContext getFilterContext() {
        return this._filterContext;
    }

    public int getMatchCount() {
        return this._matchCount;
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public JsonStreamContext getOutputContext() {
        return this._filterContext;
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeStartArray() throws IOException {
        if (this._itemFilter == null) {
            this._filterContext = this._filterContext.createChildArrayContext(null, false);
            return;
        }
        if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
            this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
            this.delegate.writeStartArray();
            return;
        }
        this._itemFilter = this._filterContext.checkValue(this._itemFilter);
        if (this._itemFilter == null) {
            this._filterContext = this._filterContext.createChildArrayContext(null, false);
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            this._itemFilter = this._itemFilter.filterStartArray();
        }
        if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
            _checkParentPath();
            this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
            this.delegate.writeStartArray();
        } else {
            if (this._itemFilter != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                _checkParentPath(false);
                this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
                this.delegate.writeStartArray();
                return;
            }
            this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, false);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeStartArray(int size) throws IOException {
        if (this._itemFilter == null) {
            this._filterContext = this._filterContext.createChildArrayContext(null, false);
            return;
        }
        if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
            this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
            this.delegate.writeStartArray(size);
            return;
        }
        this._itemFilter = this._filterContext.checkValue(this._itemFilter);
        if (this._itemFilter == null) {
            this._filterContext = this._filterContext.createChildArrayContext(null, false);
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            this._itemFilter = this._itemFilter.filterStartArray();
        }
        if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
            _checkParentPath();
            this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
            this.delegate.writeStartArray(size);
        } else {
            if (this._itemFilter != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                _checkParentPath(false);
                this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
                this.delegate.writeStartArray(size);
                return;
            }
            this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, false);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeStartArray(Object forValue) throws IOException {
        if (this._itemFilter == null) {
            this._filterContext = this._filterContext.createChildArrayContext(null, false);
            return;
        }
        if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
            this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
            this.delegate.writeStartArray(forValue);
            return;
        }
        this._itemFilter = this._filterContext.checkValue(this._itemFilter);
        if (this._itemFilter == null) {
            this._filterContext = this._filterContext.createChildArrayContext(null, false);
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            this._itemFilter = this._itemFilter.filterStartArray();
        }
        if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
            _checkParentPath();
            this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
            this.delegate.writeStartArray(forValue);
        } else {
            if (this._itemFilter != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                _checkParentPath(false);
                this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
                this.delegate.writeStartArray(forValue);
                return;
            }
            this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, false);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeStartArray(Object forValue, int size) throws IOException {
        if (this._itemFilter == null) {
            this._filterContext = this._filterContext.createChildArrayContext(null, false);
            return;
        }
        if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
            this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
            this.delegate.writeStartArray(forValue, size);
            return;
        }
        this._itemFilter = this._filterContext.checkValue(this._itemFilter);
        if (this._itemFilter == null) {
            this._filterContext = this._filterContext.createChildArrayContext(null, false);
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            this._itemFilter = this._itemFilter.filterStartArray();
        }
        if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
            _checkParentPath();
            this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
            this.delegate.writeStartArray(forValue, size);
        } else {
            if (this._itemFilter != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                _checkParentPath(false);
                this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, true);
                this.delegate.writeStartArray(forValue, size);
                return;
            }
            this._filterContext = this._filterContext.createChildArrayContext(this._itemFilter, false);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeEndArray() throws IOException {
        this._filterContext = this._filterContext.closeArray(this.delegate);
        if (this._filterContext != null) {
            this._itemFilter = this._filterContext.getFilter();
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeStartObject() throws IOException {
        if (this._itemFilter == null) {
            this._filterContext = this._filterContext.createChildObjectContext(this._itemFilter, false);
            return;
        }
        if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
            this._filterContext = this._filterContext.createChildObjectContext(this._itemFilter, true);
            this.delegate.writeStartObject();
            return;
        }
        TokenFilter f = this._filterContext.checkValue(this._itemFilter);
        if (f == null) {
            this._filterContext = this._filterContext.createChildObjectContext(null, false);
            return;
        }
        if (f != TokenFilter.INCLUDE_ALL) {
            f = f.filterStartObject();
        }
        if (f == TokenFilter.INCLUDE_ALL) {
            _checkParentPath();
            this._filterContext = this._filterContext.createChildObjectContext(f, true);
            this.delegate.writeStartObject();
        } else {
            if (f != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                _checkParentPath(false);
                this._filterContext = this._filterContext.createChildObjectContext(f, true);
                this.delegate.writeStartObject();
                return;
            }
            this._filterContext = this._filterContext.createChildObjectContext(f, false);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeStartObject(Object forValue) throws IOException {
        if (this._itemFilter == null) {
            this._filterContext = this._filterContext.createChildObjectContext(this._itemFilter, false);
            return;
        }
        if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
            this._filterContext = this._filterContext.createChildObjectContext(this._itemFilter, true);
            this.delegate.writeStartObject(forValue);
            return;
        }
        TokenFilter f = this._filterContext.checkValue(this._itemFilter);
        if (f == null) {
            this._filterContext = this._filterContext.createChildObjectContext(null, false);
            return;
        }
        if (f != TokenFilter.INCLUDE_ALL) {
            f = f.filterStartObject();
        }
        if (f == TokenFilter.INCLUDE_ALL) {
            _checkParentPath();
            this._filterContext = this._filterContext.createChildObjectContext(f, true);
            this.delegate.writeStartObject(forValue);
        } else {
            if (f != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                _checkParentPath(false);
                this._filterContext = this._filterContext.createChildObjectContext(f, true);
                this.delegate.writeStartObject(forValue);
                return;
            }
            this._filterContext = this._filterContext.createChildObjectContext(f, false);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeStartObject(Object forValue, int size) throws IOException {
        if (this._itemFilter == null) {
            this._filterContext = this._filterContext.createChildObjectContext(this._itemFilter, false);
            return;
        }
        if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
            this._filterContext = this._filterContext.createChildObjectContext(this._itemFilter, true);
            this.delegate.writeStartObject(forValue, size);
            return;
        }
        TokenFilter f = this._filterContext.checkValue(this._itemFilter);
        if (f == null) {
            this._filterContext = this._filterContext.createChildObjectContext(null, false);
            return;
        }
        if (f != TokenFilter.INCLUDE_ALL) {
            f = f.filterStartObject();
        }
        if (f == TokenFilter.INCLUDE_ALL) {
            _checkParentPath();
            this._filterContext = this._filterContext.createChildObjectContext(f, true);
            this.delegate.writeStartObject(forValue, size);
            return;
        }
        this._filterContext = this._filterContext.createChildObjectContext(f, false);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeEndObject() throws IOException {
        this._filterContext = this._filterContext.closeObject(this.delegate);
        if (this._filterContext != null) {
            this._itemFilter = this._filterContext.getFilter();
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeFieldName(String name) throws IOException {
        TokenFilter state = this._filterContext.setFieldName(name);
        if (state == null) {
            this._itemFilter = null;
            return;
        }
        if (state == TokenFilter.INCLUDE_ALL) {
            this._itemFilter = state;
            this.delegate.writeFieldName(name);
            return;
        }
        TokenFilter state2 = state.includeProperty(name);
        this._itemFilter = state2;
        if (state2 == TokenFilter.INCLUDE_ALL) {
            _checkPropertyParentPath();
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeFieldName(SerializableString name) throws IOException {
        TokenFilter state = this._filterContext.setFieldName(name.getValue());
        if (state == null) {
            this._itemFilter = null;
            return;
        }
        if (state == TokenFilter.INCLUDE_ALL) {
            this._itemFilter = state;
            this.delegate.writeFieldName(name);
            return;
        }
        TokenFilter state2 = state.includeProperty(name.getValue());
        this._itemFilter = state2;
        if (state2 == TokenFilter.INCLUDE_ALL) {
            _checkPropertyParentPath();
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeFieldId(long id) throws IOException {
        writeFieldName(Long.toString(id));
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeString(String value) throws IOException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeString(value)) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeString(value);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeString(char[] text, int offset, int len) throws IOException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            String value = new String(text, offset, len);
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeString(value)) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeString(text, offset, len);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeString(SerializableString value) throws IOException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeString(value.getValue())) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeString(value);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeString(Reader reader, int len) throws IOException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeString(reader, len)) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeString(reader, len);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
        if (_checkRawValueWrite()) {
            this.delegate.writeRawUTF8String(text, offset, length);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
        if (_checkRawValueWrite()) {
            this.delegate.writeUTF8String(text, offset, length);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(String text) throws IOException {
        if (_checkRawValueWrite()) {
            this.delegate.writeRaw(text);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(String text, int offset, int len) throws IOException {
        if (_checkRawValueWrite()) {
            this.delegate.writeRaw(text, offset, len);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(SerializableString text) throws IOException {
        if (_checkRawValueWrite()) {
            this.delegate.writeRaw(text);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(char[] text, int offset, int len) throws IOException {
        if (_checkRawValueWrite()) {
            this.delegate.writeRaw(text, offset, len);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(char c) throws IOException {
        if (_checkRawValueWrite()) {
            this.delegate.writeRaw(c);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeRawValue(String text) throws IOException {
        if (_checkRawValueWrite()) {
            this.delegate.writeRawValue(text);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeRawValue(String text, int offset, int len) throws IOException {
        if (_checkRawValueWrite()) {
            this.delegate.writeRawValue(text, offset, len);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeRawValue(char[] text, int offset, int len) throws IOException {
        if (_checkRawValueWrite()) {
            this.delegate.writeRawValue(text, offset, len);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
        if (_checkBinaryWrite()) {
            this.delegate.writeBinary(b64variant, data, offset, len);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException {
        if (_checkBinaryWrite()) {
            return this.delegate.writeBinary(b64variant, data, dataLength);
        }
        return -1;
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(short v) throws IOException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeNumber((int) v)) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeNumber(v);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(int v) throws IOException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeNumber(v)) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeNumber(v);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(long v) throws IOException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeNumber(v)) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeNumber(v);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(BigInteger v) throws IOException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeNumber(v)) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeNumber(v);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(double v) throws IOException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeNumber(v)) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeNumber(v);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(float v) throws IOException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeNumber(v)) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeNumber(v);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(BigDecimal v) throws IOException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeNumber(v)) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeNumber(v);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(String encodedValue) throws IOException, UnsupportedOperationException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeRawValue()) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeNumber(encodedValue);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(char[] encodedValueBuffer, int offset, int length) throws IOException, UnsupportedOperationException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeRawValue()) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeNumber(encodedValueBuffer, offset, length);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeBoolean(boolean v) throws IOException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeBoolean(v)) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeBoolean(v);
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeNull() throws IOException {
        if (this._itemFilter == null) {
            return;
        }
        if (this._itemFilter != TokenFilter.INCLUDE_ALL) {
            TokenFilter state = this._filterContext.checkValue(this._itemFilter);
            if (state == null) {
                return;
            }
            if (state != TokenFilter.INCLUDE_ALL && !state.includeNull()) {
                return;
            } else {
                _checkParentPath();
            }
        }
        this.delegate.writeNull();
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeOmittedField(String fieldName) throws IOException {
        if (this._itemFilter != null) {
            this.delegate.writeOmittedField(fieldName);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeObjectId(Object id) throws IOException {
        if (this._itemFilter != null) {
            this.delegate.writeObjectId(id);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeObjectRef(Object id) throws IOException {
        if (this._itemFilter != null) {
            this.delegate.writeObjectRef(id);
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonGeneratorDelegate, com.fasterxml.jackson.core.JsonGenerator
    public void writeTypeId(Object id) throws IOException {
        if (this._itemFilter != null) {
            this.delegate.writeTypeId(id);
        }
    }

    protected void _checkParentPath() throws IOException {
        _checkParentPath(true);
    }

    protected void _checkParentPath(boolean isMatch) throws IOException {
        if (isMatch) {
            this._matchCount++;
        }
        if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) {
            this._filterContext.writePath(this.delegate);
        } else if (this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
            this._filterContext.ensureFieldNameWritten(this.delegate);
        }
        if (isMatch && !this._allowMultipleMatches) {
            this._filterContext.skipParentChecks();
        }
    }

    protected void _checkPropertyParentPath() throws IOException {
        this._matchCount++;
        if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) {
            this._filterContext.writePath(this.delegate);
        } else if (this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
            this._filterContext.ensureFieldNameWritten(this.delegate);
        }
        if (!this._allowMultipleMatches) {
            this._filterContext.skipParentChecks();
        }
    }

    protected boolean _checkBinaryWrite() throws IOException {
        if (this._itemFilter == null) {
            return false;
        }
        if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
            return true;
        }
        if (this._itemFilter.includeBinary()) {
            _checkParentPath();
            return true;
        }
        return false;
    }

    protected boolean _checkRawValueWrite() throws IOException {
        if (this._itemFilter == null) {
            return false;
        }
        if (this._itemFilter == TokenFilter.INCLUDE_ALL) {
            return true;
        }
        if (this._itemFilter.includeRawValue()) {
            _checkParentPath();
            return true;
        }
        return false;
    }
}
