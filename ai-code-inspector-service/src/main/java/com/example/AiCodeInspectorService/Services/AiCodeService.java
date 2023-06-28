package com.example.AiCodeInspectorService.Services;

import com.example.AiCodeInspectorService.Models.*;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This service gets and tests the AI generated code from OpenAI
 */
@Service
public class AiCodeService implements IAiCodeService {

    @Value("${api_key}")
    private String key;

    /**
     * This function reaches out to the OpenAI endpoint to get AI generated code
     *
     * @param aiCodeRequest
     * @return aiCodeResponse
     */
    public AiCodeResponse getAiCode(AiCodeRequest aiCodeRequest){

        String code = getCodeFromOpenAi(aiCodeRequest.getPrompt());

        code = stripWordsBeforeActualCode(code);

        AiCodeResponse aiCodeResponse = new AiCodeResponse();
        aiCodeResponse.setCode(code);

        int numberOfInputs = getNumberOfInputsFromCode(code);
        aiCodeResponse.setNumberOfInputs(numberOfInputs);

        return aiCodeResponse;

    }

    /**
     * This function test the AI generated code by running the user built scenario
     *
     * @param aiTestCodeRequest
     * @return aiTestCodeResponse
     */
    public AiTestCodeResponse getAiCodeTestResults(AiTestCodeRequest aiTestCodeRequest){

        final String DIRECTORY = "testFiles";
        final String RESULTS_FILE_EXTENSION = ".results";

        File createdFile = createTestFile(DIRECTORY);

        if(!createdFile.exists()){
            return null;
        }

        boolean wroteToFile = writeCodeToFile(createdFile,
                aiTestCodeRequest.getCode().trim(),
                aiTestCodeRequest.getInputs());

        if(!wroteToFile){
            return null;
        }

        File resultsFile = compileJavaFile(createdFile, RESULTS_FILE_EXTENSION);

        if(!resultsFile.exists()){
            return null;
        }

        boolean result = compareTests(aiTestCodeRequest.getOutput(), resultsFile);

        AiTestCodeResponse aiTestCodeResponse = new AiTestCodeResponse();
        aiTestCodeResponse.setScenarioResults(result);

        fileCleanUp(createdFile, resultsFile);

        return aiTestCodeResponse;

    }

    /**
     * This function strips all the text befor the code from the OpenAI endpoint
     *
     * @param code
     * @return newCode
     */
    private String stripWordsBeforeActualCode(String code){
        String newCode = "";
        String[] splitPublic = code.split("public ");
        if(splitPublic.length > 1){
            newCode = "public " + splitPublic[splitPublic.length - 1];
        }else{
            String[] splitPrivate = code.split("private ");
            newCode = "private " + splitPrivate[splitPrivate.length - 1];
        }
        return newCode == "" ? code : newCode;
    }

    /**
     * This function gets the number of inputs from the AI generated code
     *
     * @param code
     * @return methodInputTypes.length
     */
    private int getNumberOfInputsFromCode(String code){
        String method = getMethodFromCode(code);
        String[] methodInputTypes = getListOfMethodInputTypes(method);
        return methodInputTypes.length;
    }

