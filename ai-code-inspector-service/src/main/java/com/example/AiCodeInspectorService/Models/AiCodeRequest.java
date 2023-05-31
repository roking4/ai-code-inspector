package com.example.AiCodeInspectorService.Models;

import lombok.Getter;
import lombok.Setter;

public class AiCodeRequest {

    @Getter
    @Setter
    private String Prompt;

    @Getter
    @Setter
    private String[] Inputs;

    @Getter
    @Setter
    private String[] Outputs;
}
