package kamaz.project.sandbox.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TelegramLoggingService {
    
    @Value("${telegram.bot.token:}")
    private String botToken;
    
    @Value("${telegram.chat.id:}")
    private String chatId;
    
    @Value("${telegram.enabled:false}")
    private boolean telegramEnabled;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @PostConstruct
    public void init() {
        if (telegramEnabled && !botToken.isEmpty() && !chatId.isEmpty()) {
            log.info("Telegram логирование включено");
            sendMessage(" Система запущена! Логирование в Telegram активировано");
        } else {
            log.info(" Telegram логирование отключено (telegram.enabled=false)");
        }
    }
    
    public void sendMessage(String message) {
        if (!telegramEnabled || botToken.isEmpty() || chatId.isEmpty()) {
            return;
        }
        
        try {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String jsonBody = String.format(
                "{\"chat_id\":\"%s\", \"text\":\"%s\"}",
                chatId, message
            );
            
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            String response = restTemplate.postForObject(url, entity, String.class);
            
            JsonNode jsonNode = objectMapper.readTree(response);
            if (jsonNode.has("ok") && jsonNode.get("ok").asBoolean()) {
                log.debug(" Сообщение отправлено в Telegram");
            } else {
                log.warn("Ошибка отправки в Telegram: {}", response);
            }
            
        } catch (Exception e) {
            log.error(" Не удалось отправить сообщение в Telegram: {}", e.getMessage());
        }
    }
    
    public void logCrudOperation(String operation, String entityType, String entityId, String username) {
        String message = String.format(
            " CRUD операция\n" +
            "Действие: %s\n" +
            "Сущность: %s\n" +
            "ID: %s\n" +
            "Пользователь: %s\n" +
            "Время: %s",
            operation, entityType, entityId, username, 
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
        );
        sendMessage(message);
    }
    
public void logAnomaly(String entityType, String entityId, String anomalyDescription, String breakReason) {
    String message = String.format(
        "АНОМАЛИЯ\nСущность: %s\nID: %s\nПричина: %s\nОписание: %s\nТребуется вмешательство!",
        entityType, entityId, breakReason, anomalyDescription
    );
    sendMessage(message);
}
}