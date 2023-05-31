package com.example.AiCodeInspectorService.Controllers;

import com.example.AiCodeInspectorService.Models.AiCodeRequest;
import com.example.AiCodeInspectorService.Services.AiCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Constructor;

@RestController
@RequestMapping("api/v1/ai-code")
public class AiCodeController {

    @Autowired
    private AiCodeService aiCodeService;

    @GetMapping()
    public AiCodeRequest getAiCode(@RequestBody AiCodeRequest aiCodeRequest){
        aiCodeService.getAiCodeAndResults(aiCodeRequest);
        return aiCodeRequest;
    }

}
