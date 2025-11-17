package org.springframework.boot.loader.jar;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

/* loaded from: agent.jar:org/springframework/boot/loader/jar/ManifestInfo.class */
class ManifestInfo {
    private static final Attributes.Name MULTI_RELEASE = new Attributes.Name("Multi-Release");
    static final ManifestInfo NONE = new ManifestInfo(null, false);
    private final Manifest manifest;
    private volatile Boolean multiRelease;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ManifestInfo(Manifest manifest) {
        this(manifest, null);
    }

    private ManifestInfo(Manifest manifest, Boolean multiRelease) {
        this.manifest = manifest;
        this.multiRelease = multiRelease;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Manifest getManifest() {
        return this.manifest;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isMultiRelease() {
        if (this.manifest == null) {
            return false;
        }
        Boolean multiRelease = this.multiRelease;
        if (multiRelease != null) {
            return multiRelease.booleanValue();
        }
        Attributes attributes = this.manifest.getMainAttributes();
        Boolean multiRelease2 = Boolean.valueOf(attributes.containsKey(MULTI_RELEASE));
        this.multiRelease = multiRelease2;
        return multiRelease2.booleanValue();
    }
}
