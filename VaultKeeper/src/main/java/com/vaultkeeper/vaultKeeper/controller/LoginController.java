package com.vaultkeeper.vaultKeeper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "expired", required = false) String expired,
                                Model model) {
        
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
        }
        
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        
        if (expired != null) {
            model.addAttribute("errorMessage", "Your session has expired. Please login again.");
        }
        
        return "login";
    }
}