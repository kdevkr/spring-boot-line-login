package com.example.demo.line;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class LineCodeResponse {
    private String code;
    private String state;
    @JsonAlias("friendship_status_changed")
    private Boolean friendshipStatusChanged;

    private String error;
    @JsonAlias("error_description")
    private String errorDescription;
}
