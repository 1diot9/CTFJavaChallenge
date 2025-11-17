package org.h2.bnf;

import java.util.HashMap;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/bnf/RuleOptional.class */
public class RuleOptional implements Rule {
    private final Rule rule;
    private boolean mapSet;

    public RuleOptional(Rule rule) {
        this.rule = rule;
    }

    @Override // org.h2.bnf.Rule
    public void accept(BnfVisitor bnfVisitor) {
        if (this.rule instanceof RuleList) {
            RuleList ruleList = (RuleList) this.rule;
            if (ruleList.or) {
                bnfVisitor.visitRuleOptional(ruleList.list);
                return;
            }
        }
        bnfVisitor.visitRuleOptional(this.rule);
    }

    @Override // org.h2.bnf.Rule
    public void setLinks(HashMap<String, RuleHead> hashMap) {
        if (!this.mapSet) {
            this.rule.setLinks(hashMap);
            this.mapSet = true;
        }
    }

    @Override // org.h2.bnf.Rule
    public boolean autoComplete(Sentence sentence) {
        sentence.stopIfRequired();
        this.rule.autoComplete(sentence);
        return true;
    }

    public String toString() {
        return '[' + this.rule.toString() + ']';
    }
}
