package com.fasterxml.jackson.core.filter;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.filter.TokenFilter;
import com.fasterxml.jackson.core.util.JsonParserDelegate;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/filter/FilteringParserDelegate.class */
public class FilteringParserDelegate extends JsonParserDelegate {
    protected TokenFilter rootFilter;
    protected boolean _allowMultipleMatches;
    protected TokenFilter.Inclusion _inclusion;
    protected JsonToken _currToken;
    protected JsonToken _lastClearedToken;
    protected TokenFilterContext _headContext;
    protected TokenFilterContext _exposedContext;
    protected TokenFilter _itemFilter;
    protected int _matchCount;

    @Deprecated
    public FilteringParserDelegate(JsonParser p, TokenFilter f, boolean includePath, boolean allowMultipleMatches) {
        this(p, f, includePath ? TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH : TokenFilter.Inclusion.ONLY_INCLUDE_ALL, allowMultipleMatches);
    }

    public FilteringParserDelegate(JsonParser p, TokenFilter f, TokenFilter.Inclusion inclusion, boolean allowMultipleMatches) {
        super(p);
        this.rootFilter = f;
        this._itemFilter = f;
        this._headContext = TokenFilterContext.createRootContext(f);
        this._inclusion = inclusion;
        this._allowMultipleMatches = allowMultipleMatches;
    }

    public TokenFilter getFilter() {
        return this.rootFilter;
    }

