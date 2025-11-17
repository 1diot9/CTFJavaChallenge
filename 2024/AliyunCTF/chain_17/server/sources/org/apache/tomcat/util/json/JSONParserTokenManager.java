package org.apache.tomcat.util.json;

import java.io.IOException;
import java.io.PrintStream;
import org.apache.tomcat.util.codec.binary.BaseNCodec;
import org.springframework.asm.Opcodes;
import org.springframework.asm.TypeReference;
import org.springframework.beans.PropertyAccessor;
import org.springframework.boot.env.RandomValuePropertySourceEnvironmentPostProcessor;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/json/JSONParserTokenManager.class */
public class JSONParserTokenManager implements JSONParserConstants {
    public PrintStream debugStream;
    int curLexState;
    int defaultLexState;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;
    protected JavaCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    private final StringBuilder jjimage;
    private StringBuilder image;
    private int jjimageLen;
    private int lengthOfMatch;
    protected int curChar;
    static final long[] jjbitVec0 = {-2, -1, -1, -1};
    static final long[] jjbitVec2 = {0, 0, -1, -1};
    public static final String[] jjstrLiteralImages = {"", null, null, null, null, null, ",", "{", "}", ":", PropertyAccessor.PROPERTY_KEY_PREFIX, "]", null, null, null, null, null, null, null, null, null, null, "''", "\"\"", null, null, null, null, null};
    static final int[] jjnextStates = {25, 26, 28, 34, 17, 20, 27, 35, 29, 25, 28, 29, 6, 7, 9, 11, 12, 14, 1, 2, 18, 19, 21, 23, 32, 33};
    public static final String[] lexStateNames = {"DEFAULT"};
    public static final int[] jjnewLexState = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    static final long[] jjtoToken = {483364801};
    static final long[] jjtoSkip = {62};
    static final long[] jjtoSpecial = {0};
    static final long[] jjtoMore = {0};

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_0(int pos, long active0) {
        switch (pos) {
            case 0:
                if ((active0 & 917504) != 0) {
                    this.jjmatchedKind = 28;
                    return 15;
                }
                if ((active0 & 4194304) != 0) {
                    return 38;
                }
                if ((active0 & 8388608) != 0) {
                    return 39;
                }
                return -1;
            case 1:
                if ((active0 & 917504) != 0) {
                    this.jjmatchedKind = 28;
                    this.jjmatchedPos = 1;
                    return 15;
                }
                return -1;
            case 2:
                if ((active0 & 917504) != 0) {
                    this.jjmatchedKind = 28;
                    this.jjmatchedPos = 2;
                    return 15;
                }
                return -1;
            case 3:
                if ((active0 & 655360) != 0) {
                    return 15;
                }
                if ((active0 & 262144) != 0) {
                    this.jjmatchedKind = 28;
                    this.jjmatchedPos = 3;
                    return 15;
                }
                return -1;
            default:
                return -1;
        }
    }

    private final int jjStartNfa_0(int pos, long active0) {
        return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
    }

