package com.example.demo.line;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.Map;
import lombok.Data;

@Data
public class LineTokenResponse {
    @JsonAlias("access_token")
    private String accessToken;
    @JsonAlias("expires_in")
    private Number expiresIn;
    @JsonAlias("id_token")
    private String idToken;
    @JsonAlias("refresh_token")
    private String refreshToken;
    private String scope;
    @JsonAlias("token_type")
    private String tokenType;

    private Map<String, Object> claims;
}
