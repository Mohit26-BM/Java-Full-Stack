package com.company.boot.model;

/** JSON body of POST /api/greetings: {"name":"...","message":"..."} */
public record GreetingRequest(String name, String message) {
}
