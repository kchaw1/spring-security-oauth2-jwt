package com.beenz.oauth2;

import com.beenz.oauth2.userinfo.GithubOAuth2UserInfo;
import com.beenz.oauth2.userinfo.GoogleOAuth2UserInfo;
import com.beenz.oauth2.userinfo.OAuth2UserInfo;

import java.util.Map;

public class Oauth2UserInfoFactory {

    /**
     * 도메인별로 인증정보 응답 구조가 다르기 때문에 각 도메인별로 OAuth2UserInfo에 맞게 처리해주는 부분
     * OAuth2UserInfo 는 추상클래스로 구현되어있고
     * 각 도메인별 XXXOAuth2UserInfo 객체는 OAuth2UserInfo를 상속받아
     * 도메인별 응답구조에 맞게 변환하는 코드를 구현한다.
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(AuthProvider.google.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.github.toString())) {
            return new GithubOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}
