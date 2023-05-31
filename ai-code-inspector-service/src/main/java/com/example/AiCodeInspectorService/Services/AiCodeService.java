package com.example.AiCodeInspectorService.Services;

import com.example.AiCodeInspectorService.Models.AiCodeRequest;
import com.example.AiCodeInspectorService.Models.AiCodeResponse;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiCodeService implements IAiCodeService {

    @Value("${api_key}")
    private String key;

    public AiCodeResponse getAiCodeAndResults(AiCodeRequest aiCodeRequest){

        getCodeFromOpenAi(aiCodeRequest.getPrompt());

        return new AiCodeResponse();

    }

    private String getCodeFromOpenAi(String prompt){
        OpenAiService service = new OpenAiService(key);
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("ada")
                .maxTokens(1000)
                .echo(false)
                .build();
        CompletionChoice code = service.createCompletion(completionRequest).getChoices().get(0);
        return code.getText();
    }

}
