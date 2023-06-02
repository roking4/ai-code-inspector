import './HomeComponent.css'
import TextareaAutosize from '@mui/base/TextareaAutosize';
import { useState } from "react";
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';

function HomeComponent() {

    const [formPrompt, setFormPropmt] = useState("I would like a java function that ");
    const [scenarios, setScenarios] = useState([
        {
            index: 0,
            inputs: [""],
            outputs: [""]
        }
    ]);

    const handlePromptChange = (e) => {
        setFormPropmt(e.target.value);
    };

    const handleInputsChange = (e) => {
        e.preventDefault();
        const newValue = e.target.value;
        const splitString = e.target.name.split("-");
        const scenarioIndex = parseInt(splitString[0]);
        const inputIndex = parseInt(splitString[1]);
        changeInputValue(scenarioIndex, inputIndex, newValue);
    };

    const changeInputValue = (scenarioIndex, inputIndex, newValue) => {
        const oldInputsArray = scenarios[scenarioIndex].inputs;
        const newInputsArray = oldInputsArray.map((input) => {
            if(oldInputsArray.indexOf(input) === inputIndex){
                return newValue
            }
            else{
                return input;
            }
        });
        const newScenarios = scenarios.map((scenario) => {
            if (scenario.index === scenarioIndex){
                return {
                    index: scenario.index,
                    inputs: newInputsArray,
                    outputs: scenario.outputs
                }
            }
            else{
                return scenario;
            }
        });
        setScenarios(newScenarios);
    };

    const handleOutputsChange = (e) => {
        e.preventDefault();
        const newValue = e.target.value;
        const splitString = e.target.name.split("-");
        const scenarioIndex = parseInt(splitString[0]);
        const outputIndex = parseInt(splitString[1]);
        changeOutputValue(scenarioIndex, outputIndex, newValue);
    };

    const changeOutputValue = (scenarioIndex, outputIndex, newValue) => {
        const oldInputsArray = scenarios[scenarioIndex].outputs;
        const newOutputsArray = oldInputsArray.map((output) => {
            if(oldInputsArray.indexOf(output) === outputIndex){
                return newValue
            }
            else{
                return output;
            }
        });
        const newScenarios = scenarios.map((scenario) => {
            if (scenario.index === scenarioIndex){
                return {
                    index: scenario.index,
                    inputs: scenario.inputs,
                    outputs: newOutputsArray
                }
            }
            else{
                return scenario;
            }
        });
        setScenarios(newScenarios);
    };

    const addScenario = () => {
        const newScenario = {
            index: scenarios.length,
            inputs: [""],
            outputs: [""]
        };
        setScenarios([...scenarios, newScenario]);
    };

    const addInputField = (index) => {
        const oldInputsArray = scenarios[index].inputs;
        const newInputsArray = [...oldInputsArray, ""];
        const newScenarios = scenarios.map((scenario) => {
            if (scenario.index === index){
                return {
                    index: scenario.index,
                    inputs: newInputsArray,
                    outputs: scenario.outputs
                }
            }
            else{
                return scenario;
            }
        });
        setScenarios(newScenarios);
    };

    const addOutputField = (index) => {
        const oldOutputsArray = scenarios[index].outputs;
        const newOutputsArray = [...oldOutputsArray, ""];
        const newScenarios = scenarios.map((scenario) => {
            if (scenario.index === index){
                return {
                    index: scenario.index,
                    inputs: scenario.inputs,
                    outputs: newOutputsArray
                }
            }
            else{
                return scenario;
            }
        });
        setScenarios(newScenarios);
    };

    const handleSubmit = (e) => {

    };

    return (
        <div>
            <form onSubmit={handleSubmit}>
                <div className={"textArea-container"}>
                    <TextareaAutosize
                        required
                        className={'text-area'}
                        minRows={6}
                        placeholder={"Prompt to AI"}
                        value={ formPrompt }
                        onChange={ handlePromptChange }
                    />
                </div>
                {
                    scenarios.map((scenario) =>
                        <div key={ "scenario-container" + scenario.index } className={"scenario-container"}>
                            <h2 key={ "scenario-header" + scenario.index }>Test Scenario { scenario.index + 1 }</h2>
                            <div key={ "input-container" + scenario.index }>
                                {
                                    scenario.inputs.map((input) =>
                                        <div key={ "input" + scenario.index + scenario.inputs.indexOf(input) } className={ "input" }>
                                            <TextField
                                                key={ "textField" + scenario.index + scenario.inputs.indexOf(input) }
                                                id="outlined-basic"
                                                label="Expected Input"
                                                variant="outlined"
                                                value={ input }
                                                name={ scenario.index + '-' + scenario.inputs.indexOf(input) }
                                                onChange={ handleInputsChange }
                                            />
                                        </div>
                                    )
                                }
                                <Button key={ "add-button-input" + scenario.index } className={ "add-button" } onClick={ () => addInputField(scenario.index) } variant="contained">+</Button>
                            </div>
                            <div key={ "output-container" + scenario.index }>
                                {
                                    scenario.outputs.map((output) =>
                                        <div key={ "output" + scenario.index + scenario.outputs.indexOf(output) } className={ "input" }>
                                            <TextField
                                                key={ "textField" + scenario.index + scenario.outputs.indexOf(output) }
                                                id="outlined-basic"
                                                label="Expected Output"
                                                variant="outlined"
                                                value={ output }
                                                name={ scenario.index + '-' + scenario.outputs.indexOf(output) }
                                                onChange={ handleOutputsChange }
                                            />
                                        </div>
                                    )
                                }
                                <Button key={ "add-button-output" + scenario.index } className={ "add-button" } onClick={ () => addOutputField(scenario.index) } variant="contained">+</Button>
                            </div>
                        </div>
                    )
                }
                <Button className={ "add-button" } onClick={ addScenario } variant="contained">Add Scenario</Button>
                <Button className={ "add-button" } disabled={ formPrompt === '' } variant="contained">Submit</Button>
            </form>
        </div>
    );
}

export default HomeComponent;
