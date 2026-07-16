package com.example.BE.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SurveyResultResponse {
    
    private Long userId;
    private String riskLevel;
}
