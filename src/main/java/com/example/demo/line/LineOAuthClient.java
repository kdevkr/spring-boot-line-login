package com.example.demo.line;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.spring.boot.LineBotProperties;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Slf4j
@Component
public class LineOAuthClient {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    private final LineOAuthProperties lineOAuthProperties;
    private final LineBotProperties lineBotProperties;

    public LineTokenResponse getToken(LineCodeResponse lineCodeResponse) {
        String code = lineCodeResponse.getCode();
        if (StringUtils.hasText(code)) {
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("grant_type", "authorization_code");
            parameters.add("code", code);
            parameters.add("redirect_uri", lineOAuthProperties.getCallbackUrl());
            parameters.add("client_id", lineOAuthProperties.getChannelId());
            parameters.add("client_secret", lineOAuthProperties.getChannelSecret());

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                HttpEntity<?> formData = new HttpEntity<>(parameters, headers);
                ResponseEntity<LineTokenResponse> responseEntity = restTemplate.postForEntity(lineBotProperties.getApiEndPoint() + "/oauth2/v2.1/token", formData, LineTokenResponse.class);
                LineTokenResponse response = responseEntity.getBody();

                if (response == null) {
                    return null;
                }

                String idToken = response.getIdToken();
                String payload = idToken.split("\\.")[1];
                Map<String, Object> claims = objectMapper.readValue(new String(Base64.getUrlDecoder().decode(payload)), new TypeReference<>(){});
                boolean isFriendship = isFriendship(response.getAccessToken());
                claims.put("isFriendship", isFriendship);

                response.setClaims(claims);
                return response;
            } catch (Exception e) {
                log.error("[IdToken]", e);
            }
        }
        return null;
    }

    private boolean isFriendship(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken));
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        String url = lineBotProperties.getApiEndPoint() + "/friendship/v1/status ";
        ResponseEntity<Map<String, Boolean>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {
        });
        Map<String, Boolean> response = responseEntity.getBody();
        return response != null && response.getOrDefault("friendFlag", false);
    }
}
