package org.springframework.http;

import java.io.Serializable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/DefaultHttpStatusCode.class */
public final class DefaultHttpStatusCode implements HttpStatusCode, Comparable<HttpStatusCode>, Serializable {
    private static final long serialVersionUID = 7017664779360718111L;
    private final int value;

    public DefaultHttpStatusCode(int value) {
        this.value = value;
    }

    @Override // org.springframework.http.HttpStatusCode
    public int value() {
        return this.value;
    }

    @Override // org.springframework.http.HttpStatusCode
    public boolean is1xxInformational() {
        return hundreds() == 1;
    }

    @Override // org.springframework.http.HttpStatusCode
    public boolean is2xxSuccessful() {
        return hundreds() == 2;
    }

    @Override // org.springframework.http.HttpStatusCode
    public boolean is3xxRedirection() {
        return hundreds() == 3;
    }

    @Override // org.springframework.http.HttpStatusCode
    public boolean is4xxClientError() {
        return hundreds() == 4;
    }

    @Override // org.springframework.http.HttpStatusCode
    public boolean is5xxServerError() {
        return hundreds() == 5;
    }

    @Override // org.springframework.http.HttpStatusCode
    public boolean isError() {
        int hundreds = hundreds();
        return hundreds == 4 || hundreds == 5;
    }

    private int hundreds() {
        return this.value / 100;
    }

    @Override // java.lang.Comparable
    public int compareTo(@NonNull HttpStatusCode o) {
        return Integer.compare(this.value, o.value());
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof HttpStatusCode) {
                HttpStatusCode that = (HttpStatusCode) other;
                if (this.value == that.value()) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.value;
    }

    public String toString() {
        return Integer.toString(this.value);
    }
}
