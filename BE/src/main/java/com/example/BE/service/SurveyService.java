package com.example.BE.service;

import com.example.BE.dto.SurveyQuestionResponse;
import com.example.BE.dto.SurveyResponseRequest;
import com.example.BE.dto.SurveyResponseResult;
import com.example.BE.dto.SurveyResultResponse;
import com.example.BE.entity.Choice;
import com.example.BE.entity.Survey;
import com.example.BE.entity.User;
import com.example.BE.entity.UserSurveyResponse;
import com.example.BE.repository.ChoiceRepository;
import com.example.BE.repository.SurveyRepository;
import com.example.BE.repository.UserRepository;
import com.example.BE.repository.UserSurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final ChoiceRepository choiceRepository;
    private final UserRepository userRepository;
    private final UserSurveyResponseRepository userSurveyResponseRepository;

    public SurveyQuestionResponse getSurveyQuestions() {
        Survey survey = surveyRepository.findByActiveTrue()
                .orElseThrow(() -> new IllegalStateException("활성화된 설문이 없습니다."));

        return SurveyQuestionResponse.from(survey);
    }

    @Transactional
    public SurveyResponseResult saveSurveyResponse(SurveyResponseRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        

        List<Choice> choices = choiceRepository.findAllById(request.getChoiceIds());

        if (choices.size() != request.getChoiceIds().size()) {
            throw new IllegalArgumentException("존재하지 않는 선택지가 포함되어 있습니다.");
        }

        int totalScore = choices.stream()
                .mapToInt(Choice::getScore)
                .sum();
        
        String riskLevel = calculateRiskLevel(totalScore);

        UserSurveyResponse userSurveyResponse = new UserSurveyResponse(user, totalScore, riskLevel);
        userSurveyResponseRepository.save(userSurveyResponse);

        user.updateRiskLevel(riskLevel);

        return new SurveyResponseResult(user.getId(), totalScore, riskLevel);
    }

    public SurveyResultResponse getSurveyResult(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        return new SurveyResultResponse(user.getId(), user.getRiskLevel());
    }

    private String calculateRiskLevel(int totalScore) {
        if (totalScore <= 4) {
            return "CONSERVATIVE";
        }

        if (totalScore <= 6) {
            return "MODERATE_CONSERVATIVE";
        }

        if(totalScore <= 9) {
            return "MODERATE";
        }

        if(totalScore <= 12) {
            return "MODERATE_AGGRESSIVE";
        }

        return "AGGRESSIVE";
    }
}
