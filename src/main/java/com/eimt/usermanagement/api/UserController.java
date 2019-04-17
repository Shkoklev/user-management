package com.eimt.usermanagement.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping(value = "register")
    public String showRegistrationPage(Model model) {
        return "register";
    }

}
