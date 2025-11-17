package org.springframework.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/CharacterEncodingFilter.class */
public class CharacterEncodingFilter extends OncePerRequestFilter {

    @Nullable
    private String encoding;
    private boolean forceRequestEncoding;
    private boolean forceResponseEncoding;

    public CharacterEncodingFilter() {
        this.forceRequestEncoding = false;
        this.forceResponseEncoding = false;
    }

    public CharacterEncodingFilter(String encoding) {
        this(encoding, false);
    }

    public CharacterEncodingFilter(String encoding, boolean forceEncoding) {
        this(encoding, forceEncoding, forceEncoding);
    }

    public CharacterEncodingFilter(String encoding, boolean forceRequestEncoding, boolean forceResponseEncoding) {
        this.forceRequestEncoding = false;
        this.forceResponseEncoding = false;
        Assert.hasLength(encoding, "Encoding must not be empty");
        this.encoding = encoding;
        this.forceRequestEncoding = forceRequestEncoding;
        this.forceResponseEncoding = forceResponseEncoding;
    }

    public void setEncoding(@Nullable String encoding) {
        this.encoding = encoding;
    }

    @Nullable
    public String getEncoding() {
        return this.encoding;
    }

    public void setForceEncoding(boolean forceEncoding) {
        this.forceRequestEncoding = forceEncoding;
        this.forceResponseEncoding = forceEncoding;
    }

    public void setForceRequestEncoding(boolean forceRequestEncoding) {
        this.forceRequestEncoding = forceRequestEncoding;
    }

    public boolean isForceRequestEncoding() {
        return this.forceRequestEncoding;
    }

    public void setForceResponseEncoding(boolean forceResponseEncoding) {
        this.forceResponseEncoding = forceResponseEncoding;
    }

    public boolean isForceResponseEncoding() {
        return this.forceResponseEncoding;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String encoding = getEncoding();
        if (encoding != null) {
            if (isForceRequestEncoding() || request.getCharacterEncoding() == null) {
                request.setCharacterEncoding(encoding);
            }
            if (isForceResponseEncoding()) {
                response.setCharacterEncoding(encoding);
            }
        }
        filterChain.doFilter(request, response);
    }
}
