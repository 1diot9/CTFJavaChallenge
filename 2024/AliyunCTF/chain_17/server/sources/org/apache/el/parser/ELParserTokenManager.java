package org.apache.el.parser;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.util.FileSize;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import org.apache.tomcat.util.codec.binary.BaseNCodec;
import org.jooq.types.UInteger;
import org.slf4j.Marker;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;
import org.springframework.asm.Opcodes;
import org.springframework.asm.TypeReference;
import org.springframework.beans.PropertyAccessor;
import org.springframework.boot.env.RandomValuePropertySourceEnvironmentPostProcessor;
import org.springframework.context.expression.StandardBeanExpressionResolver;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/ELParserTokenManager.class */
public class ELParserTokenManager implements ELParserConstants {
    Deque<Integer> deque;
    public PrintStream debugStream;
    int curLexState;
    int defaultLexState;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;
    protected SimpleCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    private final StringBuilder jjimage;
    private StringBuilder image;
    private int jjimageLen;
    private int lengthOfMatch;
    protected int curChar;
    static final long[] jjbitVec0 = {-2, -1, -1, -1};
    static final long[] jjbitVec2 = {0, 0, -1, -1};
    static final long[] jjbitVec3 = {2301339413881290750L, -16384, UInteger.MAX_VALUE, 432345564227567616L};
    static final long[] jjbitVec4 = {0, 0, 0, -36028797027352577L};
    static final long[] jjbitVec5 = {0, -1, -1, -1};
    static final long[] jjbitVec6 = {-1, -1, 65535, 0};
    static final long[] jjbitVec7 = {-1, -1, 0, 0};
    static final long[] jjbitVec8 = {70368744177663L, 0, 0, 0};
    public static final String[] jjstrLiteralImages = {"", null, "${", StandardBeanExpressionResolver.DEFAULT_EXPRESSION_PREFIX, null, null, null, null, "{", "}", null, null, null, null, "true", "false", "null", ".", "(", ")", PropertyAccessor.PROPERTY_KEY_PREFIX, "]", ":", ";", ",", ">", "gt", "<", "lt", ">=", "ge", "<=", "le", "==", "eq", "!=", "ne", "!", "not", "&&", "and", "||", "or", "empty", "instanceof", "*", Marker.ANY_NON_NULL_MARKER, "-", CoreConstants.NA, "/", "div", QuickTargetSourceCreator.PREFIX_THREAD_LOCAL, "mod", "+=", "=", "->", null, null, null, null, null, null};
    static final int[] jjnextStates = {0, 1, 3, 4, 2, 0, 1, 4, 2, 0, 1, 4, 5, 2, 0, 1, 2, 6, 16, 17, 18, 23, 24, 11, 12, 14, 6, 7, 9, 3, 4, 21, 22, 25, 26};
    public static final String[] lexStateNames = {"DEFAULT", "IN_EXPRESSION", "IN_SET_OR_MAP"};
    public static final int[] jjnewLexState = {-1, -1, 1, 1, -1, -1, -1, -1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    static final long[] jjtoToken = {2594073385365401359L};
    static final long[] jjtoSkip = {240};
    static final long[] jjtoSpecial = {0};
    static final long[] jjtoMore = {0};

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_0(int pos, long active0) {
        switch (pos) {
            case 0:
                if ((active0 & 12) != 0) {
                    this.jjmatchedKind = 1;
                    return 5;
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
            case 35:
                return jjMoveStringLiteralDfa1_0(8L);
            case 36:
                return jjMoveStringLiteralDfa1_0(4L);
            default:
                return jjMoveNfa_0(7, 0);
        }
    }

    private int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 123:
                    if ((active0 & 4) != 0) {
                        return jjStopAtPos(1, 2);
                    }
                    if ((active0 & 8) != 0) {
                        return jjStopAtPos(1, 3);
                    }
                    break;
            }
            return jjStartNfa_0(0, active0);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(0, active0);
            return 1;
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
            Method dump skipped, instructions count: 1005
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.el.parser.ELParserTokenManager.jjMoveNfa_0(int, int):int");
    }

