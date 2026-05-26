package kamaz.project.sandbox.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record WiperUpdateDto(
    @Schema(description = "Бренд дворника", example = "Bosch")
    String brand,
    
    @Schema(description = "Модель дворника", example = "AeroTwin")
    String model,
    
    @Schema(description = "Длина щётки в мм", example = "650")
    Integer length,
    
    @Schema(description = "Материал изготовления", example = "Cylindron")
    String material,
    
    @Schema(description = "Статус поломки", example = "false")
    Boolean isBroken,
    
    @Schema(description = "Причина поломки", example = "Сломан моторчик")
    String breakReason
) {}