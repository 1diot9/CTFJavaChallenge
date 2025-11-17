package org.springframework.web.servlet.function;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import kotlin.Metadata;
import kotlin.PublishedApi;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.servlet.tags.BindTag;

/* compiled from: RouterFunctionDsl.kt */
@Metadata(mv = {1, 7, 0}, k = 1, xi = 48, d1 = {"��¢\u0001\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0003\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010%\n\u0002\b\u0007\u0018��2\u00020\u0001B \b��\u0012\u0017\u0010\u0002\u001a\u0013\u0012\u0004\u0012\u00020��\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0002\b\u0005¢\u0006\u0002\u0010\u0006J\u001a\u0010\u000e\u001a\u00020\u00042\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u000e\u0010\u000e\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\"\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J*\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\"\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u001a\u0010\u0016\u001a\u00020\u00042\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u000e\u0010\u0016\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\"\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J*\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\"\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u001a\u0010\u0017\u001a\u00020\u00042\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u000e\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\"\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J*\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\"\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u001a\u0010\u0018\u001a\u00020\u00042\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u000e\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\"\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J*\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\"\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u001a\u0010\u0019\u001a\u00020\u00042\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u000e\u0010\u0019\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\"\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J*\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\"\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u001a\u0010\u001a\u001a\u00020\u00042\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u000e\u0010\u001a\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\"\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J*\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\"\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u001a\u0010\u001b\u001a\u00020\u00042\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u000e\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\"\u0010\u001b\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J*\u0010\u001b\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\"\u0010\u001b\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u001f\u0010\u001c\u001a\u00020\u00122\u0012\u0010\u001d\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u001f0\u001e\"\u00020\u001f¢\u0006\u0002\u0010 J\"\u0010\u001c\u001a\u00020\u00042\u0006\u0010\u001d\u001a\u00020\u001f2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u000e\u0010!\u001a\n \t*\u0004\u0018\u00010\"0\"J\u0014\u0010#\u001a\u00020\u00042\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00110%J \u0010&\u001a\u00020\u00042\u0018\u0010'\u001a\u0014\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00110(J\u000e\u0010)\u001a\n \t*\u0004\u0018\u00010\"0\"J\u001a\u0010*\u001a\u00020\u00042\u0012\u0010+\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100\u0003J\u0013\u0010,\u001a\b\u0012\u0004\u0012\u00020\u00110%H��¢\u0006\u0002\b-J\u001f\u0010.\u001a\u00020\u00122\u0012\u0010/\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u001f0\u001e\"\u00020\u001f¢\u0006\u0002\u0010 J\"\u0010.\u001a\u00020\u00042\u0006\u0010\u001d\u001a\u00020\u001f2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u0016\u00100\u001a\n \t*\u0004\u0018\u00010\"0\"2\u0006\u00101\u001a\u000202J,\u00103\u001a\u00020\u00042$\u00104\u001a \u0012\u0004\u0012\u00020\u0010\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003\u0012\u0004\u0012\u00020\u00110(J\u0016\u00105\u001a\n \t*\u0004\u0018\u00010\"0\"2\u0006\u00106\u001a\u00020\u0011J\u001a\u00107\u001a\u00020\u00122\u0012\u00108\u001a\u000e\u0012\u0004\u0012\u000209\u0012\u0004\u0012\u00020:0\u0003J.\u00107\u001a\u00020\u00042\u0012\u00108\u001a\u000e\u0012\u0004\u0012\u000209\u0012\u0004\u0012\u00020:0\u00032\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u000e\u0010;\u001a\u00020\u00122\u0006\u0010<\u001a\u00020=J\"\u0010;\u001a\u00020\u00042\u0006\u0010<\u001a\u00020=2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u0016\u0010>\u001a\u0012\u0012\u0002\b\u0003 \t*\b\u0012\u0002\b\u0003\u0018\u00010?0?J\u0016\u0010@\u001a\u0012\u0012\u0002\b\u0003 \t*\b\u0012\u0002\b\u0003\u0018\u00010?0?J\u000e\u0010A\u001a\n \t*\u0004\u0018\u00010\"0\"J4\u0010B\u001a\u00020\u00042\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020C\u0012\u0004\u0012\u00020:0\u00032\u0018\u0010D\u001a\u0014\u0012\u0004\u0012\u00020C\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110(J4\u0010B\u001a\u00020\u0004\"\n\b��\u0010E\u0018\u0001*\u00020C2\u001a\b\b\u0010D\u001a\u0014\u0012\u0004\u0012\u00020C\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110(H\u0086\bø\u0001��J\"\u0010F\u001a\u00020\u00122\u0006\u0010G\u001a\u00020\u00142\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020:0\u0003J6\u0010F\u001a\u00020\u00042\u0006\u0010G\u001a\u00020\u00142\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020:0\u00032\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u000e\u0010H\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\"\u0010H\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u001a\u0010I\u001a\u00020\u00122\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020:0\u0003J.\u0010I\u001a\u00020\u00042\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020:0\u00032\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u000e\u0010I\u001a\u00020\u00122\u0006\u0010J\u001a\u00020\u0014J\"\u0010I\u001a\u00020\u00042\u0006\u0010J\u001a\u00020\u00142\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003J\u0016\u0010K\u001a\n \t*\u0004\u0018\u00010\"0\"2\u0006\u00101\u001a\u000202J\u001c\u0010L\u001a\u00020\u00042\u0014\u0010M\u001a\u0010\u0012\u0004\u0012\u00020\u0010\u0012\u0006\u0012\u0004\u0018\u00010N0\u0003J\u0016\u0010L\u001a\u00020\u00042\u0006\u0010H\u001a\u00020\u00142\u0006\u00101\u001a\u00020NJ\u0016\u0010O\u001a\n \t*\u0004\u0018\u00010\"0\"2\u0006\u00101\u001a\u000202J\u0016\u0010P\u001a\n \t*\u0004\u0018\u00010\"0\"2\u0006\u0010P\u001a\u00020QJ\u0016\u0010P\u001a\n \t*\u0004\u0018\u00010\"0\"2\u0006\u0010P\u001a\u00020RJ\u0016\u0010S\u001a\n \t*\u0004\u0018\u00010\"0\"2\u0006\u00101\u001a\u000202J\u000e\u0010T\u001a\n \t*\u0004\u0018\u00010\"0\"J\u0016\u0010U\u001a\u00020\u00042\u0006\u0010G\u001a\u00020\u00142\u0006\u0010V\u001a\u00020\u0001J&\u0010W\u001a\u00020\u00042\u001e\u0010X\u001a\u001a\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010Y\u0012\u0004\u0012\u00020\u00040\u0003J\u0015\u0010Z\u001a\u00020\u0012*\u00020\u00142\u0006\u00106\u001a\u00020\u0012H\u0086\u0004J\u0015\u0010Z\u001a\u00020\u0012*\u00020\u00122\u0006\u00106\u001a\u00020\u0014H\u0086\u0004J\u0015\u0010Z\u001a\u00020\u0012*\u00020\u00122\u0006\u00106\u001a\u00020\u0012H\u0086\u0004J!\u0010[\u001a\u00020\u0004*\u00020\u00142\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003H\u0086\u0002J!\u0010[\u001a\u00020\u0004*\u00020\u00122\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u0003H\u0086\u0002J#\u0010\\\u001a\u00020\u0004*\u00020\u00142\u0017\u0010]\u001a\u0013\u0012\u0004\u0012\u00020��\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0002\b\u0005J#\u0010\\\u001a\u00020\u0004*\u00020\u00122\u0017\u0010]\u001a\u0013\u0012\u0004\u0012\u00020��\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0002\b\u0005J\r\u0010^\u001a\u00020\u0012*\u00020\u0012H\u0086\u0002J\u0015\u0010_\u001a\u00020\u0012*\u00020\u00142\u0006\u00106\u001a\u00020\u0012H\u0086\u0004J\u0015\u0010_\u001a\u00020\u0012*\u00020\u00122\u0006\u00106\u001a\u00020\u0014H\u0086\u0004J\u0015\u0010_\u001a\u00020\u0012*\u00020\u00122\u0006\u00106\u001a\u00020\u0012H\u0086\u0004R$\u0010\u0007\u001a\n \t*\u0004\u0018\u00010\b0\b8��X\u0081\u0004¢\u0006\u000e\n��\u0012\u0004\b\n\u0010\u000b\u001a\u0004\b\f\u0010\rR\u001f\u0010\u0002\u001a\u0013\u0012\u0004\u0012\u00020��\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0002\b\u0005X\u0082\u0004¢\u0006\u0002\n��\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006`"}, d2 = {"Lorg/springframework/web/servlet/function/RouterFunctionDsl;", "", "init", "Lkotlin/Function1;", "", "Lkotlin/ExtensionFunctionType;", "(Lkotlin/jvm/functions/Function1;)V", "builder", "Lorg/springframework/web/servlet/function/RouterFunctions$Builder;", "kotlin.jvm.PlatformType", "getBuilder$annotations", "()V", "getBuilder", "()Lorg/springframework/web/servlet/function/RouterFunctions$Builder;", "DELETE", "f", "Lorg/springframework/web/servlet/function/ServerRequest;", "Lorg/springframework/web/servlet/function/ServerResponse;", "Lorg/springframework/web/servlet/function/RequestPredicate;", "pattern", "", "predicate", "GET", WebContentGenerator.METHOD_HEAD, "OPTIONS", "PATCH", WebContentGenerator.METHOD_POST, "PUT", "accept", "mediaType", "", "Lorg/springframework/http/MediaType;", "([Lorg/springframework/http/MediaType;)Lorg/springframework/web/servlet/function/RequestPredicate;", "accepted", "Lorg/springframework/web/servlet/function/ServerResponse$BodyBuilder;", BeanUtil.PREFIX_ADDER, "routerFunction", "Lorg/springframework/web/servlet/function/RouterFunction;", "after", "responseProcessor", "Lkotlin/Function2;", "badRequest", "before", "requestProcessor", JsonPOJOBuilder.DEFAULT_BUILD_METHOD, "build$spring_webmvc", "contentType", "mediaTypes", "created", "location", "Ljava/net/URI;", "filter", "filterFunction", "from", "other", "headers", "headersPredicate", "Lorg/springframework/web/servlet/function/ServerRequest$Headers;", "", "method", "httpMethod", "Lorg/springframework/http/HttpMethod;", "noContent", "Lorg/springframework/web/servlet/function/ServerResponse$HeadersBuilder;", "notFound", "ok", "onError", "", "responseProvider", "E", "param", "name", "path", "pathExtension", "extension", "permanentRedirect", "resources", "lookupFunction", "Lorg/springframework/core/io/Resource;", "seeOther", BindTag.STATUS_VARIABLE_NAME, "", "Lorg/springframework/http/HttpStatusCode;", "temporaryRedirect", "unprocessableEntity", "withAttribute", "value", "withAttributes", "attributesConsumer", "", "and", "invoke", "nest", "r", "not", "or", "spring-webmvc"})
/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RouterFunctionDsl.class */
public final class RouterFunctionDsl {

