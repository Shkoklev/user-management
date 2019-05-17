package com.eimt.usermanagement.config;

import com.eimt.usermanagement.model.Role;
import com.eimt.usermanagement.model.User;
import com.eimt.usermanagement.service.SsoIdentityService;
import com.eimt.usermanagement.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final SsoIdentityService ssoIdentityService;
    private final UserService userService;

    public OAuth2SuccessHandler(SsoIdentityService ssoIdentityService,
                                UserService userService) {
        this.ssoIdentityService = ssoIdentityService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
        Map<String, String> detalis = (Map<String, String>) oAuth2Authentication.getUserAuthentication().getDetails();

        String ssoId = String.valueOf(detalis.getOrDefault("id", ""));
        String email = detalis.getOrDefault("email","");
        String name = detalis.getOrDefault("name","");

        this.ssoIdentityService.findUser(ssoId)
                .orElseGet(() -> {
                    User u = this.userService.createUser(new User(
                            email,
                            null,
                            name,
                            null,
                            null,
                            null,
                            LocalDateTime.now(),
                            Role.EMPLOYEE,
                            null
                    ));
                    this.ssoIdentityService.createSsoIdentity(ssoId,u);
                    this.userService.activateUser(u);
                    return u;
                });
        response.sendRedirect("http://localhost:8080/me");
    }
}
