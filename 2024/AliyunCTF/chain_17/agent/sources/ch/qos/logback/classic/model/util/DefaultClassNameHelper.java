package ch.qos.logback.classic.model.util;

import ch.qos.logback.classic.model.processor.LogbackClassicDefaultNestedComponentRules;
import ch.qos.logback.core.joran.util.ParentTag_Tag_Class_Tuple;
import ch.qos.logback.core.model.ImplicitModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.util.TagUtil;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/model/util/DefaultClassNameHelper.class */
public class DefaultClassNameHelper {
    List<ParentTag_Tag_Class_Tuple> tupleList = LogbackClassicDefaultNestedComponentRules.TUPLES_LIST;

    public void injectDefaultComponentClasses(Model aModel, Model parent) {
        applyInjectionRules(aModel, parent);
        for (Model sub : aModel.getSubModels()) {
            injectDefaultComponentClasses(sub, aModel);
        }
    }

    private void applyInjectionRules(Model aModel, Model parent) {
        if (parent == null) {
            return;
        }
        String parentTag = TagUtil.unifiedTag(parent);
        String modelTag = TagUtil.unifiedTag(aModel);
        if (aModel instanceof ImplicitModel) {
            ImplicitModel implicitModel = (ImplicitModel) aModel;
            String className = implicitModel.getClassName();
            if (className == null || className.isEmpty()) {
                for (ParentTag_Tag_Class_Tuple ruleTuple : this.tupleList) {
                    if (ruleTuple.parentTag.equals(parentTag) && ruleTuple.tag.equals(modelTag)) {
                        implicitModel.setClassName(ruleTuple.className);
                        return;
                    }
                }
            }
        }
    }
}
