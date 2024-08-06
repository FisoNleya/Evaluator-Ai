package com.fiso.nleya.marker.setup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarkingSchemeRepository extends JpaRepository<MarkingScheme, String> {

    Optional<MarkingScheme> findByFileName(String markingSchemeName);

}