    @NotNull
    private final Function1<RouterFunctionDsl, Unit> init;
    private final RouterFunctions.Builder builder;

    @PublishedApi
    public static /* synthetic */ void getBuilder$annotations() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public RouterFunctionDsl(@NotNull Function1<? super RouterFunctionDsl, Unit> init) {
        Intrinsics.checkNotNullParameter(init, "init");
        this.init = init;
        this.builder = RouterFunctions.route();
    }

    public final RouterFunctions.Builder getBuilder() {
        return this.builder;
    }

    @NotNull
    public final RequestPredicate and(@NotNull RequestPredicate $this$and, @NotNull String other) {
        Intrinsics.checkNotNullParameter($this$and, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        RequestPredicate and = $this$and.and(path(other));
        Intrinsics.checkNotNullExpressionValue(and, "this.and(path(other))");
        return and;
    }

    @NotNull
    public final RequestPredicate or(@NotNull RequestPredicate $this$or, @NotNull String other) {
        Intrinsics.checkNotNullParameter($this$or, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        RequestPredicate or = $this$or.or(path(other));
        Intrinsics.checkNotNullExpressionValue(or, "this.or(path(other))");
        return or;
    }

    @NotNull
    public final RequestPredicate and(@NotNull String $this$and, @NotNull RequestPredicate other) {
        Intrinsics.checkNotNullParameter($this$and, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        RequestPredicate and = path($this$and).and(other);
        Intrinsics.checkNotNullExpressionValue(and, "path(this).and(other)");
        return and;
    }

    @NotNull
    public final RequestPredicate or(@NotNull String $this$or, @NotNull RequestPredicate other) {
        Intrinsics.checkNotNullParameter($this$or, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        RequestPredicate or = path($this$or).or(other);
        Intrinsics.checkNotNullExpressionValue(or, "path(this).or(other)");
        return or;
    }

    @NotNull
    public final RequestPredicate and(@NotNull RequestPredicate $this$and, @NotNull RequestPredicate other) {
        Intrinsics.checkNotNullParameter($this$and, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        RequestPredicate and = $this$and.and(other);
        Intrinsics.checkNotNullExpressionValue(and, "this.and(other)");
        return and;
    }

    @NotNull
    public final RequestPredicate or(@NotNull RequestPredicate $this$or, @NotNull RequestPredicate other) {
        Intrinsics.checkNotNullParameter($this$or, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        RequestPredicate or = $this$or.or(other);
        Intrinsics.checkNotNullExpressionValue(or, "this.or(other)");
        return or;
    }

    @NotNull
    public final RequestPredicate not(@NotNull RequestPredicate $this$not) {
        Intrinsics.checkNotNullParameter($this$not, "<this>");
        RequestPredicate negate = $this$not.negate();
        Intrinsics.checkNotNullExpressionValue(negate, "this.negate()");
        return negate;
    }

    public final void nest(@NotNull RequestPredicate $this$nest, @NotNull Function1<? super RouterFunctionDsl, Unit> r) {
        Intrinsics.checkNotNullParameter($this$nest, "<this>");
        Intrinsics.checkNotNullParameter(r, "r");
        RouterFunctions.Builder builder = this.builder;
        RouterFunctionDsl routerFunctionDsl = new RouterFunctionDsl(r);
        builder.nest($this$nest, routerFunctionDsl::build$spring_webmvc);
    }

    public final void nest(@NotNull String $this$nest, @NotNull Function1<? super RouterFunctionDsl, Unit> r) {
        Intrinsics.checkNotNullParameter($this$nest, "<this>");
        Intrinsics.checkNotNullParameter(r, "r");
        nest(path($this$nest), r);
    }

    public final void GET(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.GET((v1) -> {
            return GET$lambda$0(r1, v1);
        });
    }

    private static final ServerResponse GET$lambda$0(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void GET(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.GET(pattern, (v1) -> {
            return GET$lambda$1(r2, v1);
        });
    }

    private static final ServerResponse GET$lambda$1(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void GET(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.GET(predicate, (v1) -> {
            return GET$lambda$2(r2, v1);
        });
    }

    private static final ServerResponse GET$lambda$2(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void GET(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.GET(pattern, predicate, (v1) -> {
            return GET$lambda$3(r3, v1);
        });
    }

    private static final ServerResponse GET$lambda$3(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate GET(@NotNull String pattern) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        RequestPredicate GET = RequestPredicates.GET(pattern);
        Intrinsics.checkNotNullExpressionValue(GET, "GET(pattern)");
        return GET;
    }

    public final void HEAD(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.HEAD((v1) -> {
            return HEAD$lambda$4(r1, v1);
        });
    }

    private static final ServerResponse HEAD$lambda$4(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void HEAD(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.HEAD(pattern, (v1) -> {
            return HEAD$lambda$5(r2, v1);
        });
    }

    private static final ServerResponse HEAD$lambda$5(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void HEAD(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.HEAD(predicate, (v1) -> {
            return HEAD$lambda$6(r2, v1);
        });
    }

    private static final ServerResponse HEAD$lambda$6(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void HEAD(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.HEAD(pattern, predicate, (v1) -> {
            return HEAD$lambda$7(r3, v1);
        });
    }

    private static final ServerResponse HEAD$lambda$7(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate HEAD(@NotNull String pattern) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        RequestPredicate HEAD = RequestPredicates.HEAD(pattern);
        Intrinsics.checkNotNullExpressionValue(HEAD, "HEAD(pattern)");
        return HEAD;
    }

    public final void POST(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.POST((v1) -> {
            return POST$lambda$8(r1, v1);
        });
    }

    private static final ServerResponse POST$lambda$8(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void POST(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.POST(pattern, (v1) -> {
            return POST$lambda$9(r2, v1);
        });
    }

    private static final ServerResponse POST$lambda$9(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void POST(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.POST(predicate, (v1) -> {
            return POST$lambda$10(r2, v1);
        });
    }

    private static final ServerResponse POST$lambda$10(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void POST(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.POST(pattern, predicate, (v1) -> {
            return POST$lambda$11(r3, v1);
        });
    }

    private static final ServerResponse POST$lambda$11(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate POST(@NotNull String pattern) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        RequestPredicate POST = RequestPredicates.POST(pattern);
        Intrinsics.checkNotNullExpressionValue(POST, "POST(pattern)");
        return POST;
    }

    public final void PUT(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.PUT((v1) -> {
            return PUT$lambda$12(r1, v1);
        });
    }

    private static final ServerResponse PUT$lambda$12(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void PUT(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.PUT(pattern, (v1) -> {
            return PUT$lambda$13(r2, v1);
        });
    }

    private static final ServerResponse PUT$lambda$13(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void PUT(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.PUT(predicate, (v1) -> {
            return PUT$lambda$14(r2, v1);
        });
    }

    private static final ServerResponse PUT$lambda$14(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void PUT(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.PUT(pattern, predicate, (v1) -> {
            return PUT$lambda$15(r3, v1);
        });
    }

    private static final ServerResponse PUT$lambda$15(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate PUT(@NotNull String pattern) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        RequestPredicate PUT = RequestPredicates.PUT(pattern);
        Intrinsics.checkNotNullExpressionValue(PUT, "PUT(pattern)");
        return PUT;
    }

    public final void PATCH(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.PATCH((v1) -> {
            return PATCH$lambda$16(r1, v1);
        });
    }

    private static final ServerResponse PATCH$lambda$16(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void PATCH(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.PATCH(pattern, (v1) -> {
            return PATCH$lambda$17(r2, v1);
        });
    }

    private static final ServerResponse PATCH$lambda$17(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void PATCH(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.PATCH(predicate, (v1) -> {
            return PATCH$lambda$18(r2, v1);
        });
    }

    private static final ServerResponse PATCH$lambda$18(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void PATCH(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.PATCH(pattern, predicate, (v1) -> {
            return PATCH$lambda$19(r3, v1);
        });
    }

    private static final ServerResponse PATCH$lambda$19(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate PATCH(@NotNull String pattern) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        RequestPredicate PATCH = RequestPredicates.PATCH(pattern);
        Intrinsics.checkNotNullExpressionValue(PATCH, "PATCH(pattern)");
        return PATCH;
    }

    public final void DELETE(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.DELETE((v1) -> {
            return DELETE$lambda$20(r1, v1);
        });
    }

    private static final ServerResponse DELETE$lambda$20(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void DELETE(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.DELETE(pattern, (v1) -> {
            return DELETE$lambda$21(r2, v1);
        });
    }

    private static final ServerResponse DELETE$lambda$21(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void DELETE(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.DELETE(predicate, (v1) -> {
            return DELETE$lambda$22(r2, v1);
        });
    }

    private static final ServerResponse DELETE$lambda$22(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void DELETE(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.DELETE(pattern, predicate, (v1) -> {
            return DELETE$lambda$23(r3, v1);
        });
    }

    private static final ServerResponse DELETE$lambda$23(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate DELETE(@NotNull String pattern) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        RequestPredicate DELETE = RequestPredicates.DELETE(pattern);
        Intrinsics.checkNotNullExpressionValue(DELETE, "DELETE(pattern)");
        return DELETE;
    }

    public final void OPTIONS(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.OPTIONS((v1) -> {
            return OPTIONS$lambda$24(r1, v1);
        });
    }

    private static final ServerResponse OPTIONS$lambda$24(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void OPTIONS(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.OPTIONS(pattern, (v1) -> {
            return OPTIONS$lambda$25(r2, v1);
        });
    }

    private static final ServerResponse OPTIONS$lambda$25(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void OPTIONS(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.OPTIONS(predicate, (v1) -> {
            return OPTIONS$lambda$26(r2, v1);
        });
    }

    private static final ServerResponse OPTIONS$lambda$26(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void OPTIONS(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.OPTIONS(pattern, predicate, (v1) -> {
            return OPTIONS$lambda$27(r3, v1);
        });
    }

    private static final ServerResponse OPTIONS$lambda$27(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate OPTIONS(@NotNull String pattern) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        RequestPredicate OPTIONS = RequestPredicates.OPTIONS(pattern);
        Intrinsics.checkNotNullExpressionValue(OPTIONS, "OPTIONS(pattern)");
        return OPTIONS;
    }

    public final void accept(@NotNull MediaType mediaType, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(mediaType, "mediaType");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.accept(mediaType), (v1) -> {
            return accept$lambda$28(r2, v1);
        }));
    }

    private static final ServerResponse accept$lambda$28(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate accept(@NotNull MediaType... mediaType) {
        Intrinsics.checkNotNullParameter(mediaType, "mediaType");
        RequestPredicate accept = RequestPredicates.accept((MediaType[]) Arrays.copyOf(mediaType, mediaType.length));
        Intrinsics.checkNotNullExpressionValue(accept, "accept(*mediaType)");
        return accept;
    }

    public final void contentType(@NotNull MediaType mediaType, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(mediaType, "mediaType");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.contentType(mediaType), (v1) -> {
            return contentType$lambda$29(r2, v1);
        }));
    }

    private static final ServerResponse contentType$lambda$29(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate contentType(@NotNull MediaType... mediaTypes) {
        Intrinsics.checkNotNullParameter(mediaTypes, "mediaTypes");
        RequestPredicate contentType = RequestPredicates.contentType((MediaType[]) Arrays.copyOf(mediaTypes, mediaTypes.length));
        Intrinsics.checkNotNullExpressionValue(contentType, "contentType(*mediaTypes)");
        return contentType;
    }

    public final void headers(@NotNull Function1<? super ServerRequest.Headers, Boolean> headersPredicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(headersPredicate, "headersPredicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.headers((v1) -> {
            return headers$lambda$30(r1, v1);
        }), (v1) -> {
            return headers$lambda$31(r2, v1);
        }));
    }

    private static final boolean headers$lambda$30(Function1 $tmp0, ServerRequest.Headers p0) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return ((Boolean) $tmp0.invoke(p0)).booleanValue();
    }

    private static final ServerResponse headers$lambda$31(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate headers(@NotNull Function1<? super ServerRequest.Headers, Boolean> headersPredicate) {
        Intrinsics.checkNotNullParameter(headersPredicate, "headersPredicate");
        RequestPredicate headers = RequestPredicates.headers((v1) -> {
            return headers$lambda$32(r0, v1);
        });
        Intrinsics.checkNotNullExpressionValue(headers, "headers(headersPredicate)");
        return headers;
    }

    private static final boolean headers$lambda$32(Function1 $tmp0, ServerRequest.Headers p0) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return ((Boolean) $tmp0.invoke(p0)).booleanValue();
    }

    public final void method(@NotNull HttpMethod httpMethod, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(httpMethod, "httpMethod");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.method(httpMethod), (v1) -> {
            return method$lambda$33(r2, v1);
        }));
    }

    private static final ServerResponse method$lambda$33(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate method(@NotNull HttpMethod httpMethod) {
        Intrinsics.checkNotNullParameter(httpMethod, "httpMethod");
        RequestPredicate method = RequestPredicates.method(httpMethod);
        Intrinsics.checkNotNullExpressionValue(method, "method(httpMethod)");
        return method;
    }

    public final void path(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.path(pattern), (v1) -> {
            return path$lambda$34(r2, v1);
        }));
    }

    private static final ServerResponse path$lambda$34(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate path(@NotNull String pattern) {
        Intrinsics.checkNotNullParameter(pattern, "pattern");
        RequestPredicate path = RequestPredicates.path(pattern);
        Intrinsics.checkNotNullExpressionValue(path, "path(pattern)");
        return path;
    }

    public final void pathExtension(@NotNull String extension, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(extension, "extension");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.pathExtension(extension), (v1) -> {
            return pathExtension$lambda$35(r2, v1);
        }));
    }

    private static final ServerResponse pathExtension$lambda$35(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate pathExtension(@NotNull String extension) {
        Intrinsics.checkNotNullParameter(extension, "extension");
        RequestPredicate pathExtension = RequestPredicates.pathExtension(extension);
        Intrinsics.checkNotNullExpressionValue(pathExtension, "pathExtension(extension)");
        return pathExtension;
    }

    public final void pathExtension(@NotNull Function1<? super String, Boolean> predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.pathExtension((Predicate<String>) (v1) -> {
            return pathExtension$lambda$36(r1, v1);
        }), (v1) -> {
            return pathExtension$lambda$37(r2, v1);
        }));
    }

    private static final boolean pathExtension$lambda$36(Function1 $tmp0, String p0) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return ((Boolean) $tmp0.invoke(p0)).booleanValue();
    }

    private static final ServerResponse pathExtension$lambda$37(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate pathExtension(@NotNull Function1<? super String, Boolean> predicate) {
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        RequestPredicate pathExtension = RequestPredicates.pathExtension((Predicate<String>) (v1) -> {
            return pathExtension$lambda$38(r0, v1);
        });
        Intrinsics.checkNotNullExpressionValue(pathExtension, "pathExtension(predicate)");
        return pathExtension;
    }

    private static final boolean pathExtension$lambda$38(Function1 $tmp0, String p0) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return ((Boolean) $tmp0.invoke(p0)).booleanValue();
    }

    public final void param(@NotNull String name, @NotNull Function1<? super String, Boolean> predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.param(name, (Predicate<String>) (v1) -> {
            return param$lambda$39(r2, v1);
        }), (v1) -> {
            return param$lambda$40(r2, v1);
        }));
    }

    private static final boolean param$lambda$39(Function1 $tmp0, String p0) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return ((Boolean) $tmp0.invoke(p0)).booleanValue();
    }

    private static final ServerResponse param$lambda$40(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    @NotNull
    public final RequestPredicate param(@NotNull String name, @NotNull Function1<? super String, Boolean> predicate) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        RequestPredicate param = RequestPredicates.param(name, (Predicate<String>) (v1) -> {
            return param$lambda$41(r1, v1);
        });
        Intrinsics.checkNotNullExpressionValue(param, "param(name, predicate)");
        return param;
    }

    private static final boolean param$lambda$41(Function1 $tmp0, String p0) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return ((Boolean) $tmp0.invoke(p0)).booleanValue();
    }

    public final void invoke(@NotNull RequestPredicate $this$invoke, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter($this$invoke, "<this>");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.add(RouterFunctions.route($this$invoke, (v1) -> {
            return invoke$lambda$42(r2, v1);
        }));
    }

    private static final ServerResponse invoke$lambda$42(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void invoke(@NotNull String $this$invoke, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkNotNullParameter($this$invoke, "<this>");
        Intrinsics.checkNotNullParameter(f, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.path($this$invoke), (v1) -> {
            return invoke$lambda$43(r2, v1);
        }));
    }

    private static final ServerResponse invoke$lambda$43(Function1 $tmp0, ServerRequest request) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(request);
    }

    public final void resources(@NotNull String path, @NotNull Resource location) {
        Intrinsics.checkNotNullParameter(path, "path");
        Intrinsics.checkNotNullParameter(location, "location");
        this.builder.resources(path, location);
    }

    public final void resources(@NotNull Function1<? super ServerRequest, ? extends Resource> lookupFunction) {
        Intrinsics.checkNotNullParameter(lookupFunction, "lookupFunction");
        this.builder.resources((v1) -> {
            return resources$lambda$44(r1, v1);
        });
    }

    private static final Optional resources$lambda$44(Function1 $lookupFunction, ServerRequest it) {
        Intrinsics.checkNotNullParameter($lookupFunction, "$lookupFunction");
        Intrinsics.checkNotNullExpressionValue(it, "it");
        return Optional.ofNullable($lookupFunction.invoke(it));
    }

    public final void add(@NotNull RouterFunction<ServerResponse> routerFunction) {
        Intrinsics.checkNotNullParameter(routerFunction, "routerFunction");
        this.builder.add(routerFunction);
    }

    public final void filter(@NotNull Function2<? super ServerRequest, ? super Function1<? super ServerRequest, ? extends ServerResponse>, ? extends ServerResponse> filterFunction) {
        Intrinsics.checkNotNullParameter(filterFunction, "filterFunction");
        this.builder.filter((v1, v2) -> {
            return filter$lambda$45(r1, v1, v2);
        });
    }

    private static final ServerResponse filter$lambda$45(Function2 $filterFunction, ServerRequest request, final HandlerFunction next) {
        Intrinsics.checkNotNullParameter($filterFunction, "$filterFunction");
        Intrinsics.checkNotNullExpressionValue(request, "request");
        return (ServerResponse) $filterFunction.invoke(request, new Function1<ServerRequest, ServerResponse>() { // from class: org.springframework.web.servlet.function.RouterFunctionDsl$filter$1$1
            /* JADX INFO: Access modifiers changed from: package-private */
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @NotNull
            public final ServerResponse invoke(@NotNull ServerRequest handlerRequest) {
                Intrinsics.checkNotNullParameter(handlerRequest, "handlerRequest");
                ServerResponse handle = next.handle(handlerRequest);
                Intrinsics.checkNotNullExpressionValue(handle, "next.handle(handlerRequest)");
                return handle;
            }
        });
    }

    public final void before(@NotNull Function1<? super ServerRequest, ? extends ServerRequest> requestProcessor) {
        Intrinsics.checkNotNullParameter(requestProcessor, "requestProcessor");
        this.builder.before((v1) -> {
            return before$lambda$46(r1, v1);
        });
    }

    private static final ServerRequest before$lambda$46(Function1 $tmp0, ServerRequest p0) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerRequest) $tmp0.invoke(p0);
    }

    public final void after(@NotNull Function2<? super ServerRequest, ? super ServerResponse, ? extends ServerResponse> responseProcessor) {
        Intrinsics.checkNotNullParameter(responseProcessor, "responseProcessor");
        this.builder.after((v1, v2) -> {
            return after$lambda$47(r1, v1, v2);
        });
    }

    private static final ServerResponse after$lambda$47(Function2 $tmp0, ServerRequest p0, ServerResponse p1) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(p0, p1);
    }

    public final void onError(@NotNull Function1<? super Throwable, Boolean> predicate, @NotNull Function2<? super Throwable, ? super ServerRequest, ? extends ServerResponse> responseProvider) {
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Intrinsics.checkNotNullParameter(responseProvider, "responseProvider");
        this.builder.onError((v1) -> {
            return onError$lambda$48(r1, v1);
        }, (v1, v2) -> {
            return onError$lambda$49(r2, v1, v2);
        });
    }

    private static final boolean onError$lambda$48(Function1 $tmp0, Throwable p0) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return ((Boolean) $tmp0.invoke(p0)).booleanValue();
    }

    private static final ServerResponse onError$lambda$49(Function2 $tmp0, Throwable p0, ServerRequest p1) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        return (ServerResponse) $tmp0.invoke(p0, p1);
    }

    public final /* synthetic */ <E extends Throwable> void onError(final Function2<? super Throwable, ? super ServerRequest, ? extends ServerResponse> function2) {
        Intrinsics.checkNotNullParameter(function2, "responseProvider");
        RouterFunctions.Builder builder = getBuilder();
        Intrinsics.needClassReification();
        builder.onError(new Predicate() { // from class: org.springframework.web.servlet.function.RouterFunctionDsl$onError$1
            @Override // java.util.function.Predicate
            public final boolean test(Throwable it) {
                Intrinsics.reifiedOperationMarker(3, "E");
                return it instanceof Throwable;
            }
        }, new BiFunction(function2) { // from class: org.springframework.web.servlet.function.RouterFunctionDslKt$sam$i$java_util_function_BiFunction$0
            private final /* synthetic */ Function2 function;

            {
                Intrinsics.checkNotNullParameter(function2, "function");
                this.function = function2;
            }

            @Override // java.util.function.BiFunction
            public final /* synthetic */ Object apply(Object p0, Object p1) {
                return this.function.invoke(p0, p1);
            }
        });
    }

    public final void withAttribute(@NotNull String name, @NotNull Object value) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(value, "value");
        this.builder.withAttribute(name, value);
    }

    public final void withAttributes(@NotNull Function1<? super Map<String, Object>, Unit> attributesConsumer) {
        Intrinsics.checkNotNullParameter(attributesConsumer, "attributesConsumer");
        this.builder.withAttributes((v1) -> {
            withAttributes$lambda$50(r1, v1);
        });
    }

    private static final void withAttributes$lambda$50(Function1 $tmp0, Map p0) {
        Intrinsics.checkNotNullParameter($tmp0, "$tmp0");
        $tmp0.invoke(p0);
    }

    @NotNull
    public final RouterFunction<ServerResponse> build$spring_webmvc() {
        this.init.invoke(this);
        RouterFunction<ServerResponse> build = this.builder.build();
        Intrinsics.checkNotNullExpressionValue(build, "builder.build()");
        return build;
    }

    public final ServerResponse.BodyBuilder from(@NotNull ServerResponse other) {
        Intrinsics.checkNotNullParameter(other, "other");
        return ServerResponse.from(other);
    }

    public final ServerResponse.BodyBuilder created(@NotNull URI location) {
        Intrinsics.checkNotNullParameter(location, "location");
        return ServerResponse.created(location);
    }

    public final ServerResponse.BodyBuilder ok() {
        return ServerResponse.ok();
    }

    public final ServerResponse.HeadersBuilder<?> noContent() {
        return ServerResponse.noContent();
    }

    public final ServerResponse.BodyBuilder accepted() {
        return ServerResponse.accepted();
    }

    public final ServerResponse.BodyBuilder permanentRedirect(@NotNull URI location) {
        Intrinsics.checkNotNullParameter(location, "location");
        return ServerResponse.permanentRedirect(location);
    }

    public final ServerResponse.BodyBuilder temporaryRedirect(@NotNull URI location) {
        Intrinsics.checkNotNullParameter(location, "location");
        return ServerResponse.temporaryRedirect(location);
    }

    public final ServerResponse.BodyBuilder seeOther(@NotNull URI location) {
        Intrinsics.checkNotNullParameter(location, "location");
        return ServerResponse.seeOther(location);
    }

    public final ServerResponse.BodyBuilder badRequest() {
        return ServerResponse.badRequest();
    }

    public final ServerResponse.HeadersBuilder<?> notFound() {
        return ServerResponse.notFound();
    }

    public final ServerResponse.BodyBuilder unprocessableEntity() {
        return ServerResponse.unprocessableEntity();
    }

    public final ServerResponse.BodyBuilder status(@NotNull HttpStatusCode status) {
        Intrinsics.checkNotNullParameter(status, BindTag.STATUS_VARIABLE_NAME);
        return ServerResponse.status(status);
    }

    public final ServerResponse.BodyBuilder status(int status) {
        return ServerResponse.status(status);
    }
}
