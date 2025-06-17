package ru.andreevcode.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Movie(
        @JsonProperty("movie_id")
        Long movieId,
        String title,
        String action,
        @JsonProperty("user_id")
        Long userId
) {
}
