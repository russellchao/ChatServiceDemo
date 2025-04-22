package com.FlightIQ.ChatService.ChatServiceDemo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


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


    @GetMapping("/ratemetarsafety")
    public ResponseEntity<String> RateMETARSafety(@RequestParam(value = "ICAO") String ICAO) {

        String apiUrl = "https://wx-svc-x86-272565453292.us-central1.run.app/api/v1/getAirportWeather?airportCode={ICAO}";
        String endpoint = apiUrl.replace("{ICAO}", ICAO);
        RestTemplate restTemplate = new RestTemplate(); 
        String apiResponseJSON = restTemplate.getForObject(endpoint, String.class);
        String promptStr = "You are an expert single engine plane pilot. rate this METAR on a saftey scale of 1-10 with 10 being the safest to fly. be concise.";
        String fullPrompt = promptStr + apiResponseJSON;
       

        try {
            String response = chatClient.prompt()
                    .user(fullPrompt)
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

    
