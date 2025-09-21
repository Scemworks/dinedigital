package com.dinedigital.controller;

import com.dinedigital.dao.MenuDao;
import com.dinedigital.dao.ReservationDao;
import com.dinedigital.dao.UserDao;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final MenuDao menuDao;
    private final ReservationDao reservationDao;
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    public AdminController(MenuDao menuDao, ReservationDao reservationDao, UserDao userDao, PasswordEncoder passwordEncoder) {
        this.menuDao = menuDao;
        this.reservationDao = reservationDao;
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String dashboard() {
        return "admin/dashboard";
    }

    // Menu management
    @GetMapping("/menu")
    public String menu(Model model) {
        model.addAttribute("items", menuDao.findAll());
        return "admin/menu";
    }

    @PostMapping("/menu/add")
    public String addMenu(@RequestParam String name,
                          @RequestParam String description,
                          @RequestParam BigDecimal price,
                          @RequestParam(required = false) String image) {
        menuDao.insert(name, description, price, image);
        return "redirect:/admin/menu";
    }

    @PostMapping("/menu/delete")
    public String deleteMenu(@RequestParam long id) {
        menuDao.delete(id);
        return "redirect:/admin/menu";
    }

    @PostMapping("/menu/update")
    public String updateMenu(@RequestParam long id,
                             @RequestParam String name,
                             @RequestParam String description,
                             @RequestParam BigDecimal price,
                             @RequestParam(required = false) String image) {
        menuDao.update(id, name, description, price, image);
        return "redirect:/admin/menu";
    }

    // Reservations
    @GetMapping("/reservations")
    public String reservations(Model model) {
        model.addAttribute("reservations", reservationDao.listAll());
        return "admin/reservations";
    }

    @PostMapping("/reservations/checkin")
    public String checkIn(@RequestParam String code) {
        reservationDao.checkInByCode(code);
        return "redirect:/admin/reservations";
    }

    // QR code feature removed

    // User management
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userDao.listAll());
        return "admin/users";
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String role) {
        userDao.insert(username, passwordEncoder.encode(password), role);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam long id) {
        // Prevent deleting the last admin
        long admins = userDao.countAdmins();
        var u = userDao.listAll().stream().filter(x -> x.getId() == id).findFirst();
        if (u.isPresent() && "ADMIN".equals(u.get().getRole()) && admins <= 1) {
            return "redirect:/admin/users"; // no-op
        }
        userDao.deleteById(id);
        return "redirect:/admin/users";
    }
}
