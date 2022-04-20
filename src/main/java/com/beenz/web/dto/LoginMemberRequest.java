package com.beenz.web.dto;

import lombok.Data;

@Data
public class LoginMemberRequest {

    private String username;
    private String password;
}
