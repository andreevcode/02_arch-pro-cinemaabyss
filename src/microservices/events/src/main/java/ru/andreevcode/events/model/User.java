package ru.andreevcode.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record User(
        @JsonProperty("user_id")
        Long userId,
        String username,
        String action,
        Instant timestamp) {
}
