package org.h2.server.web;

import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/web/ConnectionInfo.class */
public class ConnectionInfo implements Comparable<ConnectionInfo> {
    public String driver;
    public String url;
    public String user;
    String name;
    int lastAccess;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConnectionInfo() {
    }

    public ConnectionInfo(String str) {
        String[] arraySplit = StringUtils.arraySplit(str, '|', false);
        this.name = get(arraySplit, 0);
        this.driver = get(arraySplit, 1);
        this.url = get(arraySplit, 2);
        this.user = get(arraySplit, 3);
    }

    private static String get(String[] strArr, int i) {
        return (strArr == null || strArr.length <= i) ? "" : strArr[i];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getString() {
        return StringUtils.arrayCombine(new String[]{this.name, this.driver, this.url, this.user}, '|');
    }

    @Override // java.lang.Comparable
    public int compareTo(ConnectionInfo connectionInfo) {
        return Integer.compare(connectionInfo.lastAccess, this.lastAccess);
    }
}
