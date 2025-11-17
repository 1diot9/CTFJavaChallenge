package org.yaml.snakeyaml.scanner;

import ch.qos.logback.classic.net.SyslogAppender;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.asm.Opcodes;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.tokens.BlockEndToken;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.tokens.BlockMappingStartToken;
import org.yaml.snakeyaml.tokens.BlockSequenceStartToken;
import org.yaml.snakeyaml.tokens.CommentToken;
import org.yaml.snakeyaml.tokens.DirectiveToken;
import org.yaml.snakeyaml.tokens.DocumentEndToken;
import org.yaml.snakeyaml.tokens.DocumentStartToken;
import org.yaml.snakeyaml.tokens.FlowEntryToken;
import org.yaml.snakeyaml.tokens.FlowMappingEndToken;
import org.yaml.snakeyaml.tokens.FlowMappingStartToken;
import org.yaml.snakeyaml.tokens.FlowSequenceEndToken;
import org.yaml.snakeyaml.tokens.FlowSequenceStartToken;
import org.yaml.snakeyaml.tokens.KeyToken;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.StreamEndToken;
import org.yaml.snakeyaml.tokens.StreamStartToken;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.TagTuple;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.tokens.ValueToken;
import org.yaml.snakeyaml.util.ArrayStack;
import org.yaml.snakeyaml.util.UriEncoder;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/scanner/ScannerImpl.class */
public final class ScannerImpl implements Scanner {
    private static final Pattern NOT_HEXA = Pattern.compile("[^0-9A-Fa-f]");
    public static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap();
    public static final Map<Character, Integer> ESCAPE_CODES = new HashMap();
    private final StreamReader reader;
    private final List<Token> tokens;
    private Token lastToken;
    private final ArrayStack<Integer> indents;
    private final boolean parseComments;
    private final LoaderOptions loaderOptions;
    private final Map<Integer, SimpleKey> possibleSimpleKeys;
    private boolean done = false;
    private int flowLevel = 0;
    private int tokensTaken = 0;
    private int indent = -1;
    private boolean allowSimpleKey = true;

    static {
        ESCAPE_REPLACEMENTS.put('0', "��");
        ESCAPE_REPLACEMENTS.put('a', "\u0007");
        ESCAPE_REPLACEMENTS.put('b', "\b");
        ESCAPE_REPLACEMENTS.put('t', SyslogAppender.DEFAULT_STACKTRACE_PATTERN);
        ESCAPE_REPLACEMENTS.put('n', "\n");
        ESCAPE_REPLACEMENTS.put('v', "\u000b");
        ESCAPE_REPLACEMENTS.put('f', "\f");
        ESCAPE_REPLACEMENTS.put('r', "\r");
        ESCAPE_REPLACEMENTS.put('e', "\u001b");
        ESCAPE_REPLACEMENTS.put(' ', " ");
        ESCAPE_REPLACEMENTS.put('\"', "\"");
        ESCAPE_REPLACEMENTS.put('\\', "\\");
        ESCAPE_REPLACEMENTS.put('N', "\u0085");
        ESCAPE_REPLACEMENTS.put('_', " ");
        ESCAPE_REPLACEMENTS.put('L', "\u2028");
        ESCAPE_REPLACEMENTS.put('P', "\u2029");
        ESCAPE_CODES.put('x', 2);
        ESCAPE_CODES.put('u', 4);
        ESCAPE_CODES.put('U', 8);
    }

    public ScannerImpl(StreamReader reader, LoaderOptions options) {
        if (options == null) {
            throw new NullPointerException("LoaderOptions must be provided.");
        }
        this.parseComments = options.isProcessComments();
        this.reader = reader;
        this.tokens = new ArrayList(100);
        this.indents = new ArrayStack<>(10);
        this.possibleSimpleKeys = new LinkedHashMap();
        this.loaderOptions = options;
        fetchStreamStart();
    }

