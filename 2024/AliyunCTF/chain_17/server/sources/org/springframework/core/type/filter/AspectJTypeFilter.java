package org.springframework.core.type.filter;

import java.io.IOException;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.weaver.ICrossReferenceHandler;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.FormalBinding;
import org.aspectj.weaver.patterns.PatternParser;
import org.aspectj.weaver.patterns.SimpleScope;
import org.aspectj.weaver.patterns.TypePattern;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/filter/AspectJTypeFilter.class */
public class AspectJTypeFilter implements TypeFilter {
    private final World world;
    private final TypePattern typePattern;

    public AspectJTypeFilter(String typePatternExpression, @Nullable ClassLoader classLoader) {
        this.world = new BcelWorld(classLoader, IMessageHandler.THROW, (ICrossReferenceHandler) null);
        this.world.setBehaveInJava5Way(true);
        PatternParser patternParser = new PatternParser(typePatternExpression);
        TypePattern typePattern = patternParser.parseTypePattern();
        typePattern.resolve(this.world);
        this.typePattern = typePattern.resolveBindings(new SimpleScope(this.world, new FormalBinding[0]), Bindings.NONE, false, false);
    }

    @Override // org.springframework.core.type.filter.TypeFilter
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        String className = metadataReader.getClassMetadata().getClassName();
        ResolvedType resolvedType = this.world.resolve(className);
        return this.typePattern.matchesStatically(resolvedType);
    }
}
