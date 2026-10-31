package com.dockerdemoapp;

import java.net.InetAddress;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    @GetMapping("/api/info")
    public Map<String, String> info() throws Exception {
        return Map.of(
                "host", InetAddress.getLocalHost().getHostName(),
                "app", "KubernetesDemoApp");
    }
}
