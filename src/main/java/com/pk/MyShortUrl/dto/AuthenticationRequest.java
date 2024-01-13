package com.pk.MyShortUrl.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
// used by authetication controller (jwt token for restfull APIs)
// this is a dto used to transfer information for authentication  between client and server
// DTOs are used to encapsulate data
@Getter
@Setter
@NoArgsConstructor
public class AuthenticationRequest {
    private String username;
    private String password;

}
