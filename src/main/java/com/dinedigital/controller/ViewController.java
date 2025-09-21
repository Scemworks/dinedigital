package com.dinedigital.controller;

import com.dinedigital.dao.MenuDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    private final MenuDao menuDao;

    public ViewController(MenuDao menuDao) {
        this.menuDao = menuDao;
    }

    @GetMapping({"/", "/home"})
    public String home() {
        return "index";
    }

    @GetMapping("/order")
    public String order(Model model) {
        model.addAttribute("items", menuDao.findAll());
        return "order";
    }
}
