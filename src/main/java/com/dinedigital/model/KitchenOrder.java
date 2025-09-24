package com.dinedigital.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class KitchenOrder {
    public static class Item {
        public long id; public String name; public int quantity; public BigDecimal price;
        public Item(long id, String name, int quantity, BigDecimal price) { this.id=id; this.name=name; this.quantity=quantity; this.price=price; }
        public long getId() { return id; }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public BigDecimal getPrice() { return price; }
    }
    public long realId; public long orderId; public Integer tableNumber; public Integer reservationId; public Date createdAt; public String createdAtStr; public List<Item> items;
    public long getRealId() { return realId; }
    public long getOrderId() { return orderId; }
    public Integer getTableNumber() { return tableNumber; }
    public Integer getReservationId() { return reservationId; }
    public Date getCreatedAt() { return createdAt; }
    public String getCreatedAtStr() { return createdAtStr; }
    public List<Item> getItems() { return items; }
}
