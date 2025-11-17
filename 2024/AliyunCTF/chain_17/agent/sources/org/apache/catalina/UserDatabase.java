package org.apache.catalina;

import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/UserDatabase.class */
public interface UserDatabase {
    Iterator<Group> getGroups();

    String getId();

    Iterator<Role> getRoles();

    Iterator<User> getUsers();

    void close() throws Exception;

    Group createGroup(String str, String str2);

    Role createRole(String str, String str2);

    User createUser(String str, String str2, String str3);

    Group findGroup(String str);

    Role findRole(String str);

    User findUser(String str);

    void open() throws Exception;

    void removeGroup(Group group);

    void removeRole(Role role);

    void removeUser(User user);

    void save() throws Exception;

    default void modifiedGroup(Group group) {
    }

    default void modifiedRole(Role role) {
    }

    default void modifiedUser(User user) {
    }

    default void backgroundProcess() {
    }

    default boolean isAvailable() {
        return true;
    }

    default boolean isSparse() {
        return false;
    }
}
