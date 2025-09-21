package com.dinedigital.controller;

import com.dinedigital.dao.ReservationDao;
import com.dinedigital.dao.MenuDao;
import com.dinedigital.model.Reservation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import com.dinedigital.util.ConfirmationCodeGenerator;
import com.dinedigital.dao.OrderDao;

@Controller
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationDao reservationDao;
    private final OrderDao orderDao;
    private final MenuDao menuDao;

    public ReservationController(ReservationDao reservationDao, OrderDao orderDao, MenuDao menuDao) {
        this.reservationDao = reservationDao;
        this.orderDao = orderDao;
        this.menuDao = menuDao;
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("items", menuDao.findAll());
        return "reservation";
    }

    @PostMapping("/confirm")
    public String confirm(@RequestParam String name,
                          @RequestParam String email,
                          @RequestParam String date,
                          @RequestParam String time,
                          @RequestParam int guests,
                          @RequestParam(required = false, name = "preorderNames") String[] preorderNames,
                          @RequestParam(required = false, name = "preorderQtys") Integer[] preorderQtys,
                          @RequestParam(required = false, name = "preorderPrices") java.math.BigDecimal[] preorderPrices,
                          Model model) {
    // housekeeping: delete past reservations
    reservationDao.deleteOlderThanToday();

        Reservation r = new Reservation();
        r.setName(name);
        r.setEmail(email);
        r.setDate(LocalDate.parse(date));
        r.setTime(LocalTime.parse(time));
        r.setGuests(guests);
    String code = ConfirmationCodeGenerator.generate(8);
    r.setConfirmationCode(code);
        reservationDao.insert(r);

        // If preorder arrays exist and have items, create an order linked to this reservation
        if (preorderNames != null && preorderQtys != null && preorderPrices != null &&
            preorderNames.length == preorderQtys.length && preorderQtys.length == preorderPrices.length) {
            long orderId = orderDao.createOrder(null, r.getId().intValue());
            for (int i = 0; i < preorderNames.length; i++) {
                if (preorderNames[i] == null || preorderNames[i].isBlank()) continue;
                int qty = preorderQtys[i] != null ? preorderQtys[i] : 1;
                java.math.BigDecimal price = preorderPrices[i] != null ? preorderPrices[i] : java.math.BigDecimal.ZERO;
                orderDao.addItem(orderId, preorderNames[i], qty, price);
            }
        }
        model.addAttribute("name", name);
        model.addAttribute("email", email);
    model.addAttribute("code", code);
        return "confirmation";
    }
}
