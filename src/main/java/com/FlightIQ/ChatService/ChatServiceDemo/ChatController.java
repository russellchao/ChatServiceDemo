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
                .defaultSystem("You are an AI aviation assistant that helps pilots plan flights. you will be responsible for translating NOTAMS to English as well as summarizing metar reports and forecasts to give pilots a safety outlook.")
                .build();
    }


    private final ChatClient chatClient;

    
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    
    @GetMapping("/translatenotam")
    public ResponseEntity<String> translateNotam(@RequestParam(value = "NOTAM") String NOTAM) {

        String promptStr = "You are an expert at translating NOTAMS. Please be as concise as you can given the NOTAM, say 3 lines max, and also please be extra cautious of dates.";
        String fullPrompt = promptStr + NOTAM; 

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


    @GetMapping("/ratemetarsafety")
    public ResponseEntity<String> RateMETARSafety(@RequestParam(value = "ICAO") String ICAO) {

        String apiUrl = "https://wx-svc-x86-272565453292.us-central1.run.app/api/v1/getAirportWeather?airportCode={ICAO}";
        String endpoint = apiUrl.replace("{ICAO}", ICAO);
        RestTemplate restTemplate = new RestTemplate(); 
        String apiResponseJSON = restTemplate.getForObject(endpoint, String.class);
        String promptStr = "You are an expert single engine plane pilot. rate this METAR on a saftey scale of 1-10 with 10 being the safest to fly. be concise. Also, if there's hazardous conditions like thunderstorms, lower the rating significantly.";
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

    
