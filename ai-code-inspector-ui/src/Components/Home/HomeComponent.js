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
                        <div className={"scenario-container"}>
                            <h2>Test Scenario {index}</h2>
                            <div>
                                {
                                    numberOfInputs.map((index) =>
                                        <div className={"input"}>
                                            <TextField
                                                id="outlined-basic"
                                                label="Expected Input"
                                                variant="outlined" />
                                            <Button className={"add-button"} variant="contained">+</Button>
                                        </div>
                                    )
                                }
                            </div>
                            <div>
                                {
                                    numberOfOutputs.map((index) =>
                                        <div className={"input"}>
                                            <TextField
                                                id="outlined-basic"
                                                label="Expected Output"
                                                variant="outlined" />
                                            <Button className={"add-button"} variant="contained">+</Button>
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
