package com.codeJavengers.budgetManager.controllers.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class ViewController {
    @GetMapping("/view/login")
    public Mono<String> login() {
        return Mono.just("login");
    }
    @GetMapping("/view/register")
    public Mono<String> register() {
        return Mono.just("register");
    }
    @GetMapping("/view/home")
    public Mono<String> home() {
        return Mono.just("home");
    }
}
