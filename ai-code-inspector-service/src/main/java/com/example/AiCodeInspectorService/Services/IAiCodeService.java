package com.example.AiCodeInspectorService.Services;

import com.example.AiCodeInspectorService.Models.AiCodeRequest;
import com.example.AiCodeInspectorService.Models.AiCodeResponse;
import com.example.AiCodeInspectorService.Models.AiTestCodeRequest;
import com.example.AiCodeInspectorService.Models.AiTestCodeResponse;

public interface IAiCodeService {
    public AiCodeResponse getAiCode(AiCodeRequest aiCodeRequest);
    public AiTestCodeResponse getAiCodeTestResults(AiTestCodeRequest aiTestCodeRequest);

}
