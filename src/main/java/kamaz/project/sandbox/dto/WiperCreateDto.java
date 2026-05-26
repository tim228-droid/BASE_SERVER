package kamaz.project.sandbox.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record WiperCreateDto(
    @Schema(description = "Бренд", example = "Bosch")
    String brand,
    
    @Schema(description = "Модель", example = "AeroTwin")
    String model,
    
    @Schema(description = "Длина (норма 300–900)", example = "650")
    Integer length,
    
    @Schema(description = "Материал", example = "Silicone")
    String material
) {}