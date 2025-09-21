package com.dinedigital.dao;

import com.dinedigital.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ReservationDao {
    private final JdbcTemplate jdbcTemplate;

    public ReservationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Reservation insert(Reservation r) {
        String sql = "INSERT INTO reservations(name, email, date, time, guests, confirmation_code) VALUES (?,?,?,?,?,?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, r.getName());
            ps.setString(2, r.getEmail());
            ps.setObject(3, r.getDate());
            ps.setObject(4, r.getTime());
            ps.setInt(5, r.getGuests());
            ps.setString(6, r.getConfirmationCode());
            return ps;
        }, kh);
        if (kh.getKey() != null) r.setId(kh.getKey().longValue());
        return r;
    }

    public List<Reservation> list() {
    return jdbcTemplate.query("SELECT id, name, email, date, time, guests, confirmation_code, created_at FROM reservations ORDER BY id DESC",
                (rs, i) -> {
                    Reservation r = new Reservation();
                    r.setId(rs.getLong("id"));
                    r.setName(rs.getString("name"));
                    r.setEmail(rs.getString("email"));
                    r.setDate(rs.getObject("date", java.time.LocalDate.class));
                    r.setTime(rs.getObject("time", java.time.LocalTime.class));
                    r.setGuests(rs.getInt("guests"));
                    r.setConfirmationCode(rs.getString("confirmation_code"));
                    r.setCreatedAt(rs.getObject("created_at", java.time.LocalDateTime.class));
                    return r;
                });
    }

    public List<Reservation> listAll() {
        return jdbcTemplate.query("SELECT id, name, email, date, time, guests, confirmation_code, created_at, checked_in, check_in_time FROM reservations ORDER BY date DESC, time DESC",
                (rs, i) -> {
                    Reservation r = new Reservation();
                    r.setId(rs.getLong("id"));
                    r.setName(rs.getString("name"));
                    r.setEmail(rs.getString("email"));
                    r.setDate(rs.getObject("date", java.time.LocalDate.class));
                    r.setTime(rs.getObject("time", java.time.LocalTime.class));
                    r.setGuests(rs.getInt("guests"));
                    r.setConfirmationCode(rs.getString("confirmation_code"));
                    r.setCreatedAt(rs.getObject("created_at", java.time.LocalDateTime.class));
                    r.setCheckedIn(rs.getBoolean("checked_in"));
                    var cit = rs.getTimestamp("check_in_time");
                    r.setCheckInTime(cit == null ? null : cit.toLocalDateTime());
                    return r;
                });
    }

    public int deleteOlderThanToday() {
        return jdbcTemplate.update("DELETE FROM reservations WHERE date < CURRENT_DATE");
    }

    public int checkInByCode(String code) {
        return jdbcTemplate.update("UPDATE reservations SET checked_in = TRUE, check_in_time = CURRENT_TIMESTAMP WHERE confirmation_code = ?",
                code);
    }
}
