package com.aliyunctf.agent.controller;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
/* loaded from: agent.jar:BOOT-INF/classes/com/aliyunctf/agent/controller/IndexController.class */
public class IndexController {
    static String banner = "Welcome to AliyunCTF2024, --agent";

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
                Hessian2Input hessian2Input = new Hessian2Input(byteArrayInputStream);
                Object object = hessian2Input.readObject();
                return object.getClass().toString();
            } catch (Exception e) {
                return e.toString();
            }
        }
        return "ok";
    }
}
