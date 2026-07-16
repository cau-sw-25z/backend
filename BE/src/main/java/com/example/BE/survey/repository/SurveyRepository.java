package com.example.BE.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.BE.survey.entity.Survey;

import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    Optional<Survey> findByActiveTrue();
}
