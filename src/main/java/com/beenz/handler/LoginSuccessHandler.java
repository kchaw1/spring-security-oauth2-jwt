package com.beenz.handler;

import com.beenz.entity.CustomUserDetails;
import com.beenz.util.JwtTokenProvider;
import com.beenz.web.dto.ApiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private final String LOGIN_SUCCESS_MESSAGE = "로그인되었습니다.";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        Set<SimpleGrantedAuthority> authorities = authentication.getAuthorities().stream().map(
                auth -> new SimpleGrantedAuthority(auth.getAuthority())
        ).collect(Collectors.toSet());

        String accessToken = jwtTokenProvider.createAccessToken(details.getId(), authorities);
        String refreshToken = jwtTokenProvider.createRefreshToken(details.getId(), authorities);
        String tokenPrefix = jwtTokenProvider.getTokenPrefix();

        Map<String, Object> data = new HashMap<>();
        data.put("tokenPrefix", tokenPrefix);
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);

        ApiResponseDto<Map<String,Object>> apiResponseDto = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                LOGIN_SUCCESS_MESSAGE,
                data
        );

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(apiResponseDto)
        );
    }
}
