//package com.mukesh.smarthealthmanagement.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//import com.mukesh.smarthealthmanagement.services.UserDetailsServiceImpl;
//
//@Configuration
//@EnableWebSecurity
//public class WebSecurityConfig {
//
//    @Autowired
//    private UserDetailsServiceImpl userDetailsService;
//
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
//            .authorizeHttpRequests(authorize -> authorize
//                .requestMatchers("/api/auth/**").permitAll() // Permit access to authentication endpoints
//                .anyRequest().authenticated() // All other requests require authentication
//            )
//            .formLogin(form -> form
//                .disable() // Disable form login
//            )
//            .logout(logout -> logout
//                .logoutUrl("/logout") // Specify the logout URL
//                .logoutSuccessUrl("/login?logout=true") // URL to redirect after logout
//                .invalidateHttpSession(true) // Invalidate the session on logout
//                .deleteCookies("JSESSIONID") // Delete cookies on logout
//            );
//        return http.build();
//    }
//
//    @Bean
//    AuthenticationManager authManager(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authenticationManagerBuilder = 
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder
//            .userDetailsService(userDetailsService)
//            .passwordEncoder(passwordEncoder());
//        return authenticationManagerBuilder.build();
//    }
//
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
package com.mukesh.smarthealthmanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.mukesh.smarthealthmanagement.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/api/auth/**").permitAll() // Permit access to authentication endpoints
                                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Only ADMIN role can access admin endpoints
                                .requestMatchers("/api/doctors/**", "/api/nurses/**", "/api/patients/**", "/api/medicalrecords/**,", "/api/appointments/**").hasAnyRole("ADMIN", "USER")
                                .anyRequest().authenticated() // All other requests require authentication
                )
                .formLogin(form -> form.disable()) // Disable form login
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "JSESSIONID")); // Adjust for JWT tokens or session handling

            
            
//            .logout(logout -> logout
//                .logoutUrl("/logout") // Specify the logout URL
//                .logoutSuccessUrl("/login?logout=true") // URL to redirect after logout
//                .invalidateHttpSession(true) // Invalidate the session on logout
//                .deleteCookies("JSESSIONID") // Delete cookies on logout
//            );
   return http.build();
    }

    @Bean
    AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

