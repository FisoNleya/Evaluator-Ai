package com.fiso.nleya.marker.evaluation;

import com.fiso.nleya.marker.evaluation.results.Results;
import lombok.extern.slf4j.Slf4j;
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

    @Value("classpath:/rag-prompt-template.st")
    private Resource ragPromptTemplate;


    public EvaluationService(ChatClient.Builder chatClientBuilder,
                             VectorStore vectorStore) {
        this.aiClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;

    }



    Results evaluate(Answers questionAnswers){

        StringBuilder inputString =  new StringBuilder();

        questionAnswers.answers().forEach(answer -> {
            String val = String.format(" Question: %d%n Answer: %s", answer.question(), answer.answer());
            inputString.append(val);

        });

        String input = inputString.toString();
        log.info("INPUT: {}", input);


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
                        .param("documents", String.join("\n", contentList)))
                .call()
                .content();

        return outputParser.convert(answer);

    }

}
