package com.beenz.service;

import com.beenz.entity.CustomUserDetails;
import com.beenz.entity.Role;
import com.beenz.entity.User;
import com.beenz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                Set.of(new SimpleGrantedAuthority(Role.USER.getKey())),
                null,
                user.getEmail()
        );
    }
}
