package com.Jarvis.Jarvis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Jarvis.Jarvis.service.UserService;
import org.springframework.ui.Model;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }

    @PostMapping("/register")
    public String register(
        @RequestParam String name,
        @RequestParam String email,
        @RequestParam String password,
        Model model){

            String result = userService.register(name, email, password);

            if (result.equals("success")) {
                return "redirect:/login?registered";
            }else{
                model.addAttribute("error", result);
                return "register";
            }
        }
}