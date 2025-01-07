package com.byt.freeEdu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/view")
public class ViewController {

    @GetMapping("/login")
    public Mono<String> login() {
        return Mono.just("login");
    }

    @GetMapping("/homepage")
    public Mono<String> homepage() {
        return Mono.just("homepage");
    }

    @GetMapping("/mainpage")
    public Mono<String> mainpage() {
        return Mono.just("mainpage");
    }

    @GetMapping("/mainpage/teacher")
    public Mono<String> mainpageTeacher() {
        return Mono.just("mainpage_Teacher");
    }

    @GetMapping("/mainpage/admin")
    public Mono<String> mainpageAdmin() {
        return Mono.just("mainpage_Admin");
    }

    @GetMapping("/mainpage/parent")
    public Mono<String> mainpageParent() {
        return Mono.just("mainpage_Parent");
    }
}



