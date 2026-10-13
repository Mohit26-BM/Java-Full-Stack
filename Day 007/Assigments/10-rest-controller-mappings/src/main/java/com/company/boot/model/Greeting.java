package com.company.boot.model;

import java.time.LocalDateTime;

/** A stored greeting (one row of a10_greetings). Serialised to JSON automatically. */
public record Greeting(int id, String name, String message, LocalDateTime createdAt) {
}
