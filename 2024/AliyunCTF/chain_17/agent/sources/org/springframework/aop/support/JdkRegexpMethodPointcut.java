package org.springframework.aop.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/JdkRegexpMethodPointcut.class */
public class JdkRegexpMethodPointcut extends AbstractRegexpMethodPointcut {
    private Pattern[] compiledPatterns = new Pattern[0];
    private Pattern[] compiledExclusionPatterns = new Pattern[0];

    @Override // org.springframework.aop.support.AbstractRegexpMethodPointcut
    protected void initPatternRepresentation(String[] patterns) throws PatternSyntaxException {
        this.compiledPatterns = compilePatterns(patterns);
    }

    @Override // org.springframework.aop.support.AbstractRegexpMethodPointcut
    protected void initExcludedPatternRepresentation(String[] excludedPatterns) throws PatternSyntaxException {
        this.compiledExclusionPatterns = compilePatterns(excludedPatterns);
    }

    @Override // org.springframework.aop.support.AbstractRegexpMethodPointcut
    protected boolean matches(String pattern, int patternIndex) {
        Matcher matcher = this.compiledPatterns[patternIndex].matcher(pattern);
        return matcher.matches();
    }

    @Override // org.springframework.aop.support.AbstractRegexpMethodPointcut
    protected boolean matchesExclusion(String candidate, int patternIndex) {
        Matcher matcher = this.compiledExclusionPatterns[patternIndex].matcher(candidate);
        return matcher.matches();
    }

    private Pattern[] compilePatterns(String[] source) throws PatternSyntaxException {
        Pattern[] destination = new Pattern[source.length];
        for (int i = 0; i < source.length; i++) {
            destination[i] = Pattern.compile(source[i]);
        }
        return destination;
    }
}
