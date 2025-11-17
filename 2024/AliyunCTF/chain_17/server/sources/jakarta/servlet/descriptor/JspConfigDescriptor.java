package jakarta.servlet.descriptor;

import java.util.Collection;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/descriptor/JspConfigDescriptor.class */
public interface JspConfigDescriptor {
    Collection<TaglibDescriptor> getTaglibs();

    Collection<JspPropertyGroupDescriptor> getJspPropertyGroups();
}
