package org.h2.bnf;

import java.util.HashMap;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/bnf/RuleRepeat.class */
public class RuleRepeat implements Rule {
    private final Rule rule;
    private final boolean comma;

    public RuleRepeat(Rule rule, boolean z) {
        this.rule = rule;
        this.comma = z;
    }

    @Override // org.h2.bnf.Rule
    public void accept(BnfVisitor bnfVisitor) {
        bnfVisitor.visitRuleRepeat(this.comma, this.rule);
    }

    @Override // org.h2.bnf.Rule
    public void setLinks(HashMap<String, RuleHead> hashMap) {
    }

    @Override // org.h2.bnf.Rule
    public boolean autoComplete(Sentence sentence) {
        sentence.stopIfRequired();
        do {
        } while (this.rule.autoComplete(sentence));
        String query = sentence.getQuery();
        while (true) {
            String str = query;
            if (Bnf.startWithSpace(str)) {
                query = str.substring(1);
            } else {
                sentence.setQuery(str);
                return true;
            }
        }
    }

    public String toString() {
        return this.comma ? ", ..." : " ...";
    }
}
