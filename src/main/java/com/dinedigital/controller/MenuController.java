package com.dinedigital.controller;

import com.dinedigital.dao.MenuDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/menu")
public class MenuController {

    private final MenuDao menuDao;

    public MenuController(MenuDao menuDao) {
        this.menuDao = menuDao;
    }

    @GetMapping
    public String menu(Model model) {
        model.addAttribute("menu", menuDao.findAll());
        return "menu";
    }
}
