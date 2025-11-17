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

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/UserMBean.class */
public class UserMBean extends BaseModelMBean {
    private static final StringManager sm = StringManager.getManager((Class<?>) UserMBean.class);
    protected final Registry registry = MBeanUtils.createRegistry();
    protected final ManagedBean managed = this.registry.findManagedBean("User");

    public String[] getGroups() {
        User user = (User) this.resource;
        List<String> results = new ArrayList<>();
        Iterator<Group> groups = user.getGroups();
        while (groups.hasNext()) {
            Group group = null;
            try {
                group = groups.next();
                ObjectName oname = MBeanUtils.createObjectName(this.managed.getDomain(), group);
                results.add(oname.toString());
            } catch (MalformedObjectNameException e) {
                throw new IllegalArgumentException(sm.getString("userMBean.createError.group", group), e);
            }
        }
        return (String[]) results.toArray(new String[0]);
    }

    public String[] getRoles() {
        User user = (User) this.resource;
        List<String> results = new ArrayList<>();
        Iterator<Role> roles = user.getRoles();
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

    public void addGroup(String groupname) {
        User user = (User) this.resource;
        if (user == null) {
            return;
        }
        Group group = user.getUserDatabase().findGroup(groupname);
        if (group == null) {
            throw new IllegalArgumentException(sm.getString("userMBean.invalidGroup", groupname));
        }
        user.addGroup(group);
    }

    public void addRole(String rolename) {
        User user = (User) this.resource;
        if (user == null) {
            return;
        }
        Role role = user.getUserDatabase().findRole(rolename);
        if (role == null) {
            throw new IllegalArgumentException(sm.getString("userMBean.invalidRole", rolename));
        }
        user.addRole(role);
    }

    public void removeGroup(String groupname) {
        User user = (User) this.resource;
        if (user == null) {
            return;
        }
        Group group = user.getUserDatabase().findGroup(groupname);
        if (group == null) {
            throw new IllegalArgumentException(sm.getString("userMBean.invalidGroup", groupname));
        }
        user.removeGroup(group);
    }

    public void removeRole(String rolename) {
        User user = (User) this.resource;
        if (user == null) {
            return;
        }
        Role role = user.getUserDatabase().findRole(rolename);
        if (role == null) {
            throw new IllegalArgumentException(sm.getString("userMBean.invalidRole", rolename));
        }
        user.removeRole(role);
    }
}
