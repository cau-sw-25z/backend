package com.example.BE.survey.controller;

import com.example.BE.common.response.ApiResponse;
import com.example.BE.survey.dto.SurveyQuestionResponse;
import com.example.BE.survey.dto.SurveyResponseRequest;
import com.example.BE.survey.dto.SurveyResponseResult;
import com.example.BE.survey.dto.SurveyResultResponse;
import com.example.BE.survey.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Survey", description = "투자성향 설문 API")
@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @Operation(summary = "설문 문항 조회", description = "현재 활성화된 설문의 문항과 선택지를 조회합니다.")
    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<SurveyQuestionResponse>> getSurveyQuestions() {
        SurveyQuestionResponse response = surveyService.getSurveyQuestions();
        return ResponseEntity.ok(ApiResponse.success(response, "설문 문항 조회 성공"));
    }

    @Operation(summary = "설문 응답 제출", description = "로그인한 사용자의 설문 응답을 저장하고 투자성향을 계산합니다.")
    @PostMapping("/response")
    public ResponseEntity<ApiResponse<SurveyResponseResult>> saveSurveyResponse(
            @Valid @RequestBody SurveyResponseRequest request
    ) {
        SurveyResponseResult response = surveyService.saveSurveyResponse(request);
        return ResponseEntity.ok(ApiResponse.success(response, "설문 응답 제출 성공"));
    }

    @Operation(summary = "설문 결과 조회", description = "로그인한 사용자의 투자성향 결과를 조회합니다.")
    @GetMapping("/result")
    public ResponseEntity<ApiResponse<SurveyResultResponse>> getSurveyResult() {
        SurveyResultResponse response = surveyService.getSurveyResult();
        return ResponseEntity.ok(ApiResponse.success(response, "설문 결과 조회 성공"));
    }
}
