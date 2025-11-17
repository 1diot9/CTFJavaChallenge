package org.apache.tomcat.util.descriptor.web;

import jakarta.servlet.HttpConstraintElement;
import jakarta.servlet.HttpMethodConstraintElement;
import jakarta.servlet.ServletSecurityElement;
import jakarta.servlet.annotation.ServletSecurity;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/SecurityConstraint.class */
public class SecurityConstraint extends XmlEncodingBase implements Serializable {
    private static final long serialVersionUID = 1;
    public static final String ROLE_ALL_ROLES = "*";
    public static final String ROLE_ALL_AUTHENTICATED_USERS = "**";
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE_NAME);
    private boolean allRoles = false;
    private boolean authenticatedUsers = false;
    private boolean authConstraint = false;
    private String[] authRoles = new String[0];
    private SecurityCollection[] collections = new SecurityCollection[0];
    private String displayName = null;
    private String userConstraint = "NONE";

    public boolean getAllRoles() {
        return this.allRoles;
    }

    public boolean getAuthenticatedUsers() {
        return this.authenticatedUsers;
    }

    public boolean getAuthConstraint() {
        return this.authConstraint;
    }

    public void setAuthConstraint(boolean authConstraint) {
        this.authConstraint = authConstraint;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserConstraint() {
        return this.userConstraint;
    }

    public void setUserConstraint(String userConstraint) {
        if (userConstraint != null) {
            this.userConstraint = userConstraint;
        }
    }

    public void treatAllAuthenticatedUsersAsApplicationRole() {
        if (this.authenticatedUsers) {
            this.authenticatedUsers = false;
            String[] results = (String[]) Arrays.copyOf(this.authRoles, this.authRoles.length + 1);
            results[this.authRoles.length] = ROLE_ALL_AUTHENTICATED_USERS;
            this.authRoles = results;
            this.authConstraint = true;
        }
    }

    public void addAuthRole(String authRole) {
        if (authRole == null) {
            return;
        }
        if ("*".equals(authRole)) {
            this.allRoles = true;
            return;
        }
        if (ROLE_ALL_AUTHENTICATED_USERS.equals(authRole)) {
            this.authenticatedUsers = true;
            return;
        }
        String[] results = (String[]) Arrays.copyOf(this.authRoles, this.authRoles.length + 1);
        results[this.authRoles.length] = authRole;
        this.authRoles = results;
        this.authConstraint = true;
    }

    @Override // org.apache.tomcat.util.descriptor.web.XmlEncodingBase
    public void setCharset(Charset charset) {
        super.setCharset(charset);
        for (SecurityCollection collection : this.collections) {
            collection.setCharset(getCharset());
        }
    }

    public void addCollection(SecurityCollection collection) {
        if (collection == null) {
            return;
        }
        collection.setCharset(getCharset());
        SecurityCollection[] results = (SecurityCollection[]) Arrays.copyOf(this.collections, this.collections.length + 1);
        results[this.collections.length] = collection;
        this.collections = results;
    }

    public boolean findAuthRole(String role) {
        if (role == null) {
            return false;
        }
        for (String authRole : this.authRoles) {
            if (role.equals(authRole)) {
                return true;
            }
        }
        return false;
    }

    public String[] findAuthRoles() {
        return this.authRoles;
    }

    public SecurityCollection findCollection(String name) {
        if (name == null) {
            return null;
        }
        for (SecurityCollection collection : this.collections) {
            if (name.equals(collection.getName())) {
                return collection;
            }
        }
        return null;
    }

    public SecurityCollection[] findCollections() {
        return this.collections;
    }

    public boolean included(String uri, String method) {
        if (method == null) {
            return false;
        }
        for (SecurityCollection collection : this.collections) {
            if (collection.findMethod(method)) {
                String[] patterns = collection.findPatterns();
                for (String pattern : patterns) {
                    if (matchPattern(uri, pattern)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void removeAuthRole(String authRole) {
        if (authRole == null) {
            return;
        }
        if ("*".equals(authRole)) {
            this.allRoles = false;
            return;
        }
        if (ROLE_ALL_AUTHENTICATED_USERS.equals(authRole)) {
            this.authenticatedUsers = false;
            return;
        }
        int n = -1;
        int i = 0;
        while (true) {
            if (i >= this.authRoles.length) {
                break;
            }
            if (!this.authRoles[i].equals(authRole)) {
                i++;
            } else {
                n = i;
                break;
            }
        }
        if (n >= 0) {
            int j = 0;
            String[] results = new String[this.authRoles.length - 1];
            for (int i2 = 0; i2 < this.authRoles.length; i2++) {
                if (i2 != n) {
                    int i3 = j;
                    j++;
                    results[i3] = this.authRoles[i2];
                }
            }
            this.authRoles = results;
        }
    }

    public void removeCollection(SecurityCollection collection) {
        if (collection == null) {
            return;
        }
        int n = -1;
        int i = 0;
        while (true) {
            if (i >= this.collections.length) {
                break;
            }
            if (!this.collections[i].equals(collection)) {
                i++;
            } else {
                n = i;
                break;
            }
        }
        if (n >= 0) {
            int j = 0;
            SecurityCollection[] results = new SecurityCollection[this.collections.length - 1];
            for (int i2 = 0; i2 < this.collections.length; i2++) {
                if (i2 != n) {
                    int i3 = j;
                    j++;
                    results[i3] = this.collections[i2];
                }
            }
            this.collections = results;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SecurityConstraint[");
        for (int i = 0; i < this.collections.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.collections[i].getName());
        }
        sb.append(']');
        return sb.toString();
    }

    private boolean matchPattern(String path, String pattern) {
        if (path == null || path.length() == 0) {
            path = "/";
        }
        if (pattern == null || pattern.length() == 0) {
            pattern = "/";
        }
        if (path.equals(pattern)) {
            return true;
        }
        if (pattern.startsWith("/") && pattern.endsWith("/*")) {
            String pattern2 = pattern.substring(0, pattern.length() - 2);
            if (pattern2.length() == 0) {
                return true;
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            while (!pattern2.equals(path)) {
                int slash = path.lastIndexOf(47);
                if (slash > 0) {
                    path = path.substring(0, slash);
                } else {
                    return false;
                }
            }
            return true;
        }
        if (pattern.startsWith("*.")) {
            int slash2 = path.lastIndexOf(47);
            int period = path.lastIndexOf(46);
            if (slash2 >= 0 && period > slash2 && path.endsWith(pattern.substring(1))) {
                return true;
            }
            return false;
        }
        if (pattern.equals("/")) {
            return true;
        }
        return false;
    }

    public static SecurityConstraint[] createConstraints(ServletSecurityElement element, String urlPattern) {
        Set<SecurityConstraint> result = new HashSet<>();
        Collection<HttpMethodConstraintElement> methods = element.getHttpMethodConstraints();
        for (HttpMethodConstraintElement methodElement : methods) {
            SecurityConstraint constraint = createConstraint(methodElement, urlPattern, true);
            SecurityCollection collection = constraint.findCollections()[0];
            collection.addMethod(methodElement.getMethodName());
            result.add(constraint);
        }
        SecurityConstraint constraint2 = createConstraint(element, urlPattern, false);
        if (constraint2 != null) {
            SecurityCollection collection2 = constraint2.findCollections()[0];
            for (String name : element.getMethodNames()) {
                collection2.addOmittedMethod(name);
            }
            result.add(constraint2);
        }
        return (SecurityConstraint[]) result.toArray(new SecurityConstraint[0]);
    }

    private static SecurityConstraint createConstraint(HttpConstraintElement element, String urlPattern, boolean alwaysCreate) {
        SecurityConstraint constraint = new SecurityConstraint();
        SecurityCollection collection = new SecurityCollection();
        boolean create = alwaysCreate;
        if (element.getTransportGuarantee() != ServletSecurity.TransportGuarantee.NONE) {
            constraint.setUserConstraint(element.getTransportGuarantee().name());
            create = true;
        }
        if (element.getRolesAllowed().length > 0) {
            String[] roles = element.getRolesAllowed();
            for (String role : roles) {
                constraint.addAuthRole(role);
            }
            create = true;
        }
        if (element.getEmptyRoleSemantic() != ServletSecurity.EmptyRoleSemantic.PERMIT) {
            constraint.setAuthConstraint(true);
            create = true;
        }
        if (create) {
            collection.addPattern(urlPattern);
            constraint.addCollection(collection);
            return constraint;
        }
        return null;
    }

    public static SecurityConstraint[] findUncoveredHttpMethods(SecurityConstraint[] constraints, boolean denyUncoveredHttpMethods, Log log) {
        Set<String> coveredPatterns = new HashSet<>();
        Map<String, Set<String>> urlMethodMap = new HashMap<>();
        Map<String, Set<String>> urlOmittedMethodMap = new HashMap<>();
        List<SecurityConstraint> newConstraints = new ArrayList<>();
        for (SecurityConstraint securityConstraint : constraints) {
            SecurityCollection[] collections = securityConstraint.findCollections();
            for (SecurityCollection collection : collections) {
                String[] patterns = collection.findPatterns();
                String[] methods = collection.findMethods();
                String[] omittedMethods = collection.findOmittedMethods();
                if (methods.length == 0 && omittedMethods.length == 0) {
                    coveredPatterns.addAll(Arrays.asList(patterns));
                } else {
                    List<String> omNew = null;
                    if (omittedMethods.length != 0) {
                        omNew = Arrays.asList(omittedMethods);
                    }
                    for (String pattern : patterns) {
                        if (!coveredPatterns.contains(pattern)) {
                            if (methods.length == 0) {
                                Set<String> om = urlOmittedMethodMap.get(pattern);
                                if (om == null) {
                                    Set<String> om2 = new HashSet<>();
                                    urlOmittedMethodMap.put(pattern, om2);
                                    om2.addAll(omNew);
                                } else {
                                    om.retainAll(omNew);
                                }
                            } else {
                                urlMethodMap.computeIfAbsent(pattern, k -> {
                                    return new HashSet();
                                }).addAll(Arrays.asList(methods));
                            }
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, Set<String>> entry : urlMethodMap.entrySet()) {
            String pattern2 = entry.getKey();
            if (coveredPatterns.contains(pattern2)) {
                urlOmittedMethodMap.remove(pattern2);
            } else {
                Set<String> omittedMethods2 = urlOmittedMethodMap.remove(pattern2);
                Set<String> methods2 = entry.getValue();
                if (omittedMethods2 == null) {
                    StringBuilder msg = new StringBuilder();
                    for (String method : methods2) {
                        msg.append(method);
                        msg.append(' ');
                    }
                    if (denyUncoveredHttpMethods) {
                        log.info(sm.getString("securityConstraint.uncoveredHttpMethodFix", pattern2, msg.toString().trim()));
                        SecurityCollection collection2 = new SecurityCollection();
                        for (String method2 : methods2) {
                            collection2.addOmittedMethod(method2);
                        }
                        collection2.addPatternDecoded(pattern2);
                        collection2.setName("deny-uncovered-http-methods");
                        SecurityConstraint constraint = new SecurityConstraint();
                        constraint.setAuthConstraint(true);
                        constraint.addCollection(collection2);
                        newConstraints.add(constraint);
                    } else {
                        log.error(sm.getString("securityConstraint.uncoveredHttpMethod", pattern2, msg.toString().trim()));
                    }
                } else {
                    omittedMethods2.removeAll(methods2);
                    handleOmittedMethods(omittedMethods2, pattern2, denyUncoveredHttpMethods, newConstraints, log);
                }
            }
        }
        for (Map.Entry<String, Set<String>> entry2 : urlOmittedMethodMap.entrySet()) {
            String pattern3 = entry2.getKey();
            if (!coveredPatterns.contains(pattern3)) {
                handleOmittedMethods(entry2.getValue(), pattern3, denyUncoveredHttpMethods, newConstraints, log);
            }
        }
        return (SecurityConstraint[]) newConstraints.toArray(new SecurityConstraint[0]);
    }

    private static void handleOmittedMethods(Set<String> omittedMethods, String pattern, boolean denyUncoveredHttpMethods, List<SecurityConstraint> newConstraints, Log log) {
        if (omittedMethods.size() > 0) {
            StringBuilder msg = new StringBuilder();
            for (String method : omittedMethods) {
                msg.append(method);
                msg.append(' ');
            }
            if (denyUncoveredHttpMethods) {
                log.info(sm.getString("securityConstraint.uncoveredHttpOmittedMethodFix", pattern, msg.toString().trim()));
                SecurityCollection collection = new SecurityCollection();
                for (String method2 : omittedMethods) {
                    collection.addMethod(method2);
                }
                collection.addPatternDecoded(pattern);
                collection.setName("deny-uncovered-http-methods");
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setAuthConstraint(true);
                constraint.addCollection(collection);
                newConstraints.add(constraint);
                return;
            }
            log.error(sm.getString("securityConstraint.uncoveredHttpOmittedMethod", pattern, msg.toString().trim()));
        }
    }
}
