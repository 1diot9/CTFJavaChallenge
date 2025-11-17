package jakarta.servlet.http;

import java.text.MessageFormat;
import java.util.BitSet;
import java.util.ResourceBundle;
import org.springframework.asm.Opcodes;

/* compiled from: Cookie.java */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/CookieNameValidator.class */
class CookieNameValidator {
    private static final String LSTRING_FILE = "jakarta.servlet.http.LocalStrings";
    protected static final ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);
    protected final BitSet allowed = new BitSet(128);

    /* JADX INFO: Access modifiers changed from: protected */
    public CookieNameValidator(String separators) {
        this.allowed.set(32, Opcodes.LAND);
        for (int i = 0; i < separators.length(); i++) {
            char ch2 = separators.charAt(i);
            this.allowed.clear(ch2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void validate(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException(lStrings.getString("err.cookie_name_blank"));
        }
        if (!isToken(name)) {
            String errMsg = lStrings.getString("err.cookie_name_is_token");
            throw new IllegalArgumentException(MessageFormat.format(errMsg, name));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isToken(String possibleToken) {
        int len = possibleToken.length();
        for (int i = 0; i < len; i++) {
            char c = possibleToken.charAt(i);
            if (!this.allowed.get(c)) {
                return false;
            }
        }
        return true;
    }
}
