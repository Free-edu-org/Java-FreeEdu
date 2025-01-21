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
            model.addAttribute("error", true); // Ustawia atrybut tylko w przypadku błędu
        } else {
            model.addAttribute("error", false); // Brak błędu
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("username") String username,
            @ModelAttribute("firstname") String firstname,
            @ModelAttribute("lastname") String lastname,
            @ModelAttribute("email") String email,
            @ModelAttribute("password") String password,
            @ModelAttribute("confirmPassword") String confirmPassword,
            Model model
    ) {
        // Walidacja haseł
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Hasła się nie zgadzają.");
            return "register";
        }
        System.out.println(username);
        // Utworzenie nowego użytkownika
        boolean success = userService.addUser(username,firstname,lastname,email,password);

        if (!success) {
            model.addAttribute("error", "Nie udało się zarejestrować użytkownika. Spróbuj ponownie.");
            return "register";
        }

        return "redirect:/login";
    }
}



