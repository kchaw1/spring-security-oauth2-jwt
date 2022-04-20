package com.beenz.config;

import com.beenz.filter.UsernamePasswordAuthFilter;
import com.beenz.handler.LoginFailureHandler;
import com.beenz.handler.LoginSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String LOGIN_PROCESS_URL = "/api/v1/login";

    private final ObjectMapper objectMapper;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
//    private final JwtExceptionHandler jwtExceptionHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
//                .exceptionHandling().authenticationEntryPoint(jwtExceptionHandler)
//                .and()
                .addFilter(usernamePasswordAuthFilter())
                .oauth2Login()
                    .successHandler(loginSuccessHandler)
                    .failureHandler(loginFailureHandler);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/h2-console/**");
    }

    private UsernamePasswordAuthFilter usernamePasswordAuthFilter() throws Exception {
        UsernamePasswordAuthFilter filter = new UsernamePasswordAuthFilter(authenticationManager(), objectMapper);
        filter.setFilterProcessesUrl(LOGIN_PROCESS_URL);
        filter.setAuthenticationSuccessHandler(loginSuccessHandler);
        filter.setAuthenticationFailureHandler(loginFailureHandler);
        return filter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
