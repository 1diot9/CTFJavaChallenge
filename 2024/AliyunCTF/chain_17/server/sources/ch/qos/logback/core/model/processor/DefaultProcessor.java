package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ModelHandlerFactoryMethod;
import ch.qos.logback.core.model.NamedComponentModel;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.FilterReply;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/DefaultProcessor.class */
public class DefaultProcessor extends ContextAwareBase {
    protected final ModelInterpretationContext mic;
    final HashMap<Class<? extends Model>, ModelHandlerFactoryMethod> modelClassToHandlerMap = new HashMap<>();
    final HashMap<Class<? extends Model>, Supplier<ModelHandlerBase>> modelClassToDependencyAnalyserMap = new HashMap<>();
    ChainedModelFilter phaseOneFilter = new ChainedModelFilter();
    ChainedModelFilter phaseTwoFilter = new ChainedModelFilter();
    static final int DENIED = -1;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/DefaultProcessor$TraverseMethod.class */
    public interface TraverseMethod {
        int traverse(Model model, ModelFilter modelFilter);
    }

    public DefaultProcessor(Context context, ModelInterpretationContext mic) {
        setContext(context);
        this.mic = mic;
    }

    public void addHandler(Class<? extends Model> modelClass, ModelHandlerFactoryMethod modelFactoryMethod) {
        this.modelClassToHandlerMap.put(modelClass, modelFactoryMethod);
        ProcessingPhase phase = determineProcessingPhase(modelClass);
        switch (phase) {
            case FIRST:
                getPhaseOneFilter().allow(modelClass);
                return;
            case SECOND:
                getPhaseTwoFilter().allow(modelClass);
                return;
            default:
                throw new IllegalArgumentException("unexpected value " + String.valueOf(phase) + " for model class " + modelClass.getName());
        }
    }

    private ProcessingPhase determineProcessingPhase(Class<? extends Model> modelClass) {
        PhaseIndicator phaseIndicator = (PhaseIndicator) modelClass.getAnnotation(PhaseIndicator.class);
        if (phaseIndicator == null) {
            return ProcessingPhase.FIRST;
        }
        ProcessingPhase phase = phaseIndicator.phase();
        return phase;
    }

    public void addAnalyser(Class<? extends Model> modelClass, Supplier<ModelHandlerBase> analyserSupplier) {
        this.modelClassToDependencyAnalyserMap.put(modelClass, analyserSupplier);
    }

    private void traversalLoop(TraverseMethod traverseMethod, Model model, ModelFilter modelfFilter, String phaseName) {
        for (int i = 0; i < 3; i++) {
            int handledModelCount = traverseMethod.traverse(model, modelfFilter);
            if (handledModelCount == 0) {
                return;
            }
        }
    }

    public void process(Model model) {
        if (model == null) {
            addError("Expecting non null model to process");
            return;
        }
        initialObjectPush();
        mainTraverse(model, getPhaseOneFilter());
        analyseDependencies(model);
        traversalLoop(this::secondPhaseTraverse, model, getPhaseTwoFilter(), "phase 2");
        addInfo("End of configuration.");
        finalObjectPop();
    }

    private void finalObjectPop() {
        this.mic.popObject();
    }

    private void initialObjectPush() {
        this.mic.pushObject(this.context);
    }

    public ChainedModelFilter getPhaseOneFilter() {
        return this.phaseOneFilter;
    }

    public ChainedModelFilter getPhaseTwoFilter() {
        return this.phaseTwoFilter;
    }

    protected void analyseDependencies(Model model) {
        Supplier<ModelHandlerBase> analyserSupplier = this.modelClassToDependencyAnalyserMap.get(model.getClass());
        ModelHandlerBase analyser = null;
        if (analyserSupplier != null) {
            analyser = analyserSupplier.get();
        }
        if (analyser != null && !model.isSkipped()) {
            callAnalyserHandleOnModel(model, analyser);
        }
        for (Model m : model.getSubModels()) {
            analyseDependencies(m);
        }
        if (analyser != null && !model.isSkipped()) {
            callAnalyserPostHandleOnModel(model, analyser);
        }
    }

    private void callAnalyserPostHandleOnModel(Model model, ModelHandlerBase analyser) {
        try {
            analyser.postHandle(this.mic, model);
        } catch (ModelHandlerException e) {
            addError("Failed to invoke postHandle on model " + model.getTag(), e);
        }
    }

    private void callAnalyserHandleOnModel(Model model, ModelHandlerBase analyser) {
        try {
            analyser.handle(this.mic, model);
        } catch (ModelHandlerException e) {
            addError("Failed to traverse model " + model.getTag(), e);
        }
    }

