package com.example.AiCodeInspectorService.Controllers;

import com.example.AiCodeInspectorService.Models.AiCodeRequest;
import com.example.AiCodeInspectorService.Models.AiCodeResponse;
import com.example.AiCodeInspectorService.Models.AiTestCodeRequest;
import com.example.AiCodeInspectorService.Models.AiTestCodeResponse;
import com.example.AiCodeInspectorService.Services.AiCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("api/v1/ai-code")
public class AiCodeController {

    @Autowired
    private AiCodeService aiCodeService;

    /**
     * This takes in a AiCodeRequest that contains a string for the prompt and returns AI generated code from the service.
     *
     * @param aiCodeRequest
     * @return aiCodeResponse
     */
    @PostMapping()
    public AiCodeResponse getAiCode(@RequestBody AiCodeRequest aiCodeRequest){
        AiCodeResponse aiCodeResponse = aiCodeService.getAiCode(aiCodeRequest);
        return aiCodeResponse;
    }

    /**
     * This takes in the code to test and a scenario that contains an array of inputs and a singular output. The endpoint
     * returns a response that a boolean based on the pass or failure of the code.
     *
     * @param aiTestCodeRequest
     * @return aiTestCodeResponse
     */
    @PostMapping("/test")
    public AiTestCodeResponse getAiCode(@RequestBody AiTestCodeRequest aiTestCodeRequest){
        AiTestCodeResponse aiTestCodeResponse = aiCodeService.getAiCodeTestResults(aiTestCodeRequest);
        if(aiTestCodeResponse == null){
            aiTestCodeResponse = new AiTestCodeResponse();
            aiTestCodeResponse.setScenarioResults(false);
        }
        return aiTestCodeResponse;
    }

}
