package org.apache.catalina.users;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/users/DataSourceUserDatabaseFactory.class */
public class DataSourceUserDatabaseFactory implements ObjectFactory {
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        if (obj == null || !(obj instanceof Reference)) {
            return null;
        }
        Reference ref = (Reference) obj;
        if (!"org.apache.catalina.UserDatabase".equals(ref.getClassName())) {
            return null;
        }
        DataSource dataSource = null;
        String dataSourceName = null;
        RefAddr ra = ref.get("dataSourceName");
        if (ra != null) {
            dataSourceName = ra.getContent().toString();
            dataSource = (DataSource) nameCtx.lookup(dataSourceName);
        }
        DataSourceUserDatabase database = new DataSourceUserDatabase(dataSource, name.toString());
        database.setDataSourceName(dataSourceName);
        RefAddr ra2 = ref.get(AbstractHtmlInputElementTag.READONLY_ATTRIBUTE);
        if (ra2 != null) {
            database.setReadonly(Boolean.parseBoolean(ra2.getContent().toString()));
        }
        RefAddr ra3 = ref.get("userTable");
        if (ra3 != null) {
            database.setUserTable(ra3.getContent().toString());
        }
        RefAddr ra4 = ref.get("groupTable");
        if (ra4 != null) {
            database.setGroupTable(ra4.getContent().toString());
        }
        RefAddr ra5 = ref.get("roleTable");
        if (ra5 != null) {
            database.setRoleTable(ra5.getContent().toString());
        }
        RefAddr ra6 = ref.get("userRoleTable");
        if (ra6 != null) {
            database.setUserRoleTable(ra6.getContent().toString());
        }
        RefAddr ra7 = ref.get("userGroupTable");
        if (ra7 != null) {
            database.setUserGroupTable(ra7.getContent().toString());
        }
        RefAddr ra8 = ref.get("groupRoleTable");
        if (ra8 != null) {
            database.setGroupRoleTable(ra8.getContent().toString());
        }
        RefAddr ra9 = ref.get("roleNameCol");
        if (ra9 != null) {
            database.setRoleNameCol(ra9.getContent().toString());
        }
        RefAddr ra10 = ref.get("roleAndGroupDescriptionCol");
        if (ra10 != null) {
            database.setRoleAndGroupDescriptionCol(ra10.getContent().toString());
        }
        RefAddr ra11 = ref.get("groupNameCol");
        if (ra11 != null) {
            database.setGroupNameCol(ra11.getContent().toString());
        }
        RefAddr ra12 = ref.get("userCredCol");
        if (ra12 != null) {
            database.setUserCredCol(ra12.getContent().toString());
        }
        RefAddr ra13 = ref.get("userFullNameCol");
        if (ra13 != null) {
            database.setUserFullNameCol(ra13.getContent().toString());
        }
        RefAddr ra14 = ref.get("userNameCol");
        if (ra14 != null) {
            database.setUserNameCol(ra14.getContent().toString());
        }
        database.open();
        return database;
    }
}
