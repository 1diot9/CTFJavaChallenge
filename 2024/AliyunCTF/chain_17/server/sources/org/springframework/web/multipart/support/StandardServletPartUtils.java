package org.springframework.web.multipart.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartException;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/multipart/support/StandardServletPartUtils.class */
public abstract class StandardServletPartUtils {
    public static MultiValueMap<String, Part> getParts(HttpServletRequest request) throws MultipartException {
        try {
            MultiValueMap<String, Part> parts = new LinkedMultiValueMap<>();
            for (Part part : request.getParts()) {
                parts.add(part.getName(), part);
            }
            return parts;
        } catch (Exception ex) {
            throw new MultipartException("Failed to get request parts", ex);
        }
    }

    public static List<Part> getParts(HttpServletRequest request, String name) throws MultipartException {
        try {
            List<Part> parts = new ArrayList<>(1);
            for (Part part : request.getParts()) {
                if (part.getName().equals(name)) {
                    parts.add(part);
                }
            }
            return parts;
        } catch (Exception ex) {
            throw new MultipartException("Failed to get request parts", ex);
        }
    }

    public static void bindParts(HttpServletRequest request, MutablePropertyValues mpvs, boolean bindEmpty) throws MultipartException {
        getParts(request).forEach((key, values) -> {
            if (values.size() == 1) {
                Part part = (Part) values.get(0);
                if (bindEmpty || part.getSize() > 0) {
                    mpvs.add(key, part);
                    return;
                }
                return;
            }
            mpvs.add(key, values);
        });
    }
}