    private int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case 34:
                return jjMoveStringLiteralDfa1_0(8388608L);
            case 39:
                return jjMoveStringLiteralDfa1_0(4194304L);
            case 44:
                return jjStopAtPos(0, 6);
            case 58:
                return jjStopAtPos(0, 9);
            case 70:
            case Opcodes.FSUB /* 102 */:
                return jjMoveStringLiteralDfa1_0(262144L);
            case 78:
            case Opcodes.FDIV /* 110 */:
                return jjMoveStringLiteralDfa1_0(524288L);
            case Opcodes.BASTORE /* 84 */:
            case 116:
                return jjMoveStringLiteralDfa1_0(131072L);
            case 91:
                return jjStopAtPos(0, 10);
            case 93:
                return jjStopAtPos(0, 11);
            case 123:
                return jjStopAtPos(0, 7);
            case 125:
                return jjStopAtPos(0, 8);
            default:
                return jjMoveNfa_0(0, 0);
        }
    }

    private int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 34:
                    if ((active0 & 8388608) != 0) {
                        return jjStopAtPos(1, 23);
                    }
                    break;
                case 39:
                    if ((active0 & 4194304) != 0) {
                        return jjStopAtPos(1, 22);
                    }
                    break;
                case 65:
                case 97:
                    return jjMoveStringLiteralDfa2_0(active0, 262144L);
                case Opcodes.DASTORE /* 82 */:
                case Opcodes.FREM /* 114 */:
                    return jjMoveStringLiteralDfa2_0(active0, 131072L);
                case Opcodes.CASTORE /* 85 */:
                case Opcodes.LNEG /* 117 */:
                    return jjMoveStringLiteralDfa2_0(active0, 524288L);
            }
            return jjStartNfa_0(0, active0);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(0, active0);
            return 1;
        }
    }

    private int jjMoveStringLiteralDfa2_0(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_0(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
                case 108:
                    return jjMoveStringLiteralDfa3_0(active02, 786432L);
                case Opcodes.CASTORE /* 85 */:
                case Opcodes.LNEG /* 117 */:
                    return jjMoveStringLiteralDfa3_0(active02, 131072L);
                default:
                    return jjStartNfa_0(1, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(1, active02);
            return 2;
        }
    }

    private int jjMoveStringLiteralDfa3_0(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_0(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                case 101:
                    if ((active02 & 131072) != 0) {
                        return jjStartNfaWithStates_0(3, 17, 15);
                    }
                    break;
                case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
                case 108:
                    if ((active02 & 524288) != 0) {
                        return jjStartNfaWithStates_0(3, 19, 15);
                    }
                    break;
                case 83:
                case 115:
                    return jjMoveStringLiteralDfa4_0(active02, 262144L);
            }
            return jjStartNfa_0(2, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(2, active02);
            return 3;
        }
    }

    private int jjMoveStringLiteralDfa4_0(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_0(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                case 101:
                    if ((active02 & 262144) != 0) {
                        return jjStartNfaWithStates_0(4, 18, 15);
                    }
                    break;
            }
            return jjStartNfa_0(3, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(3, active02);
            return 4;
        }
    }

    private int jjStartNfaWithStates_0(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
            return jjMoveNfa_0(state, pos + 1);
        } catch (IOException e) {
            return pos + 1;
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Failed to find switch 'out' block (already processed)
        	at jadx.core.dex.visitors.regions.RegionMaker.calcSwitchOut(RegionMaker.java:923)
        	at jadx.core.dex.visitors.regions.RegionMaker.processSwitch(RegionMaker.java:797)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:157)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:91)
        	at jadx.core.dex.visitors.regions.RegionMaker.processLoop(RegionMaker.java:242)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:135)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:91)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:735)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:152)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:91)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeEndlessLoop(RegionMaker.java:411)
        	at jadx.core.dex.visitors.regions.RegionMaker.processLoop(RegionMaker.java:201)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:135)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:91)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    private int jjMoveNfa_0(int r9, int r10) {
        /*
            Method dump skipped, instructions count: 2219
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.json.JSONParserTokenManager.jjMoveNfa_0(int, int):int");
    }

    protected Token jjFillToken() {
        String im = jjstrLiteralImages[this.jjmatchedKind];
        String curTokenImage = im == null ? this.input_stream.GetImage() : im;
        int beginLine = this.input_stream.getBeginLine();
        int beginColumn = this.input_stream.getBeginColumn();
        int endLine = this.input_stream.getEndLine();
        int endColumn = this.input_stream.getEndColumn();
        Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }

    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0:
                return (jjbitVec2[i2] & l2) != 0;
            default:
                if ((jjbitVec0[i1] & l1) != 0) {
                    return true;
                }
                return false;
        }
    }

    public Token getNextToken() {
        do {
            try {
                this.curChar = this.input_stream.BeginToken();
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                int curPos = jjMoveStringLiteralDfa0_0();
                if (this.jjmatchedKind != Integer.MAX_VALUE) {
                    if (this.jjmatchedPos + 1 < curPos) {
                        this.input_stream.backup((curPos - this.jjmatchedPos) - 1);
                    }
                } else {
                    int error_line = this.input_stream.getEndLine();
                    int error_column = this.input_stream.getEndColumn();
                    String error_after = null;
                    boolean EOFSeen = false;
                    try {
                        this.input_stream.readChar();
                        this.input_stream.backup(1);
                    } catch (IOException e) {
                        EOFSeen = true;
                        error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
                        if (this.curChar == 10 || this.curChar == 13) {
                            error_line++;
                            error_column = 0;
                        } else {
                            error_column++;
                        }
                    }
                    if (!EOFSeen) {
                        this.input_stream.backup(1);
                        error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
                    }
                    throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
                }
            } catch (Exception e2) {
                this.jjmatchedKind = 0;
                this.jjmatchedPos = -1;
                Token matchedToken = jjFillToken();
                return matchedToken;
            }
        } while ((jjtoToken[this.jjmatchedKind >> 6] & (1 << (this.jjmatchedKind & 63))) == 0);
        Token matchedToken2 = jjFillToken();
        return matchedToken2;
    }

    void SkipLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            default:
                return;
        }
    }

    void MoreLexicalActions() {
        int i = this.jjimageLen;
        int i2 = this.jjmatchedPos + 1;
        this.lengthOfMatch = i2;
        this.jjimageLen = i + i2;
        switch (this.jjmatchedKind) {
            default:
                return;
        }
    }

    void TokenLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            default:
                return;
        }
    }

    private void jjCheckNAdd(int state) {
        if (this.jjrounds[state] != this.jjround) {
            int[] iArr = this.jjstateSet;
            int i = this.jjnewStateCnt;
            this.jjnewStateCnt = i + 1;
            iArr[i] = state;
            this.jjrounds[state] = this.jjround;
        }
    }

    private void jjAddStates(int start, int end) {
        int i;
        do {
            int[] iArr = this.jjstateSet;
            int i2 = this.jjnewStateCnt;
            this.jjnewStateCnt = i2 + 1;
            iArr[i2] = jjnextStates[start];
            i = start;
            start++;
        } while (i != end);
    }

    private void jjCheckNAddTwoStates(int state1, int state2) {
        jjCheckNAdd(state1);
        jjCheckNAdd(state2);
    }

    private void jjCheckNAddStates(int start, int end) {
        int i;
        do {
            jjCheckNAdd(jjnextStates[start]);
            i = start;
            start++;
        } while (i != end);
    }

    public JSONParserTokenManager(JavaCharStream stream) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[38];
        this.jjstateSet = new int[76];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        this.input_stream = stream;
    }

    public JSONParserTokenManager(JavaCharStream stream, int lexState) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[38];
        this.jjstateSet = new int[76];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        ReInit(stream);
        SwitchTo(lexState);
    }

    public void ReInit(JavaCharStream stream) {
        this.jjnewStateCnt = 0;
        this.jjmatchedPos = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        ReInitRounds();
    }

    private void ReInitRounds() {
        this.jjround = RandomValuePropertySourceEnvironmentPostProcessor.ORDER;
        int i = 38;
        while (true) {
            int i2 = i;
            i--;
            if (i2 > 0) {
                this.jjrounds[i] = Integer.MIN_VALUE;
            } else {
                return;
            }
        }
    }

    public void ReInit(JavaCharStream stream, int lexState) {
        ReInit(stream);
        SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 1 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
}
