package com.dinedigital.controller;

import com.dinedigital.dao.OrderDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderDao orderDao;
    public OrderController(OrderDao orderDao) { this.orderDao = orderDao; }

    @GetMapping("/place")
    public String placeGetRedirect() {
        return "redirect:/order";
    }

    @PostMapping("/place")
    public String place(@RequestParam(required = false) Integer tableNumber,
                        @RequestParam(name = "names", required = false) java.util.List<String> names,
                        @RequestParam(name = "qtys", required = false) java.util.List<Integer> qtys,
                        @RequestParam(name = "prices", required = false) java.util.List<java.math.BigDecimal> prices,
                        Model model) {
        if (names == null || qtys == null || prices == null) {
            return "redirect:/order?error=selectItems";
        }
        int n = java.util.stream.Stream.of(names.size(), qtys.size(), prices.size()).min(Integer::compareTo).orElse(0);
        java.util.List<Integer> idx = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            String nm = names.get(i);
            Integer q = qtys.get(i);
            if (nm != null && !nm.isBlank() && q != null && q > 0) {
                idx.add(i);
            }
        }
        if (idx.isEmpty()) {
            return "redirect:/order?error=noItems";
        }
        long orderId = orderDao.createOrder(tableNumber, 0);
        for (Integer i : idx) {
            String nm = names.get(i);
            Integer q = qtys.get(i);
            java.math.BigDecimal p = prices.get(i) != null ? prices.get(i) : java.math.BigDecimal.ZERO;
            orderDao.addItem(orderId, nm, q, p);
        }
        var ord = orderDao.findOrder(orderId).orElseThrow();
        model.addAttribute("orderNumber", ord.get("order_id"));
        model.addAttribute("realOrderId", orderId);
        model.addAttribute("createdAt", ord.get("created_at"));
        model.addAttribute("tableNumber", ord.get("table_number"));
        model.addAttribute("reservationId", ord.get("reservation_id"));
        return "order-confirmation";
    }

    @GetMapping(path = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> orderPdf(@RequestParam Long orderId) throws Exception {
        var ordOpt = orderDao.findOrder(orderId);
        if (ordOpt.isEmpty()) return ResponseEntity.notFound().build();
        var ord = ordOpt.get();
        var items = orderDao.findOrderItems(orderId);
        java.math.BigDecimal total = items.stream()
            .map(i -> ((java.math.BigDecimal) i.get("price")).multiply(new java.math.BigDecimal(((Number)i.get("quantity")).intValue())))
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        try (org.apache.pdfbox.pdmodel.PDDocument doc = new org.apache.pdfbox.pdmodel.PDDocument(); java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
            var page = new org.apache.pdfbox.pdmodel.PDPage(org.apache.pdfbox.pdmodel.common.PDRectangle.A4);
            doc.addPage(page);
            try (org.apache.pdfbox.pdmodel.PDPageContentStream cs = new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page)) {
                float margin = 50;
                float y = page.getMediaBox().getHeight() - margin;
                cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 18);
                cs.beginText(); cs.newLineAtOffset(margin, y); cs.showText("DineDigital - Order #" + ord.get("order_id")); cs.endText();
                y -= 24;
                cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 12);
                cs.beginText(); cs.newLineAtOffset(margin, y); cs.showText("Thank you for ordering!"); cs.endText();
                y -= 18;
                y -= 10;
                cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 12);
                cs.beginText(); cs.newLineAtOffset(margin, y); cs.showText("Item"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(320, y); cs.showText("Qty"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(360, y); cs.showText("Price"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(430, y); cs.showText("Amount"); cs.endText();
                y -= 14; cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 12);
                for (java.util.Map<String,Object> it : items) {
                    java.math.BigDecimal price = (java.math.BigDecimal) it.get("price");
                    int qty = ((Number) it.get("quantity")).intValue();
                    java.math.BigDecimal amt = price.multiply(new java.math.BigDecimal(qty));
                    cs.beginText(); cs.newLineAtOffset(margin, y); cs.showText(String.valueOf(it.get("name"))); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(320, y); cs.showText(String.valueOf(qty)); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(360, y); cs.showText(String.valueOf(price)); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(430, y); cs.showText(String.valueOf(amt)); cs.endText();
                    y -= 16;
                }
                y -= 10;
                cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 12);
                cs.beginText(); cs.newLineAtOffset(360, y); cs.showText("Total:"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(430, y); cs.showText(String.valueOf(total)); cs.endText();
            }
            doc.save(baos);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order-"+orderId+".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(baos.toByteArray());
        }
    }
}
