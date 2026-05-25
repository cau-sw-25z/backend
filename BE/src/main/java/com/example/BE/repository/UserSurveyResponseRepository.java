package com.example.BE.repository;

import com.example.BE.entity.UserSurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSurveyResponseRepository extends JpaRepository<UserSurveyResponse, Long> {
}