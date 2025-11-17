package ch.qos.logback.core.joran.sanity;

import ch.qos.logback.core.model.AppenderModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/sanity/AppenderWithinAppenderSanityChecker.class */
public class AppenderWithinAppenderSanityChecker extends ContextAwareBase implements SanityChecker {
    public static String NESTED_APPENDERS_WARNING = "As of logback version 1.3, nested appenders are not allowed.";

    @Override // ch.qos.logback.core.joran.sanity.SanityChecker
    public void check(Model model) {
        if (model == null) {
            return;
        }
        List<Model> appenderModels = new ArrayList<>();
        deepFindAllModelsOfType(AppenderModel.class, appenderModels, model);
        List<Pair<Model, Model>> nestedPairs = deepFindNestedSubModelsOfType(AppenderModel.class, appenderModels);
        List<Pair<Model, Model>> filteredNestedPairs = (List) nestedPairs.stream().filter(pair -> {
            return !isSiftingAppender((Model) pair.first);
        }).collect(Collectors.toList());
        if (filteredNestedPairs.isEmpty()) {
            return;
        }
        addWarn(NESTED_APPENDERS_WARNING);
        for (Pair<Model, Model> pair2 : filteredNestedPairs) {
            addWarn("Appender at line " + pair2.first.getLineNumber() + " contains a nested appender at line " + pair2.second.getLineNumber());
        }
    }

    private boolean isSiftingAppender(Model first) {
        if (first instanceof AppenderModel) {
            AppenderModel appenderModel = (AppenderModel) first;
            String classname = appenderModel.getClassName();
            if (classname == null) {
                return false;
            }
            return appenderModel.getClassName().contains("SiftingAppender");
        }
        return false;
    }
}
