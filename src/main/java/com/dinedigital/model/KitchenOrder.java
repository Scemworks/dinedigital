package com.dinedigital.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class KitchenOrder {
    public static class Item {
        public long id; public String name; public int quantity; public BigDecimal price;
        public Item(long id, String name, int quantity, BigDecimal price) { this.id=id; this.name=name; this.quantity=quantity; this.price=price; }
    }
    public long realId; public long orderId; public Integer tableNumber; public Integer reservationId; public LocalDateTime createdAt; public List<Item> items;
}
