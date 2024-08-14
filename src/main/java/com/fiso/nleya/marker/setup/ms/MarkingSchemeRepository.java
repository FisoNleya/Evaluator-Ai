package com.fiso.nleya.marker.setup.ms;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarkingSchemeRepository extends JpaRepository<MarkingScheme, String> {

    Optional<MarkingScheme> findByFileName(String markingSchemeName);

    List<MarkingScheme> findByCreatedBy(String email);

}