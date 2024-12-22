package com.byt.freeEdu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class ViewController {
    @GetMapping("/view/login")
    public Mono<String> login() {
        return Mono.just("login");
    }

    @GetMapping("/test")
    public Mono<String> test() {

        return Mono.just("login");
    }

}
