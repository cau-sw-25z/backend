package com.example.BE.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SurveyResponseRequest {

    private Long userId;
    private List<Long> choiceIds;
}