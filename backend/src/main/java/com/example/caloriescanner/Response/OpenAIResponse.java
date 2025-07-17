package com.example.caloriescanner.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIResponse {
    public List<Choice> choices;
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice{
        public Message message;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message{
        public String role;
        public String content;
    }
}
