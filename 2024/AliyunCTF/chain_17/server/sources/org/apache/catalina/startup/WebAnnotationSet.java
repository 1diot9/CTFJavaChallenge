package org.apache.catalina.startup;

import jakarta.annotation.Resource;
import jakarta.annotation.Resources;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RunAs;
import jakarta.servlet.ServletSecurityElement;
import jakarta.servlet.annotation.ServletSecurity;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationServletRegistration;
import org.apache.catalina.util.Introspection;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.MessageDestinationRef;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/WebAnnotationSet.class */
public class WebAnnotationSet {
    private static final String SEPARATOR = "/";
    private static final String MAPPED_NAME_PROPERTY = "mappedName";
    protected static final StringManager sm = StringManager.getManager(Constants.Package);

    public static void loadApplicationAnnotations(Context context) {
        loadApplicationListenerAnnotations(context);
        loadApplicationFilterAnnotations(context);
        loadApplicationServletAnnotations(context);
    }

    protected static void loadApplicationListenerAnnotations(Context context) {
        String[] applicationListeners = context.findApplicationListeners();
        for (String className : applicationListeners) {
            Class<?> clazz = Introspection.loadClass(context, className);
            if (clazz != null) {
                loadClassAnnotation(context, clazz);
                loadFieldsAnnotation(context, clazz);
                loadMethodsAnnotation(context, clazz);
            }
        }
    }

    protected static void loadApplicationFilterAnnotations(Context context) {
        FilterDef[] filterDefs = context.findFilterDefs();
        for (FilterDef filterDef : filterDefs) {
            Class<?> clazz = Introspection.loadClass(context, filterDef.getFilterClass());
            if (clazz != null) {
                loadClassAnnotation(context, clazz);
                loadFieldsAnnotation(context, clazz);
                loadMethodsAnnotation(context, clazz);
            }
        }
    }

    protected static void loadApplicationServletAnnotations(Context context) {
        Class<?> clazz;
        Container[] children = context.findChildren();
        for (Container child : children) {
            if (child instanceof Wrapper) {
                Wrapper wrapper = (Wrapper) child;
                if (wrapper.getServletClass() != null && (clazz = Introspection.loadClass(context, wrapper.getServletClass())) != null) {
                    loadClassAnnotation(context, clazz);
                    loadFieldsAnnotation(context, clazz);
                    loadMethodsAnnotation(context, clazz);
                    RunAs runAs = (RunAs) clazz.getAnnotation(RunAs.class);
                    if (runAs != null) {
                        wrapper.setRunAs(runAs.value());
                    }
                    ServletSecurity servletSecurity = (ServletSecurity) clazz.getAnnotation(ServletSecurity.class);
                    if (servletSecurity != null) {
                        context.addServletSecurity(new ApplicationServletRegistration(wrapper, context), new ServletSecurityElement(servletSecurity));
                    }
                }
            }
        }
    }

    protected static void loadClassAnnotation(Context context, Class<?> clazz) {
        Resource resourceAnnotation = (Resource) clazz.getAnnotation(Resource.class);
        if (resourceAnnotation != null) {
            addResource(context, resourceAnnotation);
        }
        Resources resourcesAnnotation = (Resources) clazz.getAnnotation(Resources.class);
        if (resourcesAnnotation != null && resourcesAnnotation.value() != null) {
            for (Resource resource : resourcesAnnotation.value()) {
                addResource(context, resource);
            }
        }
        DeclareRoles declareRolesAnnotation = (DeclareRoles) clazz.getAnnotation(DeclareRoles.class);
        if (declareRolesAnnotation != null && declareRolesAnnotation.value() != null) {
            for (String role : declareRolesAnnotation.value()) {
                context.addSecurityRole(role);
            }
        }
    }

