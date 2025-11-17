package org.springframework.boot.autoconfigure.data.redis;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/RedisUrlSyntaxException.class */
class RedisUrlSyntaxException extends RuntimeException {
    private final String url;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RedisUrlSyntaxException(String url, Exception cause) {
        super(buildMessage(url), cause);
        this.url = url;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RedisUrlSyntaxException(String url) {
        super(buildMessage(url));
        this.url = url;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getUrl() {
        return this.url;
    }

    private static String buildMessage(String url) {
        return "Invalid Redis URL '" + url + "'";
    }
}
