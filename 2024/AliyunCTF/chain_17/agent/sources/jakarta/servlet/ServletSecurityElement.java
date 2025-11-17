package jakarta.servlet;

import jakarta.servlet.annotation.HttpMethodConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletSecurityElement.class */
public class ServletSecurityElement extends HttpConstraintElement {
    private final Map<String, HttpMethodConstraintElement> methodConstraints;

    public ServletSecurityElement() {
        this.methodConstraints = new HashMap();
    }

    public ServletSecurityElement(HttpConstraintElement httpConstraintElement) {
        this(httpConstraintElement, null);
    }

    public ServletSecurityElement(Collection<HttpMethodConstraintElement> httpMethodConstraints) {
        this.methodConstraints = new HashMap();
        addHttpMethodConstraints(httpMethodConstraints);
    }

    public ServletSecurityElement(HttpConstraintElement httpConstraintElement, Collection<HttpMethodConstraintElement> httpMethodConstraints) {
        super(httpConstraintElement.getEmptyRoleSemantic(), httpConstraintElement.getTransportGuarantee(), httpConstraintElement.getRolesAllowed());
        this.methodConstraints = new HashMap();
        addHttpMethodConstraints(httpMethodConstraints);
    }

    public ServletSecurityElement(ServletSecurity annotation) {
        this(new HttpConstraintElement(annotation.value().value(), annotation.value().transportGuarantee(), annotation.value().rolesAllowed()));
        List<HttpMethodConstraintElement> l = new ArrayList<>();
        HttpMethodConstraint[] constraints = annotation.httpMethodConstraints();
        if (constraints != null) {
            for (HttpMethodConstraint constraint : constraints) {
                HttpMethodConstraintElement e = new HttpMethodConstraintElement(constraint.value(), new HttpConstraintElement(constraint.emptyRoleSemantic(), constraint.transportGuarantee(), constraint.rolesAllowed()));
                l.add(e);
            }
        }
        addHttpMethodConstraints(l);
    }

    public Collection<HttpMethodConstraintElement> getHttpMethodConstraints() {
        Collection<HttpMethodConstraintElement> result = new HashSet<>(this.methodConstraints.values());
        return result;
    }

    public Collection<String> getMethodNames() {
        Collection<String> result = new HashSet<>(this.methodConstraints.keySet());
        return result;
    }

    private void addHttpMethodConstraints(Collection<HttpMethodConstraintElement> httpMethodConstraints) {
        if (httpMethodConstraints == null) {
            return;
        }
        for (HttpMethodConstraintElement constraint : httpMethodConstraints) {
            String method = constraint.getMethodName();
            if (this.methodConstraints.containsKey(method)) {
                throw new IllegalArgumentException("Duplicate method name: " + method);
            }
            this.methodConstraints.put(method, constraint);
        }
    }
}
