package com.company.boot.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @RestController = @Controller + @ResponseBody:
 * every return value is written straight into the HTTP response body
 * (String -> text/plain, objects/maps -> JSON), no view/JSP involved.
 *
 * @RequestMapping("/api") on the class is a prefix for every method below.
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    /** GET /api/hello */
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    /**
     * GET /api/hello/{name}
     * @PathVariable takes the value from the URL PATH: /api/hello/Mohit -> name = "Mohit".
     */
    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable("name") String name) {
        return "Hello, " + name + "!";
    }

    /**
     * GET /api/greet?name=Mohit&lang=hi
     * @RequestParam takes values from the QUERY STRING.
     * - name: required (missing -> 400 Bad Request, handled by Spring)
     * - lang: optional, defaults to "en"
     * ResponseEntity lets us choose the status: 400 for a blank name or unknown language.
     */
    @GetMapping("/greet")
    public ResponseEntity<Map<String, Object>> greet(@RequestParam("name") String name,
                                                     @RequestParam(name = "lang", defaultValue = "en") String lang) {
        Map<String, Object> body = new LinkedHashMap<>();

        if (name.isBlank()) {
            body.put("error", "Query parameter 'name' must not be blank");
            return ResponseEntity.badRequest().body(body);
        }

        String salutation;
        switch (lang) {
            case "en":
                salutation = "Hello";
                break;
            case "hi":
                salutation = "Namaste";
                break;
            case "fr":
                salutation = "Bonjour";
                break;
            default:
                body.put("error", "Unsupported lang '" + lang + "'. Use en, hi or fr");
                return ResponseEntity.badRequest().body(body);
        }

        body.put("greeting", salutation + ", " + name.trim() + "!");
        body.put("name", name.trim());
        body.put("lang", lang);
        return ResponseEntity.ok(body);
    }
}
