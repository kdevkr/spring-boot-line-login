package com.example.demo.line;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class LineErrorResponse {
    private String error;
    @JsonAlias("error_description")
    private String errorDescription;
}
