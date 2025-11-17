package org.yaml.snakeyaml.extensions.compactnotation;

import org.yaml.snakeyaml.LoaderOptions;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/extensions/compactnotation/PackageCompactConstructor.class */
public class PackageCompactConstructor extends CompactConstructor {
    private final String packageName;

    public PackageCompactConstructor(String packageName) {
        super(new LoaderOptions());
        this.packageName = packageName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.yaml.snakeyaml.constructor.Constructor
    public Class<?> getClassForName(String name) throws ClassNotFoundException {
        if (name.indexOf(46) < 0) {
            try {
                Class<?> clazz = Class.forName(this.packageName + "." + name);
                return clazz;
            } catch (ClassNotFoundException e) {
            }
        }
        return super.getClassForName(name);
    }
}
