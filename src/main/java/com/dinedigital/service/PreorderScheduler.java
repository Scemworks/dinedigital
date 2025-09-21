package com.dinedigital.service;

import com.dinedigital.dao.OrderDao;
import com.dinedigital.dao.ReservationDao;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PreorderScheduler {
    private final OrderDao orderDao;
    private final ReservationDao reservationDao;

    public PreorderScheduler(OrderDao orderDao, ReservationDao reservationDao) {
        this.orderDao = orderDao;
        this.reservationDao = reservationDao;
    }

    // Run every 10 minutes, promote PREORDERs whose reservation is within the next hour
    @Scheduled(fixedDelay = 600_000)
    public void promotePreorders() {
        var reservations = reservationDao.listAll();
        LocalDateTime now = LocalDateTime.now();
        for (var r : reservations) {
            if (r.getId() == null) continue;
            if (r.getDate() == null || r.getTime() == null) continue;
            LocalDateTime resAt = LocalDateTime.of(r.getDate(), r.getTime());
            if (!r.isCheckedIn() && resAt.isAfter(now) && resAt.isBefore(now.plusHours(1))) {
                // Find PREORDERs for this reservation and set them to NEW
                List<Long> ids = orderDao.findOrderIdsByReservationAndStatus(r.getId(), "PREORDER");
                for (Long id : ids) {
                    orderDao.setStatus(id, "NEW");
                }
            }
        }
    }
}
