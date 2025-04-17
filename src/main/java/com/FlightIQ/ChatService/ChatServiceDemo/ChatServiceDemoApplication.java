package com.FlightIQ.ChatService.ChatServiceDemo;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;



@SpringBootApplication
public class ChatServiceDemoApplication implements CommandLineRunner {

	@Autowired
	OllamaChatModel chatModel;


	@Bean
	OllamaChatModel ollamaChatModel(@Value("spring.ai.ollama.base-url") String baseUrl) {
		return new OllamaChatModel(new OllamaApi(baseUrl),
			OllamaOptions.create()
			.withModel("gemma")
			.withTemperature(0.4f));
	}


	public static void main(String[] args) {
		SpringApplication.run(ChatServiceDemoApplication.class, args);
	}



	@Override
	public void run(String... args) throws Exception {

		chatModel.stream(new Prompt(
			"Generate the names of 5 famous pirates.",
			OllamaOptions.create()
			.withModel("llama3")
			.withTemperature(0.4F)
		)).subscribe(chatResponse -> {
			System.out.print(chatResponse.getResult().getOutput().getContent());
		});

	}

}
