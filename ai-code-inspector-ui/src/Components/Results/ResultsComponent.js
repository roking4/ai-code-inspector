import Alert from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
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

function ResultsComponent(props){

    useEffect(() => {
        countScenarios();
        getResults();
    }, []);

    const [scenarioLength, setScenarioLength] = useState(0);
    const navigate = useNavigate();
    const totalNumberOfScenarios = scenarioLength;
    const [errors, setErrors] = useState(0);
    const [scenarios, setScenarios] = useState(props.scenarios);

    const getResults = () => {
        scenarios.map((originalScenario) => {
            AiCodeService.getTestResults(props.aiCode, originalScenario.inputs, originalScenario.output)
                .then((response) => {
                    const newScenarios = scenarios.map((scenario) => {
                        if(scenario.index === originalScenario.index){
                            return {
                                index: scenario.index,
                                inputs: scenario.inputs,
                                output: scenario.output,
                                pass: response.data.scenarioResults
                            }
                            return scenario;
                        }
                    });
                    setScenarios(newScenarios);
                }).catch((error) => {
                    console.log(error);
            });
        });
    };

    const convertArrayToString = (list) => {
        let inputString = "";
        list.map((item) => {
            inputString += item + ", "
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
                {
                    props.aiCode === undefined ?
                        <Box sx={{ display: 'flex', justifyContent: 'center', alignContent: 'center' }}>
                            <CircularProgress />
                        </Box>
                        :
                        <div>
                            <p>{ props.aiCode }</p>
                            <ContentCopyIcon />
                        </div>
                }
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
                            <div>
                                <h2>
                                    Test Scenario { scenario.index + 1 }
                                    {
                                        scenario.pass === undefined ?
                                            <Box sx={{ display: 'flex', justifyContent: 'center', alignContent: 'center' }}>
                                                <CircularProgress />
                                            </Box> :
                                            <div>
                                                {
                                                    scenario.pass === true ?
                                                        <CheckCircleRoundedIcon color="success" /> :
                                                        <ErrorRoundedIcon sx={{ color: red[500] }} />
                                                }
                                            </div>
                                    }
                                </h2>
                                <p>Inputs: [{ convertArrayToString(scenario.inputs) }]</p>
                                <p>Output: { scenario.output }</p>
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
