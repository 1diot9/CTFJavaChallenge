package org.h2.command;

import ch.qos.logback.core.joran.JoranConstants;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.ListIterator;
import org.h2.api.ErrorCode;
import org.h2.command.Token;
import org.h2.engine.CastDataProvider;
import org.h2.engine.Constants;
import org.h2.message.DbException;
import org.h2.table.Column;
import org.h2.util.StringUtils;
import org.h2.value.ValueBigint;
import org.h2.value.ValueDecfloat;
import org.h2.value.ValueNumeric;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Tokenizer.class */
public final class Tokenizer {
    private final CastDataProvider provider;
    private final boolean identifiersToUpper;
    private final boolean identifiersToLower;
    private final BitSet nonKeywords;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Tokenizer(CastDataProvider castDataProvider, boolean z, boolean z2, BitSet bitSet) {
        this.provider = castDataProvider;
        this.identifiersToUpper = z;
        this.identifiersToLower = z2;
        this.nonKeywords = bitSet;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:213:0x02f2, code lost:            throw org.h2.message.DbException.getSyntaxError(r10, r17);     */
    /* JADX WARN: Code restructure failed: missing block: B:223:0x09e6, code lost:            if (r15 == false) goto L208;     */
    /* JADX WARN: Code restructure failed: missing block: B:224:0x09e9, code lost:            processUescape(r10, r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:225:0x09ef, code lost:            r0.add(new org.h2.command.Token.EndOfInputToken(r14 + 1));     */
    /* JADX WARN: Code restructure failed: missing block: B:226:0x0a02, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:348:0x095d, code lost:            throw org.h2.message.DbException.getSyntaxError(r10, r17);     */
    /* JADX WARN: Failed to find 'out' block for switch in B:5:0x002b. Please report as an issue. */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.ArrayList<org.h2.command.Token> tokenize(java.lang.String r10, boolean r11, java.util.BitSet r12) {
        /*
            Method dump skipped, instructions count: 2563
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Tokenizer.tokenize(java.lang.String, boolean, java.util.BitSet):java.util.ArrayList");
    }

    private int readIdentifier(String str, int i, int i2, int i3, ArrayList<Token> arrayList) {
        int findIdentifierEnd = findIdentifierEnd(str, i, i3);
        arrayList.add(new Token.IdentifierToken(i2, extractIdentifier(str, i2, findIdentifierEnd), false, false));
        return findIdentifierEnd;
    }

    private int readA(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (i4 == 2) {
            i3 = (str.charAt(i2 + 1) & 65503) == 83 ? 7 : 2;
        } else if (eq("ALL", str, i2, i4)) {
            i3 = 3;
        } else if (eq("AND", str, i2, i4)) {
            i3 = 4;
        } else if (eq("ANY", str, i2, i4)) {
            i3 = 5;
        } else if (eq("ARRAY", str, i2, i4)) {
            i3 = 6;
        } else if (eq("ASYMMETRIC", str, i2, i4)) {
            i3 = 8;
        } else if (eq("AUTHORIZATION", str, i2, i4)) {
            i3 = 9;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readB(String str, int i, int i2, ArrayList<Token> arrayList) {
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, eq("BETWEEN", str, i2, findIdentifierEnd - i2) ? 10 : 2);
    }

    private int readC(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (eq("CASE", str, i2, i4)) {
            i3 = 11;
        } else if (eq("CAST", str, i2, i4)) {
            i3 = 12;
        } else if (eq("CHECK", str, i2, i4)) {
            i3 = 13;
        } else if (eq("CONSTRAINT", str, i2, i4)) {
            i3 = 14;
        } else if (eq("CROSS", str, i2, i4)) {
            i3 = 15;
        } else if (i4 >= 12 && eq("CURRENT_", str, i2, 8)) {
            i3 = getTokenTypeCurrent(str, i2, i4);
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private static int getTokenTypeCurrent(String str, int i, int i2) {
        int i3 = i + 8;
        switch (i2) {
            case 12:
                if (eqCurrent("CURRENT_DATE", str, i3, i2)) {
                    return 17;
                }
                if (eqCurrent("CURRENT_PATH", str, i3, i2)) {
                    return 18;
                }
                if (eqCurrent("CURRENT_ROLE", str, i3, i2)) {
                    return 19;
                }
                if (eqCurrent("CURRENT_TIME", str, i3, i2)) {
                    return 21;
                }
                if (eqCurrent("CURRENT_USER", str, i3, i2)) {
                    return 23;
                }
                return 2;
            case 13:
            case 16:
            default:
                return 2;
            case 14:
                if (eqCurrent("CURRENT_SCHEMA", str, i3, i2)) {
                    return 20;
                }
                return 2;
            case 15:
                if (eqCurrent("CURRENT_CATALOG", str, i3, i2)) {
                    return 16;
                }
                return 2;
            case 17:
                if (eqCurrent("CURRENT_TIMESTAMP", str, i3, i2)) {
                    return 22;
                }
                return 2;
        }
    }

    private static boolean eqCurrent(String str, String str2, int i, int i2) {
        for (int i3 = 8; i3 < i2; i3++) {
            int i4 = i;
            i++;
            if (str.charAt(i3) != (str2.charAt(i4) & 65503)) {
                return false;
            }
        }
        return true;
    }

    private int readD(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (eq("DAY", str, i2, i4)) {
            i3 = 24;
        } else if (eq("DEFAULT", str, i2, i4)) {
            i3 = 25;
        } else if (eq("DISTINCT", str, i2, i4)) {
            i3 = 26;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readE(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (eq("ELSE", str, i2, i4)) {
            i3 = 27;
        } else if (eq("END", str, i2, i4)) {
            i3 = 28;
        } else if (eq("EXCEPT", str, i2, i4)) {
            i3 = 29;
        } else if (eq("EXISTS", str, i2, i4)) {
            i3 = 30;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readF(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (eq("FETCH", str, i2, i4)) {
            i3 = 32;
        } else if (eq("FROM", str, i2, i4)) {
            i3 = 35;
        } else if (eq("FOR", str, i2, i4)) {
            i3 = 33;
        } else if (eq("FOREIGN", str, i2, i4)) {
            i3 = 34;
        } else if (eq("FULL", str, i2, i4)) {
            i3 = 36;
        } else if (eq("FALSE", str, i2, i4)) {
            i3 = 31;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readG(String str, int i, int i2, ArrayList<Token> arrayList) {
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, eq("GROUP", str, i2, findIdentifierEnd - i2) ? 37 : 2);
    }

    private int readH(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (eq("HAVING", str, i2, i4)) {
            i3 = 38;
        } else if (eq("HOUR", str, i2, i4)) {
            i3 = 39;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readI(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (i4 == 2) {
            switch (str.charAt(i2 + 1) & 65503) {
                case 70:
                    i3 = 40;
                    break;
                case 78:
                    i3 = 41;
                    break;
                case 83:
                    i3 = 45;
                    break;
                default:
                    i3 = 2;
                    break;
            }
        } else if (eq("INNER", str, i2, i4)) {
            i3 = 42;
        } else if (eq("INTERSECT", str, i2, i4)) {
            i3 = 43;
        } else if (eq("INTERVAL", str, i2, i4)) {
            i3 = 44;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readJ(String str, int i, int i2, ArrayList<Token> arrayList) {
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, eq("JOIN", str, i2, findIdentifierEnd - i2) ? 46 : 2);
    }

    private int readK(String str, int i, int i2, ArrayList<Token> arrayList) {
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, eq("KEY", str, i2, findIdentifierEnd - i2) ? 47 : 2);
    }

    private int readL(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (eq("LEFT", str, i2, i4)) {
            i3 = 48;
        } else if (eq("LIMIT", str, i2, i4)) {
            i3 = this.provider.getMode().limit ? 50 : 2;
        } else if (eq("LIKE", str, i2, i4)) {
            i3 = 49;
        } else if (eq("LOCALTIME", str, i2, i4)) {
            i3 = 51;
        } else if (eq("LOCALTIMESTAMP", str, i2, i4)) {
            i3 = 52;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readM(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (eq("MINUS", str, i2, i4)) {
            i3 = this.provider.getMode().minusIsExcept ? 53 : 2;
        } else if (eq("MINUTE", str, i2, i4)) {
            i3 = 54;
        } else if (eq("MONTH", str, i2, i4)) {
            i3 = 55;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readN(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (eq("NOT", str, i2, i4)) {
            i3 = 57;
        } else if (eq("NATURAL", str, i2, i4)) {
            i3 = 56;
        } else if (eq(JoranConstants.NULL, str, i2, i4)) {
            i3 = 58;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readO(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (i4 == 2) {
            switch (str.charAt(i2 + 1) & 65503) {
                case 78:
                    i3 = 60;
                    break;
                case 82:
                    i3 = 61;
                    break;
                default:
                    i3 = 2;
                    break;
            }
        } else if (eq("OFFSET", str, i2, i4)) {
            i3 = 59;
        } else if (eq("ORDER", str, i2, i4)) {
            i3 = 62;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readP(String str, int i, int i2, ArrayList<Token> arrayList) {
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, eq("PRIMARY", str, i2, findIdentifierEnd - i2) ? 63 : 2);
    }

    private int readQ(String str, int i, int i2, ArrayList<Token> arrayList) {
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, eq("QUALIFY", str, i2, findIdentifierEnd - i2) ? 64 : 2);
    }

    private int readR(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (eq("RIGHT", str, i2, i4)) {
            i3 = 65;
        } else if (eq("ROW", str, i2, i4)) {
            i3 = 66;
        } else if (eq("ROWNUM", str, i2, i4)) {
            i3 = 67;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readS(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (eq("SECOND", str, i2, i4)) {
            i3 = 68;
        } else if (eq("SELECT", str, i2, i4)) {
            i3 = 69;
        } else if (eq("SESSION_USER", str, i2, i4)) {
            i3 = 70;
        } else if (eq("SET", str, i2, i4)) {
            i3 = 71;
        } else if (eq("SOME", str, i2, i4)) {
            i3 = 72;
        } else if (eq("SYMMETRIC", str, i2, i4)) {
            i3 = 73;
        } else if (eq("SYSTEM_USER", str, i2, i4)) {
            i3 = 74;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readT(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (i4 == 2) {
            i3 = (str.charAt(i2 + 1) & 65503) == 79 ? 76 : 2;
        } else if (eq("TABLE", str, i2, i4)) {
            i3 = 75;
        } else if (eq(Constants.CLUSTERING_ENABLED, str, i2, i4)) {
            i3 = 77;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readU(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (eq("UESCAPE", str, i2, i4)) {
            i3 = 78;
        } else if (eq("UNION", str, i2, i4)) {
            i3 = 79;
        } else if (eq("UNIQUE", str, i2, i4)) {
            i3 = 80;
        } else if (eq("UNKNOWN", str, i2, i4)) {
            i3 = 81;
        } else if (eq("USER", str, i2, i4)) {
            i3 = 82;
        } else if (eq("USING", str, i2, i4)) {
            i3 = 83;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readV(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (eq("VALUE", str, i2, i4)) {
            i3 = 84;
        } else if (eq("VALUES", str, i2, i4)) {
            i3 = 85;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readW(String str, int i, int i2, ArrayList<Token> arrayList) {
        int i3;
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        int i4 = findIdentifierEnd - i2;
        if (eq("WHEN", str, i2, i4)) {
            i3 = 86;
        } else if (eq("WHERE", str, i2, i4)) {
            i3 = 87;
        } else if (eq("WINDOW", str, i2, i4)) {
            i3 = 88;
        } else if (eq("WITH", str, i2, i4)) {
            i3 = 89;
        } else {
            i3 = 2;
        }
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, i3);
    }

    private int readY(String str, int i, int i2, ArrayList<Token> arrayList) {
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, eq("YEAR", str, i2, findIdentifierEnd - i2) ? 90 : 2);
    }

    private int read_(String str, int i, int i2, ArrayList<Token> arrayList) {
        int findIdentifierEnd = findIdentifierEnd(str, i, i2);
        return readIdentifierOrKeyword(str, i2, arrayList, findIdentifierEnd, (findIdentifierEnd - i2 == 7 && Column.ROWID.regionMatches(true, 1, str, i2 + 1, 6)) ? 91 : 2);
    }

    private int readIdentifierOrKeyword(String str, int i, ArrayList<Token> arrayList, int i2, int i3) {
        Token keywordToken;
        if (i3 == 2) {
            keywordToken = new Token.IdentifierToken(i, extractIdentifier(str, i, i2), false, false);
        } else if (this.nonKeywords != null && this.nonKeywords.get(i3)) {
            keywordToken = new Token.KeywordOrIdentifierToken(i, i3, extractIdentifier(str, i, i2));
        } else {
            keywordToken = new Token.KeywordToken(i, i3);
        }
        arrayList.add(keywordToken);
        return i2;
    }

    private static boolean eq(String str, String str2, int i, int i2) {
        if (i2 != str.length()) {
            return false;
        }
        for (int i3 = 1; i3 < i2; i3++) {
            i++;
            if (str.charAt(i3) != (str2.charAt(i) & 65503)) {
                return false;
            }
        }
        return true;
    }

    private int findIdentifierEnd(String str, int i, int i2) {
        int i3 = i2 + 1;
        while (i3 <= i) {
            int codePointAt = str.codePointAt(i3);
            if (!Character.isJavaIdentifierPart(codePointAt) && (codePointAt != 35 || !this.provider.getMode().supportPoundSymbolForColumnNames)) {
                break;
            }
            i3 += Character.charCount(codePointAt);
        }
        return i3;
    }

    private String extractIdentifier(String str, int i, int i2) {
        return convertCase(str.substring(i, i2));
    }

    private int readQuotedIdentifier(String str, int i, int i2, int i3, char c, boolean z, ArrayList<Token> arrayList) {
        int i4 = i3 + 1;
        int indexOf = str.indexOf(c, i4);
        if (indexOf < 0) {
            throw DbException.getSyntaxError(str, i2);
        }
        String substring = str.substring(i4, indexOf);
        int i5 = indexOf + 1;
        if (i5 <= i && str.charAt(i5) == c) {
            StringBuilder sb = new StringBuilder(substring);
            do {
                int indexOf2 = str.indexOf(c, i5 + 1);
                if (indexOf2 < 0) {
                    throw DbException.getSyntaxError(str, i2);
                }
                sb.append((CharSequence) str, i5, indexOf2);
                i5 = indexOf2 + 1;
                if (i5 > i) {
                    break;
                }
            } while (str.charAt(i5) == c);
            substring = sb.toString();
        }
        if (c == '`') {
            substring = convertCase(substring);
        }
        arrayList.add(new Token.IdentifierToken(i2, substring, true, z));
        return i5;
    }

    private String convertCase(String str) {
        if (this.identifiersToUpper) {
            str = StringUtils.toUpperEnglish(str);
        } else if (this.identifiersToLower) {
            str = StringUtils.toLowerEnglish(str);
        }
        return str;
    }

    private static int readBinaryString(String str, int i, int i2, int i3, ArrayList<Token> arrayList) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        do {
            int i4 = i3 + 1;
            int indexOf = str.indexOf(39, i4);
            if (indexOf < 0 || (indexOf < i2 && str.charAt(indexOf + 1) == '\'')) {
                throw DbException.getSyntaxError(str, i);
            }
            StringUtils.convertHexWithSpacesToBytes(byteArrayOutputStream, str, i4, indexOf);
            i3 = skipWhitespace(str, i2, indexOf + 1);
            if (i3 > i2) {
                break;
            }
        } while (str.charAt(i3) == '\'');
        arrayList.add(new Token.BinaryStringToken(i, byteArrayOutputStream.toByteArray()));
        return i3;
    }

    private static int readCharacterString(String str, int i, int i2, int i3, boolean z, ArrayList<Token> arrayList) {
        String str2 = null;
        StringBuilder sb = null;
        do {
            int i4 = i3 + 1;
            int indexOf = str.indexOf(39, i4);
            if (indexOf < 0) {
                throw DbException.getSyntaxError(str, i);
            }
            if (str2 == null) {
                str2 = str.substring(i4, indexOf);
            } else {
                if (sb == null) {
                    sb = new StringBuilder(str2);
                }
                sb.append((CharSequence) str, i4, indexOf);
            }
            int i5 = indexOf + 1;
            if (i5 <= i2 && str.charAt(i5) == '\'') {
                if (sb == null) {
                    sb = new StringBuilder(str2);
                }
                do {
                    int indexOf2 = str.indexOf(39, i5 + 1);
                    if (indexOf2 < 0) {
                        throw DbException.getSyntaxError(str, i);
                    }
                    sb.append((CharSequence) str, i5, indexOf2);
                    i5 = indexOf2 + 1;
                    if (i5 > i2) {
                        break;
                    }
                } while (str.charAt(i5) == '\'');
            }
            i3 = skipWhitespace(str, i2, i5);
            if (i3 > i2) {
                break;
            }
        } while (str.charAt(i3) == '\'');
        if (sb != null) {
            str2 = sb.toString();
        }
        arrayList.add(new Token.CharacterStringToken(i, str2, z));
        return i3;
    }

    private static int skipWhitespace(String str, int i, int i2) {
        while (i2 <= i) {
            int codePointAt = str.codePointAt(i2);
            if (!Character.isWhitespace(codePointAt)) {
                if (codePointAt != 47 || i2 >= i) {
                    break;
                }
                char charAt = str.charAt(i2 + 1);
                if (charAt == '*') {
                    i2 = skipBracketedComment(str, i, i2);
                } else {
                    if (charAt != '/') {
                        break;
                    }
                    i2 = skipSimpleComment(str, i, i2);
                }
            } else {
                i2 += Character.charCount(codePointAt);
            }
        }
        return i2;
    }

    private static int read0xBinaryString(String str, int i, int i2, ArrayList<Token> arrayList) {
        char charAt;
        char c;
        while (i2 <= i && (((charAt = str.charAt(i2)) >= '0' && charAt <= '9') || ((c = (char) (charAt & 65503)) >= 'A' && c <= 'F'))) {
            i2++;
        }
        if (i2 <= i && Character.isJavaIdentifierPart(str.codePointAt(i2))) {
            throw DbException.get(ErrorCode.HEX_STRING_WRONG_1, str.substring(i2, i2 + 1));
        }
        arrayList.add(new Token.BinaryStringToken(i2, StringUtils.convertHexToBytes(str.substring(i2, i2))));
        return i2;
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0161  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0169  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int readIntegerNumber(java.lang.String r10, int r11, int r12, int r13, java.util.ArrayList<org.h2.command.Token> r14, java.lang.String r15, int r16) {
        /*
            Method dump skipped, instructions count: 451
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Tokenizer.readIntegerNumber(java.lang.String, int, int, int, java.util.ArrayList, java.lang.String, int):int");
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:21:0x0104. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:68:0x0038. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:95:0x01a9  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x01b1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int readNumeric(java.lang.String r10, int r11, int r12, int r13, char r14, java.util.ArrayList<org.h2.command.Token> r15) {
        /*
            Method dump skipped, instructions count: 452
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Tokenizer.readNumeric(java.lang.String, int, int, int, char, java.util.ArrayList):int");
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:12:0x0038. Please report as an issue. */
    private static int readFloat(String str, int i, int i2, int i3, boolean z, ArrayList<Token> arrayList) {
        int i4 = i3 + 1;
        int i5 = Integer.MIN_VALUE;
        while (true) {
            i3++;
            if (i3 <= i2) {
                char charAt = str.charAt(i3);
                if (charAt < '0' || charAt > '9') {
                    if (i5 == i3 - 1) {
                        throw DbException.getSyntaxError(str, i, "Numeric");
                    }
                    switch (charAt) {
                        case 'E':
                        case 'e':
                            return readApproximateNumeric(str, i, i2, i3, z, arrayList);
                        case '_':
                            if (i3 == i4) {
                                throw DbException.getSyntaxError(str, i, "Numeric");
                            }
                            i5 = i3;
                            z = true;
                            break;
                    }
                }
            }
        }
        if (i5 == i3 - 1) {
            throw DbException.getSyntaxError(str, i, "Numeric");
        }
        arrayList.add(new Token.ValueToken(i, ValueNumeric.get(readBigDecimal(str, i, i3, z))));
        return i3;
    }

    private static int readApproximateNumeric(String str, int i, int i2, int i3, boolean z, ArrayList<Token> arrayList) {
        if (i3 == i2) {
            throw DbException.getSyntaxError(str, i, "Approximate numeric");
        }
        int i4 = i3 + 1;
        char charAt = str.charAt(i4);
        if (charAt == '+' || charAt == '-') {
            if (i4 == i2) {
                throw DbException.getSyntaxError(str, i, "Approximate numeric");
            }
            i4++;
            charAt = str.charAt(i4);
        }
        if (charAt < '0' || charAt > '9') {
            throw DbException.getSyntaxError(str, i, "Approximate numeric");
        }
        int i5 = Integer.MIN_VALUE;
        while (true) {
            i4++;
            if (i4 > i2) {
                break;
            }
            char charAt2 = str.charAt(i4);
            if (charAt2 < '0' || charAt2 > '9') {
                if (i5 == i4 - 1) {
                    throw DbException.getSyntaxError(str, i, "Approximate numeric");
                }
                if (charAt2 != '_') {
                    break;
                }
                i5 = i4;
                z = true;
            }
        }
        if (i5 == i4 - 1) {
            throw DbException.getSyntaxError(str, i, "Approximate numeric");
        }
        arrayList.add(new Token.ValueToken(i, ValueDecfloat.get(readBigDecimal(str, i, i4, z))));
        return i4;
    }

    private static BigDecimal readBigDecimal(String str, int i, int i2, boolean z) {
        try {
            return new BigDecimal(readAndRemoveUnderscores(str, i, i2, z));
        } catch (NumberFormatException e) {
            throw DbException.getSyntaxError(str, i, "Numeric");
        }
    }

    private static int finishBigInteger(String str, int i, int i2, int i3, int i4, boolean z, boolean z2, int i5, ArrayList<Token> arrayList) {
        Token bigintToken;
        if (z) {
            i3++;
        }
        if (i5 == 16 && i3 <= i2 && Character.isJavaIdentifierPart(str.codePointAt(i3))) {
            throw DbException.getSyntaxError(str, i, "Hex number");
        }
        BigInteger bigInteger = new BigInteger(readAndRemoveUnderscores(str, i4, i3, z2), i5);
        if (bigInteger.compareTo(ValueBigint.MAX_BI) > 0) {
            if (z) {
                throw DbException.getSyntaxError(str, i, "BIGINT");
            }
            bigintToken = new Token.ValueToken(i, ValueNumeric.get(bigInteger));
        } else {
            bigintToken = new Token.BigintToken(i, bigInteger.longValue());
        }
        arrayList.add(bigintToken);
        return i3;
    }

    private static String readAndRemoveUnderscores(String str, int i, int i2, boolean z) {
        if (!z) {
            return str.substring(i, i2);
        }
        StringBuilder sb = new StringBuilder((i2 - i) - 1);
        while (i < i2) {
            char charAt = str.charAt(i);
            if (charAt != '_') {
                sb.append(charAt);
            }
            i++;
        }
        return sb.toString();
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x0033, code lost:            r7 = r7 - 1;        r5 = r5 + 1;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int skipBracketedComment(java.lang.String r3, int r4, int r5) {
        /*
            r0 = r5
            r6 = r0
            int r5 = r5 + 2
            r0 = 1
            r7 = r0
        L8:
            r0 = r7
            if (r0 <= 0) goto L56
        Ld:
            r0 = r5
            r1 = r4
            if (r0 < r1) goto L18
            r0 = r3
            r1 = r6
            org.h2.message.DbException r0 = org.h2.message.DbException.getSyntaxError(r0, r1)
            throw r0
        L18:
            r0 = r3
            r1 = r5
            int r5 = r5 + 1
            char r0 = r0.charAt(r1)
            r8 = r0
            r0 = r8
            r1 = 42
            if (r0 != r1) goto L3c
            r0 = r3
            r1 = r5
            char r0 = r0.charAt(r1)
            r1 = 47
            if (r0 != r1) goto L53
            int r7 = r7 + (-1)
            int r5 = r5 + 1
            goto L8
        L3c:
            r0 = r8
            r1 = 47
            if (r0 != r1) goto L53
            r0 = r3
            r1 = r5
            char r0 = r0.charAt(r1)
            r1 = 42
            if (r0 != r1) goto L53
            int r7 = r7 + 1
            int r5 = r5 + 1
        L53:
            goto Ld
        L56:
            r0 = r5
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Tokenizer.skipBracketedComment(java.lang.String, int, int):int");
    }

    private static int skipSimpleComment(String str, int i, int i2) {
        char charAt;
        int i3 = i2 + 2;
        while (i3 <= i && (charAt = str.charAt(i3)) != '\n' && charAt != '\r') {
            i3++;
        }
        return i3;
    }

    private static int parseParameterIndex(String str, int i, int i2, ArrayList<Token> arrayList) {
        char charAt;
        long j = 0;
        do {
            i2++;
            if (i2 <= i && (charAt = str.charAt(i2)) >= '0' && charAt <= '9') {
                j = (j * 10) + (charAt - '0');
            } else {
                if (i2 > i2 + 1 && j == 0) {
                    throw DbException.getInvalidValueException("parameter index", Long.valueOf(j));
                }
                arrayList.add(new Token.ParameterToken(i2, (int) j));
                return i2;
            }
        } while (j <= 2147483647L);
        throw DbException.getInvalidValueException("parameter index", Long.valueOf(j));
    }

    private static int assignParameterIndex(ArrayList<Token> arrayList, int i, BitSet bitSet) {
        int i2;
        Token.ParameterToken parameterToken = (Token.ParameterToken) arrayList.get(arrayList.size() - 1);
        int i3 = parameterToken.index;
        if (i3 == 0) {
            if (i < 0) {
                throw DbException.get(ErrorCode.CANNOT_MIX_INDEXED_AND_UNINDEXED_PARAMS);
            }
            i2 = i + 1;
            i3 = i2;
            parameterToken.index = i2;
        } else {
            if (i > 0) {
                throw DbException.get(ErrorCode.CANNOT_MIX_INDEXED_AND_UNINDEXED_PARAMS);
            }
            i2 = -1;
        }
        bitSet.set(i3 - 1);
        return i2;
    }

    private static void processUescape(String str, ArrayList<Token> arrayList) {
        ListIterator<Token> listIterator = arrayList.listIterator();
        while (listIterator.hasNext()) {
            Token next = listIterator.next();
            if (next.needsUnicodeConversion()) {
                int i = 92;
                if (listIterator.hasNext()) {
                    Token next2 = listIterator.next();
                    if (next2.tokenType() == 78) {
                        listIterator.remove();
                        if (listIterator.hasNext()) {
                            Token next3 = listIterator.next();
                            listIterator.remove();
                            if (next3 instanceof Token.CharacterStringToken) {
                                String str2 = ((Token.CharacterStringToken) next3).string;
                                if (str2.codePointCount(0, str2.length()) == 1) {
                                    int codePointAt = str2.codePointAt(0);
                                    if (!Character.isWhitespace(codePointAt) && ((codePointAt < 48 || codePointAt > 57) && ((codePointAt < 65 || codePointAt > 70) && (codePointAt < 97 || codePointAt > 102)))) {
                                        switch (codePointAt) {
                                            case 34:
                                            case 39:
                                            case 43:
                                                break;
                                            default:
                                                i = codePointAt;
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                        throw DbException.getSyntaxError(str, next2.start() + 7, "'<Unicode escape character>'");
                    }
                }
                next.convertUnicode(i);
            }
        }
    }
}
