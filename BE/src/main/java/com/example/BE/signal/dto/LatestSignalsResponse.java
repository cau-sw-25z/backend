package com.example.BE.signal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record LatestSignalsResponse(
        @JsonProperty("signal_date")
        LocalDate signalDate,

        int count,

        List<SignalItemResponse> signals
) {
}