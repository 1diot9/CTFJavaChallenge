package org.springframework.aop.aspectj;

import java.util.Objects;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.TypePatternMatcher;
import org.springframework.aop.ClassFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/TypePatternClassFilter.class */
public class TypePatternClassFilter implements ClassFilter {
    private String typePattern = "";

    @Nullable
    private TypePatternMatcher aspectJTypePatternMatcher;

    public TypePatternClassFilter() {
    }

    public TypePatternClassFilter(String typePattern) {
        setTypePattern(typePattern);
    }

    public void setTypePattern(String typePattern) {
        Assert.notNull(typePattern, "Type pattern must not be null");
        this.typePattern = typePattern;
        this.aspectJTypePatternMatcher = PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution().parseTypePattern(replaceBooleanOperators(typePattern));
    }

    public String getTypePattern() {
        return this.typePattern;
    }

    @Override // org.springframework.aop.ClassFilter
    public boolean matches(Class<?> clazz) {
        Assert.state(this.aspectJTypePatternMatcher != null, "No type pattern has been set");
        return this.aspectJTypePatternMatcher.matches(clazz);
    }

    private String replaceBooleanOperators(String pcExpr) {
        String result = StringUtils.replace(pcExpr, " and ", " && ");
        return StringUtils.replace(StringUtils.replace(result, " or ", " || "), " not ", " ! ");
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof TypePatternClassFilter) {
                TypePatternClassFilter that = (TypePatternClassFilter) other;
                if (ObjectUtils.nullSafeEquals(this.typePattern, that.typePattern)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hashCode(this.typePattern);
    }

    public String toString() {
        return getClass().getName() + ": " + this.typePattern;
    }
}
