package ch.qos.logback.core.sift;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/sift/DefaultDiscriminator.class */
public class DefaultDiscriminator<E> extends AbstractDiscriminator<E> {
    public static final String DEFAULT = "default";

    @Override // ch.qos.logback.core.sift.Discriminator
    public String getDiscriminatingValue(E e) {
        return "default";
    }

    @Override // ch.qos.logback.core.sift.Discriminator
    public String getKey() {
        return "default";
    }
}
