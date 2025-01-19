package com.byt.freeEdu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/view/admin")
public class ViewControllerAdmin {

    @GetMapping("/mainpage")
    public Mono<String> mainpageAdmin() {
        return Mono.just("admin/admin_mainpage");
    }
}



