package com.eimt.usermanagement.ports.api;

import com.eimt.usermanagement.model.User;
import com.eimt.usermanagement.model.dto.UserDto;
import com.eimt.usermanagement.model.dto.UserEditObject;
import com.eimt.usermanagement.model.event.OnRegistrationCompleteEvent;
import com.eimt.usermanagement.model.exception.DuplicateUserException;
import com.eimt.usermanagement.model.exception.UserNotFoundException;
import com.eimt.usermanagement.repository.mail.MailSenderRepository;
import com.eimt.usermanagement.service.DepartmentService;
import com.eimt.usermanagement.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {

    ApplicationEventPublisher eventPublisher;

    UserService userService;

    DepartmentService departmentService;

    MailSenderRepository mailSenderRepository;

    public UserController(UserService userService, ApplicationEventPublisher applicationEventPublisher,
                          MailSenderRepository mailSenderRepository, DepartmentService departmentService) {
        this.userService = userService;
        this.eventPublisher = applicationEventPublisher;
        this.mailSenderRepository = mailSenderRepository;
        this.departmentService = departmentService;

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
        if (result.hasErrors()) {
            model.addAttribute("objectErrors", result.getAllErrors().stream().filter(err -> err.getClass() != FieldError.class).collect(Collectors.toList()));
            model.addAttribute("fieldErrors", result.getFieldErrors());
            return "register";
        }

        if (!userDto.getPassword().equals(userDto.getVerifiedPassword())) {
            model.addAttribute("errorPasswordsDoNotMatch", "Passwords do not match");
            return "register";
        }

        try {
            User user = this.userService.createUser(userDto);
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
        } catch (DuplicateUserException exception) {
            return "redirect:/register?duplicateUser";
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
            this.userService.verifyAndActivateUser(token);
            return new ModelAndView("redirect:/login");
        } catch (RuntimeException e) {
            return new ModelAndView("redirect:/registrationConfirm?error");
        }
    }

    @GetMapping(value = "forgotPassword")
    public String showForgotPasswordPage() {
        return "forgotPassword";
    }

    @PostMapping(value = "forgotPassword")
    public ModelAndView getNewPassword(@RequestParam("email") String email) {
        try {
            String password = this.userService.generateNewPasswordForEmail(email);
            String to = email;
            String subject = "Your new password is here";
            String confirmationUrl = "http://localhost:8080/login";
            String message = "Here is your newly generated password " + password;
            this.mailSenderRepository.sendMail(to, subject, confirmationUrl, message);
            return new ModelAndView("redirect:/login");
        } catch (RuntimeException e) {
            return new ModelAndView("redirect:/forgotPassword?error");
        }
    }

    @GetMapping(value = "editProfile")
    public String showEditProfilePage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = this.userService.getUser(username);
        UserEditObject userEditObject
                = new UserEditObject(u.getFirstName(), u.getLastName(), u.getBirthDate(), u.getGender());
        model.addAttribute("email", getActiveUser().getEmail());
        model.addAttribute("user", userEditObject);
        return "editProfile";
    }

    @PostMapping(value = "editProfile")
    public String editProfile(Model model,
                              @ModelAttribute("user") @Valid UserEditObject userEditObject,
                              BindingResult bindingResult) {
        String email = getActiveUser().getEmail();
        User u = this.userService.getUser(email);
        this.userService.editUser(u, userEditObject);
        model.addAttribute("email", email);
        return "editProfile";
    }

    @GetMapping("/employees/{email}/profile-edit")
    public String showEditProfilePageForEmployee(Model model,
                                                 @PathVariable String email) {
        User e = this.userService.getUser(email);
        UserEditObject userEditObject
                = new UserEditObject(e.getFirstName(), e.getLastName(), e.getBirthDate(), e.getGender());
        model.addAttribute("email", getActiveUser().getUsername());
        model.addAttribute("empEmail", e.getEmail());
        model.addAttribute("user", userEditObject);
        return "employee-profile-edit";
    }

    @PostMapping("/employees/{email}/profile-edit")
    public String editProfileForEmployee(Model model,
                                         Pageable pageable,
                                         Sort sort,
                                         @PathVariable String email,
                                         @ModelAttribute("user") @Valid UserEditObject userEditObject,
                                         BindingResult bindingResult) {
        User u = this.userService.getUser(email);
        this.userService.editUser(u, userEditObject);
        return getEmployeesOfDepartment(model, pageable, sort);
    }

    @GetMapping("/changePassword")
    public String showChangePasswordPage(Model model) {
        model.addAttribute("email", getActiveUser().getUsername());
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String newPasswordRepeat
    ) {
        User e = (User) userService.getUser(getActiveUser().getUsername());
        boolean validPassword = userService.isPasswordValid(e, oldPassword);
        if (!validPassword) {
            return "redirect:/changePassword?invalid";
        } else if (!newPassword.equals(newPasswordRepeat)) {
            return "redirect:/changePassword?dontMatch";
        } else {
            this.userService.updatePassword(e, newPassword);
            return "redirect:/logout"; // TODO should logout after password change
        }
    }

    @GetMapping(value = "login")
    public String login() {
        if (!(SecurityContextHolder.getContext().getAuthentication()
                instanceof AnonymousAuthenticationToken)) {
            return "redirect:/editProfile";
        }
        return "login";
    }

    @GetMapping(value = "/me")
    public String getMe(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails)principal).getUsername();
        } else {
            email = principal.toString();
        }
        model.addAttribute("email", email);
        return "me";
    }

    @GetMapping("/employees")
    public String getEmployeesOfDepartment(Model model, Pageable pageable, Sort sort) {
        Page<User> page = new PageImpl<>(Collections.emptyList());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) authentication.getAuthorities();
        String role = authorities.get(0).getAuthority();
        if (role.equals("ADMIN")) {
            page = this.userService.getUsers(pageable);
        } else if (role.equals("MANAGER")) {
            User user = getActiveUser();
            page = this.departmentService.getDepartmentByManager(user.getUsername())
                    .map(d -> this.departmentService.getEmployeesByDepartment(d.getId(), pageable))
                    .orElse(new PageImpl<>(Collections.emptyList()));
        }

        model.addAttribute("email", getActiveUser().getUsername());
        model.addAttribute("employees", page.getContent());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("pageNumber", page.getNumber());
        model.addAttribute("hasNextPage", page.hasNext());
        model.addAttribute("hasPrevPage", page.hasPrevious());

        return "management";
    }

    @RequestMapping(value = "/employees/{email}", method = RequestMethod.GET)
    public String deleteEmployee(@PathVariable String email,
                                 Pageable pageable,
                                 Sort sort,
                                 Model model) {
        this.userService.deleteUserByEmail(email);
        return getEmployeesOfDepartment(model, pageable, sort);
    }

    private User getActiveUser() {
        return (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
