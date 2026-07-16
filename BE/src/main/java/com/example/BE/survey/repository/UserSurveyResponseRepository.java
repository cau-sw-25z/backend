package com.example.BE.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.BE.survey.entity.UserSurveyResponse;

public interface UserSurveyResponseRepository extends JpaRepository<UserSurveyResponse, Long> {
}