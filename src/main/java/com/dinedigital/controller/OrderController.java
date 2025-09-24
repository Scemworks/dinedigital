package com.dinedigital.controller;

import com.dinedigital.dao.OrderDao;
import com.dinedigital.dao.ReservationDao;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderDao orderDao;
    private final ReservationDao reservationDao;
    private final ResourceLoader resourceLoader;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    public OrderController(OrderDao orderDao, ReservationDao reservationDao, ResourceLoader resourceLoader) { this.orderDao = orderDao; this.reservationDao = reservationDao; this.resourceLoader = resourceLoader; }

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
        // For walk-in orders, leave reservation_id NULL to avoid joining to a non-existent reservation
        long orderId = orderDao.createOrder(tableNumber, null);
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
            .map(i -> {
                java.math.BigDecimal price = (java.math.BigDecimal) i.getOrDefault("price", java.math.BigDecimal.ZERO);
                Number qn = (Number) i.getOrDefault("quantity", 0);
                int q = qn == null ? 0 : qn.intValue();
                return price.multiply(new java.math.BigDecimal(q));
            })
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        try (org.apache.pdfbox.pdmodel.PDDocument doc = new org.apache.pdfbox.pdmodel.PDDocument(); java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
            var page = new org.apache.pdfbox.pdmodel.PDPage(org.apache.pdfbox.pdmodel.common.PDRectangle.A4);
            doc.addPage(page);
            try (org.apache.pdfbox.pdmodel.PDPageContentStream cs = new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page)) {
                float margin = 50f;
                float pageWidth = page.getMediaBox().getWidth();
                float y = page.getMediaBox().getHeight() - margin;
                var bold = org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD;
                var regular = org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA;

                // Header with logo if available
                cs.setFont(bold, 18);
                boolean drewLogo = false;
                try {
                    byte[] bytes = null;
                    String[] candidates = new String[]{
                        "classpath:/static/logo.png",
                        "classpath:/public/logo.png",
                        "classpath:/resources/logo.png"
                    };
                    for (String loc : candidates) {
                        Resource res = resourceLoader.getResource(loc);
                        if (res.exists()) {
                            try (java.io.InputStream is = res.getInputStream()) { bytes = is.readAllBytes(); }
                            break;
                        }
                    }
                    if (bytes != null) {
                        var logo = org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject.createFromByteArray(doc, bytes, "logo");
                        float logoH = 28f;
                        float scale = logoH / logo.getHeight();
                        float logoW = logo.getWidth() * scale;
                        cs.drawImage(logo, margin, y - logoH + 4, logoW, logoH);
                        cs.beginText(); cs.newLineAtOffset(margin + logoW + 8, y); cs.showText("DineDigital"); cs.endText();
                        drewLogo = true;
                    }
                } catch (Exception ignore) {}
                if (!drewLogo) { cs.beginText(); cs.newLineAtOffset(margin, y); cs.showText("DineDigital"); cs.endText(); }

                String heading = "Order #" + ord.get("order_id");
                if (ord.get("reservation_id") != null) {
                    try {
                        long resId = ((Number) ord.get("reservation_id")).longValue();
                        var resOpt = reservationDao.findById(resId);
                        if (resOpt.isPresent()) heading = "Reservation " + resOpt.get().getConfirmationCode();
                    } catch (Exception ignored) { }
                }
                cs.setFont(bold, 12);
                cs.beginText(); cs.newLineAtOffset(pageWidth - margin - 220, y); cs.showText(heading); cs.endText();
                y -= 26;

                cs.setFont(regular, 11);
                cs.beginText(); cs.newLineAtOffset(margin, y); cs.showText("Thank you for your reservation/order."); cs.endText();
                y -= 16;

                // Reservation details
                if (ord.get("reservation_id") != null) {
                    try {
                        long resId = ((Number) ord.get("reservation_id")).longValue();
                        var resOpt = reservationDao.findById(resId);
                        if (resOpt.isPresent()) {
                            var r = resOpt.get();
                            cs.beginText(); cs.newLineAtOffset(margin, y); cs.showText("Guests: " + r.getGuests() + "    Reservation: " + r.getDate() + " " + r.getTime()); cs.endText();
                            y -= 14;
                        }
                    } catch (Exception ignored) {}
                }

                y -= 6;

                // Table header
                cs.setFont(bold, 11);
                float xItem = margin;
                float xQty = margin + 300;
                float xPrice = margin + 360;
                float xAmt = margin + 440;
                cs.beginText(); cs.newLineAtOffset(xItem, y); cs.showText("Item"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xQty, y); cs.showText("Qty"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("Price (₹)"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText("Amount (₹)"); cs.endText();
                y -= 14;
                cs.setFont(regular, 11);

                java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;
                for (java.util.Map<String,Object> it : items) {
                    java.math.BigDecimal price = (java.math.BigDecimal) it.getOrDefault("price", java.math.BigDecimal.ZERO);
                    Number qn = (Number) it.getOrDefault("quantity", 0);
                    int qty = qn == null ? 0 : qn.intValue();
                    java.math.BigDecimal amt = price.multiply(new java.math.BigDecimal(qty));
                    subtotal = subtotal.add(amt);

                    String name = String.valueOf(it.getOrDefault("name", ""));
                    try {
                        cs.beginText(); cs.newLineAtOffset(xItem, y); cs.showText(name); cs.endText();
                        cs.beginText(); cs.newLineAtOffset(xQty, y); cs.showText(String.valueOf(qty)); cs.endText();
                        cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText(String.format("₹ %.2f", price)); cs.endText();
                        cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("₹ %.2f", amt)); cs.endText();
                    } catch (Exception e) {
                        // log and continue rendering remaining items
                        logger.warn("PDF rendering: failed to render item row (orderId={}) item={}", orderId, name, e);
                    }
                    y -= 16;
                }

                java.math.BigDecimal reservationFee = java.math.BigDecimal.ZERO;
                if (ord.get("reservation_id") != null) reservationFee = new java.math.BigDecimal("50.00");
                java.math.BigDecimal gst = subtotal.multiply(new java.math.BigDecimal("0.18")).setScale(2, java.math.RoundingMode.HALF_UP);
                java.math.BigDecimal total = subtotal.add(gst).add(reservationFee);
                y -= 10;
                cs.setFont(bold, 11);
                cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("Subtotal:"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("₹ %.2f", subtotal)); cs.endText();
                y -= 14;
                cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("GST (18%):"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("₹ %.2f", gst)); cs.endText();
                y -= 14;
                if (reservationFee.compareTo(java.math.BigDecimal.ZERO) > 0) {
                    cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("Reservation Fee:"); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("₹ %.2f", reservationFee)); cs.endText();
                    y -= 14;
                }
                cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("Total:"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("₹ %.2f", total)); cs.endText();
            }
            doc.save(baos);

            String filename = "order-" + orderId + ".pdf";
            if (ord.get("reservation_id") != null) {
                try {
                    long resId = ((Number) ord.get("reservation_id")).longValue();
                    var resOpt = reservationDao.findById(resId);
                    if (resOpt.isPresent()) filename = "reservation-" + resOpt.get().getConfirmationCode() + ".pdf";
                } catch (Exception ignored) { }
            }
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(baos.toByteArray());
        }
    }
}
