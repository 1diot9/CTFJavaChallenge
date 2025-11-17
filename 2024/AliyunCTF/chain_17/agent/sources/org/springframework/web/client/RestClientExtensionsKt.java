package org.springframework.web.client;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/* compiled from: RestClientExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��\u001e\n\u0002\b\u0002\n\u0002\u0010��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\u001a \u0010��\u001a\u0004\u0018\u0001H\u0001\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0086\b¢\u0006\u0002\u0010\u0004\u001a&\u0010\u0005\u001a\u00020\u0006\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u00062\u0006\u0010��\u001a\u0002H\u0001H\u0086\b¢\u0006\u0002\u0010\u0007\u001a\u001f\u0010\b\u001a\b\u0012\u0004\u0012\u0002H\u00010\t\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0086\b¨\u0006\n"}, d2 = {"body", "T", "", "Lorg/springframework/web/client/RestClient$ResponseSpec;", "(Lorg/springframework/web/client/RestClient$ResponseSpec;)Ljava/lang/Object;", "bodyWithType", "Lorg/springframework/web/client/RestClient$RequestBodySpec;", "(Lorg/springframework/web/client/RestClient$RequestBodySpec;Ljava/lang/Object;)Lorg/springframework/web/client/RestClient$RequestBodySpec;", "toEntity", "Lorg/springframework/http/ResponseEntity;", "spring-web"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClientExtensionsKt.class */
public final class RestClientExtensionsKt {
    public static final /* synthetic */ <T> RestClient.RequestBodySpec bodyWithType(RestClient.RequestBodySpec $this$bodyWithType, T t) {
        Intrinsics.checkNotNullParameter($this$bodyWithType, "<this>");
        Intrinsics.checkNotNullParameter(t, "body");
        Intrinsics.needClassReification();
        RestClient.RequestBodySpec body = $this$bodyWithType.body(t, new ParameterizedTypeReference<T>() { // from class: org.springframework.web.client.RestClientExtensionsKt$bodyWithType$1
        });
        Intrinsics.checkNotNullExpressionValue(body, "body(body, object : Para…zedTypeReference<T>() {})");
        return body;
    }

    public static final /* synthetic */ <T> T body(RestClient.ResponseSpec responseSpec) {
        Intrinsics.checkNotNullParameter(responseSpec, "<this>");
        Intrinsics.needClassReification();
        return (T) responseSpec.body(new ParameterizedTypeReference<T>() { // from class: org.springframework.web.client.RestClientExtensionsKt$body$1
        });
    }

    public static final /* synthetic */ <T> ResponseEntity<T> toEntity(RestClient.ResponseSpec $this$toEntity) {
        Intrinsics.checkNotNullParameter($this$toEntity, "<this>");
        Intrinsics.needClassReification();
        ResponseEntity<T> entity = $this$toEntity.toEntity(new ParameterizedTypeReference<T>() { // from class: org.springframework.web.client.RestClientExtensionsKt$toEntity$1
        });
        Intrinsics.checkNotNullExpressionValue(entity, "toEntity(object : Parame…zedTypeReference<T>() {})");
        return entity;
    }
}
