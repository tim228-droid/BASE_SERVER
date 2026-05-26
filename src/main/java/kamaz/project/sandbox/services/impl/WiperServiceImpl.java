package kamaz.project.sandbox.services.impl;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kamaz.project.sandbox.dto.WiperCreateDto;
import kamaz.project.sandbox.dto.WiperResponseDto;
import kamaz.project.sandbox.dto.WiperUpdateDto;
import kamaz.project.sandbox.exception.AppException;
import kamaz.project.sandbox.models.User;
import kamaz.project.sandbox.models.Wiper;
import kamaz.project.sandbox.repositories.UserRepository;
import kamaz.project.sandbox.repositories.WiperRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WiperServiceImpl {

    private final WiperRepository wiperRepository;
    private final UserRepository userRepository;
    private final AnomalyService anomalyService;
    private final ExcelService excelService;
    private final TelegramLoggingService telegramService;

    public List<WiperResponseDto> getAllActiveWipers() {
        return wiperRepository.findByIsActiveTrue()
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public WiperResponseDto createWiper(WiperCreateDto dto, String username) {
        User user = getUserByUsername(username);

        Wiper wiper = Wiper.builder()
                .brand(dto.brand())      
                .model(dto.model())        
                .length(dto.length())      
                .material(dto.material()) 
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .createdBy(user)
                .assignedTo(user)
                .anomaly(false)
                .build();

        anomalyService.applyAnomaly(wiper);

        Wiper saved = wiperRepository.save(wiper);

        if (saved.isAnomaly()) {
            telegramService.logAnomaly("Wiper", saved.getId().toString(),
                    saved.getAnomalyDescription(), "Аномалия границ");
        }

        return toResponseDto(saved);
    }

    @Transactional
    public WiperResponseDto updateWiper(Long id, WiperUpdateDto dto, String username) {
        User user = getUserByUsername(username);
        Wiper wiper = wiperRepository.findById(id).orElseThrow();

        if (dto.brand() != null) wiper.setBrand(dto.brand());
        if (dto.model() != null) wiper.setModel(dto.model());
        if (dto.length() != null) wiper.setLength(dto.length());
        if (dto.material() != null) wiper.setMaterial(dto.material());

        wiper.setUpdatedAt(LocalDateTime.now());
        wiper.setUpdatedBy(user);

        return toResponseDto(wiperRepository.save(wiper));
    }

    @Transactional
    public WiperResponseDto repairWiper(Long id, String username) {
        User user = getUserByUsername(username);
        Wiper wiper = wiperRepository.findById(id).orElseThrow();

        anomalyService.fixAnomaly(wiper);

        wiper.setUpdatedAt(LocalDateTime.now());
        wiper.setUpdatedBy(user);

        return toResponseDto(wiperRepository.save(wiper));
    }

    @Transactional
    public void deleteWiper(Long id, String username) {
        Wiper wiper = wiperRepository.findById(id).orElseThrow();
        wiper.setIsActive(false);
        wiper.setUpdatedAt(LocalDateTime.now());
        wiperRepository.save(wiper);
    }

    public ByteArrayInputStream exportToExcel(List<WiperResponseDto> wipers) {
        return excelService.exportWipersToExcel(wipers);
    }

    public ByteArrayInputStream generateFullReport() {
        List<WiperResponseDto> wipers = getAllActiveWipers();
        return excelService.generateReport(wipers, 0, 0, new HashMap<>());
    }

    @Transactional
    public List<WiperResponseDto> importFromExcel(MultipartFile file, String username) {
        User user = getUserByUsername(username);
        List<Wiper> imported = excelService.importWipersFromExcel(file);

        List<Wiper> saved = imported.stream()
                .map(w -> {
                    w.setCreatedAt(LocalDateTime.now());
                    w.setCreatedBy(user);
                    w.setAssignedTo(user);
                    w.setIsActive(true);
                    anomalyService.applyAnomaly(w);
                    return wiperRepository.save(w);
                })
                .collect(Collectors.toList());

        return saved.stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Пользователь не найден: " + username));
    }

    private WiperResponseDto toResponseDto(Wiper wiper) {
        return new WiperResponseDto(
                wiper.getId(),
                wiper.getBrand(),
                wiper.getModel(),
                wiper.getLength(),
                wiper.getMaterial(),
                wiper.isAnomaly(),
                wiper.getAnomalyDescription(),
                wiper.getIsActive(),
                wiper.getCreatedAt(),
                wiper.getUpdatedAt(),
                wiper.getCreatedBy() != null ? wiper.getCreatedBy().getUsername() : null,
                wiper.getUpdatedBy() != null ? wiper.getUpdatedBy().getUsername() : null,
                wiper.getAssignedTo() != null ? wiper.getAssignedTo().getUsername() : null
        );
    }
}