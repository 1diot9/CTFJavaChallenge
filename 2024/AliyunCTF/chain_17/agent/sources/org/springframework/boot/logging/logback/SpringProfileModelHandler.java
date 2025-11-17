package org.springframework.boot.logging.logback;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.util.OptionHelper;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/SpringProfileModelHandler.class */
class SpringProfileModelHandler extends ModelHandlerBase {
    private final Environment environment;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringProfileModelHandler(Context context, Environment environment) {
        super(context);
        this.environment = environment;
    }

    @Override // ch.qos.logback.core.model.processor.ModelHandlerBase
    public void handle(ModelInterpretationContext intercon, Model model) throws ModelHandlerException {
        SpringProfileModel profileModel = (SpringProfileModel) model;
        if (!acceptsProfiles(intercon, profileModel)) {
            model.deepMarkAsSkipped();
        }
    }

    private boolean acceptsProfiles(ModelInterpretationContext ic, SpringProfileModel model) {
        if (this.environment == null) {
            return false;
        }
        String[] profileNames = StringUtils.trimArrayElements(StringUtils.commaDelimitedListToStringArray(model.getName()));
        if (profileNames.length == 0) {
            return false;
        }
        for (int i = 0; i < profileNames.length; i++) {
            try {
                profileNames[i] = OptionHelper.substVars(profileNames[i], ic, this.context);
            } catch (ScanException ex) {
                throw new RuntimeException(ex);
            }
        }
        return this.environment.acceptsProfiles(Profiles.of(profileNames));
    }
}
