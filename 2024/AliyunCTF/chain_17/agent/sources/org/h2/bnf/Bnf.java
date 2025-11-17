package org.h2.bnf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.h2.bnf.context.DbContextRule;
import org.h2.command.dml.Help;
import org.h2.tools.Csv;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/bnf/Bnf.class */
public class Bnf {
    private final HashMap<String, RuleHead> ruleMap = new HashMap<>();
    private String syntax;
    private String currentToken;
    private String[] tokens;
    private char firstChar;
    private int index;
    private Rule lastRepeat;
    private ArrayList<RuleHead> statements;
    private String currentTopic;

    public static Bnf getInstance(Reader reader) throws SQLException, IOException {
        Bnf bnf = new Bnf();
        if (reader == null) {
            reader = new InputStreamReader(new ByteArrayInputStream(Utils.getResource("/org/h2/res/help.csv")), StandardCharsets.UTF_8);
        }
        bnf.parse(reader);
        return bnf;
    }

    public void addAlias(String str, String str2) {
        this.ruleMap.put(str, this.ruleMap.get(str2));
    }

    private void addFixedRule(String str, int i) {
        addRule(str, "Fixed", new RuleFixed(i));
    }

    private RuleHead addRule(String str, String str2, Rule rule) {
        RuleHead ruleHead = new RuleHead(str2, str, rule);
        if (this.ruleMap.putIfAbsent(StringUtils.toLowerEnglish(str.trim().replace(' ', '_')), ruleHead) != null) {
            throw new AssertionError("already exists: " + str);
        }
        return ruleHead;
    }

    private void parse(Reader reader) throws SQLException, IOException {
        Rule rule = null;
        this.statements = new ArrayList<>();
        Csv csv = new Csv();
        csv.setLineCommentCharacter('#');
        ResultSet read = csv.read(reader, null);
        while (read.next()) {
            String trim = read.getString("SECTION").trim();
            if (!trim.startsWith("System")) {
                String string = read.getString("TOPIC");
                this.syntax = Help.stripAnnotationsFromSyntax(read.getString("SYNTAX"));
                this.currentTopic = trim;
                this.tokens = tokenize();
                this.index = 0;
                Rule parseRule = parseRule();
                if (trim.startsWith("Command")) {
                    parseRule = new RuleList(parseRule, new RuleElement(";\n\n", this.currentTopic), false);
                }
                RuleHead addRule = addRule(string, trim, parseRule);
                if (trim.startsWith("Function")) {
                    if (rule == null) {
                        rule = parseRule;
                    } else {
                        rule = new RuleList(parseRule, rule, true);
                    }
                } else if (trim.startsWith("Commands")) {
                    this.statements.add(addRule);
                }
            }
        }
        addRule("@func@", "Function", rule);
        addFixedRule("@ymd@", 0);
        addFixedRule("@hms@", 1);
        addFixedRule("@nanos@", 2);
        addFixedRule("anything_except_single_quote", 3);
        addFixedRule("single_character", 3);
        addFixedRule("anything_except_double_quote", 4);
        addFixedRule("anything_until_end_of_line", 5);
        addFixedRule("anything_until_comment_start_or_end", 6);
        addFixedRule("anything_except_two_dollar_signs", 8);
        addFixedRule("anything", 7);
        addFixedRule("@hex_start@", 9);
        addFixedRule("@octal_start@", 10);
        addFixedRule("@binary_start@", 11);
        addFixedRule("@concat@", 12);
        addFixedRule("@az_@", 13);
        addFixedRule("@af@", 14);
        addFixedRule("@digit@", 15);
        addFixedRule("@open_bracket@", 16);
        addFixedRule("@close_bracket@", 17);
        addFixedRule("json_text", 18);
        Rule rule2 = this.ruleMap.get("digit").getRule();
        this.ruleMap.get("number").setRule(new RuleList(rule2, new RuleOptional(new RuleRepeat(rule2, false)), false));
    }

