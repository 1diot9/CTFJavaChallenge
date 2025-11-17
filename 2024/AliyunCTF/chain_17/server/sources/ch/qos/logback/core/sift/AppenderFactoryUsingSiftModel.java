package ch.qos.logback.core.sift;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.JoranConstants;
import ch.qos.logback.core.joran.ParamModelHandler;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.model.AppenderModel;
import ch.qos.logback.core.model.ImplicitModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ParamModel;
import ch.qos.logback.core.model.PropertyModel;
import ch.qos.logback.core.model.SiftModel;
import ch.qos.logback.core.model.processor.AppenderModelHandler;
import ch.qos.logback.core.model.processor.ImplicitModelHandler;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.model.processor.PropertyModelHandler;
import java.util.Collection;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/sift/AppenderFactoryUsingSiftModel.class */
public class AppenderFactoryUsingSiftModel<E> implements AppenderFactory<E> {
    Context context;
    final Model siftModel;
    protected String discriminatingKey;
    protected ModelInterpretationContext parentMic;
    protected DefaultNestedComponentRegistry registry;

    public AppenderFactoryUsingSiftModel(ModelInterpretationContext parentMic, Model aSiftModel, String discriminatingKey) {
        this.siftModel = Model.duplicate(aSiftModel);
        this.discriminatingKey = discriminatingKey;
        this.parentMic = parentMic;
        this.context = parentMic.getContext();
    }

    public SiftProcessor<E> getSiftingModelProcessor(String value) {
        ModelInterpretationContext smic = new ModelInterpretationContext(this.parentMic) { // from class: ch.qos.logback.core.sift.AppenderFactoryUsingSiftModel.1
            @Override // ch.qos.logback.core.model.processor.ModelInterpretationContext
            public boolean hasDependers(String dependeeName) {
                return true;
            }
        };
        SiftProcessor<E> siftProcessor = new SiftProcessor<>(this.context, smic);
        siftProcessor.addHandler(ParamModel.class, ParamModelHandler::makeInstance);
        siftProcessor.addHandler(PropertyModel.class, PropertyModelHandler::makeInstance);
        siftProcessor.addHandler(ImplicitModel.class, ImplicitModelHandler::makeInstance);
        siftProcessor.addHandler(AppenderModel.class, AppenderModelHandler::makeInstance);
        siftProcessor.addHandler(SiftModel.class, NOPSiftModelHandler::makeInstance);
        return siftProcessor;
    }

    @Override // ch.qos.logback.core.sift.AppenderFactory
    public Appender<E> buildAppender(Context context, String discriminatingValue) throws JoranException {
        SiftProcessor<E> sp = getSiftingModelProcessor(discriminatingValue);
        ModelInterpretationContext mic = sp.getModelInterpretationContext();
        sp.setContext(context);
        Model duplicate = Model.duplicate(this.siftModel);
        mic.addSubstitutionProperty(this.discriminatingKey, discriminatingValue);
        sp.process(duplicate);
        Map<String, Appender<E>> appenderBag = (Map) mic.getObjectMap().get(JoranConstants.APPENDER_BAG);
        Collection<Appender<E>> values = appenderBag.values();
        if (values.size() == 0) {
            return null;
        }
        return (Appender) values.iterator().next();
    }

    public Model getSiftModel() {
        return this.siftModel;
    }
}
