package org.springframework.expression.spel;

import org.springframework.core.SpringProperties;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/SpelParserConfiguration.class */
public class SpelParserConfiguration {
    public static final int DEFAULT_MAX_EXPRESSION_LENGTH = 10000;
    public static final String SPRING_EXPRESSION_COMPILER_MODE_PROPERTY_NAME = "spring.expression.compiler.mode";
    private static final SpelCompilerMode defaultCompilerMode;
    private final SpelCompilerMode compilerMode;

    @Nullable
    private final ClassLoader compilerClassLoader;
    private final boolean autoGrowNullReferences;
    private final boolean autoGrowCollections;
    private final int maximumAutoGrowSize;
    private final int maximumExpressionLength;

    static {
        String compilerMode = SpringProperties.getProperty(SPRING_EXPRESSION_COMPILER_MODE_PROPERTY_NAME);
        defaultCompilerMode = compilerMode != null ? SpelCompilerMode.valueOf(compilerMode.toUpperCase()) : SpelCompilerMode.OFF;
    }

    public SpelParserConfiguration() {
        this(null, null, false, false, Integer.MAX_VALUE);
    }

    public SpelParserConfiguration(@Nullable SpelCompilerMode compilerMode, @Nullable ClassLoader compilerClassLoader) {
        this(compilerMode, compilerClassLoader, false, false, Integer.MAX_VALUE);
    }

    public SpelParserConfiguration(boolean autoGrowNullReferences, boolean autoGrowCollections) {
        this(null, null, autoGrowNullReferences, autoGrowCollections, Integer.MAX_VALUE);
    }

    public SpelParserConfiguration(boolean autoGrowNullReferences, boolean autoGrowCollections, int maximumAutoGrowSize) {
        this(null, null, autoGrowNullReferences, autoGrowCollections, maximumAutoGrowSize);
    }

    public SpelParserConfiguration(@Nullable SpelCompilerMode compilerMode, @Nullable ClassLoader compilerClassLoader, boolean autoGrowNullReferences, boolean autoGrowCollections, int maximumAutoGrowSize) {
        this(compilerMode, compilerClassLoader, autoGrowNullReferences, autoGrowCollections, maximumAutoGrowSize, 10000);
    }

    public SpelParserConfiguration(@Nullable SpelCompilerMode compilerMode, @Nullable ClassLoader compilerClassLoader, boolean autoGrowNullReferences, boolean autoGrowCollections, int maximumAutoGrowSize, int maximumExpressionLength) {
        this.compilerMode = compilerMode != null ? compilerMode : defaultCompilerMode;
        this.compilerClassLoader = compilerClassLoader;
        this.autoGrowNullReferences = autoGrowNullReferences;
        this.autoGrowCollections = autoGrowCollections;
        this.maximumAutoGrowSize = maximumAutoGrowSize;
        this.maximumExpressionLength = maximumExpressionLength;
    }

    public SpelCompilerMode getCompilerMode() {
        return this.compilerMode;
    }

    @Nullable
    public ClassLoader getCompilerClassLoader() {
        return this.compilerClassLoader;
    }

    public boolean isAutoGrowNullReferences() {
        return this.autoGrowNullReferences;
    }

    public boolean isAutoGrowCollections() {
        return this.autoGrowCollections;
    }

    public int getMaximumAutoGrowSize() {
        return this.maximumAutoGrowSize;
    }

    public int getMaximumExpressionLength() {
        return this.maximumExpressionLength;
    }
}
