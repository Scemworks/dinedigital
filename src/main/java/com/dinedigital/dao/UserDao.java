package com.dinedigital.dao;

import com.dinedigital.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.List;

@Repository
public class UserDao {
    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<User> MAPPER = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User u = new User();
            u.setId(rs.getLong("id"));
            u.setUsername(rs.getString("username"));
            u.setPasswordHash(rs.getString("password_hash"));
            u.setRole(rs.getString("role"));
            return u;
        }
    };

    public Optional<User> findByUsername(String username) {
        var list = jdbcTemplate.query("SELECT * FROM users WHERE username = ?", MAPPER, username);
        return list.stream().findFirst();
    }

    public int countAdmins() {
        Integer n = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE role = 'ADMIN'", Integer.class);
        return n == null ? 0 : n;
    }

    public void insert(String username, String passwordHash, String role) {
        jdbcTemplate.update("INSERT INTO users(username, password_hash, role) VALUES (?,?,?)",
                username, passwordHash, role);
    }

    public List<User> listAll() {
        return jdbcTemplate.query("SELECT id, username, password_hash, role FROM users ORDER BY id", MAPPER);
    }

    public void deleteById(long id) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }
}
