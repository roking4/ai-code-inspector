package com.example.AiCodeInspectorService.Controllers;

import com.example.AiCodeInspectorService.Models.AiCodeRequest;
import com.example.AiCodeInspectorService.Models.AiCodeResponse;
import com.example.AiCodeInspectorService.Services.AiCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Constructor;

@CrossOrigin("*")
@RestController
@RequestMapping("api/v1/ai-code")
public class AiCodeController {

    @Autowired
    private AiCodeService aiCodeService;

    @PostMapping()
    public AiCodeResponse getAiCode(@RequestBody AiCodeRequest aiCodeRequest){
        AiCodeResponse response = aiCodeService.getAiCodeAndResults(aiCodeRequest);
        return response;
    }

}