    @Override // org.yaml.snakeyaml.scanner.Scanner
    public boolean checkToken(Token.ID... choices) {
        while (needMoreTokens()) {
            fetchMoreTokens();
        }
        if (!this.tokens.isEmpty()) {
            if (choices.length == 0) {
                return true;
            }
            Token.ID first = this.tokens.get(0).getTokenId();
            for (Token.ID id : choices) {
                if (first == id) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // org.yaml.snakeyaml.scanner.Scanner
    public Token peekToken() {
        while (needMoreTokens()) {
            fetchMoreTokens();
        }
        return this.tokens.get(0);
    }

    @Override // org.yaml.snakeyaml.scanner.Scanner
    public Token getToken() {
        this.tokensTaken++;
        return this.tokens.remove(0);
    }

    private void addToken(Token token) {
        this.lastToken = token;
        this.tokens.add(token);
    }

    private void addToken(int index, Token token) {
        if (index == this.tokens.size()) {
            this.lastToken = token;
        }
        this.tokens.add(index, token);
    }

    private void addAllTokens(List<Token> tokens) {
        this.lastToken = tokens.get(tokens.size() - 1);
        this.tokens.addAll(tokens);
    }

    private boolean needMoreTokens() {
        if (this.done) {
            return false;
        }
        if (this.tokens.isEmpty()) {
            return true;
        }
        stalePossibleSimpleKeys();
        return nextPossibleSimpleKey() == this.tokensTaken;
    }

    private void fetchMoreTokens() {
        if (this.reader.getDocumentIndex() > this.loaderOptions.getCodePointLimit()) {
            throw new YAMLException("The incoming YAML document exceeds the limit: " + this.loaderOptions.getCodePointLimit() + " code points.");
        }
        scanToNextToken();
        stalePossibleSimpleKeys();
        unwindIndent(this.reader.getColumn());
        int c = this.reader.peek();
        switch (c) {
            case 0:
                fetchStreamEnd();
                return;
            case 33:
                fetchTag();
                return;
            case 34:
                fetchDouble();
                return;
            case 37:
                if (checkDirective()) {
                    fetchDirective();
                    return;
                }
                break;
            case 38:
                fetchAnchor();
                return;
            case 39:
                fetchSingle();
                return;
            case 42:
                fetchAlias();
                return;
            case 44:
                fetchFlowEntry();
                return;
            case 45:
                if (checkDocumentStart()) {
                    fetchDocumentStart();
                    return;
                } else if (checkBlockEntry()) {
                    fetchBlockEntry();
                    return;
                }
                break;
            case 46:
                if (checkDocumentEnd()) {
                    fetchDocumentEnd();
                    return;
                }
                break;
            case 58:
                if (checkValue()) {
                    fetchValue();
                    return;
                }
                break;
            case Opcodes.V18 /* 62 */:
                if (this.flowLevel == 0) {
                    fetchFolded();
                    return;
                }
                break;
            case 63:
                if (checkKey()) {
                    fetchKey();
                    return;
                }
                break;
            case 91:
                fetchFlowSequenceStart();
                return;
            case 93:
                fetchFlowSequenceEnd();
                return;
            case 123:
                fetchFlowMappingStart();
                return;
            case 124:
                if (this.flowLevel == 0) {
                    fetchLiteral();
                    return;
                }
                break;
            case 125:
                fetchFlowMappingEnd();
                return;
        }
        if (checkPlain()) {
            fetchPlain();
            return;
        }
        String chRepresentation = escapeChar(String.valueOf(Character.toChars(c)));
        if (c == 9) {
            chRepresentation = chRepresentation + "(TAB)";
        }
        String text = String.format("found character '%s' that cannot start any token. (Do not use %s for indentation)", chRepresentation, chRepresentation);
        throw new ScannerException("while scanning for the next token", null, text, this.reader.getMark());
    }

    private String escapeChar(String chRepresentation) {
        for (Character s : ESCAPE_REPLACEMENTS.keySet()) {
            String v = ESCAPE_REPLACEMENTS.get(s);
            if (v.equals(chRepresentation)) {
                return "\\" + s;
            }
        }
        return chRepresentation;
    }

    private int nextPossibleSimpleKey() {
        if (!this.possibleSimpleKeys.isEmpty()) {
            return this.possibleSimpleKeys.values().iterator().next().getTokenNumber();
        }
        return -1;
    }

    private void stalePossibleSimpleKeys() {
        if (!this.possibleSimpleKeys.isEmpty()) {
            Iterator<SimpleKey> iterator = this.possibleSimpleKeys.values().iterator();
            while (iterator.hasNext()) {
                SimpleKey key = iterator.next();
                if (key.getLine() != this.reader.getLine() || this.reader.getIndex() - key.getIndex() > 1024) {
                    if (key.isRequired()) {
                        throw new ScannerException("while scanning a simple key", key.getMark(), "could not find expected ':'", this.reader.getMark());
                    }
                    iterator.remove();
                }
            }
        }
    }

    private void savePossibleSimpleKey() {
        boolean required = this.flowLevel == 0 && this.indent == this.reader.getColumn();
        if (!this.allowSimpleKey && required) {
            throw new YAMLException("A simple key is required only if it is the first token in the current line");
        }
        if (this.allowSimpleKey) {
            removePossibleSimpleKey();
            int tokenNumber = this.tokensTaken + this.tokens.size();
            SimpleKey key = new SimpleKey(tokenNumber, required, this.reader.getIndex(), this.reader.getLine(), this.reader.getColumn(), this.reader.getMark());
            this.possibleSimpleKeys.put(Integer.valueOf(this.flowLevel), key);
        }
    }

    private void removePossibleSimpleKey() {
        SimpleKey key = this.possibleSimpleKeys.remove(Integer.valueOf(this.flowLevel));
        if (key != null && key.isRequired()) {
            throw new ScannerException("while scanning a simple key", key.getMark(), "could not find expected ':'", this.reader.getMark());
        }
    }

    private void unwindIndent(int col) {
        if (this.flowLevel != 0) {
            return;
        }
        while (this.indent > col) {
            Mark mark = this.reader.getMark();
            this.indent = this.indents.pop().intValue();
            addToken(new BlockEndToken(mark, mark));
        }
    }

    private boolean addIndent(int column) {
        if (this.indent < column) {
            this.indents.push(Integer.valueOf(this.indent));
            this.indent = column;
            return true;
        }
        return false;
    }

    private void fetchStreamStart() {
        Mark mark = this.reader.getMark();
        Token token = new StreamStartToken(mark, mark);
        addToken(token);
    }

    private void fetchStreamEnd() {
        unwindIndent(-1);
        removePossibleSimpleKey();
        this.allowSimpleKey = false;
        this.possibleSimpleKeys.clear();
        Mark mark = this.reader.getMark();
        Token token = new StreamEndToken(mark, mark);
        addToken(token);
        this.done = true;
    }

    private void fetchDirective() {
        unwindIndent(-1);
        removePossibleSimpleKey();
        this.allowSimpleKey = false;
        List<Token> tok = scanDirective();
        addAllTokens(tok);
    }

    private void fetchDocumentStart() {
        fetchDocumentIndicator(true);
    }

    private void fetchDocumentEnd() {
        fetchDocumentIndicator(false);
    }

    private void fetchDocumentIndicator(boolean isDocumentStart) {
        Token token;
        unwindIndent(-1);
        removePossibleSimpleKey();
        this.allowSimpleKey = false;
        Mark startMark = this.reader.getMark();
        this.reader.forward(3);
        Mark endMark = this.reader.getMark();
        if (isDocumentStart) {
            token = new DocumentStartToken(startMark, endMark);
        } else {
            token = new DocumentEndToken(startMark, endMark);
        }
        addToken(token);
    }

    private void fetchFlowSequenceStart() {
        fetchFlowCollectionStart(false);
    }

    private void fetchFlowMappingStart() {
        fetchFlowCollectionStart(true);
    }

    private void fetchFlowCollectionStart(boolean isMappingStart) {
        Token token;
        savePossibleSimpleKey();
        this.flowLevel++;
        this.allowSimpleKey = true;
        Mark startMark = this.reader.getMark();
        this.reader.forward(1);
        Mark endMark = this.reader.getMark();
        if (isMappingStart) {
            token = new FlowMappingStartToken(startMark, endMark);
        } else {
            token = new FlowSequenceStartToken(startMark, endMark);
        }
        addToken(token);
    }

    private void fetchFlowSequenceEnd() {
        fetchFlowCollectionEnd(false);
    }

    private void fetchFlowMappingEnd() {
        fetchFlowCollectionEnd(true);
    }

    private void fetchFlowCollectionEnd(boolean isMappingEnd) {
        Token token;
        removePossibleSimpleKey();
        this.flowLevel--;
        this.allowSimpleKey = false;
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        Mark endMark = this.reader.getMark();
        if (isMappingEnd) {
            token = new FlowMappingEndToken(startMark, endMark);
        } else {
            token = new FlowSequenceEndToken(startMark, endMark);
        }
        addToken(token);
    }

    private void fetchFlowEntry() {
        this.allowSimpleKey = true;
        removePossibleSimpleKey();
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        Mark endMark = this.reader.getMark();
        Token token = new FlowEntryToken(startMark, endMark);
        addToken(token);
    }

    private void fetchBlockEntry() {
        if (this.flowLevel == 0) {
            if (!this.allowSimpleKey) {
                throw new ScannerException(null, null, "sequence entries are not allowed here", this.reader.getMark());
            }
            if (addIndent(this.reader.getColumn())) {
                Mark mark = this.reader.getMark();
                addToken(new BlockSequenceStartToken(mark, mark));
            }
        }
        this.allowSimpleKey = true;
        removePossibleSimpleKey();
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        Mark endMark = this.reader.getMark();
        Token token = new BlockEntryToken(startMark, endMark);
        addToken(token);
    }

    private void fetchKey() {
        if (this.flowLevel == 0) {
            if (!this.allowSimpleKey) {
                throw new ScannerException(null, null, "mapping keys are not allowed here", this.reader.getMark());
            }
            if (addIndent(this.reader.getColumn())) {
                Mark mark = this.reader.getMark();
                addToken(new BlockMappingStartToken(mark, mark));
            }
        }
        this.allowSimpleKey = this.flowLevel == 0;
        removePossibleSimpleKey();
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        Mark endMark = this.reader.getMark();
        Token token = new KeyToken(startMark, endMark);
        addToken(token);
    }

    private void fetchValue() {
        SimpleKey key = this.possibleSimpleKeys.remove(Integer.valueOf(this.flowLevel));
        if (key != null) {
            addToken(key.getTokenNumber() - this.tokensTaken, new KeyToken(key.getMark(), key.getMark()));
            if (this.flowLevel == 0 && addIndent(key.getColumn())) {
                addToken(key.getTokenNumber() - this.tokensTaken, new BlockMappingStartToken(key.getMark(), key.getMark()));
            }
            this.allowSimpleKey = false;
        } else {
            if (this.flowLevel == 0 && !this.allowSimpleKey) {
                throw new ScannerException(null, null, "mapping values are not allowed here", this.reader.getMark());
            }
            if (this.flowLevel == 0 && addIndent(this.reader.getColumn())) {
                Mark mark = this.reader.getMark();
                addToken(new BlockMappingStartToken(mark, mark));
            }
            this.allowSimpleKey = this.flowLevel == 0;
            removePossibleSimpleKey();
        }
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        Mark endMark = this.reader.getMark();
        Token token = new ValueToken(startMark, endMark);
        addToken(token);
    }

    private void fetchAlias() {
        savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = scanAnchor(false);
        addToken(tok);
    }

    private void fetchAnchor() {
        savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = scanAnchor(true);
        addToken(tok);
    }

    private void fetchTag() {
        savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = scanTag();
        addToken(tok);
    }

    private void fetchLiteral() {
        fetchBlockScalar('|');
    }

    private void fetchFolded() {
        fetchBlockScalar('>');
    }

    private void fetchBlockScalar(char style) {
        this.allowSimpleKey = true;
        removePossibleSimpleKey();
        List<Token> tok = scanBlockScalar(style);
        addAllTokens(tok);
    }

    private void fetchSingle() {
        fetchFlowScalar('\'');
    }

    private void fetchDouble() {
        fetchFlowScalar('\"');
    }

    private void fetchFlowScalar(char style) {
        savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = scanFlowScalar(style);
        addToken(tok);
    }

    private void fetchPlain() {
        savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = scanPlain();
        addToken(tok);
    }

    private boolean checkDirective() {
        return this.reader.getColumn() == 0;
    }

    private boolean checkDocumentStart() {
        return this.reader.getColumn() == 0 && "---".equals(this.reader.prefix(3)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3));
    }

    private boolean checkDocumentEnd() {
        return this.reader.getColumn() == 0 && "...".equals(this.reader.prefix(3)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3));
    }

    private boolean checkBlockEntry() {
        return Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
    }

    private boolean checkKey() {
        if (this.flowLevel != 0) {
            return true;
        }
        return Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
    }

    private boolean checkValue() {
        if (this.flowLevel != 0) {
            return true;
        }
        return Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
    }

    private boolean checkPlain() {
        int c = this.reader.peek();
        return Constant.NULL_BL_T_LINEBR.hasNo(c, "-?:,[]{}#&*!|>'\"%@`") || (Constant.NULL_BL_T_LINEBR.hasNo(this.reader.peek(1)) && (c == 45 || (this.flowLevel == 0 && "?:".indexOf(c) != -1)));
    }

    private void scanToNextToken() {
        CommentType type;
        if (this.reader.getIndex() == 0 && this.reader.peek() == 65279) {
            this.reader.forward();
        }
        boolean found = false;
        int inlineStartColumn = -1;
        while (!found) {
            Mark startMark = this.reader.getMark();
            int columnBeforeComment = this.reader.getColumn();
            boolean commentSeen = false;
            int ff = 0;
            while (this.reader.peek(ff) == 32) {
                ff++;
            }
            if (ff > 0) {
                this.reader.forward(ff);
            }
            if (this.reader.peek() == 35) {
                commentSeen = true;
                if (columnBeforeComment != 0 && (this.lastToken == null || this.lastToken.getTokenId() != Token.ID.BlockEntry)) {
                    type = CommentType.IN_LINE;
                    inlineStartColumn = this.reader.getColumn();
                } else if (inlineStartColumn == this.reader.getColumn()) {
                    type = CommentType.IN_LINE;
                } else {
                    inlineStartColumn = -1;
                    type = CommentType.BLOCK;
                }
                CommentToken token = scanComment(type);
                if (this.parseComments) {
                    addToken(token);
                }
            }
            String breaks = scanLineBreak();
            if (breaks.length() != 0) {
                if (this.parseComments && !commentSeen && columnBeforeComment == 0) {
                    Mark endMark = this.reader.getMark();
                    addToken(new CommentToken(CommentType.BLANK_LINE, breaks, startMark, endMark));
                }
                if (this.flowLevel == 0) {
                    this.allowSimpleKey = true;
                }
            } else {
                found = true;
            }
        }
    }

    private CommentToken scanComment(CommentType type) {
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        int length = 0;
        while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(length))) {
            length++;
        }
        String value = this.reader.prefixForward(length);
        Mark endMark = this.reader.getMark();
        return new CommentToken(type, value, startMark, endMark);
    }

    private List<Token> scanDirective() {
        Mark endMark;
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        String name = scanDirectiveName(startMark);
        List<?> value = null;
        if ("YAML".equals(name)) {
            value = scanYamlDirectiveValue(startMark);
            endMark = this.reader.getMark();
        } else if ("TAG".equals(name)) {
            value = scanTagDirectiveValue(startMark);
            endMark = this.reader.getMark();
        } else {
            endMark = this.reader.getMark();
            int ff = 0;
            while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(ff))) {
                ff++;
            }
            if (ff > 0) {
                this.reader.forward(ff);
            }
        }
        CommentToken commentToken = scanDirectiveIgnoredLine(startMark);
        DirectiveToken token = new DirectiveToken(name, value, startMark, endMark);
        return makeTokenList(token, commentToken);
    }

    private String scanDirectiveName(Mark startMark) {
        int c;
        int length = 0;
        int peek = this.reader.peek(0);
        while (true) {
            c = peek;
            if (!Constant.ALPHA.has(c)) {
                break;
            }
            length++;
            peek = this.reader.peek(length);
        }
        if (length == 0) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException("while scanning a directive", startMark, "expected alphabetic or numeric character, but found " + s + "(" + c + ")", this.reader.getMark());
        }
        String value = this.reader.prefixForward(length);
        int c2 = this.reader.peek();
        if (Constant.NULL_BL_LINEBR.hasNo(c2)) {
            String s2 = String.valueOf(Character.toChars(c2));
            throw new ScannerException("while scanning a directive", startMark, "expected alphabetic or numeric character, but found " + s2 + "(" + c2 + ")", this.reader.getMark());
        }
        return value;
    }

    private List<Integer> scanYamlDirectiveValue(Mark startMark) {
        while (this.reader.peek() == 32) {
            this.reader.forward();
        }
        Integer major = scanYamlDirectiveNumber(startMark);
        int c = this.reader.peek();
        if (c != 46) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException("while scanning a directive", startMark, "expected a digit or '.', but found " + s + "(" + c + ")", this.reader.getMark());
        }
        this.reader.forward();
        Integer minor = scanYamlDirectiveNumber(startMark);
        int c2 = this.reader.peek();
        if (Constant.NULL_BL_LINEBR.hasNo(c2)) {
            String s2 = String.valueOf(Character.toChars(c2));
            throw new ScannerException("while scanning a directive", startMark, "expected a digit or ' ', but found " + s2 + "(" + c2 + ")", this.reader.getMark());
        }
        List<Integer> result = new ArrayList<>(2);
        result.add(major);
        result.add(minor);
        return result;
    }

    private Integer scanYamlDirectiveNumber(Mark startMark) {
        int c = this.reader.peek();
        if (!Character.isDigit(c)) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException("while scanning a directive", startMark, "expected a digit, but found " + s + "(" + c + ")", this.reader.getMark());
        }
        int length = 0;
        while (Character.isDigit(this.reader.peek(length))) {
            length++;
        }
        String number = this.reader.prefixForward(length);
        if (length > 3) {
            throw new ScannerException("while scanning a YAML directive", startMark, "found a number which cannot represent a valid version: " + number, this.reader.getMark());
        }
        Integer value = Integer.valueOf(Integer.parseInt(number));
        return value;
    }

    private List<String> scanTagDirectiveValue(Mark startMark) {
        while (this.reader.peek() == 32) {
            this.reader.forward();
        }
        String handle = scanTagDirectiveHandle(startMark);
        while (this.reader.peek() == 32) {
            this.reader.forward();
        }
        String prefix = scanTagDirectivePrefix(startMark);
        List<String> result = new ArrayList<>(2);
        result.add(handle);
        result.add(prefix);
        return result;
    }

    private String scanTagDirectiveHandle(Mark startMark) {
        String value = scanTagHandle("directive", startMark);
        int c = this.reader.peek();
        if (c != 32) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException("while scanning a directive", startMark, "expected ' ', but found " + s + "(" + c + ")", this.reader.getMark());
        }
        return value;
    }

    private String scanTagDirectivePrefix(Mark startMark) {
        String value = scanTagUri("directive", startMark);
        int c = this.reader.peek();
        if (Constant.NULL_BL_LINEBR.hasNo(c)) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException("while scanning a directive", startMark, "expected ' ', but found " + s + "(" + c + ")", this.reader.getMark());
        }
        return value;
    }

    private CommentToken scanDirectiveIgnoredLine(Mark startMark) {
        while (this.reader.peek() == 32) {
            this.reader.forward();
        }
        CommentToken commentToken = null;
        if (this.reader.peek() == 35) {
            CommentToken comment = scanComment(CommentType.IN_LINE);
            if (this.parseComments) {
                commentToken = comment;
            }
        }
        int c = this.reader.peek();
        String lineBreak = scanLineBreak();
        if (lineBreak.length() == 0 && c != 0) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException("while scanning a directive", startMark, "expected a comment or a line break, but found " + s + "(" + c + ")", this.reader.getMark());
        }
        return commentToken;
    }

    private Token scanAnchor(boolean isAnchor) {
        int c;
        Token tok;
        Mark startMark = this.reader.getMark();
        int indicator = this.reader.peek();
        String name = indicator == 42 ? "alias" : "anchor";
        this.reader.forward();
        int length = 0;
        int peek = this.reader.peek(0);
        while (true) {
            c = peek;
            if (!Constant.NULL_BL_T_LINEBR.hasNo(c, ":,[]{}/.*&")) {
                break;
            }
            length++;
            peek = this.reader.peek(length);
        }
        if (length == 0) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException("while scanning an " + name, startMark, "unexpected character found " + s + "(" + c + ")", this.reader.getMark());
        }
        String value = this.reader.prefixForward(length);
        int c2 = this.reader.peek();
        if (Constant.NULL_BL_T_LINEBR.hasNo(c2, "?:,]}%@`")) {
            String s2 = String.valueOf(Character.toChars(c2));
            throw new ScannerException("while scanning an " + name, startMark, "unexpected character found " + s2 + "(" + c2 + ")", this.reader.getMark());
        }
        Mark endMark = this.reader.getMark();
        if (isAnchor) {
            tok = new AnchorToken(value, startMark, endMark);
        } else {
            tok = new AliasToken(value, startMark, endMark);
        }
        return tok;
    }

    private Token scanTag() {
        String suffix;
        Mark startMark = this.reader.getMark();
        int c = this.reader.peek(1);
        String handle = null;
        if (c == 60) {
            this.reader.forward(2);
            suffix = scanTagUri("tag", startMark);
            int c2 = this.reader.peek();
            if (c2 != 62) {
                String s = String.valueOf(Character.toChars(c2));
                throw new ScannerException("while scanning a tag", startMark, "expected '>', but found '" + s + "' (" + c2 + ")", this.reader.getMark());
            }
            this.reader.forward();
        } else if (Constant.NULL_BL_T_LINEBR.has(c)) {
            suffix = "!";
            this.reader.forward();
        } else {
            int length = 1;
            boolean useHandle = false;
            while (true) {
                if (!Constant.NULL_BL_LINEBR.hasNo(c)) {
                    break;
                }
                if (c == 33) {
                    useHandle = true;
                    break;
                }
                length++;
                c = this.reader.peek(length);
            }
            if (useHandle) {
                handle = scanTagHandle("tag", startMark);
            } else {
                handle = "!";
                this.reader.forward();
            }
            suffix = scanTagUri("tag", startMark);
        }
        int c3 = this.reader.peek();
        if (Constant.NULL_BL_LINEBR.hasNo(c3)) {
            String s2 = String.valueOf(Character.toChars(c3));
            throw new ScannerException("while scanning a tag", startMark, "expected ' ', but found '" + s2 + "' (" + c3 + ")", this.reader.getMark());
        }
        TagTuple value = new TagTuple(handle, suffix);
        Mark endMark = this.reader.getMark();
        return new TagToken(value, startMark, endMark);
    }

    private List<Token> scanBlockScalar(char style) {
        int indent;
        String breaks;
        Mark endMark;
        boolean folded = style == '>';
        StringBuilder chunks = new StringBuilder();
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        Chomping chompi = scanBlockScalarIndicators(startMark);
        int increment = chompi.getIncrement();
        CommentToken commentToken = scanBlockScalarIgnoredLine(startMark);
        int minIndent = this.indent + 1;
        if (minIndent < 1) {
            minIndent = 1;
        }
        if (increment == -1) {
            Object[] brme = scanBlockScalarIndentation();
            breaks = (String) brme[0];
            int maxIndent = ((Integer) brme[1]).intValue();
            endMark = (Mark) brme[2];
            indent = Math.max(minIndent, maxIndent);
        } else {
            indent = (minIndent + increment) - 1;
            Object[] brme2 = scanBlockScalarBreaks(indent);
            breaks = (String) brme2[0];
            endMark = (Mark) brme2[1];
        }
        String lineBreak = "";
        while (this.reader.getColumn() == indent && this.reader.peek() != 0) {
            chunks.append(breaks);
            boolean leadingNonSpace = " \t".indexOf(this.reader.peek()) == -1;
            int length = 0;
            while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(length))) {
                length++;
            }
            chunks.append(this.reader.prefixForward(length));
            lineBreak = scanLineBreak();
            Object[] brme3 = scanBlockScalarBreaks(indent);
            breaks = (String) brme3[0];
            endMark = (Mark) brme3[1];
            if (this.reader.getColumn() != indent || this.reader.peek() == 0) {
                break;
            }
            if (folded && "\n".equals(lineBreak) && leadingNonSpace && " \t".indexOf(this.reader.peek()) == -1) {
                if (breaks.length() == 0) {
                    chunks.append(" ");
                }
            } else {
                chunks.append(lineBreak);
            }
        }
        if (chompi.chompTailIsNotFalse()) {
            chunks.append(lineBreak);
        }
        if (chompi.chompTailIsTrue()) {
            chunks.append(breaks);
        }
        ScalarToken scalarToken = new ScalarToken(chunks.toString(), false, startMark, endMark, DumperOptions.ScalarStyle.createStyle(Character.valueOf(style)));
        return makeTokenList(commentToken, scalarToken);
    }

    private Chomping scanBlockScalarIndicators(Mark startMark) {
        Boolean chomping = null;
        int increment = -1;
        int c = this.reader.peek();
        if (c == 45 || c == 43) {
            if (c == 43) {
                chomping = Boolean.TRUE;
            } else {
                chomping = Boolean.FALSE;
            }
            this.reader.forward();
            int c2 = this.reader.peek();
            if (Character.isDigit(c2)) {
                String s = String.valueOf(Character.toChars(c2));
                increment = Integer.parseInt(s);
                if (increment == 0) {
                    throw new ScannerException("while scanning a block scalar", startMark, "expected indentation indicator in the range 1-9, but found 0", this.reader.getMark());
                }
                this.reader.forward();
            }
        } else if (Character.isDigit(c)) {
            String s2 = String.valueOf(Character.toChars(c));
            increment = Integer.parseInt(s2);
            if (increment == 0) {
                throw new ScannerException("while scanning a block scalar", startMark, "expected indentation indicator in the range 1-9, but found 0", this.reader.getMark());
            }
            this.reader.forward();
            int c3 = this.reader.peek();
            if (c3 == 45 || c3 == 43) {
                if (c3 == 43) {
                    chomping = Boolean.TRUE;
                } else {
                    chomping = Boolean.FALSE;
                }
                this.reader.forward();
            }
        }
        int c4 = this.reader.peek();
        if (Constant.NULL_BL_LINEBR.hasNo(c4)) {
            String s3 = String.valueOf(Character.toChars(c4));
            throw new ScannerException("while scanning a block scalar", startMark, "expected chomping or indentation indicators, but found " + s3 + "(" + c4 + ")", this.reader.getMark());
        }
        return new Chomping(chomping, increment);
    }

    private CommentToken scanBlockScalarIgnoredLine(Mark startMark) {
        while (this.reader.peek() == 32) {
            this.reader.forward();
        }
        CommentToken commentToken = null;
        if (this.reader.peek() == 35) {
            commentToken = scanComment(CommentType.IN_LINE);
        }
        int c = this.reader.peek();
        String lineBreak = scanLineBreak();
        if (lineBreak.length() == 0 && c != 0) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException("while scanning a block scalar", startMark, "expected a comment or a line break, but found " + s + "(" + c + ")", this.reader.getMark());
        }
        return commentToken;
    }

    private Object[] scanBlockScalarIndentation() {
        StringBuilder chunks = new StringBuilder();
        int maxIndent = 0;
        Mark endMark = this.reader.getMark();
        while (Constant.LINEBR.has(this.reader.peek(), " \r")) {
            if (this.reader.peek() != 32) {
                chunks.append(scanLineBreak());
                endMark = this.reader.getMark();
            } else {
                this.reader.forward();
                if (this.reader.getColumn() > maxIndent) {
                    maxIndent = this.reader.getColumn();
                }
            }
        }
        return new Object[]{chunks.toString(), Integer.valueOf(maxIndent), endMark};
    }

    private Object[] scanBlockScalarBreaks(int indent) {
        StringBuilder chunks = new StringBuilder();
        Mark endMark = this.reader.getMark();
        for (int col = this.reader.getColumn(); col < indent && this.reader.peek() == 32; col++) {
            this.reader.forward();
        }
        while (true) {
            String lineBreak = scanLineBreak();
            if (lineBreak.length() != 0) {
                chunks.append(lineBreak);
                endMark = this.reader.getMark();
                for (int col2 = this.reader.getColumn(); col2 < indent && this.reader.peek() == 32; col2++) {
                    this.reader.forward();
                }
            } else {
                return new Object[]{chunks.toString(), endMark};
            }
        }
    }

    private Token scanFlowScalar(char style) {
        boolean _double = style == '\"';
        StringBuilder chunks = new StringBuilder();
        Mark startMark = this.reader.getMark();
        int quote = this.reader.peek();
        this.reader.forward();
        chunks.append(scanFlowScalarNonSpaces(_double, startMark));
        while (this.reader.peek() != quote) {
            chunks.append(scanFlowScalarSpaces(startMark));
            chunks.append(scanFlowScalarNonSpaces(_double, startMark));
        }
        this.reader.forward();
        Mark endMark = this.reader.getMark();
        return new ScalarToken(chunks.toString(), false, startMark, endMark, DumperOptions.ScalarStyle.createStyle(Character.valueOf(style)));
    }

    /* JADX WARN: Code restructure failed: missing block: B:73:0x0222, code lost:            return r0.toString();     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.lang.String scanFlowScalarNonSpaces(boolean r8, org.yaml.snakeyaml.error.Mark r9) {
        /*
            Method dump skipped, instructions count: 550
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.yaml.snakeyaml.scanner.ScannerImpl.scanFlowScalarNonSpaces(boolean, org.yaml.snakeyaml.error.Mark):java.lang.String");
    }

    private String scanFlowScalarSpaces(Mark startMark) {
        StringBuilder chunks = new StringBuilder();
        int length = 0;
        while (" \t".indexOf(this.reader.peek(length)) != -1) {
            length++;
        }
        String whitespaces = this.reader.prefixForward(length);
        int c = this.reader.peek();
        if (c == 0) {
            throw new ScannerException("while scanning a quoted scalar", startMark, "found unexpected end of stream", this.reader.getMark());
        }
        String lineBreak = scanLineBreak();
        if (lineBreak.length() != 0) {
            String breaks = scanFlowScalarBreaks(startMark);
            if (!"\n".equals(lineBreak)) {
                chunks.append(lineBreak);
            } else if (breaks.length() == 0) {
                chunks.append(" ");
            }
            chunks.append(breaks);
        } else {
            chunks.append(whitespaces);
        }
        return chunks.toString();
    }

    private String scanFlowScalarBreaks(Mark startMark) {
        StringBuilder chunks = new StringBuilder();
        while (true) {
            String prefix = this.reader.prefix(3);
            if (("---".equals(prefix) || "...".equals(prefix)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3))) {
                throw new ScannerException("while scanning a quoted scalar", startMark, "found unexpected document separator", this.reader.getMark());
            }
            while (" \t".indexOf(this.reader.peek()) != -1) {
                this.reader.forward();
            }
            String lineBreak = scanLineBreak();
            if (lineBreak.length() != 0) {
                chunks.append(lineBreak);
            } else {
                return chunks.toString();
            }
        }
    }

    private Token scanPlain() {
        StringBuilder chunks = new StringBuilder();
        Mark startMark = this.reader.getMark();
        Mark endMark = startMark;
        int indent = this.indent + 1;
        String spaces = "";
        while (true) {
            int length = 0;
            if (this.reader.peek() != 35) {
                while (true) {
                    int c = this.reader.peek(length);
                    if (!Constant.NULL_BL_T_LINEBR.has(c)) {
                        if (c == 58) {
                            if (Constant.NULL_BL_T_LINEBR.has(this.reader.peek(length + 1), this.flowLevel != 0 ? ",[]{}" : "")) {
                                break;
                            }
                        }
                        if (this.flowLevel != 0 && ",?[]{}".indexOf(c) != -1) {
                            break;
                        }
                        length++;
                    } else {
                        break;
                    }
                }
                if (length != 0) {
                    this.allowSimpleKey = false;
                    chunks.append(spaces);
                    chunks.append(this.reader.prefixForward(length));
                    endMark = this.reader.getMark();
                    spaces = scanPlainSpaces();
                    if (spaces.length() == 0 || this.reader.peek() == 35 || (this.flowLevel == 0 && this.reader.getColumn() < indent)) {
                        break;
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return new ScalarToken(chunks.toString(), startMark, endMark, true);
    }

    private boolean atEndOfPlain() {
        int wsLength = 0;
        int wsColumn = this.reader.getColumn();
        while (true) {
            int c = this.reader.peek(wsLength);
            if (c == 0 || !Constant.NULL_BL_T_LINEBR.has(c)) {
                break;
            }
            wsLength++;
            if (!Constant.LINEBR.has(c) && ((c != 13 || this.reader.peek(wsLength + 1) != 10) && c != 65279)) {
                wsColumn++;
            } else {
                wsColumn = 0;
            }
        }
        if (this.reader.peek(wsLength) == 35 || this.reader.peek(wsLength + 1) == 0) {
            return true;
        }
        if (this.flowLevel == 0 && wsColumn < this.indent) {
            return true;
        }
        if (this.flowLevel == 0) {
            int extra = 1;
            while (true) {
                int c2 = this.reader.peek(wsLength + extra);
                if (c2 != 0 && !Constant.NULL_BL_T_LINEBR.has(c2)) {
                    if (c2 != 58 || !Constant.NULL_BL_T_LINEBR.has(this.reader.peek(wsLength + extra + 1))) {
                        extra++;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private String scanPlainSpaces() {
        int length = 0;
        while (true) {
            if (this.reader.peek(length) != 32 && this.reader.peek(length) != 9) {
                break;
            }
            length++;
        }
        String whitespaces = this.reader.prefixForward(length);
        String lineBreak = scanLineBreak();
        if (lineBreak.length() != 0) {
            this.allowSimpleKey = true;
            String prefix = this.reader.prefix(3);
            if (!"---".equals(prefix)) {
                if ("...".equals(prefix) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3))) {
                    return "";
                }
                if (this.parseComments && atEndOfPlain()) {
                    return "";
                }
                StringBuilder breaks = new StringBuilder();
                while (true) {
                    if (this.reader.peek() == 32) {
                        this.reader.forward();
                    } else {
                        String lb = scanLineBreak();
                        if (lb.length() != 0) {
                            breaks.append(lb);
                            String prefix2 = this.reader.prefix(3);
                            if (!"---".equals(prefix2)) {
                                if ("...".equals(prefix2) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3))) {
                                    return "";
                                }
                            } else {
                                return "";
                            }
                        } else {
                            if (!"\n".equals(lineBreak)) {
                                return lineBreak + ((Object) breaks);
                            }
                            if (breaks.length() == 0) {
                                return " ";
                            }
                            return breaks.toString();
                        }
                    }
                }
            } else {
                return "";
            }
        } else {
            return whitespaces;
        }
    }

    private String scanTagHandle(String name, Mark startMark) {
        int c = this.reader.peek();
        if (c != 33) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException("while scanning a " + name, startMark, "expected '!', but found " + s + "(" + c + ")", this.reader.getMark());
        }
        int length = 1;
        int c2 = this.reader.peek(1);
        if (c2 != 32) {
            while (Constant.ALPHA.has(c2)) {
                length++;
                c2 = this.reader.peek(length);
            }
            if (c2 != 33) {
                this.reader.forward(length);
                String s2 = String.valueOf(Character.toChars(c2));
                throw new ScannerException("while scanning a " + name, startMark, "expected '!', but found " + s2 + "(" + c2 + ")", this.reader.getMark());
            }
            length++;
        }
        String value = this.reader.prefixForward(length);
        return value;
    }

    private String scanTagUri(String name, Mark startMark) {
        int c;
        StringBuilder chunks = new StringBuilder();
        int length = 0;
        int peek = this.reader.peek(0);
        while (true) {
            c = peek;
            if (!Constant.URI_CHARS.has(c)) {
                break;
            }
            if (c == 37) {
                chunks.append(this.reader.prefixForward(length));
                length = 0;
                chunks.append(scanUriEscapes(name, startMark));
            } else {
                length++;
            }
            peek = this.reader.peek(length);
        }
        if (length != 0) {
            chunks.append(this.reader.prefixForward(length));
        }
        if (chunks.length() == 0) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException("while scanning a " + name, startMark, "expected URI, but found " + s + "(" + c + ")", this.reader.getMark());
        }
        return chunks.toString();
    }

    private String scanUriEscapes(String name, Mark startMark) {
        int length = 1;
        while (this.reader.peek(length * 3) == 37) {
            length++;
        }
        Mark beginningMark = this.reader.getMark();
        ByteBuffer buff = ByteBuffer.allocate(length);
        while (this.reader.peek() == 37) {
            this.reader.forward();
            try {
                byte code = (byte) Integer.parseInt(this.reader.prefix(2), 16);
                buff.put(code);
                this.reader.forward(2);
            } catch (NumberFormatException e) {
                int c1 = this.reader.peek();
                String s1 = String.valueOf(Character.toChars(c1));
                int c2 = this.reader.peek(1);
                String s2 = String.valueOf(Character.toChars(c2));
                throw new ScannerException("while scanning a " + name, startMark, "expected URI escape sequence of 2 hexadecimal numbers, but found " + s1 + "(" + c1 + ") and " + s2 + "(" + c2 + ")", this.reader.getMark());
            }
        }
        buff.flip();
        try {
            return UriEncoder.decode(buff);
        } catch (CharacterCodingException e2) {
            throw new ScannerException("while scanning a " + name, startMark, "expected URI in UTF-8: " + e2.getMessage(), beginningMark);
        }
    }

    private String scanLineBreak() {
        int c = this.reader.peek();
        if (c == 13 || c == 10 || c == 133) {
            if (c == 13 && 10 == this.reader.peek(1)) {
                this.reader.forward(2);
                return "\n";
            }
            this.reader.forward();
            return "\n";
        }
        if (c == 8232 || c == 8233) {
            this.reader.forward();
            return String.valueOf(Character.toChars(c));
        }
        return "";
    }

    private List<Token> makeTokenList(Token... tokens) {
        List<Token> tokenList = new ArrayList<>();
        for (int ix = 0; ix < tokens.length; ix++) {
            if (tokens[ix] != null && (this.parseComments || !(tokens[ix] instanceof CommentToken))) {
                tokenList.add(tokens[ix]);
            }
        }
        return tokenList;
    }

    @Override // org.yaml.snakeyaml.scanner.Scanner
    public void resetDocumentIndex() {
        this.reader.resetDocumentIndex();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/scanner/ScannerImpl$Chomping.class */
    public static class Chomping {
        private final Boolean value;
        private final int increment;

        public Chomping(Boolean value, int increment) {
            this.value = value;
            this.increment = increment;
        }

        public boolean chompTailIsNotFalse() {
            return this.value == null || this.value.booleanValue();
        }

        public boolean chompTailIsTrue() {
            return this.value != null && this.value.booleanValue();
        }

        public int getIncrement() {
            return this.increment;
        }
    }
}
