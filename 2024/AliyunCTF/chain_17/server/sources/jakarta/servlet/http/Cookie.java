package jakarta.servlet.http;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/Cookie.class */
public class Cookie implements Cloneable, Serializable {
    private static final String LSTRING_FILE = "jakarta.servlet.http.LocalStrings";
    private static final ResourceBundle LSTRINGS = ResourceBundle.getBundle(LSTRING_FILE);
    private static final CookieNameValidator validation = new RFC6265Validator();
    private static final long serialVersionUID = 2;
    private final String name;
    private String value;
    private volatile Map<String, String> attributes;
    private static final String DOMAIN = "Domain";
    private static final String MAX_AGE = "Max-Age";
    private static final String PATH = "Path";
    private static final String SECURE = "Secure";
    private static final String HTTP_ONLY = "HttpOnly";

    public Cookie(String name, String value) {
        validation.validate(name);
        this.name = name;
        this.value = value;
    }

    @Deprecated(since = "Servlet 6.0", forRemoval = true)
    public void setComment(String purpose) {
    }

    @Deprecated(since = "Servlet 6.0", forRemoval = true)
    public String getComment() {
        return null;
    }

    public void setDomain(String pattern) {
        if (pattern == null) {
            setAttributeInternal("Domain", null);
        } else {
            setAttributeInternal("Domain", pattern.toLowerCase(Locale.ENGLISH));
        }
    }

    public String getDomain() {
        return getAttribute("Domain");
    }

    public void setMaxAge(int expiry) {
        setAttributeInternal("Max-Age", Integer.toString(expiry));
    }

    public int getMaxAge() {
        String maxAge = getAttribute("Max-Age");
        if (maxAge == null) {
            return -1;
        }
        return Integer.parseInt(maxAge);
    }

    public void setPath(String uri) {
        setAttributeInternal("Path", uri);
    }

    public String getPath() {
        return getAttribute("Path");
    }

    public void setSecure(boolean flag) {
        setAttributeInternal("Secure", Boolean.toString(flag));
    }

    public boolean getSecure() {
        return Boolean.parseBoolean(getAttribute("Secure"));
    }

    public String getName() {
        return this.name;
    }

    public void setValue(String newValue) {
        this.value = newValue;
    }

    public String getValue() {
        return this.value;
    }

    @Deprecated(since = "Servlet 6.0", forRemoval = true)
    public int getVersion() {
        return 0;
    }

    @Deprecated(since = "Servlet 6.0", forRemoval = true)
    public void setVersion(int v) {
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setHttpOnly(boolean httpOnly) {
        setAttributeInternal("HttpOnly", Boolean.toString(httpOnly));
    }

    public boolean isHttpOnly() {
        return Boolean.parseBoolean(getAttribute("HttpOnly"));
    }

    public void setAttribute(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException(LSTRINGS.getString("cookie.attribute.invalidName.null"));
        }
        if (!validation.isToken(name)) {
            String msg = LSTRINGS.getString("cookie.attribute.invalidName.notToken");
            throw new IllegalArgumentException(MessageFormat.format(msg, name));
        }
        if (name.equalsIgnoreCase("Max-Age")) {
            if (value == null) {
                setAttributeInternal("Max-Age", null);
                return;
            } else {
                setMaxAge(Integer.parseInt(value));
                return;
            }
        }
        setAttributeInternal(name, value);
    }

    private void setAttributeInternal(String name, String value) {
        if (this.attributes == null) {
            if (value == null) {
                return;
            } else {
                this.attributes = new TreeMap(String.CASE_INSENSITIVE_ORDER);
            }
        }
        this.attributes.put(name, value);
    }

    public String getAttribute(String name) {
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.get(name);
    }

    public Map<String, String> getAttributes() {
        if (this.attributes == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.attributes);
    }

    public int hashCode() {
        int result = (31 * 1) + (this.attributes == null ? 0 : this.attributes.hashCode());
        return (31 * ((31 * result) + (this.name == null ? 0 : this.name.hashCode()))) + (this.value == null ? 0 : this.value.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Cookie other = (Cookie) obj;
        if (this.attributes == null) {
            if (other.attributes != null) {
                return false;
            }
        } else if (!this.attributes.equals(other.attributes)) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
            return true;
        }
        if (!this.value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
