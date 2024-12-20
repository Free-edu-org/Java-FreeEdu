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

    @GetMapping("/view/homepage")
    public Mono<String> homepage() {
        return Mono.just("homepage");
    }

    @GetMapping("/view/mainpage")
    public Mono<String> mainpage() {
        return Mono.just("mainpage");
    }
    @GetMapping("/view/mainpage_Teacher")
    public Mono<String> mainpage_Teacher() {
        return Mono.just("mainpage_Teacher");
    }
    @GetMapping("/view/mainpage_Admin")
    public Mono<String> mainpage_Admin() {
        return Mono.just("mainpage_Admin");
    }
    @GetMapping("/view/mainpage_Parent")
    public Mono<String> mainpage_Parent() {
        return Mono.just("mainpage_Parent");
    }
}
