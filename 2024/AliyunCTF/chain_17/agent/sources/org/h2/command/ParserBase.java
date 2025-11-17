package org.h2.command;

import cn.hutool.core.text.CharSequenceUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import org.h2.api.ErrorCode;
import org.h2.command.Token;
import org.h2.engine.Database;
import org.h2.engine.DbSettings;
import org.h2.engine.SessionLocal;
import org.h2.expression.Parameter;
import org.h2.message.DbException;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.CompareMode;
import org.h2.value.Value;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ParserBase.class */
public class ParserBase {
    final Database database;
    final SessionLocal session;
    private final boolean identifiersToLower;
    private final boolean identifiersToUpper;
    final boolean variableBinary;
    final BitSet nonKeywords;
    ArrayList<Token> tokens;
    int tokenIndex;
    Token token;
    int currentTokenType;
    String currentToken;
    String sqlCommand;
    ArrayList<Parameter> parameters;
    BitSet usedParameters;
    private boolean literalsChecked;
    ArrayList<String> expectedList;

    public static String quoteIdentifier(String str, int i) {
        if (str == null) {
            return "\"\"";
        }
        if ((i & 1) != 0 && ParserUtil.isSimpleIdentifier(str, false, false)) {
            return str;
        }
        return StringUtils.quoteIdentifier(str);
    }

    public static BitSet parseNonKeywords(String[] strArr) {
        if (strArr.length == 0) {
            return null;
        }
        BitSet bitSet = new BitSet();
        for (String str : strArr) {
            int binarySearch = Arrays.binarySearch(Token.TOKENS, 3, 92, str);
            if (binarySearch >= 0) {
                bitSet.set(binarySearch);
            }
        }
        if (bitSet.isEmpty()) {
            return null;
        }
        return bitSet;
    }

