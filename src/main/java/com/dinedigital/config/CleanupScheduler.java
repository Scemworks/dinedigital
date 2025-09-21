package com.dinedigital.config;

import com.dinedigital.dao.ReservationDao;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class CleanupScheduler {
    private final ReservationDao reservationDao;
    public CleanupScheduler(ReservationDao reservationDao) { this.reservationDao = reservationDao; }

    // Run daily at 02:00
    @Scheduled(cron = "0 0 2 * * *")
    public void purgeOldReservations() {
        reservationDao.deleteOlderThanToday();
    }
}
