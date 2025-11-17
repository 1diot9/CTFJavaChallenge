package org.springframework.beans.support;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.beans.propertyeditors.InputSourceEditor;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import org.springframework.beans.propertyeditors.PathEditor;
import org.springframework.beans.propertyeditors.ReaderEditor;
import org.springframework.beans.propertyeditors.URIEditor;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.xml.sax.InputSource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/support/ResourceEditorRegistrar.class */
public class ResourceEditorRegistrar implements PropertyEditorRegistrar {
    private final PropertyResolver propertyResolver;
    private final ResourceLoader resourceLoader;

    public ResourceEditorRegistrar(ResourceLoader resourceLoader, PropertyResolver propertyResolver) {
        this.resourceLoader = resourceLoader;
        this.propertyResolver = propertyResolver;
    }

    @Override // org.springframework.beans.PropertyEditorRegistrar
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        ResourceEditor baseEditor = new ResourceEditor(this.resourceLoader, this.propertyResolver);
        doRegisterEditor(registry, Resource.class, baseEditor);
        doRegisterEditor(registry, ContextResource.class, baseEditor);
        doRegisterEditor(registry, WritableResource.class, baseEditor);
        doRegisterEditor(registry, InputStream.class, new InputStreamEditor(baseEditor));
        doRegisterEditor(registry, InputSource.class, new InputSourceEditor(baseEditor));
        doRegisterEditor(registry, File.class, new FileEditor(baseEditor));
        doRegisterEditor(registry, Path.class, new PathEditor(baseEditor));
        doRegisterEditor(registry, Reader.class, new ReaderEditor(baseEditor));
        doRegisterEditor(registry, URL.class, new URLEditor(baseEditor));
        ClassLoader classLoader = this.resourceLoader.getClassLoader();
        doRegisterEditor(registry, URI.class, new URIEditor(classLoader));
        doRegisterEditor(registry, Class.class, new ClassEditor(classLoader));
        doRegisterEditor(registry, Class[].class, new ClassArrayEditor(classLoader));
        ResourceLoader resourceLoader = this.resourceLoader;
        if (resourceLoader instanceof ResourcePatternResolver) {
            ResourcePatternResolver resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
            doRegisterEditor(registry, Resource[].class, new ResourceArrayPropertyEditor(resourcePatternResolver, this.propertyResolver));
        }
    }

    private void doRegisterEditor(PropertyEditorRegistry registry, Class<?> requiredType, PropertyEditor editor) {
        if (registry instanceof PropertyEditorRegistrySupport) {
            PropertyEditorRegistrySupport registrySupport = (PropertyEditorRegistrySupport) registry;
            registrySupport.overrideDefaultEditor(requiredType, editor);
        } else {
            registry.registerCustomEditor(requiredType, editor);
        }
    }
}
