package jakarta.websocket;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/CloseReason.class */
public class CloseReason {
    private final CloseCode closeCode;
    private final String reasonPhrase;

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/CloseReason$CloseCode.class */
    public interface CloseCode {
        int getCode();
    }

    public CloseReason(CloseCode closeCode, String reasonPhrase) {
        this.closeCode = closeCode;
        this.reasonPhrase = reasonPhrase;
    }

    public CloseCode getCloseCode() {
        return this.closeCode;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public String toString() {
        return "CloseReason: code [" + this.closeCode.getCode() + "], reason [" + this.reasonPhrase + "]";
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/CloseReason$CloseCodes.class */
    public enum CloseCodes implements CloseCode {
        NORMAL_CLOSURE(1000),
        GOING_AWAY(1001),
        PROTOCOL_ERROR(1002),
        CANNOT_ACCEPT(1003),
        RESERVED(1004),
        NO_STATUS_CODE(1005),
        CLOSED_ABNORMALLY(1006),
        NOT_CONSISTENT(1007),
        VIOLATED_POLICY(1008),
        TOO_BIG(1009),
        NO_EXTENSION(1010),
        UNEXPECTED_CONDITION(1011),
        SERVICE_RESTART(1012),
        TRY_AGAIN_LATER(1013),
        TLS_HANDSHAKE_FAILURE(1015);

        private int code;

        CloseCodes(int code) {
            this.code = code;
        }

        public static CloseCode getCloseCode(final int code) {
            if (code > 2999 && code < 5000) {
                return new CloseCode() { // from class: jakarta.websocket.CloseReason.CloseCodes.1
                    @Override // jakarta.websocket.CloseReason.CloseCode
                    public int getCode() {
                        return code;
                    }
                };
            }
            switch (code) {
                case 1000:
                    return NORMAL_CLOSURE;
                case 1001:
                    return GOING_AWAY;
                case 1002:
                    return PROTOCOL_ERROR;
                case 1003:
                    return CANNOT_ACCEPT;
                case 1004:
                    return RESERVED;
                case 1005:
                    return NO_STATUS_CODE;
                case 1006:
                    return CLOSED_ABNORMALLY;
                case 1007:
                    return NOT_CONSISTENT;
                case 1008:
                    return VIOLATED_POLICY;
                case 1009:
                    return TOO_BIG;
                case 1010:
                    return NO_EXTENSION;
                case 1011:
                    return UNEXPECTED_CONDITION;
                case 1012:
                    return SERVICE_RESTART;
                case 1013:
                    return TRY_AGAIN_LATER;
                case 1014:
                default:
                    throw new IllegalArgumentException("Invalid close code: [" + code + "]");
                case 1015:
                    return TLS_HANDSHAKE_FAILURE;
            }
        }

        @Override // jakarta.websocket.CloseReason.CloseCode
        public int getCode() {
            return this.code;
        }
    }
}
