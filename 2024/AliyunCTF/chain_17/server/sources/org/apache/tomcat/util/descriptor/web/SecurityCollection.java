package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.tomcat.util.buf.UDecoder;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/SecurityCollection.class */
public class SecurityCollection extends XmlEncodingBase implements Serializable {
    private static final long serialVersionUID = 1;
    private String description;
    private String[] methods;
    private String[] omittedMethods;
    private String name;
    private String[] patterns;
    private boolean isFromDescriptor;

    public SecurityCollection() {
        this(null, null);
    }

    public SecurityCollection(String name, String description) {
        this.description = null;
        this.methods = new String[0];
        this.omittedMethods = new String[0];
        this.name = null;
        this.patterns = new String[0];
        this.isFromDescriptor = true;
        setName(name);
        setDescription(description);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFromDescriptor() {
        return this.isFromDescriptor;
    }

    public void setFromDescriptor(boolean isFromDescriptor) {
        this.isFromDescriptor = isFromDescriptor;
    }

    public void addMethod(String method) {
        if (method == null) {
            return;
        }
        String[] results = (String[]) Arrays.copyOf(this.methods, this.methods.length + 1);
        results[this.methods.length] = method;
        this.methods = results;
    }

    public void addOmittedMethod(String method) {
        if (method == null) {
            return;
        }
        String[] results = (String[]) Arrays.copyOf(this.omittedMethods, this.omittedMethods.length + 1);
        results[this.omittedMethods.length] = method;
        this.omittedMethods = results;
    }

    public void addPattern(String pattern) {
        addPatternDecoded(UDecoder.URLDecode(pattern, StandardCharsets.UTF_8));
    }

    public void addPatternDecoded(String pattern) {
        if (pattern == null) {
            return;
        }
        String decodedPattern = UDecoder.URLDecode(pattern, getCharset());
        String[] results = (String[]) Arrays.copyOf(this.patterns, this.patterns.length + 1);
        results[this.patterns.length] = decodedPattern;
        this.patterns = results;
    }

    public boolean findMethod(String method) {
        if (this.methods.length == 0 && this.omittedMethods.length == 0) {
            return true;
        }
        if (this.methods.length > 0) {
            for (String s : this.methods) {
                if (s.equals(method)) {
                    return true;
                }
            }
            return false;
        }
        if (this.omittedMethods.length > 0) {
            for (String omittedMethod : this.omittedMethods) {
                if (omittedMethod.equals(method)) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public String[] findMethods() {
        return this.methods;
    }

    public String[] findOmittedMethods() {
        return this.omittedMethods;
    }

    public boolean findPattern(String pattern) {
        for (String s : this.patterns) {
            if (s.equals(pattern)) {
                return true;
            }
        }
        return false;
    }

    public String[] findPatterns() {
        return this.patterns;
    }

    public void removeMethod(String method) {
        if (method == null) {
            return;
        }
        int n = -1;
        int i = 0;
        while (true) {
            if (i >= this.methods.length) {
                break;
            }
            if (!this.methods[i].equals(method)) {
                i++;
            } else {
                n = i;
                break;
            }
        }
        if (n >= 0) {
            int j = 0;
            String[] results = new String[this.methods.length - 1];
            for (int i2 = 0; i2 < this.methods.length; i2++) {
                if (i2 != n) {
                    int i3 = j;
                    j++;
                    results[i3] = this.methods[i2];
                }
            }
            this.methods = results;
        }
    }

    public void removeOmittedMethod(String method) {
        if (method == null) {
            return;
        }
        int n = -1;
        int i = 0;
        while (true) {
            if (i >= this.omittedMethods.length) {
                break;
            }
            if (!this.omittedMethods[i].equals(method)) {
                i++;
            } else {
                n = i;
                break;
            }
        }
        if (n >= 0) {
            int j = 0;
            String[] results = new String[this.omittedMethods.length - 1];
            for (int i2 = 0; i2 < this.omittedMethods.length; i2++) {
                if (i2 != n) {
                    int i3 = j;
                    j++;
                    results[i3] = this.omittedMethods[i2];
                }
            }
            this.omittedMethods = results;
        }
    }

    public void removePattern(String pattern) {
        if (pattern == null) {
            return;
        }
        int n = -1;
        int i = 0;
        while (true) {
            if (i >= this.patterns.length) {
                break;
            }
            if (!this.patterns[i].equals(pattern)) {
                i++;
            } else {
                n = i;
                break;
            }
        }
        if (n >= 0) {
            int j = 0;
            String[] results = new String[this.patterns.length - 1];
            for (int i2 = 0; i2 < this.patterns.length; i2++) {
                if (i2 != n) {
                    int i3 = j;
                    j++;
                    results[i3] = this.patterns[i2];
                }
            }
            this.patterns = results;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SecurityCollection[");
        sb.append(this.name);
        if (this.description != null) {
            sb.append(", ");
            sb.append(this.description);
        }
        sb.append(']');
        return sb.toString();
    }
}
