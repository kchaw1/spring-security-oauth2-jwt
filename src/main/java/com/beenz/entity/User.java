package com.beenz.entity;

import com.beenz.oauth2.AuthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserRole> userRole = new HashSet<>();

    @Builder
    public User(String email, String password, AuthProvider provider) {
        this.email = email;
        this.password = password;
        this.provider = provider;
    }

    public void addUserRole(UserRole... userRoles) {
        for (UserRole role : userRoles) {
            this.userRole.add(role);
            role.setUser(this);
        }
    }
}