    public static String formatNonKeywords(BitSet bitSet) {
        if (bitSet == null || bitSet.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int i = -1;
        while (true) {
            int nextSetBit = bitSet.nextSetBit(i + 1);
            i = nextSetBit;
            if (nextSetBit >= 0) {
                if (i >= 3 && i <= 91) {
                    if (sb.length() > 0) {
                        sb.append(',');
                    }
                    sb.append(Token.TOKENS[i]);
                }
            } else {
                return sb.toString();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isKeyword(int i) {
        return i >= 3 && i <= 91;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParserBase(SessionLocal sessionLocal) {
        this.usedParameters = new BitSet();
        this.database = sessionLocal.getDatabase();
        DbSettings settings = this.database.getSettings();
        this.identifiersToLower = settings.databaseToLower;
        this.identifiersToUpper = settings.databaseToUpper;
        this.variableBinary = sessionLocal.isVariableBinary();
        this.nonKeywords = sessionLocal.getNonKeywords();
        this.session = sessionLocal;
    }

    public ParserBase() {
        this.usedParameters = new BitSet();
        this.database = null;
        this.identifiersToLower = false;
        this.identifiersToUpper = false;
        this.variableBinary = false;
        this.nonKeywords = null;
        this.session = null;
    }

    public final void setLiteralsChecked(boolean z) {
        this.literalsChecked = z;
    }

    public final void setSuppliedParameters(ArrayList<Parameter> arrayList) {
        this.parameters = arrayList;
    }

    public Object parseColumnList(String str, int i) {
        initialize(str, null, true);
        int i2 = 0;
        int size = this.tokens.size();
        while (true) {
            if (i2 >= size) {
                break;
            }
            if (this.tokens.get(i2).start() < i) {
                i2++;
            } else {
                setTokenIndex(i2);
                break;
            }
        }
        read(105);
        if (readIf(106)) {
            return Utils.EMPTY_INT_ARRAY;
        }
        if (isIdentifier()) {
            ArrayList newSmallArrayList = Utils.newSmallArrayList();
            while (isIdentifier()) {
                newSmallArrayList.add(this.currentToken);
                read();
                if (!readIfMore()) {
                    return newSmallArrayList.toArray(new String[0]);
                }
            }
            throw getSyntaxError();
        }
        if (this.currentTokenType == 94) {
            ArrayList newSmallArrayList2 = Utils.newSmallArrayList();
            do {
                newSmallArrayList2.add(Integer.valueOf(readInt()));
            } while (readIfMore());
            int size2 = newSmallArrayList2.size();
            int[] iArr = new int[size2];
            for (int i3 = 0; i3 < size2; i3++) {
                iArr[i3] = ((Integer) newSmallArrayList2.get(i3)).intValue();
            }
            return iArr;
        }
        throw getSyntaxError();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void initialize(String str, ArrayList<Token> arrayList, boolean z) {
        if (str == null) {
            str = "";
        }
        this.sqlCommand = str;
        if (arrayList == null) {
            BitSet bitSet = new BitSet();
            this.tokens = new Tokenizer(this.database, this.identifiersToUpper, this.identifiersToLower, this.nonKeywords).tokenize(str, z, bitSet);
            if (this.parameters == null) {
                int length = bitSet.length();
                if (length > 100000) {
                    throw DbException.getInvalidValueException("parameter index", Integer.valueOf(length));
                }
                if (length > 0) {
                    this.parameters = new ArrayList<>(length);
                    for (int i = 0; i < length; i++) {
                        this.parameters.add(new Parameter(i));
                    }
                } else {
                    this.parameters = new ArrayList<>();
                }
            }
        } else {
            this.tokens = arrayList;
        }
        resetTokenIndex();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void resetTokenIndex() {
        this.tokenIndex = -1;
        this.token = null;
        this.currentTokenType = -1;
        this.currentToken = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setTokenIndex(int i) {
        if (i != this.tokenIndex) {
            if (this.expectedList != null) {
                this.expectedList.clear();
            }
            this.token = this.tokens.get(i);
            this.tokenIndex = i;
            this.currentTokenType = this.token.tokenType();
            this.currentToken = this.token.asIdentifier();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final BitSet openParametersScope() {
        BitSet bitSet = this.usedParameters;
        this.usedParameters = new BitSet();
        return bitSet;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ArrayList<Parameter> closeParametersScope(BitSet bitSet) {
        BitSet bitSet2 = this.usedParameters;
        int cardinality = bitSet2.cardinality();
        ArrayList<Parameter> arrayList = new ArrayList<>(cardinality);
        if (cardinality > 0) {
            int i = -1;
            while (true) {
                int nextSetBit = bitSet2.nextSetBit(i + 1);
                i = nextSetBit;
                if (nextSetBit < 0) {
                    break;
                }
                arrayList.add(this.parameters.get(i));
            }
        }
        bitSet.or(bitSet2);
        this.usedParameters = bitSet;
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void read(String str) {
        if (!testToken(str, this.token)) {
            addExpected(str);
            throw getSyntaxError();
        }
        read();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void read(int i) {
        if (i != this.currentTokenType) {
            addExpected(i);
            throw getSyntaxError();
        }
        read();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void readCompat(int i) {
        if (i != this.currentTokenType) {
            throw getSyntaxError();
        }
        read();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean readIf(String str) {
        if (testToken(str, this.token)) {
            read();
            return true;
        }
        addExpected(str);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean readIfCompat(String str) {
        if (testToken(str, this.token)) {
            read();
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean readIf(String str, String str2) {
        int i = this.tokenIndex + 1;
        if (i + 1 < this.tokens.size() && testToken(str, this.token) && testToken(str2, this.tokens.get(i))) {
            setTokenIndex(i + 1);
            return true;
        }
        addExpected(str, str2);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean readIf(String str, int i) {
        int i2 = this.tokenIndex + 1;
        if (i2 + 1 < this.tokens.size() && this.tokens.get(i2).tokenType() == i && testToken(str, this.token)) {
            setTokenIndex(i2 + 1);
            return true;
        }
        addExpected(str, Token.TOKENS[i]);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean readIf(int i) {
        if (i == this.currentTokenType) {
            read();
            return true;
        }
        addExpected(i);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean readIfCompat(int i) {
        if (i == this.currentTokenType) {
            read();
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean readIf(int i, int i2) {
        if (i == this.currentTokenType) {
            int i3 = this.tokenIndex + 1;
            if (this.tokens.get(i3).tokenType() == i2) {
                setTokenIndex(i3 + 1);
                return true;
            }
        }
        addExpected(i, i2);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean readIfCompat(int i, int i2) {
        if (i == this.currentTokenType) {
            int i3 = this.tokenIndex + 1;
            if (this.tokens.get(i3).tokenType() == i2) {
                setTokenIndex(i3 + 1);
                return true;
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean readIf(int i, String str) {
        if (i == this.currentTokenType) {
            int i2 = this.tokenIndex + 1;
            if (testToken(str, this.tokens.get(i2))) {
                setTokenIndex(i2 + 1);
                return true;
            }
        }
        addExpected(Token.TOKENS[i], str);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean readIfCompat(int i, String str) {
        if (i == this.currentTokenType) {
            int i2 = this.tokenIndex + 1;
            if (testToken(str, this.tokens.get(i2))) {
                setTokenIndex(i2 + 1);
                return true;
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean readIf(Object... objArr) {
        int length = objArr.length;
        int size = this.tokens.size();
        int i = this.tokenIndex;
        if (i + length < size) {
            for (Object obj : objArr) {
                int i2 = i;
                i++;
                if (testToken(obj, this.tokens.get(i2))) {
                }
            }
            setTokenIndex(i);
            return true;
        }
        addExpected(objArr);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean readIfCompat(Object... objArr) {
        int length = objArr.length;
        int size = this.tokens.size();
        int i = this.tokenIndex;
        if (i + length < size) {
            for (Object obj : objArr) {
                int i2 = i;
                i++;
                if (!testToken(obj, this.tokens.get(i2))) {
                    return false;
                }
            }
            setTokenIndex(i);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isToken(String str) {
        if (testToken(str, this.token)) {
            return true;
        }
        addExpected(str);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isTokenCompat(String str) {
        return testToken(str, this.token);
    }

    private boolean testToken(Object obj, Token token) {
        return obj instanceof Integer ? ((Integer) obj).intValue() == token.tokenType() : testToken((String) obj, token);
    }

    private boolean testToken(String str, Token token) {
        if (!token.isQuoted()) {
            String asIdentifier = token.asIdentifier();
            return this.identifiersToUpper ? str.equals(asIdentifier) : str.equalsIgnoreCase(asIdentifier);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isToken(int i) {
        if (i == this.currentTokenType) {
            return true;
        }
        addExpected(i);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean equalsToken(String str, String str2) {
        return str == null ? str2 == null : str.equals(str2) || (!this.identifiersToUpper && str.equalsIgnoreCase(str2));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isIdentifier() {
        return this.currentTokenType == 2 || (this.nonKeywords != null && this.nonKeywords.get(this.currentTokenType));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addExpected(String str) {
        if (this.expectedList != null) {
            this.expectedList.add(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addExpected(int i) {
        if (this.expectedList != null) {
            this.expectedList.add(Token.TOKENS[i]);
        }
    }

    private void addExpected(int i, int i2) {
        if (this.expectedList != null) {
            this.expectedList.add(Token.TOKENS[i] + ' ' + Token.TOKENS[i2]);
        }
    }

    private void addExpected(String str, String str2) {
        if (this.expectedList != null) {
            this.expectedList.add(str + ' ' + str2);
        }
    }

    private void addExpected(Object... objArr) {
        if (this.expectedList != null) {
            StringJoiner stringJoiner = new StringJoiner(CharSequenceUtil.SPACE);
            for (Object obj : objArr) {
                stringJoiner.add(obj instanceof Integer ? Token.TOKENS[((Integer) obj).intValue()] : (String) obj);
            }
            this.expectedList.add(stringJoiner.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addMultipleExpected(int... iArr) {
        for (int i : iArr) {
            this.expectedList.add(Token.TOKENS[i]);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void read() {
        if (this.expectedList != null) {
            this.expectedList.clear();
        }
        if (this.tokenIndex + 1 < this.tokens.size()) {
            ArrayList<Token> arrayList = this.tokens;
            int i = this.tokenIndex + 1;
            this.tokenIndex = i;
            this.token = arrayList.get(i);
            this.currentTokenType = this.token.tokenType();
            this.currentToken = this.token.asIdentifier();
            if (this.currentToken != null && this.currentToken.length() > 256) {
                throw DbException.get(ErrorCode.NAME_TOO_LONG_2, this.currentToken.substring(0, 32), "256");
            }
            if (this.currentTokenType == 94) {
                checkLiterals();
                return;
            }
            return;
        }
        throw getSyntaxError();
    }

    private void checkLiterals() {
        if (!this.literalsChecked && this.session != null && !this.session.getAllowLiterals()) {
            int allowLiterals = this.database.getAllowLiterals();
            if (allowLiterals == 0 || (((this.token instanceof Token.CharacterStringToken) || (this.token instanceof Token.BinaryStringToken)) && allowLiterals != 2)) {
                throw DbException.get(ErrorCode.LITERALS_ARE_NOT_ALLOWED);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean readIfMore() {
        if (readIf(Opcodes.LDIV)) {
            return true;
        }
        read(106);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int readNonNegativeInt() {
        int readInt = readInt();
        if (readInt < 0) {
            throw DbException.getInvalidValueException("non-negative integer", Integer.valueOf(readInt));
        }
        return readInt;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int readInt() {
        boolean z = false;
        if (this.currentTokenType == 102) {
            z = true;
            read();
        } else if (this.currentTokenType == 103) {
            read();
        }
        if (this.currentTokenType != 94) {
            throw DbException.getSyntaxError(this.sqlCommand, this.token.start(), "integer");
        }
        Value value = this.token.value(this.session);
        if (z) {
            value = value.negate();
        }
        int i = value.getInt();
        read();
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final long readPositiveLong() {
        long readLong = readLong();
        if (readLong <= 0) {
            throw DbException.getInvalidValueException("positive long", Long.valueOf(readLong));
        }
        return readLong;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final long readLong() {
        boolean z = false;
        if (this.currentTokenType == 102) {
            z = true;
            read();
        } else if (this.currentTokenType == 103) {
            read();
        }
        if (this.currentTokenType != 94) {
            throw DbException.getSyntaxError(this.sqlCommand, this.token.start(), "long");
        }
        Value value = this.token.value(this.session);
        if (z) {
            value = value.negate();
        }
        long j = value.getLong();
        read();
        return j;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean readBooleanSetting() {
        switch (this.currentTokenType) {
            case 31:
                read();
                return false;
            case 60:
            case 77:
                read();
                return true;
            case 94:
                boolean z = this.token.value(this.session).getBoolean();
                read();
                return z;
            default:
                if (readIf(CompareMode.OFF)) {
                    return false;
                }
                if (this.expectedList != null) {
                    addMultipleExpected(60, 77, 31);
                }
                throw getSyntaxError();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Parameter readParameter() {
        int index = ((Token.ParameterToken) this.token).index() - 1;
        read();
        this.usedParameters.set(index);
        return this.parameters.get(index);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isKeyword(String str) {
        return ParserUtil.isKeyword(str, !this.identifiersToUpper);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String upperName(String str) {
        return this.identifiersToUpper ? str : StringUtils.toUpperEnglish(str);
    }

    public final int getLastParseIndex() {
        return this.token.start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ArrayList<Token> getRemainingTokens(int i) {
        List<Token> subList = this.tokens.subList(this.tokenIndex, this.tokens.size());
        ArrayList<Token> arrayList = new ArrayList<>(subList);
        subList.clear();
        this.tokens.add(new Token.EndOfInputToken(i));
        Iterator<Token> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().subtractFromStart(i);
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final DbException getSyntaxError() {
        if (this.expectedList == null || this.expectedList.isEmpty()) {
            return DbException.getSyntaxError(this.sqlCommand, this.token.start());
        }
        return DbException.getSyntaxError(this.sqlCommand, this.token.start(), String.join(", ", this.expectedList));
    }

    public final String toString() {
        return StringUtils.addAsterisk(this.sqlCommand, this.token.start());
    }
}
