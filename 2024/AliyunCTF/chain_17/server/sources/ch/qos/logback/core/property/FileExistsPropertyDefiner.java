package ch.qos.logback.core.property;

import ch.qos.logback.core.PropertyDefinerBase;
import ch.qos.logback.core.util.OptionHelper;
import java.io.File;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/property/FileExistsPropertyDefiner.class */
public class FileExistsPropertyDefiner extends PropertyDefinerBase {
    String path;

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override // ch.qos.logback.core.spi.PropertyDefiner
    public String getPropertyValue() {
        if (OptionHelper.isNullOrEmpty(this.path)) {
            addError("The \"path\" property must be set.");
            return null;
        }
        File file = new File(this.path);
        return booleanAsStr(file.exists());
    }
}
