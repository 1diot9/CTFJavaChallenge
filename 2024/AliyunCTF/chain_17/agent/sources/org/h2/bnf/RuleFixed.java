package org.h2.bnf;

import java.util.HashMap;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/bnf/RuleFixed.class */
public class RuleFixed implements Rule {
    public static final int YMD = 0;
    public static final int HMS = 1;
    public static final int NANOS = 2;
    public static final int ANY_EXCEPT_SINGLE_QUOTE = 3;
    public static final int ANY_EXCEPT_DOUBLE_QUOTE = 4;
    public static final int ANY_UNTIL_EOL = 5;
    public static final int ANY_UNTIL_END = 6;
    public static final int ANY_WORD = 7;
    public static final int ANY_EXCEPT_2_DOLLAR = 8;
    public static final int HEX_START = 9;
    public static final int OCTAL_START = 10;
    public static final int BINARY_START = 11;
    public static final int CONCAT = 12;
    public static final int AZ_UNDERSCORE = 13;
    public static final int AF = 14;
    public static final int DIGIT = 15;
    public static final int OPEN_BRACKET = 16;
    public static final int CLOSE_BRACKET = 17;
    public static final int JSON_TEXT = 18;
    private final int type;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RuleFixed(int i) {
        this.type = i;
    }

    @Override // org.h2.bnf.Rule
    public void accept(BnfVisitor bnfVisitor) {
        bnfVisitor.visitRuleFixed(this.type);
    }

    @Override // org.h2.bnf.Rule
    public void setLinks(HashMap<String, RuleHead> hashMap) {
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0012. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0405  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0421 A[RETURN] */
    @Override // org.h2.bnf.Rule
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean autoComplete(org.h2.bnf.Sentence r6) {
        /*
            Method dump skipped, instructions count: 1059
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.bnf.RuleFixed.autoComplete(org.h2.bnf.Sentence):boolean");
    }

    public String toString() {
        return "#" + this.type;
    }
}
