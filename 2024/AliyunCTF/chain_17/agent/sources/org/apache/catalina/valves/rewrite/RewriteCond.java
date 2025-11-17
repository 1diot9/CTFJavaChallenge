package org.apache.catalina.valves.rewrite;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/RewriteCond.class */
public class RewriteCond {
    protected String testString = null;
    protected String condPattern = null;
    protected String flagsString = null;
    protected boolean positive = true;
    protected Substitution test = null;
    protected Condition condition = null;
    public boolean nocase = false;
    public boolean ornext = false;

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/RewriteCond$Condition.class */
    public static abstract class Condition {
        public abstract boolean evaluate(String str, Resolver resolver);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/RewriteCond$PatternCondition.class */
    public static class PatternCondition extends Condition {
        public Pattern pattern;
        private ThreadLocal<Matcher> matcher = new ThreadLocal<>();

        @Override // org.apache.catalina.valves.rewrite.RewriteCond.Condition
        public boolean evaluate(String value, Resolver resolver) {
            Matcher m = this.pattern.matcher(value);
            if (m.matches()) {
                this.matcher.set(m);
                return true;
            }
            return false;
        }

        public Matcher getMatcher() {
            return this.matcher.get();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/RewriteCond$LexicalCondition.class */
    public static class LexicalCondition extends Condition {
        public int type = 0;
        public String condition;

        @Override // org.apache.catalina.valves.rewrite.RewriteCond.Condition
        public boolean evaluate(String value, Resolver resolver) {
            int result = value.compareTo(this.condition);
            switch (this.type) {
                case -1:
                    return result < 0;
                case 0:
                    return result == 0;
                case 1:
                    return result > 0;
                default:
                    return false;
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/RewriteCond$ResourceCondition.class */
    public static class ResourceCondition extends Condition {
        public int type = 0;

        @Override // org.apache.catalina.valves.rewrite.RewriteCond.Condition
        public boolean evaluate(String value, Resolver resolver) {
            return resolver.resolveResource(this.type, value);
        }
    }

    public String getCondPattern() {
        return this.condPattern;
    }

    public void setCondPattern(String condPattern) {
        this.condPattern = condPattern;
    }

    public String getTestString() {
        return this.testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

    public final String getFlagsString() {
        return this.flagsString;
    }

    public final void setFlagsString(String flagsString) {
        this.flagsString = flagsString;
    }

    public void parse(Map<String, RewriteMap> maps) {
        this.test = new Substitution();
        this.test.setSub(this.testString);
        this.test.parse(maps);
        if (this.condPattern.startsWith("!")) {
            this.positive = false;
            this.condPattern = this.condPattern.substring(1);
        }
        if (this.condPattern.startsWith("<")) {
            LexicalCondition ncondition = new LexicalCondition();
            ncondition.type = -1;
            ncondition.condition = this.condPattern.substring(1);
            this.condition = ncondition;
            return;
        }
        if (this.condPattern.startsWith(">")) {
            LexicalCondition ncondition2 = new LexicalCondition();
            ncondition2.type = 1;
            ncondition2.condition = this.condPattern.substring(1);
            this.condition = ncondition2;
            return;
        }
        if (this.condPattern.startsWith("=")) {
            LexicalCondition ncondition3 = new LexicalCondition();
            ncondition3.type = 0;
            ncondition3.condition = this.condPattern.substring(1);
            this.condition = ncondition3;
            return;
        }
        if (this.condPattern.equals("-d")) {
            ResourceCondition ncondition4 = new ResourceCondition();
            ncondition4.type = 0;
            this.condition = ncondition4;
            return;
        }
        if (this.condPattern.equals("-f")) {
            ResourceCondition ncondition5 = new ResourceCondition();
            ncondition5.type = 1;
            this.condition = ncondition5;
        } else if (this.condPattern.equals("-s")) {
            ResourceCondition ncondition6 = new ResourceCondition();
            ncondition6.type = 2;
            this.condition = ncondition6;
        } else {
            PatternCondition ncondition7 = new PatternCondition();
            int flags = 32;
            if (isNocase()) {
                flags = 32 | 2;
            }
            ncondition7.pattern = Pattern.compile(this.condPattern, flags);
            this.condition = ncondition7;
        }
    }

    public Matcher getMatcher() {
        if (this.condition instanceof PatternCondition) {
            return ((PatternCondition) this.condition).getMatcher();
        }
        return null;
    }

    public String toString() {
        return "RewriteCond " + this.testString + " " + this.condPattern + (this.flagsString != null ? " " + this.flagsString : "");
    }

    public boolean evaluate(Matcher rule, Matcher cond, Resolver resolver) {
        String value = this.test.evaluate(rule, cond, resolver);
        if (this.positive) {
            return this.condition.evaluate(value, resolver);
        }
        return !this.condition.evaluate(value, resolver);
    }

    public boolean isNocase() {
        return this.nocase;
    }

    public void setNocase(boolean nocase) {
        this.nocase = nocase;
    }

    public boolean isOrnext() {
        return this.ornext;
    }

    public void setOrnext(boolean ornext) {
        this.ornext = ornext;
    }

    public boolean isPositive() {
        return this.positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }
}
