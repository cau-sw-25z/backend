package com.example.BE.service;

import com.example.BE.dto.SurveyQuestionResponse;
import com.example.BE.entity.Survey;
import com.example.BE.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyService {

    private final SurveyRepository surveyRepository;

    public SurveyQuestionResponse getSurveyQuestions() {
        Survey survey = surveyRepository.findByActiveTrue()
                .orElseThrow(() -> new IllegalStateException("활성화된 설문이 없습니다."));

        return SurveyQuestionResponse.from(survey);
    }
}
