package com.example.AiCodeInspectorService.Services;

import com.example.AiCodeInspectorService.Models.*;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class AiCodeService implements IAiCodeService {

    @Value("${api_key}")
    private String key;

    public AiCodeResponse getAiCode(AiCodeRequest aiCodeRequest){

        var code = getCodeFromOpenAi(aiCodeRequest.getPrompt());
        AiCodeResponse aiCodeResponse = new AiCodeResponse();
        aiCodeResponse.setCode(code);

        return aiCodeResponse;

    }

    public AiCodeTestResponse getAiCodeTestResults(AiCodeTestRequest aiCodeTestRequest){

        String fileName = createTestFile();
        writeCodeToFile(fileName,
                aiCodeTestRequest.getCode(),
                aiCodeTestRequest.getInputs(),
                aiCodeTestRequest.getOutputs());

        AiCodeTestResponse aiCodeTestResponse = new AiCodeTestResponse();
        aiCodeTestResponse.setScenarioResults(true);

        return aiCodeTestResponse;

    }

    private void writeCodeToFile(String fileName, String code, String[] inputs, String[] outputs){
        String fileNameWithoutExtension = fileName.replace(".java", "");
        try {
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write("public class " + fileNameWithoutExtension + " {" +
                    "\npublic static void main(String[] args) {" +
                    "\n");
            myWriter.write("\n}");
            myWriter.write("\n" +  code);
            myWriter.write("\n}");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file.");
            e.printStackTrace();
        }

    }

    private String createTestFile(){
        int fileNumber = 0;
        String fileName = "";
        boolean isFileCreated = false;
        File file = new File("");
        String directoryName = "testFiles";
        createTestFileirectory(directoryName);
        try {
            do {
                fileName = "test" +  fileNumber + ".java";
                file = new File(directoryName, fileName);
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                    isFileCreated = true;
                } else {
                    System.out.println("File already exists.");
                    fileNumber++;
                }
            } while (!isFileCreated);
        } catch (IOException e) {
            System.out.println("An error occurred while creating a file.");
            e.printStackTrace();
        }
        return file.getPath();
    }

    private void createTestFileirectory(String directoryName){
        File directory = new File(directoryName);
        if(!directory.exists()){
            directory.mkdir();
        }
    }

    private String getCodeFromOpenAi(String prompt){
        OpenAiService service = new OpenAiService(key);
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("text-davinci-003")
                .maxTokens(1000)
                .echo(false)
                .build();
        CompletionChoice code = service.createCompletion(completionRequest).getChoices().get(0);
        return code.getText();
    }

}
