package ch.qos.logback.classic.joran.sanity;

import ch.qos.logback.classic.model.LoggerModel;
import ch.qos.logback.classic.model.RootLoggerModel;
import ch.qos.logback.core.joran.sanity.Pair;
import ch.qos.logback.core.joran.sanity.SanityChecker;
import ch.qos.logback.core.model.AppenderModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.conditional.IfModel;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/joran/sanity/IfNestedWithinSecondPhaseElementSC.class */
public class IfNestedWithinSecondPhaseElementSC extends ContextAwareBase implements SanityChecker {
    public static final String NESTED_IF_WARNING_URL = "http://logback.qos.ch/codes.html#nested_if_element";

    @Override // ch.qos.logback.core.joran.sanity.SanityChecker
    public void check(Model model) {
        if (model == null) {
            return;
        }
        List<Model> secondPhaseModels = new ArrayList<>();
        deepFindAllModelsOfType(AppenderModel.class, secondPhaseModels, model);
        deepFindAllModelsOfType(LoggerModel.class, secondPhaseModels, model);
        deepFindAllModelsOfType(RootLoggerModel.class, secondPhaseModels, model);
        List<Pair<Model, Model>> nestedPairs = deepFindNestedSubModelsOfType(IfModel.class, secondPhaseModels);
        if (nestedPairs.isEmpty()) {
            return;
        }
        addWarn("<if> elements cannot be nested within an <appender>, <logger> or <root> element");
        addWarn("See also http://logback.qos.ch/codes.html#nested_if_element");
        for (Pair<Model, Model> pair : nestedPairs) {
            Model p = pair.first;
            int pLine = p.getLineNumber();
            Model s = pair.second;
            int sLine = s.getLineNumber();
            addWarn("Element <" + p.getTag() + "> at line " + pLine + " contains a nested <" + s.getTag() + "> element at line " + sLine);
        }
    }

    public String toString() {
        return "IfNestedWithinSecondPhaseElementSC";
    }
}
