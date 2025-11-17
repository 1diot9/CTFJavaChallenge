package org.h2.command;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.JoranConstants;
import cn.hutool.core.text.StrPool;
import org.h2.engine.CastDataProvider;
import org.h2.engine.Constants;
import org.h2.message.DbException;
import org.h2.table.Column;
import org.h2.util.StringUtils;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueInteger;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;
import org.slf4j.Marker;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Token.class */
public abstract class Token implements Cloneable {
    static final int PARAMETER = 92;
    static final int END_OF_INPUT = 93;
    static final int LITERAL = 94;
    static final int EQUAL = 95;
    static final int BIGGER_EQUAL = 96;
    static final int BIGGER = 97;
    static final int SMALLER = 98;
    static final int SMALLER_EQUAL = 99;
    static final int NOT_EQUAL = 100;
    static final int AT = 101;
    static final int MINUS_SIGN = 102;
    static final int PLUS_SIGN = 103;
    static final int CONCATENATION = 104;
    static final int OPEN_PAREN = 105;
    static final int CLOSE_PAREN = 106;
    static final int SPATIAL_INTERSECTS = 107;
    static final int ASTERISK = 108;
    static final int COMMA = 109;
    static final int DOT = 110;
    static final int OPEN_BRACE = 111;
    static final int CLOSE_BRACE = 112;
    static final int SLASH = 113;
    static final int PERCENT = 114;
    static final int SEMICOLON = 115;
    static final int COLON = 116;
    static final int OPEN_BRACKET = 117;
    static final int CLOSE_BRACKET = 118;
    static final int TILDE = 119;
    static final int COLON_COLON = 120;
    static final int COLON_EQ = 121;
    static final int NOT_TILDE = 122;
    static final String[] TOKENS = {null, null, null, "ALL", "AND", "ANY", "ARRAY", "AS", "ASYMMETRIC", "AUTHORIZATION", "BETWEEN", "CASE", "CAST", "CHECK", "CONSTRAINT", "CROSS", "CURRENT_CATALOG", "CURRENT_DATE", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_SCHEMA", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "DAY", "DEFAULT", "DISTINCT", "ELSE", "END", "EXCEPT", "EXISTS", "FALSE", "FETCH", "FOR", "FOREIGN", "FROM", "FULL", "GROUP", "HAVING", "HOUR", "IF", "IN", "INNER", "INTERSECT", "INTERVAL", "IS", "JOIN", "KEY", "LEFT", "LIKE", "LIMIT", "LOCALTIME", "LOCALTIMESTAMP", "MINUS", "MINUTE", "MONTH", "NATURAL", "NOT", JoranConstants.NULL, "OFFSET", "ON", "OR", "ORDER", "PRIMARY", "QUALIFY", "RIGHT", "ROW", "ROWNUM", "SECOND", "SELECT", "SESSION_USER", "SET", "SOME", "SYMMETRIC", "SYSTEM_USER", "TABLE", "TO", Constants.CLUSTERING_ENABLED, "UESCAPE", "UNION", "UNIQUE", "UNKNOWN", "USER", "USING", "VALUE", "VALUES", "WHEN", "WHERE", "WINDOW", "WITH", "YEAR", Column.ROWID, CoreConstants.NA, null, null, "=", ">=", ">", "<", "<=", "<>", StrPool.AT, "-", Marker.ANY_NON_NULL_MARKER, "||", "(", ")", "&&", "*", ",", ".", StrPool.DELIM_START, "}", "/", QuickTargetSourceCreator.PREFIX_THREAD_LOCAL, ";", ":", "[", "]", Constants.SERVER_PROPERTIES_DIR, "::", ":=", "!~"};
    private int start;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract int tokenType();

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Token$IdentifierToken.class */
    static class IdentifierToken extends Token {
        private String identifier;
        private final boolean quoted;
        private boolean unicode;