    protected static void loadFieldsAnnotation(Context context, Class<?> clazz) {
        Field[] fields = Introspection.getDeclaredFields(clazz);
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                Resource annotation = (Resource) field.getAnnotation(Resource.class);
                if (annotation != null) {
                    String defaultName = clazz.getName() + "/" + field.getName();
                    Class<?> defaultType = field.getType();
                    addResource(context, annotation, defaultName, defaultType);
                }
            }
        }
    }

    protected static void loadMethodsAnnotation(Context context, Class<?> clazz) {
        Method[] methods = Introspection.getDeclaredMethods(clazz);
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {
                Resource annotation = (Resource) method.getAnnotation(Resource.class);
                if (annotation != null) {
                    if (!Introspection.isValidSetter(method)) {
                        throw new IllegalArgumentException(sm.getString("webAnnotationSet.invalidInjection"));
                    }
                    String defaultName = clazz.getName() + "/" + Introspection.getPropertyName(method);
                    Class<?> defaultType = method.getParameterTypes()[0];
                    addResource(context, annotation, defaultName, defaultType);
                }
            }
        }
    }

    protected static void addResource(Context context, Resource annotation) {
        addResource(context, annotation, null, null);
    }

    protected static void addResource(Context context, Resource annotation, String defaultName, Class<?> defaultType) {
        String name = getName(annotation, defaultName);
        String type = getType(annotation, defaultType);
        if (type.equals("java.lang.String") || type.equals("java.lang.Character") || type.equals("java.lang.Integer") || type.equals("java.lang.Boolean") || type.equals("java.lang.Double") || type.equals("java.lang.Byte") || type.equals("java.lang.Short") || type.equals("java.lang.Long") || type.equals("java.lang.Float")) {
            ContextEnvironment resource = new ContextEnvironment();
            resource.setName(name);
            resource.setType(type);
            resource.setDescription(annotation.description());
            resource.setProperty(MAPPED_NAME_PROPERTY, annotation.mappedName());
            resource.setLookupName(annotation.lookup());
            context.getNamingResources().addEnvironment(resource);
            return;
        }
        if (type.equals("javax.xml.rpc.Service")) {
            ContextService service = new ContextService();
            service.setName(name);
            service.setWsdlfile(annotation.mappedName());
            service.setType(type);
            service.setDescription(annotation.description());
            service.setLookupName(annotation.lookup());
            context.getNamingResources().addService(service);
            return;
        }
        if (type.equals("javax.sql.DataSource") || type.equals("javax.jms.ConnectionFactory") || type.equals("javax.jms.QueueConnectionFactory") || type.equals("javax.jms.TopicConnectionFactory") || type.equals("jakarta.mail.Session") || type.equals("java.net.URL") || type.equals("javax.resource.cci.ConnectionFactory") || type.equals("org.omg.CORBA_2_3.ORB") || type.endsWith("ConnectionFactory")) {
            ContextResource resource2 = new ContextResource();
            resource2.setName(name);
            resource2.setType(type);
            if (annotation.authenticationType() == Resource.AuthenticationType.CONTAINER) {
                resource2.setAuth("Container");
            } else if (annotation.authenticationType() == Resource.AuthenticationType.APPLICATION) {
                resource2.setAuth("Application");
            }
            resource2.setScope(annotation.shareable() ? "Shareable" : "Unshareable");
            resource2.setProperty(MAPPED_NAME_PROPERTY, annotation.mappedName());
            resource2.setDescription(annotation.description());
            resource2.setLookupName(annotation.lookup());
            context.getNamingResources().addResource(resource2);
            return;
        }
        if (type.equals("javax.jms.Queue") || type.equals("javax.jms.Topic")) {
            MessageDestinationRef resource3 = new MessageDestinationRef();
            resource3.setName(name);
            resource3.setType(type);
            resource3.setUsage(annotation.mappedName());
            resource3.setDescription(annotation.description());
            resource3.setLookupName(annotation.lookup());
            context.getNamingResources().addMessageDestinationRef(resource3);
            return;
        }
        ContextResourceEnvRef resource4 = new ContextResourceEnvRef();
        resource4.setName(name);
        resource4.setType(type);
        resource4.setProperty(MAPPED_NAME_PROPERTY, annotation.mappedName());
        resource4.setDescription(annotation.description());
        resource4.setLookupName(annotation.lookup());
        context.getNamingResources().addResourceEnvRef(resource4);
    }

    private static String getType(Resource annotation, Class<?> defaultType) {
        Class<?> type = annotation.type();
        if (type == null || type.equals(Object.class)) {
            if (defaultType != null) {
                type = defaultType;
            } else {
                type = Object.class;
            }
        }
        return Introspection.convertPrimitiveType(type).getCanonicalName();
    }

    private static String getName(Resource annotation, String defaultName) {
        String name = annotation.name();
        if ((name == null || name.equals("")) && defaultName != null) {
            name = defaultName;
        }
        return name;
    }
}
