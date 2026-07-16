package com.example.BE.survey.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SurveyResponseRequest {

    private List<Long> choiceIds;
}