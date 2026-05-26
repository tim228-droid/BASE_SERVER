package kamaz.project.sandbox.controllers;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kamaz.project.sandbox.dto.WiperCreateDto;
import kamaz.project.sandbox.dto.WiperResponseDto;
import kamaz.project.sandbox.dto.WiperUpdateDto;
import kamaz.project.sandbox.services.impl.WiperServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Дворники", description = "CRUD операции для управления дворниками")
@RestController
@RequestMapping("/api/wipers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WiperController {

    private final WiperServiceImpl wiperService;

    private void logAction(String action, String details) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("[{}] {} -> {}", timestamp, username, action + " " + details);
    }


    @Operation(summary = "Получить все дворники", description = "Доступно всем авторизованным пользователям")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WiperResponseDto>> getAllWipers() {
        logAction("GET", "/api/wipers - просмотр всех дворников");
        return ResponseEntity.ok(wiperService.getAllActiveWipers());
    }



    @Operation(summary = "Создать дворника", description = "Только для ADMIN. С вероятностью 30% аномалия (длина вне диапазона 300–900)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WiperResponseDto> createWiper(@Valid @RequestBody WiperCreateDto createDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logAction("POST", "/api/wipers - создание дворника: " + createDto.brand() + " " + createDto.model());
        log.info("Пользователь {} создал дворника: {} {}", username, createDto.brand(), createDto.model());
        return ResponseEntity.ok(wiperService.createWiper(createDto, username));
    }



    @Operation(summary = "Обновить дворника", description = "Только для ADMIN")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WiperResponseDto> updateWiper(@PathVariable Long id, @Valid @RequestBody WiperUpdateDto updateDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logAction("PUT", "/api/wipers/" + id + " - обновление дворника");
        log.info("Пользователь {} обновил дворника ID: {}", username, id);
        return ResponseEntity.ok(wiperService.updateWiper(id, updateDto, username));
    }


    @Operation(summary = "Починить дворника (исправить аномалию)", description = "Только для ADMIN")
    @PostMapping("/{id}/repair")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WiperResponseDto> repairWiper(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logAction("POST", "/api/wipers/" + id + "/repair - починка дворника");
        log.info("Пользователь {} починил дворника ID: {}", username, id);
        return ResponseEntity.ok(wiperService.repairWiper(id, username));
    }


    @Operation(summary = "Удалить дворника", description = "Только для ADMIN")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteWiper(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logAction("DELETE", "/api/wipers/" + id + " - удаление дворника");
        log.info("Пользователь {} удалил дворника ID: {}", username, id);
        wiperService.deleteWiper(id, username);
        return ResponseEntity.ok("Дворник удалён");
    }


    @Operation(summary = "Экспорт всех дворников в Excel", description = "Только для ADMIN")
    @GetMapping("/export/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> exportToExcel() {
        logAction("GET", "/api/wipers/export/excel - экспорт в Excel");
        log.info("Экспорт всех дворников в Excel");
        List<WiperResponseDto> wipers = wiperService.getAllActiveWipers();
        ByteArrayInputStream in = wiperService.exportToExcel(wipers);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=wipers.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }

    @Operation(summary = "Сформировать отчёт", description = "Только для ADMIN")
    @GetMapping("/report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateReport() {
        logAction("GET", "/api/wipers/report - формирование отчета");
        log.info("Формирование отчёта по дворникам");
        ByteArrayInputStream in = wiperService.generateFullReport();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=wipers_report.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }


    @Operation(summary = "Импорт из Excel", description = "Только для ADMIN")
    @PostMapping(value = "/import/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WiperResponseDto>> importFromExcel(
            @Parameter(description = "Excel файл")
            @RequestParam("file") MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logAction("POST", "/api/wipers/import/excel - импорт из Excel, файл: " + file.getOriginalFilename());
        log.info("Пользователь {} импортирует файл: {}", username, file.getOriginalFilename());
        return ResponseEntity.ok(wiperService.importFromExcel(file, username));
    }
}