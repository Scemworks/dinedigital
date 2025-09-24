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
import java.time.temporal.ChronoUnit;
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

        // Server-side validation: prevent past dates
        LocalDate chosenDate;
        try {
            chosenDate = LocalDate.parse(date);
        } catch (Exception ex) {
            model.addAttribute("error", "Invalid date. Please choose a valid reservation date.");
            model.addAttribute("items", menuDao.findAll());
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("date", date);
            model.addAttribute("time", time);
            model.addAttribute("guests", guests);
            return "reservation";
        }
        if (chosenDate.isBefore(LocalDate.now())) {
            model.addAttribute("error", "Please choose today or a future date for your reservation.");
            model.addAttribute("items", menuDao.findAll());
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("date", date);
            model.addAttribute("time", time);
            model.addAttribute("guests", guests);
            return "reservation";
        }

        // Max booking window: 30 days ahead
        LocalDate maxAllowedDate = LocalDate.now().plusDays(30);
        if (chosenDate.isAfter(maxAllowedDate)) {
            model.addAttribute("error", "Please choose a date within the next 30 days.");
            model.addAttribute("items", menuDao.findAll());
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("date", date);
            model.addAttribute("time", time);
            model.addAttribute("guests", guests);
            return "reservation";
        }

        // Parse and validate time; for same-day reservations ensure time is not in the past
        LocalTime chosenTime;
        try {
            chosenTime = LocalTime.parse(time);
        } catch (Exception ex) {
            model.addAttribute("error", "Invalid time. Please choose a valid reservation time.");
            model.addAttribute("items", menuDao.findAll());
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("date", date);
            model.addAttribute("time", time);
            model.addAttribute("guests", guests);
            return "reservation";
        }
        // Latest time cutoff: no bookings after 22:00 (10 PM)
        LocalTime lastBookableTime = LocalTime.of(22, 0);
        if (chosenTime.isAfter(lastBookableTime)) {
            model.addAttribute("error", "Please choose a time no later than 10:00 PM.");
            model.addAttribute("items", menuDao.findAll());
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("date", date);
            model.addAttribute("time", time);
            model.addAttribute("guests", guests);
            return "reservation";
        }

        if (chosenDate.isEqual(LocalDate.now())) {
            LocalTime minAllowed = LocalTime.now().plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MINUTES);
            if (chosenTime.isBefore(minAllowed)) {
                model.addAttribute("error", "Please choose a time at least 1 hour from now.");
                model.addAttribute("items", menuDao.findAll());
                model.addAttribute("name", name);
                model.addAttribute("email", email);
                model.addAttribute("date", date);
                model.addAttribute("time", time);
                model.addAttribute("guests", guests);
                return "reservation";
            }
            // If no slots remain today
            if (minAllowed.isAfter(lastBookableTime)) {
                model.addAttribute("error", "No available times remain today. Please choose a future date.");
                model.addAttribute("items", menuDao.findAll());
                model.addAttribute("name", name);
                model.addAttribute("email", email);
                model.addAttribute("date", date);
                model.addAttribute("time", time);
                model.addAttribute("guests", guests);
                return "reservation";
            }
        }

        Reservation r = new Reservation();
        r.setName(name);
        r.setEmail(email);
        r.setDate(chosenDate);
        r.setTime(chosenTime);
        r.setGuests(guests);
    String code = ConfirmationCodeGenerator.generate(8);
    r.setConfirmationCode(code);
    reservationDao.insert(r);
    // Always expose reservation basics to the confirmation view
    model.addAttribute("guests", r.getGuests());
    model.addAttribute("reservationDate", r.getDate());
    model.addAttribute("reservationTime", r.getTime());

        // If preorder arrays exist and have items, create an order linked to this reservation
        if (preorderNames != null && preorderQtys != null && preorderPrices != null &&
            preorderNames.length == preorderQtys.length && preorderQtys.length == preorderPrices.length) {
            long orderId = orderDao.createOrder(null, r.getId().intValue());
            model.addAttribute("orderId", orderId);
            for (int i = 0; i < preorderNames.length; i++) {
                if (preorderNames[i] == null || preorderNames[i].isBlank()) continue;
                int qty = preorderQtys[i] != null ? preorderQtys[i] : 1;
                java.math.BigDecimal price = preorderPrices[i] != null ? preorderPrices[i] : java.math.BigDecimal.ZERO;
                orderDao.addItem(orderId, preorderNames[i], qty, price);
            }
            // Mark as PREORDER so it does not show in active kitchen/billing until check-in or scheduled send
            orderDao.setStatus(orderId, "PREORDER");
            // compute preorder total and add reservation fee
            var items = orderDao.findOrderItems(orderId);
            java.math.BigDecimal subtotal = items.stream()
                    .map(it -> ((java.math.BigDecimal) it.get("price")).multiply(new java.math.BigDecimal(((Number)it.get("quantity")).intValue())))
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            java.math.BigDecimal fee = new java.math.BigDecimal("50.00");
            java.math.BigDecimal total = subtotal.add(fee);
            model.addAttribute("preorderSubtotal", subtotal);
            model.addAttribute("reservationFee", fee);
            model.addAttribute("preorderTotal", total);
            model.addAttribute("preorderItems", items);
            model.addAttribute("guests", r.getGuests());
            // fetch order record to get daily order_number if available
            var orderOpt = orderDao.findOrder(orderId);
            if (orderOpt.isPresent()) {
                var orderMap = orderOpt.get();
                model.addAttribute("orderNumber", orderMap.get("order_id"));
            }
        }
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("code", code);
        return "confirmation";
    }
}
