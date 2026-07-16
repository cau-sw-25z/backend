package com.example.BE.survey.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SurveyResponseRequest {

     @NotEmpty(message = "choiceIds는 최소 1개 이상이어야 합니다.")
    private List<Long> choiceIds;
}