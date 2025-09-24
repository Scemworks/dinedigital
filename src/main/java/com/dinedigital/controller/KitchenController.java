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
            try {
            KitchenOrder ko = new KitchenOrder();
            Object realIdObj = r.get("real_id");
            ko.realId = (realIdObj instanceof Number) ? ((Number) realIdObj).longValue() : -1L;
            Object idObj = r.get("order_id");
            ko.orderId = (idObj instanceof Number) ? ((Number) idObj).longValue() : -1L;
            Object tnObj = r.get("table_number");
            ko.tableNumber = (tnObj instanceof Number) ? ((Number) tnObj).intValue() : null;
            Object rnObj = r.get("reservation_id");
            ko.reservationId = (rnObj instanceof Number) ? ((Number) rnObj).intValue() : null;
            Object ts = r.get("created_at");
            if (ts instanceof java.sql.Timestamp ts0) {
                ko.createdAt = new java.util.Date(ts0.getTime());
            } else if (ts instanceof java.time.LocalDateTime ldt) {
                ko.createdAt = java.util.Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
            } else if (ts instanceof java.util.Date d) {
                ko.createdAt = d;
            } else if (ts instanceof CharSequence cs) {
                try {
                    java.time.LocalDateTime parsed = java.time.LocalDateTime.parse(cs.toString().replace(' ', 'T'));
                    ko.createdAt = java.util.Date.from(parsed.atZone(java.time.ZoneId.systemDefault()).toInstant());
                } catch (Exception e) {
                    ko.createdAt = new java.util.Date();
                }
            } else {
                ko.createdAt = new java.util.Date();
            }
            try {
                ko.createdAtStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(ko.createdAt);
            } catch (Exception ignored) { ko.createdAtStr = ""; }
            // Use realId (primary key) to fetch items, not display order number
            ko.items = orderDao.listItems(ko.realId).stream().map(i -> {
                Object itemIdObj = i.get("id");
                Object nameObj = i.get("name");
                Object qtyObj = i.get("quantity");
                Object priceObj = i.get("price");
                if (!(itemIdObj instanceof Number) || !(nameObj instanceof String) || !(qtyObj instanceof Number) || !(priceObj instanceof java.math.BigDecimal)) {
                    return null;
                }
                return new KitchenOrder.Item(
                        ((Number) itemIdObj).longValue(),
                        (String) nameObj,
                        ((Number) qtyObj).intValue(),
                        (java.math.BigDecimal) priceObj
                );
            }).filter(item -> item != null).collect(Collectors.toList());
            return ko;
            } catch (Exception e) { return null; }
        }).collect(Collectors.toList());
        orders = orders.stream().filter(o -> o != null).collect(Collectors.toList());
        model.addAttribute("orders", orders);
        model.addAttribute("hasOrders", !orders.isEmpty());
        return "kitchen";
    }

    @PostMapping("/complete")
    public String complete(@RequestParam("orderId") long orderId) {
        // In kitchen, marking as served should not mark as paid. Only update status.
        try { orderDao.setStatus(orderId, "SERVED"); } catch (Exception e) { }
        return "redirect:/kitchen";
    }
}
