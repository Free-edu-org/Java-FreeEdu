package com.byt.freeEdu.controller;

import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/view")
public class ViewControllerHomePage {

    @Autowired
    private UserService userService;

    @GetMapping("/homepage")
    public Mono<String> homepage() {
        return Mono.just("homepage");
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        } else {
            model.addAttribute("error", null);
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
                               Model model
    ) {
        boolean success = userService.addUser(user.getUsername(),user.getFirstname(),user.getLastname(),user.getEmail(),user.getPassword());
        if (!success) {
            model.addAttribute("error", "Nie udało się zarejestrować użytkownika. Spróbuj ponownie.");
            return "register";
        }

        return "redirect:/view/login";
    }
}



