package com.fiso.nleya.marker.evaluation;

import com.fiso.nleya.marker.evaluation.results.Results;
import com.fiso.nleya.marker.setup.assessment.Assessment;
import com.fiso.nleya.marker.setup.assessment.AssessmentService;
import com.fiso.nleya.marker.setup.ms.MarkingScheme;
import com.fiso.nleya.marker.setup.ms.MarkingSchemeService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EvaluationService {

    private final ChatClient aiClient;
    private final VectorStore vectorStore;
    private final MarkingSchemeService markingSchemeService;
    private final AssessmentService assessmentService;

    @Value("classpath:/rag-prompt-template.st")
    private Resource ragPromptTemplate;


    public EvaluationService(ChatClient.Builder chatClientBuilder,
                             VectorStore vectorStore,
                             MarkingSchemeService markingSchemeService,
                             AssessmentService assessmentService

    ) {
        this.aiClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
        this.markingSchemeService = markingSchemeService;
        this.assessmentService = assessmentService;

    }


    Results evaluate(Answers questionAnswers, String email, String code){


        Assessment assessment = assessmentService.findByAssessesAndCode(email, code);


        MarkingScheme ms = assessment.getMarkingScheme();
        String input = getQueryString(questionAnswers, ms);

        List<Document> similarDocuments = vectorStore
                .similaritySearch(SearchRequest.query(input)
                        .withTopK(2));
        List<String> contentList = similarDocuments.stream()
                .map(Document::getContent)
                .toList();


        var outputParser = new BeanOutputConverter<>(Results.class);
        String format = outputParser.getFormat();


        //Pass format
        String answer = aiClient.prompt()
                .user(spec -> spec
                        .text(ragPromptTemplate)
                        .param("input", input)
                        .param("format", format)
                        .param("documents", String.join(System.lineSeparator(), contentList)))
                .call()
                .content();

        return outputParser.convert(answer);

    }

    private static @NotNull String getQueryString(Answers questionAnswers, MarkingScheme ms) {

        StringBuilder inputString =  new StringBuilder();
        inputString.append(String.format("%s : %s", ms.getFileName(), ms.getId()))
                .append(System.lineSeparator());

        questionAnswers.answers().forEach(answer -> {
            String val = String.format(" Question: %d%n Student's Answer: %s", answer.question(), answer.answer());
            inputString.append(val).append(System.lineSeparator());
        });

        String input = inputString.toString();
        log.info("INPUT: {}", input);
        return input;
    }

}
