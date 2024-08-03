package com.fiso.nleya.marker.setup;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SetUpService {

    private final ResourceLoader resourceLoader;
    private final TokenTextSplitter tokenTextSplitter;
    private final VectorStore vectorStore;
    private final MarkingSchemeService markingSchemeService;



    MarkingScheme loadMarkingScheme(String fileName){

        var optionalMarkingScheme = markingSchemeService.findOptionalMsByFileName(fileName);
        if(optionalMarkingScheme.isPresent()){
            return optionalMarkingScheme.get();
        }


        String text = "";
        try {
            Resource resource = resourceLoader.getResource("classpath:./ms/"+ fileName);

         text = new BufferedReader(new InputStreamReader(resource.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"));

        }catch (Exception e){
            throw new RuntimeException("Failed to load the given document",e);
            //change to custom exceptions
        }


        Document document = new Document(text);
        List<Document> splits = tokenTextSplitter.split(document);
        vectorStore.accept(splits);

        //Set ms such that its not saved again and again
        return  markingSchemeService.save(MarkingScheme.builder()
                        .fileName(fileName)
                        .createdBy("walker@gmail.com")
                .build());
    }

}
