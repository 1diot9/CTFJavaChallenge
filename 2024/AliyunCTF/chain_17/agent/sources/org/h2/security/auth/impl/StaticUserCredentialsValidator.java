package org.h2.security.auth.impl;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import org.h2.api.CredentialsValidator;
import org.h2.security.SHA256;
import org.h2.security.auth.AuthenticationException;
import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.ConfigProperties;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/impl/StaticUserCredentialsValidator.class */
public class StaticUserCredentialsValidator implements CredentialsValidator {
    private Pattern userNamePattern;
    private String password;
    private byte[] salt;
    private byte[] hashWithSalt;

    public StaticUserCredentialsValidator() {
    }

    public StaticUserCredentialsValidator(String str, String str2) {
        if (str != null) {
            this.userNamePattern = Pattern.compile(str.toUpperCase());
        }
        this.salt = MathUtils.secureRandomBytes(256);
        this.hashWithSalt = SHA256.getHashWithSalt(str2.getBytes(StandardCharsets.UTF_8), this.salt);
    }

    @Override // org.h2.api.CredentialsValidator
    public boolean validateCredentials(AuthenticationInfo authenticationInfo) throws AuthenticationException {
        if (this.userNamePattern != null && !this.userNamePattern.matcher(authenticationInfo.getUserName()).matches()) {
            return false;
        }
        if (this.password != null) {
            return this.password.equals(authenticationInfo.getPassword());
        }
        return Utils.compareSecure(this.hashWithSalt, SHA256.getHashWithSalt(authenticationInfo.getPassword().getBytes(StandardCharsets.UTF_8), this.salt));
    }

    @Override // org.h2.security.auth.Configurable
    public void configure(ConfigProperties configProperties) {
        String stringValue = configProperties.getStringValue("userNamePattern", null);
        if (stringValue != null) {
            this.userNamePattern = Pattern.compile(stringValue);
        }
        this.password = configProperties.getStringValue("password", this.password);
        String stringValue2 = configProperties.getStringValue("salt", null);
        if (stringValue2 != null) {
            this.salt = StringUtils.convertHexToBytes(stringValue2);
        }
        String stringValue3 = configProperties.getStringValue("hash", null);
        if (stringValue3 != null) {
            this.hashWithSalt = SHA256.getHashWithSalt(StringUtils.convertHexToBytes(stringValue3), this.salt);
        }
    }
}
