import './HomeComponent.css'
import TextareaAutosize from '@mui/base/TextareaAutosize';
import { useState } from "react";
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';

function HomeComponent() {

    const [scenarios, setScenarios] = useState([
        {
            index: 0,
            numberOfInputs: [1],
            numberOfOutputs: [1]
        }
    ]);

    function addScenario(){
        const newScenario = {
            index: scenarios.length,
            numberOfInputs: [1],
            numberOfOutputs: [1]
        };
        setScenarios([...scenarios, newScenario]);
    }

    function addInput(index){
        const oldNumberOfInputs = scenarios[index].numberOfInputs;
        const newNumberOfInputs = [...oldNumberOfInputs, oldNumberOfInputs.length + 1];
        const newScenarios = scenarios.map((scenario) => {
            if (scenario.index === index){
                return {
                    index: scenario.index,
                    numberOfInputs: newNumberOfInputs,
                    numberOfOutputs: scenario.numberOfOutputs
                }
            }
            else{
                return scenario;
            }
        });
        setScenarios(newScenarios);
    }

    function addOutput(index){
        const oldNumberOfOutputs = scenarios[index].numberOfOutputs;
        const newNumberOfOutputs = [...oldNumberOfOutputs, oldNumberOfOutputs.length + 1];
        const newScenarios = scenarios.map((scenario) => {
            if (scenario.index === index){
                return {
                    index: scenario.index,
                    numberOfInputs: scenario.numberOfInputs,
                    numberOfOutputs: newNumberOfOutputs
                }
            }
            else{
                return scenario;
            }
        });
        setScenarios(newScenarios);
    }

    return (
        <div>
            <form>
                <div className={"textArea-container"}>
                    <TextareaAutosize
                        required
                        className={'text-area'}
                        minRows={6}
                        placeholder={"Prompt to AI"}
                    />
                </div>
                {
                    scenarios.map((scenario) =>
                        <div key={ "scenario-container" + scenario.index } className={"scenario-container"}>
                            <h2 key={ "scenario-header" + scenario.index }>Test Scenario { scenario.index + 1 }</h2>
                            <div key={ "input-container" + scenario.index }>
                                {
                                    scenario.numberOfInputs.map((index) =>
                                        <div key={ "input" + scenario.index + index } className={ "input" }>
                                            <TextField
                                                key={ "textField" + scenario.index + index }
                                                id="outlined-basic"
                                                label="Expected Input"
                                                variant="outlined"
                                            />
                                        </div>
                                    )
                                }
                                <Button key={ "add-button-input" + scenario.index } className={ "add-button" } onClick={ () => addInput(scenario.index) } variant="contained">+</Button>
                            </div>
                            <div key={ "output-container" + scenario.index }>
                                {
                                    scenario.numberOfOutputs.map((index) =>
                                        <div key={ "output" + scenario.index + index } className={ "input" }>
                                            <TextField
                                                key={ "textField" + scenario.index + index }
                                                id="outlined-basic"
                                                label="Expected Output"
                                                variant="outlined"
                                            />
                                        </div>
                                    )
                                }
                                <Button key={ "add-button-output" + scenario.index } className={ "add-button" } onClick={ () => addOutput(scenario.index) } variant="contained">+</Button>
                            </div>
                        </div>
                    )
                }
                <Button className={ "add-button" } onClick={ addScenario } variant="contained">Add Scenario</Button>
                <Button className={ "add-button" } variant="contained">Submit</Button>
            </form>
        </div>
    );
}

export default HomeComponent;
