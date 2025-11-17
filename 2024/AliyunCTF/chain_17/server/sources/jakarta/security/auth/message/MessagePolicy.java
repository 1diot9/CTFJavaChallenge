package jakarta.security.auth.message;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/MessagePolicy.class */
public class MessagePolicy {
    private final TargetPolicy[] targetPolicies;
    private final boolean mandatory;

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/MessagePolicy$ProtectionPolicy.class */
    public interface ProtectionPolicy {
        public static final String AUTHENTICATE_SENDER = "#authenticateSender";
        public static final String AUTHENTICATE_CONTENT = "#authenticateContent";
        public static final String AUTHENTICATE_RECIPIENT = "#authenticateRecipient";

        String getID();
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/MessagePolicy$Target.class */
    public interface Target {
        Object get(MessageInfo messageInfo);

        void remove(MessageInfo messageInfo);

        void put(MessageInfo messageInfo, Object obj);
    }

    public MessagePolicy(TargetPolicy[] targetPolicies, boolean mandatory) {
        if (targetPolicies == null) {
            throw new IllegalArgumentException("targetPolicies is null");
        }
        this.targetPolicies = targetPolicies;
        this.mandatory = mandatory;
    }

    public boolean isMandatory() {
        return this.mandatory;
    }

    public TargetPolicy[] getTargetPolicies() {
        if (this.targetPolicies.length == 0) {
            return null;
        }
        return this.targetPolicies;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/security/auth/message/MessagePolicy$TargetPolicy.class */
    public static class TargetPolicy {
        private final Target[] targets;
        private final ProtectionPolicy protectionPolicy;

        public TargetPolicy(Target[] targets, ProtectionPolicy protectionPolicy) {
            if (protectionPolicy == null) {
                throw new IllegalArgumentException("protectionPolicy is null");
            }
            this.targets = targets;
            this.protectionPolicy = protectionPolicy;
        }

        public Target[] getTargets() {
            if (this.targets == null || this.targets.length == 0) {
                return null;
            }
            return this.targets;
        }

        public ProtectionPolicy getProtectionPolicy() {
            return this.protectionPolicy;
        }
    }
}
