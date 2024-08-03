package com.fiso.nleya.marker.testing;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ask")
public class AskController {


    private final ChatClient aiClient;


    public AskController(ChatClient.Builder chatClientBuilder) {
        this.aiClient = chatClientBuilder.build();
    }




    @GetMapping("/ai")
    String generation(String userInput) {

        return this.aiClient.prompt()
                .user(userInput)
                .call()
                .content();
    }

//    @GetMapping("/load-store")
//    String loadVectorStore() {
//
//
//        String question1 = """
//        Question 1
//        When is Fiso available in the office?
//
//        Answer 1
//        Fiso is available on Saturday or Sunday.
//                """;
//
//        String question2 = """
//       Question 2
//        Name any one entity included in African seven colors meal?
//
//        Answer 2
//        African seven colors meal include rice, beef, green salad, potatoes and apples.
//                """;
//
//        var documents = List.of(
//                new Document(question1, Map.of("questionId", 1)),
//                new Document(question2, Map.of("questionId", 2))
//        );
//
//
//        List<Document> splits = tokenTextSplitter.apply(documents);
//        vectorStore.accept(splits);
//
//        System.out.println("Successfully loaded vector store");
//        return "Yey!!";
//    }


}
