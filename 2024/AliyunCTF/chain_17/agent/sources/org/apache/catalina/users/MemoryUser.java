package org.apache.catalina.users;

import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.security.Escape;

@Deprecated
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/users/MemoryUser.class */
public class MemoryUser extends GenericUser<MemoryUserDatabase> {
    MemoryUser(MemoryUserDatabase database, String username, String password, String fullName) {
        super(database, username, password, fullName, null, null);
    }

    public String toXml() {
        StringBuilder sb = new StringBuilder("<user username=\"");
        sb.append(Escape.xml(this.username));
        sb.append("\" password=\"");
        sb.append(Escape.xml(this.password));
        sb.append("\"");
        if (this.fullName != null) {
            sb.append(" fullName=\"");
            sb.append(Escape.xml(this.fullName));
            sb.append("\"");
        }
        sb.append(" groups=\"");
        StringUtils.join((Iterable) this.groups, ',', x -> {
            return Escape.xml(x.getGroupname());
        }, sb);
        sb.append("\"");
        sb.append(" roles=\"");
        StringUtils.join((Iterable) this.roles, ',', x2 -> {
            return Escape.xml(x2.getRolename());
        }, sb);
        sb.append("\"");
        sb.append("/>");
        return sb.toString();
    }

    @Override // java.security.Principal
    public String toString() {
        StringBuilder sb = new StringBuilder("User username=\"");
        sb.append(Escape.xml(this.username));
        sb.append("\"");
        if (this.fullName != null) {
            sb.append(", fullName=\"");
            sb.append(Escape.xml(this.fullName));
            sb.append("\"");
        }
        sb.append(", groups=\"");
        StringUtils.join((Iterable) this.groups, ',', x -> {
            return Escape.xml(x.getGroupname());
        }, sb);
        sb.append("\"");
        sb.append(", roles=\"");
        StringUtils.join((Iterable) this.roles, ',', x2 -> {
            return Escape.xml(x2.getRolename());
        }, sb);
        sb.append("\"");
        return sb.toString();
    }
}
