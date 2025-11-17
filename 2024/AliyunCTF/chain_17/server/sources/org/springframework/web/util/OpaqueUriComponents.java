package org.springframework.web.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.UriComponents;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/OpaqueUriComponents.class */
final class OpaqueUriComponents extends UriComponents {
    private static final MultiValueMap<String, String> QUERY_PARAMS_NONE = new LinkedMultiValueMap();

    @Nullable
    private final String ssp;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OpaqueUriComponents(@Nullable String scheme, @Nullable String schemeSpecificPart, @Nullable String fragment) {
        super(scheme, fragment);
        this.ssp = schemeSpecificPart;
    }

    @Override // org.springframework.web.util.UriComponents
    @Nullable
    public String getSchemeSpecificPart() {
        return this.ssp;
    }

    @Override // org.springframework.web.util.UriComponents
    @Nullable
    public String getUserInfo() {
        return null;
    }

    @Override // org.springframework.web.util.UriComponents
    @Nullable
    public String getHost() {
        return null;
    }

    @Override // org.springframework.web.util.UriComponents
    public int getPort() {
        return -1;
    }

    @Override // org.springframework.web.util.UriComponents
    @Nullable
    public String getPath() {
        return null;
    }

    @Override // org.springframework.web.util.UriComponents
    public List<String> getPathSegments() {
        return Collections.emptyList();
    }

    @Override // org.springframework.web.util.UriComponents
    @Nullable
    public String getQuery() {
        return null;
    }

    @Override // org.springframework.web.util.UriComponents
    public MultiValueMap<String, String> getQueryParams() {
        return QUERY_PARAMS_NONE;
    }

    @Override // org.springframework.web.util.UriComponents
    public UriComponents encode(Charset charset) {
        return this;
    }

    @Override // org.springframework.web.util.UriComponents
    protected UriComponents expandInternal(UriComponents.UriTemplateVariables uriVariables) {
        String expandedScheme = expandUriComponent(getScheme(), uriVariables);
        String expandedSsp = expandUriComponent(getSchemeSpecificPart(), uriVariables);
        String expandedFragment = expandUriComponent(getFragment(), uriVariables);
        return new OpaqueUriComponents(expandedScheme, expandedSsp, expandedFragment);
    }

    @Override // org.springframework.web.util.UriComponents
    public UriComponents normalize() {
        return this;
    }

    @Override // org.springframework.web.util.UriComponents
    public String toUriString() {
        StringBuilder uriBuilder = new StringBuilder();
        if (getScheme() != null) {
            uriBuilder.append(getScheme());
            uriBuilder.append(':');
        }
        if (this.ssp != null) {
            uriBuilder.append(this.ssp);
        }
        if (getFragment() != null) {
            uriBuilder.append('#');
            uriBuilder.append(getFragment());
        }
        return uriBuilder.toString();
    }

    @Override // org.springframework.web.util.UriComponents
    public URI toUri() {
        try {
            return new URI(getScheme(), this.ssp, getFragment());
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not create URI object: " + ex.getMessage(), ex);
        }
    }

    @Override // org.springframework.web.util.UriComponents
    protected void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
        if (getScheme() != null) {
            builder.scheme(getScheme());
        }
        if (getSchemeSpecificPart() != null) {
            builder.schemeSpecificPart(getSchemeSpecificPart());
        }
        if (getFragment() != null) {
            builder.fragment(getFragment());
        }
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof OpaqueUriComponents) {
                OpaqueUriComponents that = (OpaqueUriComponents) other;
                if (!ObjectUtils.nullSafeEquals(getScheme(), that.getScheme()) || !ObjectUtils.nullSafeEquals(this.ssp, that.ssp) || !ObjectUtils.nullSafeEquals(getFragment(), that.getFragment())) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(getScheme(), this.ssp, getFragment());
    }
}
