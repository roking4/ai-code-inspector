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
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

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

        final String DIRECTORY = "testFiles";

        File createdFile = createTestFile(DIRECTORY);

        if(!createdFile.exists()){
            return null;
        }

        boolean wroteToFile = writeCodeToFile(createdFile,
                aiCodeTestRequest.getCode(),
                aiCodeTestRequest.getInputs(),
                aiCodeTestRequest.getOutputs());

        if(!wroteToFile){
            return null;
        }

        String[] compiled = compileJavaFile(createdFile);

        AiCodeTestResponse aiCodeTestResponse = new AiCodeTestResponse();
        aiCodeTestResponse.setScenarioResults(true);

        return aiCodeTestResponse;

    }

    private String[] compileJavaFile(File file) {

        String osName = getOsName().toLowerCase();

        if(osName.contains("mac")){
            ProcessBuilder pb =
                    new ProcessBuilder("javac", file.getAbsolutePath());
            File log = new File("testFiles/log");
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
            try {
                Process p = pb.start();
                assert pb.redirectInput() == ProcessBuilder.Redirect.PIPE;
                assert pb.redirectOutput().file() == log;
                assert p.getInputStream().read() == -1;

                // Wait for the process to complete before running the program
                boolean processComplete = false;
                while(!processComplete) {
                    try {
                        p.waitFor();
                        processComplete = true;
                    }catch(InterruptedException error){

                    }
                    runProgram(file);
                }

            }catch(IOException error){
                System.out.println(error);
            }
        }else if(osName.contains("win")){

        }else if(osName.contains("lin")){

        }else{
            return null;
        }

        return null;
    }

    private void runProgram(File file){
        String filePathWithoutExtension = file.getName().replace(".java", "");
        String parentDirectory = getParentDirectory(file);
        ProcessBuilder pb2 =
                new ProcessBuilder("java", "-cp", parentDirectory, filePathWithoutExtension);
        File log2 = new File("testFiles/log2");
        pb2.redirectErrorStream(true);
        pb2.redirectOutput(ProcessBuilder.Redirect.appendTo(log2));
        try {
            Process p2 = pb2.start();
            assert pb2.redirectInput() == ProcessBuilder.Redirect.PIPE;
            assert pb2.redirectOutput().file() == log2;
            assert p2.getInputStream().read() == -1;
        }catch(IOException error){
            System.out.println(error);
        }
    }

    private String getParentDirectory(File file){
        String[] splitFile = file.getPath().split("/");
        return splitFile[0];
    }

    private String getOsName(){
        return System.getProperty("os.name");
    }

    private boolean writeCodeToFile(File file, String code, String[] inputs, String[] outputs){
        String fileName = file.getName();
        String fileNameWithoutExtension = fileName.replace(".java", "");
        String method = getMethodFromCode(code);
        String methodName = getMethodName(method);
        String[] methodInputTypes = getListOfMethodInputTypes(method);
        code = validateCode(code);
        List<String[]> actualInputs = getActualInputs(inputs, method, methodInputTypes);
        if(actualInputs.size() == 0){
            return false;
        }
        try {
            FileWriter myWriter = new FileWriter(file.getAbsolutePath());
            myWriter.write("public class " + fileNameWithoutExtension + " {" +
                    "\npublic static void main(String[] args) {" +
                    "\n");

            writeLinesToFile(myWriter, methodName, actualInputs);
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

    private String validateCode(String code){
        String[] splitCode = code.split("\\s+");
        String newCode = "";

        if(splitCode[0].equalsIgnoreCase("public") && !splitCode[1].equalsIgnoreCase("static")){
            newCode = code.replace("public ", "public static ");
        } else if (splitCode[0].equalsIgnoreCase("private") && !splitCode[1].equalsIgnoreCase("static")) {
            newCode = code.replace("private ","private static ");
        }else {
            return code;
        }

        return newCode;
    }

    private List<String[]> getActualInputs(String[] inputs, String method, String[] methodInputTypes){
        List<String[]> differentCombinations = generateDifferentCombinations(inputs);

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

    private void writeLinesToFile(FileWriter myWriter, String methodName,  List<String[]> actualInputs){
        for( String[] actualInput : actualInputs) {
            String inputs = "";
            for (int i = 0; i < actualInput.length; i++) {
                inputs += actualInput[i];
                if(i < actualInput.length - 1){
                    inputs += ", ";
                }
            }
            try {
                myWriter.write("System.out.println(" + methodName + "(" + inputs + "));\n");
            } catch (IOException error) {
                System.out.println(error);
            }
        }
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
        return splitMethod[3];
    }

    private String getMethodFromCode(String code){
        String[] splitAiCode = code.split("\\{");
        return splitAiCode[0];
    }

    private File createTestFile(String directory){
        int fileNumber = 0;
        String fileName = "";
        boolean isFileCreated = false;
        File file = new File("");
        createTestFileirectory(directory);
        try {
            do {
                fileName = "test" +  fileNumber + ".java";
                file = new File(directory, fileName);
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
        return file;
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
