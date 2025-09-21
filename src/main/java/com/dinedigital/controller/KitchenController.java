package com.dinedigital.controller;

import com.dinedigital.dao.OrderDao;
import com.dinedigital.model.KitchenOrder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/kitchen")
public class KitchenController {
    private final OrderDao orderDao;
    public KitchenController(OrderDao orderDao) { this.orderDao = orderDao; }

    @GetMapping
    public String dashboard(Model model) {
        List<Map<String, Object>> rows = orderDao.listPendingOrders();
        List<KitchenOrder> orders = rows.stream().map(r -> {
            KitchenOrder ko = new KitchenOrder();
            Number realIdNum = (Number) r.get("real_id");
            ko.realId = realIdNum != null ? realIdNum.longValue() : -1L;
            Number idNum = (Number) r.get("order_id");
            ko.orderId = idNum != null ? idNum.longValue() : -1L;
            Number tn = (Number) r.get("table_number");
            ko.tableNumber = tn != null ? tn.intValue() : null;
            Number rn = (Number) r.get("reservation_id");
            ko.reservationId = rn != null ? rn.intValue() : null;
            Object ts = r.get("created_at");
            if (ts instanceof java.sql.Timestamp ts0) {
                ko.createdAt = ts0.toLocalDateTime();
            } else if (ts instanceof java.time.LocalDateTime ldt) {
                ko.createdAt = ldt;
            } else if (ts instanceof java.util.Date d) {
                ko.createdAt = d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            } else if (ts instanceof CharSequence cs) {
                ko.createdAt = java.time.LocalDateTime.parse(cs.toString().replace(' ', 'T'));
            } else {
                ko.createdAt = java.time.LocalDateTime.now();
            }
            ko.items = orderDao.listItems(ko.orderId).stream().map(i -> new KitchenOrder.Item(
                    ((Number) i.get("id")).longValue(),
                    (String) i.get("name"),
                    ((Number) i.get("quantity")).intValue(),
                    (java.math.BigDecimal) i.get("price")
            )).collect(Collectors.toList());
            return ko;
        }).collect(Collectors.toList());
        model.addAttribute("orders", orders);
        return "kitchen";
    }

    @PostMapping("/complete")
    public String complete(@RequestParam("orderId") long orderId) {
        orderDao.complete(orderId);
        return "redirect:/kitchen";
    }
}
