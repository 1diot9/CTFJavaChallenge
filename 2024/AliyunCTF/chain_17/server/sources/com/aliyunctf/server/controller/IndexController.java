package com.aliyunctf.server.controller;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
/* loaded from: server.jar:BOOT-INF/classes/com/aliyunctf/server/controller/IndexController.class */
public class IndexController {
    static String banner = "Welcome to AliyunCTF 2024, --server";

    @RequestMapping({"/"})
    public String index() {
        return banner;
    }

    @RequestMapping({"/read"})
    public String read(@RequestBody String body) {
        if (body != null) {
            try {
                byte[] data = Base64.getDecoder().decode(body);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                Object object = objectInputStream.readObject();
                return object.getClass().toString();
            } catch (Exception e) {
                return e.toString();
            }
        }
        return "ok";
    }
}
