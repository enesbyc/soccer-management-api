package com.soccer.management.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.soccer.management.service.impl.UserService;

/**
 * @author enes.boyaci
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userDetailsService;

    @Autowired
    private CustomJwtAuthenticationFilter customJwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private static final String[] AUTH_WHITELIST = {"/v2/api-docs", "/swagger-resources",
                                                    "/swagger-resources/**", "/configuration/ui",
                                                    "/configuration/security", "/webjars/**",
                                                    "/v3/api-docs/**", "/swagger-ui", "/api/auth**",
                                                    "/swagger-ui/**", "/authenticate", "/register"};

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                        .antMatchers("/transfer", "/team", "/player", "/user").authenticated().and()
                        .authorizeRequests().antMatchers(AUTH_WHITELIST).permitAll().and()
                        .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .and().sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                        .addFilterBefore(customJwtAuthenticationFilter,
                                         UsernamePasswordAuthenticationFilter.class);
    }

}