    public void visit(BnfVisitor bnfVisitor, String str) {
        this.syntax = str;
        this.tokens = tokenize();
        this.index = 0;
        Rule parseRule = parseRule();
        parseRule.setLinks(this.ruleMap);
        parseRule.accept(bnfVisitor);
    }

    public static boolean startWithSpace(String str) {
        return str.length() > 0 && Character.isWhitespace(str.charAt(0));
    }

    public static String getRuleMapKey(String str) {
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (Character.isUpperCase(charAt)) {
                sb.append('_').append(Character.toLowerCase(charAt));
            } else {
                sb.append(charAt);
            }
        }
        return sb.toString();
    }

    public RuleHead getRuleHead(String str) {
        return this.ruleMap.get(str);
    }

    private Rule parseRule() {
        read();
        return parseOr();
    }

    private Rule parseOr() {
        Rule parseList = parseList();
        if (this.firstChar == '|') {
            read();
            parseList = new RuleList(parseList, parseOr(), true);
        }
        this.lastRepeat = parseList;
        return parseList;
    }

    private Rule parseList() {
        Rule parseToken = parseToken();
        if (this.firstChar != '|' && this.firstChar != ']' && this.firstChar != '}' && this.firstChar != 0) {
            parseToken = new RuleList(parseToken, parseList(), false);
        }
        this.lastRepeat = parseToken;
        return parseToken;
    }

    private RuleExtension parseExtension(boolean z) {
        Rule parseOr;
        read();
        if (this.firstChar == '[') {
            read();
            parseOr = new RuleOptional(parseOr());
            if (this.firstChar != ']') {
                throw new AssertionError("expected ], got " + this.currentToken + " syntax:" + this.syntax);
            }
        } else if (this.firstChar == '{') {
            read();
            parseOr = parseOr();
            if (this.firstChar != '}') {
                throw new AssertionError("expected }, got " + this.currentToken + " syntax:" + this.syntax);
            }
        } else {
            parseOr = parseOr();
        }
        return new RuleExtension(parseOr, z);
    }

    private Rule parseToken() {
        Rule ruleElement;
        if ((this.firstChar >= 'A' && this.firstChar <= 'Z') || (this.firstChar >= 'a' && this.firstChar <= 'z')) {
            ruleElement = new RuleElement(this.currentToken, this.currentTopic);
        } else if (this.firstChar == '[') {
            read();
            ruleElement = new RuleOptional(parseOr());
            if (this.firstChar != ']') {
                throw new AssertionError("expected ], got " + this.currentToken + " syntax:" + this.syntax);
            }
        } else if (this.firstChar == '{') {
            read();
            ruleElement = parseOr();
            if (this.firstChar != '}') {
                throw new AssertionError("expected }, got " + this.currentToken + " syntax:" + this.syntax);
            }
        } else if (this.firstChar == '@') {
            if ("@commaDots@".equals(this.currentToken)) {
                ruleElement = new RuleRepeat(new RuleList(new RuleElement(",", this.currentTopic), this.lastRepeat, false), true);
            } else if ("@dots@".equals(this.currentToken)) {
                ruleElement = new RuleRepeat(this.lastRepeat, false);
            } else if ("@c@".equals(this.currentToken)) {
                ruleElement = parseExtension(true);
            } else if ("@h2@".equals(this.currentToken)) {
                ruleElement = parseExtension(false);
            } else {
                ruleElement = new RuleElement(this.currentToken, this.currentTopic);
            }
        } else {
            ruleElement = new RuleElement(this.currentToken, this.currentTopic);
        }
        this.lastRepeat = ruleElement;
        read();
        return ruleElement;
    }

    private void read() {
        if (this.index < this.tokens.length) {
            String[] strArr = this.tokens;
            int i = this.index;
            this.index = i + 1;
            this.currentToken = strArr[i];
            this.firstChar = this.currentToken.charAt(0);
            return;
        }
        this.currentToken = "";
        this.firstChar = (char) 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.index; i++) {
            sb.append(this.tokens[i]).append(' ');
        }
        sb.append("[*]");
        for (int i2 = this.index; i2 < this.tokens.length; i2++) {
            sb.append(' ').append(this.tokens[i2]);
        }
        return sb.toString();
    }

    private String[] tokenize() {
        ArrayList arrayList = new ArrayList();
        this.syntax = StringUtils.replaceAll(this.syntax, "yyyy-MM-dd", "@ymd@");
        this.syntax = StringUtils.replaceAll(this.syntax, "hh:mm:ss", "@hms@");
        this.syntax = StringUtils.replaceAll(this.syntax, "hh:mm", "@hms@");
        this.syntax = StringUtils.replaceAll(this.syntax, "mm:ss", "@hms@");
        this.syntax = StringUtils.replaceAll(this.syntax, "nnnnnnnnn", "@nanos@");
        this.syntax = StringUtils.replaceAll(this.syntax, "function", "@func@");
        this.syntax = StringUtils.replaceAll(this.syntax, "0x", "@hexStart@");
        this.syntax = StringUtils.replaceAll(this.syntax, "0o", "@octalStart@");
        this.syntax = StringUtils.replaceAll(this.syntax, "0b", "@binaryStart@");
        this.syntax = StringUtils.replaceAll(this.syntax, ",...", "@commaDots@");
        this.syntax = StringUtils.replaceAll(this.syntax, "...", "@dots@");
        this.syntax = StringUtils.replaceAll(this.syntax, "||", "@concat@");
        this.syntax = StringUtils.replaceAll(this.syntax, "a-z|_", "@az_@");
        this.syntax = StringUtils.replaceAll(this.syntax, "A-Z|_", "@az_@");
        this.syntax = StringUtils.replaceAll(this.syntax, "A-F", "@af@");
        this.syntax = StringUtils.replaceAll(this.syntax, "0-9", "@digit@");
        this.syntax = StringUtils.replaceAll(this.syntax, "'['", "@openBracket@");
        this.syntax = StringUtils.replaceAll(this.syntax, "']'", "@closeBracket@");
        StringTokenizer tokenizer = getTokenizer(this.syntax);
        while (tokenizer.hasMoreTokens()) {
            String cache = StringUtils.cache(tokenizer.nextToken());
            if (cache.length() != 1 || " \r\n".indexOf(cache.charAt(0)) < 0) {
                arrayList.add(cache);
            }
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public HashMap<String, String> getNextTokenList(String str) {
        Sentence sentence = new Sentence();
        sentence.setQuery(str);
        try {
            Iterator<RuleHead> it = this.statements.iterator();
            while (it.hasNext()) {
                RuleHead next = it.next();
                if (next.getSection().startsWith("Commands")) {
                    sentence.start();
                    if (next.getRule().autoComplete(sentence)) {
                        break;
                    }
                }
            }
        } catch (IllegalStateException e) {
        }
        return sentence.getNext();
    }

    public void linkStatements() {
        Iterator<RuleHead> it = this.ruleMap.values().iterator();
        while (it.hasNext()) {
            it.next().getRule().setLinks(this.ruleMap);
        }
    }

    public void updateTopic(String str, DbContextRule dbContextRule) {
        String lowerEnglish = StringUtils.toLowerEnglish(str);
        RuleHead ruleHead = this.ruleMap.get(lowerEnglish);
        if (ruleHead == null) {
            RuleHead ruleHead2 = new RuleHead("db", lowerEnglish, dbContextRule);
            this.ruleMap.put(lowerEnglish, ruleHead2);
            this.statements.add(ruleHead2);
            return;
        }
        ruleHead.setRule(dbContextRule);
    }

    public ArrayList<RuleHead> getStatements() {
        return this.statements;
    }

    public static StringTokenizer getTokenizer(String str) {
        return new StringTokenizer(str, " [](){}|.,\r\n<>:-+*/=\"!'$", true);
    }
}
