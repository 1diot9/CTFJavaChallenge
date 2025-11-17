package org.apache.catalina.mbeans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.tomcat.util.modeler.BaseModelMBean;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/DataSourceUserDatabaseMBean.class */
public class DataSourceUserDatabaseMBean extends BaseModelMBean {
    protected final Registry registry = MBeanUtils.createRegistry();
    protected final ManagedBean managed = this.registry.findManagedBean("DataSourceUserDatabase");

    public String[] getGroups() {
        UserDatabase database = (UserDatabase) this.resource;
        List<String> results = new ArrayList<>();
        Iterator<Group> groups = database.getGroups();
        while (groups.hasNext()) {
            Group group = groups.next();
            results.add(group.getGroupname());
        }
        return (String[]) results.toArray(new String[0]);
    }

    public String[] getRoles() {
        UserDatabase database = (UserDatabase) this.resource;
        List<String> results = new ArrayList<>();
        Iterator<Role> roles = database.getRoles();
        while (roles.hasNext()) {
            Role role = roles.next();
            results.add(role.getRolename());
        }
        return (String[]) results.toArray(new String[0]);
    }

    public String[] getUsers() {
        UserDatabase database = (UserDatabase) this.resource;
        List<String> results = new ArrayList<>();
        Iterator<User> users = database.getUsers();
        while (users.hasNext()) {
            User user = users.next();
            results.add(user.getUsername());
        }
        return (String[]) results.toArray(new String[0]);
    }

    public String createGroup(String groupname, String description) {
        UserDatabase database = (UserDatabase) this.resource;
        Group group = database.createGroup(groupname, description);
        return group.getGroupname();
    }

    public String createRole(String rolename, String description) {
        UserDatabase database = (UserDatabase) this.resource;
        Role role = database.createRole(rolename, description);
        return role.getRolename();
    }

    public String createUser(String username, String password, String fullName) {
        UserDatabase database = (UserDatabase) this.resource;
        User user = database.createUser(username, password, fullName);
        return user.getUsername();
    }

    public void removeGroup(String groupname) {
        UserDatabase database = (UserDatabase) this.resource;
        Group group = database.findGroup(groupname);
        if (group == null) {
            return;
        }
        database.removeGroup(group);
    }

    public void removeRole(String rolename) {
        UserDatabase database = (UserDatabase) this.resource;
        Role role = database.findRole(rolename);
        if (role == null) {
            return;
        }
        database.removeRole(role);
    }

    public void removeUser(String username) {
        UserDatabase database = (UserDatabase) this.resource;
        User user = database.findUser(username);
        if (user == null) {
            return;
        }
        database.removeUser(user);
    }

    public void changeUserPassword(String username, String password) {
        UserDatabase database = (UserDatabase) this.resource;
        User user = database.findUser(username);
        if (user != null) {
            user.setPassword(password);
        }
    }

    public void addUserRole(String username, String rolename) {
        UserDatabase database = (UserDatabase) this.resource;
        User user = database.findUser(username);
        Role role = database.findRole(rolename);
        if (user != null && role != null) {
            user.addRole(role);
        }
    }

    public void removeUserRole(String username, String rolename) {
        UserDatabase database = (UserDatabase) this.resource;
        User user = database.findUser(username);
        Role role = database.findRole(rolename);
        if (user != null && role != null) {
            user.removeRole(role);
        }
    }

    public String[] getUserRoles(String username) {
        UserDatabase database = (UserDatabase) this.resource;
        User user = database.findUser(username);
        if (user != null) {
            List<String> results = new ArrayList<>();
            Iterator<Role> roles = user.getRoles();
            while (roles.hasNext()) {
                Role role = roles.next();
                results.add(role.getRolename());
            }
            return (String[]) results.toArray(new String[0]);
        }
        return null;
    }

    public void addUserGroup(String username, String groupname) {
        UserDatabase database = (UserDatabase) this.resource;
        User user = database.findUser(username);
        Group group = database.findGroup(groupname);
        if (user != null && group != null) {
            user.addGroup(group);
        }
    }

    public void removeUserGroup(String username, String groupname) {
        UserDatabase database = (UserDatabase) this.resource;
        User user = database.findUser(username);
        Group group = database.findGroup(groupname);
        if (user != null && group != null) {
            user.removeGroup(group);
        }
    }

    public String[] getUserGroups(String username) {
        UserDatabase database = (UserDatabase) this.resource;
        User user = database.findUser(username);
        if (user != null) {
            List<String> results = new ArrayList<>();
            Iterator<Group> groups = user.getGroups();
            while (groups.hasNext()) {
                Group group = groups.next();
                results.add(group.getGroupname());
            }
            return (String[]) results.toArray(new String[0]);
        }
        return null;
    }

    public void addGroupRole(String groupname, String rolename) {
        UserDatabase database = (UserDatabase) this.resource;
        Group group = database.findGroup(groupname);
        Role role = database.findRole(rolename);
        if (group != null && role != null) {
            group.addRole(role);
        }
    }

    public void removeGroupRole(String groupname, String rolename) {
        UserDatabase database = (UserDatabase) this.resource;
        Group group = database.findGroup(groupname);
        Role role = database.findRole(rolename);
        if (group != null && role != null) {
            group.removeRole(role);
        }
    }

    public String[] getGroupRoles(String groupname) {
        UserDatabase database = (UserDatabase) this.resource;
        Group group = database.findGroup(groupname);
        if (group != null) {
            List<String> results = new ArrayList<>();
            Iterator<Role> roles = group.getRoles();
            while (roles.hasNext()) {
                Role role = roles.next();
                results.add(role.getRolename());
            }
            return (String[]) results.toArray(new String[0]);
        }
        return null;
    }
}
