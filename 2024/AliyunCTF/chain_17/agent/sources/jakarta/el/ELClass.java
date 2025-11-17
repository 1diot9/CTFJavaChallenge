package jakarta.el;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/ELClass.class */
public class ELClass {
    private final Class<?> clazz;

    public ELClass(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getKlass() {
        return this.clazz;
    }
}
