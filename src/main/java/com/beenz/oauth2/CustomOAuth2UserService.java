package com.beenz.oauth2;


import com.beenz.entity.CustomUserDetails;
import com.beenz.entity.Role;
import com.beenz.entity.User;
import com.beenz.entity.UserRole;
import com.beenz.oauth2.userinfo.OAuth2UserInfo;
import com.beenz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.stream.Collectors;

// 인증정보를 통해 DB에 저장하기 위한 커스텀 OAuth2서비스
// https://www.callicoder.com/spring-boot-security-oauth2-social-login-part-2/ 를 참고함
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = Oauth2UserInfoFactory.getOAuth2UserInfo(
                userRequest.getClientRegistration().getRegistrationId()
                , oAuth2User.getAttributes()
        );

        // 메일정보가 없을 경우 예외발생
        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("OAuth2 provider로 부터 Email을 찾을 수 없습니다.");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if (userOptional.isPresent()) { // 이미 정보가 있는 경우는 update
            user = userOptional.get();
            // 가입된 AuthProvider와 다를경우 예외발생
            if (!user.getProvider()
                    .equals(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
        } else { // 아니면 insert
            user = registerNewUser(userRequest, oAuth2UserInfo);
        }
        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getUserRole().stream()
                        .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getKey()))
                        .collect(Collectors.toSet()),
                oAuth2UserInfo.getAttributes(),
                oAuth2UserInfo.getName());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                .email(oAuth2UserInfo.getEmail())
                .provider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))
                .build();
        UserRole userRole = new UserRole(Role.USER);
        user.addUserRole(userRole);
        return userRepository.save(user);
    }
}
