package com.example.AiCodeInspectorService.Services;

import com.example.AiCodeInspectorService.Models.AiCodeRequest;
import com.example.AiCodeInspectorService.Models.AiCodeResponse;
import com.example.AiCodeInspectorService.Models.AiCodeTestRequest;
import com.example.AiCodeInspectorService.Models.AiCodeTestResponse;

public interface IAiCodeService {
    public AiCodeResponse getAiCode(AiCodeRequest aiCodeRequest);
    public AiCodeTestResponse getAiCodeTestResults(AiCodeTestRequest aiCodeTestRequest);

}
