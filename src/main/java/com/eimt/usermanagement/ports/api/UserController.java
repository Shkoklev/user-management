package com.eimt.usermanagement.ports.api;

import com.eimt.usermanagement.model.User;
import com.eimt.usermanagement.model.dto.UserDto;
import com.eimt.usermanagement.model.event.OnRegistrationCompleteEvent;
import com.eimt.usermanagement.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Controller
public class UserController {

    ApplicationEventPublisher eventPublisher;

    UserService userService;

    public UserController(UserService userService, ApplicationEventPublisher applicationEventPublisher) {
        this.userService = userService;
        this.eventPublisher = applicationEventPublisher;

    }

    @GetMapping(value = "register")
    public String showRegistrationPage(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "register";
    }

    @PostMapping(value = "register")
    public String registerUser(
            @ModelAttribute("user") @Valid UserDto userDto,
            BindingResult result, WebRequest request, Model model
    ) {
        if(result.hasErrors()) {
            model.addAttribute("objectErrors", result.getAllErrors().stream().filter(err -> err.getClass() != FieldError.class).collect(Collectors.toList()));
            model.addAttribute("fieldErrors", result.getFieldErrors());
            return "register";
        }

        if(!userDto.getPassword().equals(userDto.getVerifiedPassword()))
        {
            model.addAttribute("errorPasswordsDoNotMatch", "Passwords do not match");
            return "register";
        }
        User user = this.userService.createUser(userDto);

        try {
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user,request.getLocale(),appUrl));
        } catch (Exception me) {
            me.printStackTrace();
        }

        return "redirect:/registrationConfirm";
    }

    @GetMapping(value = "registrationConfirm")
    public String showActivationPage() {
        return "registrationConfirm";
    }

    @PostMapping(value = "registrationConfirm")
    public ModelAndView activateUser(@RequestParam("token") String token) {
        try {
            this.userService.verifyUser(token); 
            return new ModelAndView("redirect:/login");
        } catch (RuntimeException e) {
            return new ModelAndView("redirect:/activation?error");
        }
    }

}
