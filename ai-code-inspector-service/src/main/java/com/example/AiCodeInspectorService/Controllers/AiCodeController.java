package com.example.AiCodeInspectorService.Controllers;

import com.example.AiCodeInspectorService.Models.AiCodeRequest;
import com.example.AiCodeInspectorService.Models.AiCodeResponse;
import com.example.AiCodeInspectorService.Models.AiCodeTestRequest;
import com.example.AiCodeInspectorService.Models.AiCodeTestResponse;
import com.example.AiCodeInspectorService.Services.AiCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("api/v1/ai-code")
public class AiCodeController {

    @Autowired
    private AiCodeService aiCodeService;

    @PostMapping()
    public AiCodeResponse getAiCode(@RequestBody AiCodeRequest aiCodeRequest){
        AiCodeResponse response = aiCodeService.getAiCode(aiCodeRequest);
        return response;
    }

    @PostMapping("/test")
    public AiCodeTestResponse getAiCode(@RequestBody AiCodeTestRequest aiCodeTestRequest){
        AiCodeTestResponse response = aiCodeService.getAiCodeTestResults(aiCodeTestRequest);
        return response;
    }

}
