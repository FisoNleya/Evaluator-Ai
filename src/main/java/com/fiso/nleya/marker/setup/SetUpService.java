package com.fiso.nleya.marker.setup;


import com.fiso.nleya.marker.auth.user.User;
import com.fiso.nleya.marker.setup.ms.MarkingScheme;
import com.fiso.nleya.marker.setup.ms.MarkingSchemeService;
import com.fiso.nleya.marker.shared.exceptions.InvalidRequestException;
import com.fiso.nleya.marker.storage.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SetUpService {


    private final TokenTextSplitter tokenTextSplitter;
    private final VectorStore vectorStore;
    private final MarkingSchemeService markingSchemeService;
    private final ObjectStorageService objectStorageService;


    MarkingScheme loadMarkingScheme(MultipartFile file, String username) {

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


        String objectName = objectStorageService.uploadFile(file, username);


        //Set ms such that its not saved again and again
        return markingSchemeService.save(MarkingScheme.builder()
                .id(msId)
                .fileName(fileName)
                .storageObjectName(objectName)
                .build());
    }





    static @NotNull String readFile(MultipartFile file) {
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


    String generateMsId() {
        return "MS-"+UUID.randomUUID().toString();
    }


}
