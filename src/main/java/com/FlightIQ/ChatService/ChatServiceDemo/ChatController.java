package com.FlightIQ.ChatService.ChatServiceDemo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/chat")
public class ChatController {

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("You are an AI assistant who's an expert in translating FAA NOTAMS into English. Pay extra attention to the dates please!")
                .build();
    }

    private final ChatClient chatClient;

    
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    
    @GetMapping("/generate")
    public ResponseEntity<String> generate(@RequestParam(value = "message") String message) {
        try {
            String response = chatClient.prompt()
                    .user(message)
                    .call()
                    .content()
                    .toString();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
    
}
