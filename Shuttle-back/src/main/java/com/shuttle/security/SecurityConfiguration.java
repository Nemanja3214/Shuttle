package com.shuttle.security;

import com.shuttle.security.jwt.JwtTokenUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.shuttle.security.jwt.JwtRequestFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

//    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private JwtTokenUtil tokenUtils;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //will use email as username
        http.addFilterBefore(new JwtRequestFilter(tokenUtils, userDetailsService()), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint);
        http.headers().frameOptions().disable();
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> web.ignoring().requestMatchers(HttpMethod.POST, "/api/user/login","/api/unregisteredUser/**")
                .requestMatchers(HttpMethod.GET, "/**","/", "/webjars/**", "/*.html", "favicon.ico",
                        "/**/*.html", "/**/*.css", "/**/*.js");

    }

}
