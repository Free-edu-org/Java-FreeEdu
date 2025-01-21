package com.byt.freeEdu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/view")
public class ViewControllerHomePage {

    @GetMapping("/homepage")
    public Mono<String> homepage() {
        return Mono.just("homepage");
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", true); // Ustawia atrybut tylko w przypadku błędu
        } else {
            model.addAttribute("error", false); // Brak błędu
        }
        return "login";
    }
}



