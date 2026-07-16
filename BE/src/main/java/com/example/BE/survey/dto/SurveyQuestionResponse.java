package com.example.BE.survey.dto;

import java.util.List;

import com.example.BE.survey.entity.Choice;
import com.example.BE.survey.entity.Question;
import com.example.BE.survey.entity.Survey;

public record SurveyQuestionResponse(
        Long surveyId,
        String title,
        String description,
        List<QuestionResponse> questions
) {

    public static SurveyQuestionResponse from(Survey survey) {
        return new SurveyQuestionResponse(
                survey.getId(),
                survey.getTitle(),
                survey.getDescription(),
                survey.getQuestions().stream()
                        .map(QuestionResponse::from)
                        .toList()
        );
    }

    public record QuestionResponse(
            Long questionId,
            String content,
            Integer displayOrder,
            List<ChoiceResponse> choices
    ) {
        public static QuestionResponse from(Question question) {
            return new QuestionResponse(
                    question.getId(),
                    question.getContent(),
                    question.getDisplayOrder(),
                    question.getChoices().stream()
                            .map(ChoiceResponse::from)
                            .toList()
            );
        }
    }

    public record ChoiceResponse(
            Long choiceId,
            String content,
            Integer score,
            Integer displayOrder
    ) {
        public static ChoiceResponse from(Choice choice) {
            return new ChoiceResponse(
                    choice.getId(),
                    choice.getContent(),
                    choice.getScore(),
                    choice.getDisplayOrder()
            );
        }
    }
}
