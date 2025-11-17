package org.springframework.web.servlet.function;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.util.pattern.PathPatternParser;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ChangePathPatternParserVisitor.class */
public class ChangePathPatternParserVisitor implements RouterFunctions.Visitor {
    private final PathPatternParser parser;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ChangePathPatternParserVisitor$Target.class */
    public interface Target {
        void changeParser(PathPatternParser parser);
    }

    public ChangePathPatternParserVisitor(PathPatternParser parser) {
        Assert.notNull(parser, "Parser must not be null");
        this.parser = parser;
    }

    @Override // org.springframework.web.servlet.function.RouterFunctions.Visitor
    public void startNested(RequestPredicate predicate) {
        changeParser(predicate);
    }

    @Override // org.springframework.web.servlet.function.RouterFunctions.Visitor
    public void endNested(RequestPredicate predicate) {
    }

    @Override // org.springframework.web.servlet.function.RouterFunctions.Visitor
    public void route(RequestPredicate predicate, HandlerFunction<?> handlerFunction) {
        changeParser(predicate);
    }

    @Override // org.springframework.web.servlet.function.RouterFunctions.Visitor
    public void resources(Function<ServerRequest, Optional<Resource>> lookupFunction) {
    }

    @Override // org.springframework.web.servlet.function.RouterFunctions.Visitor
    public void attributes(Map<String, Object> attributes) {
    }

    @Override // org.springframework.web.servlet.function.RouterFunctions.Visitor
    public void unknown(RouterFunction<?> routerFunction) {
    }

    private void changeParser(RequestPredicate predicate) {
        if (predicate instanceof Target) {
            Target target = (Target) predicate;
            target.changeParser(this.parser);
        }
    }
}
