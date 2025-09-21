package com.dinedigital.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class MenuDao {
    private final JdbcTemplate jdbcTemplate;

    public MenuDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findAll() {
        return jdbcTemplate.query("SELECT id, name, description, price, image FROM menu ORDER BY id", (rs, i) -> Map.of(
                "id", rs.getLong("id"),
                "name", rs.getString("name"),
                "description", rs.getString("description"),
                "price", rs.getBigDecimal("price"),
                "image", rs.getString("image")
        ));
    }

    public void insert(String name, String description, java.math.BigDecimal price, String image) {
        jdbcTemplate.update("INSERT INTO menu(name, description, price, image) VALUES (?,?,?,?)",
                name, description, price, image);
    }

    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM menu WHERE id = ?", id);
    }

    public void update(long id, String name, String description, java.math.BigDecimal price, String image) {
        jdbcTemplate.update("UPDATE menu SET name=?, description=?, price=?, image=? WHERE id=?",
                name, description, price, image, id);
    }
}
