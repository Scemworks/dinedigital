package com.dinedigital.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class OrderDao {
    private final JdbcTemplate jdbcTemplate;
    public OrderDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public long createOrder(Integer tableNumber, Integer reservationId) {
        String sql = "INSERT INTO orders(table_number, reservation_id, status) VALUES (?,?, 'NEW')";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            // Use RETURN_GENERATED_KEYS for broader JDBC compatibility (H2, Postgres, etc.)
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (tableNumber == null) ps.setNull(1, java.sql.Types.INTEGER); else ps.setInt(1, tableNumber);
            if (reservationId == null) ps.setNull(2, java.sql.Types.INTEGER); else ps.setInt(2, reservationId);
            return ps;
        }, kh);
    // Prefer central Key value when available
    var key = kh.getKey();
    long orderId = -1L;
    if (key != null) orderId = key.longValue();
        // Fallbacks: different drivers may return different key-map casing/keys
        if (orderId == -1L && !kh.getKeyList().isEmpty()) {
            var map = kh.getKeyList().get(0);
            Object v;
            if ((v = map.get("id")) instanceof Number) orderId = ((Number) v).longValue();
            if ((v = map.get("ID")) instanceof Number) orderId = ((Number) v).longValue();
            if ((v = map.get("generated_key")) instanceof Number) orderId = ((Number) v).longValue();
            // try first numeric entry
            for (Object val : map.values()) {
                if (val instanceof Number) { orderId = ((Number) val).longValue(); break; }
            }
        }
        if (orderId != -1L) {
            // Calculate and set order_number
            Integer nextOrderNumber = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(order_number), 0) + 1 FROM orders WHERE DATE(created_at) = CURRENT_DATE", Integer.class);
            jdbcTemplate.update("UPDATE orders SET order_number = ? WHERE id = ?", nextOrderNumber, orderId);
        }
        return orderId;
    }

    public void addItem(long orderId, String name, int qty, java.math.BigDecimal price) {
        jdbcTemplate.update("INSERT INTO order_items(order_id, name, quantity, price) VALUES (?,?,?,?)",
                orderId, name, qty, price);
    }

    public List<Map<String, Object>> listPendingOrders() {
        String sql = "SELECT o.id as real_id, o.order_number as order_id, o.table_number, o.reservation_id, o.status, o.created_at, o.paid_at " +
                "FROM orders o WHERE o.status = 'NEW' ORDER BY o.created_at ASC";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> listItems(long orderId) {
        return jdbcTemplate.queryForList("SELECT id, name, quantity, price FROM order_items WHERE order_id=? ORDER BY id", orderId);
    }

    public int complete(long orderId) {
        // Mark the order as paid to remove from kitchen board
        return jdbcTemplate.update("UPDATE orders SET status='PAID', paid_at=CURRENT_TIMESTAMP WHERE id=?", orderId);
    }

    public java.util.Optional<java.util.Map<String,Object>> findOrder(long orderId){
        var list = jdbcTemplate.queryForList("SELECT id as real_id, order_number as order_id, table_number, reservation_id, status, created_at, paid_at FROM orders WHERE id=?", orderId);
        return list.stream().findFirst();
    }

    public java.util.List<java.util.Map<String,Object>> findOrderItems(long orderId){
        return jdbcTemplate.queryForList("SELECT name, quantity, price FROM order_items WHERE order_id=? ORDER BY id", orderId);
    }
}
