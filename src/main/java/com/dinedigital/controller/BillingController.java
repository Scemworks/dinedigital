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
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Controller
@RequestMapping("/admin/billing")
public class BillingController {
    private final OrderDao orderDao;
    private final ReservationDao reservationDao;
    private final ResourceLoader resourceLoader;
    public BillingController(OrderDao orderDao, ReservationDao reservationDao, ResourceLoader resourceLoader) { this.orderDao = orderDao; this.reservationDao = reservationDao; this.resourceLoader = resourceLoader; }

    @GetMapping
    public String billing(Model model) {
        // Billing should show all unpaid orders, regardless of kitchen status
        model.addAttribute("orders", orderDao.listUnpaidForBilling());
        return "billing";
    }

    @GetMapping(path = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> billingPdf(@RequestParam Long orderId) throws Exception {
        var ordOpt = orderDao.findOrder(orderId);
        if (ordOpt.isEmpty()) return ResponseEntity.notFound().build();
        Map<String,Object> ord = ordOpt.get();
        List<Map<String,Object>> items = orderDao.findOrderItems(orderId);
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
                // Draw logo (if available) and title
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
                            try (java.io.InputStream is = res.getInputStream()) {
                                bytes = is.readAllBytes();
                            }
                            break;
                        }
                    }
                    if (bytes != null) {
                        PDImageXObject logo = PDImageXObject.createFromByteArray(doc, bytes, "logo");
                        float logoH = 28f;
                        float scale = logoH / logo.getHeight();
                        float logoW = logo.getWidth() * scale;
                        cs.drawImage(logo, margin, y - logoH + 4, logoW, logoH);
                        cs.beginText(); cs.newLineAtOffset(margin + logoW + 8, y); cs.showText("DineDigital - Order #" + ord.get("order_id")); cs.endText();
                        drewLogo = true;
                    }
                } catch (Exception ignore) {}
                if (!drewLogo) {
                    cs.beginText(); cs.newLineAtOffset(margin, y); cs.showText("DineDigital - Order #" + ord.get("order_id")); cs.endText();
                }
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
                cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("Price (₹)"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText("Amount (₹)"); cs.endText();
                y -= 14; cs.setFont(PDType1Font.HELVETICA, 12);
                for (Map<String,Object> it : items) {
                    BigDecimal price = (BigDecimal) it.get("price");
                    int qty = ((Number) it.get("quantity")).intValue();
                    BigDecimal amt = price.multiply(new BigDecimal(qty));
                    cs.beginText(); cs.newLineAtOffset(xItem, y); cs.showText(String.valueOf(it.get("name"))); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(xQty, y); cs.showText(String.valueOf(qty)); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText(String.format("₹ %.2f", price)); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("₹ %.2f", amt)); cs.endText();
                    y -= 16;
                }
                y -= 10;
                cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                // Subtotals & taxes
                cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("Subtotal:"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("₹ %.2f", subtotal)); cs.endText();
                y -= 14;
                cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("GST (18%):"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("₹ %.2f", gstAmount)); cs.endText();
                y -= 14;
                if (reservationFee.compareTo(BigDecimal.ZERO) > 0) {
                    y -= 10;
                    cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("Reservation Fee:"); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("₹ %.2f", reservationFee)); cs.endText();
                    total = total.add(reservationFee);
                }
                y -= 10;
                cs.beginText(); cs.newLineAtOffset(xPrice, y); cs.showText("Total:"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(xAmt, y); cs.showText(String.format("₹ %.2f", total)); cs.endText();
            }
            doc.save(baos);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order-"+orderId+".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());
        }
    }

    @PostMapping("/paid")
    public String markPaid(@RequestParam Long orderId) {
        try {
            orderDao.complete(orderId);
        } catch (Exception ignored) {
            // Swallow to keep UX simple; redirect will show generic success
        }
        return "redirect:/admin/billing?paid=1";
    }
}
