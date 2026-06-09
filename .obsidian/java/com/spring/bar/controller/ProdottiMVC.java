package com.spring.bar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.spring.bar.entities.ProdottoBar;
import com.spring.bar.services.ProdottoBarService;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class ProdottiMVC {
    
    @Autowired
    private ProdottoBarService service;

    @GetMapping("/prodotti")
    public String getMethodName(Model model) {
        model.addAttribute("prodotti",service.getAllProdottiBar());
        model.addAttribute("title", "Listino Prezzi Bar");
        model.addAttribute("sezioni", service.getSezioni());
        return "prodotti";
    }

    @PostMapping("/prodotti")
    public String postMethodName(ProdottoBar p) {
        service.addProdottoBar(p);
        return "redirect:/prodotti";
    }
    
}
