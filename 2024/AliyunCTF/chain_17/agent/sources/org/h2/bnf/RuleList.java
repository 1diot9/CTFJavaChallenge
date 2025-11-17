package org.h2.bnf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/bnf/RuleList.class */
public class RuleList implements Rule {
    final boolean or;
    final ArrayList<Rule> list = Utils.newSmallArrayList();
    private boolean mapSet;

    public RuleList(Rule rule, Rule rule2, boolean z) {
        if ((rule instanceof RuleList) && ((RuleList) rule).or == z) {
            this.list.addAll(((RuleList) rule).list);
        } else {
            this.list.add(rule);
        }
        if ((rule2 instanceof RuleList) && ((RuleList) rule2).or == z) {
            this.list.addAll(((RuleList) rule2).list);
        } else {
            this.list.add(rule2);
        }
        this.or = z;
    }

    @Override // org.h2.bnf.Rule
    public void accept(BnfVisitor bnfVisitor) {
        bnfVisitor.visitRuleList(this.or, this.list);
    }

    @Override // org.h2.bnf.Rule
    public void setLinks(HashMap<String, RuleHead> hashMap) {
        if (!this.mapSet) {
            Iterator<Rule> it = this.list.iterator();
            while (it.hasNext()) {
                it.next().setLinks(hashMap);
            }
            this.mapSet = true;
        }
    }

    @Override // org.h2.bnf.Rule
    public boolean autoComplete(Sentence sentence) {
        sentence.stopIfRequired();
        String query = sentence.getQuery();
        if (this.or) {
            Iterator<Rule> it = this.list.iterator();
            while (it.hasNext()) {
                Rule next = it.next();
                sentence.setQuery(query);
                if (next.autoComplete(sentence)) {
                    return true;
                }
            }
            return false;
        }
        Iterator<Rule> it2 = this.list.iterator();
        while (it2.hasNext()) {
            if (!it2.next().autoComplete(sentence)) {
                sentence.setQuery(query);
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int size = this.list.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                if (this.or) {
                    sb.append(" | ");
                } else {
                    sb.append(' ');
                }
            }
            sb.append(this.list.get(i).toString());
        }
        return sb.toString();
    }
}