    public int getMatchCount() {
        return this._matchCount;
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public JsonToken getCurrentToken() {
        return this._currToken;
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public JsonToken currentToken() {
        return this._currToken;
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    @Deprecated
    public final int getCurrentTokenId() {
        return currentTokenId();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public final int currentTokenId() {
        JsonToken t = this._currToken;
        if (t == null) {
            return 0;
        }
        return t.id();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public boolean hasCurrentToken() {
        return this._currToken != null;
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public boolean hasTokenId(int id) {
        JsonToken t = this._currToken;
        return t == null ? 0 == id : t.id() == id;
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public final boolean hasToken(JsonToken t) {
        return this._currToken == t;
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public boolean isExpectedStartArrayToken() {
        return this._currToken == JsonToken.START_ARRAY;
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public boolean isExpectedStartObjectToken() {
        return this._currToken == JsonToken.START_OBJECT;
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public JsonLocation getCurrentLocation() {
        return this.delegate.getCurrentLocation();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public JsonStreamContext getParsingContext() {
        return _filterContext();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public String getCurrentName() throws IOException {
        JsonStreamContext ctxt = _filterContext();
        if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
            JsonStreamContext parent = ctxt.getParent();
            if (parent == null) {
                return null;
            }
            return parent.getCurrentName();
        }
        return ctxt.getCurrentName();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public String currentName() throws IOException {
        JsonStreamContext ctxt = _filterContext();
        if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
            JsonStreamContext parent = ctxt.getParent();
            if (parent == null) {
                return null;
            }
            return parent.getCurrentName();
        }
        return ctxt.getCurrentName();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public void clearCurrentToken() {
        if (this._currToken != null) {
            this._lastClearedToken = this._currToken;
            this._currToken = null;
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public JsonToken getLastClearedToken() {
        return this._lastClearedToken;
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public void overrideCurrentName(String name) {
        throw new UnsupportedOperationException("Can not currently override name during filtering read");
    }

    /* JADX WARN: Code restructure failed: missing block: B:100:0x0247, code lost:            r5._headContext = r5._headContext.createChildObjectContext(r8, true);        r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:101:0x025a, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:103:0x025c, code lost:            if (r8 == null) goto L103;     */
    /* JADX WARN: Code restructure failed: missing block: B:105:0x0266, code lost:            if (r5._inclusion != com.fasterxml.jackson.core.filter.TokenFilter.Inclusion.INCLUDE_NON_NULL) goto L103;     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x0269, code lost:            r5._headContext = r5._headContext.createChildObjectContext(r8, true);        r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:107:0x027c, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:108:0x027d, code lost:            r5._headContext = r5._headContext.createChildObjectContext(r8, false);     */
    /* JADX WARN: Code restructure failed: missing block: B:109:0x0291, code lost:            if (r5._inclusion != com.fasterxml.jackson.core.filter.TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) goto L163;     */
    /* JADX WARN: Code restructure failed: missing block: B:110:0x0294, code lost:            r0 = _nextTokenWithBuffering(r5._headContext);     */
    /* JADX WARN: Code restructure failed: missing block: B:111:0x029e, code lost:            if (r0 == null) goto L163;     */
    /* JADX WARN: Code restructure failed: missing block: B:112:0x02a1, code lost:            r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:113:0x02a7, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:114:0x02a8, code lost:            r0 = r5._headContext.isStartHandled();        r0 = r5._headContext.getFilter();     */
    /* JADX WARN: Code restructure failed: missing block: B:115:0x02ba, code lost:            if (r0 == null) goto L117;     */
    /* JADX WARN: Code restructure failed: missing block: B:117:0x02c1, code lost:            if (r0 == com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL) goto L117;     */
    /* JADX WARN: Code restructure failed: missing block: B:119:0x02c9, code lost:            if (r0.id() != 4) goto L116;     */
    /* JADX WARN: Code restructure failed: missing block: B:120:0x02cc, code lost:            r0.filterFinishArray();     */
    /* JADX WARN: Code restructure failed: missing block: B:121:0x02d3, code lost:            r0.filterFinishObject();     */
    /* JADX WARN: Code restructure failed: missing block: B:122:0x02d7, code lost:            r5._headContext = r5._headContext.getParent();        r5._itemFilter = r5._headContext.getFilter();     */
    /* JADX WARN: Code restructure failed: missing block: B:123:0x02ef, code lost:            if (r0 == false) goto L163;     */
    /* JADX WARN: Code restructure failed: missing block: B:124:0x02f2, code lost:            r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:125:0x02f8, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:126:0x02fc, code lost:            r0 = r5.delegate.getCurrentName();        r0 = r5._headContext.setFieldName(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:127:0x0313, code lost:            if (r0 != com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL) goto L126;     */
    /* JADX WARN: Code restructure failed: missing block: B:128:0x0316, code lost:            r5._itemFilter = r0;        r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:129:0x0321, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:131:0x0323, code lost:            if (r0 != null) goto L129;     */
    /* JADX WARN: Code restructure failed: missing block: B:132:0x0326, code lost:            r5.delegate.nextToken();        r5.delegate.skipChildren();     */
    /* JADX WARN: Code restructure failed: missing block: B:133:0x0339, code lost:            r0 = r0.includeProperty(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:134:0x0341, code lost:            if (r0 != null) goto L132;     */
    /* JADX WARN: Code restructure failed: missing block: B:135:0x0344, code lost:            r5.delegate.nextToken();        r5.delegate.skipChildren();     */
    /* JADX WARN: Code restructure failed: missing block: B:136:0x0357, code lost:            r5._itemFilter = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:137:0x0360, code lost:            if (r0 != com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL) goto L141;     */
    /* JADX WARN: Code restructure failed: missing block: B:139:0x0367, code lost:            if (_verifyAllowedMatches() == false) goto L140;     */
    /* JADX WARN: Code restructure failed: missing block: B:141:0x0371, code lost:            if (r5._inclusion != com.fasterxml.jackson.core.filter.TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) goto L141;     */
    /* JADX WARN: Code restructure failed: missing block: B:142:0x0374, code lost:            r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:143:0x037a, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:144:0x037b, code lost:            r5.delegate.nextToken();        r5.delegate.skipChildren();     */
    /* JADX WARN: Code restructure failed: missing block: B:146:0x0392, code lost:            if (r5._inclusion == com.fasterxml.jackson.core.filter.TokenFilter.Inclusion.ONLY_INCLUDE_ALL) goto L163;     */
    /* JADX WARN: Code restructure failed: missing block: B:147:0x0395, code lost:            r0 = _nextTokenWithBuffering(r5._headContext);     */
    /* JADX WARN: Code restructure failed: missing block: B:148:0x039f, code lost:            if (r0 == null) goto L163;     */
    /* JADX WARN: Code restructure failed: missing block: B:149:0x03a2, code lost:            r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:150:0x03a8, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:151:0x03a9, code lost:            r0 = r5._itemFilter;     */
    /* JADX WARN: Code restructure failed: missing block: B:152:0x03b2, code lost:            if (r0 != com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL) goto L151;     */
    /* JADX WARN: Code restructure failed: missing block: B:153:0x03b5, code lost:            r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:154:0x03bb, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:156:0x03bd, code lost:            if (r0 == null) goto L163;     */
    /* JADX WARN: Code restructure failed: missing block: B:157:0x03c0, code lost:            r0 = r5._headContext.checkValue(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:158:0x03cd, code lost:            if (r0 == com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL) goto L159;     */
    /* JADX WARN: Code restructure failed: missing block: B:160:0x03d1, code lost:            if (r0 == null) goto L163;     */
    /* JADX WARN: Code restructure failed: missing block: B:162:0x03dc, code lost:            if (r0.includeValue(r5.delegate) == false) goto L163;     */
    /* JADX WARN: Code restructure failed: missing block: B:164:0x03e3, code lost:            if (_verifyAllowedMatches() == false) goto L163;     */
    /* JADX WARN: Code restructure failed: missing block: B:165:0x03e6, code lost:            r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:166:0x03ec, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x004a, code lost:            if (r6 != null) goto L20;     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x004d, code lost:            r0 = r6.nextTokenToRead();     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0053, code lost:            if (r0 == null) goto L24;     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0062, code lost:            if (r6 != r5._headContext) goto L40;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x00d3, code lost:            r6 = r5._headContext.findChildOf(r6);        r5._exposedContext = r6;     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00e2, code lost:            if (r6 != null) goto L168;     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x00eb, code lost:            throw _constructError("Unexpected problem: chain of filtered context broken");     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0065, code lost:            r5._exposedContext = null;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x006e, code lost:            if (r6.inArray() == false) goto L33;     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0071, code lost:            r0 = r5.delegate.getCurrentToken();        r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0085, code lost:            if (r5._currToken != com.fasterxml.jackson.core.JsonToken.END_ARRAY) goto L31;     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0088, code lost:            r5._headContext = r5._headContext.getParent();        r5._itemFilter = r5._headContext.getFilter();     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x009f, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00a0, code lost:            r0 = r5.delegate.currentToken();     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00ac, code lost:            if (r0 != com.fasterxml.jackson.core.JsonToken.END_OBJECT) goto L36;     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00af, code lost:            r5._headContext = r5._headContext.getParent();        r5._itemFilter = r5._headContext.getFilter();     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00c9, code lost:            if (r0 == com.fasterxml.jackson.core.JsonToken.FIELD_NAME) goto L45;     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00cc, code lost:            r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00d2, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0056, code lost:            r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x005c, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x00ef, code lost:            r0 = r5.delegate.nextToken();     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x00f8, code lost:            if (r0 != null) goto L49;     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x00fb, code lost:            r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0101, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0106, code lost:            switch(r0.id()) {            case 1: goto L80;            case 2: goto L109;            case 3: goto L51;            case 4: goto L109;            case 5: goto L122;            default: goto L147;        };     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0128, code lost:            r0 = r5._itemFilter;     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0131, code lost:            if (r0 != com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL) goto L55;     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0134, code lost:            r5._headContext = r5._headContext.createChildArrayContext(r0, true);        r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x0147, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x0149, code lost:            if (r0 != null) goto L58;     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x014c, code lost:            r5.delegate.skipChildren();     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x03f1, code lost:            return _nextToken2();     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x0157, code lost:            r8 = r5._headContext.checkValue(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x0161, code lost:            if (r8 != null) goto L61;     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x0164, code lost:            r5.delegate.skipChildren();     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x0173, code lost:            if (r8 == com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL) goto L64;     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x0176, code lost:            r8 = r8.filterStartArray();     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x017b, code lost:            r5._itemFilter = r8;     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x0184, code lost:            if (r8 != com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL) goto L68;     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x0187, code lost:            r5._headContext = r5._headContext.createChildArrayContext(r8, true);        r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x019a, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x019c, code lost:            if (r8 == null) goto L74;     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x01a6, code lost:            if (r5._inclusion != com.fasterxml.jackson.core.filter.TokenFilter.Inclusion.INCLUDE_NON_NULL) goto L74;     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x01a9, code lost:            r5._headContext = r5._headContext.createChildArrayContext(r8, true);        r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x01bc, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x01bd, code lost:            r5._headContext = r5._headContext.createChildArrayContext(r8, false);     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x01d1, code lost:            if (r5._inclusion != com.fasterxml.jackson.core.filter.TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) goto L163;     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x01d4, code lost:            r0 = _nextTokenWithBuffering(r5._headContext);     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x01de, code lost:            if (r0 == null) goto L163;     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x01e1, code lost:            r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x01e7, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x01e8, code lost:            r0 = r5._itemFilter;     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x01f1, code lost:            if (r0 != com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL) goto L84;     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x01f4, code lost:            r5._headContext = r5._headContext.createChildObjectContext(r0, true);        r5._currToken = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x0207, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x0209, code lost:            if (r0 != null) goto L87;     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x020c, code lost:            r5.delegate.skipChildren();     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x0217, code lost:            r8 = r5._headContext.checkValue(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x0221, code lost:            if (r8 != null) goto L90;     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x0224, code lost:            r5.delegate.skipChildren();     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x0233, code lost:            if (r8 == com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL) goto L93;     */
    /* JADX WARN: Code restructure failed: missing block: B:97:0x0236, code lost:            r8 = r8.filterStartObject();     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x023b, code lost:            r5._itemFilter = r8;     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x0244, code lost:            if (r8 != com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL) goto L97;     */
    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.fasterxml.jackson.core.JsonToken nextToken() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 1010
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.filter.FilteringParserDelegate.nextToken():com.fasterxml.jackson.core.JsonToken");
    }

    protected final JsonToken _nextToken2() throws IOException {
        JsonToken t;
        TokenFilter f;
        JsonToken t2;
        JsonToken t3;
        while (true) {
            JsonToken t4 = this.delegate.nextToken();
            if (t4 == null) {
                this._currToken = t4;
                return t4;
            }
            switch (t4.id()) {
                case 1:
                    TokenFilter f2 = this._itemFilter;
                    if (f2 == TokenFilter.INCLUDE_ALL) {
                        this._headContext = this._headContext.createChildObjectContext(f2, true);
                        this._currToken = t4;
                        return t4;
                    }
                    if (f2 == null) {
                        this.delegate.skipChildren();
                        break;
                    } else {
                        TokenFilter f3 = this._headContext.checkValue(f2);
                        if (f3 == null) {
                            this.delegate.skipChildren();
                            break;
                        } else {
                            if (f3 != TokenFilter.INCLUDE_ALL) {
                                f3 = f3.filterStartObject();
                            }
                            this._itemFilter = f3;
                            if (f3 == TokenFilter.INCLUDE_ALL) {
                                this._headContext = this._headContext.createChildObjectContext(f3, true);
                                this._currToken = t4;
                                return t4;
                            }
                            if (f3 != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                                this._headContext = this._headContext.createChildObjectContext(f3, true);
                                this._currToken = t4;
                                return t4;
                            }
                            this._headContext = this._headContext.createChildObjectContext(f3, false);
                            if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH && (t3 = _nextTokenWithBuffering(this._headContext)) != null) {
                                this._currToken = t3;
                                return t3;
                            }
                        }
                    }
                    break;
                case 2:
                    boolean returnEnd = this._headContext.isStartHandled();
                    TokenFilter f4 = this._headContext.getFilter();
                    if (f4 != null && f4 != TokenFilter.INCLUDE_ALL) {
                        boolean includeEmpty = f4.includeEmptyArray(this._headContext.hasCurrentName());
                        f4.filterFinishObject();
                        if (includeEmpty) {
                            return _nextBuffered(this._headContext);
                        }
                    }
                    this._headContext = this._headContext.getParent();
                    this._itemFilter = this._headContext.getFilter();
                    if (!returnEnd) {
                        break;
                    } else {
                        this._currToken = t4;
                        return t4;
                    }
                case 3:
                    TokenFilter f5 = this._itemFilter;
                    if (f5 == TokenFilter.INCLUDE_ALL) {
                        this._headContext = this._headContext.createChildArrayContext(f5, true);
                        this._currToken = t4;
                        return t4;
                    }
                    if (f5 == null) {
                        this.delegate.skipChildren();
                        break;
                    } else {
                        TokenFilter f6 = this._headContext.checkValue(f5);
                        if (f6 == null) {
                            this.delegate.skipChildren();
                            break;
                        } else {
                            if (f6 != TokenFilter.INCLUDE_ALL) {
                                f6 = f6.filterStartArray();
                            }
                            this._itemFilter = f6;
                            if (f6 == TokenFilter.INCLUDE_ALL) {
                                this._headContext = this._headContext.createChildArrayContext(f6, true);
                                this._currToken = t4;
                                return t4;
                            }
                            if (f6 != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                                this._headContext = this._headContext.createChildArrayContext(f6, true);
                                this._currToken = t4;
                                return t4;
                            }
                            this._headContext = this._headContext.createChildArrayContext(f6, false);
                            if (this._inclusion == TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH && (t = _nextTokenWithBuffering(this._headContext)) != null) {
                                this._currToken = t;
                                return t;
                            }
                        }
                    }
                    break;
                case 4:
                    boolean returnEnd2 = this._headContext.isStartHandled();
                    TokenFilter f7 = this._headContext.getFilter();
                    if (f7 != null && f7 != TokenFilter.INCLUDE_ALL) {
                        boolean includeEmpty2 = f7.includeEmptyArray(this._headContext.hasCurrentIndex());
                        f7.filterFinishArray();
                        if (includeEmpty2) {
                            return _nextBuffered(this._headContext);
                        }
                    }
                    this._headContext = this._headContext.getParent();
                    this._itemFilter = this._headContext.getFilter();
                    if (!returnEnd2) {
                        break;
                    } else {
                        this._currToken = t4;
                        return t4;
                    }
                case 5:
                    String name = this.delegate.getCurrentName();
                    TokenFilter f8 = this._headContext.setFieldName(name);
                    if (f8 == TokenFilter.INCLUDE_ALL) {
                        this._itemFilter = f8;
                        this._currToken = t4;
                        return t4;
                    }
                    if (f8 == null) {
                        this.delegate.nextToken();
                        this.delegate.skipChildren();
                        break;
                    } else {
                        TokenFilter f9 = f8.includeProperty(name);
                        if (f9 == null) {
                            this.delegate.nextToken();
                            this.delegate.skipChildren();
                            break;
                        } else {
                            this._itemFilter = f9;
                            if (f9 == TokenFilter.INCLUDE_ALL) {
                                if (_verifyAllowedMatches()) {
                                    if (this._inclusion != TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH) {
                                        break;
                                    } else {
                                        this._currToken = t4;
                                        return t4;
                                    }
                                } else {
                                    this.delegate.nextToken();
                                    this.delegate.skipChildren();
                                    break;
                                }
                            } else if (this._inclusion != TokenFilter.Inclusion.ONLY_INCLUDE_ALL && (t2 = _nextTokenWithBuffering(this._headContext)) != null) {
                                this._currToken = t2;
                                return t2;
                            }
                        }
                    }
                    break;
                default:
                    TokenFilter f10 = this._itemFilter;
                    if (f10 == TokenFilter.INCLUDE_ALL) {
                        this._currToken = t4;
                        return t4;
                    }
                    if (f10 != null && ((f = this._headContext.checkValue(f10)) == TokenFilter.INCLUDE_ALL || (f != null && f.includeValue(this.delegate)))) {
                        if (!_verifyAllowedMatches()) {
                            break;
                        } else {
                            this._currToken = t4;
                            return t4;
                        }
                    }
                    break;
            }
        }
    }

    protected final JsonToken _nextTokenWithBuffering(TokenFilterContext buffRoot) throws IOException {
        TokenFilter f;
        while (true) {
            JsonToken t = this.delegate.nextToken();
            if (t == null) {
                return t;
            }
            switch (t.id()) {
                case 1:
                    TokenFilter f2 = this._itemFilter;
                    if (f2 == TokenFilter.INCLUDE_ALL) {
                        this._headContext = this._headContext.createChildObjectContext(f2, true);
                        return t;
                    }
                    if (f2 == null) {
                        this.delegate.skipChildren();
                        break;
                    } else {
                        TokenFilter f3 = this._headContext.checkValue(f2);
                        if (f3 == null) {
                            this.delegate.skipChildren();
                            break;
                        } else {
                            if (f3 != TokenFilter.INCLUDE_ALL) {
                                f3 = f3.filterStartObject();
                            }
                            this._itemFilter = f3;
                            if (f3 == TokenFilter.INCLUDE_ALL) {
                                this._headContext = this._headContext.createChildObjectContext(f3, true);
                                return _nextBuffered(buffRoot);
                            }
                            if (f3 != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                                this._headContext = this._headContext.createChildArrayContext(f3, true);
                                return _nextBuffered(buffRoot);
                            }
                            this._headContext = this._headContext.createChildObjectContext(f3, false);
                            break;
                        }
                    }
                case 2:
                    TokenFilter f4 = this._headContext.getFilter();
                    if (f4 != null && f4 != TokenFilter.INCLUDE_ALL) {
                        boolean includeEmpty = f4.includeEmptyObject(this._headContext.hasCurrentName());
                        f4.filterFinishObject();
                        if (includeEmpty) {
                            this._headContext._currentName = this._headContext._parent == null ? null : this._headContext._parent._currentName;
                            this._headContext._needToHandleName = false;
                            return _nextBuffered(buffRoot);
                        }
                    }
                    boolean gotEnd = this._headContext == buffRoot;
                    boolean returnEnd = gotEnd && this._headContext.isStartHandled();
                    this._headContext = this._headContext.getParent();
                    this._itemFilter = this._headContext.getFilter();
                    if (returnEnd) {
                        return t;
                    }
                    if (!gotEnd) {
                        break;
                    } else {
                        return null;
                    }
                case 3:
                    TokenFilter f5 = this._headContext.checkValue(this._itemFilter);
                    if (f5 == null) {
                        this.delegate.skipChildren();
                        break;
                    } else {
                        if (f5 != TokenFilter.INCLUDE_ALL) {
                            f5 = f5.filterStartArray();
                        }
                        this._itemFilter = f5;
                        if (f5 == TokenFilter.INCLUDE_ALL) {
                            this._headContext = this._headContext.createChildArrayContext(f5, true);
                            return _nextBuffered(buffRoot);
                        }
                        if (f5 != null && this._inclusion == TokenFilter.Inclusion.INCLUDE_NON_NULL) {
                            this._headContext = this._headContext.createChildArrayContext(f5, true);
                            return _nextBuffered(buffRoot);
                        }
                        this._headContext = this._headContext.createChildArrayContext(f5, false);
                        break;
                    }
                case 4:
                    TokenFilter f6 = this._headContext.getFilter();
                    if (f6 != null && f6 != TokenFilter.INCLUDE_ALL) {
                        boolean includeEmpty2 = f6.includeEmptyArray(this._headContext.hasCurrentIndex());
                        f6.filterFinishArray();
                        if (includeEmpty2) {
                            return _nextBuffered(buffRoot);
                        }
                    }
                    boolean gotEnd2 = this._headContext == buffRoot;
                    boolean returnEnd2 = gotEnd2 && this._headContext.isStartHandled();
                    this._headContext = this._headContext.getParent();
                    this._itemFilter = this._headContext.getFilter();
                    if (returnEnd2) {
                        return t;
                    }
                    if (!gotEnd2) {
                        break;
                    } else {
                        return null;
                    }
                case 5:
                    String name = this.delegate.getCurrentName();
                    TokenFilter f7 = this._headContext.setFieldName(name);
                    if (f7 == TokenFilter.INCLUDE_ALL) {
                        this._itemFilter = f7;
                        return _nextBuffered(buffRoot);
                    }
                    if (f7 == null) {
                        this.delegate.nextToken();
                        this.delegate.skipChildren();
                        break;
                    } else {
                        TokenFilter f8 = f7.includeProperty(name);
                        if (f8 == null) {
                            this.delegate.nextToken();
                            this.delegate.skipChildren();
                            break;
                        } else {
                            this._itemFilter = f8;
                            if (f8 == TokenFilter.INCLUDE_ALL) {
                                if (_verifyAllowedMatches()) {
                                    return _nextBuffered(buffRoot);
                                }
                                this._itemFilter = this._headContext.setFieldName(name);
                                break;
                            } else {
                                continue;
                            }
                        }
                    }
                default:
                    TokenFilter f9 = this._itemFilter;
                    if (f9 == TokenFilter.INCLUDE_ALL) {
                        return _nextBuffered(buffRoot);
                    }
                    if (f9 != null && ((f = this._headContext.checkValue(f9)) == TokenFilter.INCLUDE_ALL || (f != null && f.includeValue(this.delegate)))) {
                        if (!_verifyAllowedMatches()) {
                            break;
                        } else {
                            return _nextBuffered(buffRoot);
                        }
                    }
                    break;
            }
        }
    }

    private JsonToken _nextBuffered(TokenFilterContext buffRoot) throws IOException {
        this._exposedContext = buffRoot;
        TokenFilterContext ctxt = buffRoot;
        JsonToken t = ctxt.nextTokenToRead();
        if (t != null) {
            return t;
        }
        while (ctxt != this._headContext) {
            ctxt = this._exposedContext.findChildOf(ctxt);
            this._exposedContext = ctxt;
            if (ctxt == null) {
                throw _constructError("Unexpected problem: chain of filtered context broken");
            }
            JsonToken t2 = this._exposedContext.nextTokenToRead();
            if (t2 != null) {
                return t2;
            }
        }
        throw _constructError("Internal error: failed to locate expected buffered tokens");
    }

    private final boolean _verifyAllowedMatches() throws IOException {
        if (this._matchCount == 0 || this._allowMultipleMatches) {
            this._matchCount++;
            return true;
        }
        return false;
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public JsonToken nextValue() throws IOException {
        JsonToken t = nextToken();
        if (t == JsonToken.FIELD_NAME) {
            t = nextToken();
        }
        return t;
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public JsonParser skipChildren() throws IOException {
        if (this._currToken != JsonToken.START_OBJECT && this._currToken != JsonToken.START_ARRAY) {
            return this;
        }
        int open = 1;
        while (true) {
            JsonToken t = nextToken();
            if (t == null) {
                return this;
            }
            if (t.isStructStart()) {
                open++;
            } else if (t.isStructEnd()) {
                open--;
                if (open == 0) {
                    return this;
                }
            } else {
                continue;
            }
        }
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public String getText() throws IOException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            return currentName();
        }
        return this.delegate.getText();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public boolean hasTextCharacters() {
        if (this._currToken == JsonToken.FIELD_NAME) {
            return false;
        }
        return this.delegate.hasTextCharacters();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public char[] getTextCharacters() throws IOException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            return currentName().toCharArray();
        }
        return this.delegate.getTextCharacters();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public int getTextLength() throws IOException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            return currentName().length();
        }
        return this.delegate.getTextLength();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public int getTextOffset() throws IOException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            return 0;
        }
        return this.delegate.getTextOffset();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public BigInteger getBigIntegerValue() throws IOException {
        return this.delegate.getBigIntegerValue();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public boolean getBooleanValue() throws IOException {
        return this.delegate.getBooleanValue();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public byte getByteValue() throws IOException {
        return this.delegate.getByteValue();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public short getShortValue() throws IOException {
        return this.delegate.getShortValue();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public BigDecimal getDecimalValue() throws IOException {
        return this.delegate.getDecimalValue();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public double getDoubleValue() throws IOException {
        return this.delegate.getDoubleValue();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public float getFloatValue() throws IOException {
        return this.delegate.getFloatValue();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public int getIntValue() throws IOException {
        return this.delegate.getIntValue();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public long getLongValue() throws IOException {
        return this.delegate.getLongValue();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public JsonParser.NumberType getNumberType() throws IOException {
        return this.delegate.getNumberType();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public Number getNumberValue() throws IOException {
        return this.delegate.getNumberValue();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public int getValueAsInt() throws IOException {
        return this.delegate.getValueAsInt();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public int getValueAsInt(int defaultValue) throws IOException {
        return this.delegate.getValueAsInt(defaultValue);
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public long getValueAsLong() throws IOException {
        return this.delegate.getValueAsLong();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public long getValueAsLong(long defaultValue) throws IOException {
        return this.delegate.getValueAsLong(defaultValue);
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public double getValueAsDouble() throws IOException {
        return this.delegate.getValueAsDouble();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public double getValueAsDouble(double defaultValue) throws IOException {
        return this.delegate.getValueAsDouble(defaultValue);
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public boolean getValueAsBoolean() throws IOException {
        return this.delegate.getValueAsBoolean();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public boolean getValueAsBoolean(boolean defaultValue) throws IOException {
        return this.delegate.getValueAsBoolean(defaultValue);
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public String getValueAsString() throws IOException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            return currentName();
        }
        return this.delegate.getValueAsString();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public String getValueAsString(String defaultValue) throws IOException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            return currentName();
        }
        return this.delegate.getValueAsString(defaultValue);
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public Object getEmbeddedObject() throws IOException {
        return this.delegate.getEmbeddedObject();
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
        return this.delegate.getBinaryValue(b64variant);
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
        return this.delegate.readBinaryValue(b64variant, out);
    }

    @Override // com.fasterxml.jackson.core.util.JsonParserDelegate, com.fasterxml.jackson.core.JsonParser
    public JsonLocation getTokenLocation() {
        return this.delegate.getTokenLocation();
    }

    protected JsonStreamContext _filterContext() {
        if (this._exposedContext != null) {
            return this._exposedContext;
        }
        return this._headContext;
    }
}