    private ModelHandlerBase createHandler(Model model) {
        ModelHandlerFactoryMethod modelFactoryMethod = this.modelClassToHandlerMap.get(model.getClass());
        if (modelFactoryMethod == null) {
            addError("Can't handle model of type " + String.valueOf(model.getClass()) + "  with tag: " + model.getTag() + " at line " + model.getLineNumber());
            return null;
        }
        ModelHandlerBase handler = modelFactoryMethod.make(this.context, this.mic);
        if (handler == null) {
            return null;
        }
        if (!handler.isSupportedModelType(model)) {
            addWarn("Handler [" + String.valueOf(handler.getClass()) + "] does not support " + model.idString());
            return null;
        }
        return handler;
    }

    protected int mainTraverse(Model model, ModelFilter modelFiler) {
        FilterReply filterReply = modelFiler.decide(model);
        if (filterReply == FilterReply.DENY) {
            return -1;
        }
        int count = 0;
        try {
            ModelHandlerBase handler = null;
            boolean unhandled = model.isUnhandled();
            if (unhandled) {
                handler = createHandler(model);
                if (handler != null) {
                    handler.handle(this.mic, model);
                    model.markAsHandled();
                    count = 0 + 1;
                }
            }
            if (!model.isSkipped()) {
                for (Model m : model.getSubModels()) {
                    count += mainTraverse(m, modelFiler);
                }
            }
            if (unhandled && handler != null) {
                handler.postHandle(this.mic, model);
            }
        } catch (ModelHandlerException e) {
            addError("Failed to traverse model " + model.getTag(), e);
        }
        return count;
    }

    protected int secondPhaseTraverse(Model model, ModelFilter modelFilter) {
        boolean allDependenciesStarted;
        ModelHandlerBase handler;
        FilterReply filterReply = modelFilter.decide(model);
        if (filterReply == FilterReply.DENY) {
            return 0;
        }
        int count = 0;
        try {
            allDependenciesStarted = allDependenciesStarted(model);
            handler = null;
            if (model.isUnhandled() && allDependenciesStarted) {
                handler = createHandler(model);
                if (handler != null) {
                    handler.handle(this.mic, model);
                    model.markAsHandled();
                    count = 0 + 1;
                }
            }
        } catch (ModelHandlerException e) {
            addError("Failed to traverse model " + model.getTag(), e);
        }
        if (!allDependenciesStarted && !dependencyIsADirectSubmodel(model)) {
            return count;
        }
        if (!model.isSkipped()) {
            for (Model m : model.getSubModels()) {
                count += secondPhaseTraverse(m, modelFilter);
            }
        }
        if (handler != null) {
            handler.postHandle(this.mic, model);
        }
        return count;
    }

    private boolean dependencyIsADirectSubmodel(Model model) {
        List<String> dependecyNames = this.mic.getDependeeNamesForModel(model);
        if (dependecyNames == null || dependecyNames.isEmpty()) {
            return false;
        }
        for (Model submodel : model.getSubModels()) {
            if (submodel instanceof NamedComponentModel) {
                NamedComponentModel namedComponentModel = (NamedComponentModel) submodel;
                String subModelName = namedComponentModel.getName();
                if (dependecyNames.contains(subModelName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean allDependenciesStarted(Model model) {
        List<String> dependencyNames = this.mic.getDependeeNamesForModel(model);
        if (dependencyNames == null || dependencyNames.isEmpty()) {
            return true;
        }
        for (String name : dependencyNames) {
            boolean isStarted = this.mic.isNamedDependeeStarted(name);
            if (!isStarted) {
                return false;
            }
        }
        return true;
    }

    ModelHandlerBase instantiateHandler(Class<? extends ModelHandlerBase> handlerClass) {
        try {
            Constructor<? extends ModelHandlerBase> commonConstructor = getWithContextConstructor(handlerClass);
            if (commonConstructor != null) {
                return commonConstructor.newInstance(this.context);
            }
            Constructor<? extends ModelHandlerBase> constructorWithBDC = getWithContextAndBDCConstructor(handlerClass);
            if (constructorWithBDC != null) {
                return constructorWithBDC.newInstance(this.context, this.mic.getBeanDescriptionCache());
            }
            addError("Failed to find suitable constructor for class [" + String.valueOf(handlerClass) + "]");
            return null;
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException | InvocationTargetException e) {
            addError("Failed to instantiate " + String.valueOf(handlerClass));
            return null;
        }
    }

    private Constructor<? extends ModelHandlerBase> getWithContextConstructor(Class<? extends ModelHandlerBase> handlerClass) {
        try {
            Constructor<? extends ModelHandlerBase> constructor = handlerClass.getConstructor(Context.class);
            return constructor;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private Constructor<? extends ModelHandlerBase> getWithContextAndBDCConstructor(Class<? extends ModelHandlerBase> handlerClass) {
        try {
            Constructor<? extends ModelHandlerBase> constructor = handlerClass.getConstructor(Context.class, BeanDescriptionCache.class);
            return constructor;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
