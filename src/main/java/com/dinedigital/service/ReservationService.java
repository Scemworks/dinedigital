package com.dinedigital.service;

import com.dinedigital.model.Reservation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ReservationService {
    private final List<Reservation> reservations = new ArrayList<>();

    public Reservation save(Reservation r) {
        r.setId(System.currentTimeMillis());
        reservations.add(r);
        return r;
    }

    public List<Reservation> list() {
        return Collections.unmodifiableList(reservations);
    }
}
