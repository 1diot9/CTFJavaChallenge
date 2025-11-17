package ch.qos.logback.core.model;

import java.util.Objects;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/SerializeModelModel.class */
public class SerializeModelModel extends Model {
    private static final long serialVersionUID = 16385651235687L;
    String file;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public SerializeModelModel makeNewInstance() {
        return new SerializeModelModel();
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override // ch.qos.logback.core.model.Model
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        SerializeModelModel that = (SerializeModelModel) o;
        return Objects.equals(this.file, that.file);
    }

    @Override // ch.qos.logback.core.model.Model
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), this.file);
    }
}
