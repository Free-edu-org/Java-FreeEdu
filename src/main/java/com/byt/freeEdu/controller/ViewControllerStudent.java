package com.byt.freeEdu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/view/student")
public class ViewControllerStudent {

    @GetMapping("/mainpage")
    public Mono<String> mainpage() {
        return Mono.just("student/student_mainpage");
    }
}



