package com.example.BE.survey.controller;

import com.example.BE.survey.dto.SurveyQuestionResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.BE.survey.dto.SurveyResponseRequest;
import com.example.BE.survey.dto.SurveyResultResponse;
import com.example.BE.survey.service.SurveyService;
import com.example.BE.survey.dto.SurveyResponseResult;

@RestController
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @GetMapping("/survey/questions")
    public SurveyQuestionResponse getSurveyQuestions() {
        return surveyService.getSurveyQuestions();
    }

    @PostMapping("/survey/response")
    public SurveyResponseResult saveSurveyResponse(@RequestBody SurveyResponseRequest request) {
        return surveyService.saveSurveyResponse(request);
    }

    @GetMapping("/survey/result")
    public SurveyResultResponse getSurveyResult() {
        return surveyService.getSurveyResult();
    }
}