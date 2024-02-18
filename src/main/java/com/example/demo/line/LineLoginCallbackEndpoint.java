package com.example.demo.line;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RequiredArgsConstructor
@Controller
@RequestMapping("/line/login")
public class LineLoginCallbackEndpoint {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LineMessagingClient lineMessagingClient;
    private final LineOAuthClient lineOAuthClient;

    @GetMapping()
    public View callback(@RequestParam Map<String, String> params,
                         RedirectAttributes redirectAttributes) {
        // NOTE: Remove query parameters forwarded from the line platform from the URL.
        RedirectView redirectView = new RedirectView();
        redirectView.setExposeModelAttributes(false);

        LineCodeResponse response = objectMapper.convertValue(params, LineCodeResponse.class);
        LineTokenResponse tokenResponse = lineOAuthClient.getToken(response);
        if (tokenResponse != null) {
            Map<String, Object> claims = tokenResponse.getClaims();
            claims.forEach(redirectAttributes::addFlashAttribute);

            if (Boolean.TRUE.equals(claims.get("isFriendship"))) {
                PushMessage pushMessage = new PushMessage((String) claims.get("sub"), new TextMessage(String.format("Hello! My friend, %s", claims.get("name"))));
                lineMessagingClient.pushMessage(pushMessage);
            }

            redirectView.setUrl("/line/login/complete");
            return redirectView;
        }


        redirectView.setUrl("/");
        return redirectView;
    }

    @GetMapping("/complete")
    public String complete(Model model) {
        return "line_login_completed";
    }
}
