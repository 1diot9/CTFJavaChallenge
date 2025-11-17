package org.springframework.web.multipart;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/multipart/MultipartRequest.class */
public interface MultipartRequest {
    Iterator<String> getFileNames();

    @Nullable
    MultipartFile getFile(String name);

    List<MultipartFile> getFiles(String name);

    Map<String, MultipartFile> getFileMap();

    MultiValueMap<String, MultipartFile> getMultiFileMap();

    @Nullable
    String getMultipartContentType(String paramOrFileName);
}
