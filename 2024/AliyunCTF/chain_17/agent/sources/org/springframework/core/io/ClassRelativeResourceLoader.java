package org.springframework.core.io;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/ClassRelativeResourceLoader.class */
public class ClassRelativeResourceLoader extends DefaultResourceLoader {
    private final Class<?> clazz;

    public ClassRelativeResourceLoader(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        this.clazz = clazz;
        setClassLoader(clazz.getClassLoader());
    }

    @Override // org.springframework.core.io.DefaultResourceLoader
    protected Resource getResourceByPath(String path) {
        return new ClassRelativeContextResource(path, this.clazz);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/ClassRelativeResourceLoader$ClassRelativeContextResource.class */
    private static class ClassRelativeContextResource extends ClassPathResource implements ContextResource {
        private final Class<?> clazz;

        public ClassRelativeContextResource(String path, Class<?> clazz) {
            super(path, clazz);
            this.clazz = clazz;
        }

        @Override // org.springframework.core.io.ContextResource
        public String getPathWithinContext() {
            return getPath();
        }

        @Override // org.springframework.core.io.ClassPathResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public Resource createRelative(String relativePath) {
            String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
            return new ClassRelativeContextResource(pathToUse, this.clazz);
        }
    }
}