        @Override // org.h2.command.Token
        /* renamed from: clone */
        protected /* bridge */ /* synthetic */ Object mo1443clone() throws CloneNotSupportedException {
            return super.mo1443clone();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public IdentifierToken(int i, String str, boolean z, boolean z2) {
            super(i);
            this.identifier = str;
            this.quoted = z;
            this.unicode = z2;
        }

        @Override // org.h2.command.Token
        int tokenType() {
            return 2;
        }

        @Override // org.h2.command.Token
        String asIdentifier() {
            return this.identifier;
        }

        @Override // org.h2.command.Token
        boolean isQuoted() {
            return this.quoted;
        }

        @Override // org.h2.command.Token
        boolean needsUnicodeConversion() {
            return this.unicode;
        }

        @Override // org.h2.command.Token
        void convertUnicode(int i) {
            if (this.unicode) {
                this.identifier = StringUtils.decodeUnicodeStringSQL(this.identifier, i);
                this.unicode = false;
                return;
            }
            throw DbException.getInternalError();
        }

        public String toString() {
            return this.quoted ? StringUtils.quoteIdentifier(this.identifier) : this.identifier;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Token$KeywordToken.class */
    static final class KeywordToken extends Token {
        private final int type;

        @Override // org.h2.command.Token
        /* renamed from: clone */
        protected /* bridge */ /* synthetic */ Object mo1443clone() throws CloneNotSupportedException {
            return super.mo1443clone();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public KeywordToken(int i, int i2) {
            super(i);
            this.type = i2;
        }

        @Override // org.h2.command.Token
        int tokenType() {
            return this.type;
        }

        @Override // org.h2.command.Token
        String asIdentifier() {
            return TOKENS[this.type];
        }

        public String toString() {
            return TOKENS[this.type];
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Token$KeywordOrIdentifierToken.class */
    static final class KeywordOrIdentifierToken extends Token {
        private final int type;
        private final String identifier;

        @Override // org.h2.command.Token
        /* renamed from: clone */
        protected /* bridge */ /* synthetic */ Object mo1443clone() throws CloneNotSupportedException {
            return super.mo1443clone();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public KeywordOrIdentifierToken(int i, int i2, String str) {
            super(i);
            this.type = i2;
            this.identifier = str;
        }

        @Override // org.h2.command.Token
        int tokenType() {
            return this.type;
        }

        @Override // org.h2.command.Token
        String asIdentifier() {
            return this.identifier;
        }

        public String toString() {
            return this.identifier;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Token$LiteralToken.class */
    static abstract class LiteralToken extends Token {
        Value value;

        @Override // org.h2.command.Token
        /* renamed from: clone */
        protected /* bridge */ /* synthetic */ Object mo1443clone() throws CloneNotSupportedException {
            return super.mo1443clone();
        }

        LiteralToken(int i) {
            super(i);
        }

        @Override // org.h2.command.Token
        final int tokenType() {
            return 94;
        }

        public final String toString() {
            return value(null).getTraceSQL();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Token$BinaryStringToken.class */
    static final class BinaryStringToken extends LiteralToken {
        private final byte[] string;

        /* JADX INFO: Access modifiers changed from: package-private */
        public BinaryStringToken(int i, byte[] bArr) {
            super(i);
            this.string = bArr;
        }

        @Override // org.h2.command.Token
        Value value(CastDataProvider castDataProvider) {
            if (this.value == null) {
                this.value = ValueVarbinary.getNoCopy(this.string);
            }
            return this.value;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Token$CharacterStringToken.class */
    static final class CharacterStringToken extends LiteralToken {
        String string;
        private boolean unicode;

        /* JADX INFO: Access modifiers changed from: package-private */
        public CharacterStringToken(int i, String str, boolean z) {
            super(i);
            this.string = str;
            this.unicode = z;
        }

        @Override // org.h2.command.Token
        Value value(CastDataProvider castDataProvider) {
            if (this.value == null) {
                this.value = ValueVarchar.get(this.string, castDataProvider);
            }
            return this.value;
        }

        @Override // org.h2.command.Token
        boolean needsUnicodeConversion() {
            return this.unicode;
        }

        @Override // org.h2.command.Token
        void convertUnicode(int i) {
            if (this.unicode) {
                this.string = StringUtils.decodeUnicodeStringSQL(this.string, i);
                this.unicode = false;
                return;
            }
            throw DbException.getInternalError();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Token$IntegerToken.class */
    static final class IntegerToken extends LiteralToken {
        private final int number;

        /* JADX INFO: Access modifiers changed from: package-private */
        public IntegerToken(int i, int i2) {
            super(i);
            this.number = i2;
        }

        @Override // org.h2.command.Token
        Value value(CastDataProvider castDataProvider) {
            if (this.value == null) {
                this.value = ValueInteger.get(this.number);
            }
            return this.value;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Token$BigintToken.class */
    static final class BigintToken extends LiteralToken {
        private final long number;

        /* JADX INFO: Access modifiers changed from: package-private */
        public BigintToken(int i, long j) {
            super(i);
            this.number = j;
        }

        @Override // org.h2.command.Token
        Value value(CastDataProvider castDataProvider) {
            if (this.value == null) {
                this.value = ValueBigint.get(this.number);
            }
            return this.value;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Token$ValueToken.class */
    static final class ValueToken extends LiteralToken {
        /* JADX INFO: Access modifiers changed from: package-private */
        public ValueToken(int i, Value value) {
            super(i);
            this.value = value;
        }

        @Override // org.h2.command.Token
        Value value(CastDataProvider castDataProvider) {
            return this.value;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Token$ParameterToken.class */
    static final class ParameterToken extends Token {
        int index;

        @Override // org.h2.command.Token
        /* renamed from: clone */
        protected /* bridge */ /* synthetic */ Object mo1443clone() throws CloneNotSupportedException {
            return super.mo1443clone();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ParameterToken(int i, int i2) {
            super(i);
            this.index = i2;
        }

        @Override // org.h2.command.Token
        int tokenType() {
            return 92;
        }

        @Override // org.h2.command.Token
        String asIdentifier() {
            return CoreConstants.NA;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public int index() {
            return this.index;
        }

        public String toString() {
            return this.index == 0 ? CoreConstants.NA : CoreConstants.NA + this.index;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Token$EndOfInputToken.class */
    static final class EndOfInputToken extends Token {
        @Override // org.h2.command.Token
        /* renamed from: clone */
        protected /* bridge */ /* synthetic */ Object mo1443clone() throws CloneNotSupportedException {
            return super.mo1443clone();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public EndOfInputToken(int i) {
            super(i);
        }

        @Override // org.h2.command.Token
        int tokenType() {
            return 93;
        }
    }

    Token(int i) {
        this.start = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int start() {
        return this.start;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setStart(int i) {
        this.start = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void subtractFromStart(int i) {
        this.start -= i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String asIdentifier() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isQuoted() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Value value(CastDataProvider castDataProvider) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean needsUnicodeConversion() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void convertUnicode(int i) {
        throw DbException.getInternalError();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // 
    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public Token mo1443clone() {
        try {
            return (Token) super.clone();
        } catch (CloneNotSupportedException e) {
            throw DbException.getInternalError();
        }
    }
}
