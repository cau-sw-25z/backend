package com.example.BE.controller;

import com.example.BE.dto.SurveyQuestionResponse;
import com.example.BE.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @GetMapping("/survey/questions")
    public SurveyQuestionResponse getSurveyQuestions() {
        return surveyService.getSurveyQuestions();
    }
}