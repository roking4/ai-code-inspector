import './EnterTests.css';
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import AiCodeService from "../../Services/AiCodeService";
import Box from "@mui/material/Box";
import CircularProgress from "@mui/material/CircularProgress";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";

function EnterTests(props) {

    const navigate = useNavigate();
    const [code, setCode] = useState();
    const [defaultInputArray, setDefaultInputArray] = useState([]);
    const [formScenarios, setFormScenarios] = useState([]);

    useEffect(() => {
        if(!props.prompt){
            navigate('/')
        }
        getAiCode()
    }, []);

    const getAiCode = () => {
        AiCodeService.getAiCode(props.prompt)
            .then((response) => {
                const data = response.data;
                const inputKeys = getDefaultInputs(data.numberOfInputs);
                setCode(response.data.code);
                setDefaultInputArray(inputKeys);
                setFormScenarios([
                    {
                        index: 0,
                        inputs: inputKeys,
                        output: ""
                    }
                ]);
            }).catch((error) => {
            console.log(error);
        });
    }

    const getDefaultInputs = (numberOfInputs) => {
        let defaultInputArray = [];
        for(let i = 0; i < numberOfInputs; i++){
            defaultInputArray.push({
                id: i,
                value: ""
            });
        }
        return defaultInputArray;
    };

    const handleRegenerateCode = (e) => {
        e.preventDefault();
        setCode(undefined);
        getAiCode();
    }

    const handleEnterNewPrompt = (e) => {
        e.preventDefault();
        navigate("/");
    }



    const handleInputsChange = (e) => {
        e.preventDefault();
        const newValue = e.target.value;
        const splitString = e.target.name.split("-");
        const scenarioIndex = parseInt(splitString[0]);
        const inputIndex = parseInt(splitString[1]);
        changeInputValue(scenarioIndex, inputIndex, newValue);
    };

    const changeInputValue = (scenarioIndex, inputIndex, newValue) => {
        const oldInputsArray = formScenarios[scenarioIndex].inputs;
        const newInputsArray = oldInputsArray.map((input) => {
            if(oldInputsArray.indexOf(input) === inputIndex){
                return { id: input.id, value: newValue };
            }
            else{
                return input;
            }
        });
        const newScenarios = formScenarios.map((scenario) => {
            if (scenario.index === scenarioIndex){
                return {
                    index: scenario.index,
                    inputs: newInputsArray,
                    output: scenario.output
                }
            }
            else{
                return scenario;
            }
        });
        setFormScenarios(newScenarios);
    };

    const changeOutputValue = (scenarioIndex, newValue) => {
        const newScenarios = formScenarios.map((scenario) => {
            if (scenario.index === scenarioIndex){
                return {
                    index: scenario.index,
                    inputs: scenario.inputs,
                    output: newValue
                };
            }
            else{
                return scenario;
            }
        });
        setFormScenarios(newScenarios);
    };

    const addScenario = () => {
        const newScenario = {
            index: formScenarios.length,
            inputs: defaultInputArray,
            output: ""
        };
        setFormScenarios([...formScenarios, newScenario]);
    };

    const addInputField = (index) => {
        const oldInputsArray = formScenarios[index].inputs;
        const newIndex = oldInputsArray.length
        const newInputsArray = [...oldInputsArray, { id: newIndex, value: "" }];
        const newScenarios = formScenarios.map((scenario) => {
            if (scenario.index === index){
                return {
                    index: scenario.index,
                    inputs: newInputsArray,
                    output: scenario.output
                }
            }
            else{
                return scenario;
            }
        });
        setFormScenarios(newScenarios);
    };

    const handleOutputChange = (e) => {
        e.preventDefault();
        const newValue = e.target.value;
        const splitString = e.target.name.split("-");
        const scenarioIndex = parseInt(splitString[1]);
        changeOutputValue(scenarioIndex, newValue);
    };

    const handleRunTestsButton = (e) => {
        e.preventDefault();
        props.setScenarios(formScenarios);
        props.setAiCode(code);
        navigate('/results');
    };

    const handleCopyToClipBoard = () => {
        navigator.clipboard.writeText(code);
    };

    return (
        <div>
            {
                code === undefined ?
                    <Box sx={{ display: 'flex', justifyContent: 'center', alignContent: 'center' }}>
                        <CircularProgress />
                    </Box>
                    :
                    <div>
                        <p>{code}</p>
                        <ContentCopyIcon onClick={ handleCopyToClipBoard } />
                        <Button className={ "add-button" } onClick={ handleRegenerateCode } variant="contained">Regenerate Code</Button>
                        <Button className={ "add-button" } onClick={ handleEnterNewPrompt } variant="contained">Enter New Prompt</Button>
                    </div>
            }
            {
                formScenarios.map((scenario) =>
                    <div key={ "scenario-container" + scenario.index } className={"scenario-container"}>
                        <h2 key={ "scenario-header" + scenario.index }>Test Scenario { scenario.index + 1 }</h2>
                        <div key={ "input-container" + scenario.index }>
                            {
                                scenario.inputs.map((input) =>
                                    <div key={ "input" + scenario.index + input.id }>
                                        <TextField
                                            key={ "textField" + scenario.index + input.id }
                                            className={ "input" }
                                            id="outlined-basic"
                                            label="Expected Input"
                                            variant="outlined"
                                            value={ input.value }
                                            name={ scenario.index + '-' + input.id }
                                            onChange={ handleInputsChange }
                                        />
                                    </div>
                                )
                            }
                        </div>
                        <div key={ "output-container" + scenario.index }>
                            <TextField
                                key={ "output-textfield" + scenario.index }
                                className={ "input" }
                                id="outlined-basic"
                                label="Expected Output"
                                variant="outlined"
                                value={ scenario.output }
                                name={ "output-" + scenario.index }
                                onChange={ handleOutputChange }
                            />
                        </div>
                    </div>
                )
            }
        <Button className={ "add-button" } onClick={ addScenario } variant="contained">Add Scenario</Button>
        <Button className={ "add-button" } onClick={ handleRunTestsButton } variant="contained">Run Tests</Button>
        </div>
    );
}

export default EnterTests;
