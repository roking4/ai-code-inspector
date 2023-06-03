package com.example.AiCodeInspectorService.Models;

import lombok.Getter;
import lombok.Setter;

public class AiCodeTestRequest {

    @Getter
    @Setter
    private String Code;

    @Getter
    @Setter
    private String[] Inputs;

    @Getter
    @Setter
    private String Output;

}
