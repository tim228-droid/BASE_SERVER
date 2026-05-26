package kamaz.project.sandbox.services.impl;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Service;

import kamaz.project.sandbox.models.Wiper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AnomalyService {

    private final Random random = new SecureRandom();
    private static final int MIN_NORMAL = 300;
    private static final int MAX_NORMAL = 900;
    private static final double ANOMALY_CHANCE = 0.3;

    // НОВАЯ АНОМАЛИЯ (выход за границы длины)
    public void applyAnomaly(Wiper wiper) {
        boolean isAnomaly = random.nextDouble() < ANOMALY_CHANCE;

        if (isAnomaly) {
            int abnormal = random.nextBoolean()
                ? random.nextInt(MIN_NORMAL)
                : MAX_NORMAL + 1 + random.nextInt(500);
            
            wiper.setLength(abnormal);
            wiper.setAnomaly(true);
            wiper.setAnomalyDescription("Длина " + abnormal + " мм выходит за пределы нормы (300–900)");
            
            log.warn("Аномалия: длина {} выходит за пределы 300–900", abnormal);
        } else {
            wiper.setAnomaly(false);
            wiper.setAnomalyDescription(null);
        }
    }

    public void fixAnomaly(Wiper wiper) {
        if (wiper.isAnomaly()) {
            wiper.setLength(650);
            wiper.setAnomaly(false);
            wiper.setAnomalyDescription(null);
            log.info("Аномалия исправлена: длина установлена в 650 мм");
        }
    }

    public void applyAnomalyIfNeeded(Wiper wiper) {
        applyAnomaly(wiper);
    }

    public void repairWiper(Wiper wiper) {
        fixAnomaly(wiper);
    }
}