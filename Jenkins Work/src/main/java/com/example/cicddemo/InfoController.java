package com.example.cicddemo;

import java.net.InetAddress;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

	@GetMapping("/api/info")
	public Map<String, String> info() throws Exception {
		String version = System.getenv("APP_VERSION");
		return Map.of(
				"host", InetAddress.getLocalHost().getHostName(),
				"version", version == null || version.isBlank() ? "local" : version);
	}

}
