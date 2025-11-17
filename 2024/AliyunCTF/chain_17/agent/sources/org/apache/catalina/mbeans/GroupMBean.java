package org.apache.catalina.mbeans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.tomcat.util.modeler.BaseModelMBean;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/GroupMBean.class */
public class GroupMBean extends BaseModelMBean {
    private static final StringManager sm = StringManager.getManager((Class<?>) GroupMBean.class);
    protected final Registry registry = MBeanUtils.createRegistry();
    protected final ManagedBean managed = this.registry.findManagedBean("Group");

    public String[] getRoles() {
        Group group = (Group) this.resource;
        List<String> results = new ArrayList<>();
        Iterator<Role> roles = group.getRoles();
        while (roles.hasNext()) {
            Role role = null;
            try {
                role = roles.next();
                ObjectName oname = MBeanUtils.createObjectName(this.managed.getDomain(), role);
                results.add(oname.toString());
            } catch (MalformedObjectNameException e) {
                throw new IllegalArgumentException(sm.getString("userMBean.createError.role", role), e);
            }
        }
        return (String[]) results.toArray(new String[0]);
    }

    public String[] getUsers() {
        Group group = (Group) this.resource;
        List<String> results = new ArrayList<>();
        Iterator<User> users = group.getUsers();
        while (users.hasNext()) {
            User user = null;
            try {
                user = users.next();
                ObjectName oname = MBeanUtils.createObjectName(this.managed.getDomain(), user);
                results.add(oname.toString());
            } catch (MalformedObjectNameException e) {
                throw new IllegalArgumentException(sm.getString("userMBean.createError.user", user), e);
            }
        }
        return (String[]) results.toArray(new String[0]);
    }

    public void addRole(String rolename) {
        Group group = (Group) this.resource;
        if (group == null) {
            return;
        }
        Role role = group.getUserDatabase().findRole(rolename);
        if (role == null) {
            throw new IllegalArgumentException(sm.getString("userMBean.invalidRole", rolename));
        }
        group.addRole(role);
    }

    public void removeRole(String rolename) {
        Group group = (Group) this.resource;
        if (group == null) {
            return;
        }
        Role role = group.getUserDatabase().findRole(rolename);
        if (role == null) {
            throw new IllegalArgumentException(sm.getString("userMBean.invalidRole", rolename));
        }
        group.removeRole(role);
    }
}
