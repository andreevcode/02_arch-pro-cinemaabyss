package ru.andreevcode.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record Payment(
        @JsonProperty("payment_id")
        Long paymentId,
        @JsonProperty("user_id")
        Long userId,
        Double amount,
        String status,
        Instant timestamp,
        @JsonProperty("method_type")
        String methodType
      ) {
}