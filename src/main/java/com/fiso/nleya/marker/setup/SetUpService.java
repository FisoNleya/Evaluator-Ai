package com.fiso.nleya.marker.setup;


import com.fiso.nleya.marker.shared.exceptions.InvalidRequestException;
import com.fiso.nleya.marker.storage.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.bc.ObjectStore;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SetUpService {

    private final ResourceLoader resourceLoader;
    private final TokenTextSplitter tokenTextSplitter;
    private final VectorStore vectorStore;
    private final MarkingSchemeService markingSchemeService;
    private final ObjectStorageService objectStorageService;


    MarkingScheme loadMarkingScheme(MultipartFile file) {

        String fileName = file.getName();

        var optionalMarkingScheme = markingSchemeService.findOptionalMsByFileName(fileName);
        if (optionalMarkingScheme.isPresent()) {
            return optionalMarkingScheme.get();
        }

        String text = readFile(file);

        String ids = "{fileName} : {msId} ".concat(System.lineSeparator());
        text = ids.concat(text);

        String msId = generateMsId();

        Document document = new Document(text, Map.of("fileName", fileName, "msId", msId));
        List<Document> splits = tokenTextSplitter.split(document);
        vectorStore.accept(splits);


        String objectName = objectStorageService.uploadFile(file, "walker");


        //Set ms such that its not saved again and again
        return markingSchemeService.save(MarkingScheme.builder()
                .id(msId)
                .fileName(fileName)
                .storageObjectName(objectName)
                .createdBy("walker@gmail.com")
                .build());
    }


    private static @NotNull String readFile(MultipartFile file) {
        String text;
        try {

            text = new BufferedReader(new InputStreamReader(file.getInputStream()))
                    .lines()
                    .collect(Collectors.joining(System.lineSeparator()));

        } catch (Exception e) {
            throw new InvalidRequestException("Failed to extract the given document content");
        }
        return text;
    }


    public String generateMsId() {
        return UUID.randomUUID().toString();
    }



}
