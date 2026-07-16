package com.example.BE.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.BE.survey.entity.Choice;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
}
