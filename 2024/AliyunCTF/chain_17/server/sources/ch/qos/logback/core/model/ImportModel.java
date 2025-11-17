package ch.qos.logback.core.model;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/ImportModel.class */
public class ImportModel extends Model {
    private static final long serialVersionUID = 1;
    String className;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public ImportModel makeNewInstance() {
        return new ImportModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.model.Model
    public void mirror(Model that) {
        ImportModel actual = (ImportModel) that;
        super.mirror(actual);
        this.className = actual.className;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
