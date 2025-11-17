package org.apache.catalina.manager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/manager/Constants.class */
public class Constants {
    public static final String Package = "org.apache.catalina.manager";
    public static final String REL_EXTERNAL = "rel=\"noopener noreferrer\"";
    public static final String HTML_HEADER_SECTION = "<html>\n<head>\n<link rel=\"stylesheet\" href=\"{0}/css/manager.css\">\n";
    public static final String BODY_HEADER_SECTION = "<title>{0}</title>\n</head>\n\n<body bgcolor=\"#FFFFFF\">\n\n<table cellspacing=\"4\" border=\"0\">\n <tr>\n  <td colspan=\"2\">\n   <a href=\"https://tomcat.apache.org/\" rel=\"noopener noreferrer\">\n    <img class='tomcat-logo' alt=\"The Tomcat Servlet/JSP Container\"\n         src=\"{0}/images/tomcat.svg\">\n   </a>\n   <a href=\"https://www.apache.org/\" rel=\"noopener noreferrer\">\n    <img border=\"0\" alt=\"The Apache Software Foundation\" align=\"right\"\n         src=\"{0}/images/asf-logo.svg\" style=\"width: 266px; height: 83px;\">\n   </a>\n  </td>\n </tr>\n</table>\n<hr size=\"1\" noshade=\"noshade\">\n<table cellspacing=\"4\" border=\"0\">\n <tr>\n  <td class=\"page-title\" bordercolor=\"#000000\" align=\"left\" nowrap>\n   <font size=\"+2\">{1}</font>\n  </td>\n </tr>\n</table>\n<br>\n\n";
    public static final String MESSAGE_SECTION = org.apache.catalina.manager.host.Constants.MESSAGE_SECTION;
    public static final String MANAGER_SECTION = org.apache.catalina.manager.host.Constants.MANAGER_SECTION;
    public static final String SERVER_HEADER_SECTION = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"8\" class=\"title\">{0}</td>\n</tr>\n<tr>\n <td class=\"header-center\"><small>{1}</small></td>\n <td class=\"header-center\"><small>{2}</small></td>\n <td class=\"header-center\"><small>{3}</small></td>\n <td class=\"header-center\"><small>{4}</small></td>\n <td class=\"header-center\"><small>{5}</small></td>\n <td class=\"header-center\"><small>{6}</small></td>\n <td class=\"header-center\"><small>{7}</small></td>\n <td class=\"header-center\"><small>{8}</small></td>\n</tr>\n";
    public static final String SERVER_ROW_SECTION = "<tr>\n <td class=\"row-center\"><small>{0}</small></td>\n <td class=\"row-center\"><small>{1}</small></td>\n <td class=\"row-center\"><small>{2}</small></td>\n <td class=\"row-center\"><small>{3}</small></td>\n <td class=\"row-center\"><small>{4}</small></td>\n <td class=\"row-center\"><small>{5}</small></td>\n <td class=\"row-center\"><small>{6}</small></td>\n <td class=\"row-center\"><small>{7}</small></td>\n</tr>\n</table>\n<br>\n\n";
    public static final String HTML_TAIL_SECTION = org.apache.catalina.manager.host.Constants.HTML_TAIL_SECTION;
    public static final String CHARSET = "utf-8";
    public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    public static final String XML_STYLE = "<?xml-stylesheet type=\"text/xsl\" href=\"{0}/xform.xsl\" ?>\n";
}
