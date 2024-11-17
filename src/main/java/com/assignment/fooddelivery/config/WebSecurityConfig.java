package com.assignment.fooddelivery.config;

import com.assignment.fooddelivery.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // Inject the entry point

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/delivery-agent/register").permitAll()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/delivery-agent/**").hasAnyRole("DELIVERY_AGENT", "ADMIN") // Restricting access
                .antMatchers("/api/customer/**").hasAnyRole("CUSTOMER", "ADMIN") // Restricting access
                .antMatchers("/api/delivery/**").hasRole("DELIVERY_AGENT") // Restricting access
                .antMatchers("/api/restaurant/owner/register").permitAll()
                .antMatchers("/api/restaurant/**").hasAnyRole("RESTAURANT_OWNER", "ADMIN") // Restricting access
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint); // Use custom entry point

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}