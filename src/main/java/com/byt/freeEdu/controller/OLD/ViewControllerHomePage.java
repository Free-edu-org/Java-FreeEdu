package com.byt.freeEdu.controller.OLD;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.users.UserService;

@Controller
@RequestMapping("/view")
public class ViewControllerHomePage{

  @Autowired
  private UserService userService;

  @GetMapping("/homepage")
  public String homepage() {
    return "homepage";
  }

  @GetMapping("/login")
  public String loginPage(@RequestParam(value = "error", required = false) String error,
      Model model) {
    if (error != null) {
      model.addAttribute("error",error);
    } else {
      model.addAttribute("error",null);
    }
    return "login";
  }

  @GetMapping("/register")
  public String showRegistrationForm(Model model) {
    model.addAttribute("user",new User());
    return "register";
  }

  @PostMapping("/register")
  public String registerUser(@ModelAttribute UserDto user, Model model) {
    try {
      boolean success = userService.addUser(user);

      if (!success) {
        model.addAttribute("error","Nie udało się zarejestrować użytkownika. Spróbuj ponownie.");
        return "register";
      }

      return "redirect:/view/login";
    } catch (IllegalArgumentException ex) {
      model.addAttribute("errorMessage",ex.getMessage());
      return "register";
    }
  }
}
