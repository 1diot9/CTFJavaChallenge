package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.LoaderOptions;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/constructor/CustomClassLoaderConstructor.class */
public class CustomClassLoaderConstructor extends Constructor {
    private final ClassLoader loader;

    public CustomClassLoaderConstructor(ClassLoader loader, LoaderOptions loadingConfig) {
        this(Object.class, loader, loadingConfig);
    }

    public CustomClassLoaderConstructor(Class<? extends Object> theRoot, ClassLoader theLoader, LoaderOptions loadingConfig) {
        super(theRoot, loadingConfig);
        if (theLoader == null) {
            throw new NullPointerException("Loader must be provided.");
        }
        this.loader = theLoader;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.yaml.snakeyaml.constructor.Constructor
    public Class<?> getClassForName(String name) throws ClassNotFoundException {
        return Class.forName(name, true, this.loader);
    }
}
