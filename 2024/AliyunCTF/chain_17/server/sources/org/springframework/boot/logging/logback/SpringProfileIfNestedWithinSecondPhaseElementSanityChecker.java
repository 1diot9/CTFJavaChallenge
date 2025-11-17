package org.springframework.boot.logging.logback;

import ch.qos.logback.classic.model.LoggerModel;
import ch.qos.logback.classic.model.RootLoggerModel;
import ch.qos.logback.core.joran.sanity.Pair;
import ch.qos.logback.core.joran.sanity.SanityChecker;
import ch.qos.logback.core.model.AppenderModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.util.ArrayList;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/SpringProfileIfNestedWithinSecondPhaseElementSanityChecker.class */
class SpringProfileIfNestedWithinSecondPhaseElementSanityChecker extends ContextAwareBase implements SanityChecker {
    private static final List<Class<? extends Model>> SECOND_PHASE_TYPES = List.of(AppenderModel.class, LoggerModel.class, RootLoggerModel.class);

    @Override // ch.qos.logback.core.joran.sanity.SanityChecker
    public void check(Model model) {
        if (model == null) {
            return;
        }
        List<Model> models = new ArrayList<>();
        SECOND_PHASE_TYPES.forEach(type -> {
            deepFindAllModelsOfType(type, models, model);
        });
        List<Pair<Model, Model>> nestedPairs = deepFindNestedSubModelsOfType(SpringProfileModel.class, models);
        if (!nestedPairs.isEmpty()) {
            addWarn("<springProfile> elements cannot be nested within an <appender>, <logger> or <root> element");
            nestedPairs.forEach(nested -> {
                Model first = (Model) nested.first;
                Model second = (Model) nested.second;
                addWarn("Element <%s> at line %s contains a nested <%s> element at line %s".formatted(first.getTag(), Integer.valueOf(first.getLineNumber()), second.getTag(), Integer.valueOf(second.getLineNumber())));
            });
        }
    }
}
