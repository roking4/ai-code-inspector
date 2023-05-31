package com.example.AiCodeInspectorService.Services;

import com.example.AiCodeInspectorService.Models.AiCodeRequest;
import com.example.AiCodeInspectorService.Models.AiCodeResponse;

public interface IAiCodeService {
    public AiCodeResponse getAiCodeAndResults(AiCodeRequest aiCodeRequest);

}
