import './HomeComponent.css'
import TextareaAutosize from '@mui/base/TextareaAutosize';
import { useState } from "react";
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';

function HomeComponent() {
    const [numberOfScenarios, setNumberOfScenarios] = useState([1]);
    const [numberOfInputs, setNumberOfInputs] = useState([1]);
    const [numberOfOutputs, setNumberOfOutputs] = useState([1]);

    return (
        <div>
            <form>
                <div className={"textArea-container"}>
                    <TextareaAutosize
                        className={'text-area'}
                        minRows={6}
                        placeholder={"Prompt to AI"}
                    />
                </div>
                {
                    numberOfScenarios.map((index) =>
                        <div key={"scenario-container" + index} className={"scenario-container"}>
                            <h2 key={"scenario-header" + index}>Test Scenario {index}</h2>
                            <div key={"input-container" + index}>
                                {
                                    numberOfInputs.map((index) =>
                                        <div key={"input" + index} className={"input"}>
                                            <TextField
                                                key={"textField" + index}
                                                id="outlined-basic"
                                                label="Expected Input"
                                                variant="outlined"
                                            />
                                            <Button key={"add-button-input" + index} className={"add-button"} variant="contained">+</Button>
                                        </div>
                                    )
                                }
                            </div>
                            <div key={"output-container" + index}>
                                {
                                    numberOfOutputs.map((index) =>
                                        <div key={"output" + index} className={"input"}>
                                            <TextField
                                                key={"textField" + index}
                                                id="outlined-basic"
                                                label="Expected Output"
                                                variant="outlined"
                                            />
                                            <Button key={"add-button-output" + index} className={"add-button"} variant="contained">+</Button>
                                        </div>
                                    )
                                }
                            </div>
                        </div>
                    )
                }
                <Button className={"add-button"} variant="contained">Add Scenario</Button>
                <Button className={"add-button"} variant="contained">Submit</Button>
            </form>
        </div>
    );
}

export default HomeComponent;
