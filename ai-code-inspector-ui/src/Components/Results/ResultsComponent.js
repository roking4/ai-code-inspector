import Alert from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import { useEffect, useState } from "react";
import CheckCircleRoundedIcon from '@mui/icons-material/CheckCircleRounded';
import ErrorRoundedIcon from '@mui/icons-material/ErrorRounded';
import { red } from '@mui/material/colors';
import Button from "@mui/material/Button";
import './ResultsComponent.css';
import AiCodeService from "../../Services/AiCodeService";
import { useNavigate } from "react-router-dom";
import CircularProgress from '@mui/material/CircularProgress';
import Box from '@mui/material/Box';
import DisplayCodeComponent from "../DisplayCodeComponent/DisplayCodeComponent";

function ResultsComponent(props){

    useEffect(() => {
        countScenarios();
        getResults();
    }, []);

    const [scenarioLength, setScenarioLength] = useState(0);
    const navigate = useNavigate();
    const totalNumberOfScenarios = scenarioLength;
    const [errors, setErrors] = useState();
    const [scenarios, setScenarios] = useState(props.scenarios);

    const getResults = () => {
        let newScenarios = [];
        let scenariosCopy = scenarios.map((scenario) => ({...scenario}));
        scenariosCopy.map((originalScenario) => {
            AiCodeService.getTestResults(props.aiCode, originalScenario.inputs, originalScenario.output)
                .then((response) => {
                    const result = response.data.scenarioResults;
                    if(result === false){
                        if(!errors){
                            setErrors(1);
                        }else {
                            setErrors(errors + 1);
                        }
                    }else{
                        if (!errors) {
                            setErrors(0);
                        }
                    }
                    const newScenario = scenarios[originalScenario.index];
                    newScenario.pass = result;
                    newScenarios.push(newScenario);
                }).catch((error) => {
                    console.log(error);
            });
        });
        setScenarios(newScenarios);
    };

    const convertArrayToString = (list) => {
        let inputString = "";
        list.map((item) => {
            inputString += item.value + ", "
        });
        return inputString.slice(0, -2);
    }

    const countScenarios = () => {
        let i = 0;
        props.scenarios.map((object) => {
            i++;
        });
        setScenarioLength(i);
    };

    const handleRegenerateCode = (e) => {
        e.preventDefault();
        navigate('/enter-tests');
    }

    return(
        <div>
            <form>
                <DisplayCodeComponent code={props.aiCode} />
                {
                    errors >= 1 ?
                        <Alert severity="error">
                            <AlertTitle>Error</AlertTitle>
                            <strong>{ errors } test(s) failed!</strong>
                        </Alert>
                        :
                        <Alert severity="success">
                            <AlertTitle>Success</AlertTitle>
                            <strong>All { totalNumberOfScenarios } scenarios pass!</strong>
                        </Alert>
                }
                {
                    scenarios !== [] ?
                        scenarios.map((scenario) =>
                            <div key={ "scenario" + scenario.index }>
                                <h2 key={ "header" + scenario.index }>
                                    Test Scenario { scenario.index + 1 }
                                    {
                                        scenario.pass === undefined ?
                                            <Box key={ "box" + scenario.index } sx={{ display: 'flex', justifyContent: 'center', alignContent: 'center' }}>
                                                <CircularProgress key={ "progress" + scenario.index } />
                                            </Box> :
                                            <div key={ "check" + scenario.index }>
                                                {
                                                    scenario.pass === true ?
                                                        <CheckCircleRoundedIcon key={ "check" + scenario.index } color="success" /> :
                                                        <ErrorRoundedIcon key={ "wrong" + scenario.index } sx={{ color: red[500] }} />
                                                }
                                            </div>
                                    }
                                </h2>
                                <p key={ "inputs" + scenario.index } >Inputs: [{ convertArrayToString(scenario.inputs) }]</p>
                                <p key={ "output" + scenario.index } >Output: { scenario.output }</p>
                            </div>
                        )
                        : null
                }
                <Button onClick={ handleRegenerateCode } variant="contained">Regenerate Code</Button>
                <Button href={ '/' } className={ "new-prompt-button" } variant="contained">New Prompt</Button>
            </form>
        </div>
    );
}

export default ResultsComponent;
