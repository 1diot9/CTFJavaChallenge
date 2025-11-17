package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.io.MergedStream;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/DataFormatReaders.class */
public class DataFormatReaders {
    public static final int DEFAULT_MAX_INPUT_LOOKAHEAD = 64;
    protected final ObjectReader[] _readers;
    protected final MatchStrength _optimalMatch;
    protected final MatchStrength _minimalMatch;
    protected final int _maxInputLookahead;

    public DataFormatReaders(ObjectReader... detectors) {
        this(detectors, MatchStrength.SOLID_MATCH, MatchStrength.WEAK_MATCH, 64);
    }

    public DataFormatReaders(Collection<ObjectReader> detectors) {
        this((ObjectReader[]) detectors.toArray(new ObjectReader[detectors.size()]));
    }

    private DataFormatReaders(ObjectReader[] detectors, MatchStrength optMatch, MatchStrength minMatch, int maxInputLookahead) {
        this._readers = detectors;
        this._optimalMatch = optMatch;
        this._minimalMatch = minMatch;
        this._maxInputLookahead = maxInputLookahead;
    }

    public DataFormatReaders withOptimalMatch(MatchStrength optMatch) {
        if (optMatch == this._optimalMatch) {
            return this;
        }
        return new DataFormatReaders(this._readers, optMatch, this._minimalMatch, this._maxInputLookahead);
    }

    public DataFormatReaders withMinimalMatch(MatchStrength minMatch) {
        if (minMatch == this._minimalMatch) {
            return this;
        }
        return new DataFormatReaders(this._readers, this._optimalMatch, minMatch, this._maxInputLookahead);
    }

    public DataFormatReaders with(ObjectReader[] readers) {
        return new DataFormatReaders(readers, this._optimalMatch, this._minimalMatch, this._maxInputLookahead);
    }

    public DataFormatReaders withMaxInputLookahead(int lookaheadBytes) {
        if (lookaheadBytes == this._maxInputLookahead) {
            return this;
        }
        return new DataFormatReaders(this._readers, this._optimalMatch, this._minimalMatch, lookaheadBytes);
    }

    public DataFormatReaders with(DeserializationConfig config) {
        int len = this._readers.length;
        ObjectReader[] r = new ObjectReader[len];
        for (int i = 0; i < len; i++) {
            r[i] = this._readers[i].with(config);
        }
        return new DataFormatReaders(r, this._optimalMatch, this._minimalMatch, this._maxInputLookahead);
    }

    public DataFormatReaders withType(JavaType type) {
        int len = this._readers.length;
        ObjectReader[] r = new ObjectReader[len];
        for (int i = 0; i < len; i++) {
            r[i] = this._readers[i].forType(type);
        }
        return new DataFormatReaders(r, this._optimalMatch, this._minimalMatch, this._maxInputLookahead);
    }

    public Match findFormat(InputStream in) throws IOException {
        return _findFormat(new AccessorForReader(in, new byte[this._maxInputLookahead]));
    }

    public Match findFormat(byte[] fullInputData) throws IOException {
        return _findFormat(new AccessorForReader(fullInputData));
    }

    public Match findFormat(byte[] fullInputData, int offset, int len) throws IOException {
        return _findFormat(new AccessorForReader(fullInputData, offset, len));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        int len = this._readers.length;
        if (len > 0) {
            sb.append(this._readers[0].getFactory().getFormatName());
            for (int i = 1; i < len; i++) {
                sb.append(", ");
                sb.append(this._readers[i].getFactory().getFormatName());
            }
        }
        sb.append(']');
        return sb.toString();
    }

    private Match _findFormat(AccessorForReader acc) throws IOException {
        ObjectReader bestMatch = null;
        MatchStrength bestMatchStrength = null;
        for (ObjectReader f : this._readers) {
            acc.reset();
            MatchStrength strength = f.getFactory().hasFormat(acc);
            if (strength != null && strength.ordinal() >= this._minimalMatch.ordinal() && (bestMatch == null || bestMatchStrength.ordinal() < strength.ordinal())) {
                bestMatch = f;
                bestMatchStrength = strength;
                if (strength.ordinal() >= this._optimalMatch.ordinal()) {
                    break;
                }
            }
        }
        return acc.createMatcher(bestMatch, bestMatchStrength);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/DataFormatReaders$AccessorForReader.class */
    public static class AccessorForReader extends InputAccessor.Std {
        public AccessorForReader(InputStream in, byte[] buffer) {
            super(in, buffer);
        }

        public AccessorForReader(byte[] inputDocument) {
            super(inputDocument);
        }

        public AccessorForReader(byte[] inputDocument, int start, int len) {
            super(inputDocument, start, len);
        }

        public Match createMatcher(ObjectReader match, MatchStrength matchStrength) {
            return new Match(this._in, this._buffer, this._bufferedStart, this._bufferedEnd - this._bufferedStart, match, matchStrength);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/DataFormatReaders$Match.class */
    public static class Match {
        protected final InputStream _originalStream;
        protected final byte[] _bufferedData;
        protected final int _bufferedStart;
        protected final int _bufferedLength;
        protected final ObjectReader _match;
        protected final MatchStrength _matchStrength;

        protected Match(InputStream in, byte[] buffered, int bufferedStart, int bufferedLength, ObjectReader match, MatchStrength strength) {
            this._originalStream = in;
            this._bufferedData = buffered;
            this._bufferedStart = bufferedStart;
            this._bufferedLength = bufferedLength;
            this._match = match;
            this._matchStrength = strength;
        }

        public boolean hasMatch() {
            return this._match != null;
        }

        public MatchStrength getMatchStrength() {
            return this._matchStrength == null ? MatchStrength.INCONCLUSIVE : this._matchStrength;
        }

        public ObjectReader getReader() {
            return this._match;
        }

        public String getMatchedFormatName() {
            return this._match.getFactory().getFormatName();
        }

        public JsonParser createParserWithMatch() throws IOException {
            if (this._match == null) {
                return null;
            }
            JsonFactory jf = this._match.getFactory();
            if (this._originalStream == null) {
                return jf.createParser(this._bufferedData, this._bufferedStart, this._bufferedLength);
            }
            return jf.createParser(getDataStream());
        }

        public InputStream getDataStream() {
            if (this._originalStream == null) {
                return new ByteArrayInputStream(this._bufferedData, this._bufferedStart, this._bufferedLength);
            }
            return new MergedStream(null, this._originalStream, this._bufferedData, this._bufferedStart, this._bufferedLength);
        }
    }
}
