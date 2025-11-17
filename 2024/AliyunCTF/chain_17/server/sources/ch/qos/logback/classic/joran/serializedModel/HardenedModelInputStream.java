package ch.qos.logback.classic.joran.serializedModel;

import ch.qos.logback.classic.model.ConfigurationModel;
import ch.qos.logback.classic.model.ContextNameModel;
import ch.qos.logback.classic.model.LoggerContextListenerModel;
import ch.qos.logback.classic.model.LoggerModel;
import ch.qos.logback.classic.model.ReceiverModel;
import ch.qos.logback.classic.model.RootLoggerModel;
import ch.qos.logback.core.model.AppenderModel;
import ch.qos.logback.core.model.AppenderRefModel;
import ch.qos.logback.core.model.ComponentModel;
import ch.qos.logback.core.model.DefineModel;
import ch.qos.logback.core.model.EventEvaluatorModel;
import ch.qos.logback.core.model.ImplicitModel;
import ch.qos.logback.core.model.ImportModel;
import ch.qos.logback.core.model.IncludeModel;
import ch.qos.logback.core.model.InsertFromJNDIModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.NamedComponentModel;
import ch.qos.logback.core.model.NamedModel;
import ch.qos.logback.core.model.ParamModel;
import ch.qos.logback.core.model.PropertyModel;
import ch.qos.logback.core.model.SequenceNumberGeneratorModel;
import ch.qos.logback.core.model.SerializeModelModel;
import ch.qos.logback.core.model.ShutdownHookModel;
import ch.qos.logback.core.model.SiftModel;
import ch.qos.logback.core.model.StatusListenerModel;
import ch.qos.logback.core.model.TimestampModel;
import ch.qos.logback.core.model.conditional.ElseModel;
import ch.qos.logback.core.model.conditional.IfModel;
import ch.qos.logback.core.model.conditional.ThenModel;
import ch.qos.logback.core.net.HardenedObjectInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/joran/serializedModel/HardenedModelInputStream.class */
public class HardenedModelInputStream extends HardenedObjectInputStream {
    public static List<String> getWhilelist() {
        List<String> whitelist = new ArrayList<>();
        whitelist.add(Model.class.getName());
        whitelist.add(Model.class.getName());
        whitelist.add(IncludeModel.class.getName());
        whitelist.add(InsertFromJNDIModel.class.getName());
        whitelist.add(RootLoggerModel.class.getName());
        whitelist.add(ImportModel.class.getName());
        whitelist.add(AppenderRefModel.class.getName());
        whitelist.add(ComponentModel.class.getName());
        whitelist.add(StatusListenerModel.class.getName());
        whitelist.add(ShutdownHookModel.class.getName());
        whitelist.add(NamedComponentModel.class.getName());
        whitelist.add(AppenderModel.class.getName());
        whitelist.add(EventEvaluatorModel.class.getName());
        whitelist.add(DefineModel.class.getName());
        whitelist.add(SequenceNumberGeneratorModel.class.getName());
        whitelist.add(ImplicitModel.class.getName());
        whitelist.add(ReceiverModel.class.getName());
        whitelist.add(LoggerContextListenerModel.class.getName());
        whitelist.add(ThenModel.class.getName());
        whitelist.add(IfModel.class.getName());
        whitelist.add(NamedModel.class.getName());
        whitelist.add(ContextNameModel.class.getName());
        whitelist.add(ParamModel.class.getName());
        whitelist.add(TimestampModel.class.getName());
        whitelist.add(PropertyModel.class.getName());
        whitelist.add(ElseModel.class.getName());
        whitelist.add(ConfigurationModel.class.getName());
        whitelist.add(SiftModel.class.getName());
        whitelist.add(LoggerModel.class.getName());
        whitelist.add(SerializeModelModel.class.getName());
        return whitelist;
    }

    public HardenedModelInputStream(InputStream is) throws IOException {
        super(is, getWhilelist());
    }
}
