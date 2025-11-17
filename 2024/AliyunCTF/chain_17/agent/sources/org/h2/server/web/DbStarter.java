package org.h2.server.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.h2.Driver;
import org.h2.tools.Server;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/web/DbStarter.class */
public class DbStarter implements ServletContextListener {
    private Connection conn;
    private Server server;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            Driver.load();
            ServletContext servletContext = servletContextEvent.getServletContext();
            String parameter = getParameter(servletContext, "db.url", "jdbc:h2:~/test");
            String parameter2 = getParameter(servletContext, "db.user", "sa");
            String parameter3 = getParameter(servletContext, "db.password", "sa");
            String parameter4 = getParameter(servletContext, "db.tcpServer", null);
            if (parameter4 != null) {
                this.server = Server.createTcpServer(StringUtils.arraySplit(parameter4, ' ', true));
                this.server.start();
            }
            this.conn = DriverManager.getConnection(parameter, parameter2, parameter3);
            servletContext.setAttribute("connection", this.conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getParameter(ServletContext servletContext, String str, String str2) {
        String initParameter = servletContext.getInitParameter(str);
        return initParameter == null ? str2 : initParameter;
    }

    public Connection getConnection() {
        return this.conn;
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            Statement createStatement = this.conn.createStatement();
            createStatement.execute("SHUTDOWN");
            createStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.conn.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        if (this.server != null) {
            this.server.stop();
            this.server = null;
        }
    }
}
