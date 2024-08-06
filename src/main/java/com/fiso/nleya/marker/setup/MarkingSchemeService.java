package com.fiso.nleya.marker.setup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkingSchemeService {


    private final MarkingSchemeRepository markingSchemeRepository;


    MarkingScheme save(MarkingScheme markingScheme) {
        var optionalMarkingScheme = findOptionalMsByFileName(markingScheme.getFileName());
        return optionalMarkingScheme.orElseGet(() -> markingSchemeRepository.save(markingScheme));
    }

    Optional<MarkingScheme> findOptionalMsByFileName(String fileName) {
        return markingSchemeRepository.findByFileName(fileName);
    }


    public MarkingScheme findById(String  msId) {
        return markingSchemeRepository.findById(msId)
                .orElseThrow(()-> new RuntimeException("MarkingScheme not found with id: " + msId));
    }


}
