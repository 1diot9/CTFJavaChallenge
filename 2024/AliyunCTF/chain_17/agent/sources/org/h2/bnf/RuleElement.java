package org.h2.bnf;

import java.util.HashMap;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/bnf/RuleElement.class */
public class RuleElement implements Rule {
    private final boolean keyword;
    private final String name;
    private Rule link;
    private final int type;

    public RuleElement(String str, String str2) {
        this.name = str;
        this.keyword = str.length() == 1 || str.equals(StringUtils.toUpperEnglish(str));
        this.type = StringUtils.toLowerEnglish(str2).startsWith("function") ? 2 : 1;
    }

    @Override // org.h2.bnf.Rule
    public void accept(BnfVisitor bnfVisitor) {
        bnfVisitor.visitRuleElement(this.keyword, this.name, this.link);
    }

    @Override // org.h2.bnf.Rule
    public void setLinks(HashMap<String, RuleHead> hashMap) {
        if (this.link != null) {
            this.link.setLinks(hashMap);
        }
        if (this.keyword) {
            return;
        }
        String ruleMapKey = Bnf.getRuleMapKey(this.name);
        for (int i = 0; i < ruleMapKey.length(); i++) {
            RuleHead ruleHead = hashMap.get(ruleMapKey.substring(i));
            if (ruleHead != null) {
                this.link = ruleHead.getRule();
                return;
            }
        }
        throw new AssertionError("Unknown " + this.name + "/" + ruleMapKey);
    }

    @Override // org.h2.bnf.Rule
    public boolean autoComplete(Sentence sentence) {
        String str;
        sentence.stopIfRequired();
        if (this.keyword) {
            String query = sentence.getQuery();
            String trim = query.trim();
            String trim2 = sentence.getQueryUpper().trim();
            if (trim2.startsWith(this.name)) {
                String substring = query.substring(this.name.length());
                while (true) {
                    str = substring;
                    if ("_".equals(this.name) || !Bnf.startWithSpace(str)) {
                        break;
                    }
                    substring = str.substring(1);
                }
                sentence.setQuery(str);
                return true;
            }
            if ((trim.length() == 0 || this.name.startsWith(trim2)) && trim.length() < this.name.length()) {
                sentence.add(this.name, this.name.substring(trim.length()), this.type);
                return false;
            }
            return false;
        }
        return this.link.autoComplete(sentence);
    }

    public String toString() {
        return this.name;
    }
}
