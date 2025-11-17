package org.springframework.web.client;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

/* compiled from: RestOperationsExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��:\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n��\n\u0002\u0010\u0011\n\u0002\u0010��\n��\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\u000f\u001a;\u0010��\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0002\b\u0003\u0018\u00010\tH\u0086\b\u001aT\u0010��\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0002\b\u0003\u0018\u00010\t2\u0012\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\f\"\u00020\rH\u0086\b¢\u0006\u0002\u0010\u000e\u001aM\u0010��\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0002\b\u0003\u0018\u00010\t2\u0010\u0010\u000b\u001a\f\u0012\u0004\u0012\u00020\n\u0012\u0002\b\u00030\u000fH\u0086\b\u001a'\u0010��\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\n\u0010\b\u001a\u0006\u0012\u0002\b\u00030\u0010H\u0086\b\u001a#\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0086\b\u001a<\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\u0012\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\f\"\u00020\rH\u0086\b¢\u0006\u0002\u0010\u0012\u001a5\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\u0010\u0010\u000b\u001a\f\u0012\u0004\u0012\u00020\n\u0012\u0002\b\u00030\u000fH\u0086\b\u001a\"\u0010\u0013\u001a\u0002H\u0002\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0086\b¢\u0006\u0002\u0010\u0014\u001a6\u0010\u0013\u001a\u0002H\u0002\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\u0012\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\f\"\u00020\rH\u0086\b¢\u0006\u0002\u0010\u0015\u001a8\u0010\u0013\u001a\u0002H\u0002\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\u0014\u0010\u000b\u001a\u0010\u0012\u0004\u0012\u00020\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\u000fH\u0086\b¢\u0006\u0002\u0010\u0016\u001a.\u0010\u0017\u001a\u0002H\u0002\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\rH\u0086\b¢\u0006\u0002\u0010\u0019\u001aB\u0010\u0017\u001a\u0002H\u0002\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\r2\u0012\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\f\"\u00020\rH\u0086\b¢\u0006\u0002\u0010\u001a\u001a@\u0010\u0017\u001a\u0002H\u0002\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\r2\u0010\u0010\u000b\u001a\f\u0012\u0004\u0012\u00020\n\u0012\u0002\b\u00030\u000fH\u0086\b¢\u0006\u0002\u0010\u001b\u001a/\u0010\u001c\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\rH\u0086\b\u001aH\u0010\u001c\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\r2\u0012\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\f\"\u00020\rH\u0086\b¢\u0006\u0002\u0010\u001d\u001aA\u0010\u001c\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\r2\u0010\u0010\u000b\u001a\f\u0012\u0004\u0012\u00020\n\u0012\u0002\b\u00030\u000fH\u0086\b\u001a.\u0010\u001e\u001a\u0002H\u0002\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\rH\u0086\b¢\u0006\u0002\u0010\u0019\u001aB\u0010\u001e\u001a\u0002H\u0002\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\r2\u0012\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\f\"\u00020\rH\u0086\b¢\u0006\u0002\u0010\u001a\u001a@\u0010\u001e\u001a\u0002H\u0002\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\n2\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\r2\u0010\u0010\u000b\u001a\f\u0012\u0004\u0012\u00020\n\u0012\u0002\b\u00030\u000fH\u0086\b¢\u0006\u0002\u0010\u001b¨\u0006\u001f"}, d2 = {"exchange", "Lorg/springframework/http/ResponseEntity;", "T", "Lorg/springframework/web/client/RestOperations;", "url", "Ljava/net/URI;", "method", "Lorg/springframework/http/HttpMethod;", "requestEntity", "Lorg/springframework/http/HttpEntity;", "", "uriVariables", "", "", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Lorg/springframework/http/HttpMethod;Lorg/springframework/http/HttpEntity;[Ljava/lang/Object;)Lorg/springframework/http/ResponseEntity;", "", "Lorg/springframework/http/RequestEntity;", "getForEntity", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;[Ljava/lang/Object;)Lorg/springframework/http/ResponseEntity;", "getForObject", "(Lorg/springframework/web/client/RestOperations;Ljava/net/URI;)Ljava/lang/Object;", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Ljava/util/Map;)Ljava/lang/Object;", "patchForObject", "request", "(Lorg/springframework/web/client/RestOperations;Ljava/net/URI;Ljava/lang/Object;)Ljava/lang/Object;", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Ljava/lang/Object;Ljava/util/Map;)Ljava/lang/Object;", "postForEntity", "(Lorg/springframework/web/client/RestOperations;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)Lorg/springframework/http/ResponseEntity;", "postForObject", "spring-web"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestOperationsExtensionsKt.class */
public final class RestOperationsExtensionsKt {
    public static final /* synthetic */ <T> T getForObject(RestOperations restOperations, String str, Object... objArr) throws RestClientException {
        Intrinsics.checkNotNullParameter(restOperations, "<this>");
        Intrinsics.checkNotNullParameter(str, "url");
        Intrinsics.checkNotNullParameter(objArr, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object forObject = restOperations.getForObject(str, Object.class, Arrays.copyOf(objArr, objArr.length));
        Intrinsics.reifiedOperationMarker(1, "T");
        return (T) forObject;
    }

    public static final /* synthetic */ <T> T getForObject(RestOperations restOperations, String str, Map<String, ? extends Object> map) throws RestClientException {
        Intrinsics.checkNotNullParameter(restOperations, "<this>");
        Intrinsics.checkNotNullParameter(str, "url");
        Intrinsics.checkNotNullParameter(map, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object forObject = restOperations.getForObject(str, Object.class, map);
        Intrinsics.reifiedOperationMarker(1, "T");
        return (T) forObject;
    }

    public static final /* synthetic */ <T> T getForObject(RestOperations restOperations, URI uri) throws RestClientException {
        Intrinsics.checkNotNullParameter(restOperations, "<this>");
        Intrinsics.checkNotNullParameter(uri, "url");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object forObject = restOperations.getForObject(uri, Object.class);
        Intrinsics.reifiedOperationMarker(1, "T");
        return (T) forObject;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> getForEntity(RestOperations $this$getForEntity, URI url) throws RestClientException {
        Intrinsics.checkNotNullParameter($this$getForEntity, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity<T> forEntity = $this$getForEntity.getForEntity(url, Object.class);
        Intrinsics.checkNotNullExpressionValue(forEntity, "getForEntity(url, T::class.java)");
        return forEntity;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> getForEntity(RestOperations $this$getForEntity, String url, Object... uriVariables) throws RestClientException {
        Intrinsics.checkNotNullParameter($this$getForEntity, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(uriVariables, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity<T> forEntity = $this$getForEntity.getForEntity(url, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkNotNullExpressionValue(forEntity, "getForEntity(url, T::class.java, *uriVariables)");
        return forEntity;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> getForEntity(RestOperations $this$getForEntity, String url, Map<String, ?> map) throws RestClientException {
        Intrinsics.checkNotNullParameter($this$getForEntity, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(map, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity<T> forEntity = $this$getForEntity.getForEntity(url, Object.class, map);
        Intrinsics.checkNotNullExpressionValue(forEntity, "getForEntity(url, T::class.java, uriVariables)");
        return forEntity;
    }

    public static /* synthetic */ Object patchForObject$default(RestOperations $this$patchForObject_u24default, String url, Object request, Object[] uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.checkNotNullParameter($this$patchForObject_u24default, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(uriVariables, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object patchForObject = $this$patchForObject_u24default.patchForObject(url, request, (Class<Object>) Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.reifiedOperationMarker(1, "T");
        return patchForObject;
    }

    public static final /* synthetic */ <T> T patchForObject(RestOperations restOperations, String str, Object obj, Object... objArr) throws RestClientException {
        Intrinsics.checkNotNullParameter(restOperations, "<this>");
        Intrinsics.checkNotNullParameter(str, "url");
        Intrinsics.checkNotNullParameter(objArr, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object patchForObject = restOperations.patchForObject(str, obj, Object.class, Arrays.copyOf(objArr, objArr.length));
        Intrinsics.reifiedOperationMarker(1, "T");
        return (T) patchForObject;
    }

    public static /* synthetic */ Object patchForObject$default(RestOperations $this$patchForObject_u24default, String url, Object request, Map uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.checkNotNullParameter($this$patchForObject_u24default, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(uriVariables, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object patchForObject = $this$patchForObject_u24default.patchForObject(url, request, (Class<Object>) Object.class, (Map<String, ?>) uriVariables);
        Intrinsics.reifiedOperationMarker(1, "T");
        return patchForObject;
    }

    public static final /* synthetic */ <T> T patchForObject(RestOperations restOperations, String str, Object obj, Map<String, ?> map) throws RestClientException {
        Intrinsics.checkNotNullParameter(restOperations, "<this>");
        Intrinsics.checkNotNullParameter(str, "url");
        Intrinsics.checkNotNullParameter(map, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object patchForObject = restOperations.patchForObject(str, obj, Object.class, map);
        Intrinsics.reifiedOperationMarker(1, "T");
        return (T) patchForObject;
    }

    public static /* synthetic */ Object patchForObject$default(RestOperations $this$patchForObject_u24default, URI url, Object request, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.checkNotNullParameter($this$patchForObject_u24default, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object patchForObject = $this$patchForObject_u24default.patchForObject(url, request, Object.class);
        Intrinsics.reifiedOperationMarker(1, "T");
        return patchForObject;
    }

    public static final /* synthetic */ <T> T patchForObject(RestOperations restOperations, URI uri, Object obj) throws RestClientException {
        Intrinsics.checkNotNullParameter(restOperations, "<this>");
        Intrinsics.checkNotNullParameter(uri, "url");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object patchForObject = restOperations.patchForObject(uri, obj, Object.class);
        Intrinsics.reifiedOperationMarker(1, "T");
        return (T) patchForObject;
    }

    public static /* synthetic */ Object postForObject$default(RestOperations $this$postForObject_u24default, String url, Object request, Object[] uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.checkNotNullParameter($this$postForObject_u24default, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(uriVariables, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object postForObject = $this$postForObject_u24default.postForObject(url, request, (Class<Object>) Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.reifiedOperationMarker(1, "T");
        return postForObject;
    }

    public static final /* synthetic */ <T> T postForObject(RestOperations restOperations, String str, Object obj, Object... objArr) throws RestClientException {
        Intrinsics.checkNotNullParameter(restOperations, "<this>");
        Intrinsics.checkNotNullParameter(str, "url");
        Intrinsics.checkNotNullParameter(objArr, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object postForObject = restOperations.postForObject(str, obj, Object.class, Arrays.copyOf(objArr, objArr.length));
        Intrinsics.reifiedOperationMarker(1, "T");
        return (T) postForObject;
    }

    public static /* synthetic */ Object postForObject$default(RestOperations $this$postForObject_u24default, String url, Object request, Map uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.checkNotNullParameter($this$postForObject_u24default, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(uriVariables, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object postForObject = $this$postForObject_u24default.postForObject(url, request, (Class<Object>) Object.class, (Map<String, ?>) uriVariables);
        Intrinsics.reifiedOperationMarker(1, "T");
        return postForObject;
    }

    public static final /* synthetic */ <T> T postForObject(RestOperations restOperations, String str, Object obj, Map<String, ?> map) throws RestClientException {
        Intrinsics.checkNotNullParameter(restOperations, "<this>");
        Intrinsics.checkNotNullParameter(str, "url");
        Intrinsics.checkNotNullParameter(map, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object postForObject = restOperations.postForObject(str, obj, Object.class, map);
        Intrinsics.reifiedOperationMarker(1, "T");
        return (T) postForObject;
    }

    public static /* synthetic */ Object postForObject$default(RestOperations $this$postForObject_u24default, URI url, Object request, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.checkNotNullParameter($this$postForObject_u24default, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object postForObject = $this$postForObject_u24default.postForObject(url, request, Object.class);
        Intrinsics.reifiedOperationMarker(1, "T");
        return postForObject;
    }

    public static final /* synthetic */ <T> T postForObject(RestOperations restOperations, URI uri, Object obj) throws RestClientException {
        Intrinsics.checkNotNullParameter(restOperations, "<this>");
        Intrinsics.checkNotNullParameter(uri, "url");
        Intrinsics.reifiedOperationMarker(4, "T");
        Object postForObject = restOperations.postForObject(uri, obj, Object.class);
        Intrinsics.reifiedOperationMarker(1, "T");
        return (T) postForObject;
    }

    public static /* synthetic */ ResponseEntity postForEntity$default(RestOperations $this$postForEntity_u24default, String url, Object request, Object[] uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.checkNotNullParameter($this$postForEntity_u24default, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(uriVariables, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity postForEntity = $this$postForEntity_u24default.postForEntity(url, request, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkNotNullExpressionValue(postForEntity, "postForEntity(url, reque…lass.java, *uriVariables)");
        return postForEntity;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> postForEntity(RestOperations $this$postForEntity, String url, Object request, Object... uriVariables) throws RestClientException {
        Intrinsics.checkNotNullParameter($this$postForEntity, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(uriVariables, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity<T> postForEntity = $this$postForEntity.postForEntity(url, request, Object.class, Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkNotNullExpressionValue(postForEntity, "postForEntity(url, reque…lass.java, *uriVariables)");
        return postForEntity;
    }

    public static /* synthetic */ ResponseEntity postForEntity$default(RestOperations $this$postForEntity_u24default, String url, Object request, Map uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.checkNotNullParameter($this$postForEntity_u24default, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(uriVariables, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity postForEntity = $this$postForEntity_u24default.postForEntity(url, request, Object.class, (Map<String, ?>) uriVariables);
        Intrinsics.checkNotNullExpressionValue(postForEntity, "postForEntity(url, reque…class.java, uriVariables)");
        return postForEntity;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> postForEntity(RestOperations $this$postForEntity, String url, Object request, Map<String, ?> map) throws RestClientException {
        Intrinsics.checkNotNullParameter($this$postForEntity, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(map, "uriVariables");
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity<T> postForEntity = $this$postForEntity.postForEntity(url, request, Object.class, map);
        Intrinsics.checkNotNullExpressionValue(postForEntity, "postForEntity(url, reque…class.java, uriVariables)");
        return postForEntity;
    }

    public static /* synthetic */ ResponseEntity postForEntity$default(RestOperations $this$postForEntity_u24default, URI url, Object request, int i, Object obj) throws RestClientException {
        if ((i & 2) != 0) {
            request = null;
        }
        Intrinsics.checkNotNullParameter($this$postForEntity_u24default, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity postForEntity = $this$postForEntity_u24default.postForEntity(url, request, Object.class);
        Intrinsics.checkNotNullExpressionValue(postForEntity, "postForEntity(url, request, T::class.java)");
        return postForEntity;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> postForEntity(RestOperations $this$postForEntity, URI url, Object request) throws RestClientException {
        Intrinsics.checkNotNullParameter($this$postForEntity, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.reifiedOperationMarker(4, "T");
        ResponseEntity<T> postForEntity = $this$postForEntity.postForEntity(url, request, Object.class);
        Intrinsics.checkNotNullExpressionValue(postForEntity, "postForEntity(url, request, T::class.java)");
        return postForEntity;
    }

    public static /* synthetic */ ResponseEntity exchange$default(RestOperations $this$exchange_u24default, String url, HttpMethod method, HttpEntity requestEntity, Object[] uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 4) != 0) {
            requestEntity = null;
        }
        Intrinsics.checkNotNullParameter($this$exchange_u24default, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(method, "method");
        Intrinsics.checkNotNullParameter(uriVariables, "uriVariables");
        Intrinsics.needClassReification();
        ResponseEntity exchange = $this$exchange_u24default.exchange(url, method, (HttpEntity<?>) requestEntity, new RestOperationsExtensionsKt$exchange$1(), Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkNotNullExpressionValue(exchange, "exchange(url, method, re…e<T>() {}, *uriVariables)");
        return exchange;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> exchange(RestOperations $this$exchange, String url, HttpMethod method, HttpEntity<?> httpEntity, Object... uriVariables) throws RestClientException {
        Intrinsics.checkNotNullParameter($this$exchange, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(method, "method");
        Intrinsics.checkNotNullParameter(uriVariables, "uriVariables");
        Intrinsics.needClassReification();
        ResponseEntity<T> exchange = $this$exchange.exchange(url, method, httpEntity, new RestOperationsExtensionsKt$exchange$1(), Arrays.copyOf(uriVariables, uriVariables.length));
        Intrinsics.checkNotNullExpressionValue(exchange, "exchange(url, method, re…e<T>() {}, *uriVariables)");
        return exchange;
    }

    public static /* synthetic */ ResponseEntity exchange$default(RestOperations $this$exchange_u24default, String url, HttpMethod method, HttpEntity requestEntity, Map uriVariables, int i, Object obj) throws RestClientException {
        if ((i & 4) != 0) {
            requestEntity = null;
        }
        Intrinsics.checkNotNullParameter($this$exchange_u24default, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(method, "method");
        Intrinsics.checkNotNullParameter(uriVariables, "uriVariables");
        Intrinsics.needClassReification();
        ResponseEntity exchange = $this$exchange_u24default.exchange(url, method, (HttpEntity<?>) requestEntity, new RestOperationsExtensionsKt$exchange$2(), (Map<String, ?>) uriVariables);
        Intrinsics.checkNotNullExpressionValue(exchange, "exchange(url, method, re…ce<T>() {}, uriVariables)");
        return exchange;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> exchange(RestOperations $this$exchange, String url, HttpMethod method, HttpEntity<?> httpEntity, Map<String, ?> map) throws RestClientException {
        Intrinsics.checkNotNullParameter($this$exchange, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(method, "method");
        Intrinsics.checkNotNullParameter(map, "uriVariables");
        Intrinsics.needClassReification();
        ResponseEntity<T> exchange = $this$exchange.exchange(url, method, httpEntity, new RestOperationsExtensionsKt$exchange$2(), map);
        Intrinsics.checkNotNullExpressionValue(exchange, "exchange(url, method, re…ce<T>() {}, uriVariables)");
        return exchange;
    }

    public static /* synthetic */ ResponseEntity exchange$default(RestOperations $this$exchange_u24default, URI url, HttpMethod method, HttpEntity requestEntity, int i, Object obj) throws RestClientException {
        if ((i & 4) != 0) {
            requestEntity = null;
        }
        Intrinsics.checkNotNullParameter($this$exchange_u24default, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(method, "method");
        Intrinsics.needClassReification();
        ResponseEntity exchange = $this$exchange_u24default.exchange(url, method, (HttpEntity<?>) requestEntity, new RestOperationsExtensionsKt$exchange$3());
        Intrinsics.checkNotNullExpressionValue(exchange, "exchange(url, method, re…zedTypeReference<T>() {})");
        return exchange;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> exchange(RestOperations $this$exchange, URI url, HttpMethod method, HttpEntity<?> httpEntity) throws RestClientException {
        Intrinsics.checkNotNullParameter($this$exchange, "<this>");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(method, "method");
        Intrinsics.needClassReification();
        ResponseEntity<T> exchange = $this$exchange.exchange(url, method, httpEntity, new RestOperationsExtensionsKt$exchange$3());
        Intrinsics.checkNotNullExpressionValue(exchange, "exchange(url, method, re…zedTypeReference<T>() {})");
        return exchange;
    }

    public static final /* synthetic */ <T> ResponseEntity<T> exchange(RestOperations $this$exchange, RequestEntity<?> requestEntity) throws RestClientException {
        Intrinsics.checkNotNullParameter($this$exchange, "<this>");
        Intrinsics.checkNotNullParameter(requestEntity, "requestEntity");
        Intrinsics.needClassReification();
        ResponseEntity<T> exchange = $this$exchange.exchange(requestEntity, new ParameterizedTypeReference<T>() { // from class: org.springframework.web.client.RestOperationsExtensionsKt$exchange$4
        });
        Intrinsics.checkNotNullExpressionValue(exchange, "exchange(requestEntity, …zedTypeReference<T>() {})");
        return exchange;
    }
}
