package com.fiso.nleya.marker.setup.assessment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    Optional<Assessment>  findByAssesees_EmailAndCode (String email, String code);

}