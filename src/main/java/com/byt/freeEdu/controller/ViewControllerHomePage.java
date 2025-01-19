package com.byt.freeEdu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/view")
public class ViewControllerHomePage {

    @GetMapping("/homepage")
    public Mono<String> homepage() {
        return Mono.just("homepage");
    }

    @GetMapping("/login")
    public Mono<String> login() {
        return Mono.just("login");
    }
}



