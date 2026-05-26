package kamaz.project.sandbox.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kamaz.project.sandbox.dto.WiperResponseDto;
import kamaz.project.sandbox.models.Wiper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExcelService {
    
    public ByteArrayInputStream exportWipersToExcel(List<WiperResponseDto> wipers) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Дворники");
            
            // Стиль для заголовков
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // Заголовки
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Бренд", "Модель", "Длина (мм)", "Материал", 
                "Аномалия", "Описание аномалии", "Активен", "Создан", "Кем создан"};
            
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Данные
            int rowNum = 1;
            for (WiperResponseDto wiper : wipers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(wiper.id());
                row.createCell(1).setCellValue(wiper.brand());
                row.createCell(2).setCellValue(wiper.model());
                row.createCell(3).setCellValue(wiper.length());
                row.createCell(4).setCellValue(wiper.material());
                row.createCell(5).setCellValue(wiper.anomaly() ? "ДА" : "НЕТ");
                row.createCell(6).setCellValue(wiper.anomalyDescription() != null ? wiper.anomalyDescription() : "-");
                row.createCell(7).setCellValue(wiper.isActive() ? "ДА" : "НЕТ");
                row.createCell(8).setCellValue(formatDateTime(wiper.createdAt()));
                row.createCell(9).setCellValue(wiper.createdByUsername() != null ? wiper.createdByUsername() : "-");
            }
            
            // Автоширина
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            log.info("Экспортировано {} дворников в Excel", wipers.size());
            return new ByteArrayInputStream(out.toByteArray());
            
        } catch (IOException e) {
            log.error("Ошибка при экспорте в Excel: {}", e.getMessage());
            throw new RuntimeException("Ошибка при создании Excel файла", e);
        }
    }
    
    public List<Wiper> importWipersFromExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Wiper> wipers = new ArrayList<>();
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                // Пропускаем пустые строки
                if (row.getCell(0) == null || row.getCell(0).getStringCellValue().isEmpty()) {
                    continue;
                }
                
                Wiper wiper = Wiper.builder()
                    .brand(getCellValue(row.getCell(0)))
                    .model(getCellValue(row.getCell(1)))
                    .length((int) row.getCell(2).getNumericCellValue())
                    .material(getCellValue(row.getCell(3)))
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build();
                
                wipers.add(wiper);
            }
            
            log.info("Импортировано {} дворников из Excel", wipers.size());
            return wipers;
            
        } catch (IOException e) {
            log.error("Ошибка при импорте из Excel: {}", e.getMessage());
            throw new RuntimeException("Ошибка при чтении Excel файла", e);
        }
    }
    
    public ByteArrayInputStream generateReport(List<WiperResponseDto> wipers, long totalBroken, double brokenPercentage, Map<String, Long> breakReasons) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Лист с отчётом
            Sheet reportSheet = workbook.createSheet("Отчёт");
            
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // Заголовок отчёта
            Row titleRow = reportSheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("ОТЧЁТ ПО ДВОРНИКАМ");
            titleCell.setCellStyle(titleStyle);
            
            // Статистика
            Row statsRow1 = reportSheet.createRow(2);
            statsRow1.createCell(0).setCellValue("Всего дворников:");
            statsRow1.createCell(1).setCellValue(wipers.size());
            
            long anomalyCount = wipers.stream().filter(WiperResponseDto::anomaly).count();
            Row statsRow2 = reportSheet.createRow(3);
            statsRow2.createCell(0).setCellValue("Дворников с аномалией:");
            statsRow2.createCell(1).setCellValue(anomalyCount);
            
            Row statsRow3 = reportSheet.createRow(4);
            statsRow3.createCell(0).setCellValue("Процент аномалий:");
            double anomalyPercent = wipers.isEmpty() ? 0 : (anomalyCount * 100.0 / wipers.size());
            statsRow3.createCell(1).setCellValue(String.format("%.2f%%", anomalyPercent));
            
            // Второй лист - список дворников
            Sheet wipersSheet = workbook.createSheet("Список дворников");
            Row headerRow = wipersSheet.createRow(0);
            String[] columns = {"ID", "Бренд", "Модель", "Длина", "Материал", "Аномалия", "Описание"};
            
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            int rowNum = 1;
            for (WiperResponseDto wiper : wipers) {
                Row row = wipersSheet.createRow(rowNum++);
                row.createCell(0).setCellValue(wiper.id());
                row.createCell(1).setCellValue(wiper.brand());
                row.createCell(2).setCellValue(wiper.model());
                row.createCell(3).setCellValue(wiper.length());
                row.createCell(4).setCellValue(wiper.material());
                row.createCell(5).setCellValue(wiper.anomaly() ? "ДА" : "НЕТ");
                row.createCell(6).setCellValue(wiper.anomalyDescription() != null ? wiper.anomalyDescription() : "-");
            }
            
            for (int i = 0; i < columns.length; i++) {
                wipersSheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
            
        } catch (IOException e) {
            log.error("Ошибка при создании отчёта: {}", e.getMessage());
            throw new RuntimeException("Ошибка при создании отчёта", e);
        }
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        return style;
    }
    
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }
    
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "-";
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }
}