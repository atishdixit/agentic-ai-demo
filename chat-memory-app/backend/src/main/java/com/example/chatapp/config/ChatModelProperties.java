package com.example.chatapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** app.chat.* - sourced from .env, keeps the portable ChatOptions builder's inputs in one place. */
@Component
@ConfigurationProperties(prefix = "app.chat")
public class ChatModelProperties {

    private String model = "llama3.2:3b";
    private Double temperature = 0.6;
    private Integer maxTokens = 300;
    private Integer memoryWindow = 10;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Integer getMemoryWindow() {
        return memoryWindow;
    }

    public void setMemoryWindow(Integer memoryWindow) {
        this.memoryWindow = memoryWindow;
    }
}