    /**
     * This function compares the results from the generated AI code and user inputs with the user's expected output
     *
     * @param expectedOutput
     * @param results
     * @return
     */
    private boolean compareTests(String expectedOutput, File results){
        try {
            Scanner myReader = new Scanner(results);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                if(expectedOutput.equals(line)){
                    return true;
                }else{
                    try{
                        double convertedToDouble = Double.parseDouble(expectedOutput);
                        String convertBackToString = Double.toString(convertedToDouble);
                        if(convertBackToString.equals(line)){
                            return true;
                        }
                    }catch(Exception error){
                        System.out.println(error);
                    }
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This function deletes the files generated during testing the AI generated code
     *
     * @param javaFile
     * @param resultsFile
     */
    private void fileCleanUp(File javaFile, File resultsFile){
        String fullPath = javaFile.getAbsolutePath();
        String classFileName = fullPath.replace(".java", ".class");
        File classFile = new File(classFileName);
        classFile.delete();
        javaFile.delete();
        resultsFile.delete();
    }

    /**
     * This function compiles the Java file created to test the AI generated code
     *
     * @param file
     * @param resultsFileExtension
     * @return resultsFile
     */
    private File compileJavaFile(File file, String resultsFileExtension) {
        String osName = getOsName().toLowerCase();
        File resultsFile = null;

        if(osName.contains("mac")){
            ProcessBuilder processBuilder =
                    new ProcessBuilder("javac", file.getAbsolutePath());
            processBuilder.redirectErrorStream(true);
            startProcess(processBuilder, null);
            resultsFile = runProgram(file, resultsFileExtension);
        }else if(osName.contains("win")){
            return null;
        }else if(osName.contains("lin")){
            return null;
        }else{
            return null;
        }
        return resultsFile;
    }

    /**
     * This function runs the compiled Java file to generate the results file
     *
     * @param file
     * @param resultsFileExtension
     * @return resultsFile
     */
    private File runProgram(File file, String resultsFileExtension){
        String fileNameWithoutExtension = file.getName().replace(".java", "");
        String parentDirectory = getParentDirectory(file);
        ProcessBuilder processBuilder =
                new ProcessBuilder("java", "-cp", parentDirectory, fileNameWithoutExtension);
        File resultsFile = createResultFile(file, resultsFileExtension);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(resultsFile));
        startProcess(processBuilder, resultsFile);
        return resultsFile;
    }

    /**
     * This function starts the process from the ProcessBuilder passed in
     *
     * @param processBuilder
     * @param logFile
     */
    private void startProcess(ProcessBuilder processBuilder, File logFile){
        try {
            Process process = processBuilder.start();
            assert processBuilder.redirectInput() == ProcessBuilder.Redirect.PIPE;
            if(logFile != null) {
                assert processBuilder.redirectOutput().file() == logFile;
            }
            assert process.getInputStream().read() == -1;
            waitForProcessToComplete(process);
        }catch(IOException error){
            System.out.println(error);
        }
    }

    /**
     * This function waits for the process to complete before continuing
     *
     * @param process
     */
    private void waitForProcessToComplete(Process process){
        // Wait for the process to complete before running the program
        boolean processComplete = false;
        while(!processComplete) {
            try {
                process.waitFor();
                processComplete = true;
            }catch(InterruptedException error){
                System.out.println(error);
            }
        }
    }

    /**
     * This function creates the results file needed to write the output from the compiled Java file
     *
     * @param file
     * @param resultsExtension
     * @return resultsFile
     */
    private File createResultFile(File file, String resultsExtension){
        String filePathWithoutExtension = file.getPath().replace(".java", resultsExtension);
        return new File(filePathWithoutExtension);
    }

    /**
     * This function gets the parent directory for the file passed in
     *
     * @param file
     * @return
     */
    private String getParentDirectory(File file){
        String[] splitFile = file.getPath().split("/");
        return splitFile[0];
    }

    /**
     * This function gets the OS name where the server is running
     *
     * @return String
     */
    private String getOsName(){
        return System.getProperty("os.name");
    }

    /**
     * This function writes the code to the file along with the user inputs
     *
     * @param file
     * @param code
     * @param inputs
     * @return boolean
     */
    private boolean writeCodeToFile(File file, String code, Input[] inputs){
        String fileName = file.getName();
        String fileNameWithoutExtension = fileName.replace(".java", "");
        code = validateCode(code);
        String method = getMethodFromCode(code);
        String methodName = getMethodName(method);
        String[] methodInputTypes = getListOfMethodInputTypes(method);
        List<String[]> actualInputs = getActualInputs(inputs, methodInputTypes);
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

    /**
     * This function takes in the AI generated code and validates the function is static, if not, the function makes the
     * code static
     *
     * @param code
     * @return String
     */
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

    /**
     * This function generates a list of all possible inputs by compareing all combinations of user inputs and the actual
     * input types from the code
     *
     * @param inputs
     * @param methodInputTypes
     * @return List<String>[]
     */
    private List<String[]> getActualInputs(Input[] inputs, String[] methodInputTypes){

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
                        case "long":
                            Long.parseLong(combination[i]);
                            break;
                        default:
                            combination[i] = "\"" + combination[i] + "\"";
                    }
                }
                actualCombinations.add(combination);
            }catch (Exception error){
                System.out.print(error);
            }
        }
        return actualCombinations;
    }

    /**
     * This function writes the System.out.print lines which contain the method name with the user inputs to the Java
     * file
     *
     * @param myWriter
     * @param methodName
     * @param actualInputs
     */
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

    /**
     * This function generates all the user input different combinations so the user does not have to worry about order
     *
     * @param inputs
     * @return List<String[]>
     */
    private static List<String[]> generateDifferentCombinations(Input[] inputs){

        List<String[]> result = new ArrayList<>();
        if(inputs.length == 1){
            String getValue = inputs[0].getValue();
            String[] tmpArray = {getValue};
            result.add(tmpArray);
            return result;
        }

        String[] inputValues = new String[inputs.length];
        int index = 0;
        for(Input input : inputs){
            inputValues[index] = input.getValue();
            index++;
        }
        if(inputValues.length == 2){
            result.add(inputValues);
            String[] tmp = {inputValues[1], inputValues[0]};
            result.add(tmp);
        }
        else {
            for (int i = 0; i < inputValues.length; i++) {
                for (int j = 1; j <= inputValues.length - 1; j++) {
                    String[] copyArray = inputValues.clone();
                    result.add(copyArray);
                    inputValues = swap(1, inputValues);
                }
                inputValues = swap(0, inputValues);
            }
        }
        return result;
    }

    /**
     * This funtion swaps the order of the array in a queue to help get the different combinations
     *
     * @param startingIndex
     * @param array
     * @return String[]
     */
    private static String[] swap(int startingIndex, String[] array){
        String tmp = array[startingIndex];
        for(int k = startingIndex; k < array.length - 1; k++){
            array[k] = array[k + 1];
        }
        array[array.length - 1] = tmp;
        return array;
    }

    /**
     * This function gets the list of input types from the method to later validate the user inputs
     *
     * @param method
     * @return inputTypes
     */
    private String[] getListOfMethodInputTypes(String method){
        String[] methodSplit = method.split("\\(");
        String[] allInputs = methodSplit[1].split("\\)");
        String[] splitInputs = allInputs[0].split(",");
        int maxArrayNumber = splitInputs.length;
        String[] inputTypes = new String[maxArrayNumber];
        for(int i = 0; i < inputTypes.length; i++) {
            String[] inputs = splitInputs[i].trim().split("\\s+");
            inputTypes[i] = inputs[0];
        }
        return inputTypes;
    }

    /**
     * This function gets the name of the method
     *
     * @param method
     * @return String
     */
    private String getMethodName(String method){
        String[] splitMethod = method.split("\\s+|\\(");
        return splitMethod[3];
    }

    /**
     * This function gets the method line from the code
     * @param code
     * @return String
     */
    private String getMethodFromCode(String code){
        String[] splitAiCode = code.split("\\{");
        return splitAiCode[0];
    }

    /**
     * This function creates the test file that will be written to and compiled
     *
     * @param directory
     * @return File
     */
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

    /**
     * This function creates the testing file directory (if not already created) where all the created files will be
     *
     * @param directoryName
     */
    private void createTestFileirectory(String directoryName){
        File directory = new File(directoryName);
        if(!directory.exists()){
            directory.mkdir();
        }
    }

    /**
     * This function gets the code from the OpenAI endpoint
     *
     * @param prompt
     * @return String
     */
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
