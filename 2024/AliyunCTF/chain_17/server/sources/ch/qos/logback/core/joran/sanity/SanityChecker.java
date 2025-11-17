package ch.qos.logback.core.joran.sanity;

import ch.qos.logback.core.model.Model;
import java.util.ArrayList;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/sanity/SanityChecker.class */
public interface SanityChecker {
    void check(Model model);

    default void deepFindAllModelsOfType(Class<? extends Model> modelClass, List<Model> modelList, Model model) {
        if (modelClass.isInstance(model)) {
            modelList.add(model);
        }
        for (Model m : model.getSubModels()) {
            deepFindAllModelsOfType(modelClass, modelList, m);
        }
    }

    default List<Pair<Model, Model>> deepFindNestedSubModelsOfType(Class<? extends Model> modelClass, List<? extends Model> parentList) {
        List<Pair<Model, Model>> nestingPairs = new ArrayList<>();
        for (Model parent : parentList) {
            List<Model> nestedElements = new ArrayList<>();
            parent.getSubModels().stream().forEach(m -> {
                deepFindAllModelsOfType(modelClass, nestedElements, m);
            });
            nestedElements.forEach(n -> {
                nestingPairs.add(new Pair(parent, n));
            });
        }
        return nestingPairs;
    }
}