    private final int jjStopStringLiteralDfa_2(int pos, long active0) {
        switch (pos) {
            case 0:
                if ((active0 & 131072) != 0) {
                    return 1;
                }
                if ((active0 & 5661751853039616L) != 0) {
                    this.jjmatchedKind = 56;
                    return 30;
                }
                return -1;
            case 1:
                if ((active0 & 4489650110464L) != 0) {
                    return 30;
                }
                if ((active0 & 5657262202929152L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 1;
                    return 30;
                }
                return -1;
            case 2:
                if ((active0 & 5630873923747840L) != 0) {
                    return 30;
                }
                if ((active0 & 26388279181312L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 2;
                    return 30;
                }
                return -1;
            case 3:
                if ((active0 & 81920) != 0) {
                    return 30;
                }
                if ((active0 & 26388279099392L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 3;
                    return 30;
                }
                return -1;
            case 4:
                if ((active0 & 8796093054976L) != 0) {
                    return 30;
                }
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 4;
                    return 30;
                }
                return -1;
            case 5:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 5;
                    return 30;
                }
                return -1;
            case 6:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 6;
                    return 30;
                }
                return -1;
            case 7:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 7;
                    return 30;
                }
                return -1;
            case 8:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 8;
                    return 30;
                }
                return -1;
            default:
                return -1;
        }
    }

    private final int jjStartNfa_2(int pos, long active0) {
        return jjMoveNfa_2(jjStopStringLiteralDfa_2(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_2() {
        switch (this.curChar) {
            case 33:
                this.jjmatchedKind = 37;
                return jjMoveStringLiteralDfa1_2(34359738368L);
            case 34:
            case 35:
            case 36:
            case 39:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
            case 70:
            case TypeReference.CAST /* 71 */:
            case 72:
            case 73:
            case 74:
            case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT /* 75 */:
            case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
            case 77:
            case 78:
            case Opcodes.IASTORE /* 79 */:
            case 80:
            case Opcodes.FASTORE /* 81 */:
            case Opcodes.DASTORE /* 82 */:
            case 83:
            case Opcodes.BASTORE /* 84 */:
            case Opcodes.CASTORE /* 85 */:
            case Opcodes.SASTORE /* 86 */:
            case Opcodes.POP /* 87 */:
            case 88:
            case Opcodes.DUP /* 89 */:
            case 90:
            case 92:
            case Opcodes.DUP2_X2 /* 94 */:
            case Opcodes.SWAP /* 95 */:
            case 96:
            case Opcodes.FADD /* 98 */:
            case 99:
            case 104:
            case Opcodes.FMUL /* 106 */:
            case Opcodes.DMUL /* 107 */:
            case 112:
            case Opcodes.LREM /* 113 */:
            case Opcodes.FREM /* 114 */:
            case 115:
            case Opcodes.LNEG /* 117 */:
            case Opcodes.FNEG /* 118 */:
            case Opcodes.DNEG /* 119 */:
            case 120:
            case Opcodes.LSHL /* 121 */:
            case 122:
            default:
                return jjMoveNfa_2(0, 0);
            case 37:
                return jjStopAtPos(0, 51);
            case 38:
                return jjMoveStringLiteralDfa1_2(549755813888L);
            case 40:
                return jjStopAtPos(0, 18);
            case 41:
                return jjStopAtPos(0, 19);
            case 42:
                return jjStopAtPos(0, 45);
            case ELParserConstants.EMPTY /* 43 */:
                this.jjmatchedKind = 46;
                return jjMoveStringLiteralDfa1_2(9007199254740992L);
            case 44:
                return jjStopAtPos(0, 24);
            case 45:
                this.jjmatchedKind = 47;
                return jjMoveStringLiteralDfa1_2(36028797018963968L);
            case 46:
                return jjStartNfaWithStates_2(0, 17, 1);
            case 47:
                return jjStopAtPos(0, 49);
            case 58:
                return jjStopAtPos(0, 22);
            case 59:
                return jjStopAtPos(0, 23);
            case 60:
                this.jjmatchedKind = 27;
                return jjMoveStringLiteralDfa1_2(2147483648L);
            case 61:
                this.jjmatchedKind = 54;
                return jjMoveStringLiteralDfa1_2(8589934592L);
            case Opcodes.V18 /* 62 */:
                this.jjmatchedKind = 25;
                return jjMoveStringLiteralDfa1_2(536870912L);
            case 63:
                return jjStopAtPos(0, 48);
            case 91:
                return jjStopAtPos(0, 20);
            case 93:
                return jjStopAtPos(0, 21);
            case 97:
                return jjMoveStringLiteralDfa1_2(1099511627776L);
            case 100:
                return jjMoveStringLiteralDfa1_2(1125899906842624L);
            case 101:
                return jjMoveStringLiteralDfa1_2(8813272891392L);
            case Opcodes.FSUB /* 102 */:
                return jjMoveStringLiteralDfa1_2(32768L);
            case Opcodes.DSUB /* 103 */:
                return jjMoveStringLiteralDfa1_2(1140850688L);
            case Opcodes.LMUL /* 105 */:
                return jjMoveStringLiteralDfa1_2(17592186044416L);
            case 108:
                return jjMoveStringLiteralDfa1_2(4563402752L);
            case Opcodes.LDIV /* 109 */:
                return jjMoveStringLiteralDfa1_2(4503599627370496L);
            case Opcodes.FDIV /* 110 */:
                return jjMoveStringLiteralDfa1_2(343597449216L);
            case Opcodes.DDIV /* 111 */:
                return jjMoveStringLiteralDfa1_2(4398046511104L);
            case 116:
                return jjMoveStringLiteralDfa1_2(16384L);
            case 123:
                return jjStopAtPos(0, 8);
            case 124:
                return jjMoveStringLiteralDfa1_2(2199023255552L);
            case 125:
                return jjStopAtPos(0, 9);
        }
    }

    private int jjMoveStringLiteralDfa1_2(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 38:
                    if ((active0 & 549755813888L) != 0) {
                        return jjStopAtPos(1, 39);
                    }
                    break;
                case 61:
                    if ((active0 & 536870912) != 0) {
                        return jjStopAtPos(1, 29);
                    }
                    if ((active0 & 2147483648L) != 0) {
                        return jjStopAtPos(1, 31);
                    }
                    if ((active0 & 8589934592L) != 0) {
                        return jjStopAtPos(1, 33);
                    }
                    if ((active0 & 34359738368L) != 0) {
                        return jjStopAtPos(1, 35);
                    }
                    if ((active0 & 9007199254740992L) != 0) {
                        return jjStopAtPos(1, 53);
                    }
                    break;
                case Opcodes.V18 /* 62 */:
                    if ((active0 & 36028797018963968L) != 0) {
                        return jjStopAtPos(1, 55);
                    }
                    break;
                case 97:
                    return jjMoveStringLiteralDfa2_2(active0, 32768L);
                case 101:
                    if ((active0 & FileSize.GB_COEFFICIENT) != 0) {
                        return jjStartNfaWithStates_2(1, 30, 30);
                    }
                    if ((active0 & 4294967296L) != 0) {
                        return jjStartNfaWithStates_2(1, 32, 30);
                    }
                    if ((active0 & 68719476736L) != 0) {
                        return jjStartNfaWithStates_2(1, 36, 30);
                    }
                    break;
                case Opcodes.LMUL /* 105 */:
                    return jjMoveStringLiteralDfa2_2(active0, 1125899906842624L);
                case Opcodes.LDIV /* 109 */:
                    return jjMoveStringLiteralDfa2_2(active0, 8796093022208L);
                case Opcodes.FDIV /* 110 */:
                    return jjMoveStringLiteralDfa2_2(active0, 18691697672192L);
                case Opcodes.DDIV /* 111 */:
                    return jjMoveStringLiteralDfa2_2(active0, 4503874505277440L);
                case Opcodes.LREM /* 113 */:
                    if ((active0 & 17179869184L) != 0) {
                        return jjStartNfaWithStates_2(1, 34, 30);
                    }
                    break;
                case Opcodes.FREM /* 114 */:
                    if ((active0 & 4398046511104L) != 0) {
                        return jjStartNfaWithStates_2(1, 42, 30);
                    }
                    return jjMoveStringLiteralDfa2_2(active0, 16384L);
                case 116:
                    if ((active0 & 67108864) != 0) {
                        return jjStartNfaWithStates_2(1, 26, 30);
                    }
                    if ((active0 & 268435456) != 0) {
                        return jjStartNfaWithStates_2(1, 28, 30);
                    }
                    break;
                case Opcodes.LNEG /* 117 */:
                    return jjMoveStringLiteralDfa2_2(active0, 65536L);
                case 124:
                    if ((active0 & 2199023255552L) != 0) {
                        return jjStopAtPos(1, 41);
                    }
                    break;
            }
            return jjStartNfa_2(0, active0);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(0, active0);
            return 1;
        }
    }

    private int jjMoveStringLiteralDfa2_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 100:
                    if ((active02 & 1099511627776L) != 0) {
                        return jjStartNfaWithStates_2(2, 40, 30);
                    }
                    if ((active02 & 4503599627370496L) != 0) {
                        return jjStartNfaWithStates_2(2, 52, 30);
                    }
                    break;
                case 108:
                    return jjMoveStringLiteralDfa3_2(active02, 98304L);
                case 112:
                    return jjMoveStringLiteralDfa3_2(active02, 8796093022208L);
                case 115:
                    return jjMoveStringLiteralDfa3_2(active02, 17592186044416L);
                case 116:
                    if ((active02 & 274877906944L) != 0) {
                        return jjStartNfaWithStates_2(2, 38, 30);
                    }
                    break;
                case Opcodes.LNEG /* 117 */:
                    return jjMoveStringLiteralDfa3_2(active02, 16384L);
                case Opcodes.FNEG /* 118 */:
                    if ((active02 & 1125899906842624L) != 0) {
                        return jjStartNfaWithStates_2(2, 50, 30);
                    }
                    break;
            }
            return jjStartNfa_2(1, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(1, active02);
            return 2;
        }
    }

    private int jjMoveStringLiteralDfa3_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 101:
                    if ((active02 & 16384) != 0) {
                        return jjStartNfaWithStates_2(3, 14, 30);
                    }
                    break;
                case 108:
                    if ((active02 & 65536) != 0) {
                        return jjStartNfaWithStates_2(3, 16, 30);
                    }
                    break;
                case 115:
                    return jjMoveStringLiteralDfa4_2(active02, 32768L);
                case 116:
                    return jjMoveStringLiteralDfa4_2(active02, 26388279066624L);
            }
            return jjStartNfa_2(2, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(2, active02);
            return 3;
        }
    }

    private int jjMoveStringLiteralDfa4_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 97:
                    return jjMoveStringLiteralDfa5_2(active02, 17592186044416L);
                case 101:
                    if ((active02 & 32768) != 0) {
                        return jjStartNfaWithStates_2(4, 15, 30);
                    }
                    break;
                case Opcodes.LSHL /* 121 */:
                    if ((active02 & 8796093022208L) != 0) {
                        return jjStartNfaWithStates_2(4, 43, 30);
                    }
                    break;
            }
            return jjStartNfa_2(3, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(3, active02);
            return 4;
        }
    }

    private int jjMoveStringLiteralDfa5_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(3, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Opcodes.FDIV /* 110 */:
                    return jjMoveStringLiteralDfa6_2(active02, 17592186044416L);
                default:
                    return jjStartNfa_2(4, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(4, active02);
            return 5;
        }
    }

    private int jjMoveStringLiteralDfa6_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(4, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 99:
                    return jjMoveStringLiteralDfa7_2(active02, 17592186044416L);
                default:
                    return jjStartNfa_2(5, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(5, active02);
            return 6;
        }
    }

    private int jjMoveStringLiteralDfa7_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(5, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 101:
                    return jjMoveStringLiteralDfa8_2(active02, 17592186044416L);
                default:
                    return jjStartNfa_2(6, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(6, active02);
            return 7;
        }
    }

    private int jjMoveStringLiteralDfa8_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(6, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Opcodes.DDIV /* 111 */:
                    return jjMoveStringLiteralDfa9_2(active02, 17592186044416L);
                default:
                    return jjStartNfa_2(7, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(7, active02);
            return 8;
        }
    }

    private int jjMoveStringLiteralDfa9_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(7, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Opcodes.FSUB /* 102 */:
                    if ((active02 & 17592186044416L) != 0) {
                        return jjStartNfaWithStates_2(9, 44, 30);
                    }
                    break;
            }
            return jjStartNfa_2(8, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(8, active02);
            return 9;
        }
    }

    private int jjStartNfaWithStates_2(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
            return jjMoveNfa_2(state, pos + 1);
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
    private int jjMoveNfa_2(int r9, int r10) {
        /*
            Method dump skipped, instructions count: 2062
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.el.parser.ELParserTokenManager.jjMoveNfa_2(int, int):int");
    }

    private final int jjStopStringLiteralDfa_1(int pos, long active0) {
        switch (pos) {
            case 0:
                if ((active0 & 131072) != 0) {
                    return 1;
                }
                if ((active0 & 5661751853039616L) != 0) {
                    this.jjmatchedKind = 56;
                    return 30;
                }
                return -1;
            case 1:
                if ((active0 & 4489650110464L) != 0) {
                    return 30;
                }
                if ((active0 & 5657262202929152L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 1;
                    return 30;
                }
                return -1;
            case 2:
                if ((active0 & 5630873923747840L) != 0) {
                    return 30;
                }
                if ((active0 & 26388279181312L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 2;
                    return 30;
                }
                return -1;
            case 3:
                if ((active0 & 81920) != 0) {
                    return 30;
                }
                if ((active0 & 26388279099392L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 3;
                    return 30;
                }
                return -1;
            case 4:
                if ((active0 & 8796093054976L) != 0) {
                    return 30;
                }
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 4;
                    return 30;
                }
                return -1;
            case 5:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 5;
                    return 30;
                }
                return -1;
            case 6:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 6;
                    return 30;
                }
                return -1;
            case 7:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 7;
                    return 30;
                }
                return -1;
            case 8:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 8;
                    return 30;
                }
                return -1;
            default:
                return -1;
        }
    }

    private final int jjStartNfa_1(int pos, long active0) {
        return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case 33:
                this.jjmatchedKind = 37;
                return jjMoveStringLiteralDfa1_1(34359738368L);
            case 34:
            case 35:
            case 36:
            case 39:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
            case 70:
            case TypeReference.CAST /* 71 */:
            case 72:
            case 73:
            case 74:
            case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT /* 75 */:
            case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
            case 77:
            case 78:
            case Opcodes.IASTORE /* 79 */:
            case 80:
            case Opcodes.FASTORE /* 81 */:
            case Opcodes.DASTORE /* 82 */:
            case 83:
            case Opcodes.BASTORE /* 84 */:
            case Opcodes.CASTORE /* 85 */:
            case Opcodes.SASTORE /* 86 */:
            case Opcodes.POP /* 87 */:
            case 88:
            case Opcodes.DUP /* 89 */:
            case 90:
            case 92:
            case Opcodes.DUP2_X2 /* 94 */:
            case Opcodes.SWAP /* 95 */:
            case 96:
            case Opcodes.FADD /* 98 */:
            case 99:
            case 104:
            case Opcodes.FMUL /* 106 */:
            case Opcodes.DMUL /* 107 */:
            case 112:
            case Opcodes.LREM /* 113 */:
            case Opcodes.FREM /* 114 */:
            case 115:
            case Opcodes.LNEG /* 117 */:
            case Opcodes.FNEG /* 118 */:
            case Opcodes.DNEG /* 119 */:
            case 120:
            case Opcodes.LSHL /* 121 */:
            case 122:
            default:
                return jjMoveNfa_1(0, 0);
            case 37:
                return jjStopAtPos(0, 51);
            case 38:
                return jjMoveStringLiteralDfa1_1(549755813888L);
            case 40:
                return jjStopAtPos(0, 18);
            case 41:
                return jjStopAtPos(0, 19);
            case 42:
                return jjStopAtPos(0, 45);
            case ELParserConstants.EMPTY /* 43 */:
                this.jjmatchedKind = 46;
                return jjMoveStringLiteralDfa1_1(9007199254740992L);
            case 44:
                return jjStopAtPos(0, 24);
            case 45:
                this.jjmatchedKind = 47;
                return jjMoveStringLiteralDfa1_1(36028797018963968L);
            case 46:
                return jjStartNfaWithStates_1(0, 17, 1);
            case 47:
                return jjStopAtPos(0, 49);
            case 58:
                return jjStopAtPos(0, 22);
            case 59:
                return jjStopAtPos(0, 23);
            case 60:
                this.jjmatchedKind = 27;
                return jjMoveStringLiteralDfa1_1(2147483648L);
            case 61:
                this.jjmatchedKind = 54;
                return jjMoveStringLiteralDfa1_1(8589934592L);
            case Opcodes.V18 /* 62 */:
                this.jjmatchedKind = 25;
                return jjMoveStringLiteralDfa1_1(536870912L);
            case 63:
                return jjStopAtPos(0, 48);
            case 91:
                return jjStopAtPos(0, 20);
            case 93:
                return jjStopAtPos(0, 21);
            case 97:
                return jjMoveStringLiteralDfa1_1(1099511627776L);
            case 100:
                return jjMoveStringLiteralDfa1_1(1125899906842624L);
            case 101:
                return jjMoveStringLiteralDfa1_1(8813272891392L);
            case Opcodes.FSUB /* 102 */:
                return jjMoveStringLiteralDfa1_1(32768L);
            case Opcodes.DSUB /* 103 */:
                return jjMoveStringLiteralDfa1_1(1140850688L);
            case Opcodes.LMUL /* 105 */:
                return jjMoveStringLiteralDfa1_1(17592186044416L);
            case 108:
                return jjMoveStringLiteralDfa1_1(4563402752L);
            case Opcodes.LDIV /* 109 */:
                return jjMoveStringLiteralDfa1_1(4503599627370496L);
            case Opcodes.FDIV /* 110 */:
                return jjMoveStringLiteralDfa1_1(343597449216L);
            case Opcodes.DDIV /* 111 */:
                return jjMoveStringLiteralDfa1_1(4398046511104L);
            case 116:
                return jjMoveStringLiteralDfa1_1(16384L);
            case 123:
                return jjStopAtPos(0, 8);
            case 124:
                return jjMoveStringLiteralDfa1_1(2199023255552L);
            case 125:
                return jjStopAtPos(0, 9);
        }
    }

    private int jjMoveStringLiteralDfa1_1(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 38:
                    if ((active0 & 549755813888L) != 0) {
                        return jjStopAtPos(1, 39);
                    }
                    break;
                case 61:
                    if ((active0 & 536870912) != 0) {
                        return jjStopAtPos(1, 29);
                    }
                    if ((active0 & 2147483648L) != 0) {
                        return jjStopAtPos(1, 31);
                    }
                    if ((active0 & 8589934592L) != 0) {
                        return jjStopAtPos(1, 33);
                    }
                    if ((active0 & 34359738368L) != 0) {
                        return jjStopAtPos(1, 35);
                    }
                    if ((active0 & 9007199254740992L) != 0) {
                        return jjStopAtPos(1, 53);
                    }
                    break;
                case Opcodes.V18 /* 62 */:
                    if ((active0 & 36028797018963968L) != 0) {
                        return jjStopAtPos(1, 55);
                    }
                    break;
                case 97:
                    return jjMoveStringLiteralDfa2_1(active0, 32768L);
                case 101:
                    if ((active0 & FileSize.GB_COEFFICIENT) != 0) {
                        return jjStartNfaWithStates_1(1, 30, 30);
                    }
                    if ((active0 & 4294967296L) != 0) {
                        return jjStartNfaWithStates_1(1, 32, 30);
                    }
                    if ((active0 & 68719476736L) != 0) {
                        return jjStartNfaWithStates_1(1, 36, 30);
                    }
                    break;
                case Opcodes.LMUL /* 105 */:
                    return jjMoveStringLiteralDfa2_1(active0, 1125899906842624L);
                case Opcodes.LDIV /* 109 */:
                    return jjMoveStringLiteralDfa2_1(active0, 8796093022208L);
                case Opcodes.FDIV /* 110 */:
                    return jjMoveStringLiteralDfa2_1(active0, 18691697672192L);
                case Opcodes.DDIV /* 111 */:
                    return jjMoveStringLiteralDfa2_1(active0, 4503874505277440L);
                case Opcodes.LREM /* 113 */:
                    if ((active0 & 17179869184L) != 0) {
                        return jjStartNfaWithStates_1(1, 34, 30);
                    }
                    break;
                case Opcodes.FREM /* 114 */:
                    if ((active0 & 4398046511104L) != 0) {
                        return jjStartNfaWithStates_1(1, 42, 30);
                    }
                    return jjMoveStringLiteralDfa2_1(active0, 16384L);
                case 116:
                    if ((active0 & 67108864) != 0) {
                        return jjStartNfaWithStates_1(1, 26, 30);
                    }
                    if ((active0 & 268435456) != 0) {
                        return jjStartNfaWithStates_1(1, 28, 30);
                    }
                    break;
                case Opcodes.LNEG /* 117 */:
                    return jjMoveStringLiteralDfa2_1(active0, 65536L);
                case 124:
                    if ((active0 & 2199023255552L) != 0) {
                        return jjStopAtPos(1, 41);
                    }
                    break;
            }
            return jjStartNfa_1(0, active0);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(0, active0);
            return 1;
        }
    }

    private int jjMoveStringLiteralDfa2_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 100:
                    if ((active02 & 1099511627776L) != 0) {
                        return jjStartNfaWithStates_1(2, 40, 30);
                    }
                    if ((active02 & 4503599627370496L) != 0) {
                        return jjStartNfaWithStates_1(2, 52, 30);
                    }
                    break;
                case 108:
                    return jjMoveStringLiteralDfa3_1(active02, 98304L);
                case 112:
                    return jjMoveStringLiteralDfa3_1(active02, 8796093022208L);
                case 115:
                    return jjMoveStringLiteralDfa3_1(active02, 17592186044416L);
                case 116:
                    if ((active02 & 274877906944L) != 0) {
                        return jjStartNfaWithStates_1(2, 38, 30);
                    }
                    break;
                case Opcodes.LNEG /* 117 */:
                    return jjMoveStringLiteralDfa3_1(active02, 16384L);
                case Opcodes.FNEG /* 118 */:
                    if ((active02 & 1125899906842624L) != 0) {
                        return jjStartNfaWithStates_1(2, 50, 30);
                    }
                    break;
            }
            return jjStartNfa_1(1, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(1, active02);
            return 2;
        }
    }

    private int jjMoveStringLiteralDfa3_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 101:
                    if ((active02 & 16384) != 0) {
                        return jjStartNfaWithStates_1(3, 14, 30);
                    }
                    break;
                case 108:
                    if ((active02 & 65536) != 0) {
                        return jjStartNfaWithStates_1(3, 16, 30);
                    }
                    break;
                case 115:
                    return jjMoveStringLiteralDfa4_1(active02, 32768L);
                case 116:
                    return jjMoveStringLiteralDfa4_1(active02, 26388279066624L);
            }
            return jjStartNfa_1(2, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(2, active02);
            return 3;
        }
    }

    private int jjMoveStringLiteralDfa4_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 97:
                    return jjMoveStringLiteralDfa5_1(active02, 17592186044416L);
                case 101:
                    if ((active02 & 32768) != 0) {
                        return jjStartNfaWithStates_1(4, 15, 30);
                    }
                    break;
                case Opcodes.LSHL /* 121 */:
                    if ((active02 & 8796093022208L) != 0) {
                        return jjStartNfaWithStates_1(4, 43, 30);
                    }
                    break;
            }
            return jjStartNfa_1(3, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(3, active02);
            return 4;
        }
    }

    private int jjMoveStringLiteralDfa5_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(3, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Opcodes.FDIV /* 110 */:
                    return jjMoveStringLiteralDfa6_1(active02, 17592186044416L);
                default:
                    return jjStartNfa_1(4, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(4, active02);
            return 5;
        }
    }

    private int jjMoveStringLiteralDfa6_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(4, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 99:
                    return jjMoveStringLiteralDfa7_1(active02, 17592186044416L);
                default:
                    return jjStartNfa_1(5, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(5, active02);
            return 6;
        }
    }

    private int jjMoveStringLiteralDfa7_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(5, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 101:
                    return jjMoveStringLiteralDfa8_1(active02, 17592186044416L);
                default:
                    return jjStartNfa_1(6, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(6, active02);
            return 7;
        }
    }

    private int jjMoveStringLiteralDfa8_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(6, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Opcodes.DDIV /* 111 */:
                    return jjMoveStringLiteralDfa9_1(active02, 17592186044416L);
                default:
                    return jjStartNfa_1(7, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(7, active02);
            return 8;
        }
    }

    private int jjMoveStringLiteralDfa9_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(7, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Opcodes.FSUB /* 102 */:
                    if ((active02 & 17592186044416L) != 0) {
                        return jjStartNfaWithStates_1(9, 44, 30);
                    }
                    break;
            }
            return jjStartNfa_1(8, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(8, active02);
            return 9;
        }
    }

    private int jjStartNfaWithStates_1(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
            return jjMoveNfa_1(state, pos + 1);
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
    private int jjMoveNfa_1(int r9, int r10) {
        /*
            Method dump skipped, instructions count: 2062
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.el.parser.ELParserTokenManager.jjMoveNfa_1(int, int):int");
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

    private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0:
                return (jjbitVec4[i2] & l2) != 0;
            case 48:
                return (jjbitVec5[i2] & l2) != 0;
            case 49:
                return (jjbitVec6[i2] & l2) != 0;
            case 51:
                return (jjbitVec7[i2] & l2) != 0;
            case 61:
                return (jjbitVec8[i2] & l2) != 0;
            default:
                if ((jjbitVec3[i1] & l1) != 0) {
                    return true;
                }
                return false;
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:5:0x003b. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0129  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x019b A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.apache.el.parser.Token getNextToken() {
        /*
            Method dump skipped, instructions count: 565
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.el.parser.ELParserTokenManager.getNextToken():org.apache.el.parser.Token");
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
            case 2:
                this.image.append(jjstrLiteralImages[2]);
                this.lengthOfMatch = jjstrLiteralImages[2].length();
                this.deque.push(0);
                return;
            case 3:
                this.image.append(jjstrLiteralImages[3]);
                this.lengthOfMatch = jjstrLiteralImages[3].length();
                this.deque.push(0);
                return;
            case 4:
            case 5:
            case 6:
            case 7:
            default:
                return;
            case 8:
                this.image.append(jjstrLiteralImages[8]);
                this.lengthOfMatch = jjstrLiteralImages[8].length();
                this.deque.push(Integer.valueOf(this.curLexState));
                return;
            case 9:
                this.image.append(jjstrLiteralImages[9]);
                this.lengthOfMatch = jjstrLiteralImages[9].length();
                SwitchTo(this.deque.pop().intValue());
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

    public ELParserTokenManager(SimpleCharStream stream) {
        this.deque = new ArrayDeque();
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[30];
        this.jjstateSet = new int[60];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        this.input_stream = stream;
    }

    public ELParserTokenManager(SimpleCharStream stream, int lexState) {
        this.deque = new ArrayDeque();
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[30];
        this.jjstateSet = new int[60];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        ReInit(stream);
        SwitchTo(lexState);
    }

    public void ReInit(SimpleCharStream stream) {
        this.jjnewStateCnt = 0;
        this.jjmatchedPos = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        ReInitRounds();
    }

    private void ReInitRounds() {
        this.jjround = RandomValuePropertySourceEnvironmentPostProcessor.ORDER;
        int i = 30;
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

    public void ReInit(SimpleCharStream stream, int lexState) {
        ReInit(stream);
        SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 3 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
}
