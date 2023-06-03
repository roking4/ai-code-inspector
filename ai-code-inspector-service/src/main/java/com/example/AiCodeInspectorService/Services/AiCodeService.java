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
import java.util.ArrayList;
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
        boolean createdFile = writeCodeToFile(fileName,
                aiCodeTestRequest.getCode(),
                aiCodeTestRequest.getInputs(),
                aiCodeTestRequest.getOutputs());

        if(!createdFile){
            return null;
        }

        AiCodeTestResponse aiCodeTestResponse = new AiCodeTestResponse();
        aiCodeTestResponse.setScenarioResults(true);

        return aiCodeTestResponse;

    }

    private boolean writeCodeToFile(String fileName, String code, String[] inputs, String[] outputs){
        String fileNameWithoutExtension = fileName.replace(".java", "");
        String method = getMethodFromCode(code);
        String methodName = getMethodName(method);
        List<String[]> actualInputs = getActualInputs(inputs, method);
        if(actualInputs.size() == 0){
            return false;
        }
        try {
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write("public class " + fileNameWithoutExtension + " {" +
                    "\npublic static void main(String[] args) {" +
                    "\n");
            myWriter.write("System.out.println(" + methodName + ");\n");
            writeLinesToFile();
            myWriter.write("}");
            myWriter.write("\n" +  code);
            myWriter.write("\n}");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file.");
            e.printStackTrace();
        }
        return false;
    }

    private List<String[]> getActualInputs(String[] inputs, String method){
        List<String[]> differentCombinations = generateDifferentCombinations(inputs);
        String[] methodInputTypes = getListOfMethodInputTypes(method);
        List<String[]> actualCombinations = new ArrayList<>();
        if(methodInputTypes.length != inputs.length){
            return actualCombinations;
        }
        for(String[] combination : differentCombinations){
            try{
                for(int i = 0; i < combination.length; i++){
                    switch(methodInputTypes[i]){
                        case "int":
                            Integer.parseInt(combination[i]);
                            break;
                        case "double":
                            Double.parseDouble(combination[i]);
                            break;
                        case "boolean":
                            Boolean.parseBoolean(combination[i]);
                            break;
                        case "float":
                            Float.parseFloat(combination[i]);
                            break;
                    }
                }
                actualCombinations.add(combination);
            }catch (Exception e){

            }
        }
        return actualCombinations;
    }

    private void writeLinesToFile(){

    }

    private static List<String[]> generateDifferentCombinations(String[] arr){
        List<String[]> result = new ArrayList<>();
        if(arr.length == 2){
            result.add(arr);
            String[] tmp = {arr[1], arr[0]};
            result.add(tmp);
        }
        else {
            for (int i = 0; i < arr.length; i++) {
                for (int j = 1; j <= arr.length - 1; j++) {
                    String[] copyArray = arr.clone();
                    result.add(copyArray);
                    arr = swap(1, arr);
                }
                arr = swap(0, arr);
            }
        }
        return result;
    }

    private static String[] swap(int startingIndex, String[] array){
        String tmp = array[startingIndex];
        for(int k = startingIndex; k < array.length - 1; k++){
            array[k] = array[k + 1];
        }
        array[array.length - 1] = tmp;
        return array;
    }

    private String[] getListOfMethodInputTypes(String method){
        String[] methodSplit = method.split("\\s+|\\(|\\)");
        int maxArrayNumber = (methodSplit.length - 3) / 2;
        String[] inputTypes = new String[maxArrayNumber];
        int j = 3;
        for(int i = 0; i < inputTypes.length; i++) {
            inputTypes[i] = methodSplit[j];
            j = j + 2;
        }
        return inputTypes;
    }

    private String getMethodName(String method){
        String[] splitMethod = method.split("\\s+|\\(");
        return splitMethod[2];
    }

    private String getMethodFromCode(String code){
        String[] splitAiCode = code.split("\\{");
        return splitAiCode[0];
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
