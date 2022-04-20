package com.beenz;

import com.beenz.entity.Role;
import com.beenz.entity.User;
import com.beenz.entity.UserRole;
import com.beenz.oauth2.AuthProvider;
import com.beenz.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class InitDb {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void addUser() {
        User user = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("12345"))
                .provider(AuthProvider.local)
                .build();

        UserRole userRole = new UserRole(Role.USER);

        user.addUserRole(userRole);

        userRepository.save(user);
    }
}
