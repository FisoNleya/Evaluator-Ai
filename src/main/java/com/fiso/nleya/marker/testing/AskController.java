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


}
