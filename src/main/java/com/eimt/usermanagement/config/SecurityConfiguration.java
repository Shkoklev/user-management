package com.eimt.usermanagement.config;

import com.eimt.usermanagement.model.User;
import com.eimt.usermanagement.repository.UserRepository;
import com.eimt.usermanagement.service.UserService;
import com.eimt.usermanagement.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CompositeFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
@Configuration
@EnableWebMvc
@EnableOAuth2Client
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final OAuth2ClientContext oauth2ClientContext;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfiguration(@Qualifier("oauth2ClientContext") OAuth2ClientContext oauth2ClientContext,
                                 AuthenticationSuccessHandler authenticationSuccessHandler,
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 UserService userService) {
        this.oauth2ClientContext = oauth2ClientContext;
        this.userService = userService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .httpBasic().disable()
                .exceptionHandling();

        http
                .authorizeRequests()
                .antMatchers("/register").permitAll()
                .antMatchers("/registrationConfirm").permitAll()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/forgotPassword").permitAll()
                .antMatchers(HttpMethod.GET, "/employees").hasAnyAuthority("ADMIN", "MANAGER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/editProfile")
                .failureUrl("/login?error")
                .permitAll()
                .and()
                .logout()
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("remember-me")
                .deleteCookies("JSESSIONID")
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll()
                .and()
                .rememberMe()
                .key("uniqueAndSecret")
                .tokenValiditySeconds(86400)
                .and()
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }

    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    @Bean
    @ConfigurationProperties("github")
    public ClientResources github() {
        return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("facebook")
    public ClientResources facebook() {
        return new ClientResources();
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    private Filter ssoFilter() {
        CompositeFilter compositeFilter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(ssoFilter(facebook(), "/login/facebook"));
        filters.add(ssoFilter(github(), "/login/github"));
        compositeFilter.setFilters(filters);
        return compositeFilter;
    }

    private Filter ssoFilter(ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationFilter
                = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate oAuth2RestTemplate
                = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        oAuth2ClientAuthenticationFilter.setRestTemplate(oAuth2RestTemplate);
        UserInfoTokenServices tokenServices
                = new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getResource().getClientId());
        tokenServices.setRestTemplate(oAuth2RestTemplate);
        oAuth2ClientAuthenticationFilter.setTokenServices(tokenServices);
        oAuth2ClientAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        return oAuth2ClientAuthenticationFilter;
    }

}

