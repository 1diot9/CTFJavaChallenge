package cn.hutool.core.text.finder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/finder/PatternFinder.class */
public class PatternFinder extends TextFinder {
    private static final long serialVersionUID = 1;
    private final Pattern pattern;
    private Matcher matcher;

    public PatternFinder(String regex, boolean caseInsensitive) {
        this(Pattern.compile(regex, caseInsensitive ? 2 : 0));
    }

    public PatternFinder(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override // cn.hutool.core.text.finder.TextFinder
    public TextFinder setText(CharSequence text) {
        this.matcher = this.pattern.matcher(text);
        return super.setText(text);
    }

    @Override // cn.hutool.core.text.finder.TextFinder
    public TextFinder setNegative(boolean negative) {
        throw new UnsupportedOperationException("Negative is invalid for Pattern!");
    }

    @Override // cn.hutool.core.text.finder.Finder
    public int start(int from) {
        if (this.matcher.find(from) && this.matcher.end() <= getValidEndIndex()) {
            return this.matcher.start();
        }
        return -1;
    }

    @Override // cn.hutool.core.text.finder.Finder
    public int end(int start) {
        int limit;
        int end = this.matcher.end();
        if (this.endIndex < 0) {
            limit = this.text.length();
        } else {
            limit = Math.min(this.endIndex, this.text.length());
        }
        if (end <= limit) {
            return end;
        }
        return -1;
    }

    @Override // cn.hutool.core.text.finder.Finder
    public PatternFinder reset() {
        this.matcher.reset();
        return this;
    }
}
