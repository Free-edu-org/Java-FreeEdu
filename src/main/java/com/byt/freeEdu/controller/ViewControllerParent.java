package com.byt.freeEdu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/view/parent")
public class ViewControllerParent {

    @GetMapping("/mainpage")
    public Mono<String> mainpageParent() {
        return Mono.just("parent/parent_mainpage");
    }
}



