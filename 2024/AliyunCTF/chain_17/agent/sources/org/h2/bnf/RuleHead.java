package org.h2.bnf;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/bnf/RuleHead.class */
public class RuleHead {
    private final String section;
    private final String topic;
    private Rule rule;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RuleHead(String str, String str2, Rule rule) {
        this.section = str;
        this.topic = str2;
        this.rule = rule;
    }

    public String getTopic() {
        return this.topic;
    }

    public Rule getRule() {
        return this.rule;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public String getSection() {
        return this.section;
    }
}
