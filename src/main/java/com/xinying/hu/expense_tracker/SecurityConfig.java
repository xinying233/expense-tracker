package com.xinying.hu.expense_tracker;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
                .logoutUrl("/logout") // Specify the logout URL
                .logoutSuccessUrl("/user/index") // Redirect to login page after logout
                .invalidateHttpSession(true) // Invalidate the session
                .deleteCookies("JSESSIONID"); // Remove the session cookie

        return http.build();
    }

}
