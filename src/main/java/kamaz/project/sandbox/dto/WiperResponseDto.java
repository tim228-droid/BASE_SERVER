package kamaz.project.sandbox.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record WiperResponseDto(
    @Schema(description = "ID дворника", example = "1")
    Long id,
    
    @Schema(description = "Бренд", example = "Bosch")
    String brand,
    
    @Schema(description = "Модель", example = "AeroTwin")
    String model,
    
    @Schema(description = "Длина (мм)", example = "650")
    Integer length,
    
    @Schema(description = "Материал", example = "Silicone")
    String material,
    
    @Schema(description = "Аномалия (длина вне диапазона 300–900)", example = "true")
    boolean anomaly,
    
    @Schema(description = "Описание аномалии", example = "Длина 1200 выходит за пределы нормы")
    String anomalyDescription,
    
    @Schema(description = "Активен", example = "true")
    Boolean isActive,
    
    @Schema(description = "Дата создания")
    LocalDateTime createdAt,
    
    @Schema(description = "Дата обновления")
    LocalDateTime updatedAt,
    
    @Schema(description = "Кем создан")
    String createdByUsername,
    
    @Schema(description = "Кем обновлён")
    String updatedByUsername,
    
    @Schema(description = "Закреплён за")
    String assignedToUsername
) {}