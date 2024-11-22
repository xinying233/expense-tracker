package com.xinying.hu.expense_tracker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF if not needed
                .authorizeHttpRequests()
                .requestMatchers("/login", "/register").permitAll() // Public endpoints
                .anyRequest().authenticated() // Protect other endpoints
                .and()
                .httpBasic() // Use Basic Auth (or .formLogin() for form-based login)
                .and()
                .logout()
                .logoutUrl("/user/logout") // Specify the logout URL
                .logoutSuccessHandler((request, response, authentication) -> {
                    // Get the current host
                    String currentHost = request.getServerName() + ":" + request.getServerPort();
                    String scheme = request.getScheme();
                    String logoutRedirectUrl = scheme + "://invalidusername:invalidpassword@" + currentHost + "/user/index";

                    // Perform the redirect
                    response.sendRedirect(logoutRedirectUrl);
                })
                .invalidateHttpSession(true) // Invalidate the session
                .deleteCookies("JSESSIONID"); // Remove the session cookie

        return http.build();
    }

}
