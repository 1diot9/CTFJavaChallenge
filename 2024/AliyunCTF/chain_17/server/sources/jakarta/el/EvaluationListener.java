package jakarta.el;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/EvaluationListener.class */
public abstract class EvaluationListener {
    public void beforeEvaluation(ELContext context, String expression) {
    }

    public void afterEvaluation(ELContext context, String expression) {
    }

    public void propertyResolved(ELContext context, Object base, Object property) {
    }
}
