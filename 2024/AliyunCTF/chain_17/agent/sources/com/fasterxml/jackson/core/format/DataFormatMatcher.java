package com.fasterxml.jackson.core.format;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.io.MergedStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/format/DataFormatMatcher.class */
public class DataFormatMatcher {
    protected final InputStream _originalStream;
    protected final byte[] _bufferedData;
    protected final int _bufferedStart;
    protected final int _bufferedLength;
    protected final JsonFactory _match;
    protected final MatchStrength _matchStrength;

    /* JADX INFO: Access modifiers changed from: protected */
    public DataFormatMatcher(InputStream in, byte[] buffered, int bufferedStart, int bufferedLength, JsonFactory match, MatchStrength strength) {
        this._originalStream = in;
        this._bufferedData = buffered;
        this._bufferedStart = bufferedStart;
        this._bufferedLength = bufferedLength;
        this._match = match;
        this._matchStrength = strength;
        if ((bufferedStart | bufferedLength) < 0 || bufferedStart + bufferedLength > buffered.length) {
            throw new IllegalArgumentException(String.format("Illegal start/length (%d/%d) wrt input array of %d bytes", Integer.valueOf(bufferedStart), Integer.valueOf(bufferedLength), Integer.valueOf(buffered.length)));
        }
    }

    public boolean hasMatch() {
        return this._match != null;
    }

    public MatchStrength getMatchStrength() {
        return this._matchStrength == null ? MatchStrength.INCONCLUSIVE : this._matchStrength;
    }

    public JsonFactory getMatch() {
        return this._match;
    }

    public String getMatchedFormatName() {
        if (hasMatch()) {
            return getMatch().getFormatName();
        }
        return null;
    }

    public JsonParser createParserWithMatch() throws IOException {
        if (this._match == null) {
            return null;
        }
        if (this._originalStream == null) {
            return this._match.createParser(this._bufferedData, this._bufferedStart, this._bufferedLength);
        }
        return this._match.createParser(getDataStream());
    }

    public InputStream getDataStream() {
        if (this._originalStream == null) {
            return new ByteArrayInputStream(this._bufferedData, this._bufferedStart, this._bufferedLength);
        }
        return new MergedStream(null, this._originalStream, this._bufferedData, this._bufferedStart, this._bufferedLength);
    }
}
