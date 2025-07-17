package com.example.caloriescanner.Service;

import com.example.caloriescanner.Response.OpenAIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Service
public class OpenAIService {

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String apiKey;
    public OpenAIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
    }
    private String processResponse(String input) {
        return input.replaceAll(" - ", "\n• ");
    }
    public String analyzeImage(byte[] imageBytes) {
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        String requestBody = """
        {
          "model": "gpt-4o",
          "messages": [
            {
              "role": "user",
              "content": [
                {
                  "type": "text",
                  "text": "Analyze this food nutrition label and summarize its health impact in plain English. Mention calories, sugar, fat, and protein. Give output based on three goals. If my goal is to lose weight then should I consume it ? If I goal is to maintain weight then should I consume it ? If my goal is to gain weight then should I consume it. Keep the summary short and precise. I don't want you to give me recommendations. Give me straight answer like should I consume it or not."
                },
                {
                  "type": "image_url",
                  "image_url": {
                    "url": "data:image/jpeg;base64,%s"
                  }
                }
              ]
            }
          ],
          "max_tokens": 300
        }
        """.formatted(base64Image);

        ObjectMapper objectMapper = new ObjectMapper();

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    System.out.println("Response : "+response);
                    try {
                        OpenAIResponse aiResponse = objectMapper.readValue(response,OpenAIResponse.class);
                        String answer = "";
                        answer = aiResponse.choices.get(0).message.content;
                        return processResponse(answer);
                    } catch (Exception e) {
                        return "Could not parse response from OpenAI.";
                    }
                })
                .block();
    }
}
