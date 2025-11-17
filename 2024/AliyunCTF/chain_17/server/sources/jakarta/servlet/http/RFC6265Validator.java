package jakarta.servlet.http;

/* compiled from: Cookie.java */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/RFC6265Validator.class */
class RFC6265Validator extends CookieNameValidator {
    private static final String RFC2616_SEPARATORS = "()<>@,;:\\\"/[]?={} \t";

    /* JADX INFO: Access modifiers changed from: package-private */
    public RFC6265Validator() {
        super(RFC2616_SEPARATORS);
    }
}
