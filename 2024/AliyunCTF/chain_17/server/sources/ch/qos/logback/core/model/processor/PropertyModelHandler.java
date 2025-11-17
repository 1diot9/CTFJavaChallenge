package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ModelUtil;
import ch.qos.logback.core.model.PropertyModel;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/PropertyModelHandler.class */
public class PropertyModelHandler extends ModelHandlerBase {
    public static final String INVALID_ATTRIBUTES = "In <property> element, either the \"file\" attribute alone, or the \"resource\" element alone, or both the \"name\" and \"value\" attributes must be set.";

    public PropertyModelHandler(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance(Context context, ModelInterpretationContext ic) {
        return new PropertyModelHandler(context);
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    protected Class<PropertyModel> getSupportedModelClass() {
        return PropertyModel.class;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext interpretationContext, Model model) {
        PropertyModel propertyModel = (PropertyModel) model;
        ActionUtil.Scope scope = ActionUtil.stringToScope(propertyModel.getScopeStr());
        if (checkFileAttributeSanity(propertyModel)) {
            String file = interpretationContext.subst(propertyModel.getFile());
            try {
                FileInputStream istream = new FileInputStream(file);
                try {
                    loadAndSetProperties(interpretationContext, istream, scope);
                    istream.close();
                    return;
                } finally {
                }
            } catch (FileNotFoundException e) {
                addError("Could not find properties file [" + file + "].");
                return;
            } catch (IOException | IllegalArgumentException e1) {
                addError("Could not read properties file [" + file + "].", e1);
                return;
            }
        }
        if (!checkResourceAttributeSanity(propertyModel)) {
            if (checkValueNameAttributesSanity(propertyModel)) {
                String value = propertyModel.getValue();
                ActionUtil.setProperty(interpretationContext, propertyModel.getName(), interpretationContext.subst(value.trim()), scope);
                return;
            }
            addError(INVALID_ATTRIBUTES);
            return;
        }
        String resource = interpretationContext.subst(propertyModel.getResource());
        URL resourceURL = Loader.getResourceBySelfClassLoader(resource);
        if (resourceURL == null) {
            addError("Could not find resource [" + resource + "].");
            return;
        }
        try {
            InputStream istream2 = resourceURL.openStream();
            try {
                loadAndSetProperties(interpretationContext, istream2, scope);
                if (istream2 != null) {
                    istream2.close();
                }
            } finally {
            }
        } catch (IOException e2) {
            addError("Could not read resource file [" + resource + "].", e2);
        }
    }

    void loadAndSetProperties(ModelInterpretationContext mic, InputStream istream, ActionUtil.Scope scope) throws IOException {
        Properties props = new Properties();
        props.load(istream);
        ModelUtil.setProperties(mic, props, scope);
    }

    boolean checkFileAttributeSanity(PropertyModel propertyModel) {
        String file = propertyModel.getFile();
        String name = propertyModel.getName();
        String value = propertyModel.getValue();
        String resource = propertyModel.getResource();
        return !OptionHelper.isNullOrEmpty(file) && OptionHelper.isNullOrEmpty(name) && OptionHelper.isNullOrEmpty(value) && OptionHelper.isNullOrEmpty(resource);
    }

    boolean checkResourceAttributeSanity(PropertyModel propertyModel) {
        String file = propertyModel.getFile();
        String name = propertyModel.getName();
        String value = propertyModel.getValue();
        String resource = propertyModel.getResource();
        return !OptionHelper.isNullOrEmpty(resource) && OptionHelper.isNullOrEmpty(name) && OptionHelper.isNullOrEmpty(value) && OptionHelper.isNullOrEmpty(file);
    }

    boolean checkValueNameAttributesSanity(PropertyModel propertyModel) {
        String file = propertyModel.getFile();
        String name = propertyModel.getName();
        String value = propertyModel.getValue();
        String resource = propertyModel.getResource();
        return !OptionHelper.isNullOrEmpty(name) && !OptionHelper.isNullOrEmpty(value) && OptionHelper.isNullOrEmpty(file) && OptionHelper.isNullOrEmpty(resource);
    }
}
