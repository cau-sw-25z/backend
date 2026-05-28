package com.example.BE.controller;

import com.example.BE.dto.SurveyQuestionResponse;
import com.example.BE.service.SurveyService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.BE.dto.SurveyResponseRequest;
import com.example.BE.dto.SurveyResultResponse;
import com.example.BE.dto.SurveyResponseResult;

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
    public SurveyResultResponse getSurveyResult(@RequestParam Long userId) {
        return surveyService.getSurveyResult(userId);
    }
}