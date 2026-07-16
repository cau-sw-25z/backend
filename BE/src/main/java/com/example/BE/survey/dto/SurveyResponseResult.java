package com.example.BE.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SurveyResponseResult {

    private Long userId;
    private int totalScore;
    private String riskLevel;
}