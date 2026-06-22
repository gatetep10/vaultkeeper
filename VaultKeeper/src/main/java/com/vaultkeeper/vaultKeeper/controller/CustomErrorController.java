package com.vaultkeeper.vaultKeeper.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMessage = "An unexpected error occurred.";
        
        if (statusCode != null) {
            int status = Integer.parseInt(statusCode.toString());
            
            if (status == HttpStatus.BAD_REQUEST.value()) {
                errorMessage = "Invalid request. Please check your input and try again.";
            } else if (status == HttpStatus.NOT_FOUND.value()) {
                errorMessage = "The page you requested was not found.";
            } else if (status == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorMessage = "Something went wrong on our end. Please try again later.";
            } else if (status == HttpStatus.FORBIDDEN.value()) {
                errorMessage = "You don't have permission to access this resource.";
            }
        }
        
        model.addAttribute("statusCode", statusCode != null ? statusCode.toString() : "500");
        model.addAttribute("errorMessage", errorMessage);
        
        return "error";
    }
}