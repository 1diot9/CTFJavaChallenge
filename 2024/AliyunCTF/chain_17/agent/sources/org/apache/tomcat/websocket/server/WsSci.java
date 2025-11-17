package org.apache.tomcat.websocket.server;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HandlesTypes;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.server.ServerApplicationConfig;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.ServerEndpointConfig;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@HandlesTypes({ServerEndpoint.class, ServerApplicationConfig.class, Endpoint.class})
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/server/WsSci.class */
public class WsSci implements ServletContainerInitializer {
    @Override // jakarta.servlet.ServletContainerInitializer
    public void onStartup(Set<Class<?>> clazzes, ServletContext ctx) throws ServletException {
        WsServerContainer sc = init(ctx, true);
        if (clazzes == null || clazzes.size() == 0) {
            return;
        }
        Set<ServerApplicationConfig> serverApplicationConfigs = new HashSet<>();
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        try {
            String wsPackage = ContainerProvider.class.getName();
            String wsPackage2 = wsPackage.substring(0, wsPackage.lastIndexOf(46) + 1);
            for (Class<?> clazz : clazzes) {
                int modifiers = clazz.getModifiers();
                if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers) && isExported(clazz) && !clazz.getName().startsWith(wsPackage2)) {
                    if (ServerApplicationConfig.class.isAssignableFrom(clazz)) {
                        serverApplicationConfigs.add((ServerApplicationConfig) clazz.getConstructor(new Class[0]).newInstance(new Object[0]));
                    }
                    if (Endpoint.class.isAssignableFrom(clazz)) {
                        hashSet.add(clazz);
                    }
                    if (clazz.isAnnotationPresent(ServerEndpoint.class)) {
                        hashSet2.add(clazz);
                    }
                }
            }
            Set<ServerEndpointConfig> filteredEndpointConfigs = new HashSet<>();
            Set<Class<?>> filteredPojoEndpoints = new HashSet<>();
            if (serverApplicationConfigs.isEmpty()) {
                filteredPojoEndpoints.addAll(hashSet2);
            } else {
                for (ServerApplicationConfig config : serverApplicationConfigs) {
                    Set<ServerEndpointConfig> configFilteredEndpoints = config.getEndpointConfigs(hashSet);
                    if (configFilteredEndpoints != null) {
                        filteredEndpointConfigs.addAll(configFilteredEndpoints);
                    }
                    Set<Class<?>> configFilteredPojos = config.getAnnotatedEndpointClasses(hashSet2);
                    if (configFilteredPojos != null) {
                        filteredPojoEndpoints.addAll(configFilteredPojos);
                    }
                }
            }
            try {
                Iterator<ServerEndpointConfig> it = filteredEndpointConfigs.iterator();
                while (it.hasNext()) {
                    sc.addEndpoint(it.next());
                }
                Iterator<Class<?>> it2 = filteredPojoEndpoints.iterator();
                while (it2.hasNext()) {
                    sc.addEndpoint(it2.next(), true);
                }
            } catch (DeploymentException e) {
                throw new ServletException(e);
            }
        } catch (ReflectiveOperationException e2) {
            throw new ServletException(e2);
        }
    }

    private static boolean isExported(Class<?> type) {
        String packageName = type.getPackage().getName();
        Module module = type.getModule();
        return module.isExported(packageName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static WsServerContainer init(ServletContext servletContext, boolean initBySciMechanism) {
        WsServerContainer sc = new WsServerContainer(servletContext);
        servletContext.setAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE, sc);
        servletContext.addListener((ServletContext) new WsSessionListener(sc));
        if (initBySciMechanism) {
            servletContext.addListener((ServletContext) new WsContextListener());
        }
        return sc;
    }
}
