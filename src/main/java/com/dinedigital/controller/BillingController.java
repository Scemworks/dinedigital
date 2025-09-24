package com.dinedigital.controller;

import com.dinedigital.dao.OrderDao;
import com.dinedigital.dao.ReservationDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
 

@Controller
@RequestMapping("/admin/billing")
public class BillingController {
    private final OrderDao orderDao;
    private final ReservationDao reservationDao;
    public BillingController(OrderDao orderDao, ReservationDao reservationDao) { this.orderDao = orderDao; this.reservationDao = reservationDao; }

    @GetMapping
    public String billing(@RequestParam(name = "orderId", required = false) Integer orderNumber,
                          Model model) {
        // If an order number is provided, show a focused billing view for that order
        if (orderNumber != null && orderNumber > 0) {
            var ordOpt = orderDao.findOrderByOrderNumber(orderNumber);
            if (ordOpt.isEmpty()) {
                model.addAttribute("notFound", true);
            } else {
                var ord = ordOpt.get();
                model.addAttribute("order", ord);
                var items = orderDao.findOrderItems(((Number) ord.get("real_id")).longValue());
                model.addAttribute("items", items);
                java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;
                for (java.util.Map<String,Object> it : items) {
                    java.math.BigDecimal price = (java.math.BigDecimal) it.get("price");
                    int qty = ((Number) it.get("quantity")).intValue();
                    subtotal = subtotal.add(price.multiply(new java.math.BigDecimal(qty)));
                }
                java.math.BigDecimal gst = subtotal.multiply(new java.math.BigDecimal("0.18")).setScale(2, java.math.RoundingMode.HALF_UP);
                java.math.BigDecimal total = subtotal.add(gst);
                if (ord.get("reservation_id") != null) {
                    total = total.add(new java.math.BigDecimal("50.00"));
                }
                model.addAttribute("total", total);
            }
        }
        // List all candidates for convenience
        model.addAttribute("orders", orderDao.listUnpaidForBilling());
        return "billing";
    }

    @GetMapping(path = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> billingPdf(@RequestParam Long orderId) throws Exception {
        // Accept orderId as the displayed order number and resolve to real ID if needed
        java.util.Optional<java.util.Map<String,Object>> ordOpt;
        if (orderId < 1000000L) { // heuristic: small numbers are order_number, not PK
            ordOpt = orderDao.findOrderByOrderNumber(orderId.intValue());
        } else {
            ordOpt = orderDao.findOrder(orderId);
        }
        if (ordOpt.isEmpty()) return ResponseEntity.notFound().build();
        Map<String,Object> ord = ordOpt.get();
        long realId = ((Number) ord.get("real_id")).longValue();
        List<Map<String,Object>> items = orderDao.findOrderItems(realId);
        BigDecimal subtotal = items.stream()
                .map(i -> ((BigDecimal) i.get("price")).multiply(new BigDecimal(((Number)i.get("quantity")).intValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal gstRate = new BigDecimal("0.18");
        BigDecimal gstAmount = subtotal.multiply(gstRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(gstAmount);

        // If this order is a preorder linked to a reservation, add reservation fee to billing PDF
        BigDecimal reservationFee = BigDecimal.ZERO;
        if (ord.get("reservation_id") != null) {
            try {
                long resId = ((Number) ord.get("reservation_id")).longValue();
                var resOpt = reservationDao.findById(resId);
                if (resOpt.isPresent()) {
                    // It's a preorder linked to a reservation; apply reservation fee
                    reservationFee = new BigDecimal("50.00");
                    total = total.add(reservationFee);
                }
            } catch (Exception ignored) {
                // fallback: if reservation lookup fails, do not apply fee
            }
        }

        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float margin = 50;
                float y = page.getMediaBox().getHeight() - margin;
                cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
                // Title without logo
                cs.beginText(); cs.newLineAtOffset(margin, y); cs.showText("DineDigital - Order #" + ord.get("order_id")); cs.endText();
                y -= 24;
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.beginText(); cs.newLineAtOffset(margin, y); cs.showText("Table: " + ord.get("table_number") + "    Date: " + ord.get("created_at")); cs.endText();
                y -= 18;
                // header
                y -= 10;
                cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                float xItem = margin;
                float xQty = 320f;
                float xPrice = 380f; // moved right to give room for price text
                float xAmt = 480f;   // moved right to avoid overlap with labels
                cs.beginText(); cs.newLineAtOffset(xItem, y); cs.showText("Item"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xQty, y); cs.showText("Qty"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("Price (INR)"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText("Amount (INR)"); cs.endText();
                y -= 14; cs.setFont(PDType1Font.HELVETICA, 12);
                for (Map<String,Object> it : items) {
                    BigDecimal price = (BigDecimal) it.get("price");
                    int qty = ((Number) it.get("quantity")).intValue();
                    BigDecimal amt = price.multiply(new BigDecimal(qty));
                    cs.beginText(); cs.newLineAtOffset(xItem, y); cs.showText(String.valueOf(it.get("name"))); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(xQty, y); cs.showText(String.valueOf(qty)); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText(String.format("INR %.2f", price)); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("INR %.2f", amt)); cs.endText();
                    y -= 16;
                }
                y -= 10;
                cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                // Subtotals & taxes
                cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("Subtotal:"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("INR %.2f", subtotal)); cs.endText();
                y -= 14;
                cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("GST (18%):"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("INR %.2f", gstAmount)); cs.endText();
                y -= 14;
                if (reservationFee.compareTo(BigDecimal.ZERO) > 0) {
                    y -= 10;
                    cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("Reservation Fee:"); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("INR %.2f", reservationFee)); cs.endText();
                    total = total.add(reservationFee);
                }
                y -= 10;
                cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("Total:"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("INR %.2f", total)); cs.endText();
            }
            doc.save(baos);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order-"+ord.get("order_id")+".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());
        }
    }

    @PostMapping("/paid")
    public String markPaid(@RequestParam Long orderId) {
        try {
            // Accept either displayed order number or real id
            if (orderId < 1000000L) {
                var ordOpt = orderDao.findOrderByOrderNumber(orderId.intValue());
                ordOpt.ifPresent(o -> orderDao.complete(((Number)o.get("real_id")).longValue()));
            } else {
                orderDao.complete(orderId);
            }
        } catch (Exception ignored) {
            // Swallow to keep UX simple; redirect will show generic success
        }
        return "redirect:/admin/billing?paid=1";
    }
}
