package com.beenz.oauth2.userinfo;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo{

    @Override
    public Map<String, Object> getAttributes() {
        return super.getAttributes();
    }

    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return ((Integer) attributes.get("id")).toString();
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }
}
